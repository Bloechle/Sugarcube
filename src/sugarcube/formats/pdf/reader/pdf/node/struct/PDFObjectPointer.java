package sugarcube.formats.pdf.reader.pdf.node.struct;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class PDFObjectPointer extends PDFNode
{
  public Reference obj;
  public Reference page;

  public PDFObjectPointer(PDFStructElem parent, PDFDictionary map)
  {
    super("OBJR", parent);//ObjectRef
    if (map.has("Obj"))
      obj = map.get("Obj").toPDFPointer().get();
    if (map.has("Pg"))
      page = map.get("Pg").toPDFPointer().get();
    else
      page = parent.page;
  }

  @Override
  public String sticker()
  {
    return "ObjectPointer" + obj;
  }

  @Override
  public String toString()
  {
    return sticker()
      + "\nObj[" + (obj == null ? "null" : reference) + "]"
      + "\nPage[" + (page == null ? "null" : page) + "]"
      + "";

  }
}
