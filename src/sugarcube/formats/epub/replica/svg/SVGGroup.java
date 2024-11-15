package sugarcube.formats.epub.replica.svg;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.numerics.Math3;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGGroup extends SVGPaintable
{

  private Set3<SVGPaintable> nodes = new Set3<SVGPaintable>();

  public SVGGroup(String tag, OCDNode parent, SVGPage page, String clipID, SVGPaintable... nodes)
  {
    super(tag, parent, page);
    this.clipID = clipID;
    this.addAll(nodes);
  }

  public SVGGroup(OCDNode parent, SVGPage page, String clipID, SVGPaintable... nodes)
  {
    this("g", parent, page, clipID, nodes);
  }

  public SVGGroup(OCDNode parent, SVGPage page, String... props)
  {
    this("g", parent, page, null);
    this.props.putAll(props);
  }

  public SVGGroup scaleTransform(double scale)
  {
    if (Math3.Eq(1.0, scale, 0.0001))
      return this;
    return set("transform", "scale(" + scale + ")");
  }

  public SVGGroup clipPath(String clipID)
  {
    if (Str.IsVoid(clipID))
      return this;
    return set("clip-path", "url(#" + clipID + ")");
  }

  public SVGGroup set(String key, String value)
  {
    this.props.put(key, value);
    return this;
  }

  public SVGGroup addAll(SVGPaintable... nodes)
  {
    this.nodes.addAll(nodes);
    for (SVGPaintable node : nodes)
      node.setParent(this);
    return this;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    if (this.hasClip())
      this.writeXmlClip(xml);
    props.writeAttributes(xml);
    return this.children();
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    return nodes;
  }

  public Set3<SVGPaintable> nodes()
  {
    return nodes;
  }
}
