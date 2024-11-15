package sugarcube.formats.epub.structure.ncx;

import sugarcube.common.data.collections.StringMap;
import sugarcube.common.system.io.File3;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;
import sugarcube.formats.ocd.objects.nav.OCDToc;

public class NCXNav extends NCXNavPoint
{
  private transient StringMap<Integer> playOrders = new StringMap<Integer>();
  private transient int navID = 0;
  private transient String lastLink;
  public OCDDocument ocd;

  public boolean flat = false;

  public NCXNav(OCDDocument ocd, OCDNavigation nav)
  {
    super("navMap");
    this.ocd = ocd;
    this.lastLink = File3.Extense(ocd.pageHandler.firstPage().entryFilename(), ".xhtml");
    OCDToc toc = nav.toc();
    this.populateTOC(0, this, toc);
  }

  public void populateTOC(int level, NCXNavPoint point, OCDNavItem node)
  {
    for (OCDNavItem item : node)
    {
      String label = null;
      label = item.text;
      if (item.hasLink())
        lastLink = File3.Extense(item.link, ".xhtml");
      label = label == null || label.isEmpty() ? "***" : label;

      NCXNavPoint childPoint = new NCXNavPoint(navID(), playOrder(lastLink), lastLink, label);
      point.addChild(childPoint);

      populateTOC(level, childPoint, item);
    }
  }

  public String navID()
  {
    return "nav-" + (++navID);
  }

  public int playOrder(String filename)
  {
    if (filename == null || filename.isEmpty())
      return playOrders.size();
    if (!playOrders.has(filename))
      playOrders.put(filename, (playOrders.size() + 1));
    return playOrders.get(filename);
  }

}
