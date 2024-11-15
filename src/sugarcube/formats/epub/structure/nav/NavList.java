
package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;

public class NavList extends NavNode
{
  public static final String TAG = "ol";

  public NavList(OCDNode parent)
  {
    super(TAG, parent);
  }

  public NavItem addItem()
  {
    NavItem li = new NavItem(this);
    this.addChild(li);
    return li;
  }

  public NavItem addLinkItem(String href, String cdata, String... props)
  {
    NavItem li = addItem();
    li.addLink(href, cdata, props);
    return li;
  }

}
