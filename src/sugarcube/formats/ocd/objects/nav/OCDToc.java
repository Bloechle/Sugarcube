package sugarcube.formats.ocd.objects.nav;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.system.io.File3;
import sugarcube.formats.ocd.objects.OCDNode;

public class OCDToc extends OCDBookmarks
{
  public static final String TAG = "table-of-contents";

  public OCDToc(OCDNode parent)
  {
    super(TAG, parent);
  }
  
  public boolean isBookmarksRoot()
  {
    return false;
  }
    
  public boolean isTOCRoot()
  {
    return true;
  }
  
  public OCDToc populateFlatTOC(String[] pages, String ext)
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

  public OCDToc completeTOC(String[] pages)
  {
    for (int i = 0; i < pages.length; i++)
      pages[i] = File3.Extense(pages[i], "");
    OCDNavItem[] links = this.links();
    for (int i = 0; i < links.length - 1; i++)
    {
      OCDNavItem l0 = links[i];
      OCDNavItem l1 = links[i + 1];
      String p0 = File3.Extense(l0.link, "");
      String p1 = File3.Extense(l1.link, "");
      if (!p0.equals(p1))
      {
        for (int j = 0; j < pages.length - 1; j++)
        {
          if (pages[j].equals(p0))
          {
            int k = j;
            StringList missing = new StringList();
            while (++k < pages.length && !pages[k].equals(p1))
            {
              missing.add(pages[k]);
            }
            OCDNavItem anchor = l0;
            if (missing.isPopulated())
            {
              OCDNavItem parent = (OCDNavItem) l0.parent();
              for (String filename : missing)
              {
                String text = filename.replace("-0000", "-").replace("-000", "-").replace("-00", "-").replace("-0", "-").replace("page-", "Page-");
                OCDNavItem item = new OCDNavItem(parent, text, filename + ".xhtml");
                parent.addChild(item, anchor);
                anchor = item;
              }
            }
            break;
          }
        }
      }
    }

    Log.debug(this, ".completeToc - links: " + links);
    return this;
  }

  public String script()
  {
    return script(new StringBuilder(), " -").toString();
  }

  public OCDToc parseScript(String script, boolean clear)
  {
    if (clear)
      this.clear();
    TocScript ts = new TocScript(script);
    ts.populateToc(this);
    return this;
  }

  // public OCDToc parseScript(String data, boolean clear)
  // {
  // if(clear)
  // this.clear();
  // this.level = 0;
  // if (data == null || data.isEmpty())
  // return this;
  // OCDTocItem item = this;
  // for (String line : data.split("\\r?\\n"))
  // {
  // int i = line.indexOf('-');
  // if (i < 0)
  // continue;
  //
  // int level = 1;
  // String href = null;
  //
  // while (++i < line.length() && line.charAt(i) == '-')
  // level++;
  // line = line.substring(i, line.length()).trim();
  //
  // i = line.indexOf("|");
  // if (i > -1)
  // {
  // href = line.substring(0, i).trim();
  // if (i + 1 < line.length())
  // line = line.substring(i + 1, line.length()).trim();
  // }
  //
  // while (level <= item.level && (item = item.parentItem()) != null)
  // ;
  //
  // if (item == null)
  // {
  // Log.debug(this, ".parseScript - parentItem not found: " + item);
  // item = this;
  // break;
  // }
  //
  // if (level > item.level)
  // {
  // for (i = item.level + 1; i < level; i++)
  // {
  // item = item.add(null, null);
  // item.level = i;
  // }
  //
  // }
  //
  // item = item.add(line, href);
  // item.level = level;
  // }
  //
  // Log.debug(this, ".parseScript - done: "+Xml.toString(this));
  //
  // return this;
  // }
  
  @Override
  public OCDToc copy()
  {
    OCDToc node = new OCDToc(parent());
    copyTo(node);
    return node;
  }

  public void copyTo(OCDBookmarks node)
  {
    super.copyTo(node);
  }

}
