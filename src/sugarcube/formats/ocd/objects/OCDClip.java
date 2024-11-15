package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;

import java.awt.*;
import java.util.Collection;

public class OCDClip extends OCDPaintable
{
  public static final String TAG = "clip";
  public static final String ID_NONE = "";
  public static final String ID_PAGE = "c0";
  
  protected Path3 path = null;

  public OCDClip(OCDNode parent)
  {
    super(TAG, parent);
  }
  
  public OCDClip(OCDNode parent, String id)
  {
    super(TAG, parent);
    this.setID(id);
  }
  
  public OCDClip(OCDNode parent, Path3 path, boolean copy)
  {
    this(parent);
    this.path = copy ? path.copy() : path;
  }

  public OCDClip(OCDNode parent, Shape shape)
  {
    this(parent);
    this.path = new Path3(shape);
  }

  public OCDClip(OCDNode parent, Shape shape, String id)
  {
    this(parent);
    this.path = new Path3(shape);
    this.setID(id);
  }

  public int doClip(OCDNode node, double overlapMin)
  {
    if (node == null)
      return 1;

    Rectangle3 box = node.bounds();
    // Horizontal and vertical lines may have zero width or height
    if (box.width < 0.01f)
      box.width = 0.01f;
    if (box.height < 0.01f)
      box.height = 0.01f;

    if (path().contains(box))
      return 1;

    if (bounds().overlapThat(box) > overlapMin)
      return 0;

    node.delete();
    return -1;
  }

  public boolean isNone()
  {
    String id = id();
    return Str.IsVoid(id) || id.equals(ID_PAGE);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    if (path != null)
    {
      xml.writeID(id);
      xml.write("wind", path.isNonZero() ? null : path.wind());
      props.writeAttributes(xml);
      xml.write("d", OCD.path2xml(path, xml.numberFormat()));
    }
    return children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
   readXmlID(dom);
    if (dom.has("d"))
      path = OCD.xml2path(dom.value("d", ""), false);
    else
      path = OCD.xml2path(dom.cdata(), false);
    path.setWind(dom.value("wind", Path3.NONZERO));
    props.readAttributes(dom);

  }

  @Override
  public Path3 path()
  {
    return path;
  }

  public void setPath(Path3 path)
  {
    this.path = path;
  }

  @Override
  public Rectangle3 bounds()
  {
    return new Rectangle3(path.getBounds2D());
  }

  @Override
  public String sticker()
  {
    Rectangle3 r = this.bounds();
    return this.id() + "[" + (int) r.x() + ", " + (int) r.y() + ", " + (int) r.width() + ", " + (int) r.height() + "]";
  }

  @Override
  public String toString()
  {
    return "OCDClip[" + tag + "]" + (this.isDefinition() ? "\nID[" + this.id() + "]" : "\nZOrder[" + this.zOrder + "]") + "\nPath["
        + this.path.stringValue(0.1f) + "]" + "\nWind[" + this.path.wind() + "]" + "\n" + Xml.toString(this);
  }

  public boolean fits(Path3 path, double precision)
  {
    if (this.path.isBBox(precision) && path.isBBox(precision) && this.bounds().equals(path.bounds(), precision))
      return true;
    else
      return this.path.equalsPath(path, precision);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    return path.equalsPath(((OCDClip) o).path, 0.01f);
  }

  @Override
  public int hashCode()
  {
    return path.hashCode();
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    g.setClip(props.use_clips ? this.path() : null);
    if (props.show_clips)
    {
      g.setStroke(new BasicStroke(1));
      g.setColor(Color3.TEAL.alpha(0.05));
      g.fill(path());
      g.setColor(Color3.TEAL.alpha(0.5));
      g.draw(path());
    }
  }
}
