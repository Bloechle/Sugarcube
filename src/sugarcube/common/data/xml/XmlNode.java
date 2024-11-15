package sugarcube.common.data.xml;

import sugarcube.common.system.reflection.Annot._Xml;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.interfaces.Visitor;
import sugarcube.common.system.io.IO;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDEntry;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class XmlNode implements XmlINode, Unjammable
{
  protected static final List<XmlNode> NO_CHILD = Collections.emptyList();
  public final String tag;
  protected XmlINode parent;
  protected String id;

  public XmlNode(XmlINode parent)
  {    
    this(null, parent);
  }

  // needs a non-null tag value since tag field is final (uses
  // object.toString())
  public XmlNode(Object tag, XmlINode parent)
  {
    this.tag = checkTag(tag);
    this.parent = parent;
    this.id = null;
    // Log.debug(this,
    // " - tag="+this.tag+", class="+this.getClass().getSimpleName());
  }
  
  public String cast()
  {
    return this.getClass().getSimpleName();
  }

  protected final String checkTag(Object tag)
  {
    if (tag != null)
      return tag.toString();
    Class cls = this.getClass();
    _Xml annot = cls.isAnnotationPresent(_Xml.class) ? (_Xml) cls.getAnnotation(_Xml.class) : null;
    if (annot != null && !Zen.isVoid(annot.tag()))
      return annot.tag();
    try
    {
      for (Field field : this.getClass().getFields())
        if (field.getName().equals("TAG") && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
          return field.get(this).toString();
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return cls.getSimpleName();
  }

  public boolean isAnnotated()
  {
    return false;
  }

  public boolean isVisible()
  {
    return true;
  }

  // public int index()
  // {
  // int index = 0;
  // if (parent != null)
  // for (Object node : parent.children())
  // {
  // if (node == this)
  // return index;
  // index++;
  // }
  // return -1;
  // }

  public XmlINode parent(String tag)
  {
    XmlINode node = this;
    while ((node = node.parent()) != null)
      if (node.tag().equals(tag))
        return node;
    return null;
  }

  public void ensureParents()
  {
    if (this.parent instanceof XmlNode)
      this.ensureParents((XmlNode) this.parent);
  }

  private void ensureParents(XmlNode parent)
  {
    this.parent = parent;
    for (XmlNode node : children())
      node.ensureParents(this);
  }

  // public void visit(Visitable<XmlNode> visitor, Bool doStop)
  // {
  // visitor.visit(this, doStop);
  // for (XmlNode node : children())
  // if (doStop.isFalse())
  // node.visit(visitor, doStop);
  // }

  public XmlNode setID(String id)
  {
    this.id = id;
    return this;
  }

  public String id()
  {
    return this.id;
  }

  public boolean isID(String id)
  {
    return Zen.equals(this.id, id);
  }

  public XmlINode root()
  {
    XmlINode root = this;
    while (root.parent() != null)
      root = root.parent();
    return root;
  }

  @Override
  public Collection<? extends XmlNode> children()
  {
    return NO_CHILD;
  }

  public synchronized List3<XmlNode> syncChildren()
  {
    List3<XmlNode> list = new List3<>();
    Collection<? extends XmlNode> children = children();
    if (children != null)
      list.addAll(children);
    return list;
  }

  public int nbOfChildren()
  {
    return children().size();
  }

  @Override
  public XmlINode parent()
  {
    return parent;
  }

  @Override
  public void setParent(XmlINode parent)
  {
    this.parent = parent;
  }

  public void adopt(XmlINode child)
  {
    child.setParent(this);
  }

  public boolean hasParent()
  {
    return parent != null;
  }

  public XmlINode[] treePath()
  {
    List<XmlINode> path = new LinkedList<>();
    XmlINode node = this;
    path.add(this);
    while (node.parent() != null)
      path.add(node = node.parent());
    Collections.reverse(path);
    return path.toArray(new XmlNode[0]);
  }

  // public boolean is(Taggable... tags)
  // {
  // for (Taggable taggable : tags)
  // if (this.tag().equals(taggable.tag()))
  // return true;
  // return false;
  // }

  public boolean is(String... tags)
  {
    for (String value : tags)
      if (this.tag().equals(value))
        return true;
    return false;
  }

  public boolean hasChild(String... tags)
  {
    for (XmlNode node : this.children())
    {
      for (String value : tags)
        if (node.tag().equals(value))
          return true;
      if (node.hasChild(tags))
        return true;
    }
    return false;
  }

  public int childIndex(XmlNode child)
  {
    int index = 0;
    for (XmlINode node : children())
    {
      if (node == child)
        return index;
      index++;
    }
    return -1;
  }
  
  @Override
  public String tag()
  {
    return tag;
  }

  @Override
  public String sticker()
  {
    return tag();
  }

  @Override
  public String toString()
  {
    return tag();
  }

  @Override
  public Xmlizer xmlizer()
  {
    return this instanceof Xmlizer ? (Xmlizer) this : new Xmlizer.Adapter<XmlNode>(this);
  }

  public boolean visitTree(Visitor<XmlINode> visitor)
  {
    return Xml.VisitTree(this, visitor);
  }

  public static class Flatten<S>
  {
    private XmlNode root;

    public Flatten(XmlNode root)
    {
      this.root = root;
    }

    public List3<S> list(OCD... tags)
    {
      return list(Zen.Array.toStrings(tags));
    }

    public List3<S> list(String... tags)
    {
      return list(new List3<S>(), tags);
    }

    public List3<S> list(List3<S> list, String... tags)
    {
      list = list == null ? new List3<>() : list;
      for (XmlNode node : root.flatten(tags))
        list.add((S) node);
      return list;
    }
  }   

  public synchronized List3<XmlNode> flatten(String... tags)
  {
    return this.flatten(new List3<XmlNode>(), tags.length == 0 ? null : new StringSet(tags), true);
  }

  protected synchronized List3<XmlNode> flatten(List3<XmlNode> list, StringSet tags, boolean doDig)
  {
    for (XmlNode node : syncChildren())
      if (node != null)
        if (tags == null || tags.isEmpty() || tags.has(node.tag))
        {
          list.add(node);
          if (doDig)
            node.flatten(list, tags, doDig);
        } else
          node.flatten(list, tags, doDig);
    return list;
  }

  protected synchronized XmlNode identify(String id, StringSet tags)
  {
    for (XmlNode node : syncChildren())
    {
      if (id.equals(node.id) && (tags == null || tags.isEmpty() || tags.has(node.tag)))
        return node;
      if ((node = node.identify(id, tags)) != null)
        return node;
    }
    return null;
  }

  public synchronized StringMap<XmlNode> idMap(StringMap<XmlNode> map, StringSet tags)
  {
    if (map == null)
      map = new StringMap<XmlNode>();
    for (XmlNode node : syncChildren())
    {
      if (node.id != null && (tags == null || tags.isEmpty() || tags.has(node.tag)))
        map.put(node.id, node);
      node.idMap(map, tags);
    }
    return map;
  }

  public boolean readNode(InputStream stream)
  {
    if (stream == null)
      Log.warn(this, ".readNode - stream==null");
    else
      try
      {
        Xml.Load(this, stream);
        // we read a standalone zip entry inputstream and close it
        stream.close();
        return true;
      } catch (Exception e)
      {
        Log.info(this, ".readNode - inputstream reading exception: " + e.getMessage());
        e.printStackTrace();
      } finally
      {
        IO.Close(stream);
      }
    return false;
  }

  public boolean writeNode(OutputStream stream)
  {
    try
    {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, Xml.ENCODING)); // "UTF-8"
      Xml xml = new Xml(writer, 1);
      xml.writeHeader();
      xml.write(this);
      xml = null;
      writer.flush();
      // write.close(); //do not uncomment this since zip outputstream needs to
      // stay open for further entries
      writer = null;
      return true;
    } catch (Exception e)
    {
      Log.warn(OCDEntry.class, ".writeNode - outputstream writing exception: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  public void copyTo(XmlNode node)
  {
    node.parent = parent;
    // we do not copy id, since ids are unique !
  }

  public String xmlString()
  {
    return Xml.toString(this);
  }
  
  public void printXmlString()
  {
    Log.info(this,  ".printXmlString - "+Xml.toString(this));
  }
}
