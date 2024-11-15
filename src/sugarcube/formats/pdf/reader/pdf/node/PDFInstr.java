package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.data.collections.List3;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.PDFOperator;
import sugarcube.formats.pdf.reader.pdf.object.StreamLocator;

import java.util.List;

public class PDFInstr
{
  protected PDFOperator op;
  protected List3<PDFObject> params = new List3<>();
  protected PDFContent.Phase phase;

  public PDFInstr(PDFOperator op, List<PDFObject> params, PDFContent.Phase phase)
  {
    this.op = op;
    this.params.addAll(params);
    this.phase = phase;
  }

  public StreamLocator streamLocator()
  {
    return op.streamLocator();
  }

  public PDFOperator.OP op()
  {
    return op.op();
  }

  public List3<PDFObject> params()
  {
    return params;
  }

  public PDFContent.Phase phase()
  {
    return phase;
  }

  @Override
  public String toString()
  {
    return op.streamLocator().toString();
  }
}
