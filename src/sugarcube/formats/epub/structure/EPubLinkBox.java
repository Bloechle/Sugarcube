package sugarcube.formats.epub.structure;

import sugarcube.common.graphics.geom.Rectangle3;

public class EPubLinkBox
{  
  public String url;
  public Rectangle3 box;
  public String target;

  public EPubLinkBox(String url, Rectangle3 box, String target)
  {    
    this.url = url;
    this.box = box;
    this.target = target;
  }
  
  public String toString()
  {
    return url;
  }
}
