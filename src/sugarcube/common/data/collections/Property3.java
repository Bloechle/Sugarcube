package sugarcube.common.data.collections;

import sugarcube.common.system.reflection.Bean;
import sugarcube.common.data.xml.*;

import java.util.Collection;

public class Property3 extends XmlNode implements Xmlizer
{
  public static final String TAG = "prop";
  public static final String EMPTY_KEY = "";// e.g.: cdata when used for XML
                                            // attributes
  public String key;
  public String value;

  public Property3()
  {
    super(TAG, null);
  }

  public Property3(XmlINode parent)
  {
    super(TAG, parent);
    this.parent = parent;
  }

  public Property3(String key, Object value)
  {
    this(null, key, value);
  }

  public Property3(XmlINode parent, String key, Object value)
  {
    super(TAG, parent);
    this.key = key;
    this.value = value == null ? null : value.toString();
  }

  public boolean isCssKey()
  {
    return this.key.endsWith(":");
  }

  public boolean isEmptyKey()
  {
    return this.key.equals(EMPTY_KEY);
  }

  public boolean isBoolean()
  {
    return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
  }

  public boolean booleanValue()
  {
    return value.equalsIgnoreCase("true");
  }

  public boolean isInteger()
  {
    return Nb.isInteger(value);
  }

  public int intValue()
  {
    return Nb.Int(value);
  }

  public boolean isReal()
  {
    return Nb.isReal(value);
  }

  public float realValue()
  {
    return Nb.Float(value);
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

  public Property3 copy()
  {
    return new Property3(parent, key, value);
  }

  public Property3 copy(Bean parent)
  {
    return new Property3(parent, key, value);
  }

  public String stringLabel()
  {
    return key + "[" + value + "]";
  }

  @Override
  public String toString()
  {
    return key;
  }

  @Override
  public Collection<? extends XmlNode> children()
  {
    return null;
  }

  @Override
  public XmlINode parent()
  {
    return parent;
  }

  @Override
  public String tag()
  {
    return TAG;
  }

  @Override
  public String sticker()
  {
    return key;
  }

  @Override
  public Xmlizer xmlizer()
  {
    return this;
  }

  @Override
  public Collection<? extends XmlINode> writeAttributes(Xml xml)
  {
    xml.write("name", key);
    if (value != null)
      xml.writeCData(CharRef.Html(value.toString()), false);
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
    this.key = e.contains("name") ? e.value("name") : e.value("key");
    this.value = e.contains("value") ? e.value("value") : e.cdata();
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
  }

  @Override
  public boolean equals(Object o)
  {
    if (o == null || !o.getClass().isInstance(this))
      return false;
    final Property3 that = (Property3) o;
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
  public void setParent(XmlINode parent)
  {
    this.parent = parent;
  }

  public String string()
  {
    return this.key + "=" + this.value;
  }

}
