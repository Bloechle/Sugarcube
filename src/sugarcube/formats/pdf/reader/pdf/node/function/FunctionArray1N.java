package sugarcube.formats.pdf.reader.pdf.node.function;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

public class FunctionArray1N extends PDFFunction
{
  private PDFFunction[] functions;

  public FunctionArray1N(PDFNode parent, PDFArray array)
  {
    super(parent);
    PDFObject[] fcts = array.array();
    this.functions = new PDFFunction[fcts.length];
    for (int i = 0; i < fcts.length; i++)
    {
      functions[i] = PDFFunction.instance(parent, fcts[0]);
      this.add(functions[i]);
    }
  }

  @Override
  public float[] transform(float[] inputs)
  {
    float[] out = new float[functions.length];
    for (int i = 0; i < functions.length; i++)
      out[i] = functions[i].transform(new float[]
        {
          inputs[0]
        })[0];
    return out;
  }

  @Override
  protected float[] eval(float[] inputs)
  {
    float[] out = new float[functions.length];
    for (int i = 0; i < functions.length; i++)
      out[i] = functions[i].eval(new float[]
        {
          inputs[0]
        })[0];
    return out;
  }
}
