package sugarcube.formats.pdf.reader.pdf.node;

import java.util.LinkedList;

public class PDFPages extends PDFNode<PDFPage>
{
  public PDFPages(PDFDocument parent)
  {
    super("Pages", parent);
  }

  @Override
  public String sticker()
  {
    return "Pages";
  }

  public PDFPage firstPage()
  {
    return children.isEmpty() ? null : children.getFirst();
  }

  public LinkedList<PDFPage> getPages()
  {
    return children();
  }
}
