package sugarcube.formats.ocd.analysis.text;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.*;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.analysis.DexterProps;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.lists.OCDList;
import sugarcube.formats.ocd.objects.lists.OCDTexts;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Map;

public class Canonizer extends DexterProps
{
    public static long TIME_CLEANING = 0;
    public static long TIME_TOKENIZE = 0;
    public static long TIME_LINEARIZE = 0;
    public static long TIME_CLUSTERIZE = 0;
    public static long TIME_RETROACTIVE = 0;
    public static long TIME_INTERLINE = 0;
    public static long TIME_OVERLAP = 0;
    public static long TIME_RUNIZE = 0;

    public static String PrintElapsed()
    {
        return "Clean,Tokens,Lines,Blocks,Retro,Inter,Over,Runs=" + TIME_CLEANING + "," + TIME_TOKENIZE + "," + TIME_LINEARIZE + "," + TIME_CLUSTERIZE
                + "," + TIME_RETROACTIVE + "," + TIME_INTERLINE + "," + TIME_OVERLAP + "," + TIME_RUNIZE + "ms";
    }

    public static int MAX_STEP = 0;
    public static final int STEP_CLEAN = ++MAX_STEP;
    public static final int STEP_TOKEN = ++MAX_STEP;
    public static final int STEP_LINE = ++MAX_STEP;
    public static final int STEP_ITEMS = ++MAX_STEP;
    public static final int STEP_CLUSTER = ++MAX_STEP;
    public static final int STEP_RETROACTIVE = ++MAX_STEP;
    public static final int STEP_INTERLINE = ++MAX_STEP;
    public static final int STEP_OVERLAP = ++MAX_STEP;

    protected OCDPage page;
    protected long time = System.currentTimeMillis();
    protected StringMap<OCDAnnot> layoutAnnots = new StringMap<>();
    private Set3<OCDPaintable> occludedNodes = new Set3<>();
    private WallDetection wall = new WallDetection();

    public Canonizer(OCDPage page, DexterProps props)
    {
        this.page = page;
        this.populate(props);
        this.ocr = page.isProd("ocr");
    }

    public void process()
    {
        long ts = Sys.Millis();//Log.trace(this, ".process - layoutAnnots.size()=" + page.layoutAnnots().size());
        try
        {
            this.layoutAnnots = page.layoutAnnots();

            if (!DO_KEEP_INVISIBLE_TEXTS)
                detectOccludedNodes();

            // reverse to check text under shapes
            OCDList pageNodes = new OCDList(page.content().children()).reverse();

            StringMap<OCDTexts> inputs = new StringMap<>();
            StringMap<OCDList> outputs = new StringMap<>();

            this.addContent(pageNodes, "", inputs, outputs);

            for (OCDTexts input : inputs.values())
                input.reverse();
            for (OCDList output : outputs.values())
                output.reverse();

            for (Map.Entry<String, OCDTexts> entry : inputs.entrySet())
                canonize(entry.getValue(), entry.getKey(), outputs);

            this.updatePageContent(outputs);

        } catch (Exception e)
        {
            Log.error(Canonizer.class, ".process - exception thrown: " + e);
            e.printStackTrace();
        }
//        Log.trace(this, ".process - done: elapsed=" + Sys.Elapsed(ts) + " " + PrintElapsed());
    }

    private void detectOccludedNodes()
    {
        List3<Rectangle2D> occlusions = new List3<>();

        //from front to back
        OCDList list = page.content().zOrderedPrimitives(false).reverse();
        for (OCDPaintable node : list)
        {
            if (node.isText())
            {
                OCDText text = node.asText();
                if (text.isApparent())
                {
                    Rectangle3 box = text.textBounds();
                    box = box.inflate(-box.height / 4, 0);
                    double cx = box.getCenterX();
                    double cy = box.getCenterY();

                    for (Rectangle2D occludingBox : occlusions)
                        if (occludingBox.contains(cx, cy))
                        {
                            occludedNodes.add(node);
                            break;
                        }
                    occlusions.add(box);
                }
            } else if (node.isImage())
            {
                OCDImage image = node.asImage();
                if (image.clip().path().isBBox(0.01) && image.isOpaque())
                    occlusions.add(image.bounds().intersection(image.clip().bounds()));
            } else if (node.isPath())
            {
                OCDPath path = node.asPath();
                if (path.isFilled() && path.clip().path().isBBox(0.01) && path.path3().isBBox(0.01))
                    occlusions.add(path.bounds());
            }
        }
    }

    private List3<OCDTextBlock> canonize(List3<OCDText> contentTexts, String contentID, StringMap<OCDList> contentNodes)
    {
        Iterator<OCDText> textit = contentTexts.iterator();
        while (textit.hasNext())
            textit.next().canonize();
        // Log.debug(this, ".canonize - texts=" + texts.size()+"");

        this.doClusterize = true;
        this.doMergeItems = true;
        this.doMergeTokens = true;
        this.doMergeRuns = true;
        this.doCleanRaws = true;

        this.steps = 100;

        List3<OCDTextBlock> blocks = clusterize(mergeItems(linearize(tokenize(clean(contentTexts)))), false);
        blocks = retroactive(blocks);
        blocks = interline(blocks);// top-down phase
        blocks = overlap(blocks);
        blocks = runize(blocks);// text runs and spaces
        blocks = paragraph(blocks);
        blocks = runize(blocks);
        blocks = removeEmpty(blocks);
        blocks = normalizeSpaces(blocks);

        for (OCDTextBlock block : blocks)
            OCDDoctor.CleanWhiteCharSpaces(block.uncanonize());

        BoxNodeSorter sorter = BoxNodeSorter.Get(blocks).sort();

        for (BoxNode node : sorter.sortedGroup())
            addOutput(node.node, contentID, contentNodes);

        return blocks;
    }

    public void canonizeTextBlock(OCDTextBlock block, boolean explode)
    {
        OCDTextBlock group = block;
        List3<OCDText> texts = new List3<>();
        for (OCDTextLine line : block)
            for (OCDText text : line)
                texts.add(text.canonize());

        if (explode)
            texts = explode(texts);

        block = clusterize(mergeItems(linearize(tokenize(clean(texts)))), true).first();
        if (block != null)
        {
            block = retroactive(block);
            block = runize(block);// text runs and spaces
            block = normalizeSpaces(block);
            block = block.uncanonize();
            block = OCDDoctor.CleanWhiteCharSpaces(block);
        }

        group.children().setAll(block.children());
    }

    public void addContent(List3<OCDPaintable> nodes, String contentID, StringMap<OCDTexts> inputs, StringMap<OCDList> outputs)
    {
//        OCDText prevText = null;
//        long ts = Sys.Millis(); //Log.trace(this, ".addContent - id=" + contentID);

        for (OCDPaintable node : nodes)
        {
            if (node.isText())
            {
                OCDText text = node.asText();
                if (text.isApparent() || DO_KEEP_INVISIBLE_TEXTS)
                {
                    if (!occludedNodes.has(node) && !text.isMode(OCDText.MODE_TTB))
                    {
                        OCDText copy = text.copy();
                        this.addInput(copy, contentID, inputs, outputs);
//                        if (isShadow(copy, prevText))
//                            copy.groupID = -1;
//                        prevText = copy;
                    } else
                    {
                        this.addOutput(node, contentID, outputs);
                    }
                }
            } else if (OCD.isParagraph(node))
            {
                OCDTextBlock block = (OCDTextBlock) node;
                for (OCDTextLine line : block)
                    for (OCDText text : line)
                        if (text.hasPaths())
                        {
                            text = text.copy();
                            this.addInput(text, contentID, inputs, outputs);
                        }
            } else if (node.isGroupContent())
            {
//                Log.debug(this, ".addContent - sub content id=" + node.needID() + (Str.HasChar(contentID) ? ", parent content id=" + contentID : ""));
                this.addContent(node.asContent().children(), node.needID(), inputs, outputs);
            } else
            {
//                Log.trace(this, ".addContent - node: " + node.tag + ", type=" + node.type() + ", id=" + node.needID());
                this.addOutput(node, contentID, outputs);
            }

        }
//        Log.trace(this, ".addContent - done: elapsed=" + Sys.Elapsed(ts));
    }

    public void addInput(OCDText text, String contentID, StringMap<OCDTexts> inputs, StringMap<OCDList> outputs)
    {
        OCDTexts texts = inputs.get(contentID);
        if (texts == null)
            inputs.put(contentID, texts = new OCDTexts());
        List3<OCDText> tokens = text.splitByCharSpaces(1);
        for (OCDText token : tokens.reverse())
        {
//            if (isOccluded(token))
//                addOutput(token, contentID, outputs);
//            else
//            {
            texts.add(token);
//                addOcclusion(token.textBounds());
//            }
        }
        // Log.debug(this, ".addInputText - "+texts);
    }

    public void addOutput(OCDPaintable node, String contentID, StringMap<OCDList> outputs)
    {
        OCDList nodes = outputs.get(contentID);
        if (nodes == null)
            outputs.put(contentID, nodes = new OCDList());
        nodes.add(node);
    }

    public List3<OCDTextLine> mergeItems(List3<OCDTextLine> input)
    {
        if (!doMergeItems || steps < STEP_ITEMS || items.length == 0)
            return input;

        List3<OCDTextLine> output = new List3<>();
        SortCX(input);
        LOOP:
        while (!input.isEmpty())
        {
            OCDTextLine cHead = input.removeFirst();
            output.add(cHead);
            Iterator<OCDTextLine> bodit = input.iterator();
            for (String item : items)
                if (cHead.size() == 1 && cHead.uniString().trim().equals(item))
                    while (bodit.hasNext())
                    {
                        OCDTextLine cBody = bodit.next();
                        if (cHead.groupID == cBody.groupID && cHead != cBody && coverOK(cBody, cHead) && cHead.x() < cBody.x())
                        {
                            cHead.addAll(cBody);
                            cHead.canon.refresh();
                            bodit.remove();// removes l2
                            continue LOOP;
                        }
                    }
        }
        return output;
    }

    private List3<OCDTextBlock> removeEmpty(List3<OCDTextBlock> blocks)
    {
        Iterator<OCDTextBlock> blockIt = blocks.iterator();
        while (blockIt.hasNext())
        {
            OCDTextBlock block = blockIt.next();
            Iterator<OCDTextLine> lineIt = block.iterator();
            while (lineIt.hasNext())
                if (lineIt.next().stringValue().trim().isEmpty())
                    lineIt.remove();
            if (block.isEmpty())
                blockIt.remove();
        }
        return blocks;
    }

    private List3<OCDTextBlock> normalizeSpaces(List3<OCDTextBlock> blocks)
    {
        Iterator<OCDTextBlock> blockit = blocks.iterator();
        while (blockit.hasNext())
        {
            OCDTextBlock block = normalizeSpaces(blockit.next());

            if (block.isEmpty())
                blockit.remove();
        }
        return blocks;
    }

    private OCDTextBlock normalizeSpaces(OCDTextBlock block)
    {
        for (OCDTextLine line : block)
        {
            OCDText first = line.first();
            first.canon.unspaceStart();
            line.last().canon.unspaceEnd();
            OCDText pre = null;
            for (OCDText text : line)
            {
                OCDText cur = ensureMonoSpace(text);
                if (pre != null)
                    if (cur.startsWithSpace())
                    {
                        pre.canon.addSpace(cur.canon.minX(), pre.y());
                        cur.canon.unspaceStart();
                    }
                pre = cur;
            }
        }
        return block;
    }

    private OCDText ensureMonoSpace(OCDText text)
    {
        int i;
        while ((i = spaceRunIndex(text)) > -1)
        {
            text.canon.coords.remove(i);
            text.unicodes().remove(i);
        }
        text.canon.refresh();
        return text;
    }

    private int spaceRunIndex(OCDText text)
    {
        int index = 0;
        int pre = -1;
        for (int cur : text.unicodes())
        {
            if (cur == Unicodes.ASCII_SP && pre == cur)
                return index;
            index++;
            pre = cur;
        }
        return -1;
    }

    private List3<OCDTextBlock> paragraph(List3<OCDTextBlock> blocks)
    {
        if (!useIndent || splitIndent <= 0)
            return blocks;

        List3<OCDTextBlock> list = new List3<>();
        for (OCDTextBlock block : blocks)
        {
            if (block.groupID < 0)
            {
                SortCY(block.children());
                double lineHeight = block.first().canon.height();
                double minGap = splitIndent * lineHeight;
                double maxGap = splitIndentMax * lineHeight;
                double noGap = sameFontsize * lineHeight;
                int splitIndex;
                do
                {
                    Iterator<OCDTextLine> iterator = block.iterator();
                    OCDTextLine pre = null;
                    OCDTextLine cur = iterator.hasNext() ? iterator.next() : null;
                    OCDTextLine nex = iterator.hasNext() ? iterator.next() : null;
                    int i = 0;
                    splitIndex = -1;
                    do
                    {
                        i++;
                        if (pre != null && nex != null)
                        {
                            float x0 = pre.x();
                            float x1 = cur.x();
                            float x2 = nex.x();
                            float w0 = pre.lastX();
                            float w1 = cur.lastX();
                            float w2 = nex.lastX();

                            if (!IsGroup(pre, cur) && lineEnds(pre, cur) && (x1 - x2 > minGap) && (w2 - w1) < maxGap)
                                splitIndex = i - 1;
                            else if (!IsGroup(cur, nex) && lineEnds(cur, nex) && (x2 - x1 > minGap) && block.nbOfLines() == i + 1)
                                splitIndex = i;
//                            else if (!IsGroup(cur, nex) && lineEnds(cur, nex) && Math.abs(w0 - w2) < noGap && w0 - w1 > noGap)
//                                splitIndex = i;
                        }
                        pre = cur;
                        cur = nex;
                        nex = iterator.hasNext() ? iterator.next() : null;
                    } while (nex != null && splitIndex < 0);
                    if (splitIndex > 0)
                        list.add(block.split(splitIndex, false).canonize());
                } while (splitIndex > 0);
            }
            if (!block.isEmpty())
            {
                block.canon.refresh();
                list.add(block);
            }
        }
        return list;
    }

    public static boolean lineEnds(OCDTextLine lineEnd, OCDTextLine lineStart)
    {
        int end = lineEnd.last().last();
        int start = lineStart.first().first();
        if (end == '.')
            return true;
        if (end == ',')
            return false;
        if (!Character.isLetterOrDigit(end) && (Character.isUpperCase(start) || !Character.isLetterOrDigit(start)))
            return true;

        return false;
    }

    private List3<OCDTextBlock> interline(List3<OCDTextBlock> blocks)
    {
        long millis = Sys.Millis();

        List3<OCDTextBlock> list = new List3<>();
        if (steps >= STEP_INTERLINE && useInterline && splitInterline >= 0)
            for (OCDTextBlock block : blocks)
            {
                if (block.groupID < 0)
                {
                    SortCY(block.children());
                    double interline = splitInterline * block.first().canon.height();
                    int i;
                    do
                    {
                        OCDTextLine pre = null;
                        OCDTextLine cur = null;
                        OCDTextLine nex = null;
                        i = 0;
                        for (OCDTextLine line : block)
                        {
                            pre = cur;
                            cur = nex;
                            nex = line;
                            if (cur != null && pre != null)
                                if (pre.y() < cur.y() && cur.y() < nex.y() && Math.abs(nex.y() - 2 * cur.y() + pre.y()) > interline)
                                    if (pre.lastY() < cur.lastY() && cur.lastY() < nex.lastY() && Math.abs(nex.lastY() - 2 * cur.lastY() + pre.lastY()) > interline)
                                        if (nex.meanY() - cur.meanY() > cur.meanY() - pre.meanY())
                                        {
                                            if (!IsGroup(cur, nex))
                                            {
                                                i = -i;
                                                break;// if interline suddenly changes then break
                                            }
                                        } else if (!IsGroup(pre, cur))
                                        {
                                            i = -i - 1;
                                            break;// if interline suddenly changes then break
                                        }
                            i--;
                        }
                        if (i > 0)
                            list.add(block.split(i, false).canonize());
                    } while (i > 0);
                }
                if (!block.isEmpty())
                    list.add(block);
            }
        else
            return blocks;

        for (OCDTextBlock block : list)
            block.canon.refresh();

        TIME_INTERLINE += Sys.Elapsed(millis);
        return list;
    }

    private List3<OCDTextBlock> runize(List3<OCDTextBlock> blocks)
    {
        long millis = Sys.Millis();
        if (steps >= STEP_LINE)
            for (OCDTextBlock block : blocks)
                runize(block);
        TIME_RUNIZE += Sys.Elapsed(millis);
        return blocks;
    }

    private OCDTextBlock runize(OCDTextBlock block)
    {
        float zOrder = 0;
        for (OCDTextLine line : block)
        {
            SortCX(line.children());
            Iterator<OCDText> iterator = line.iterator();
            OCDText t1 = null;
            while (iterator.hasNext())
            {
                OCDText t2 = iterator.next();
                if (t2.zOrder > zOrder)
                    zOrder = t2.zOrder;
                // if (t1 != null && Math.abs(t2.canon.minX() - t1.canon.maxX()) >
                // props.mergeToken * Math.max(t1.canon.height(),
                // t2.canon.height()))
                // {
                // if (t1.unicodes().last() == Unicodes.ASCII_SP)
                // t1.canon.removeLast();
                // t1.canon.addSpace(t2.canon.minX(), t1.y());
                // }

                if (t1 == null || t1.endsWithSpace())
                    continue;

                // merge similar tokens into runs (still separated by spaces or
                // different properties)
                if (baselineOK(t1, t2) && t1.fontname().equals(t2.fontname()) && basicsOK(t1, t2) && runOK(t1, t2) && shearOK(t1, t2) && scriptOK(t1, t2)
                        && colorsOK(t1, t2))
                {
                    // Log.debug(this, ".spacify - " + pre.string() + "-" +
                    // cur.string() + " " + pre.zOrder() + "-" + cur.zOrder());
                    t1.unicodes().append(t2.unicodes());
                    t1.coords().removeLast();
                    t1.coords().add(t2.coords());
                    t1.zOrder = Math.max(t1.zOrder, t2.zOrder);
                    t1.setID(t1.id() == null ? t2.id() : t1.id());
                    iterator.remove();
                    t1.canon.refresh();
                } else
                    t1 = t2;
            }

            List3<OCDText> texts = new List3<>();
            for (OCDText text : line)
            {
                texts.add(text);
                int index = -1;
                while ((index = (text.unicodes().firstSpace(true))) > -1)
                {
                    OCDText split = text.canon.split(index);
                    texts.add(split);
                }
                text.canon.refresh();
            }

            SortCX(texts);
            line.children().setAll(texts);
            line.canon.refresh();
        }
        if (block.canon == null)
            Log.debug(this, ".runize - cluster.canon is null: " + block.stringValue(false));
        block.canon.refresh();
        block.zOrder = zOrder;

        return block;
    }

    private List3<OCDTextBlock> overlap(List3<OCDTextBlock> clusters)
    {
        long millis = Sys.Millis();

        if (steps >= STEP_OVERLAP && !clusters.isEmpty())
        {
            SortOX(clusters);
            clusters = overlapPass(clusters);
            SortInvertedOX(clusters);
            clusters = overlapPass(clusters);
        }
        SortCY(clusters);

        TIME_OVERLAP += Sys.Elapsed(millis);
        return clusters;
    }

    private List3<OCDTextBlock> overlapPass(List3<OCDTextBlock> clusters)
    {
        if (steps >= STEP_OVERLAP && !clusters.isEmpty())
        {
            List3<OCDTextBlock> blocks = new List3<>();
            do
            {
                // miam... since it eats other blocks
                OCDTextBlock miam = clusters.removeFirst();
                blocks.add(miam);
                if (miam.groupID < 0)
                {
                    // expand block box in order to merge small and alone characters
                    miam.canon.refresh(1);
                    Iterator<OCDTextBlock> blockIt = clusters.iterator();
                    while (blockIt.hasNext())
                    {
                        OCDTextBlock block = blockIt.next();
                        Iterator<OCDTextLine> lineIt = block.iterator();
                        while (lineIt.hasNext())
                        {
                            OCDTextLine line = lineIt.next();
                            boolean ok = line.groupID == miam.groupID && angleOK(line.first(), miam.firstText()) && miam.canon.contains(line.canon);
                            if (ok)
                            {
                                ok = false;
                                // check non rectangular bounds
                                for (OCDTextLine l : miam)
                                    if (l.canon.inflate(l.canon.height * 2, true).contains(line.canon))
                                    {
                                        ok = true;
                                        break;
                                    }
                            }
                            if (ok && neighborOK(miam, line) && miam.nbOfTexts() > block.nbOfTexts() && miam.fontsize() < block.fontsize() * 3)
                            {
                                for (OCDText text : line)
                                    miam.add(new OCDTextLine(page, text).canonize());
                                lineIt.remove();
                            }
                        }
                        if (block.isEmpty())
                            blockIt.remove();
                    }
                    retroactive(miam);
                    miam.canon.refresh();
                }
            } while (!clusters.isEmpty());
            clusters = blocks;
        }
        return clusters;
    }

    private boolean neighborOK(OCDTextBlock phagocyte, OCDTextLine line)
    {
        Iterator<OCDTextLine> lineIt = phagocyte.iterator();
        while (lineIt.hasNext())
        {
            OCDTextLine l = lineIt.next();
            if (l.canon.distance(line.canon) < l.first().scaledFontsize() + minWS(l, mergeToken) / 2)
                return true;
        }
        return false;
    }

    public double minWS(OCDTextLine line, double tokenTh)
    {
        double minWS = Double.POSITIVE_INFINITY;
        OCDText a = null;
        for (OCDText b : line)
        {
            if (a != null && b != null)
                if (Math.abs(b.canon.minX() - a.canon.maxX()) > tokenTh * Math.max(a.canon.height(), b.canon.height()))
                {
                    double ws = b.canon.minX() - a.canon.maxX();
                    if (ws < minWS)
                        minWS = ws;
                }
            a = b;
        }
        return minWS == Double.POSITIVE_INFINITY ? 0.0 : minWS;
    }

    private List3<OCDTextBlock> retroactive(List3<OCDTextBlock> blocks)
    {
        if (steps >= STEP_RETROACTIVE)
            for (OCDTextBlock block : blocks)
                retroactive(block);
        return blocks;
    }

    private OCDTextBlock retroactive(OCDTextBlock block)
    {
        long millis = Sys.Millis();

        SortOY(block.children(), null);
        List3<OCDTextLine> lines = new List3<>();
        while (!block.children().isEmpty())
        {
            OCDTextLine l1 = block.children().remove(0);
            lines.add(l1);
            Iterator<OCDTextLine> iterator = block.iterator();

            while (iterator.hasNext())
            {
                OCDTextLine l2 = iterator.next();
                if (coverOK(l1, l2))
                {
                    // be sure to insert token at the right place into the line
                    for (OCDText t2 : l2)
                    {
                        t2.setParent(l1);
                        double x = t2.x();
                        if (x > l1.last().x())
                            l1.children().add(t2);
                        else
                        {
                            int index = 0;
                            for (OCDText t1 : l1)
                            {
                                if (t1.x() > x)
                                    break;
                                index++;
                            }
                            l1.children().add(index, t2);
                        }
                    }
                    l1.canon.refresh();
                    iterator.remove();// removes l2
                } else
                    break; // blocks are too distant to be merged
            }
        }
        block.children().clear();
        block.addAll(lines);
        SortCY(block.children());
        block.canon.refresh();

        TIME_RETROACTIVE += Sys.Elapsed(millis);
        return block;
    }

    private List3<OCDTextBlock> clusterize(List3<OCDTextLine> lines, boolean oneTextBlock)
    {
        long millis = Sys.Millis();

        List3<OCDTextBlock> clusters = new List3<>();
        if (doClusterize && steps >= STEP_CLUSTER)
        {
            for (OCDTextLine line : lines)
                line.canon.refresh(mergeBlock, mergeLine <= 0 ? page.width : mergeLine / 3);

            while (!lines.isEmpty())
            {
                boolean doesIntersect = true;
                OCDTextBlock cluster = new OCDTextBlock(page, lines.removeFirst()).canonize();
                clusters.add(cluster);
                loop:
                while (doesIntersect)
                {
                    doesIntersect = false;
                    Iterator<OCDTextLine> iterator = lines.iterator();
                    while (iterator.hasNext())
                    {
                        OCDTextLine alone = iterator.next();
                        for (OCDTextLine line : cluster)
                            if (oneTextBlock || IsGroup(line, alone)
                                    || (line.canon.intersects(alone.canon) && (basicsOK(line.first(), alone.first()) || basicsOK(line.last(), alone.last())))
                                    && layoutOK(line, alone))
                            {
                                // bounds directly without calling canon to have raw bounds
                                cluster.add(alone);
                                iterator.remove(); // removes alone line
                                doesIntersect = true;
                                continue loop;
                            }
                    }
                }

                int gid = -1;
                String label = null;
                for (OCDTextLine line : cluster)
                {
                    line.canon.refresh();

                    if (gid < 0 && line.groupID >= 0)
                        gid = line.groupID;

                    if (label == null && line.hasLabel())
                        label = line.label();

                    for (OCDText text : line)
                    {
                        if (gid < 0 && text.groupID >= 0)
                            gid = text.groupID;

                        if (label == null && text.hasLabel())
                            label = text.label();

                        text.groupID = -1;
                        text.clearLabel();
                    }

                    line.groupID = -1;
                    line.clearLabel();
                }
                // ensures that PDF keep structures when available (word, omnipage)
                if (gid >= 0)
                    cluster.groupID = gid;

                if (label != null)
                    cluster.setLabel(label);

            }

        } else
            for (OCDTextLine line : lines)
                clusters.add(new OCDTextBlock(page, line).canonize());

        TIME_CLUSTERIZE += Sys.Elapsed(millis);
        return clusters;
    }

    private List3<OCDTextLine> linearize(List3<OCDText> runs)
    {
        long millis = Sys.Millis();

        List3<OCDTextLine> lines = new List3<>();
        if (doMergeTokens && steps >= STEP_LINE)
        {
            for (OCDText run : runs)
                run.canon.refresh();
            SortCX(runs);
            while (!runs.isEmpty())
            {
                OCDText t1 = runs.removeFirst();

                OCDTextLine l1 = new OCDTextLine(page, t1).canonize();
                lines.add(l1);
                // if groupID then paragraph recovered directly in clustering
                if (l1.groupID < 0)
                {
                    Iterator<OCDText> iterator = runs.iterator();
                    while (iterator.hasNext())
                    {
                        OCDText t2 = iterator.next();

                        boolean cover = coverOK(t1, t2);
                        boolean baseline = baselineOK(t1, t2);

                        if (cover || baseline)
                        {
                            boolean stop = true;
                            if (layoutOK(t1, t2) && angleOK(t1, t2))
                                if (baseline && basicsOK(t1, t2) && linesOK(t1, t2) && wall.canMerge(t1, t2) || cover && tokensOK(t1, t2) && FontsizeOK(t1, t2, 1))
                                {
                                    if (t1.unicodes().last() == Unicodes.ASCII_SP)
                                        t1.canon.removeLast();
                                    if (!tokensOK(t1, t2))
                                        t1.canon.addSpace(t2.canon.minX(), t1.y());
                                    l1.add(t2);
                                    l1.canon.refresh();
                                    t1 = t2;
                                    iterator.remove();// removes t2
                                    stop = false;
                                }
                            if (stop)
                                break;
                        }
                    }
                }
            }
        } else
            for (OCDText text : runs)
                lines.add(new OCDTextLine(page, text).canonize());

        // for(OCDTextLine text: lines)
        // Log.debug(this, ".tokenize - \""+text.string()+"\"");

        TIME_LINEARIZE += Sys.Elapsed(millis);
        return lines;
    }

    private boolean layoutOK(OCDText t1, OCDText t2)
    {
        if (!useLayout || layoutAnnots.isEmpty())
            return true;
        return layoutOK(new Rectangle3(true, t1.x(), t1.y(), t1.lastX(), t1.lastY()), new Rectangle3(true, t2.x(), t2.y(), t2.lastX(), t2.lastY()));
    }

    private boolean layoutOK(OCDTextLine l1, OCDTextLine l2)
    {
        if (!useLayout || layoutAnnots.isEmpty())
            return true;
        OCDText t1 = l1.first();
        OCDText t1_ = l1.last();
        OCDText t2 = l2.first();
        OCDText t2_ = l2.last();
        return layoutOK(new Rectangle3(true, t1.x(), t1.y(), t1_.lastX(), t1_.lastY()), new Rectangle3(true, t2.x(), t2.y(), t2_.lastX(), t2_.lastY()));
    }

    private boolean layoutOK(Rectangle3 r1, Rectangle3 r2)
    {
        if (!useLayout || layoutAnnots.isEmpty())
            return true;
        r1 = r1.checkMin(1);
        r2 = r2.checkMin(1);
        Rectangle3 box;
        for (OCDAnnot annot : layoutAnnots)
            if ((box = annot.bounds()).contains(r1) && !box.contains(r2))
                return false;
        return true;
    }

    // merge segmented tokens (text, number, identical), also tokenizing in
    // spacify (between white spaces)
    private List3<OCDText> tokenize(List3<OCDText> runs)
    {
        long millis = Sys.Millis();

        List3<OCDText> tokens = new List3<>();
        if (doMergeRuns && steps >= STEP_TOKEN)
        {
            for (OCDText run : runs)
                run.canon.refresh();
            SortCX(runs);
            while (!runs.isEmpty())
            {
                OCDText t1 = runs.removeFirst();
                tokens.add(t1);
                // if groupID then paragraph recovered directly in clustering
                if (t1.groupID < 0)
                {
                    Iterator<OCDText> iterator = runs.iterator();
                    while (iterator.hasNext())
                    {
                        OCDText t2 = iterator.next();
                        if (baselineOK(t1, t2))
                        {
                            if (basicsOK(t1, t2) && runOK(t1, t2) && shearOK(t1, t2) && scriptOK(t1, t2) && colorsOK(t1, t2) && tokensOK(t1, t2)
                                    && t1.fontname().equals(t2.fontname()))
                            {
                                // Log.debug(this,
                                // ".tokenize: "+t1.string()+"-"+t2.string()+"
                                // "+(int)t1.zOrder()+"-"+(int)t2.zOrder());
                                t1.unicodes().append(t2.unicodes());
                                t1.coords().removeLast();
                                t1.coords().add(t2.coords());
                                if (t2.zOrder > t1.zOrder)
                                    t1.zOrder = t2.zOrder;
                                t1.canon.refresh();
                                t1.setID(t1.id() == null ? t2.id() : t1.id());
                                // if t1 has a clip that does not include t2
                                t1.setClip(null);
                                iterator.remove(); // removes t2

                            } else
                            {
                                // apostrophe with another font may happen!
                                break;
                            }
                        }
                    }
                }
            }
        } else
            tokens = runs;

        wall.detect(tokens);

        TIME_TOKENIZE += Sys.Elapsed(millis);
        return tokens;
    }

    private List3<OCDText> explode(List3<OCDText> raws)
    {
        List3<OCDText> chars = new List3<>();
        for (OCDText raw : raws)
        {
            for (int i = 1; i < raw.length() - 1; i++)
            {
                OCDText c = raw.canon.split(i);
                if (!c.isEmptyPath())
                    chars.add(c);
            }
            if (!raw.isEmptyPath())
                chars.add(raw.copy());
        }
        return chars;
    }

    // remove spaces from tokens, an internal space splits a token
    private List3<OCDText> clean(List3<OCDText> raws)
    {

        long millis = Sys.Millis();

        List3<OCDText> list = new List3<>();
        // Log.debug(this, ".clean - step: "+step);
        if (doCleanRaws && steps >= STEP_CLEAN)
        {
            for (OCDText raw : raws)
            {
                boolean oldSpace = false;
                if (raw.groupID < 0)
                {
                    raw.canon.refresh();
                    boolean process = false;
                    if (!doTrustSpaces)
                        raw.canon.trim(); // removes surrounding white spaces
                    do
                    {
                        process = false;
                        int index = 1;
                        for (int i = index; i < raw.length() - 1; i++)
                        {
                            if (raw.isSpace(i))
                            {
                                OCDText prev = raw.canon.split(index + 1);
                                if (!doTrustSpaces)
                                    raw.canon.trim();
                                if (prev != null)
                                {
                                    process = true;
                                    if (!doTrustSpaces)
                                        prev.canon.trim();
                                    if (!prev.isEmpty())
                                    {
                                        boolean space = prev.unicodes().is(' ');
                                        if (!(space && oldSpace))
                                            list.add(prev);
                                        oldSpace = space;
                                    }
                                }
                                break;
                            }
                            index++;
                        }
                    } while (process);
                    if (!doTrustSpaces)
                        raw.canon.trim();
                }
                if (!raw.isEmpty())
                {
                    list.add(raw);
                }
            }
        } else
            list.addAll(raws);
        for (OCDText text : list)
            text.canon.refresh();

        TIME_CLEANING += Sys.Elapsed(millis);
        return list;
    }

    public void updatePageContent(StringMap<OCDList> contents)
    {
        OCDList pageContent = new OCDList();
        for (String contentID : contents.keys())
        {
            OCDList nodes = contents.get(contentID);
            if (Str.IsVoid(contentID))
                pageContent.addAll(nodes);
            else
            {
                OCDContent subContent = new OCDContent(page.content());
                subContent.setID(contentID);
                subContent.addAll(nodes);
                if (subContent.isPopulated())
                    pageContent.add(subContent);
            }
        }

        page.content().setGraphics(pageContent);
        page.ensureParents();
    }

    public static void Process(OCDPage page, DexterProps props)
    {
        new Canonizer(page, props).process();
    }

    public static Canonizer Get(OCDPage page, DexterProps props)
    {
        return new Canonizer(page, props);
    }
}
