package sugarcube.formats.ocd.objects.nav;

import sugarcube.common.system.io.File3;
import sugarcube.formats.ocd.objects.OCDNode;

public class OCDPageList extends OCDBookmarks
{
  public static final String TAG = "page-list";

  public OCDPageList(OCDNode parent)
  {
    super(TAG, parent);
  }
  
  public boolean isBookmarksRoot()
  {
    return false;
  }
    
  public boolean isPageListRoot()
  {
    return true;
  }
  
  public OCDPageList populate(String[] pages, String ext)
  {
    int counter = 0;
    this.clear();
    for (String page : pages)
    {
      if (ext != null && !ext.isEmpty())
        File3.Extense(page, ext);
      this.addChild(new OCDNavItem(this, "Page-" + (++counter), page));
    }
    return this;
  }

  public String script()
  {
    return script(new StringBuilder(), " -").toString();
  }
  
  @Override
  public OCDPageList copy()
  {
    OCDPageList node = new OCDPageList(parent());
    copyTo(node);
    return node;
  }

  public void copyTo(OCDBookmarks node)
  {
    super.copyTo(node);
  }

}
