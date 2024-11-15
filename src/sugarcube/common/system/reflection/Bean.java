package sugarcube.common.system.reflection;

import sugarcube.common.system.reflection.Annot._Bean;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Property3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.File3;
import sugarcube.common.data.xml.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

public class Bean extends XmlNode implements Xmlizer, Cloneable, Unjammable
{
  public interface Listener
  {
     void beanFieldModified(Bean bean, Field field, Object src);
  }

  private StringSet ignore = new StringSet();
  private String name = null;
  private String filepath = null;
  private Object object = null;// if null bean==this

  public Bean()
  {
    super(null);
    object = this;
  }

  public Bean(XmlINode parent)
  {
    super(parent);
    object = this;
  }

  public Bean(String tag)// , Actionable<Property3>... listeners)
  {
    this(tag, null);
    object = this;
  }

  public Bean(String tag, XmlINode parent)// , Actionable<Property3>...
                                          // listeners)
  {
    super(tag, parent);
    object = this;
    // this.listeners.add(listeners);
  }

  public Bean(Bean beanObject)
  {
    super("data", null);
    this.object = beanObject;
  }

  public Bean(Object beanObject)
  {
    super("data", null);
    this.object = beanObject;
  }

  public Bean(String tag, Bean beanObject)
  {
    super(tag, null);
    this.object = beanObject;
  }

  public Bean(String tag, Object beanObject)
  {
    super(tag, null);
    this.object = beanObject;
  }

  public Bean ignore(String... fields)
  {
    this.ignore.addAll(fields);
    return this;
  }

  public Object object()
  {
    return object;
  }

  public String filepath()
  {
    return filepath;
  }

  public Bean setFilepath(String path)
  {
    this.filepath = path;
    return this;
  }

  public Field field(String name)
  {
    try
    {
      Class cls = object.getClass();
      do
      {
        Field field = cls.getDeclaredField(name);
        if (field != null)
          return field;
      } while ((cls = cls.getSuperclass()) != null);
      return null;
    } catch (Exception e)
    {
    }
    return null;
  }

  public Field[] fields()
  {
    List3<Field> fields = new List3<>();
    if (object != null)
    {
      int mods;
      Class cls = object.getClass();
      do
      {
        for (Field field : cls.getDeclaredFields())
        {
          _Bean annot = (_Bean) field.getAnnotation(_Bean.class);
          boolean isBean = annot != null;
          boolean isHidden = isBean && annot.hide();
          if (!isHidden)
            if (isBean || Modifier.isPublic(mods = field.getModifiers()) && !Modifier.isStatic(mods) && !Modifier.isTransient(mods)
                && !Modifier.isFinal(mods))
              if (!ignore.has(field.getName()))
                fields.add(field);
        }
      } while ((cls = cls.getSuperclass()) != null);
    }
    return fields.toArray(new Field[0]);
  }

  public String name()
  {
    return name;
  }

  public Bean setName(String name)
  {
    this.name = name;
    return this;
  }

  @Override
  public Xmlizer xmlizer()
  {
    return this;
  }

  // public void addListeners(Actionable<Property3>... listeners)
  // {
  // this.listeners.add(listeners);
  // }
  public boolean load()
  {
    return load(new File3(filepath));
  }

  public boolean load(String xml)
  {
    return Xml.Load(this, xml) != null;
  }

  public boolean load(File file)
  {
    return Xml.Load(this, file) != null;
  }

  public boolean load(InputStream stream)
  {
    return Xml.Load(this, stream) != null;
  }

  public boolean store()
  {
    return this.store(new File3(filepath));
  }

  public boolean store(File file)
  {
    return Xml.write(this, file);
  }

  public boolean store(Writer writer, int indent, boolean header)
  {
    return Xml.write(this, writer, indent, header);
  }

  public boolean store(OutputStream stream, int indent, boolean header)
  {
    return Xml.write(this, stream, indent, header);
  }

  public String string(Field field)
  {
    return Bean.string(field.getType(), get(field));
  }

  public Object get(Field field)
  {
    field.setAccessible(true);
    try
    {
      return field.get(object);
    } catch (IllegalArgumentException | IllegalAccessException e)
    {
      e.printStackTrace();
    }
    field.setAccessible(false);
    return null;
  }

  public Boolean getBoolean(Field field)
  {
    field.setAccessible(true);
    try
    {
      return field.getBoolean(object);
    } catch (IllegalArgumentException | IllegalAccessException e)
    {
      e.printStackTrace();
    }
    field.setAccessible(false);
    return null;
  }

  public Integer getInt(Field field)
  {
    field.setAccessible(true);
    try
    {
      return field.getInt(object);
    } catch (IllegalArgumentException | IllegalAccessException e)
    {
      e.printStackTrace();
    }
    field.setAccessible(false);
    return null;
  }

  public Float getFloat(Field field)
  {
    field.setAccessible(true);
    try
    {
      return field.getFloat(object);
    } catch (IllegalArgumentException | IllegalAccessException e)
    {
      e.printStackTrace();
    }
    field.setAccessible(false);
    return null;
  }

  public Double getDouble(Field field)
  {
    field.setAccessible(true);
    try
    {
      return field.getDouble(object);
    } catch (IllegalArgumentException | IllegalAccessException e)
    {
      e.printStackTrace();
    }
    field.setAccessible(false);
    return null;
  }

  public void set(Bean bean)
  {
    this.set(bean.name, bean);
  }

  public void set(Property3 property)
  {
    this.set(property.key, property.value);
  }

  public void set(String name, Object obj)
  {
    Field field = field(name);
    if (field == null)
      Log.debug(this, ".set - null field: " + this.getClass().getSimpleName() + ", name=" + name + ", obj=" + obj);
    else
      this.set(field, obj);
  }

  public void set(Field field, Object value)
  {
    try
    {
      if (field == null)
      {
        Log.debug(this, ".set - unable to set name=" + name + ": " + value.toString());
        return;
      }
      Class cls = field.getType();
      // Log.debug(this, ".set - field="+field+": "+obj);
      // if string, data has to be parsed...
      if (cls.equals(String[].class) || cls.isArray())
      {
        cls = cls.getComponentType();// String[] -> String
        try
        {
          Object[] oldArray = (Object[]) field.get(object);
          Object[] newArray = (Object[]) Array.newInstance(cls, oldArray == null ? 0 : oldArray.length + 1);
          System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
          newArray[oldArray.length] = ClassField.cast(value, cls);
          value = newArray;
        } catch (Exception e)
        {
          Log.warn(this, ".set - failed: " + name + " " + value);
          e.printStackTrace();
        }
      } else
        value = ClassField.cast(value, cls);

      Reflect.Set(field, object, value);
      // listeners.notifyListeners(MSG_PROPERTY_CHANGED, new Property3(this,
      // name, obj));
    } catch (Exception e)
    {
      Log.warn(this, ".set - bean exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static String string(Class cls, Object obj)
  {
    if (obj == null)
      return "";
    else if (cls.equals(float[].class))
      return Zen.Array.String((float[]) obj);
    else
      return obj.toString();
  }

  @Override
  public void setParent(XmlINode parent)
  {
    this.parent = parent;
  }

  public Bean updateParent(XmlINode parent)
  {
    this.setParent(parent);
    return this;
  }

  @Override
  public XmlINode parent()
  {
    return parent;
  }

  public Property3[] properties()
  {
    List3<Property3> list = new List3<>();
    for (Field field : fields())
      try
      {
        Object value = field.get(object);
        Class type = field.getType();
        if (!type.isArray() && !(value instanceof Bean))
          list.add(new Property3(this, field.getName(), value));
      } catch (Exception e)
      {
        Log.warn(this, ".properties - " + field.getName() + ": " + e.getMessage());
      }

    return list.toArray(new Property3[0]);
  }

  @Override
  public Collection<? extends XmlNode> children()
  {
    List3<XmlNode> list = new List3<>();
    try
    {
      for (Field field : fields())
        try
        {
          String fieldName = field.getName();
          Object value = field.get(object);
          Class cls = field.getType();
          if (cls.isArray())
          {
            cls = cls.getComponentType();
            // Log.debug(this, ".children - field=" + fieldName + ", class=" +
            // type + ", isBean=" + isBean);
            if (Bean.class.isAssignableFrom(cls))
              for (Bean bean : (Bean[]) value)
                list.add(bean.setName(fieldName));
            else
              for (Object data : (Object[]) value)
                list.add(new Property3(this, fieldName, data));
          } else if (value instanceof Bean)
            list.add(((Bean) value).setName(fieldName));
          else
            list.add(new Property3(this, fieldName, value));
        } catch (Exception e)
        {
          Log.warn(this, ".children - " + field.getName() + ": " + e.getMessage());
        }
      return list;
    } catch (Exception e)
    {
      Log.warn(this, ".children - bean exception: " + e.getMessage());
    }
    return list;
  }

  @Override
  public String tag()
  {
    return tag;
  }

  @Override
  public String sticker()
  {
    return tag;
  }

  @Override
  public Collection<? extends XmlINode> writeAttributes(Xml xml)
  {
    if (this.name != null && !this.name.trim().isEmpty())
      xml.write("name", name);
    return this.children();
  }

  @Override
  public void readAttributes(DomNode dom)
  {
    this.name = dom.value("name", null);
  }

  public XmlINode newChild(Class type)
  {
    try
    {
      if (Bean.class.isAssignableFrom(type))
        return (((Bean) type.newInstance()).updateParent(this));
      else
        return new Property3(this, null, null);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    // Log.debug(this, ".newChild - " + child.value(NAME, null));
    if (child.tag().equals(Property3.TAG))
      return new Property3(this, null, null);
    else
    {
      Field field = field(child.value("name", null));
      if (field == null)
      {
        Log.debug(this, ".newChild - field not found: " + child.value("name", null));
        return null;
      }
      Class type = field.getType();
      return newChild(type.isArray() ? type.getComponentType() : type);
    }
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child != null)
      if (child instanceof Property3)
        this.set((Property3) child);
      else if (child instanceof Bean)
        this.set((Bean) child);
  }

  @Override
  public String toString()
  {
    return tag();
  }

  public String filename()
  {
    return this.tag + ".xml";
  }

  public void copyTo(Bean copy)
  {
    super.copyTo(copy);
    copy.filepath = this.filepath;
    copy.name = this.name;
    copy.object = this.object;
    for (Field field : fields())
      try
      {
        copy.field(field.getName()).set(copy.object, field.get(object));
      } catch (Exception e)
      {
        Log.warn(this, ".copyTo - " + field.getName() + ": " + e.getMessage());
      }
  }
}
