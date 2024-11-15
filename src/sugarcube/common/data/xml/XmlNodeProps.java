package sugarcube.common.data.xml;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.StringSet;

import java.util.Collection;
import java.util.Iterator;

public class XmlNodeProps extends XmlNode implements Xmlizer, Iterable<XmlNodeProps>
{
  protected XmlProps props = new XmlProps();
  protected XmlNodePropsList children = new XmlNodePropsList();

  public XmlNodeProps(String tag)
  {
    super(tag, null);
  }

  public XmlNodeProps(String tag, XmlNodeProps parent)
  {
    super(tag, parent);
  }

  public XmlNodeProps(String tag, String... props)
  {
    super(tag, null);
    this.addAttributes(props);
  }
  
  public XmlProps props()
  {
    return props;
  }

  public XmlNodePropsList list()
  {
    return children;
  }

  @Override
  public Collection<? extends XmlNode> children()
  {
    return children;
  }

  public XmlNodeProps firstChild()
  {
    return this.children.first();
  }

  public XmlNodeProps secondChild()
  {
    return this.children.second();
  }

  public XmlNodeProps thirdChild()
  {
    return this.children.third();
  }

  // since meta is often used in Xml descriptions
  public XmlNodeProps addMeta(String name, String content)
  {
    return this.addChild("meta", "name", name, "content", content);
  }

  public XmlNodeProps addChild(String tag, String... props)
  {
    return this.addChildren(new XmlNodeProps(tag, props));
  }

  public XmlNodeProps addChild(XmlNodeProps child)
  {
    return this.addChildren(child);
  }

  public XmlNodeProps addChildren(XmlNodeProps... children)
  {
    for (XmlNodeProps child : children)
    {
      if (child != null)
      {
        this.children.add(child);
        child.setParent(this);
      } else
        Log.debug(this, ".addChildren - null children: " + this.tag);
    }
    return this;
  }

  public XmlNodeProps addAttribute(String key, String value)
  {
    return this.addAttributes(key, value);
  }

  public XmlNodeProps addAttributes(String... props)
  {
    this.props.putAll(props);
    return this;
  }

  @Override
  public Xmlizer xmlizer()
  {
    return this;
  }

  @Override
  public Collection<? extends XmlINode> writeAttributes(Xml xml)
  {
    props.writeAttributes(xml);
    return this.children();
  }

  @Override
  public final void readAttributes(DomNode dom)
  {
    props.readAttributes(dom);
  }

  @Override
  public XmlNodeProps newChild(DomNode child)
  {
    XmlNodeProps content = new XmlNodeProps(child.tag(), this);
    if (child.hasOnlyCData())
      content.setCData(child.cdata());
    return content;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child != null && child instanceof XmlNodeProps)
      this.addChild((XmlNodeProps) child);
  }

  public final XmlNodeProps setDoEscapeCData(boolean doEscape)
  {
    this.props.escapeCData(doEscape);
    return this;
  }

  public final boolean doEscapeCData()
  {
    return this.props.escapeCData();
  }

  public final XmlNodeProps setCData(String cdata, boolean doEscape)
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

  @Override
  public Iterator<XmlNodeProps> iterator()
  {
    return children.iterator();
  }

  public XmlNodePropsList getAll(String... tags)
  {
    return getAll(new XmlNodePropsList(), tags);
  }

  public XmlNodePropsList getAll(XmlNodePropsList list, String... tags)
  {
    StringSet set = new StringSet(tags);
    for (XmlNodeProps child : this)
    {
      if (set.isEmpty() || set.has(child.tag))
        list.add(child);
      else
        child.getAll(list, tags);
    }

    return list;
  }

}
