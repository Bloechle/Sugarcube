package sugarcube.formats.epub.structure;

import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Rectangle3;

public class EPubLinks extends List3<EPubLinkBox>
{

  public EPubLinks()
  {
  }

  public EPubLinkBox addLink(String url, Rectangle3 box, String target)
  {
    EPubLinkBox link = new EPubLinkBox(url, box, target);
    this.add(link);
    return link;
  }

}
