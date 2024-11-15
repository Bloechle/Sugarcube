package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

import java.awt.color.ColorSpace;

public class CalGray extends PDFColorSpace
{
  private static ColorSpace cie = ColorSpace.getInstance(ColorSpace.CS_sRGB);
  private float[] white =
  {
    1f, 1f, 1f
  };
  private float[] black =
  {
    0f, 0f, 0f
  };
  private float gamma = 1f;

  public CalGray(PDFNode vo, PDFDictionary map)
  {
    super(vo, "CalGray", 1);
    this.white = map.get("WhitePoint").toPDFArray().floatValues(white);
    this.black = map.get("BlackPoint").toPDFArray().floatValues(black);
    this.gamma = map.get("Gamma").toPDFNumber().floatValue(gamma);
    this.colorSpace = new ColorSpaceCalGray();
  }

  public class ColorSpaceCalGray extends ColorSpace
  {
    public ColorSpaceCalGray()
    {
      super(6, 1);
    }

    @Override
    public float[] toRGB(float[] comp)
    {
      if (comp.length == 1)
      {
        float mul = (float) Math.pow(comp[0], gamma);
        float[] xyz =
        {
          white[0] * mul, 0.0F, 0.0F
        };

        float[] rgb = cie.fromCIEXYZ(xyz);
        return rgb;
      }
      return black;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue)
    {
      return new float[1];
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue)
    {
      return new float[1];
    }

    @Override
    public int getNumComponents()
    {
      return 1;
    }

    @Override
    public int getType()
    {
      return 6;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue)
    {
      return new float[3];
    }
  }
}
