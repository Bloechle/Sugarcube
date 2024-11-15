package sugarcube.common.data.xml;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import sugarcube.common.system.reflection.Annot._Xml;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.interfaces.Taggable;
import sugarcube.common.interfaces.Valuable;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.reflection.Reflect;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DomNode implements Iterable<DomNode>, Taggable, Valuable
{
  private final Node node;
  private StringSet read = new StringSet();

  public DomNode(Node node)
  {
    this.node = node;
  }

  public StringSet remainingAttributes()
  {
    StringSet attributes = this.attributes();
    // Log.debug(this, ".remainingAttributes - "+attributes+": "+read);
    attributes.removeAll(read);
    return attributes;
  }

  public StringSet attributes()
  {
    StringSet attributes = new StringSet();
    NamedNodeMap map = element().getAttributes();
    for (int i = 0; i < map.getLength(); i++)
    {
      Node attNode = map.item(i);
      if (attNode.getNodeType() == Node.ATTRIBUTE_NODE)
        attributes.add(attNode.getNodeName());
    }
    return attributes;
  }
  
  public Props props()
  {
    Props props = new Props();
    NamedNodeMap map = element().getAttributes();
    for (int i = 0; i < map.getLength(); i++)
    {
      
      Node attNode = map.item(i);
      if (attNode.getNodeType() == Node.ATTRIBUTE_NODE)
        props.put(attNode.getNodeName(), attNode.getTextContent());
    }
    return props;
  }

  public String cdata()
  {
    try
    {
      return node.getTextContent();
    } catch (Exception e)
    {
      return null;
    }
  }

  public String cdata(String def)
  {
    // be carefull, retrieves all cdata even if node contains child elements
    // need to call hasCData() to be sure it's pure text data
    String cdata = cdata();
    return cdata == null || cdata.trim().isEmpty() ? def : cdata;
  }

  public boolean hasChildNodes()
  {
    return node.hasChildNodes();
  }

  public boolean hasOnlyCData()
  {
    Node child = node.getFirstChild();
    while (child != null)
    {
      if (child.getNodeType() == Node.ELEMENT_NODE)
        return false;
      child = child.getNextSibling();
    }
    return true;
  }

  public Element element()
  {
    return (Element) node;
  }

  public short type()
  {
    return node.getNodeType();
  }

  private static boolean isNode(Node node)
  {
    return node.getNodeType() == Node.ELEMENT_NODE;
    // switch (node.getNodeType())
    // {
    // case Node.ELEMENT_NODE:
    // return true;
    // case Node.TEXT_NODE:
    // case Node.CDATA_SECTION_NODE:
    // return !node.getNodeValue().trim().isEmpty();
    // default:
    // return false;
    // }
  }

  private static boolean isCData(Node node)
  {
    switch (node.getNodeType())
    {
    case Node.TEXT_NODE:
    case Node.CDATA_SECTION_NODE:
      return !node.getNodeValue().trim().isEmpty();
    default:
      return false;
    }
  }

  private static boolean isElement(Node node)
  {
    return node.getNodeType() == Node.ELEMENT_NODE;
  }

  public boolean isNode()
  {
    return isNode(node);
  }

  public boolean isCData()
  {
    return isCData(node);
  }

  public boolean isElement()
  {
    return isElement(node);
  }

  public static DomNode Load(String path)
  {
    if (path == null)
      return null;  
    DomNode element = File3.Exists(path) ? Load(new File(path)) : null;
    if (element == null)
    {
      InputStream stream = IO.load(path);
      element = Load(stream);
      if(element==null)
        Log.debug(DomNode.class,  ".Load - null element: "+path);
      IO.Close(stream);
    }
    return element;
  }

  public static DomNode Load(File file)
  {
    try
    {
      return new DomNode(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getDocumentElement());
    } catch (Exception ex)
    {
      Log.warn(DomNode.class, ".Load - failed:" + ex.getMessage());
      return null;
    }
  }

  public static DomNode Load(InputStream stream)
  {
    try
    {
      return new DomNode(DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(stream instanceof BufferedInputStream ? stream : new BufferedInputStream(stream)).getDocumentElement());
    } catch (Exception ex)
    {
      ex.printStackTrace();
      Log.warn(DomNode.class, ".Load - failed: stream=" + stream + " message=" + ex.getMessage());
      return null;
    }
  }

  public boolean exists(String name)
  {
    return !isCData() && element().hasAttribute(name);
  }

  private class ElementIterator implements Iterator<DomNode>
  {
    private DomNode next;

    public ElementIterator()
    {
      Node node = DomNode.this.node.getFirstChild();
      while (node != null && !isNode(node))
        node = node.getNextSibling();
      this.next = node == null ? null : new DomNode(node);
    }

    @Override
    public DomNode next()
    {
      Node node = next.node.getNextSibling();
      while (node != null && !isNode(node))
        node = node.getNextSibling();
      DomNode element = next;
      this.next = node == null ? null : new DomNode(node);
      return element;
    }

    @Override
    public boolean hasNext()
    {
      return next != null;
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }

  @Override
  public Iterator<DomNode> iterator()
  {
    return new ElementIterator();
  }

  public List<DomNode> getChildren()
  {
    List<DomNode> list = new LinkedList<>();
    for (DomNode element : this)
      list.add(element);
    return list;
  }

  public List<? extends DomNode> getChildren(String name)
  {
    List<DomNode> list = new LinkedList<>();
    for (DomNode element : this)
      if (element.isTag(name))
        list.add(element);
    return list;
  }

  public boolean is(Object name)
  {
    return this.isTag(name);
  }

  public boolean isTag(Object name)
  {
    return isElement() ? element().getTagName().equals(name.toString()) : false;
  }

  @Override
  public String tag()
  {
    return isElement() ? element().getTagName() : "";
  }

  public boolean has(String name)
  {
    return this.contains(name);
  }

  public boolean hasNonEmpty(String name)
  {
    return this.contains(name) && !Zen.isVoid(this.value(name));
  }

  public boolean contains(String name)
  {
    return name.equals(XmlINode.CDATA) || (isElement() && element().hasAttribute(name));
  }

  public String id()
  {
    return this.value(Xml.TAG_ID, null);
  }

  public String id(String def)
  {
    return this.value(Xml.TAG_ID, def);
  }

  @Override
  public String value(String name)
  {
    return value(name, "");
  }

  public String value(String name, String def)
  {
    if (name.equals(XmlINode.CDATA))
      return cdata();
    else if (isElement())
    {
      // Log.debug(this, ".value - read: "+name);
      this.read(name);
      String value = element().getAttribute(name);
      return value.isEmpty() ? def : value;
    } else
      return def;
  }

  private DomNode read(String name)
  {
    this.read.add(name);
    return this;
  }

  public boolean is(String name, String value, String... alternatives)
  {
    if (value(name).equals(value))
      return true;
    for (String alt : alternatives)
      if (value(name).equals(alt))
        return true;
    return false;
  }

  public String[] values(String name)
  {
    String value = value(name);
    return value == null || value.isEmpty() ? new String[0] : Xml.unescapeWhitespaces(value.split("\\s+"));
  }

  public String[] values(String name, String separator)
  {
    return value(name).split(separator);
  }

  @Override
  public boolean bool(String name)
  {
    return bool(name, true);
  }

  public boolean bool(String name, boolean def)
  {
    String v = value(name);
    if (v == null || v.isEmpty())
      return def;
    else
      try
      {
        return Boolean.parseBoolean(v);
      } catch (NumberFormatException e)
      {
        Log.warn(this, ".bool - string parsing exception: " + e);
        return def;
      }
  }

  public boolean[] bools(String name, boolean... def)
  {
    if (this.contains(name))
    {
      String[] v = values(name);
      boolean[] n = new boolean[v.length];
      for (int i = 0; i < v.length; i++)
      {
        v[i] = v[i].trim().toLowerCase();
        if (v[i].length() == 1)
          n[i] = v[i].equals("1") || v[i].equals("t");
        else
          n[i] = Nb.Bool(v[i]);
      }
      return n;
    } else
      return def;
  }

  public boolean[] bools(boolean... def)
  {
    return bools(XmlINode.CDATA, def);
  }

  @Override
  public int integer(String name)
  {
    return integer(name, 0);
  }

  public int integer(String name, int def)
  {
    return Nb.Int(value(name), def);
  }

  public int[] integers(String name, int... def)
  {
    if (this.contains(name))
    {
      String[] v = values(name);
      int[] n = new int[v.length];
      for (int i = 0; i < v.length; i++)
        n[i] = Nb.Int(v[i], i < def.length ? def[i] : 0);
      return n;
    }
    return def;
  }

  public int[] integers(int... def)
  {
    return integers(XmlINode.CDATA, def);
  }

  @Override
  public float real(String name)
  {
    return real(name, 0.0);
  }

  public float real(String name, double def)
  {
    return Nb.Float(value(name), def);
  }

  public float[] reals(String name, float... def)
  {
    if (this.contains(name))
    {
      String[] v = values(name);      
      float[] d = new float[v.length];
      for (int i = 0; i < v.length; i++)
        d[i] = v[i].trim().equals("-") ? Float.NaN : Nb.Float(v[i], i < def.length ? def[i] : 0);
      return d;
    }
    return def;
  }

  public float[] reals(float... data)
  {
    return reals(XmlINode.CDATA, data);
  }

  public int hexa(String name)
  {
    return hexa(name, 0);
  }

  public int hexa(String name, int def)
  {
    try
    {
      return this.contains(name) ? (int) Long.parseLong(value(name), 16) : def;
    } catch (NumberFormatException e)
    {
      Log.warn(this, ".hexa - string parsing exception: " + e);
      return def;
    }
  }

  public DomNode readField(Field field, XmlINode node)
  {
    String key = field.getName();
    if (field.isAnnotationPresent(_Xml.class))
    {
      _Xml annot = (_Xml) field.getAnnotation(_Xml.class);
      if (annot.key() != null && !annot.key().trim().isEmpty())
        key = annot.key();
      if (annot.ns() != null && !annot.ns().trim().isEmpty())
        key = annot.ns() + ":" + key;
    }
    try
    {
      if (this.hasNonEmpty(key))
        Reflect.Set(field, node, this.value(key));
      else
        this.read(key);
    } catch (Exception e)
    {
      e.printStackTrace();
      return this;
    }
    return this;
  }

  @Override
  public String toString()
  {
    return this.tag();
  }

}
