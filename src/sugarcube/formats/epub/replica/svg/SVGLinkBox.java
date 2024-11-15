package sugarcube.formats.epub.replica.svg;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGLinkBox extends SVGPaintable
{
  public static final String TAG = "rect";
  public static final String CLASS = "sc-link-box";
  protected Rectangle3 rect;

  public SVGLinkBox(double x, double y, double w, double h)
  {
    super(TAG, null, null);
    this.rect = new Rectangle3(x, y, w, h);
  }

  public SVGLinkBox(String id, double x, double y, double w, double h)
  {
    this(x, y, w, h);
    this.id = id;
  }
  
  public Rectangle3 rect()
  {
    return rect;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("id", id);
    xml.write("x", rect.intX());
    xml.write("y", rect.intY());
//    xml.write("rx", "8");
//    xml.write("ry", "8");
    xml.write("width", rect.intWidth());
    xml.write("height", rect.intHeight());
    xml.write("class", CLASS);
    return this.children();
  }
}
