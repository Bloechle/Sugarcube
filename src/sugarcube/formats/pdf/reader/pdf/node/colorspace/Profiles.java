package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.data.Zen;
import sugarcube.formats.pdf.resources.pdf.RS_PDF;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;

public class Profiles
{
  private static ICC_Profile YCC601_PF = null;
  private static ICC_ColorSpace YCC601_CS = null;
  private static ICC_Profile ADOBE_CMYK_PF = null;
  private static ICC_ColorSpace ADOBE_CMYK_CS = null;
  private static ColorModel SRGB_MODEL = null;
  private static ColorSpace SRGB_CS = null;

  public static ICC_Profile YCC601_PROFILE()
  {
    return YCC601_PF == null ? YCC601_PF = ICC_Profile.getInstance(RS_PDF.bytes("YCC601.pf")) : YCC601_PF;
  }
  
  public static ColorSpace YCC601_CS()
  {
    return YCC601_CS == null ? YCC601_CS = new ICC_ColorSpace(YCC601_PROFILE()) : YCC601_CS;
  }
  
  public static ICC_Profile ADOBE_CMYK_PROFILE()
  {
    return ADOBE_CMYK_PF == null ? ADOBE_CMYK_PF = ICC_Profile.getInstance(RS_PDF.bytes("cmyk.icm")) : ADOBE_CMYK_PF;
  }

  public static ColorSpace ADOBE_CMYK_CS()
  {
    return ADOBE_CMYK_CS == null ? ADOBE_CMYK_CS = new ICC_ColorSpace(ADOBE_CMYK_PROFILE()) : ADOBE_CMYK_CS;
  }

  public static ColorModel RGB_MODEL()
  {
    return SRGB_MODEL == null ? SRGB_MODEL = new ComponentColorModel(RGB_CS(), Zen.Array.Ints(8, 8, 8), false, false, ColorModel.OPAQUE, DataBuffer.TYPE_BYTE) : SRGB_MODEL;
  }

  public static ColorSpace RGB_CS()
  {
    return SRGB_CS == null ? SRGB_CS = ColorSpace.getInstance(ColorSpace.CS_sRGB) : SRGB_CS;
  }
}
