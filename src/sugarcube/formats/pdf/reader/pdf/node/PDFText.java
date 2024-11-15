package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFont;
import sugarcube.formats.pdf.reader.pdf.object.PDFString;
import sugarcube.formats.pdf.reader.pdf.object.StreamLocator;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class PDFText extends PDFPaintable
{
  protected StreamLocator streamLocator;  
  protected PDFClip clip;
  protected Unicodes codes; // these are char codes actually
  protected Coords coords; // contains last glyph width
  protected PDFFont font;
  protected String fontname;
  protected float fontsize;
  protected PDFColor fillColor;
  protected PDFColor strokeColor;
  protected String[] blendModes = null;
  protected PDFStroke stroke;
  protected boolean isVertical = false;
  protected boolean isTransparent = false;
  private PDFContent content;  
  public double adj;

  private PDFText(PDFContent content)
  {
    super(Dexter.TEXT, content);
  }

  protected PDFText(PDFContent content, PDFString string, double adjustment, PDFInstr instr)
  {
    super(Dexter.TEXT, content);
    this.adj = adjustment;
    PDFState state = content.state();
    PDFTextState textState = state.textState;
    this.streamLocator = string.streamLocator();
    this.content = content;
    this.font = textState.font();
    this.fontname = textState.fontname;
    if (this.font != null)
    {
      this.codes = this.font.fontCodes(string);
      this.isVertical = this.font.isVerticalMode();
    } else
    {
      this.codes = new Unicodes(string.charValues());
      Log.warn(this, " - null font: " + fontname + ", codes=" + string.stringValue());
    }

    this.fillColor = textState.isFilled() ? state.fillColor() : new PDFColor(this, true);
    this.strokeColor = textState.isStroked() ? state.strokeColor() : new PDFColor(this, true);
    if (content.baseFillAlpha < 1)
      this.fillColor.composeAlpha(content.baseFillAlpha);
    if (content.baseStrokeAlpha < 1)
      this.strokeColor.composeAlpha(content.baseStrokeAlpha);
    this.stroke = state.stroke();

    // if (this.codes.are("Z"))
    // Log.debug(this, " - stroke width = " + stroke.getLineWidth());
    this.clip = state.clip();
    this.blendModes = content.blendModes(state.blendModes);
    this.coords = textState.showText(this, adjustment);
    // in reverse order... i.e., closest nesting first
    this.marks.setAll(content.marks());

    //Log.debug(this, " - "+this.stringValue());
  }

  public PDFText mergeRun(PDFText run)
  {
    this.codes.append(run.codes);
    if (coords.size() > 0)
      this.coords.removeLast();
    this.coords.add(run.coords);
    return this;
  }

  public PDFText transparentize()
  {
    this.isTransparent = true;
    this.fillColor = new PDFColor(this, true);
    this.strokeColor = new PDFColor(this, true);
    this.stroke = new PDFStroke(this, 0);
    return this;
  }

  @Override
  public PDFText instance(PDFContent content, PDFInstr instr, PDFContext context)
  {
    PDFState currentState = document().content().state();
    PDFText text = copy();
    text.tm = currentState.ctm;
    text.content = content;
    text.streamLocator = instr.streamLocator();
    return text;
  }

  public boolean isVerticalMode()
  {
    return this.isVertical;
  }

  @Override
  public String blendMode()
  {
    return blendModes == null || blendModes.length == 0 ? null : blendModes[0];
  }

  public PDFText copy()
  {
    PDFText text = new PDFText(this.content);
    text.codes = this.codes;
    text.streamLocator = this.streamLocator;
    text.tm = this.tm.copy();
    text.clip = this.clip.copy();
    text.coords = this.coords.copy();
    text.font = this.font;
    text.fillColor = this.fillColor.copy();
    text.strokeColor = this.strokeColor.copy();
    text.isVertical = this.isVertical;
    text.content = this.content;
    return text;
  }

  @Override
  public StreamLocator streamLocator()
  {
    return streamLocator;
  }

  public Coords deviceCoords(Rectangle2D bounds)
  {
    return deviceCoords(bounds.getMinX(), bounds.getMaxY());
  }

  public Coords deviceCoords(double minX, double maxY)
  {
    return new Transform3(1, 0, 0, -1, -minX, maxY).transform(coords);
  }

  public PDFClip clip()
  {
    return clip;
  }

  public PDFFont getFont()
  {
    return font;
  }

  public Unicodes codes()
  {
    return codes;
  }

  public Coords coords()
  {
    return coords;
  }

  @Override
  public boolean isValid()
  {
    return this.codes.length() > 0;
  }

  public float fontSize()
  {
    return this.fontsize;
  }

  public boolean hasFillColorPattern()
  {
    return this.fillColor != null && this.fillColor.isPattern();
  }

  @Override
  public PDFColor getFillColor()
  {
    return fillColor;
  }

  public Color3 strokeColor()
  {
    return strokeColor.color();
  }

  public PDFColor getStrokeColor()
  {
    return strokeColor;
  }

  public PDFStroke getStroke()
  {
    return stroke;
  }

  public Path3 toClippingPath()
  {
    Area area = new Area();
    for (int i = 0; i < codes.length(); i++)
      area.add(new Area(tm.newPosition(coords.get(i)).transform(font.render(codes.codeAt(i), fontsize).path().reverseY())));
    return new Path3(area);
  }

  public String stringValue()
  {
    return codes.stringValue();
  }

  @Override
  public String sticker()
  {
    int mark = mcid();
    return (mark < 0 ? "" : mark + "_") + id + " » " + (font == null ? "null font - "+fontname : font.encoding().unicodesFromCodes(codes.ints()));
  }

  @Override
  public String toString()
  {
    return type + (streamLocator() == null ? "" : streamLocator()) + "\nUnicodes["
        + (font == null ? codes.toString() : font.encoding().unicodesFromCodes(codes.ints())) + "]" + "\nFontCodes[" + codes.toIntegerString() + "]"
        + "\nFontID[" + (font == null ? "null" : font.fontID()) + "]" + "\nFontName[" + fontname + "]"
        + "\nBaseFont[" + (font == null ? "null" : font.basefont()) + "]" + "\nFontSize[" + fontsize + "]" + "\nClip[" + clip + "]" + "\nBlendMode["
        + this.blendMode() + "]" + "\nFillColor[" + Zen.Array.String(fillColor.rgbaValues()) + "]" + "\nStrokeColor["
        + Zen.Array.String(strokeColor.rgbaValues()) + "]" + "\nStrokeWidth[" + stroke.getLineWidth() + "]" + "\nMatrix" + tm + "\nCoords" + coords
        + "\nVertical[" + isVertical + "]" + "\nMarks" + this.marks;
  }

  @Override
  public Shape shape(double minX, double maxY)
  {
    Coords c = deviceCoords(minX, maxY);
    PDFMatrix textMatrix = tm.reverse();
    Path3 path = new Path3();
    for (int i = 0; i < codes.length(); i++)
    {
      Transform3 ptm = textMatrix.newPosition(c.get(i)).transform();
      path.append(ptm.transform(font.render(codes.codeAt(i), fontsize).path()), false);
    }
    return path;
  }

  @Override
  public void paint(Graphics3 g, PDFDisplayProps props)
  {
    if (props.displayText)
      try
      {
        g.setComposite(1, blendModes);
        // g.resetComposite();
        Coords c = deviceCoords(props.pageBounds);
        Transform3 t3 = tm.transform();
        g.setStroke(this.stroke.stroke3(1 / Math.abs(t3.scaleWidth())));

        Transform3 otm = g.transform();
        PDFMatrix textMatrix = tm.reverse();

        if (!fillColor.isTransparent())
        {
          if (this.hasFillColorPattern())
            g.setPaint(props.enableColors ? fillColor.colorSpace().toPattern().paint(props) : Color3.BLACK);
          else
            g.setColor(props.enableColors ? fillColor.color() : Color3.BLACK);

          // Log.debug(this, ".paint - " + textMatrix);
          for (int i = 0; i < codes.length(); i++)
          {

            g.setTransform(otm.concat(textMatrix.newPosition(c.get(i)).transform()));
//            if (font == null)
//              g.graphics().fill(Font3.CALIBRI_FONT.derive(fontsize).glyph(codes.codeAt(i)));
            if(font!=null)
              g.graphics().fill(font.render(codes.codeAt(i), fontsize).path());
          }
        }
        if (!strokeColor.isTransparent() && font != null)
        {
          g.setColor(props.enableColors ? strokeColor.color() : Color3.BLACK);
          for (int i = 0; i < codes.length(); i++)
          {
            g.setTransform(otm.concat(textMatrix.newPosition(c.get(i)).transform()));
            g.graphics().draw(font.render(codes.codeAt(i), fontsize).path());
          }
        }

        if (props.highlightTexts && font != null)
        {
          g.setClip(null);
          g.setColor(new Color(0, 150, 0, 150));
          for (int i = 0; i < codes.length(); i++)
          {
            g.setTransform(otm.concat(textMatrix.newPosition(c.get(i)).transform()));
            g.graphics().fill(font.render(codes.codeAt(i), fontsize).path());
          }
        }

        g.setTransform(otm);
      } catch (Exception e)
      {
        Log.warn(this, ".paint - exception thrown: " + e);
        e.printStackTrace();
      }
  }
}
