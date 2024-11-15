package sugarcube.formats.pdf.reader.pdf.node.function;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

import java.util.LinkedList;
import java.util.List;

public class StitchingFunction extends PDFFunction
{
//use several 1D input functions to procude a new 1D input function
  private PDFFunction[] functions;
  private float[] bounds = null;
  private float[] encode = null;
  //Stitching function

  public StitchingFunction(PDFNode vo, PDFDictionary map)
  {
    super(vo, map);
    List<PDFFunction> list = new LinkedList<PDFFunction>();
    for (PDFObject po : map.get("Functions").toPDFArray())
    {
      PDFFunction fct = PDFFunction.instance(vo, po);
      list.add(fct);
      this.add(fct);
    }
    this.functions = list.toArray(new PDFFunction[0]);
    this.bounds = map.get("Bounds").toPDFArray().floatValues();
    this.encode = map.get("Encode").toPDFArray().floatValues();
  }

  @Override
  public float[] eval(float[] inputs)
  {
    float f1 = Math.min(Math.max(inputs[0], this.domain[0]), this.domain[1]);
    int i;
    for (i = this.bounds.length - 1; (i >= 0) && (f1 < this.bounds[i]); i--);
    i++;
    float[] interpol = new float[1];
    float f2 = this.domain[0];
    float f3 = this.domain[1];
    if (i > 0)
      f2 = this.bounds[i - 1];
    if (i < this.bounds.length)
      f3 = this.bounds[i];
    float f4 = this.encode[i * 2];
    float f5 = this.encode[i * 2 + 1];
    interpol[0] = interpolate(f1, f2, f3, f4, f5);
    float[] tmp = this.functions[i].eval(interpol);
    float[] outputs = new float[tmp.length];
    int j;
    if (this.range != null && this.range.length > 0)
      for (j = 0; j != this.range.length / 2; j++)
        outputs[j] = Math.min(Math.max(tmp[j], this.range[0]), this.range[1]);
    else
      for (j = 0; j != tmp.length; j++)
        outputs[j] = tmp[j];
    return outputs;
  }

  @Override
  public String toString()
  {
    return sticker()
      + "\nFunctionType[" + this.fctType + "]" + this.reference()
      + "\nDomain" + PDF.toString(domain)
      + "\nRange" + PDF.toString(range)
      + "\nBounds" + PDF.toString(bounds)
      + "\nEncode" + PDF.toString(encode);
//      + "\nMap[" + (pdfStream == null ? "null" : pdfStream.reference().toString()) + "]";
  }
}
