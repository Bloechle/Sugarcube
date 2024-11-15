package sugarcube.formats.ocd.objects.nav;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Ints;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.Base;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;
import java.util.Iterator;

public class OCDNavItem extends OCDNode implements Iterable<OCDNavItem>
{
  public static final String TAG_ITEM = "item";
  public transient boolean isExpanded = true;
  public transient int level = -1;
  public String type = "";
  protected List3<OCDNavItem> items = new List3<>();
  public String text;
  public String link;
  

  public OCDNavItem(String tag, OCDNode parent)
  {
    super(tag, parent);
  }

  public OCDNavItem(OCDNode parent)
  {
    this(TAG_ITEM, parent);
  }

  public OCDNavItem(OCDNode parent, String text, String link)
  {
    this(TAG_ITEM, parent);
    this.text = text;
    this.link = link;
  }

  public boolean delete()
  {
    OCDNode node = this.parent();
    if (node != null && node instanceof OCDNavItem)
    {
      OCDNavItem parent = (OCDNavItem) node;
      parent.items.remove(this);
      return true;
    }
    return false;
  }

  public OCDNavItem anchor(String anchor)
  {
    this.link += "#" + anchor;
    return this;
  }

  public String path(String sep)
  {
    Ints path = new Ints();
    OCDNavItem item = this;

    do
    {
      path.add(item.index() + 1);
    } while (!(item = item.parentItem()).isRoot());

    return path.reverse().toString(sep);
  }

  public int index()
  {
    OCDNavItem parent = this.parentItem();
    if (parent == null)
      return -1;
    int index = 0;
    for (OCDNavItem item : parent)
    {
      if (item == this)
        return index;
      index++;
    }
    return index;
  }

  @Override
  public String needID()
  {
    return id == null ? id = "i" + Base.x32.random8() : id;
  }

  public boolean isLevel(int level)
  {
    return level == level();
  }

  public int level()
  {
    if (level > -1)
      return level;

    int level = 1;
    OCDNavItem item = this;
    while (!(item = item.parentItem()).isRoot())
      level++;
    return this.level = level;
  }

  public OCDNavItem level(int level)
  {
    OCDNavItem item = this;
    while (level < item.level())
      item = item.parentItem();
    return item;
  }
  
  public boolean isNavRoot()
  {
    return OCDNavigation.TAG.equals(tag);
  }
  
  public boolean isPageListRoot()
  {
    return false;
  }
  
  public boolean isBookmarksRoot()
  {
    return false;
  }
  
  public boolean isTOCRoot()
  {
    return false;
  }

  public boolean isRoot()
  {
    return false;
  }

  public boolean isLeaf()
  {
    return items.isEmpty();
  }

  public List3<OCDNavItem> populate(List3<OCDNavItem> list)
  {
    for (OCDNavItem item : this)
    {
      list.add(item);
      item.populate(list);
    }
    return list;
  }

  public StringMap<OCDNavItem> populateLinks(StringMap<OCDNavItem> links)
  {
    for (OCDNavItem item : this)
    {
      if (item.hasLink() && !links.has(item.link))
        links.put(item.link, item);
      item.populateLinks(links);
    }
    return links;
  }

  public OCDNavItem parentItem()
  {
    return parent instanceof OCDNavItem ? (OCDNavItem) parent : null;
  }

  public OCDNavItem parentItem(int level)
  {
    OCDNavItem item = null;
    while ((item = parentItem()) != null && item.level != level)
      ;
    return item;
  }

  @Override
  public OCDNavItem clear()
  {
    this.items.clear();
    return this;
  }

  public boolean hasText()
  {
    return text != null && !text.isEmpty();
  }

  public boolean hasLink()
  {
    return link != null && !link.isEmpty();
  }

  public boolean hasFileLink()
  {
    return hasLink() && link.contains(".");
  }

  @Override
  public OCDNavItem setID(String id)
  {
    super.setID(id);
    this.props.put("id", id);
    return this;
  }

  public OCDNavItem add(String content, String href)
  {
    OCDNavItem item = new OCDNavItem(this, content, href);
    this.addChild(item);
    return item;
  }

  public OCDNavItem addChild(OCDNavItem item)
  {
    // Log.debug(this,".addChild - this="+this.tag+", child="+node.tag);
    item.setParent(this);
    this.items.add(item);
    return this;
  }

  public OCDNavItem addChild(OCDNavItem item, OCDNavItem anchor)
  {
    item.setParent(this);
    this.items.addAfter(item, anchor);
    return this;
  }

  // public OCDTocItem removeChild(OCDTocItem item)
  // {
  // this.items.remove(item);
  // return this;
  // }
  //
  // public OCDTocItem remove()
  // {
  // OCDTocItem parent = this.parentItem();
  // if(parent!=null)
  // parent.removeChild(this);
  // return this;
  // }

  public OCDNavItem addChildren(Collection<? extends OCDNavItem> children)
  {
    for (OCDNavItem child : children)
      this.addChild(child);
    return this;
  }

  public OCDNavItem addChildren(OCDNavItem... children)
  {
    for (OCDNavItem child : children)
      this.addChild(child);
    return this;
  }

  public OCDNavItem addAttribute(String key, String value)
  {
    return this.addAttributes(key, value);
  }

  public OCDNavItem addAttributes(String... props)
  {
    this.props.putAll(props);
    return this;
  }

  public OCDNavItem addCData(String cdata, boolean escaped)
  {
    this.props.escapeCData(escaped);
    this.props.putEmptyValue(cdata);
    return this;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    // Log.debug(this, ".writeAttributes - link="+link);
    xml.write("link", link);
    xml.write("text", text);
    props.writeAttributes(xml);
    return this.children();
  }

  @Override
  public XmlINode newChild(DomNode child)
  {    
    return new OCDNavItem(child == null ? "" : child.tag(), this);
  }

  @Override
  public List3<? extends OCDNavItem> children()
  {
    return items;
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    this.link = dom.value("link", link);
    this.text = dom.value("text", text);
    props.readAttributes(dom, true);
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child != null)
      this.addChild((OCDNavItem) child);
  }

  public final OCDNode setDoEscapeCData(boolean doEscape)
  {
    this.props.escapeCData(doEscape);
    return this;
  }

  public final boolean doEscapeCData()
  {
    return this.props.escapeCData();
  }

  public final OCDNode setCData(String cdata, boolean doEscape)
  {
    this.setCData(cdata);
    this.setDoEscapeCData(doEscape);
    return this;
  }

  public void setCData(String cdata)
  {
    this.props.setEmptyValue(cdata);
  }

  public String cdata()
  {
    return this.props.emptyValue();
  }

  public boolean hasCData()
  {
    return this.props.containsEmptyKey();
  }

  public String type()
  {
    return type;
  }

  @Override
  public String sticker()
  {
    return this.tag;
  }

  // public OCDNode lookFor(String tag, String type)
  // {
  // if (this.tag.equals(tag) && this.type.equals(type))
  // return this;
  // NavNode nav;
  // for (OCDNode node : nodes)
  // if ((nav = ((NavNode) node).lookFor(tag, type)) != null)
  // return nav;
  // return null;
  // }

  @Override
  public Iterator<OCDNavItem> iterator()
  {
    return items.iterator();
  }

  @Override
  public OCDNavItem copy()
  {
    OCDNavItem node = new OCDNavItem(parent());
    copyTo(node);
    return node;
  }

  public void copyTo(OCDNavItem node)
  {
    super.copyTo(node);
    node.level = this.level;
    node.type = this.type;
    node.text = text;
    node.link = link;
    for (OCDNavItem child : this)
      node.addChild(child.copy());
  }

  public StringBuilder script(StringBuilder sb, String prefix)
  {
    for (OCDNavItem item : this)
    {
      sb.append(prefix);
      sb.append(item.hasLink() ? " " + item.link + " |" : "");
      sb.append(item.hasText() ? " " + item.text : "");
      sb.append("\n");
      if (item.items.isPopulated())
        item.script(sb, prefix + "-");
      if (prefix.trim().length() == 1)
        sb.append("\n");
    }
    return sb;
  }

  @Override
  public String toString()
  {
    if (isRoot())
      return "Table of Contents";
    else if (isLeaf())
      return link;
    else
      return text;
  }

  public String xhtmlLink()
  {
    return link == null ? null : link.replace(".xml", ".xhtml");
  }
  
  public static void Group(OCDNavItem parent, OCDNavItem... items)
  {
    for (OCDNavItem item : items)
      item.delete(false);
    parent.addChildren(items);    
  }

  public static void main(String... args)
  {
    OCDNavItem item = new OCDNavItem(null, "bou\u0009", "http://www.sugarcube.ch");

    String xml = Xml.toString(item);

    Log.debug(OCDNavItem.class, " - " + xml);

    Xml.Load(item, xml);
  }

}