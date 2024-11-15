package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.system.io.Mime;
import sugarcube.formats.ocd.objects.OCDNode;

public class HTMLAudio extends HTMLNode
{
  public static final String TAG = "audio";

  public HTMLAudio(String id, boolean controls, String... sources)
  {
    super(TAG);
    this.setID(id);
    if (controls)
      this.addAttributes("controls", "controls");
    this.addAttributes("preload", "none");
    for (String src : sources)
      this.addSource(src);

//    XHTMLAnchor anchor = new XHTMLAnchor(src);
//    XHTMLImage image = new XHTMLImage(EPUB.IMAGE_FOLDER + RS.IMG_AUDIO);
//    anchor.addChild(image);
    this.addChild(new HTMLCData("Audio not supported"));
  }

  public final HTMLAudio addSource(String src)
  {
    OCDNode cdata = nodes.isPopulated() && nodes.last() instanceof HTMLCData ? nodes.removeLast() : null;
    this.addChild(new HTMLSource(src, Mime.get(src, null)));
    if (cdata != null)
      this.addChild(cdata);
    return this;
  }
}