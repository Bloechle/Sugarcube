package sugarcube.common.data.xml;

import javafx.beans.property.Property;
import sugarcube.common.system.reflection.Annot._Xml;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.*;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.reflection.ClassField;
import sugarcube.common.system.reflection.Reflect;
import sugarcube.common.ui.fx.beans.PBool;
import sugarcube.common.ui.fx.beans.PDouble;
import sugarcube.common.ui.fx.beans.PString;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;

public abstract class XmlNodeReflect extends XmlNode implements Xmlizer, Unjammable
{
  protected transient boolean isLoaded = false;

  public XmlNodeReflect()
  {
    super(null);
  }

  public XmlNodeReflect(XmlINode parent)
  {
    super(parent);
  }

  public XmlNodeReflect(Object tag, XmlINode parent)
  {
    super(tag, parent);
  }

  public boolean isLoaded()
  {
    return isLoaded;
  }

  public boolean isInactive()
  {
    return false;
  }

  @Override
  public final Xmlizer xmlizer()
  {
    return this;
  }

  @Override
  public final boolean isAnnotated()
  {
    return true;// enables XML annotation
  }

  public _Xml annot(Field field)
  {
    return field.isAnnotationPresent(_Xml.class) ? (_Xml) field.getAnnotation(_Xml.class) : null;
  }

  public boolean isAttribute(Field field)
  {
    // attibutes must be unique... thus an array is obviously not an attribute
    if (field.getType().isArray())
      return false;
    if (XmlINode.class.isAssignableFrom(field.getType()))
      return false;
    // must be annotated to be an XML attribute (as well as children node)
    _Xml annot = annot(field);
    // field is an annot if it has no XML tag or XML tag equals this one
    return (annot == null ? false : (annot.tag() == null || annot.tag().trim().isEmpty() || annot.tag().equals(tag())));
  }

  public boolean isNul(Field field)
  {
    try
    {
      _Xml annot = annot(field);
      String nul = annot == null ? null : annot.def();
      Object o = field.get(this);
      if (o == null && nul == null)
        return true;
      else if (o != null && nul != null && nul.equals((o instanceof Property ? ((Property) o).getValue() : o).toString()))
        return true;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }

  public Field[] fields()
  {
    return Reflect.Fields(this.getClass(), XmlNodeReflect.class);
  }

  public String[] fieldNames()
  {
    Field[] fields = fields();
    String[] names = new String[fields.length];
    for (int i = 0; i < names.length; i++)
      names[i] = fields[i].getName();
    return names;
  }

  @Override
  public Collection<? extends XmlNode> writeAttributes(Xml xml)
  {
    if (Str.HasChar(id))
      xml.write("id", id);

    for (Field field : fields())
      if (isAttribute(field) && !isNul(field))
        xml.writeField(field, this);
    return this.children(true);
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    this.isLoaded = true;
    this.id = dom.value("id", id);
    for (Field field : fields())
    {
      if (isAttribute(field))
        dom.readField(field, this);
    }
    StringSet unused = dom.remainingAttributes();
    if (unused.isPopulated())
      Log.debug(this, ".readAttributes - unused: " + unused);
  }

  // public void copyTo(XmlAnnotated node)
  // {
  // for (Field field : fields())
  // {
  // if (isAttribute(field) && node.isAttribute(field) && !nulAttribute(field))
  // {
  // boolean isAccessible = field.isAccessible();

  // }
  // }
  // }

  private XmlINode setParent3(XmlINode node)
  {
    node.setParent(parent);
    return node;
  }

  private XmlINode instance(Field field)
  {
    Class cls = field.getType();
    boolean isArray = false;
    if (cls.isArray())
    {
      cls = cls.getComponentType();
      isArray = true;
    }

    // if (this.tag.equals("operation"))
    // Log.debug(this, ".instance - field=" +
    // field.getName()+", cls="+cls.getName()+",
    // xmlinode?"+XmlINode.class.isAssignableFrom(cls));
    if (XmlINode.class.isAssignableFrom(cls))
      try
      {
        if (!isArray)
        {
          // if object is already instanciated, use it...
          XmlINode node = (XmlINode) field.get(this);
          if (node != null)
            return setParent3(node);
        }

        // tries new XmlINode(XmlINode parent)
        for (Constructor ct : cls.getDeclaredConstructors())
        {
          Class[] types = ct.getParameterTypes();
          if (types.length == 0)
            return setParent3((XmlINode) ct.newInstance());
          else if (types.length == 1 && XmlINode.class.isAssignableFrom(types[0]))
            return (XmlINode) ct.newInstance(Zen.Array.objects(this));

        }
        return setParent3((XmlINode) cls.newInstance());
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    return null;
  }

  protected boolean isChildField(Field field, String child)
  {
    // checks if an object field is actually used as a child node (child field)
    // Log.debug(this, ".isChildField - "+field.getName()+"="+child);
    Class fieldClass = field.getType();
    boolean isArray = fieldClass.isArray();
    if (isArray)
      fieldClass = fieldClass.getComponentType();

    // children (as well as attributes) must be annotated
    if (!field.isAnnotationPresent(_Xml.class))
      return false;

    // its an attribute field, hence not a children node
    if (isAttribute(field))
      return false;

    if (field.isAnnotationPresent(_Xml.class))
    {
      _Xml fieldAnnot = (_Xml) field.getAnnotation(_Xml.class);
      if (!isArray && XmlINode.class.isAssignableFrom(fieldClass))
        try
        {
          XmlINode node = (XmlINode) field.get(this);
          if (node != null)
            return node.tag().equals(child);
        } catch (Exception e)
        {
          e.printStackTrace();
        }
      else
      {
        if (fieldClass.isAnnotationPresent(_Xml.class))
        {
          _Xml classAnnot = (_Xml) fieldClass.getAnnotation(_Xml.class);
          if (classAnnot.tag() != null)
            return classAnnot.tag().equals(child) || classAnnot.tag().endsWith("_") && child.startsWith(classAnnot.tag());// spMF_1,
                                                                                                                          // spMF_2
        } else if (fieldAnnot.tag() != null && !fieldAnnot.tag().isEmpty())
          return fieldAnnot.tag().equals(child);
        else if (field.getName().equals(child))
          // Log.debug(this, ".isChildField - field=" + field.getName() +
          // ", cls=" + fieldClass.getSimpleName() + ", child=" + child +
          // ", annot=" + fieldAnnot.tag().isEmpty());
          return true;
        try
        {
          for (Field tagField : fieldClass.getDeclaredFields())
            if (tagField.getName().equals("TAG"))
              return tagField.get(null).toString().equals(child);
        } catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }

    }
    return false;
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    // Log.debug(this,
    // ".newChild - "+child.tag()+": "+Zen.A.toString(this.fieldNames()));
    XmlINode node;
    for (Field field : fields())
      if (this.isChildField(field, child.tag()) && ((node = instance(field)) != null))
        return node;
    return new XmlNodeReflectProps(child.tag(), this);
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    for (Field field : fields())
      if (isChildField(field, child.tag()))
        try
        {
          boolean isRestricted = !field.isAccessible();
          if (isRestricted)
            field.setAccessible(true);
          Class cls = field.getType();
          if (child instanceof XmlNodeReflectProps)// declared on the fly children
          {
            // Log.debug(this, ".endChild - " + child.tag() + ", child=" +
            // child);
            XmlNodeReflectProps node = (XmlNodeReflectProps) child;
            String key = annot(field).key();
            Props props = node.props;
            if (props.has(key))
            {
              if ("cdata".equals(key))
                key = "";// props cdata key
              if (cls.isArray())
              {
                // String[] -> String
                cls = cls.getComponentType();
                try
                {
                  Object[] a0 = (Object[]) field.get(this);
                  int size = a0 == null ? 0 : a0.length;
                  Object[] a1 = (Object[]) Array.newInstance(cls, size + 1);
                  System.arraycopy(a0, 0, a1, 0, a0.length);

                  // check Object && primitive types separately
                  if (Class3.isString(cls))
                    a1[a0.length] = props.get(key);
                  else if (Class3.isDouble(cls))
                    a1[a0.length] = (double) props.real(key);
                  else if (Class3.isFloat(cls))
                    a1[a0.length] = props.real(key);
                  else if (Class3.isInteger(cls))
                    a1[a0.length] = props.integer(key);
                  else if (Class3.isBoolean(cls))
                    a1[a0.length] = props.bool(key);
                  field.set(this, a1);
                } catch (Exception e)
                {
                  e.printStackTrace();
                }
              } else
                Reflect.Set(field, this, props.get(key));
            }
          } else if (child instanceof XmlINode)
          {
            if (cls.isArray())
              Reflect.UpdateArray(null, this, field, child);
            else
              field.set(this, child);
          }
          if (isRestricted)
            field.setAccessible(false);
          if (child instanceof XmlNode)
            child.setParent(this);
          return;
        } catch (Exception e)
        {
          e.printStackTrace();
          return;
        }

    // logs unused tag full paths
    List3<XmlINode> path = new List3<>(child);
    while ((child = child.parent()) != null)
      path.add(child);
    Stringer sb = new Stringer();
    for (XmlINode node : path.descending())
      sb.append("/" + node.tag());// +"["+node.getClass().getSimpleName()+"]"
    Log.debug(this, ".endChild - ignored: " + sb.toString());
  }

  public Object updateArray(String fieldName, Object... array)
  {
    return Reflect.UpdateArray(null, this, fieldName, array);
  }

  public Object updateArray(Class cls, String fieldName, Object... array)
  {
    return Reflect.UpdateArray(cls, this, fieldName, array);
  }

  @Override
  public XmlChildren children()
  {
    return children(false);
  }

  public XmlChildren children(boolean hideInactive)
  {
    XmlChildren<XmlINode> children = new XmlChildren<XmlINode>();
    for (Field field : fields())
    {
      Class cls = field.getType();
      boolean isArray = cls.isArray();
      if (isArray)
        cls = cls.getComponentType();
      _Xml annot = annot(field);
      if (annot != null && (isArray || !isAttribute(field)))
        try
        {
          // Log.debug(this, ".children - field:" + field.getName() + ",cls=" +
          // cls.getSimpleName() + ", isArray=" + isArray + ", assignable=" +
          // XmlINode.class.isAssignableFrom(cls));
          String key = annot.key().equals("cdata") ? "" : annot.key();
          for (Object obj : isArray ? (Object[]) field.get(this) : Zen.Array.objects(field.get(this)))
            if (obj != null)
            {
              if (hideInactive && obj instanceof XmlNodeReflect && ((XmlNodeReflect) obj).isInactive())
                continue;

              if (XmlINode.class.isAssignableFrom(cls))
                children.add((XmlINode) obj);
              else if (obj != null)
              {
                String string = obj.toString();
                if (Str.HasData(string))
                  children.add(new XmlNodeReflectProps(annot.tag(), this, key, string));
              }
            }
        } catch (Exception e)
        {
          e.printStackTrace();
        }
    }
    // Log.debug(this, ".children - "+children);
    return children;
  }

  @Override
  public String toString()
  {
    return Xml.toString(this);
  }

  public XmlNodeReflect copyTo(XmlNodeReflect copy)
  {
    for (Field field : fields())
      if (field.isAnnotationPresent(_Xml.class))
        ClassField.copy(field, this, copy);
    return copy;
  }
  
  public static PString Empty()
  {
    return PString.New();
  }
  
  public static PBool True()
  {
    return PBool.True();
  }

  public static PBool False()
  {
    return PBool.False();
  }
  
  public static PDouble Double(double value)
  {
    return PDouble.New(value);
  }

}
