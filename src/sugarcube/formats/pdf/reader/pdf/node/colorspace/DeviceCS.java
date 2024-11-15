package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

import java.awt.color.ColorSpace;

public class DeviceCS extends PDFColorSpace
{

  // DeviceGray, DeviceRGB, DeviceCMYK
  public DeviceCS(PDFNode parent, int nbOfComponents)
  {
    super(parent, nbOfComponents == 1 ? "DeviceGray" : nbOfComponents == 4 ? "DeviceCMYK" : "DeviceRGB", nbOfComponents);
    this.colorSpace = createColorSpace();
  }

  public DeviceCS(PDFNode parent, String space)
  {
    super(parent, space);
    this.colorSpace = createColorSpace();
  }

  @Override
  public boolean isCMYK()
  {
    return this.nbOfComponents == 4;
  }

  private ColorSpace createColorSpace()
  {
    if (this.nbOfComponents == 1)
      return new GrayColorSpace(); //new ColorSpace.getInstance(ColorSpace.CS_GRAY);
    else if (this.nbOfComponents == 4)
      return CMYKColorSpace.GENERIC;
    else if (this.nbOfComponents == 3)
      return ColorSpace.getInstance(ColorSpace.CS_sRGB);

    Log.warn(this, ".createColorSpace - wrong number of components in " + name + ": " + nbOfComponents);
    return ColorSpace.getInstance(ColorSpace.CS_sRGB);
  }

  @Override
  public boolean isStandardCS()
  {
    return this.nbOfComponents != 4;
  }

  @Override
  public String toString()
  {
    return "DeviceCS[" + nbOfComponents + "]";
  }

  public static class GrayColorSpace extends ColorSpace
  {

    private GrayColorSpace()
    {
      super(ColorSpace.TYPE_GRAY, 1);
    }

    @Override
    public float[] toRGB(float[] k)
    {
      return CMYKColorSpace.GENERIC.toRGB(Zen.Array.Floats(0f, 0f, 0f, 1 - k[0])); //seems that gray level reacts as K value in DeviceCMYK (1-K)
    }

    @Override
    public float[] fromRGB(float[] rgb)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue)
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
