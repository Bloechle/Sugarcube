package sugarcube.formats.pdf.reader.pdf.node.actions;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

public class PDFGotoRAction extends PDFAction
{
  private static boolean UNLOG = true;
  private Reference pageRef = null;
  private String file = null;
  private String action = "";
  private boolean newWindow = false;

  public PDFGotoRAction(PDFNode parent, PDFDictionary map)
  {
    super(parent, map);
    file = map.get("F").stringValue("");

    PDFObject d = map.get("D").unreference();

    if (d.isPDFArray())
    {
      PDFArray a = map.get("D").toPDFArray();
      if (a.isValid())
        pageRef = a.get(0).toPDFPointer().get();
    }
    else if (d.isPDFString())
      action = d.stringValue("");

    if (UNLOG)
    {
      Log.warn(this, " - referencing external file: " + map);
      UNLOG = false;
    }
  }
  
  public String gotoAction()  
  {
    return action;
  }

  public Reference gotoRef()
  {
    return this.pageRef;
  }

  @Override
  public boolean isGotoRAction()
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
    return type + "[" + keyS + "]"
      + "\nPage[" + pageRef + "]"
      + "";

  }
}
