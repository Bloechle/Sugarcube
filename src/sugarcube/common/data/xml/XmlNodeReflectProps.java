package sugarcube.common.data.xml;

import sugarcube.common.data.collections.Props;

import java.util.Collection;

public final class XmlNodeReflectProps extends XmlNodeReflect
{
  // used to lazily store xml attributes and cdata :-)
  protected Props props = new Props(2);// minimum init size

  public XmlNodeReflectProps(Object tag, XmlINode parent, String... props)
  {
    super(tag, parent);
    if (props != null)
      this.props.putAll(props);
  }

  public Props props()
  {
    return props;
  }
  
  @Override
  public boolean isLoaded()
  {
    return !props.isEmpty();
  }

  @Override
  public boolean isInactive()
  {
    return props.isEmpty();
  }

  public String cdata(String def)
  {
    return props.cdataValue(def);
  }

  @Override
  public Collection<? extends XmlNode> writeAttributes(Xml xml)
  {
    this.props.writeAttributes(xml);
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    this.props.readAttributes(dom, true);
  }
}