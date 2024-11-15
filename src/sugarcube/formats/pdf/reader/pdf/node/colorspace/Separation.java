package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.data.collections.StringList;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

import java.awt.color.ColorSpace;

public class Separation extends PDFColorSpace
{
  private StringList names = new StringList();
  private PDFColorSpace alternateCS;
  private PDFFunction tintTransform;

  //tint 0..1, subtractive system, thus, 0 is the lightest, 1 is the darkest
  public Separation(PDFNode parent, PDFArray array)
  {
    super(parent, array.get(0).stringValue(), 1);
    if (array.get(1).isPDFName())
      this.names.add(array.get(1).toPDFName().stringValue());
    else
      for (PDFObject po : array.get(1).toPDFArray())
        this.names.add(po.toPDFName().stringValue());
    this.alternateCS = PDFColorSpace.instance(this, null, array.get(2));
    this.tintTransform = PDFFunction.instance(this, array.get(3));
    this.colorSpace = new ColorSpaceSeparation();
    this.add(alternateCS);
    this.add(tintTransform);
  }

  public class ColorSpaceSeparation extends ColorSpace
  {
    public ColorSpaceSeparation()
    {
      super(ColorSpace.CS_GRAY, 1);
    }

    @Override
    public float[] toRGB(float[] in)
    {
      return alternateCS.toRGB(tintTransform.transform(in));      
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
    public int getType()
    {
      return ColorSpace.CS_GRAY;
    }

    @Override
    public int getNumComponents()
    {
      return 1;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue)
    {
      return new float[3];
    }
  }

  @Override
  public String toString()
  {
    return "ColorSpace[" + name + "]"
      + "\nColorants" + names
      + "\nAlternate[" + alternateCS.name + "]"
      + "\nTintTransform[" + tintTransform + "]";
  }
}
