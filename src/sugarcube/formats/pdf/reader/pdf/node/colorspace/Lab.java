package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

import java.awt.color.ColorSpace;

public class Lab extends PDFColorSpace
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
  private float[] range =
  {
    -100f, 100f, -100f, 100f
  };

  public Lab(PDFNode vo, PDFDictionary map)
  {
    super(vo, "Lab", 3);
    this.white = map.get("WhitePoint").toPDFArray().floatValues(white);
    this.black = map.get("BlackPoint").toPDFArray().floatValues(black);
    this.range = map.get("Range").toPDFArray().floatValues(range);
    this.colorSpace = new ColorSpaceLab();
  }

  public class ColorSpaceLab extends ColorSpace
  {
    public ColorSpaceLab()
    {
      super(1, 3);
    }

    public final float stage2(float s1)
    {
      return s1 >= 0.2068966F ? s1 * s1 * s1 : 0.1284186F * (s1 - 0.137931F);
    }

    @Override
    public float[] toRGB(float[] comp)
    {
      if (comp.length == 3)
      {
        float l = (comp[0] + 16.0F) / 116.0F + comp[1] / 500.0F;
        float m = (comp[0] + 16.0F) / 116.0F;
        float n = (comp[0] + 16.0F) / 116.0F - comp[2] / 200.0F;
        float[] xyz =
        {
          white[0] * stage2(l), white[0] * stage2(m), white[0] * stage2(n)
        };
        return cie.fromCIEXYZ(xyz);
      }
      return black;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue)
    {
      return new float[3];
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue)
    {
      return new float[3];
    }

    @Override
    public int getNumComponents()
    {
      return 3;
    }

    @Override
    public int getType()
    {
      return 1;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue)
    {
      return new float[3];
    }
  }
}
