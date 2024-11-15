package sugarcube.formats.pdf.writer;

import sugarcube.common.data.collections.Props;

@SuppressWarnings("serial")
public class PDFParams extends Props
{
  // keys
  public static final String TRANSPARENT = "transparent";
  public static final String BINARIZE = "binarize";
  public static final String JPX = "jpx";
  public static final String DECIMATE = "decimate";
  public static final String GRAPHICS = "graphics";
  public static final String COLORSPACE = "colorspace";
  public static final String SAMPLING = "sampling";
  public static final String CCITT_DPI = "ccitt-dpi";

  // values
  public static final String VALUE_RGB_COLORSPACE = "rgb";
  public static final String VALUE_CMYK_COLORSPACE = "cmyk";

  public PDFParams()
  {

  }

  @Override
  public PDFParams set(String key, Object value)
  {
    this.put(key, value);
    return this;
  }

  public void setTransparent(boolean transparent)
  {
    this.put(TRANSPARENT, transparent);
  }

  public boolean isTransparent()
  {
    return this.bool(TRANSPARENT, false);
  }

  public int binarize(int def)
  {
    return this.integer(BINARIZE, def);
  }

  public double sampling()
  {
    return sampling(1.0);
  }

  public double sampling(double def)
  {
    return this.real(SAMPLING, def);
  }

  public void setSampling(double value)
  {
    this.set(SAMPLING, value);
  }

  public boolean jpx()
  {
    return this.bool(JPX, false);
  }

  public float decimate()
  {
    return this.real(DECIMATE, -1);
  }

  public void setColorSpace(String colorSpace, int iccProfile)
  {
    this.put(COLORSPACE, colorSpace);
	PDFColorSpaceManager.setICCColorSpace(iccProfile);
  }

  public String getColorSpace()
  {
    return this.string(COLORSPACE, VALUE_RGB_COLORSPACE);
  }

}
