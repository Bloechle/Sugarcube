package sugarcube.formats.epub.replica.svg;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGDemoText extends SVGPaintable
{
  public SVGDemoText(OCDNode parent, SVGPage page)
  {
    super("text", parent, page);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    int x=(int)page.viewBox().width / 2;
    int y=(int)page.viewBox().height / 2;
    xml.write("x", x);
    xml.write("y", y);
    xml.write("fill", "#FF0000");
    xml.write("opacity", "0.15");
    xml.write("text-anchor", "middle");
    xml.write("transform", "rotate(45,"+x+","+y+")");
    xml.write("font-family", "monospace");
    xml.write("font-size", "36");
    xml.write("font-weight", "bold");
    xml.writeCData("DEMO VERSION", false);
    return this.children();
  }
}