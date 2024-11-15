package sugarcube.formats.epub.structure.nav;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.epub.structure.xhtml.HTMLNode;
import sugarcube.formats.ocd.objects.OCDNode;

public class NavNode extends HTMLNode implements NavDocumentAccessor
{
  public String type = "";

  public NavNode(String tag, OCDNode parent)
  {
    super(tag, parent);
  }

  public NavNode(String tag, String... props)
  {
    super(tag, (OCDNode)null);
    this.addAttributes(props);
  }

  public String type()
  {
    return type;
  }

  public NavNode lookFor(String tag, String type)
  {
    if (this.tag.equals(tag) && this.type.equals(type))
      return this;
    NavNode nav;
    for (OCDNode node : nodes)
      if ((nav = ((NavNode) node).lookFor(tag, type)) != null)
        return nav;
    return null;
  }

  @Override
  public NavDocument navigation()
  {
    return parent == null ? null : ((NavDocumentAccessor) parent).navigation();
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    return instance(child == null ? "" : child.tag(), child == null ? "" : child.value(NavRoot.TYPE, ""), this);
  }
  
  @Override
  public List3<? extends OCDNode> children()
  {
    return nodes;
  }  
  
  @Override
  public void readAttributes(DomNode dom)
  {
//    Log.debug(this, ".readAttributes - remaining: "+dom.remainingAttributes());
    props.readAttributes(dom, true);
  }  

  public static NavNode instance(String tag, String type, OCDNode parent)
  {
//    Log.debug(NavNode.class, ".instance - parent=" + parent.tag + ", tag=" + tag + ", type=" + type);
    if (tag.equals(NavList.TAG))
      return new NavList(parent);
    else if (tag.equals(NavLink.TAG))
      return new NavLink(parent);
    else if (tag.equals(NavItem.TAG))
      return new NavItem(parent);
    else if (tag.equals(NavMeta.TAG))
      return new NavMeta(parent);
    else if (tag.equals(NavSpan.TAG))
      return new NavSpan(parent);
    else if (tag.equals(NavTitle.TAG))
      return new NavTitle(parent);
    else if (tag.equals(NavRoot.TAG))
      if (type.equals(NavRoot.TYPE_TOC))
        return new NavToc(parent);
      else if (type.equals(NavRoot.TYPE_LANDMARKS))
        return new NavLandmarks(parent);

    Log.debug(NavNode.class, ".instance - default node: tag=" + tag + ", type=" + type);
    return new NavNode(tag, parent);
  }
}
