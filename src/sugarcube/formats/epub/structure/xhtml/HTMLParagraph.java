
package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.data.Base;

public class HTMLParagraph extends HTMLNode
{
  public HTMLParagraph(HTMLNode... child)
  {
    super("p");
//    this.setID(id);
//    if (controls)
//      this.addAttributes("controls", "controls");
//    this.addAttributes("class", "layer", "style", "top:" + (y - fs) + "px; left:" + x + "px; font-size: " + fs + "px; ");

    this.addChildren(child);
  }
  
  public HTMLParagraph(String cdata)
  {
    super("p");
    this.setID(Base.x32.random12());
//    if (controls)
//      this.addAttributes("controls", "controls");
//    this.addAttributes("class", "layer", "style", "top:" + (y - fs) + "px; left:" + x + "px; font-size: " + fs + "px; ");

    this.addChildren(new HTMLCData(cdata));
  }  
}
