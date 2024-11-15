package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFont;
import sugarcube.formats.pdf.reader.pdf.node.function.FunctionIdentity;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary.PDFEntry;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PDFExtGState extends PDFNode
{
  public static enum OP
  {
    AIS("AIS"), BG("BG"), BG2("BG2"), BM("BM"), CA("CA"), D("D"), FL("FL"), Font("Font"), HT("HT"), LC("LC"), LJ("LJ"), LW("LW"), ML("ML"), Name(
        "Name"), // Name is not in PDF Reference 1.7... but used !!!
    OP("OP"), OPM("OPM"), RI("RI"), SA("SA"), SM("SM"), SMask("SMask"), TK("TK"), TR("TR"), TR2("TR2"), Type("Type"), UCR("UCR"), UCR2("UCR2"), ca(
        "ca"), op("op"), AAPL_AA("AAPL:AA");// AAPL_AA used by Apple but not
                                            // useful
    private static final Map<String, OP> OPS = new LinkedHashMap<String, OP>();

    static
    {
      for (OP op : OP.values())
        OPS.put(op.name, op);
    }
    protected String name;

    private OP(String name)
    {
      this.name = name;
    }

    public static boolean contains(String name)
    {
      return OPS.containsKey(name);
    }

    public static OP getOP(String name)
    {
      return OPS.get(name);
    }
  }

  private static final StringSet UNKNOWN_OP = new StringSet();
  private Set3<OP> ops = new Set3<OP>();
  private PDFResources resources;
  private PDFStroke lineStroke;
  private boolean AIS = true;
  private double fillAlpha = 1;
  private double strokeAlpha = 0;
  private PDFFunction[] TR = null;
  private PDFFunction UCR = null;
  private PDFFunction BG = null;
  private PDFFont font = null;
  private float fontSize = 1;
  private String resourceID;
  private String name;
  private PDFSoftMask softClip = null;// smask, but better to name it softClip
                                   // since it avoids misunderstanding with soft
                                   // image smask...
  private String[] blendModes = new String[0];

  public PDFExtGState(PDFNode parent, String resourceID, PDFDictionary dico)
  {
    super("ExtGState", parent);
    this.resources = parent instanceof PDFResources ? (PDFResources) parent : null;
    this.resourceID = resourceID;
    this.lineStroke = new PDFStroke(this);
    this.reference = dico.reference();

//    Log.debug(this,  " - key="+resourceID+", value="+dico);
    List3<PDFEntry> entries = dico.entries();
    for (PDFEntry e : entries)
    {
      OP op = OP.getOP(e.getKey());
      if (op != null)
      {
        ops.add(op);
        PDFObject obj = e.getValue();
        switch (op)
        {
        case AIS:
          this.AIS = obj.booleanValue();
          break;
        case BG:
          if (!dico.contains("BG2"))
            this.BG = parseFunction(obj);
          break;
        case BG2:
          this.BG = parseFunction(obj);
          break;
        case BM:
          if (obj.isPDFName())
            this.blendModes = Zen.Array.strings(obj.stringValue());
          else if (obj.isPDFArray())
            this.blendModes = obj.toPDFArray().stringValues();
          break;// Blending mode, to be implemented (Graphics2D.composite())
        case CA:
          this.strokeAlpha = obj.doubleValue();
          break;
        case ca:
          this.fillAlpha = obj.doubleValue();
          break;
        case D:
          this.lineStroke.setDashValues(obj.get(0).doubleValues(), obj.get(1).doubleValue());
          break;
        case FL:// obsolete - flatness tolerance used for rendering performance
          break;
        case HT:// obsolete - halftoning which was used with low res devices
                // (now handled by printers themselves)
          break;
        case Font:
          Log.debug(this,  " - Font... check get(0) or get(1)");
          PDFFont newFont = PDFFont.Instance(this, obj.get(0).toString(), obj.get(0));
          if (newFont != null)
            this.font = newFont;
          this.fontSize = obj.get(1).floatValue();
          break;
        case LC:
          this.lineStroke.setLineCap(obj.intValue());
          break;
        case LJ:
          this.lineStroke.setLineJoin(obj.intValue());
          break;
        case LW:
          this.lineStroke.setLineWidth(obj.doubleValue());
          break;
        case ML:
          this.lineStroke.setMiterLimit(obj.intValue());
          break;
        case Name:
          this.name = obj.stringValue();
          break;
        case OP:// General Overprint enabled
          break;
        case op:// Non stroking overprint enabled
          break;
        case OPM:// Overprint Control
          break;
        case RI:// rendering intents, not really obsolete but not really useful
          break;
        case SA:// obsolete - automatic stroke adjustment used with low
                // resolution devices
          break;
        case SM:// obsolete - smoothness tolerance used for rendering
                // performance
          break;
        case SMask:
          parseSMask(obj);
          break;
        case TK:// text knockout flag, behaviour of overlapping glyphs
                // (transparent model)
          break;
        case TR:
          if (!dico.contains("TR2"))
            this.TR = parseFunctions(obj);
          break;
        case TR2:
          this.TR = parseFunctions(obj);
          break;
        case Type:
          break;
        case UCR:
          if (!dico.contains("UCR2"))
            this.UCR = parseFunction(obj);
          break;
        case UCR2:
          this.UCR = parseFunction(obj);
          break;
        case AAPL_AA:// we do not bother since this is a true/false hint used
                     // only by Apple
          break;
        default:
          Log.info(this, " - listed operator: " + e.getKey());
        }
      } else
      {
        if(UNKNOWN_OP.notYet(e.getKey()))
          Log.info(this, " - new operator: " + e.getKey());
      }
    }
  }  

  private void parseSMask(PDFObject po)
  {
    po = po.unreference();
    if (po.isPDFDictionary())
    {
      this.softClip = new PDFSoftMask(this, po.toPDFDictionary());
      this.add(softClip);
    }
  }

  private PDFFunction parseFunction(PDFObject po)
  {
    return po.isPDFDictionary() ? PDFFunction.instance(this, po) : null;
  }

  private PDFFunction[] parseFunctions(PDFObject po)
  {
    List<PDFFunction> list = new LinkedList<PDFFunction>();
    if (po.isPDFDictionary())
      list.add(PDFFunction.instance(this, po));
    else if (po.isPDFArray())
    {
      list.add(PDFFunction.instance(this, po.get(0)));
      list.add(PDFFunction.instance(this, po.get(1)));
      list.add(PDFFunction.instance(this, po.get(2)));
      list.add(PDFFunction.instance(this, po.get(3)));
    } else if (po.toPDFName().stringValue().equals("Identity"))
      list.add(new FunctionIdentity(this));
    return list.isEmpty() ? null : list.toArray(new PDFFunction[0]);
  }

  protected void updateGraphicState(PDFState state)
  {
    state.xState = this.name;
    for (OP op : ops)
      switch (op)
      {
      case BG:
      case BG2:
        state.BG = BG == null && resources != null ? resources.defaultBG : BG;
        if (resources != null && resources.defaultBG == null)
          resources.defaultBG = BG;
        break;
      case BM:
        state.blendModes = blendModes;
        break;
      case CA:
        state.strokeAlpha = strokeAlpha;
        break;
      case ca:
        state.fillAlpha = fillAlpha;
        break;
      case D:
        state.stroke.setDashValues(lineStroke.getDashArray(), lineStroke.getDashOffset());
        break;
      case Font:
        state.textState.font = this.font;
        state.textState.fontname = this.font == null ? null : this.font.fontname();
        state.textState.fontSize = this.fontSize;
        break;
      case LC:
        state.stroke.setLineCap(lineStroke.getLineCap());
        break;
      case LJ:
        state.stroke.setLineJoin(lineStroke.getLineJoin());
        break;
      case LW:
        state.stroke.setLineWidth(lineStroke.getLineWidth());
        break;
      case ML:
        state.stroke.setMiterLimit(lineStroke.getMiterLimit());
        break;
      case Name:
        break;
      case SMask:
        state.softClip = softClip;
        break;
      case TR:
      case TR2:
        state.TR = TR == null && resources != null ? resources.defaultTR : TR;
        if (resources != null && resources.defaultTR == null)
          resources.defaultTR = TR;
        break;
      case Type:
        break;
      case UCR:
      case UCR2:
        state.UCR = UCR == null && resources != null ? resources.defaultUCR : UCR;
        if (resources != null && resources.defaultUCR == null)
          resources.defaultUCR = UCR;
        break;
      }
  }
  
  public double fillAlpha()
  {
    return this.fillAlpha;
  }
  
  public double strokeAlpha()
  {
    return this.strokeAlpha;
  }
  
  public String[] blendModes()
  {
    return this.blendModes;
  }

  private String opString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (OP op : ops)
      sb.append(op.name()).append(",");
    if (!ops.isEmpty())
      sb.deleteCharAt(sb.length() - 1);
    sb.append("]");
    return sb.toString();
  }

  @Override
  public String sticker()
  {
    return resourceID + " » " + "ExtGState" + reference();
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(sticker());
    sb.append("\nOperators").append(opString());
    if (ops.contains(OP.CA))
      sb.append("\nStrokeAlpha [CA]: ").append(this.strokeAlpha);
    if (ops.contains(OP.ca))
      sb.append("\nFillAlpha [ca]: ").append(this.fillAlpha);
    if (ops.contains(OP.Font))
    {
      sb.append("\nFont [Font]: ").append(this.font);
      sb.append("\nFontSize [Font]: ").append(this.fontSize);
    }
    if (ops.containsOne(OP.D, OP.LC, OP.LJ, OP.LW, OP.ML))
      sb.append("\nLineStroke [D,LC,LJ,LW,ML]: ").append(this.lineStroke);
    if (ops.contains(OP.SMask))
      sb.append("\nSMask [SMask]: ").append(softClip);
    if (ops.contains(OP.BM))
      sb.append("\nBlend Mode [BM]: ").append(Zen.Array.String(blendModes));
    if (ops.contains(OP.AIS))
      sb.append("\nAlpha is Shape [AIS]: ").append(this.AIS);
    return sb.toString();
  }
}
