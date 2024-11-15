package sugarcube.formats.epub.structure.nav;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.objects.OCDNode;

public class NavRoot extends NavNode
{
  public static final String TYPE = "epub:type";
  public static final String TYPE_TOC = "toc";
  public static final String TYPE_LANDMARKS = "landmarks";
  public static final String TYPE_PAGELIST = "page-list";
  public static final String TAG = "nav";
  public NavH1 h1;
  public NavList items;

  public NavRoot(OCDNode parent, String type)
  {
    super(TAG, parent);
    this.type = type;
    this.h1 = new NavH1(this, "");
    this.items = new NavList(this);
    this.addAttributes(TYPE, type);
  }   

  public void setTitle(String title)
  {
    this.h1.setCData(title);
  }

  public NavH1 title()
  {
    return h1;
  }

  public NavList items()
  {
    return items;
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    String ctag = child.tag();
    if (ctag.equals(NavH1.TAG))
      return h1;
    else if (ctag.equals((NavList.TAG)))
      return items;
    else
      return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
  }

  public boolean hasH1()
  {
    return h1 != null && h1.hasCDataChars();
  }

  @Override
  public List3<OCDNode> children()
  {
    return nodes.setNonNull(hasH1() ? h1 : null, items);
  }

  @Override
  public String sticker()
  {
    return type;
  }
}
