package sugarcube.formats.ocd.objects.nav;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringMap;
import sugarcube.formats.ocd.objects.OCDNode;

public class OCDBookmarks extends OCDNavItem
{
  public static final String TAG = "bookmarks";
  
  protected OCDBookmarks(String tag, OCDNode parent)
  {
    super(tag, parent);
    level = 0;
  }

  public OCDBookmarks(OCDNode parent)
  {
    this(TAG, parent);
  }
  
  @Override
  public int level()
  {
    return 0;
  }
  
  public boolean isBookmarksRoot()
  {
    return true;
  }   
  
  @Override
  public boolean isRoot()
  {
    return true;
  }
  
  public List3<OCDNavItem> list()
  {
    return this.populate(new List3<OCDNavItem>());
  }

  public StringMap<OCDNavItem> linksMap()
  {
    return this.populateLinks(new StringMap<OCDNavItem>());
  }

  public OCDNavItem[] links()
  {
    return linksMap().values().toArray(new OCDNavItem[0]);
  }
  
  @Override
  public OCDBookmarks copy()
  {
    OCDBookmarks node = new OCDBookmarks(parent());
    copyTo(node);
    return node;
  }

  public void copyTo(OCDBookmarks node)
  {
    super.copyTo(node);
  }


}
