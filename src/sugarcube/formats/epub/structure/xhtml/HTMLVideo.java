package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.system.io.Mime;
import sugarcube.formats.ocd.objects.OCDNode;

public class HTMLVideo extends HTMLNode
{
  public static final String TAG = "video";

  public HTMLVideo(String id, boolean controls, int width, int height, String... sources)
  {
    super(TAG, "width", "" + width, "height", "" + height);
    this.setID(id);
    if (controls)
      this.addAttributes("controls", "controls");
//    this.addAttributes("preload", "none");
    for (String src : sources)
      this.addSource(src);
    this.addChild(new HTMLCData("Video not supported"));//no "\n" line return !!!   
  }
  
  public HTMLVideo(String id, boolean controls, String poster, int width, int height, Iterable<String> sources)
  {
    super(TAG, "width", "" + width, "height", "" + height, "poster", poster);
    this.setID(id);
    if (controls)
      this.addAttributes("controls", "controls");
//    this.addAttributes("preload", "none");
    for (String src : sources)
      this.addSource(src);
    this.addChild(new HTMLCData("Video not supported"));//no "\n" line return !!!   
  }

  public final HTMLVideo addSource(String src)
  {
    OCDNode cdata = nodes.isPopulated() && nodes.last() instanceof HTMLCData ? nodes.removeLast() : null;      
    this.addChild(new HTMLSource(src, Mime.get(src, null)));
    if(cdata!=null)
      this.addChild(cdata);
    return this;
  }
}