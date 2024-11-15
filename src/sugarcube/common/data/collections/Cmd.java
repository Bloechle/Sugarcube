package sugarcube.common.data.collections;

import sugarcube.common.data.Zen;
import sugarcube.common.graphics.Color3;
import sugarcube.common.data.xml.Nb;

import java.awt.*;

public class Cmd
{
  public interface Handler
  {
    void command(Cmd cmd);
  }


  public String key;
  public Object value;
  public boolean forward = true;
  public Commands map;

  public Cmd()
  {
  }

  public Cmd(String key, Object value)
  {
    this.key = key;
    this.value = value;
  }

  public Cmd(String key, String value)
  {
    this.key = key;
    this.value = value;
  }

  public Cmd(String key, Object value, Commands map)
  {
    this.key = key;
    this.value = value;
    this.map = map;
  }

  public Cmd(String key, Commands map)
  {
    this.key = key;
    this.map = map;
  }

  public String key()
  {
    return key;
  }

  public Commands map()
  {
    return map;
  }

  public Cmd pull()
  {
    return new Cmd(key(), value(), map);
  }

  public boolean isValue(Object... values)
  {
    for (Object o : values)
      if (string("").equalsIgnoreCase(o.toString()))
        return true;
    return false;
  }

  public boolean hasValue(Object... values)
  {
    for (Object o : values)
      if (string("").contains(o.toString()))
        return true;
    return false;
  }

  public boolean is(String key, Object value)
  {
    if (isntKey(key))
      return false;
    Object v = value();
    return v == null ? value == null : v.equals(value);
  }

  public boolean isntKey(String... keys)
  {
    return !isKey(keys);
  }

  public boolean isKey(String... keys)
  {
    for (String k : keys)
      if (k.equalsIgnoreCase(key()))
        return true;
    return false;
  }

  public boolean startsWith(String prefix)
  {
    return key().toLowerCase().startsWith(prefix.toLowerCase());
  }

  public boolean endsWith(String suffix)
  {
    return key().toLowerCase().endsWith(suffix.toLowerCase());
  }

  public Object value(Object def)
  {
    if(value!=null)
      return value;        
    return map == null ? def : (map.has(key()) ? map.get(key).value : def);
  }

  public Object value()
  {
    return value(null);
  }

  public int integer(int def)
  {
    return integer(this.value(def), def);
  }

  public static int integer(Object o, int def)
  {
    if (o == null)
      return def;
    else if (o instanceof Number)
      return ((Number) o).intValue();
    else if (o instanceof String)
      return Nb.Int((String) o, def);
    else
      return def;
  }

  public float real(float def)
  {
    return real(this.value(def), def);
  }

  public static float real(Object o, float def)
  {
    if (o == null)
      return def;
    else if (o instanceof Number)
      return ((Number) o).floatValue();
    else if (o instanceof String)
      return Nb.Float((String) o, def);
    else
      return def;
  }

  public String sReal(String def)
  {
    return sReal(this.value(def), def);
  }

  public static String sReal(Object o, String def)
  {
    if (o == null)
      return def;
    else if (o instanceof Number)
      return ((Number) o).toString();
    else if (o instanceof String)
      return (String) o;
    else
      return def;
  }

  public boolean bool(boolean def)
  {
    return bool(this.value(def), def);
  }

  public static boolean bool(Object o, boolean def)
  {
    if (o == null)
      return def;
    else if (o instanceof Boolean)
      return ((Boolean) o).booleanValue();
    else if (o instanceof String)
      return Nb.Bool((String) o, def);
    else
      return def;
  }

  public String string(String def)
  {
    return string(this.value(def), def);
  }

  public static String string(Object o, String def)
  {
    return o == null ? def : o.toString().trim();
  }

  public javafx.scene.paint.Color fxColor(Color3 def)
  {
    Color3 c = color(def);
    return c == null ? (def == null ? null : def.fx()) : c.fx();
  }

  public Color3 color(Color3 def)
  {
    return color(this.value(def), def);
  }

  public static Color3 color(Object o, Color3 def)
  {
    if (o == null)
      return def == null ? null : def;
    else if (o instanceof Color3)
      return (Color3) o;
    else if (o instanceof javafx.scene.paint.Color)
      return Color3.Get((javafx.scene.paint.Color) o);
    else if (o instanceof Color)
      return new Color3((Color) o);
    else if (o instanceof String)
      return Color3.ParseCss((String) o, def);
    else
      return def;
  }
  
  public boolean equals(Cmd that)
  {
    if(this==that)
      return true;    
    if(that==null)
      return false;    
    if(!Zen.equals(this.key,  that.key))
      return false;        
    if(Zen.equals(this.value, that.value) || Zen.equalsToString(this.value,  that.value))
      return true;          
    return false;
  }
  
  @Override
  public int hashCode()
  {
    return (key==null ? 0 : key.hashCode()) + 31 * (value == null ? 0 : value.toString().hashCode());    
  }
  

  @Override
  public String toString()
  {
    Object o = this.value(null);
    return "Cmd[" + this.key() + " " + (o == null ? "null" : o.toString()) + "]";
  }

  public static final String FIELD = "field";
  public static final String DELETE = "delete";
  public static final String ADD = "add";
  public static final String UNDO = "undo";
  public static final String REDO = "redo";
  public static final String EXTENT = "extent";
  public static final String SELECTION = "selection";
  public static final String COPY = "copy";
  public static final String PASTE = "paste";
  public static final String EDIT = "edit";
  public static final String MODE = "mode";
}
