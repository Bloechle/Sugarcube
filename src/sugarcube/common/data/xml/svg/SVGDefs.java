package sugarcube.common.data.xml.svg;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.font.SVGFont;

import java.util.Collection;

public class SVGDefs extends OCDNode
{
  public static final String TAG = "defs";
  private List3<OCDNode> defs = new List3<OCDNode>();

  public SVGDefs(OCDNode parent, OCDNode... nodes)
  {
    super(TAG, parent);
    this.addDefs(nodes);
  }

  public boolean isEmpty()
  {
    return defs.isEmpty();
  }

  public List3<SVGFont> fonts()
  {
    List3<SVGFont> fonts = new List3<SVGFont>();
    for (OCDNode node : defs)
      if (node.is(SVGFont.TAG))
        fonts.add((SVGFont) node);
    return fonts;
  }

  public void addDefs(boolean updateParents, OCDNode... nodes)
  {
    if (updateParents)
      for (OCDNode node : nodes)
        node.setParent(this);
    this.defs.add(nodes);
  }

  public void addDefs(OCDNode... nodes)
  {
    this.addDefs(true, nodes);
  }

  public void addDefs(Collection<? extends OCDNode> nodes)
  {
    for (OCDNode node : nodes)
      node.setParent(this);
    this.defs.addAll(nodes);
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    return defs;
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    if (OCD.isTag(child, SVGFont.TAG))
      return new SVGFont(this);
    else
      return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    if (OCD.isTag(child, SVGFont.TAG))
      addDefs((SVGFont) child);
  }
}
