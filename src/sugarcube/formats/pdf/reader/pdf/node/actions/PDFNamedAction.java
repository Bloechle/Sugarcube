package sugarcube.formats.pdf.reader.pdf.node.actions;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class PDFNamedAction extends PDFAction
{
  private String name = null;

  public PDFNamedAction(PDFNode parent, PDFDictionary map)
  {
    super(parent, map);    
    name = map.get("N").unreference().stringValue();
  }   
  
  public String name()
  {
    return name;
  }
  
  public boolean isNamedAction()
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
    return type + "[" + keyS + "]" + "\nN[" + name + "]";

  }
}
