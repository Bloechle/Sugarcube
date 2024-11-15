package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

public class PDFObjectNode extends PDFNode
{
  public PDFObject object;

  public PDFObjectNode(PDFNode parent, PDFObject object)
  {
    super(object.type.name(), parent);
    this.object = object;
  }

  @Override
  public String sticker()
  {
    return object.sticker();
  }

  @Override
  public String toString()
  {
    return object.toString();
  }
}
