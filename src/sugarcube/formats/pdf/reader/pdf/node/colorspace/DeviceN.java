package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.List3;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

public class DeviceN extends PDFColorSpace
{
  private static String[] CMYK_COLORANTS =
  {
    "cyan", "magenta", "yellow", "black"
  };
  private String[] type = null;
  private String[] colorants = null;
  private PDFColorSpace alternateCS;
  private PDFFunction tintTransform;

  public DeviceN(PDFNode parent, PDFArray array)
  {
    super(parent, array.get(0).stringValue(), -1);
    List3<String> colorantList = new List3<String>();
    if (array.get(1).isPDFName())
      colorantList.add(array.get(1).toPDFName().stringValue().toLowerCase());
    else
      for (PDFObject po : array.get(1).toPDFArray())
        colorantList.add(po.toPDFName().stringValue().toLowerCase());
    this.alternateCS = PDFColorSpace.instance(this, null, array.get(2));
    this.tintTransform = PDFFunction.instance(this, array.get(3));
    this.nbOfComponents = colorantList.size();

    this.colorants = colorantList.toArray(new String[0]);

    if (Zen.Array.containsAll(CMYK_COLORANTS, this.colorants))
      this.type = CMYK_COLORANTS;

    this.add(alternateCS);
    this.add(tintTransform);

    //XED.LOG.debug(this, " CMYK_ARRAY=" + Zen.Array.toString(this.type));
    //this.colorSpace = this.alternateCS.colorSpace;  

  }

  private float[] toDeviceCMYK(float... c)
  {
    float[] cmyk = new float[4];
    for (int i = 0; i < c.length; i++)
      if (colorants[i].equals(CMYK_COLORANTS[0]))
        cmyk[0] = c[i];
      else if (colorants[i].equals(CMYK_COLORANTS[1]))
        cmyk[1] = c[i];
      else if (colorants[i].equals(CMYK_COLORANTS[2]))
        cmyk[2] = c[i];
      else if (colorants[i].equals(CMYK_COLORANTS[3]))
        cmyk[3] = c[i];

//    Log.debug(this, ".toDeviceCMYK - " + Zen.A.toString(colorants) + ": " + Zen.A.toString(c) + ", cmyk=" + Zen.A.toString(cmyk));
    return CMYKColorSpace.GENERIC.toRGB(cmyk);

  }

  @Override
  public float[] toMappedRGB(float... c)
  {
    if (type == CMYK_COLORANTS)
      return this.toDeviceCMYK(c);
    try
    {
      if (alternateCS != null)
      {
        float[] tm = this.tintTransform.transform(c);
        float[] rgb = alternateCS.toRGB(tm);
//        Zen.debug(this, ".toMappedRGB - inputs=" + Zen.Array.toString(c) + " transform=" + Zen.Array.toString(tm) + " outputs=" + Zen.Array.toString(rgb));
        return rgb;
      }
    }
    catch (Exception e)
    {
      Log.warn(this, ".toMappedRGB - conversion problem: name=" + name + " input=" + Zen.Array.String(c) + " colorspace=" + colorSpace.getType());
    }
    float[] rgb = new float[3];
    for (int i = 0; i < rgb.length; i++)
      rgb[i] = c[i < c.length ? i : 0];
    return rgb;
  }

  @Override
  public String toString()
  {
    return "ColorSpace[DeviceN]" + this.reference()
      + "\nColorants[" + Zen.Array.String(colorants) + "]"
      + "\nTintTransform" + this.tintTransform
      + "\nAlternate[" + alternateCS.name + "]";

  }
}
