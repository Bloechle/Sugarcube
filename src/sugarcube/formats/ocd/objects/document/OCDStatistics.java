package sugarcube.formats.ocd.objects.document;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringOccurrences;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OCDStatistics extends OCDNode implements Iterable<OCDStat>
{
  public static final String TAG = "statistics";
  // maps entryPath to ref
  protected List3<OCDStat> list = new List3<OCDStat>();

  public OCDStatistics(OCDNode parent)
  {
    super(TAG, parent);
  }
  
  public OCDStatistics addAll(String type, StringOccurrences stats)
  {
    for(String key: stats.keys())
    {
      this.addStat(type, key, "chars", ""+stats.get(key));
    }
    
    return this;
  }

  @Override
  public OCDStatistics clear()
  {
    for (OCDStat stat : list)
      stat.setParent(null);
    this.list.clear();
    return this;
  }
//
//  public List3<OCDStat> list(String... types)
//  {
//    if (types == null || types.length == 0)
//      return list;
//    Set8 set = new Set8(types);
//    List3<OCDStat> subList = new List3<OCDStat>();
//    for (OCDStat stat : list)
//      if (set.contains(stat.type()))
//        subList.add(stat);
//    return subList;
//  }

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
    if (OCD.isTag(child, OCDStat.TAG))
      return new OCDStat(this);
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    if (OCD.isTag(child, OCDStat.TAG))
      addStat((OCDStat) child);
  }

  public void addStat(OCDStat stat)
  {
    stat.setParent(this);
    this.list.add(stat);
  }

  public OCDStat addStat(String... properties)
  {
    OCDStat stat = new OCDStat(this, properties);
    this.addStat(stat);
    return stat;
  }

  public void remove(OCDStat stat)
  {
    this.list.remove(stat);
  }

  @Override
  public List<? extends OCDNode> children()
  {
    return list;
  }

  @Override
  public String sticker()
  {
    return tag();
  }

  public int size()
  {
    return list.size();
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
  }

  @Override
  public Iterator<OCDStat> iterator()
  {
    return this.list.iterator();
  }

  @Override
  public OCDStatistics copy()
  {
    OCDStatistics copy = new OCDStatistics((OCDNode) parent);
    for (OCDStat stat : this)
      copy.addStat(stat.copy());
    return copy;
  }
}
