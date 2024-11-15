package sugarcube.formats.pdf.reader.pdf.node.function;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

public abstract class PDFFunction extends PDFNode
{
  protected int fctType = -1;
  protected float[] domain = new float[0]; // required - input values domain
  protected float[] range = new float[0];// required only for type 0 and type 4

  protected PDFFunction(PDFNode node)
  {
    super("Function", node);
  }

  public PDFFunction(PDFNode parent, PDFDictionary map)
  {
    super("Function", parent);
    this.reference = map.reference();
    this.fctType = map.get("FunctionType").intValue();
    this.domain = map.get("Domain").toPDFArray().floatValues();
    this.range = map.get("Range").toPDFArray().floatValues();
  }
  
  public boolean isIdentity()
  {
    return false;
  }

  public static PDFFunction instance(PDFNode parent, PDFObject po)
  {
    // Log.debug(PDFFunction.class, ".instance - po: " + po);
    if ((po = po.unreference()).isPDFArray())
    {
      return new FunctionArray1N(parent, po.toPDFArray());
    } else if (po.isPDFDictionary())
      switch (po.toPDFDictionary().get("FunctionType").intValue())
      {
      case 0:
        // Sampled function
        return new FunctionType0(parent, po.toPDFStream());
      case 2:
        // Exponential interpolation function
        return new InterpolationFunction(parent, po.toPDFDictionary());
      case 3:
        // Stitching function
        return new StitchingFunction(parent, po.toPDFDictionary());
      case 4:
        // PostScript calculator function
        return new FunctionType4(parent, po.toPDFStream());
      default:
      {
        Log.debug(PDFFunction.class, ".instance - parent=" + parent + ", object=" + po);
      }
      }
    else
    {
      if (po.toString().equals("Identity"))
        return new FunctionIdentity(parent);
    }
    return new FunctionIdentity(parent);
  }

  public int inputSize()
  {
    return this.domain.length / 2;
  }

  public int outputSize()
  {
    return this.range.length / 2;
  }

  public float[] minDomain()
  {
    float[] a = new float[this.domain.length / 2];
    for (int i = 0; i < a.length; i++)
      a[i] = this.domain[2 * i];
    return a;
  }

  public float[] maxDomain()
  {
    float[] a = new float[this.domain.length / 2];
    for (int i = 0; i < a.length; i++)
      a[i] = this.domain[2 * i + 1];
    return a;
  }

  public float[] avgDomain()
  {
    float[] a = new float[this.domain.length / 2];
    for (int i = 0; i < a.length; i++)
      a[i] = (this.domain[2 * i] + this.domain[2 * i + 1]) / 2;
    return a;
  }

  public float[] domain(float... values)
  {
    if (domain == null || domain.length == 0 || domain.length != 2 * values.length)
      return values;
    float[] input = Zen.Array.copy(values);
    for (int i = 0; i < values.length; i++)
      if (domain[2 * i] < domain[2 * i + 1])
        if (input[i] < domain[2 * i])
          input[i] = domain[2 * i];
        else if (input[i] > domain[2 * i + 1])
          input[i] = domain[2 * i + 1];
    return input;
  }

  public float[] range(float... values)
  {
    if (range == null || range.length == 0 || range.length != 2 * values.length)
      return values;
    float[] input = Zen.Array.copy(values);
    for (int i = 0; i < values.length; i++)
      if (range[2 * i] < range[2 * i + 1])
        if (input[i] < range[2 * i])
          input[i] = range[2 * i];
        else if (input[i] > range[2 * i + 1])
          input[i] = range[2 * i + 1];
    return input;
  }

  public float[] transform(float... inputs)
  {
    if (inputs.length < inputSize())
      Log.warn(this, ".transform - wrong inputs size: " + inputs.length + "!=" + inputSize());
    for (int i = 0; i < inputs.length; i++)
      inputs[i] = Math.min(Math.max(inputs[i], domain[2 * i]), domain[2 * i + 1]);
    float[] outputs = eval(inputs);
    for (int i = 0; (this.range != null) && (i < outputs.length) && i < range.length / 2; i++)
      outputs[i] = Math.min(Math.max(outputs[i], range[2 * i]), range[2 * i + 1]);
    return outputs;
  }

  protected abstract float[] eval(float[] inputs);

  public static float interpolate(float x, float xmin, float xmax, float ymin, float ymax)
  {
    return ymin + (x - xmin) * (ymax - ymin) / (xmax - xmin);
  }

  @Override
  public String sticker()
  {
    int inSize = this.domain.length / 2;
    int outSize = this.transform(avgDomain()).length;
    return this.getClass().getSimpleName() + "[" + inSize + "D to " + outSize + "D] " + this.reference;
  }

  @Override
  public String toString()
  {
    return sticker() + "\nFunctionType[" + this.fctType + "]" + "\nDomain" + PDF.toString(domain) + "\nRange" + PDF.toString(range);
  }

  @Override
  public void paint(Graphics3 g, PDFDisplayProps props)
  {
    int inSize = this.domain.length / 2;
    int outSize = this.transform(avgDomain()).length;
    // Zen.LOG.debug(this, ".paint - inSize="+inSize+", outSize="+outSize);

    int w = g.intWidth();
    int h = g.intHeight();
    Image3 image = PDF.ImageRGB(g.width(), g.height());
    if (inSize == 1)
      for (int y = 0; y < h; y++)
        for (int x = 0; x < w; x++)
        {
          float[] out = this.transform((domain[1] - domain[0]) * x / (w - 1) + domain[0]);
          if (outSize == 1)
            image.setRGB(x, y, out[0], out[0], out[0]);
          else if (outSize == 2)
            image.setRGB(x, y, out[0], out[1], 1);
          else if (outSize == 3)
            image.setRGB(x, y, out);
          else if (outSize == 4)
            image.setCMYK(x, y, out);
        }
    g.draw(image, null);
  }
}
