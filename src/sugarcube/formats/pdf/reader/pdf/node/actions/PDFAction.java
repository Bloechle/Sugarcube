package sugarcube.formats.pdf.reader.pdf.node.actions;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class PDFAction extends PDFNode
{
  public static final String NULL = "null";
  protected String keyS;

  protected PDFAction(PDFNode parent, PDFDictionary map)
  {
    super("Action", parent);
    this.reference = map == null ? null : map.reference();
    this.keyS = map == null ? NULL : map.get("S").toPDFName().stringValue();
  }

  @Override
  public String sticker()
  {
    return type + "[" + keyS + "]";
  }

  @Override
  public String toString()
  {
    return type + "[" + keyS + "]" + "\n" + "";
  }

  public boolean isGotoRAction()
  {
    return false;
  }

  public boolean isGotoAction()
  {
    return false;
  }
  
  public boolean isNamedAction()
  {
    return false;
  }

  public boolean isURIAction()
  {
    return false;
  }

  public boolean isJavascriptAction()
  {
    return false;
  }
  
  public PDFNamedAction asNamedAction()
  {
    return (PDFNamedAction) this;
  }

  public PDFGotoAction asGotoAction()
  {
    return (PDFGotoAction) this;
  }

  public PDFGotoRAction asGotoRAction()
  {
    return (PDFGotoRAction) this;
  }

  public PDFURIAction asURIAction()
  {
    return (PDFURIAction) this;
  }

  public PDFJavascriptAction asJavascriptAction()
  {
    return (PDFJavascriptAction) this;
  }

  public static PDFAction Get(PDFNode parent, PDFDictionary map)
  {
    String action = map == null ? NULL : map.get("S").toPDFName().stringValue();
    switch (action)
    {
    case "GoTo":
      return new PDFGotoAction(parent, map);
    case "GoToR":
      return new PDFGotoRAction(parent, map);
    case "URI":
      return new PDFURIAction(parent, map);
    case "JavaScript":
      return new PDFJavascriptAction(parent, map);
    case "Named":
      return new PDFNamedAction(parent, map);
    }
    Log.debug(PDFAction.class, ".instance - " + action + " not yet implemented: " + map);
    return new PDFAction(parent, map);
  }
}
