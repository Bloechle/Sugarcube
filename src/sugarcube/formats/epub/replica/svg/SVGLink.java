package sugarcube.formats.epub.replica.svg;

import sugarcube.common.data.collections.Str;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGLink extends SVGGroup
{
  private SVGLinkBox box;
  private String xlink;
  private String target;

  public SVGLink(OCDNode parent, SVGPage page, String xlink, SVGLinkBox box, String target)
  {
    super("a", parent, page, null, box);
    this.xlink = xlink;
    this.box = box;
    this.target = target;
  }
  
  public SVGLinkBox box()
  {
    return box;
  }
  
  public String url()
  {
    return xlink.startsWith("www.") ? "http://" + xlink : xlink;
  }

  public double overlap(SVGLink link)
  {
    return this.box.rect.overlap(link.box.rect);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("xlink:href", url());
    if (Str.HasData(target))
      xml.write("target", target);
    return this.children();
  }
}
