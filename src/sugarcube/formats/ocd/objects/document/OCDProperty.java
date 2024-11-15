package sugarcube.formats.ocd.objects.document;

import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class OCDProperty extends OCDNode
{
  public static final String TAG = "prop";
  public String key;
  public String value;

  public OCDProperty(OCDNode parent)
  {
    super(TAG, parent);
  }

  public OCDProperty(OCDNode parent, String key, Object value)
  {
    this(parent);
    this.key = key;
    this.value = value == null ? null : value.toString();
  }

  public String key()
  {
    return this.key;
  }

  public String value()
  {
    return this.value;
  }

  public void setValue(Object value)
  {
    this.value = value.toString();
  }

  public boolean isValue(String value)
  {
    return this.value.equals(this.value);
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("key", key);
    xml.writeCData(value.toString());
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    this.key = dom.contains("name") ? dom.value("name") : dom.value("key", key);
    this.value = dom.contains("value") ? dom.value("value") : dom.cdata();
  }

  @Override
  public boolean equals(Object o)
  {
    if (o == null || !o.getClass().isInstance(this))
      return false;
    final OCDProperty that = (OCDProperty) o;
    if (this.key == null ? that.key != null : !this.key.equals(that.key))
      return false;
    if (this.value != that.value && (this.value == null || !this.value.equals(that.value)))
      return false;
    return true;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 83 * hash + (this.key != null ? this.key.hashCode() : 0);
    hash = 83 * hash + (this.value != null ? this.value.hashCode() : 0);
    return hash;
  }

  @Override
  public String sticker()
  {
    return key + "[" + value + "]";
  }

  @Override
  public String toString()
  {
    return sticker();
  }
}
