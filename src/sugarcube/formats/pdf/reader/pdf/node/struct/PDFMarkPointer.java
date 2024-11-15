package sugarcube.formats.pdf.reader.pdf.node.struct;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class PDFMarkPointer extends PDFNode
{
  public Reference obj;
  public Reference stm;
  public Reference stmOwn;
  public Reference page;
  public int mcid;

  public PDFMarkPointer(PDFStructElem parent, PDFDictionary map)
  {
    super("MCR", parent);//MarkContentRef
    if (map.has("Obj"))
      obj = map.get("Obj").toPDFPointer().get();
    if (map.has("Pg"))
      page = map.get("Pg").toPDFPointer().get();
    else
      page = parent.page;
    if (map.has("Stm"))
      page = map.get("Stm").toPDFPointer().get();
    if (map.has("StmOwn"))
      page = map.get("StmOwn").toPDFPointer().get();
    mcid = map.get("MCID").intValue(-1);
  }

  @Override
  public String sticker()
  {
    return "MarkPointer[" + mcid + "]";
  }

  @Override
  public String toString()
  {
    return sticker()
      + "\nObj[" + (obj == null ? "null" : reference) + "]"
      + "\nPage[" + (page == null ? "null" : page) + "]"
      + "\nStm[" + (stm == null ? "null" : stm) + "]"
      + "\nStmOwn[" + (stmOwn == null ? "null" : stmOwn) + "]"
      + "";

  }
}
