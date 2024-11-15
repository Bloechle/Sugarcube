package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

import java.awt.color.ColorSpace;

public class Pattern extends PDFColorSpace
{
  public Pattern(PDFNode node)
  {
    super(node, "Pattern", 1);
    this.colorSpace = new PatternColorSpace();
  }

  public static class PatternColorSpace extends ColorSpace
  {
    public PatternColorSpace()
    {
      super(TYPE_CMYK, 4);
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue)
    {
      return null;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue)
    {
      return null;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue)
    {
      return null;
    }

    @Override
    public final float[] toRGB(float[] cmyk)
    {
      return new float[]
        {
          1, 1, 1
        };
    }
  }
}