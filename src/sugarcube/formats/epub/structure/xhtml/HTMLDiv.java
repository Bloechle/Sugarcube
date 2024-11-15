
package sugarcube.formats.epub.structure.xhtml;

public class HTMLDiv extends HTMLNode
{
  public static final String TAG = "div";

  public HTMLDiv(String... props)
  {
    super(TAG, props);
  }
  
  public HTMLDiv idClass(String id, String classname)
  {
    return (HTMLDiv)super.idClass(id, classname);
  }
  
  public HTMLDiv style(String style)
  {
    return (HTMLDiv) super.style(style);
  }
   

}