package sugarcube.formats.ocd.objects.nav;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;

public class TocScript
{
  public List3<TocScriptItem> items = new List3<TocScriptItem>();

  public TocScript()
  {

  }

  public TocScript(String script)
  {
    parse(script, false);
  }

  public void clear()
  {
    this.items.clear();
  }
  
  public TocScriptItem[] array()
  {
    return items.toArray(new TocScriptItem[0]);
  }

  public TocScriptItem add(int level, String text, String href)
  {
    TocScriptItem item = new TocScriptItem(level, text, href);
    items.add(item);
    return item;
  }

  public TocScript parse(String script, boolean clear)
  {
    if (clear)
      this.clear();
    if (script == null || script.isEmpty())
      return this;
    for (String line : script.split("\\r?\\n"))
    {
      int i = line.indexOf('-');
      if (i < 0)
        continue;

      int level = 1;
      String link = null;

      while (++i < line.length() && line.charAt(i) == '-')
        level++;
      line = line.substring(i, line.length()).trim();

      i = line.indexOf("|");
      if (i > -1)
      {
        link = line.substring(0, i).trim();
        if (i + 1 < line.length())
          line = line.substring(i + 1, line.length()).trim();
      }
      this.add(level, line, link);
    }
    return this;
  }

  public void populateToc(OCDToc toc)
  {
    OCDNavItem ocdItem = toc;
    ocdItem.level = 0;
    for (TocScriptItem scriptItem : items)
    {
      while (scriptItem.level <= ocdItem.level && (ocdItem = ocdItem.parentItem()) != null)
        ;
      if (ocdItem == null)
      {
        Log.debug(this, ".populateToc - parentItem not found: " + ocdItem);
        ocdItem = toc;
        break;
      }
      if (scriptItem.level > ocdItem.level)
      {
        for (int i = ocdItem.level + 1; i < scriptItem.level; i++)
        {
          ocdItem = ocdItem.add(null, null);
          ocdItem.level = i;
        }
      }

      ocdItem = ocdItem.add(scriptItem.text, scriptItem.link);
      ocdItem.level = scriptItem.level;
    }
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(items.size() * 100);
    for (TocScriptItem item : items)
    {
      if (item.level == 1 && sb.length() > 0)
        sb.append("\n");
      sb.append(item.toString() + "\n");
    }

    return sb.toString();
  }
}
