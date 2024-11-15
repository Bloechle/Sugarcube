package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.ui.gui.Font3;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFont;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFGlyph;

public class PDFTextState extends PDFNode
{
  public static final int HORIZONTAL = 0;// never change these values
  public static final int VERTICAL = 1;
  protected float charSpace;
  protected float wordSpace;
  protected float hScale;// horizontal scale
  protected float leading;
  protected float fontSize;
  protected float textRise;
  protected boolean fill = true;
  protected boolean stroke = false;
  protected int renderingMode = 0;
  protected PDFMatrix tm; // text matrix
  protected PDFMatrix lm; // textline matrix
  protected int writingMode = 0;
  protected PDFFont font;
  protected String fontname;
  protected PDFState state;

  public PDFTextState(PDFState state)
  {
    super("TextState", state);
    this.state = state;
    this.charSpace = 0;
    this.wordSpace = 0;
    this.hScale = 1;
    this.leading = 0;
    this.fontSize = 1;
    this.textRise = 0;
    this.tm = new PDFMatrix();
    this.lm = new PDFMatrix();
    this.writingMode = HORIZONTAL;
  }

  public void resetMatrices()
  {
    this.tm = new PDFMatrix();
    this.lm = new PDFMatrix();
  }

  public void setMatrices(PDFMatrix matrix)
  {
    this.tm = matrix.copy();
    this.lm = matrix.copy();
  }

  public void setFont(String name)
  {
    this.fontname = name;
    this.font = document().content().resources().getFont(name);
    this.writingMode = font != null && font.isVerticalMode() ? VERTICAL : HORIZONTAL;
  }

  public void setRenderingMode(int type)
  {
    this.fill = type == 0 || type == 2 || type == 4 || type == 6;
    this.stroke = type == 1 || type == 2 || type == 5 || type == 6;
    this.renderingMode = type;
  }

  public int renderingMode()
  {
    return this.renderingMode;
  }

  public void nextLine(double tx, double ty)
  {
    double x = lm.get(0) * tx + lm.get(2) * ty + lm.get(4);
    double y = lm.get(1) * tx + lm.get(3) * ty + lm.get(5); // TODO: - lm.get(3)
                                                            // with wikipedia?
    this.lm.setPosition(x, y);
    this.tm.setPosition(x, y);
  }

  public Coords showText(PDFText text, double adjustment)
  {
    // Th => scale
    // Tfs => fontSize

    PDFMatrix device = new PDFMatrix(fontSize * hScale, 0, 0, fontSize, 0, textRise); // because
                                                                                      // horizontal
                                                                                      // scale!!!
    PDFMatrix origin = device.concat(tm.concat(state.ctm));

    text.fontsize = fontSize;
    text.tm = new PDFMatrix(hScale, 0, 0, 1, 0, 0).concat(tm.concat(state.ctm));

    Unicodes codes = text.codes;
    Coords coords = new Coords();
    int loop = 0;

    boolean vMode = writingMode == VERTICAL;
    float scale = vMode ? 1 : hScale;

    if (codes.isEmpty())// 2012.05.30 - empty code used to adjust positioning
                        // when TJ begins with number !
    {
      double displacement = -adjustment * fontSize * scale; // adjustment
                                                            // specified in TJ
                                                            // array
      this.tm.translateIt(displacement * tm.get(writingMode * 2), displacement * tm.get(writingMode * 2 + 1));
    } else
      for (int i = 0; i < codes.length(); i++)
      {
        coords.add(origin.getPosition());
        int code = codes.codeAt(i);
        double advance = 0;// width or height

        // 2014.01.31 - poor pdf may reference inexisting fonts in their
        // resource XObject !
        if (font == null)
          advance = Font3.CALIBRI_FONT.derive(1).width(code);
        else if (font != null)
        {
          font.encoding().showedCodes.add(code);
          PDFGlyph glyph = font.glyph(code);
          advance = vMode ? glyph.height() : glyph.width();
          // Log.debug(this, ".showText - " + text.stringValue() + ": " + (char)
          // code + ", advance=" + advance + " vMode=" + vMode + ", scale=" +
          // scale);
        }

        // 2011.11.09 - always keep charSpace and add wordSpace when char code
        // is 32 !!!!!! Seems to be OK !!!!!
        double displacement = (advance * fontSize + (code == 32 ? wordSpace : 0) + charSpace) * scale;
        if (++loop == codes.length())
        {
          coords.add(device.concat(tm.translate(displacement * tm.get(writingMode * 2), displacement * tm.get(writingMode * 2 + 1)).concat(state.ctm))
              .getPosition());
          displacement -= adjustment * fontSize * scale; // adjustment specified
                                                         // in TJ array
        }
        this.tm.translateIt(displacement * tm.get(writingMode * 2), displacement * tm.get(writingMode * 2 + 1));
        origin = device.concat(tm.concat(state.ctm));
      }

    return coords;
  }

  public double getScale()
  {
    return hScale;
  }

  public void setCharSpace(float charSpace)
  {
    this.charSpace = charSpace;
  }

  public void setWordSpace(float wordSpace)
  {
    this.wordSpace = wordSpace;
  }

  public void setScale(float scale)
  {
    this.hScale = scale;
  }

  public void setLeading(float leading)
  {
    this.leading = leading;
  }

  public void setFontSize(float fontSize)
  {
    this.fontSize = fontSize;
  }

  public void setTextRise(float textRise)
  {
    this.textRise = textRise;
  }

  public double getFontSize()
  {
    return fontSize;
  }

  public double getLeading()
  {
    return leading;
  }

  public PDFFont font()
  {
    return font;
  }

  public boolean isFilled()
  {
    return fill;
  }

  public boolean isStroked()
  {
    return stroke;
  }

  public PDFTextState copy()
  {
    PDFTextState textState = new PDFTextState(state);
    textState.charSpace = this.charSpace;
    textState.wordSpace = this.wordSpace;
    textState.hScale = this.hScale;
    textState.leading = this.leading;
    textState.fontSize = this.fontSize;
    textState.textRise = this.textRise;
    textState.fill = this.fill;
    textState.stroke = this.stroke;
    textState.renderingMode = this.renderingMode;
    textState.tm = this.tm.copy();
    textState.lm = this.lm.copy();
    textState.writingMode = this.writingMode;
    textState.font = this.font;
    textState.fontname = this.fontname;
    return textState;
  }

  @Override
  public String sticker()
  {
    return "TextState";
  }

  @Override
  public String toString()
  {
    return "TextState" + "\nFontname[" + fontname + "]" + "\nCharSpace[" + charSpace + "]" + "\nWordSpace[" + wordSpace + "]" + "\nScale[" + hScale
        + "]" + "\nLeading[" + leading + "]" + "\nFontSize[" + fontSize + "]" + "\nTextRise[" + textRise + "]" + "\nFill[" + fill + "]" + "\nStroke["
        + stroke + "]" + "\nRenderingMode[" + renderingMode + "]" + "\nTextMatrix" + tm + "\nLineMatrix" + lm;
  }
}
