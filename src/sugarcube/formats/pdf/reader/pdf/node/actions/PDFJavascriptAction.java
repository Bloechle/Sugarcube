package sugarcube.formats.pdf.reader.pdf.node.actions;

import sugarcube.common.data.xml.Nb;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class PDFJavascriptAction extends PDFAction
{
  private String js = "";
  private int pageNb;
  private Reference gotoRef;

  public PDFJavascriptAction(PDFNode parent, PDFDictionary map)
  {
    super(parent, map);
    this.js = map.get("JS").stringValue("");
    this.pageNb = this.parsePageNumber();
    // Log.debug(this, " - pageNb="+pageNb+", ref="+this.reference);
    if (pageNb > -1)
      this.gotoRef = this.document().refs2PagesMap.reference(pageNb);
  }

  public Reference gotoRef()
  {
    return gotoRef;
  }

  public int pageNum()
  {
    return pageNb;
  }

  private int parsePageNumber()
  {
    int i = js.toLowerCase().indexOf("this.pagenum");
    if (i < 0)
      return -1;
    String nb = "";
    boolean parsed = false;
    for (char c : js.substring(i).toCharArray())
    {
      if (c >= '0' && c <= '9')
      {
        parsed = true;
        nb += c;
      } else if (parsed)
        break;
    }
    return Nb.Int(nb, -2) + 1;
  }

  public String js()
  {
    return js;
  }

  @Override
  public boolean isJavascriptAction()
  {
    return true;
  }

  @Override
  public String sticker()
  {
    return type + "[" + keyS + "]";
  }

  @Override
  public String toString()
  {
    return type + "[" + keyS + "]" + "\nJS[" + js + "]" + "\nPageNb[" + pageNb + "]" + "\nGotoRef[" + gotoRef + "]";

  }
}
