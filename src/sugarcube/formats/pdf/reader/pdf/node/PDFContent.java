package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.*;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFColorSpace;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFTilingPattern;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFontType3;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;
import sugarcube.formats.pdf.reader.pdf.node.shade.PDFShading;
import sugarcube.formats.pdf.reader.pdf.object.*;
import sugarcube.formats.pdf.reader.pdf.object.PDFOperator.OP;
import sugarcube.formats.ocd.OCD;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static sugarcube.formats.pdf.reader.pdf.node.PDFPath.*;

/**
 * PDF Reference says: A document's pages (and other visual elements) can
 * contain any combination of text, graphics, and images. A page's appearance is
 * described by a PDF content stream, which contains a sequence of graphics
 * objects to be painted on the page. This appearance is fully specified; all
 * layout and formatting decisions have already been made by the application
 * generating the content stream.
 * <p>
 * !!! PDFContent is used to parse: PDF content streams, xObject content
 * streams, and FontType3 content streams
 */
public class PDFContent extends PDFNode<PDFNode>
{
    protected enum Phase
    {
        PAGE, PATH, TEXT, IMAGE
    }

    public static final boolean DEBUG = true;
    public static final BoyerMoore PATTERN_EI = new BoyerMoore("EI");
    public static final String TYPE_CONTENT = "Content";
    public static final String TYPE_XOBJECT = "XObject";
    public static final String TYPE_RESOURCE = "ResourceXObject";
    public static final String TYPE_FONTTYPE3 = "FontType3";
    public static final String TYPE_SOFTCLIP = "SoftClip";
    public static final String TYPE_TILINGPATTERN = "TilingPattern";
    private transient Map3<String, PDFInstr> instrFontT3 = new Map3<>();
    private transient Phase phase = Phase.PAGE;
    private transient PDFResources resources;
    private transient List3<PDFState> states = new List3<>();
    private transient PDFPath path = new PDFPath(this);
    private PDFRectangle bbox;
    private PDFState state;
    private String resourceID;
    private PDFMatrix resourceTM;
    private PDFStream resourceStream;
    private PDFTransparencyGroup group;
    private boolean hasSeenBadOp = false;
    // todo: add baseAlpha to shadings
    public double baseFillAlpha = 1.0;
    public double baseStrokeAlpha = 1.0;
    public String[] baseBlendModes = null;
    public PDFSoftMask baseSoftClip = null;
    public PDFClip baseClip = null;
    public boolean debug = false;
    private List3<PDFMark> marks = new List3<PDFMark>();// not affected by state q
    // Q
    public String subtype = TYPE_CONTENT;
    public Area textClip = null;
    public PDFContext context = null;
    public PDFContent parentContent = null;
    private int pathCounter = 0;

    // public String instanceText = "";

    // Parsing of the page PDF contents, i.e., all the contents (one or more) of a
    // single PDF page
    public PDFContent(PDFPage page, PDFObject po)
    {
        super(Dexter.CONTENT, page);
        this.subtype = TYPE_CONTENT;
        this.resources = page.resources;
        this.bbox = new PDFRectangle(po, page.boxes.mediaBox);

        this.setDocContent(this);// set current content
        this.state = new PDFState(this);// graphics state is initialized once for
        // all contents of a page
        this.addClip(new PDFClip(this, page.cropBox()));
        List<PDFObject> params = new ArrayList<PDFObject>();
        this.reference = po.reference();
        this.context = new PDFContext(this);

        if (po.type == PDFObject.Type.Stream)
            this.parse(new StreamReader(po.toPDFStream()), po, params);
        else if (po.type == PDFObject.Type.Array)
        {
            ByteArray bytes = new ByteArray();
            List3<PDFStream> streams = new List3<>();
            // markers used only for debugging purpose in PDF Inspector :-)
            IntArray streamMarkers = new IntArray();
            int marker = 0;
            for (PDFPointer ref : po.toPDFArray().pdfIndirectReferences())
            {
                PDFStream stream = ref.unreference().toPDFStream();
                byte[] streamBytes = stream.byteValues();
                marker += streamBytes.length + 1;
                streamMarkers.add(marker);
                bytes.add(streamBytes);
                bytes.add((byte) 32);
                streams.add(stream);
                //Log.debug(this, " - reading stream " + ref);
            }
            StreamReader reader = new StreamReader(streams.toArray(new PDFStream[0]), streamMarkers.array(), bytes.array());
            parse(reader, po, params);
        } else
            Log.warn(this, " - unknown object: " + po.toString());
        this.states.clear();
    }

    // parsing FontType3 content stream
    public PDFContent(PDFFontType3 font, PDFStream stream)
    {
        super(Dexter.CONTENT, font);
        this.subtype = TYPE_FONTTYPE3;
        this.reference = stream.reference();
        this.bbox = stream.get("BBox").toPDFRectangle();
        this.resources = null;
        PDFContent dc = this.setDocContent(this);// set current content
        this.state = new PDFState(this);// graphics state is initialized once for
        // all contents of a page
        this.parse(new StreamReader(stream), stream, new ArrayList<PDFObject>());
        this.setDocContent(dc);
        this.states.clear();
    }

    public PDFContent(PDFTilingPattern pattern, PDFStream stream, String id)
    {
        super(Dexter.CONTENT, pattern);
        this.subtype = TYPE_TILINGPATTERN;
        this.reference = stream.reference();
        this.bbox = stream.get("BBox").toPDFRectangle();
        this.resources = new PDFResources(this);
        this.resources.populate(stream.get("Resources").toPDFDictionary());
        PDFContent dc = this.setDocContent(this);// set current content
        this.state = new PDFState(this);// graphics state is initialized once for
        // all contents of a page
        this.addClip(new PDFClip(this, pattern.bbox().rectangle()));
        this.resourceID = id;
        this.resourceStream = stream;
        this.resourceTM = new PDFMatrix(stream.get("Matrix").toPDFArray().floatValues(1, 0, 0, 1, 0, 0));
        this.parse(new StreamReader(stream), stream, new ArrayList<PDFObject>());
        this.setDocContent(dc);
        this.states.clear();
    }

    // a special resource XObject used as soft clip for content stream primitives
    // or content XObject
    public PDFContent(PDFSoftMask smask, PDFStream stream)
    {
        super(Dexter.CONTENT, smask);
        this.subtype = TYPE_SOFTCLIP;
        PDFExtGState xState = smask.xState;
        this.baseBlendModes = xState.blendModes();
        this.baseFillAlpha = xState.fillAlpha();
        // Log.debug(this,
        // " - "+this.reference+": baseFillAlpha="+this.baseFillAlpha);
        this.baseStrokeAlpha = xState.strokeAlpha();
        this.reference = stream.reference();
        this.bbox = stream.get("BBox").toPDFRectangle();
        this.resources = new PDFResources(this);
        this.resources.populate(stream.get("Resources").toPDFDictionary());
        this.resourceID = stream.reference().toString();
        this.resourceStream = stream;
        this.resourceTM = new PDFMatrix(stream.get("Matrix").toPDFArray().floatValues(1, 0, 0, 1, 0, 0));
        this.add(this.resources);
        if (stream.contains("Group"))
        {
            this.group = new PDFTransparencyGroup(this, stream.get("Group").toPDFDictionary());
            this.add(group);
        }
    }

    // Resource content XObject added to resource dictionary, NO PARSING YET.
    public PDFContent(PDFResources parent, String resourceID, PDFStream stream)
    {
        super(Dexter.CONTENT, parent);
        this.subtype = TYPE_RESOURCE;
        this.reference = stream.reference();
        this.bbox = stream.get("BBox").toPDFRectangle();
        this.resources = new PDFResources(this);
        this.resources.populate(stream.get("Resources").toPDFDictionary());
        this.resources.setParentPDFResources(parent);
        this.resourceID = resourceID;
        this.resourceStream = stream;
        this.resourceTM = new PDFMatrix(stream.get("Matrix").toPDFArray().floatValues(1, 0, 0, 1, 0, 0));
        this.add(this.resources);
        if (stream.contains("Group"))
        {
            this.group = new PDFTransparencyGroup(this, stream.get("Group").toPDFDictionary());
            this.add(group);
        }
    }

    // Resource content XObject referenced in content stream is NOW instanciated,
    // hence PARSED
    private PDFContent(PDFContent xObject, PDFContent parentContent, PDFContext context)
    {
        super(Dexter.CONTENT, xObject.resources);

        // if (this.isReference(995))
        // {
        // Log.debug(this, " - XObject instanciation 995");
        // }
        this.subtype = TYPE_XOBJECT;
        this.debug = true;
        this.reference = xObject.reference;
        // Log.debug(this, " - XObject instance: " + reference);
        this.bbox = xObject.bbox;
        this.resources = xObject.resources;
        this.resourceID = xObject.resourceID;
        this.resourceStream = xObject.resourceStream;
        this.resourceTM = xObject.resourceTM;

        this.context = context;
        if (parentContent != null)// null when rasterizing XObject
        {
            parentContent.saveState();
            this.parentContent = parentContent;
            this.state = parentContent.state();
            this.state.fillAlpha *= parentContent.baseFillAlpha;
            this.state.strokeAlpha *= parentContent.baseStrokeAlpha;
            if (isMultiply(parentContent.baseBlendModes))
                this.state.blendModes = parentContent.baseBlendModes;
            // this.state.setContent(this);//current content is xObject
        } else
        {
            this.state = new PDFState(this);
            this.state.fillAlpha = xObject.baseStrokeAlpha;
            this.state.strokeAlpha = xObject.baseStrokeAlpha;
            if (parentContent != null && isMultiply(parentContent.baseBlendModes))
                this.state.blendModes = parentContent.baseBlendModes;
        }
        PDFContent dc = this.setDocContent(this);
        // this.state.xObject = this;
        this.baseFillAlpha = state.fillAlpha;
        this.baseStrokeAlpha = state.strokeAlpha;
        this.baseSoftClip = state.softClip;
        this.baseBlendModes = state.blendModes;
        this.baseClip = state.clip;
        if (parentContent != null)
            this.addClip(new PDFClip(this, parentContent.resources.page().cropBox()));
        else
            this.addClip(new PDFClip(this, xObject.bbox.rectangle()));

        this.state.ctm = this.resourceTM.concat(this.state.ctm);
        this.parse(new StreamReader(resourceStream), resourceStream, new ArrayList<PDFObject>());
        // Log.debug(this, " - adding xObject content "+reference+": alpha=" +
        // state.fillAlpha);
        this.setDocContent(dc);
        if (parentContent != null)
            parentContent.restoreState(); // pageContent.state.setContent(pageContent);
        // this.instanceText = "yep";
    }

    // Content XObject stream reference instanciation, i.e., Do
    @Override
    public PDFNode instance(PDFContent content, PDFInstr instr, PDFContext context)
    {
        // Resource XObject (this) are referenced in Resources dictionaries and
        // instanciated each time they are encountered in content streams
        PDFContent instance = new PDFContent(this, content, context);
        // if no baseSoftClip return XObject content

        boolean noSoftClip = instance.baseSoftClip == null;
        // if(content!=null && this.isReference(507))
        // {
        // Log.debug(this,
        // ".instance - parent="+content.subtype+", XObject="+this.reference+",
        // instance="+instance.subtype+(noSoftClip
        // ? "" : ", softClip=true"));
        // }

        if (noSoftClip)
            return instance;

        // here we've got to render raster background to compose it with the
        // baseSoftClip and this instance

        // Log.debug(this, ".instance - rasterizing XObject" + this.reference +
        // ": mask=" + stateContent.baseSoftClip.reference);
        // trick to avoid rasterized text... however rendering may not be preserved
        // since text is no more soft clipped, it's a tradeoff to assume...

        PDFImage image = instance.baseSoftClip.clip(instance, context);
        Iterator<PDFNode> it = instance.iterator();
        while (it.hasNext())
        {
            PDFNode node = it.next();
            if (node.isText())
            {
                node.toText().transparentize();
            } else
            {
                it.remove();
            }
        }
        instance.children.addFirst(image);
        return instance;
    }

    public PDFTransparencyGroup transparencyGroup()
    {
        return group;
    }

    public boolean isSubtype(String type)
    {
        return subtype == null ? type == null : subtype.equals(type);
    }

    public String[] blendModes(String[] bm)
    {
        if (isMultiply(baseBlendModes))
            return baseBlendModes;
        else if (bm == null || bm.length == 0 || bm.length == 1 && bm[0].equalsIgnoreCase("normal"))
            return this.baseBlendModes;
        else
            return bm;
    }

    public static boolean isMultiply(String[] blends)
    {
        if (blends != null)
            for (String b : blends)
                if (b.equalsIgnoreCase("multiply"))
                    return true;
        return false;
    }

    public List3<PDFMark> marks()
    {
        return this.marks;
    }

    // public PDFMark pMark()
    // {
    // for (PDFMark mark : marks)
    // if (mark.isTag("P"))
    // return mark;
    // return null;
    // }
    public void addMark(PDFMark mark)
    {
        this.marks.addFirst(mark);
    }

    public PDFMark removeMark()
    {
        if (marks.isPopulated())
            return this.marks.removeFirst();
        else
            Log.debug(this, ".removeMark - marks is empty: " + this);
        return null;
    }

    public PDFContent docContent()
    {
        return this.document().content();
    }

    public final PDFContent setDocContent(PDFContent content)
    {
        return this.document().setContent(content);
    }

    public PDFRectangle bbox()
    {
        return bbox;
    }

    public PDFContent.Phase phase()
    {
        return this.phase;
    }

    public PDFInstr instruction(String opname)
    {
        return this.instrFontT3.get(opname);
    }

    public PDFResources resources()
    {
        return resources;
    }

    public boolean hasResources()
    {
        return this.resources != null;
    }

    public PDFState state()
    {
        return this.state;
    }

    public final void parse(StreamReader reader, PDFObject stream, List<PDFObject> params)
    {
        //Log.debug(this, ".parse - "+stream.reference());
        String word;
        while ((word = reader.token()) != null)
        {
            PDFObject obj = stream.parsePDFObject(word, reader).unreference();
            if (obj.type == PDFObject.Type.Operator)
            {
                if (false && DEBUG)
                {
                    System.out.print(" " + word);
                }
                PDFInstr instr = new PDFInstr(obj.toPDFOperator(), params, phase);

                //Log.debug(this, ".parse - " + reader.streamReference() + " " + phase + " " + instr.op + ": " + params);

                switch (this.phase)
                {
                    case PAGE:
                        parsePageOp(instr);
                        break;
                    case TEXT:
                        parseTextOp(instr);
                        break;
                    case PATH:
                        parsePathOp(instr);
                        break;
                    case IMAGE:
                        parseImageOp(instr, reader, stream);
                        break;
                }
                params.clear();
            } else
                params.add(obj);
        }
        if (pathCounter++ > OCD.MAX_NB_OF_DISPLAY_PATHS)
            Log.warn(this, ".parse - high nb of paths: " + pathCounter);
    }

    private void parsePageOp(PDFInstr instr)
    {
        PDFTextState textState = state.getTextState();
        List3<PDFObject> ps = instr.params();
        switch (instr.op())
        {
            case BI:
                phase = Phase.IMAGE;
                break;
            case BT:
                textClip = null;
                textState.resetMatrices();
                phase = Phase.TEXT;
                break;
            case d:
                state.getLineStyle().setDashValues(ps.get(0).doubleValues(), ps.get(1).doubleValue());
                break;
            case Do:
                // if (reference.is(995))
                // {
                // Log.debug(this, " - parsePageOp: key=" + ps.get(0).stringValue() +
                // ", from=" + resources.xObjects.keySet());
                // }
                addNode(instr, resources.getXObject(ps.get(0).stringValue()), context);
                break;
            case d0:
                instrFontT3.put(instr.op().name, instr);
                break;
            case d1:
                instrFontT3.put(instr.op().name, instr);
                break;
            case g:// set fill color in a default DeviceGray space
                state.setFillColorSpace(PDFColorSpace.instance(this, "DeviceGray"));
                state.setFillColor(new PDFColor(this, false, ps));
                break;
            case G:// set stroke color in a default DeviceGray space
                state.setStrokeColorSpace(PDFColorSpace.instance(this, "DeviceGray"));
                state.setStrokeColor(new PDFColor(this, true, ps));
                break;
            case i:// line style flatness is an obsolete operator used for rendering
                // performance
                break;
            case j:
                state.getLineStyle().setLineJoin(ps.get(0).intValue());
                break;
            case J:
                state.getLineStyle().setLineCap(ps.get(0).intValue());
                break;
            case k:// set fill color in a default DeviceCMYK space
                state.setFillColorSpace(PDFColorSpace.instance(this, "DeviceCMYK"));
                state.setFillColor(new PDFColor(this, false, ps));
                break;
            case K:// set stroke color in a default DeviceCMYK space
                state.setStrokeColorSpace(PDFColorSpace.instance(this, "DeviceCMYK"));
                state.setStrokeColor(new PDFColor(this, true, ps));
                break;
            case m:// begin a path object
                path.moveTo(ps);
                phase = Phase.PATH;
                break;
            case M:
                state.getLineStyle().setMiterLimit(ps.get(0).doubleValue());
                break;
            case re:
                path.addRectangle(ps);
                phase = Phase.PATH;
                break;
            case ri:// rendering intent is not really useful, quite obsolete
                break;
            case sh:
                this.addShading(instr, ps.get(0).stringValue());
                break;
            case TL:
                textState.setLeading(ps.get(0).floatValue());
                break;
            case Tc:
                textState.setCharSpace(ps.get(0).floatValue());
                break;
            case Tf:
                textState.setFont(ps.get(0).stringValue());
                textState.setFontSize(ps.get(1).floatValue(1));
                break;
            case Tr:
                textState.setRenderingMode(ps.get(0).intValue());
                break;
            case Ts:
                textState.setTextRise(ps.get(0).floatValue());
                break;
            case Tw:
                textState.setWordSpace(ps.get(0).floatValue());
                break;
            case Tz:
                textState.setScale(ps.get(0).floatValue() / 100);
                break;
            case w:
                state.getLineStyle().setLineWidth(ps.get(0).doubleValue());
                break;
            default:
                parseCommonOp(instr);
                break;
        }
    }

    private void parseTextOp(PDFInstr instr)
    {
        PDFTextState textState = this.state.getTextState();
        List3<PDFObject> ps = instr.params();
        switch (instr.op())
        {
            case guil:// " move to the next line and show a text string using updated
                // word and char space, same as aw Tw ac Tc string '
                textState.setWordSpace(ps.get(0).floatValue());
                textState.setCharSpace(ps.get(1).floatValue());
                // PDF defines positive leadings but uses negative ones since the origin is
                // at the page bottom
                textState.nextLine(0, -textState.getLeading());
                addText(new PDFText(this, ps.get(2).toPDFString(), 0, instr));
                break;
            case apos:
                // ' move to the next line and show a text string, same as T* string Tj
                textState.nextLine(0, -textState.getLeading());
                addText(new PDFText(this, ps.get(0).toPDFString(), 0, instr));
                break;
            case d:
                state.getLineStyle().setDashValues(ps.get(0).doubleValues(), ps.get(1).doubleValue());
                break;
            case ET:
                phase = Phase.PAGE;
                if (this.textClip != null)
                {
                    this.addTextClip();
                    this.textClip = null;
                }
                break;
            case i:// line style flatness is an obsolete operator used for rendering
                // performance
                break;
            case j:
                state.getLineStyle().setLineJoin(ps.get(0).intValue());
                break;
            case J:
                state.getLineStyle().setLineCap(ps.get(0).intValue());
                break;
            case k:// set fill color in a default DeviceCMYK space
                state.setFillColorSpace(PDFColorSpace.instance(this, "DeviceCMYK"));
                state.setFillColor(new PDFColor(this, false, ps));
                break;
            case K:// set stroke color in a default DeviceCMYK space
                state.setStrokeColorSpace(PDFColorSpace.instance(this, "DeviceCMYK"));
                state.setStrokeColor(new PDFColor(this, true, ps));
                break;
            case M:
                state.getLineStyle().setMiterLimit(ps.get(0).doubleValue());
                break;
            case ri:// rendering intent is not really useful, quite obsolete
                break;
            case T_:// T* move to the start of the next line, same as 0 leading Td
                // PDF defines positive leadings but uses negative ones since theorigin is
                // at the page bottom
                textState.nextLine(0, -textState.getLeading());
                break;
            case TD:// move to the start of the next line by offset tx, ty (unscaled)
                // and set TL to -ty
                textState.setLeading(-ps.get(1).floatValue());
                textState.nextLine(ps.get(0).doubleValue(), ps.get(1).doubleValue());
                break;
            case TJ:// show one or more text strings, if element is a string then show
                // it, if element is a number then adjust the position
                PDFObject[] a = ps.get(0).array();
                boolean isNumberFirst = a.length > 0 && a[0].isPDFNumber();
                if (isNumberFirst)
                    this.addText(new PDFText(this, new PDFString(a[0].toPDFNumber().environment()), a[0].intValue(0) * 0.001, instr));

                PDFText run = null;
                for (int i = (isNumberFirst ? 1 : 0); i < a.length; i++)
                {
                    if (a[i].isPDFString())
                    {
                        int adj = i < a.length - 1 ? (a[i + 1].isPDFNumber() ? a[i + 1].intValue(0) : 0) : 0;
                        PDFText text = new PDFText(this, a[i].toPDFString(), adj * 0.001, instr);
                        if (run == null)
                            this.addText(run = text);
                        else
                            run.mergeRun(text);
                        if (Math.abs(adj) > 150)
                            run = null;
                    }
                }
                break;
            case TL:// set the vertical text leading, (unscaled), used by TD, T*, ', and
                // "
                textState.setLeading(ps.get(0).floatValue());
                break;
            case Tc:// set the character spacing (unscaled), used by Tj, TJ, and '
                textState.setCharSpace(ps.get(0).floatValue());
                break;
            case Td:// move to the start of the next line by offset tx and ty (unscaled)
                textState.nextLine(ps.get(0).doubleValue(), ps.get(1).doubleValue());
                break;
            case Tf:// set the text font name (mapping to font dictionary) and size
                textState.setFont(ps.get(0).stringValue());
                textState.setFontSize(ps.get(1).floatValue(1));
                break;
            case Tj:// show a text string
                addText(new PDFText(this, ps.get(0).toPDFString(), 0, instr));
                break;
            case Tm:// set the text matrix and the text line matrix to a b c d e f
                textState.setMatrices(new PDFMatrix(ps));
                break;
            case Tr:// set the text rendering mode, stroking, filling, clipping, etc.
                textState.setRenderingMode(ps.get(0).intValue());
                break;
            case Ts:// set the text rise (unscaled)
                textState.setTextRise(ps.get(0).floatValue());
                break;
            case Tw:// set the word spacing (unscaled), used by Tj, TJ, and '
                textState.setWordSpace(ps.get(0).floatValue());
                break;
            case Tz:// set the horizontal scaling to scale/100
                textState.setScale(ps.get(0).floatValue() / 100);
                break;
            case w:
                state.getLineStyle().setLineWidth(ps.get(0).doubleValue());
                break;
            default:
                parseCommonOp(instr);
                break;
        }
    }

    private void parsePathOp(PDFInstr instr)
    {
        List<PDFObject> ps = instr.params();
        switch (instr.op())
        {
            case b:
                addClipOrPath(instr, path, CLOSE_PATH | NON_ZERO_FILL_STROKE);
                phase = Phase.PAGE;
                break;
            case B:
                addClipOrPath(instr, path, NON_ZERO_FILL_STROKE);
                phase = Phase.PAGE;
                break;
            case b_:
                addClipOrPath(instr, path, CLOSE_PATH | FILL_STROKE);
                phase = Phase.PAGE;
                break;
            case B_:
                addClipOrPath(instr, path, FILL_STROKE);
                phase = Phase.PAGE;
                break;
            case c:
                path.cubicCurveTo(ps);
                break;
            case f:
                addClipOrPath(instr, path, FILL | NON_ZERO_FILL);
                phase = Phase.PAGE;
                break;
            case F:
                addClipOrPath(instr, path, FILL | NON_ZERO_FILL);
                phase = Phase.PAGE;
                break;
            case f_:
                addClipOrPath(instr, path, FILL);
                phase = Phase.PAGE;
                break;
            case h:// close the current subpath by appending a straight line segment
                // from the current point to the starting point of the subpath.
                path.closePath();
                break;
            case l:// append a straight line segment from the current point to the point
                // x,y, the new current point is x,y
                path.lineTo(ps);
                break;
            case m:// begin a new subpath by moving the current point to coordinates x,y
                path.moveTo(ps);
                break;
            case n:// no-op path painting operator, i.e, paint nothing, but creates a
                // new clipping path
                addClipOrPath(instr, path, NO_OP);
                phase = Phase.PAGE;
                break;
            case re:
                path.addRectangle(ps);
                break;
            case s:
                addClipOrPath(instr, path, CLOSE_PATH | STROKE);
                phase = Phase.PAGE;
                break;
            case S:
                addClipOrPath(instr, path, STROKE);
                phase = Phase.PAGE;
                break;
            case v:
                path.cubicCurveTo1(ps);
                break;
            case W:// modify the current clipping path by intersecting it with the
                // current path using the nonzero winding rule
                path.toNonZeroClip();
                // finalize(vop, path, NO_OP);
                break;
            case W_:// W* modify the current clipping path by intersecting it with the
                // current path using the even-odd rule
                path.toEvenOddClip();
                // finalize(vop, path, NO_OP);
                break;
            case y:
                path.cubicCurveTo2(ps);
                break;
            default:
                parseCommonOp(instr);
                break;
        }
    }

    private void parseCommonOp(PDFInstr instr)
    {
        List3<PDFObject> ps = instr.params();
        switch (instr.op())
        {
            case BDC:
            case BMC:
                this.addMark(new PDFMark(this, instr));// Marked content
                break;
            case cm: // moved because of Funke (previously page op)
                state.combineCTM(new PDFMatrix(ps));
                break;
            case cs:
                state.setFillColorSpace(this, ps.get(0).stringValue());
                break;
            case CS:
                state.setStrokeColorSpace(this, ps.get(0).stringValue());
                break;
            case EMC:
                this.removeMark();
                break;
            case DP:
                break;// Marked content
            case MP:// Marked content
                break;
            case gs:// update the current graphics state with the extended graphics
                // state (in the resource dictionary)
                state.updateExtGState(this, ps.get(0).stringValue());
                break;
            case sc:
                state.setFillColor(new PDFColor(this, false, ps));
                break;
            case SC:
                state.setStrokeColor(new PDFColor(this, true, ps));
                break;
            case scn:
                // Log.debug(this, ".parseCommonOp - scn: "+ps);
                state.setFillColor(new PDFColor(this, false, ps));
                break;
            case SCN:
                state.setStrokeColor(new PDFColor(this, true, ps));
                break;
            case rg:// set fill color in a default DeviceRGB space
                state.setFillColorSpace(PDFColorSpace.instance(this, "DeviceRGB"));
                state.setFillColor(new PDFColor(this, false, ps));
                break;
            case RG:// set stroke color in a default DeviceRGB space
                state.setStrokeColorSpace(PDFColorSpace.instance(this, "DeviceRGB"));
                state.setStrokeColor(new PDFColor(this, true, ps));
                break;
            case g:// set fill color in a default DeviceGray space //moved because of
                // Funke (previously path op)
                state.setFillColorSpace(PDFColorSpace.instance(this, "DeviceGray"));
                state.setFillColor(new PDFColor(this, false, ps));
                break;
            case G:// set stroke color in a default DeviceGray space //moved because of
                // Funke (previously path op)
                state.setStrokeColorSpace(PDFColorSpace.instance(this, "DeviceGray"));
                state.setStrokeColor(new PDFColor(this, true, ps));
                break;
            case q:// save the current graphics state on the graphics state stack
                saveState();
                break;
            case Q:// restore the graphics state by removing the most recently saved
                // state from the stack and making it the current state
                restoreState();
                break;
            case BX:// Begin ignore : used to ignore operators for old PDF readers...
                // hence we ignore ignore :-p
                break;
            case EX:// End ignore
                break;
            case n:// n sometimes appears during the Page phase (however it should not)
                // right before a Path phase (apparently with no real effect)
                break;
            default:
                this.add(new PDFBugOp(instr, this));
                if (!this.hasSeenBadOp)
                {
                    this.hasSeenBadOp = true;
                    Log.info(this, ".parseCommonOp - new op in phase " + phase.name() + ": op=" + instr.op.stringValue() + ", params=" + instr.params + ", ref="
                            + this.reference);
                }
                break;
        }
    }

    private void parseImageOp(PDFInstr instr, StreamReader reader, PDFObject po)
    {
        PDFDictionary map = new PDFDictionary(po);
        PDFName name = null;
        if (instr.op().equals(OP.ID))
        {
            for (PDFObject params : instr.params)
                if (name == null && params.isPDFName())
                    name = params.toPDFName();
                else if (name != null)
                {
                    map.add(name.toString(), params);
                    name = null;
                }
            if (reader.view() == PDF.SP)
                reader.read();


            String cs = map.get("CS").stringValue();
            boolean hasFilter = map.has("F");
            int bits = map.get("W").intValue(0) * map.get("H").intValue(0);
            int bpc = map.get("BPC").intValue(0);

            bits = bpc % 8 == 0 ? bits * bpc : 0;

            if (hasFilter || Str.HasData(cs))
                bits = 0;
            else
            {
                switch (cs)
                {
                    case "RGB":
                    case "DeviceRGB":
                        bits *= 3;
                        break;
                    case "G":
                    case "DeviceGray":
                        break;
                    case "CMYK":
                    case "DeviceCMYK":
                        bits *= 4;
                        break;
                    default:
                        bits = -1;
                }
            }

            byte[] data = reader.readStream(bits > 0 ? (bits / 8 - 2) : -1, PATTERN_EI, true);

            //Log.debug(this, ".parseImageOp - " + map + ", cs=" + cs + ", hasFilter=" + hasFilter+", bytes=" + bits/8 + ", data.length="+data.length);

            PDFStream stream = new PDFStream(map, data);
            PDFImage image = new PDFImage(this, PDFImage.INLINE_RESOURCE_ID, stream);
            this.addNode(instr, image, context);
            this.phase = Phase.PAGE;
        } else
            Log.warn(this, ".parseImageOp - inline image ID tag not found" + instr);
    }

    private void addTextClip()
    {
        // Log.debug(this, ".addTextClip - :-)");
        addClip(new PDFClip(this, new Path3(textClip)));
    }

    private void addClip(PDFClip clip)
    {
        // if(true)
        // {
        // state.setClip(clip);
        // this.add(clip);
        // return;
        // }

        // we do not want to add unused and redundant clipping paths !
        boolean addClip = true;
        Iterator<PDFNode> iterator = children.descendingIterator();

        while (iterator.hasNext())
        {
            PDFNode object = iterator.next();
            if (object.isClip())
            {
                if (clip.sameClippingPath(object.toClip()))
                {
                    addClip = false;
                    state.setClip(object.toClip());
                }
                break;
            }
        }
        if (addClip)
        {
            if (!children.isEmpty() && children.getLast().isClip())
                children.removeLast();
            state.setClip(clip);
            this.add(clip);
            // Log.debug(this, ".addClip - "+clip.path.stringValue());
        }
    }

    private void addNode(PDFInstr instr, PDFNode node, PDFContext context)
    {
        // Log.debug(this, ".addNode - marks=" + marks());

        if (node == null)
            Log.debug(this, ".addNode - null value: " + instr);
        else
        {
            // if (reference.is(995))
            // {
            // this.instanceText += ", "+instr.op;
            // Log.debug(this, " - addNode: instr=" + instr.op + ", children=" +
            // this.children.size()+", text="+this.instanceText+",
            // id="+this.resourceID);
            // }
            PDFNode instance = node.instance(this, instr, context);
            add(instance);
        }
    }

    @Override
    public void add(PDFNode node)
    {
        if (node != null)
        {
            if (this.context != null)
                context.add(node);
            super.add(node);
        }
    }

    private void addClipOrPath(PDFInstr instr, PDFPath path, int flag)
    {
        this.path = new PDFPath(this);
        if (flag == NO_OP)
        {
            // Zen.LOG.debug(this,".addClip - clipRule="+path.clipRule+",
            // data="+path.path.stringValue());
            if (path.clipRule > -1)
                this.addClip(new PDFClip(path, state, instr));
        } else
        {
            //path merging deactivated
            PDFNode prevNode = null; //this.children.last();
            PDFNode currNode = path.finalize(this, instr, flag, context);

            if (prevNode != null && prevNode.isPath() && currNode != null && currNode.isPath())
            {
                PDFPath prevPath = prevNode.toPath();
                PDFPath currPath = currNode.toPath();
                if (prevPath.hasIdenticalDrawingStateWith(currPath))
                {
                    prevPath.path.append(currPath.path, false);
                    currNode = null;
                    Log.debug(this, ".addClipOrPath - merging 2 paths: " + prevPath.path.nbOfSubPaths());
                }
            }

            if (currNode != null)
            {
                add(currNode);
                pathCounter++;
            }
        }
    }

    private void addText(PDFText... texts)
    {
        if (texts.length == 0)
            return;
        else if (this.state.textState.renderingMode < 7)
            for (PDFText text : texts)
                add(text);
        else if (this.state.textState.renderingMode > 3)
        {
            String s = "";

            for (int i = 0; i < texts.length; i++)
            {
                Area area = new Area(texts[i].toClippingPath());
                if (textClip == null)
                    textClip = area;
                else
                    textClip.add(area);
                s += texts[i].stringValue() + " ";
            }

            //Log.debug(this, ".addText - add clipping text: renderingMode=" + this.state.textState.renderingMode + ", text=" + s);
            // clip added when op ET is called
        }
    }

    private void addShading(PDFInstr instr, String name)
    {

        if (resources().shadings.containsKey(name))
        {
            PDFShading shading = resources().shadings.get(name);
            state.shading = shading;
            shading.setTransform(state.ctm);
            PDFNode last = this.children.isEmpty() ? null : this.children.getLast();

            if (last == null)
            {
                // this.shading = PDFShading.instance(this, name);
            } else if (last.isClip())
            {
                PDFClip clip = last.toClip();
                if (!clip.path.isEmpty())
                {
                    // Zen.LOG.debug(this,
                    // ".addShading - last clip="+clip.path.stringValue());
                    this.children.removeLast();

                    this.addClip(new PDFClip(this, false));

                    this.add(new PDFPath(clip, shading, state.ctm, baseFillAlpha));
                }
            } else if (last.isPath())
            {
                PDFPath shadingPath = last.toPath();
                shadingPath.setShading(shading);
                shadingPath.setShadingTM(state.ctm);
                shadingPath.setShadingAlpha(baseFillAlpha);
            }
        }

    }

    public void saveState()
    {
        this.states.add(state.copy());
    }

    public void restoreState()
    {
        if (!this.states.isEmpty())
        {
            this.state.update(states.removeLast());
            this.addClip(state.clip);// never, never, never remove this line
            // accidentally... 2014.03.03 :-p
        } else
            Log.warn(this, ".restoreState - state stack is empty");
    }

    @Override
    public String sticker()
    {
        return (resourceID == null ? "Content[" + nbOfChildren() + "]" : "XObject[" + resourceID + "]") + (reference == null ? "" : reference);
    }

    @Override
    public String toString()
    {
        return sticker() + "\nSubtype[" + this.subtype + "]\nBaseClip[" + this.baseClip + "]\nBaseSoftClip[" + this.baseSoftClip + "]\nBBox" + (bbox == null ? "[null]" : bbox)
                + "\nResourceTM" + (resourceTM == null ? "[null]" : resourceTM) + "\nBaseFillAlpha[" + this.baseFillAlpha + "]" + "\nBaseStrokeAlpha["
                + this.baseStrokeAlpha + "]" + "\nBaseBlendModes[" + Zen.Array.String(baseBlendModes) + "]" + "\n\n"
                + (resourceStream == null ? "" : resourceStream.asciiValue());
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        int counter = 0;
        for (PDFNode node : this)
        {
            if (props.nbOfPrimitives < 0 || counter++ <= props.nbOfPrimitives)
            {
                //Log.debug(this, ".paint - counter="+ (++counter)+" : "+node.toString()+"\n\n");
                node.paint(g, props);
            }
        }
    }

    // there's no back return... used when processing PDF page per page
    public void freeFromMemory()
    {
        this.parent = null;
        this.document = null;
        this.instrFontT3.clear();
        this.resources = null;
        this.states.clear();
        this.path = null;
        this.state.initialize();
        this.resourceTM = null;
        this.resourceStream = null;
        this.group = null;
        this.children.clear();
    }
}
