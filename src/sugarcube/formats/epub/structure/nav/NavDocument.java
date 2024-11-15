package sugarcube.formats.epub.structure.nav;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.interfaces.Visitor;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.objects.OCDEntry;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class NavDocument extends OCDEntry implements NavDocumentAccessor
{
  public static final String TAG = "html";
  private String tocTitle = "Table of Contents";
  private String landmarksTitle = "Guide";
  protected NavHead head;
  protected NavBody body;


  public NavDocument(OCDNode parent, String title)
  {
    super(TAG, parent, "nav.xhtml");
    this.head = new NavHead(this, Str.IfVoid(title,  tocTitle));
    this.body = new NavBody(this);    
  }

  @Override
  public NavDocument navigation()
  {
    return this;
  }


  // public NavToc toc()
  // {
  // return (NavToc) (body.lookFor(NavToc.TAG, NavToc.TYPE_TOC));
  // }
  //
  // public NavLandmarks landmarks()
  // {
  // return (NavLandmarks) body.lookFor(NavLandmarks.TAG,
  // NavLandmarks.TYPE_LANDMARKS);
  // }

  @Override
  public List3<OCDNode> children()
  {
    return new List3<OCDNode>(head, body);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("xmlns", "http://www.w3.org/1999/xhtml");
    xml.write("xmlns:epub", "http://www.idpf.org/2007/ops");
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    String ctag = child.tag();
    if (ctag.equals(NavHead.TAG))
      return head;
    else if (ctag.equals((NavBody.TAG)))
      return body;
    else
      return null;
  }

  public NavLink[] links()
  {
    final List3<NavLink> links = new List3<>();
    Visitor<XmlINode> visitor = new Visitor<XmlINode>()
    {
      @Override
      public boolean visit(XmlINode o)
      {

        if (o instanceof NavLink)
        {
          NavLink html = (NavLink) o;
          String href = html.props().get("href", null);
          if (href != null && !href.isEmpty())
          {
            links.add(html);
          }
        }
        return false;
      }
    };

    Xml.VisitTree(body, visitor);
    return links.toArray(new NavLink[0]);
  }

  @Override
  public void endChild(XmlINode child)
  {
  }

  @Override
  public String sticker()
  {
    return "navigation";
  }
}
