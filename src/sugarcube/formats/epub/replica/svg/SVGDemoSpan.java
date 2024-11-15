package sugarcube.formats.epub.replica.svg;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGDemoSpan extends SVGTextSpan
{
  public SVGDemoSpan(SVGPage page)
  {
    super(null, page);
    this.zOrder = Float.MAX_VALUE;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("class", "c0");
    xml.writeCData(" DEMO ", false);
    return this.children();
  }
}