package sugarcube.formats.ocd.objects.handlers;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.ZipItem;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDEntry;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.document.OCDItem;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class OCDHandler<T extends OCDEntry> extends OCDNode implements Iterable<T>
{
  protected StringMap<T> map = new StringMap<>();
  protected OCDDocument ocd;

  public OCDHandler(String tag, OCDDocument ocd)
  {
    super(tag, ocd);
    this.ocd = ocd;
  }

  @Override
  public synchronized Collection<? extends OCDNode> children()
  {
    return map.values();
  }

  public boolean has(String ref)
  {
    return map.has(ref) || map.has(File3.Filename(ref, true));
  }

  public boolean contains(String ref)
  {
    return has(ref);
  }

  public T get(String ref)
  {
    return map.has(ref) ? map.get(ref) : map.get(File3.Filename(ref, true));
  }

  public synchronized T add(T entry)
  {
    map.put(entry.entryFilename(), entry);
    return entry;
  }

  public synchronized T add(T entry, int index)
  {
    if (index == map.size())
      map.put(entry.entryFilename(), entry);
    else
    {
      StringMap<T> remap = new StringMap<T>();
      int i = 0;
      for (T e : map.values())
      {
        if (index == i++)
          remap.put(entry.entryFilename(), entry);
        remap.put(e.entryFilename(), e);
      }
      map.clear();
      map.putAll(remap);
    }
    return entry;
  }

  public synchronized void move(String key, String anchor)
  {
    if (Zen.equals(key, anchor))
      return;
    T entry = map.get(key, null);
    if (entry == null)
      Log.debug(this, ".move - entry not found: " + key);
    else
    {
      StringMap<T> remap = new StringMap<>();
      if (anchor == null)
        remap.put(key, entry);
      for (Map.Entry<String, T> e : map.entrySet())
      {
        if (Zen.equals(e.getKey(), key))
          continue;

        remap.put(e.getKey(), e.getValue());
        if (Zen.equals(e.getKey(), anchor))
          remap.put(key, entry);
      }
      map.clear();
      map.putAll(remap);
    }
  }

  public synchronized T remove(T entry)
  {
    return map.remove(entry.entryFilename());
  }

  public synchronized T remove(String entryFilename)
  {
    return map.remove(entryFilename);
  }

  public synchronized T addEntry(OCDItem item)
  {
    T entry = addEntry(document().zipEntry(item.filePath()));
    // entry.addPages(item.pages());
    return entry;
  }

  public abstract T addEntry(ZipItem entry);

  public Collection<T> values()
  {
    return map.values();
  }

  @Override
  public OCDHandler clear()
  {
    map.clear();
    return this;
  }

  public StringMap<T> map()
  {
    return map;
  }

  public int size()
  {
    return map.size();
  }

  @Override
  public OCDDocument document()
  {
    return ocd;
  }

  @Override
  public Iterator<T> iterator()
  {
    return map.values().iterator();
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
  }
}
