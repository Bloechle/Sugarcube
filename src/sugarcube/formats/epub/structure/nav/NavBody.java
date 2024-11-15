package sugarcube.formats.epub.structure.nav;

import sugarcube.common.data.xml.DomNode;

public class NavBody extends NavNode
{
  public static final String TAG = "body";
  private NavToc toc;
  private NavLandmarks landmarks;
  private NavPageList pageList;

  public NavBody(NavDocument parent)
  {
    super(TAG, parent);
  }

  public NavToc needToc()
  {
    if (toc == null)
      addChildren(toc = new NavToc(this));
    return toc;
  }

  public NavLandmarks needLandmarks()
  {
    if (landmarks == null)
      addChildren(landmarks = new NavLandmarks(this));
    return landmarks;
  }

  public NavPageList needPageList()
  {
    if (pageList == null)
      addChildren(pageList = new NavPageList(this));
    return pageList;
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    this.nodes.clear();
    super.readAttributes(dom);
  }
}
