package sugarcube.formats.ocd.objects.document;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.system.io.Zipper;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDEntry;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OCDManifest extends OCDEntry implements Iterable<OCDItem>
{
  public static final String TAG = "manifest";
  // maps entryPath to ref
  protected StringMap<OCDItem> map = new StringMap<OCDItem>();

  public OCDManifest(OCDNode parent)
  {
    super(TAG, parent, "manifest.xml");
  }

  public OCDManifest addMimeTypeItem()
  {
    this.addItem(Zipper.MIMETYPE, Zipper.MIMETYPE, OCDItem.TYPE_MIME);
    return this;
  }

  public OCDManifest removeAllImages()
  {
    for (String key : map.keyArray())
    {
      OCDItem item = map.get(key);
      if (item != null && item.isImageType())
        map.remove(key);
    }
    return this;
  }

  @Override
  public OCDManifest clear()
  {
    for (OCDItem item : map)
      item.setParent(null);
    this.map.clear();
    return this;
  }

  public List3<OCDItem> list(String... types)
  {
    if (types == null || types.length == 0)
      return map.list();
    StringSet set = new StringSet(types);
    List3<OCDItem> list = new List3<OCDItem>();
    for (OCDItem item : map)
      if (set.contains(item.type()))
        list.add(item);
    return list;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    if (OCD.isTag(child, OCDItem.TAG))
      return new OCDItem(this);
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    if (OCD.isTag(child, OCDItem.TAG))
      addItem((OCDItem) child);
  }

  public boolean hasItem(String entryPath)
  {
    return this.map.has(entryPath);
  }

  public OCDItem getItem(String entryPath)
  {
    return this.map.get(entryPath);
  }

  public void addItem(OCDItem item)
  {
    item.setParent(this);
    this.map.put(item.filepath, item);
  }

  public void updateItem(OCDItem update)
  {
    OCDItem item = map.get(update.filepath);
    // Log.debug(this, ".updateItem - " + update.file + ": " + update.pages);
    if (item == null)
      addItem(update);
  }

  public OCDItem addItem(String entryPath, String type, String... properties)
  {
    OCDItem item = new OCDItem(this, entryPath, type, properties);
    this.addItem(item);
    return item;
  }

  public void removeItem(OCDItem item)
  {
    this.map.remove(item.filepath);
  }

  @Override
  public List<? extends OCDNode> children()
  {
    return map.list();
  }

  @Override
  public String sticker()
  {
    return tag();
  }

  public int size()
  {
    return map.size();
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
  }

  @Override
  public Iterator<OCDItem> iterator()
  {
    return this.map.values().iterator();
  }

  @Override
  public OCDManifest copy()
  {
    OCDManifest copy = new OCDManifest((OCDNode) parent);
    for (OCDItem item : this)
      copy.addItem(item.copy());
    return copy;
  }
}
