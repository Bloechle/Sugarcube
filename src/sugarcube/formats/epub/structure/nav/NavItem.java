
package sugarcube.formats.epub.structure.nav;

import sugarcube.formats.ocd.objects.OCDNode;

public class NavItem extends NavNode
{
  public static final String TAG = "li";

  public NavItem(OCDNode parent)
  {
    super(TAG, parent);
  }

  public NavList addList()
  {
    NavList ol = new NavList(this);
    this.addChild(ol);
    return ol;
  }

  public NavLink addLink(String href, String cdata, String... props)
  {
    NavLink link = new NavLink(this, href, cdata, props);
    this.addChild(link);
    return link;
  }
  
  public NavSpan addSpan(String text)
  {
    NavSpan span = new NavSpan(this, text);    
    this.addChild(span);    
    return span;
  }

}
