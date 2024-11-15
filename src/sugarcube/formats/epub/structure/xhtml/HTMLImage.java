
package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.graphics.geom.Rectangle3;

public class HTMLImage extends HTMLNode
{
  public static final String TAG = "img";

  public HTMLImage(String src, String... props)
  {
    super(TAG, "src", src);
    this.addAttributes(props);  
    this.escape(false,  true);
  }
  
  public HTMLImage(String id, String src, Rectangle3 box, String style, String... props)
  {
    this(TAG, "id", id, "src", src, "style", "position:absolute; "+HTMLBox.Style(box)+style);        
    this.addAttributes(props);
  }  

}