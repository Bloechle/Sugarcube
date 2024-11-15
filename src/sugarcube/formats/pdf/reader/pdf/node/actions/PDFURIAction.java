package sugarcube.formats.pdf.reader.pdf.node.actions;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class PDFURIAction extends PDFAction
{
  private String uri = "";
  private boolean isMap = false;

  public PDFURIAction(PDFNode parent, PDFDictionary map)
  {
    super(parent, map);
    uri = map.get("URI").stringValue(uri);
    isMap = map.get("IsMap").booleanValue(isMap);
  }

  public String gotoURI()
  {
    return this.uri;
  }

  public boolean isMap()
  {
    return this.isMap;
  }

  @Override
  public boolean isURIAction()
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
      + "\nURI[" + uri + "]"
      + "";

  }
}