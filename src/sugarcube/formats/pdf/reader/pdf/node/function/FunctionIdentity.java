package sugarcube.formats.pdf.reader.pdf.node.function;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

public class FunctionIdentity extends PDFFunction
{
  public FunctionIdentity(PDFNode node)
  {
    super(node);
  }
  
  @Override
  public boolean isIdentity()
  {
    return true;
  }  

  @Override
  public float[] transform(float[] inputs)
  {
    return inputs;
  }

  @Override
  protected float[] eval(float[] inputs)
  {
    return inputs;
  }
}
