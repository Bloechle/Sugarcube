package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class HTMLLinkBox extends HTMLNode
{
  public static final String TAG = "a";
  private HTMLBox box;  
  private String href;

  public HTMLLinkBox(String href, HTMLBox box)
  {
    super(TAG);        
    this.href = href;
    this.box = box.pointer();    
    this.box.setCData("");
  }
  
  public HTMLLinkBox(String href, Rectangle3 box)
  {
    this(href, new HTMLBox(box).pointer());
  }

  
  public double overlap(HTMLLinkBox link)
  {
    return this.box.rect.overlap(link.box.rect);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("href", href);
    return new List3<HTMLNode>(box);
  }
}
