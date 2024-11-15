package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Array3;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.DeviceCS;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFColorSpace;
import sugarcube.formats.pdf.reader.pdf.node.function.FunctionIdentity;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.node.shade.PDFShading;

// graphics state
public class PDFState extends PDFNode
{
  protected PDFTextState textState;
  protected String xState = "";//debugging
  protected PDFSoftMask softClip = null;
  protected PDFMatrix ctm;
  protected PDFMatrix lastTM;
  protected PDFShading shading;
  private PDFColor fillColor;//private to avoid direct access since it is bug prone
  protected PDFColor strokeColor;
  protected PDFColorSpace fillCS;
  protected PDFColorSpace strokeCS;
  protected double fillAlpha;
  protected double strokeAlpha;
  protected String[] blendModes;
  protected PDFStroke stroke;
  protected PDFClip clip;
  protected PDFFunction[] TR;
  protected PDFFunction defaultTransferFct;
  protected PDFFunction UCR;
  protected PDFFunction BG;
//  protected PDFContent content;//back reference to parent content stream
//  protected PDFContent xObject;//back reference to potential xObject stream  

  public PDFState(PDFNode parent)
  {
    super("State", parent);
//    this.content = content;
    this.initialize();
  }

//  public void setContent(PDFContent content)
//  {
//    this.content = content;
//  }
  public final void initialize()
  {
    this.xState = "";
    this.ctm = new PDFMatrix();
    this.lastTM = new PDFMatrix();

    this.softClip = null;
    this.shading = null;
    this.fillColor = new PDFColor(this);
    this.strokeColor = new PDFColor(this);
    this.fillCS = new DeviceCS(this, "DeviceGray");
    this.strokeCS = new DeviceCS(this, "DeviceGray");
    this.fillAlpha = 1.0;
    this.strokeAlpha = 1.0;
    this.blendModes = null;

    this.textState = new PDFTextState(this);
    this.stroke = new PDFStroke(this);
    this.clip = new PDFClip(this);

    this.TR = new PDFFunction[]
    {
      new FunctionIdentity(this)
    };
    this.UCR = new FunctionIdentity(this);
    this.BG = new FunctionIdentity(this);
  }

  public boolean hasSMask()
  {
    return this.softClip != null;
  }

  public void clearShading()
  {
    this.shading = null;
  }

  public String blendMode()
  {
    return this.blendModes == null || this.blendModes.length == 0 ? null : this.blendModes[0];
  }

  public String[] blendModes()
  {
    return this.blendModes;
  }

  public PDFFunction[] TR()
  {
    return this.TR;
  }

  public PDFStroke getLineStyle()
  {
    return stroke;
  }

  public void setLineStyle(PDFStroke lineStyle)
  {
    this.stroke = lineStyle;
  }

  public PDFTextState getTextState()
  {
    return textState;
  }

  public void combineCTM(PDFMatrix matrix)
  {
    this.lastTM = matrix;
    this.ctm = matrix.concat(ctm);
  }

  public PDFMatrix ctm()
  {
    return ctm;
  }

  public PDFMatrix lastTM()
  {
    return lastTM;
  }

  public PDFStroke stroke()
  {
    return this.stroke.copy();
  }

  public double fillAlpha()
  {
    return fillAlpha;
  }

  public double strokeAlpha()
  {
    return strokeAlpha;
  }

  public PDFColor fillColor()
  {
    return fillColor.copy().setAlpha(fillAlpha);
  }

  public PDFColor strokeColor()
  {
    return strokeColor.copy().setAlpha(strokeAlpha);
  }

  public PDFColorSpace fillColorSpace()
  {
    return fillCS;
  }

  public PDFColorSpace strokeColorSpace()
  {
    return strokeCS;
  }

  public PDFColor setFillColor(PDFColor color)
  {
    this.shading = null;
    return this.fillColor = color;
  }

  public PDFColor setStrokeColor(PDFColor color)
  {
    return this.strokeColor = color;
  }

  public void setFillColorSpace(PDFColorSpace fillCS)
  {
    this.fillCS = fillCS;
  }

  public void setStrokeColorSpace(PDFColorSpace strokeCS)
  {
    this.strokeCS = strokeCS;
  }

  public void setFillColorSpace(PDFContent content, String name)
  {
    this.fillCS = content.resources().colorspace(name);
    if (fillCS == null)
      this.fillCS = PDFColorSpace.instance(this, name);
  }

  public void setStrokeColorSpace(PDFContent content, String name)
  {
    this.strokeCS = content.resources().colorspace(name);
    if (strokeCS == null)
      this.strokeCS = PDFColorSpace.instance(this, name);
  }

  public PDFClip setClip(PDFClip clip)
  {
    return this.clip = clip;
  }

  public PDFClip clip()
  {
    return clip;
  }

  public void setUnderColorRemovalFct(PDFFunction underColorRemovalFct)
  {
    this.UCR = underColorRemovalFct;
  }

  public PDFFunction getUnderColorRemovalFct()
  {
    return UCR;
  }

  public void setBlackGenerationFct(PDFFunction blackGenerationFct)
  {
    this.BG = blackGenerationFct;
  }

  public PDFFunction getBlackGenerationFct()
  {
    return BG;
  }

  public void updateExtGState(PDFContent content, String name)
  {
    PDFExtGState gState = content.resources().getExtGState(name);
//    Log.debug(this, ".updateExtGState - " + name + ": " + (gState==null ? "null" : gState.reference));  
    if (gState != null)
      gState.updateGraphicState(this);
    else
      Log.debug(this, ".updateExtGState - xState not found: " + name);
  }

  public void update(PDFState previousState)
  {
    this.xState = previousState.xState;
//    this.content = previousState.content;
//    this.xObject = previousState.xObject;

    this.ctm = previousState.ctm;
    this.lastTM = previousState.lastTM;

    this.softClip = previousState.softClip;
    this.shading = previousState.shading;
    this.fillColor = previousState.fillColor;
    this.fillCS = previousState.fillCS;
    this.strokeColor = previousState.strokeColor;
    this.strokeCS = previousState.strokeCS;
    this.fillAlpha = previousState.fillAlpha;
    this.strokeAlpha = previousState.strokeAlpha;
    this.blendModes = previousState.blendModes;

    this.textState = previousState.textState;
    this.stroke = previousState.stroke;
    this.clip = previousState.clip;

    this.TR = previousState.TR;
    this.defaultTransferFct = previousState.defaultTransferFct;
    this.UCR = previousState.UCR;
    this.BG = previousState.BG;
  }

  public PDFState copy()
  {
    PDFState state = new PDFState(parent);

    state.xState = this.xState;
//    state.xObject = this.xObject;

    state.ctm = this.ctm.copy();
    state.lastTM = this.lastTM.copy();

    state.softClip = this.softClip;
    state.shading = this.shading;
    state.fillColor = this.fillColor.copy();
    state.fillCS = this.fillCS;
    state.strokeColor = this.strokeColor.copy();
    state.strokeCS = this.strokeCS;
    state.fillAlpha = this.fillAlpha;
    state.strokeAlpha = this.strokeAlpha;
    state.blendModes = Array3.copy(this.blendModes);

    state.textState = this.textState.copy(); //LEAVE COPY since textState is updated externally
    state.stroke = this.stroke.copy(); //.copy();
    state.clip = this.clip.copy(); //because used in swing tree panel and needs different hashcodes !

    state.TR = this.TR;
    state.defaultTransferFct = this.defaultTransferFct;
    state.UCR = this.UCR;
    state.BG = this.BG;

    return state;
  }

  @Override
  public String sticker()
  {
    return "State";
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if (this.TR != null)
      for (PDFFunction fct : this.TR)
        sb.append(fct.toString()).append(", ");

    return "State[" + clip + "]"
      + "\nCTM" + ctm
      + "\nTR[" + sb.toString() + "]";
  }
}
