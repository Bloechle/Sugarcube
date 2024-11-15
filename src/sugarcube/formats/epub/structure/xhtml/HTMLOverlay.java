package sugarcube.formats.epub.structure.xhtml;

import sugarcube.formats.epub.structure.js.JS;

public class HTMLOverlay extends HTMLDiv
{

  public HTMLOverlay(String id, boolean modal)
  {
    super();
    this.setID(id);
    this.classname("sc-overlay");
    if (modal)
    {
      this.classname("sc-modal-off");
      this.onclick(JS.AddClass(id, "-sc-modal-on sc-modal-off"));
    }
    this.escape(false,  true);
  }
  
  public HTMLOverlay(String id, boolean modal, HTMLNode node)
  {
    this(id, modal);
    this.add(node);
  }
  
  public static HTMLOverlay Get(String id, boolean modal, HTMLNode node)
  {
    return new HTMLOverlay(id, modal, node);
  }

}
