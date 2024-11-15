package sugarcube.formats.ocd.objects;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.*;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Shape3;
import sugarcube.common.data.Byte;
import sugarcube.common.numerics.Math3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDPageContent.State;
import sugarcube.formats.ocd.objects.lists.OCDTexts;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class OCDTextBlock extends OCDGroup<OCDTextLine>
{
    public class Canon extends OCDBox
    {
        public Canon()
        {
        }

        @Override
        public Canon refresh()
        {
            OCDTextLine first = first();
            if (first != null)
                this.setRect(first.canon.refresh());
            for (OCDTextLine line : children())
                this.include(line.canon.refresh());
            return this;
        }

        public Canon refresh(double heightFactor)
        {
            refresh().inflate(first().canon.height() * heightFactor);
            return this;
        }

        public Canon refreshV(double heightFactor)
        {
            double dh = first().canon.height() * heightFactor;
            refresh().inflate(0, dh);
            return this;
        }

        @Override
        public Canon copy()
        {
            Canon canon = new Canon();
            canon.setRect(this);
            return canon;
        }
    }

    public Canon canon = null;

    public OCDTextBlock()
    {
        super(OCDGroup.PARAGRAPH);
    }

    public OCDTextBlock(OCDNode parent)
    {
        super(OCDGroup.PARAGRAPH, parent);
    }

    public OCDTextBlock(OCDNode parent, OCDTextLine line)
    {
        super(OCDGroup.PARAGRAPH, parent);
        this.add(line);
        this.groupID = line.groupID;
        this.setClassname(line.classname());
    }

    public String labelName()
    {
        Log.warn(this, ".labelName - no more implemented");
        return "";
    }

    public OCDTextLine[] lines()
    {
        return this.children().toArray(new OCDTextLine[0]);
    }

    @Override
    public OCDTextBlock canonize()
    {
        for (OCDTextLine line : this)
            line.canonize();
        if (this.canon == null)
            this.canon = new Canon();
        this.canon.refresh();
        return this;
    }

    @Override
    public OCDTextBlock uncanonize()
    {
        this.canon = null;
        for (OCDTextLine line : this)
            line.uncanonize();
        return this;
    }

    @Override
    public Cmd command(String key)
    {
        switch (key)
        {
            case CSS.TextAlign:
                return new Cmd(key, get(CSS.TextAlign, CSS._left));
            case CSS.LineHeight:
                return new Cmd(key, get(CSS.LineHeight, "1"));
            case CSS.LetterSpacing:
                return new Cmd(key, get(CSS.LetterSpacing, "0"));
            default:
                return textCommand(key);
        }
    }

    public Cmd textCommand(String key)
    {
        Occurrences<Cmd> occ = new Occurrences<>();
        for (OCDText text : this.textIt())
            occ.inc(text.command(key));
        return occ.max();
    }

    @Override
    public void command(Cmd cmd)
    {
        Log.debug(this, ".command - " + cmd);
        switch (cmd.key)
        {
            case CSS.TextAlign:
                this.set(cmd.key(), cmd.string(null));
                break;
            case CSS.LineHeight:
                this.set(cmd.key(), cmd.sReal("1"));
                break;
            case CSS.LetterSpacing:
                this.set(cmd.key, cmd.sReal("0"));
                break;
            default:
                for (OCDText text : this.textIt())
                    text.command(cmd);
                break;
        }
    }

    public float firstX()
    {
        OCDText text = this.firstText();
        return text == null ? 0 : text.x();
    }

    public float firstY()
    {
        OCDText text = this.firstText();
        return text == null ? 0 : text.y();
    }

    public Point3 firstPos()
    {
        OCDText text = this.firstText();
        return text == null ? null : text.position();
    }

    public void moveTo(double x, double y)
    {
        Rectangle3 box = this.bounds();
        double dx = x - box.x;
        double dy = y - box.y;
        for (OCDText text : this.textIt())
            text.setXY(text.x() + dx, text.y() + dy);
    }

    public String align()
    {
        return get(CSS.TextAlign, CSS._left);
    }

    // public float fontsize()
    // {
    // return props.floatValue(CSS.FontSize, 11);
    // }

    public float interline()
    {
        return props.floatValue(CSS.LineHeight, 1);
    }

    public float charspace()
    {
        return props.floatValue(CSS.LetterSpacing, 0);
    }

    public OCDTextBlock needStyle()
    {
        if (props.hasnt(CSS.LineHeight))
            props.put(CSS.LineHeight, guessLineHeight());
        if (props.hasnt(CSS.TextAlign))
            props.put(CSS.TextAlign, guessTextAlign());
        if (props.hasnt(CSS.LetterSpacing))
            props.put(CSS.LetterSpacing, guessLetterSpacing());
        return this;
    }

    public float guessLineHeight()
    {
        float fontsize = 0;
        float distance = 0;
        OCDTextLine prev = null;
        for (OCDTextLine line : this)
        {
            if (prev != null)
            {
                fontsize += prev.maxScaledFontsize();
                distance += line.y() - line.y();
            }
            prev = line;
        }
        return distance > 0 && fontsize > 0 ? distance / fontsize : 1.2f;
    }

    public String guessTextAlign()
    {

        Rectangle3 textBox = page().viewBox(OCDAnnot.ID_TEXTBOX);
        if (textBox == null)
            page().bounds();

        boolean center = true;
        for (OCDTextLine line : this)
        {
            Rectangle3 box = line.bounds();

            if (center && !Math3.Eq(box.cx(), textBox.cx(), line.fontSize()))
                center = false;

        }

        if (this.nbOfLines() == 1)
            return CSS._center;

        String align = center ? CSS._center : CSS._justify;
        // Log.debug(this, ".guessTextAlign - align=" + align + ", dLeft=" + dLeft +
        // ", dRight=" + dRight);
        return align;
    }

    public float guessLetterSpacing()
    {
        return 0f;
    }

    public boolean guessIfStarts()
    {
        OCDTextLine line = first();
        String start = Str.Substring(line == null ? "" : line.glyphString().trim(), 3);
        for (int i = 0; i < start.length(); i++)
            if (Character.isUpperCase(start.charAt(i)))
                return true;
        return false;
    }

    public boolean guessIfStops()
    {
        OCDTextLine line = last();
        String end = Str.SubstringEnd(line == null ? "" : line.glyphString().trim(), 5);
        return end.contains(".");
    }

    public OCDTextLine newLine()
    {
        return newTextLine();
    }

    public OCDTextLine newTextLine()
    {
        OCDTextLine line = new OCDTextLine(this);
        this.add(line);
        return line;
    }

    public OCDTextLine newTextLine(String content, double fontsize, String fontname, double x, double y)
    {
        OCDTextLine line = newTextLine();
        line.newText(content, fontsize, fontname, x, y);
        return line;
    }

    public OCDFlow flow()
    {
        return OCD.isFlow((OCDNode) parent) ? (OCDFlow) parent : null;
    }

    public OCDFlow flowize()
    {
        OCDFlow flow = null;
        if (parent().is(OCDGroup.TAG) || parent().is(OCDContent.TAG))
            ((OCDGroup) parent()).replace(this, flow = new OCDFlow(parent(), this.needStyle()));
        return flow;
    }

    public OCDText firstText()
    {
        return this.isEmpty() || nodes.first().isEmpty() ? null : first().first();
    }

    public OCDText lastText()
    {
        return this.isEmpty() || nodes.last().isEmpty() ? null : last().last();
    }

    @Override
    public boolean hasText()
    {
        return !isEmpty() && !first().isEmpty();
    }

    public boolean hasGlyphPath()
    {
        if (!hasText())
            return false;

        for (OCDTextLine line : this)
            for (OCDText text : line)
                if (text.hasPaths())
                    return true;

        return false;
    }

    public boolean hasOnlyDigits()
    {
        for (char c : string().toCharArray())
            if (!Character.isDigit(c) && c != ' ')
                return false;
        return true;
    }

    public int nbOfLines()
    {
        return this.nodes.size();
    }

    public int nbOfTexts()
    {
        int counter = 0;
        for (OCDTextLine line : this)
            counter += line.nbOfTexts();
        return counter;
    }

    public int nbOfChars()
    {
        int counter = 0;
        for (OCDTextLine line : this)
            for (OCDText text : line)
                counter += text.nbOfChars();
        return counter;
    }

    public int nbOf(String c)
    {
        int counter = 0;
        for (OCDTextLine line : this)
            for (OCDText text : line)
                counter += text.nbOf(c);
        return counter;
    }

    public OCDTextBlock merge(OCDTextBlock block)
    {
        this.addAll(block);
        block.remove();
        return this;
    }

    public OCDTextBlock splitXY(double xy, boolean horiz)
    {
        return horiz ? splitY(xy) : splitX(xy);
    }

    public OCDTextBlock splitX(double x)
    {
        OCDTextBlock block = new OCDTextBlock(parent());
        block.zOrder = this.zOrder;
        block.groupID = this.groupID;
        block.setClassname(this.classname());

        OCDTexts texts = new OCDTexts();
        Iterator<OCDTextLine> lineit = this.iterator();
        while (lineit.hasNext())
        {
            OCDTextLine line = lineit.next();
            Iterator<OCDText> textit = line.iterator();
            OCDText text = null;
            while (textit.hasNext() && (text = textit.next()).bounds().cx() < x)
            {
                texts.add(text);
                textit.remove();
            }
            if (line.isEmpty())
                lineit.remove();
        }
        if (texts.isPopulated())
        {
            ((OCDGroup<OCDPaintable>) this.parent()).addBefore(texts.blockize(block, false), this);
            return block;
        }
        return null;
    }

    public OCDTextBlock splitY(double y)
    {
        int index = 0;
        for (OCDTextLine line : this)
            if (y < line.y())
                break;
            else
                index++;

        return split(index, true);
    }

    public OCDTextBlock split(OCDTextLine splitLine, boolean doAdd)
    {
        // splits before splitLine
        if (splitLine == null)
            return this;
        OCDTextBlock block = new OCDTextBlock(parent());
        block.zOrder = this.zOrder;
        block.groupID = this.groupID;
        block.setClassname(this.classname());
        Iterator<OCDTextLine> iterator = iterator();
        while (iterator.hasNext())
        {
            OCDTextLine line = iterator.next();
            if (line == splitLine)
                break;
            block.add(line);
            iterator.remove();
        }
        if (block.isPopulated())
        {
            if (doAdd)
                ((OCDGroup) this.parent()).addBefore(block, this);
            return block;
        }
        return null;
    }

    public OCDTextBlock split(int index, boolean doAdd)// splits before index
    {
        if (index < 0)
            return this;
        OCDTextBlock block = new OCDTextBlock(parent());
        block.zOrder = this.zOrder;
        block.groupID = this.groupID;
        block.setClassname(this.classname());
        Iterator<OCDTextLine> iterator = iterator();
        int counter = 0;
        while (iterator.hasNext() && counter++ < index)
        {
            block.add(iterator.next());
            iterator.remove();
        }
        if (block.isPopulated())
        {
            if (doAdd)
                ((OCDGroup) this.parent()).addBefore(block, this);
            return block;
        }
        return null;
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
        State state = page().content(false).state();
        state.LINE = new OCDTextLine(this);
        state.X = Float.NEGATIVE_INFINITY;

        OCDText eolText = null;
        List3<OCDText> texts = new List3<>();
        for (OCDTextLine line : this)
        {
            for (OCDText text : line)
            {
                text.eol = false;
                texts.add(text);
                eolText = text;
            }
            if (eolText != null)
                eolText.eol = true;
        }

        super.writeAttributes(xml);
        return texts;
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        page().content(false).state().LINE = new OCDTextLine(this);
        super.readAttributes(dom);
    }

    @Override
    public XmlINode newChild(DomNode child)
    {
        if (OCD.isText(child))
            return new OCDText(this);
        else if (OCD.isGroup(child) && child.is("type", OCDGroup.TEXTLINE))
            return page().content.state().LINE = new OCDTextLine(this);
        return null;
    }

    @Override
    public void endChild(XmlINode child)
    {
        State state = page().content.state();
        if (child == null)// i.e., all children have been added
        {
            this.zOrder = this.zOrderMax();
            if (this.nodes.last() != state.LINE && !state.LINE.isEmpty())
                this.add(state.LINE);
            // Log.debug(this, "endChild - " + this.uniString(false));
        } else if (OCD.isText(child))
        {
            OCDText text = (OCDText) child;
            state.LINE.add(text);
            if (text.eol)
            {
                if (!state.LINE.isEmpty())
                    this.add(state.LINE);
                state.LINE = new OCDTextLine(this);
            }
        } else if (OCD.isGroup(child) && child instanceof OCDTextLine)
            this.add((OCDTextLine) child);
    }

    @Override
    public Shape3 shape()
    {
        return this.bounds();
    }

    public float fontsize()
    {
        return meanFontsize();
    }

    public float meanFontsize()
    {
        double fs = 0.0;
        for (OCDTextLine line : this)
            fs += line.fontSize();
        return (float) (fs / this.nbOfLines());
    }

    public boolean mostBold()
    {
        int total = 0;
        int counter = 0;
        for (OCDTextLine line : this)
            for (OCDText text : line)
            {
                total++;
                counter += text.isBold() ? 1 : 0;
            }
        return counter > total / 2;
    }

    public boolean mostItalic()
    {
        int total = 0;
        int counter = 0;
        for (OCDTextLine line : this)
            for (OCDText text : line)
            {
                total++;
                counter += text.isItalic() ? 1 : 0;
            }
        return counter > total / 2;
    }

    public double distance(OCDTextBlock block)
    {
        return this.bounds().distance(block.bounds());
    }

    public boolean distanceMax(OCDTextBlock block, double max)
    {
        return distance(block) <= max;
    }

    public boolean distanceMin(OCDTextBlock block, double min)
    {
        return distance(block) >= min;
    }

    public boolean sameFontsize(OCDTextBlock block, double epsilon)
    {
        return Math3.Eq(this.fontsize(), block.fontsize(), epsilon);
    }

    public boolean sameFontname(OCDTextBlock block)
    {
        return this.fontName().equals(block.fontName());
    }

    public String fontName()
    {
        Occurrences<String> names = new Occurrences<>();
        // Log.debug(this, ".fontName - [" + this.stringValue(false) + "]" +
        // this.nbOfLines());
        for (OCDTextLine line : this)
        {
            names.inc(line.first().fontname, line.first().length());
            names.inc(line.last().fontname, line.last().length());
        }
        return names.max();
    }

    public String fontBase()
    {
        return fontName();
    }

    public String hexaValue(boolean removeLineReturn)
    {
        StringBuilder sb = new StringBuilder();
        for (OCDTextLine line : this)
        {
            sb.append(line.hexaValue());
            sb.append(removeLineReturn ? Byte.int2hex(' ') : Byte.int2hex('\n'));
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public String string()
    {
        return stringValue(true);
    }

    public String uniString(boolean noReturn)
    {
        return this.glyphString(noReturn);
    }

    public String[] uniLines()
    {
        StringList lines = new StringList();
        for (OCDTextLine line : this)
            lines.add(line.glyphString());
        return lines.array();
    }

    public String glyphString(boolean noReturn)
    {
        StringBuilder sb = new StringBuilder();
        for (OCDTextLine line : this)
        {
            String text = line.glyphString();
            sb.append(text).append(noReturn ? (text.endsWith(" ") ? "" : " ") : "\n");
        }
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }

    public String htmlString()
    {
        return htmlString(null).toString();
    }

    public StringBuilder htmlString(StringBuilder sb)
    {
        if (sb == null)
            sb = new StringBuilder();
        int size = 0;
        for (OCDTextLine line : this)
        {
            line.htmlString(sb);
            if ((size = sb.length()) > 0 && sb.charAt(size - 1) != ' ')
                sb.append(' ');
        }
        size = sb.length();
        if (size > 0)
            sb.deleteCharAt(size - 1);
        return sb;
    }

    public String stringValue(boolean noReturn)
    {
        StringBuilder sb = new StringBuilder();
        for (OCDTextLine line : this)
            sb.append(line.stringValue()).append(noReturn ? " " : "\n");
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }

    @Override
    public String sticker()
    {
        return (hasLabel() ? label() : Str.ToString(stringValue(true), 35));
    }

    @Override
    public String toString()
    {
        return "OCDTextBlock" + "\nAlignment[" + this.align() + "]" + "\nID[" + id() + "]" + "\nZOrder[" + this.zOrder + "]" + "\nClassID[" + this.groupID
                + "]" + "\nClassname[" + this.classname() + "]" + "\nBounds" + this.bounds() + "\nLines[" + this.nbOfLines() + "]" + "\n["
                + stringValue(false) + "]";
    }

    @Override
    public boolean isEmpty()
    {
        return nodes.isEmpty();
    }

    public boolean isTextEmpty()
    {
        return isEmpty() || nodes.size() == 1 && nodes.first().isTextEmpty();
    }

    public boolean isCharEmpty()
    {
        return isEmpty() || uniString(true).trim().isEmpty();
    }

    @Override
    public OCDTextBlock copy()
    {
        return copy(parent());
    }

    public OCDTextBlock copy(OCDNode parent)
    {
        OCDTextBlock block = new OCDTextBlock(parent);
        super.copyTo(block);
        return block;
    }

    @Override
    public Rectangle3 bounds()
    {
        return canon != null ? canon : super.bounds();
    }

    @Override
    public OCDTextBlock refresh()
    {
        this.id = this.autoID();
        return this;
    }

    public OCDText textAt(int index)
    {
        int i = 0;
        for (OCDTextLine line : this)
            for (OCDText text : line)
                if (i++ == index)
                    return text;
        return null;
    }

    public int textIndex(OCDText text)
    {
        int i = 0;
        for (OCDTextLine line : this)
            for (OCDText token : line)
                if (text == token)
                    return i;
                else
                    i++;
        return -1;
    }

    public OCDText removeTextAt(int index)
    {
        int i = 0;
        for (OCDTextLine line : this)
        {
            Iterator<OCDText> textit = line.iterator();
            while (textit.hasNext())
            {
                OCDText next = textit.next();
                if (i++ == index)
                {
                    textit.remove();
                    return next;
                }
            }
        }
        return null;
    }

    public OCDText removeText(OCDText text)
    {
        for (OCDTextLine line : this)
        {
            Iterator<OCDText> textit = line.iterator();
            while (textit.hasNext())
            {
                OCDText next = textit.next();
                if (next == text)
                {
                    textit.remove();
                    return next;
                }
            }
        }
        return null;
    }

    public OCDText addTextAt(OCDText text, int index)
    {
        int i = 0;
        int textIndex = 0;
        OCDTextLine textLine = null;
        stop:
        for (OCDTextLine line : this)
        {
            textIndex = 0;
            for (OCDText t : line)
            {
                if (i++ == index)
                {
                    textLine = line;
                    break stop;
                }
                textIndex++;
            }
        }
        if (textLine != null)
        {
            textLine.nodes.add(textIndex, text);
            return text;
        }
        return null;
    }

    public OCDText addTextAt(OCDText text, OCDText anchor)
    {
        int textIndex = 0;
        OCDTextLine textLine = null;
        stop:
        for (OCDTextLine line : this)
        {
            textIndex = 0;
            for (OCDText t : line)
            {
                if (t == anchor)
                {
                    textLine = line;
                    break stop;
                }
                textIndex++;
            }
        }
        if (textLine != null)
        {
            textLine.nodes.add(textIndex, text);
            return text;
        }
        return null;
    }

    public OCDTextLine needFirstLine()
    {
        return isEmpty() ? newLine() : this.first();
    }

    public OCDTextBlock next(boolean crossLayers)
    {
        boolean next = false;
        OCDGroup<OCDPaintable> group = (OCDGroup) parent();
        if (group != null)
            for (OCDPaintable node : group)
            {
                if (next && node.isTextBlock())
                    return (OCDTextBlock) node;
                if (node == this)
                    next = true;
            }
        return null;
    }

    public OCDTextBlock[] compass()
    {
        OCDTextBlock top = null;
        OCDTextBlock right = null;
        OCDTextBlock bottom = null;
        OCDTextBlock left = null;
        Rectangle3 box = this.bounds();
        for (OCDTextBlock cBlock : this.page().content.blockIt())
        {
            Rectangle3 cBox = cBlock.bounds();
            if (box.hasOverlapX(cBox))
            {
                if (box.maxY() < cBox.minY())
                {
                    if (bottom == null || cBox.minY() < bottom.bounds().minY())
                        bottom = cBlock;
                } else if (box.minY() > cBox.maxY())
                {
                    if (top == null || cBox.maxY() > top.bounds().maxY())
                        top = cBlock;
                }
            }
            if (box.hasOverlapY(cBox))
            {
                if (box.maxX() < cBox.minX())
                {
                    if (right == null || cBox.minX() < right.bounds().minX())
                        right = cBlock;
                } else if (box.minX() > cBox.maxX())
                {
                    if (left == null || cBox.maxX() > left.bounds().maxX())
                        left = cBlock;
                }
            }
        }

        return new OCDTextBlock[]
                {top, right, bottom, left};
    }

    public void sortLines(boolean topDown)
    {
        Collections.sort(nodes, (l1, l2) -> Math3.Sign(l1.y() - l2.y(), topDown));
    }

    public OCDTextBlock sortXY()
    {
        return sort(true, true);
    }

    public OCDTextBlock sort(boolean topDown, boolean leftRight)
    {
        this.sortLines(topDown);
        for (OCDTextLine line : this)
            line.sortTexts(leftRight);
        return this;
    }

    @Override
    public int rOrder()
    {
        int rOrder = 1;
        for (OCDTextBlock cBlock : this.page().content.blockIt())
        {
            if (cBlock == this)
                return rOrder;
            rOrder++;
        }
        return rOrder;
    }

    public OCDTextBlock reblock()
    {
        return this.allTexts().blockize(this, false);
    }

    public OCDTextBlock alignRight(float maxX)
    {
        for (OCDTextLine line : this)
            line.alignRight(maxX);
        return this;
    }

    public OCDTextBlock cleanEmptyLines()
    {
        Iterator<OCDTextLine> lineIt = this.children().iterator();
        while (lineIt.hasNext())
            if (lineIt.next().children().isEmpty())
                lineIt.remove();
        return this;
    }

}
