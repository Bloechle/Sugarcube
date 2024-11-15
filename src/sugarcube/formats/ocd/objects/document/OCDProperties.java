package sugarcube.formats.ocd.objects.document;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Props;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;
import java.util.Iterator;

public class OCDProperties extends OCDNode implements Iterable<OCDProperty>
{
  public static final String TAG = "properties";
  protected Props props = new OCDProps();

  public OCDProperties(OCDNode parent)
  {
    super(TAG, parent);
  }

  public OCDProperties(OCDNode parent, Props props)
  {
    this(parent);
    this.props = props;
  }

  @Override
  public Props props()
  {
    return this.props;
  }

  public boolean has(String key)
  {
    return this.props.has(key);
  }

  public boolean hasnt(String key)
  {
    return this.props.hasnt(key);
  }

  public String get(String key)
  {
    return this.props.get(key);
  }

  public String get(String key, String recover)
  {
    return this.props.get(key, recover);
  }

  public OCDProperties put(String key, Object value)
  {
    this.props.put(key, value);
    return this;
  }

  public OCDProperties put(OCDProperty prop)
  {
    return this.put(prop.key, prop.value);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    return children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    if (OCD.isTag(child, OCDProperty.TAG))
      return new OCDProperty(this);
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    else if (OCD.isTag(child, OCDProperty.TAG))
      this.put((OCDProperty) child);
  }

  @Override
  public Collection<OCDProperty> children()
  {
    List3<OCDProperty> list = new List3<OCDProperty>();
    for (String key : props.keySet())
      list.add(new OCDProperty(this, key, props.get(key)));
    return list;
  }

  @Override
  public Iterator<OCDProperty> iterator()
  {
    return this.children().iterator();
  }

  public void write(StringBuilder sb)
  {
    Xml xml = new Xml(sb);
    xml.write(this);
    return;
  }

  @Override
  public String sticker()
  {
    return "Properties["+this.props.size()+"]";
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
  }

  @Override
  public String toString()
  {
    return Xml.toString(this);
  }
}
