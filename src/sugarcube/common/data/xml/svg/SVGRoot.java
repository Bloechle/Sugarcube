package sugarcube.common.data.xml.svg;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGRoot extends OCDNode
{
  public static final String TAG = "svg";
  protected SVGStyle style;  
  protected SVGDefs defs;
  protected String version = "1.1";
  protected String width = "100%";
  protected String height = "100%";
  protected List3<OCDNode> nodes = new List3<OCDNode>();

  public SVGRoot(OCDNode parent, OCDNode... nodes)
  {
    super(TAG, parent);
    this.style = new SVGStyle(this);
    this.defs = new SVGDefs(this);
    if (nodes != null)
      this.nodes.add(nodes);
  }

  public void add(OCDNode... nodes)
  {
    for (OCDNode node : nodes)
      node.setParent(this);
    this.nodes.add(nodes);
  }

  public SVGDefs defs()
  {
    return defs;
  }

  public SVGStyle style()
  {
    return style;
  }
  
  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("xmlns", "http://www.w3.org/2000/svg");
    xml.write("xmlns:xlink", "http://www.w3.org/1999/xlink");
    xml.write("width", width);
    xml.write("height", height);
    xml.write("version", version);
    // xml.write("viewBox", "0 0 " + width() + " " + height());
    // xml.write("preserveAspectRatio", "xMidYMid meet");

    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
    this.width = e.value("width", this.width);
    this.height = e.value("height", this.height);
    this.version = e.value("version", this.version);
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    List3<OCDNode> children = new List3<OCDNode>();
    if (!style.isEmpty())
      children.add(style);
    if (!defs.isEmpty())
      children.add(defs);
    children.addAll(nodes);
    return children;
  }

  @Override
  public OCDNode newChild(DomNode child)
  {
    if (OCD.isTag(child, SVGDefs.TAG))
      return this.defs;
    else
      return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
  }

  @Override
  public String toString()
  {
    return Xml.toString(this);
  }
}
