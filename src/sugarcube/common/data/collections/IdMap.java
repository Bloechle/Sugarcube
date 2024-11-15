package sugarcube.common.data.collections;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.time.DateUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IdMap<T> extends StringMap<T>
{
  private static transient Set<String> NOT_FOUND = new HashSet<String>();
  protected String name;
  protected T def;

  public IdMap()
  {
    this(DateUtils.universalTime());
  }

  public IdMap(String name)
  {
    this.name = name;
    this.def = null;
  }

  public IdMap(String name, T def)
  {
    this.name = name;
    this.def = def;
  }

  public IdMap(String name, String[] keys, T[] values)
  {
    this.name = name;
    this.def = null;
    for (int i = 0; i < keys.length && i < values.length; i++)
      this.put(keys[i], values[i]);
  }

  public T defaultValue()
  {
    return this.def;
  }

  public int index(String key)
  {
    int index = 0;
    for (String s : this.keySet())
    {
      if (s.equals(key))
        return index;
      index++;
    }
    return -1;
  }

  public T first()
  {
    Iterator<T> it = this.values().iterator();
    return it.hasNext() ? it.next() : def;
  }

  public T get(String key)
  {
    if (containsKey(key))
      return super.get(key);
    else
    {
      if (!NOT_FOUND.contains(key))
      {

        Log.info(this, ".get - key not found: key=" + key + " defaultValue=" + def);
        // Zen.stacktrace();
        NOT_FOUND.add(key);
      }
      return def;
    }
  }


  public T update(String key, T value)
  {
    return this.put(key, value);
  }

  public IdMap update(Map<? extends String, ? extends T> map)
  {
    this.putAll(map);
    return this;
  }


  public IdMap<T> copy()
  {
    IdMap<T> copy = new IdMap<T>(name, def);
    copy.update(this);
    return copy;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\nNameMap[").append(name).append("]\n");
    for (Map.Entry<String, T> e : IdMap.this.entrySet())
      sb.append(e.getKey()).append(" » ").append(e.getValue()).append("\n");
    return sb.toString();
  }


}
