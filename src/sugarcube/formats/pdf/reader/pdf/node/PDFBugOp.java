package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.pdf.object.StreamLocator;

//buggy operators are wrapped into this object and added to the PDF Content (text, path, image, clip, and bad ops...)
public class PDFBugOp extends PDFNode
{
  private PDFInstr op;

  public PDFBugOp(PDFInstr op, PDFContent content)
  {
    super(Dexter.BADOP, content);
    this.op = op;
  }

  public PDFInstr operator()
  {
    return op;
  }

  @Override
  public String sticker()
  {
    return id + " » " + type + "[" + op.op.stringValue() + "]";
  }

  @Override
  public String toString()
  {
    return this.getClass().getName() + "[" + op.op.op() + "]"
      + "\nParams" + op.params.toString()
      + "\nPhase[" + op.phase + "]";
  }

  @Override
  public StreamLocator streamLocator()
  {
    return op.streamLocator();
  }
}
