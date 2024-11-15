package sugarcube.formats.pdf.reader.pdf.node.function;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;

public class FunctionType4 extends PDFFunction
{
  private byte[] postscript;

  //PostScript calculator function
  public FunctionType4(PDFNode vo, PDFStream stream)
  {
    super(vo, stream);
    this.postscript = stream.byteValues();
  }

  @Override
  public float[] eval(float[] inputs)
  {

    float[] arrayOfFloat1 = new float[outputSize()];
    float[] arrayOfFloat2 = new float[outputSize()];
    try
    {
      PostscriptFactoryJP localPostscriptFactory = new PostscriptFactoryJP(this.postscript);

      localPostscriptFactory.resetStacks(inputs);

      double[] arrayOfDouble = localPostscriptFactory.executePostscript();
      //localPostscriptFactory.showStack();
      //XED.LOG.debug(this,".eval - arrayOfDouble="+Zen.Array.toString(arrayOfDouble));      
      int i;
      int j;
      if (this.domain.length / 2 == 1)
      {
        i = 0;
        j = this.range.length / 2;
        while (i < j)
        {
          arrayOfFloat1[i] = (float) arrayOfDouble[i];
          arrayOfFloat2[i] = Math.min(Math.max(arrayOfFloat1[i], this.range[(i * 2)]), this.range[(i * 2 + 1)]);
          i++;
        }
      }
      else
      {
        i = 0;
        j = this.range.length / 2;
        while (i < j)
        {
          arrayOfFloat1[i] = (float) arrayOfDouble[i];
          arrayOfFloat2[i] = Math.min(Math.max(arrayOfFloat1[i], this.range[(i * 2)]), this.range[(i * 2 + 1)]);
          i++;
        }
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }


//    XED.LOG.debug(this,".eval - inputs="+Zen.Array.toString(inputs)+" outputs="+Zen.Array.toString(arrayOfFloat2));

    return arrayOfFloat2;

  }
}
