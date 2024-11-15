package sugarcube.formats.pdf.reader.pdf.node.function;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class InterpolationFunction extends PDFFunction
{
  private float[] C0 =
  {
    0f
  };
  private float[] C1 =
  {
    1f
  };
  private double N;

  //Exponential interpolation function
  public InterpolationFunction(PDFNode node, PDFDictionary map)
  {
    super(node, map);
    this.N = map.get("N").toPDFNumber().doubleValue();
    this.C0 = map.get("C0").toPDFArray().floatValues(C0);
    this.C1 = map.get("C1").toPDFArray().floatValues(C1);
  }

  @Override
  protected float[] eval(float[] inputs)
  {
    float[] tmp = new float[this.C0.length];
    float f = Math.min(Math.max(inputs[0], this.domain[0]), this.domain[1]);
    int i;
    float[] outputs = new float[this.C0.length];
    if (this.N == 1.0f) //linear interpolation
      for (i = 0; i < this.C0.length; i++)
      {
        tmp[i] = (this.C0[i] + f * (this.C1[i] - this.C0[i]));
        if (this.range != null && this.range.length > 0)
          tmp[i] = Math.min(Math.max(tmp[i], this.range[(i * 2)]), this.range[(i * 2 + 1)]);
        outputs[i] = tmp[i];
      }
    else
      for (i = 0; i < this.C0.length; i++)
      {
        tmp[i] = (this.C0[i] + (float) Math.pow(f, this.N) * (this.C1[i] - this.C0[i]));
        if (this.range != null && this.range.length > 0)
          tmp[i] = Math.min(Math.max(tmp[i], this.range[(i * 2)]), this.range[(i * 2 + 1)]);
        outputs[i] = tmp[i];
      }
    //XED.LOG.debug(this,".eval - inputs="+Zen.Array.toString(inputs)+" ouputs="+Zen.Array.toString(outputs));
    return outputs;
  }

  @Override
  public String toString()
  {
    return sticker()
      + "\nFunctionType[" + this.fctType + "]"
      + "\nDomain" + PDF.toString(domain)
      + "\nRange" + PDF.toString(range)
      + "\nC0" + PDF.toString(C0)
      + "\nC1" + PDF.toString(C1)
      + "\nN[" + N + "]";
//      + "\nMap[" + (pdfStream == null ? "null" : pdfStream.reference().toString()) + "]";
  }
}
