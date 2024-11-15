package sugarcube.formats.epub.structure.nav;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.objects.OCDNode;

public class NavHead extends NavNode
{
  public static final String TAG = "head";
  public NavMeta charset;
  public NavTitle title;

  public NavHead(NavDocument parent, String title)
  {
    super(TAG, parent);
    this.charset = new NavMeta(this);
    this.title = new NavTitle(this);    
    this.charset.addAttribute("charset","UTF-8");
    this.setTitle(title);
  }

  public void setTitle(String title)
  {
    this.title.setCData(title);
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    String ctag = child.tag();
    if (ctag.equals(NavMeta.TAG))
      return charset;
    else if (ctag.equals((NavTitle.TAG)))
      return title;
    else
      return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
  }

  @Override
  public List3<OCDNode> children()
  {
    return super.nodes = new List3<OCDNode>(charset, title);
  }
}
