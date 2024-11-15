package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;

public class HTMLBox extends HTMLDiv
{
  protected Rectangle3 rect;

  public HTMLBox(double x, double y)
  {
    super("class", "sc-box", "style", Style(x, y));
    this.rect = new Rectangle3(x, y, 1, 1);
    this.escape(false,  true);    
  }

  public HTMLBox(double x, double y, double scale)
  {
    this(x * scale, y * scale);
  }

  public HTMLBox(String id, double x, double y)
  {
    this(x, y);
    this.setID(id);
  }

  public HTMLBox(String id, double x, double y, double scale)
  {
    this(id, x * scale, y * scale);
  }

  public HTMLBox(double x, double y, double w, double h)
  {
    super("class", "sc-box", "style", Style(x, y, w, h));
    this.rect = new Rectangle3(x, y, w, h);
    this.escape(false,  true);     
  }

  public HTMLBox(String id, double x, double y, double w, double h)
  {
    this(x, y, w, h);
    this.setID(id);
  }
  
  public HTMLBox(Rectangle3 box)
  {
    this(box.x, box.y, box.width, box.height);    
  }


  public HTMLBox(String id, Rectangle3 box)
  {
    this(box.x, box.y, box.width, box.height);
    this.setID(id);
  }
  
  public HTMLBox pointer()
  {
     this.classname("sc-pointer");
     return this;
  }

  public HTMLBox onclick(String js)
  {
    return (HTMLBox) super.onclick(js);
  }

  @Override
  public HTMLBox style(String style)
  {
    return (HTMLBox) super.style(style);
  }
  
  public HTMLModal wrapInModal()
  {
    HTMLNode parent = this.htmlParent();
    HTMLModal modal = HTMLModal.Get(this);
    if(parent==null)
      Log.debug(this,  ".wrapInModal - parent node not found");
    else
    {
      parent.removeChild(this);
      parent.add(modal);
      Log.debug(this,  ".wrapInModal - wrapping "+modal.needID()+", parent="+parent.cast());
    }   
    return modal;     
  }
  
  public static String Style(double x, double y)
  {
    return "left:" + S(x) + "px; top:" + S(y) + "px;";
  }

  public static String Style(double x, double y, double w, double h)
  {
    return Style(x, y) + " width:" + S(w) + "px; height:" + S(h) + "px;";
  }

  public static String Style(Rectangle3 box)
  {
    return Style(box.x, box.y, box.width, box.height);
  }
  
  
}