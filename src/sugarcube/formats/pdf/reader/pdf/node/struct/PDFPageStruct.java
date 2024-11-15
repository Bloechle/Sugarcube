package sugarcube.formats.pdf.reader.pdf.node.struct;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.Stringer;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

import java.util.Map;

public class PDFPageStruct extends PDFNode
{
  public Reference page = null;
  public PDFStructElem xObjectOrAnnot = null;
  public Map3<Integer, PDFStructElem> mcids = new Map3<Integer, PDFStructElem>();
  public PDFStructTreeRoot treeRoot;

  public PDFPageStruct(PDFStructTreeRoot parent, Reference page)
  {
    super("PageStruct", parent);
    this.treeRoot = parent;
    this.page = page;
  }
  
  public PDFStructTreeRoot root()
  {
    return treeRoot;
  }

  public void add(int mcid, PDFStructElem elem)
  {
    if (mcid < 0)
      this.xObjectOrAnnot = elem;
    else
    {
      if(mcids.has(mcid))
        Log.debug(this,  ".add - mcid already exists: "+mcid+", "+elem);
      this.mcids.put(mcid, elem);
    }
  }

  public PDFStructElem get(int mcid)
  {
    if (mcid < 0)
      return xObjectOrAnnot;
    else
      return mcids.get(mcid);
  }

  @Override
  public String sticker()
  {
    return "PageStruct[" + page + "]";
  }

  @Override
  public String toString()
  {
    Stringer str = new Stringer();
    for (Map.Entry<Integer, PDFStructElem> entry : mcids.entrySet())
      str.span("\nStructElem[", entry.getKey(), " »", entry.getValue().sticker(), "]");
    return "PageStruct[" + page + "]"
      + "\nXObjectOrAnnot[" + (xObjectOrAnnot == null ? "null" : xObjectOrAnnot.sticker()) + "]"
      + str
      + "";
  }
}
