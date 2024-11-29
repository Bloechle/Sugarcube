package sugarcube.formats.pdf.reader.pdf.util;

import sugarcube.common.data.collections.Unicodes;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Mapper<S, T> extends LinkedHashMap<S, T> implements Iterable<Map.Entry<S, T>>
{
  private final String name;
  private final T defaultValue;

  public Mapper(String name)
  {
    this.name = name;
    this.defaultValue = null;
  }

  public Mapper(String name, T defaultValue)
  {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public Mapper(String name, S[] keys, T[] values)
  {
    this.name = name;
    this.defaultValue = null;
    for (int i = 0; i < keys.length && i < values.length; i++)
      this.put(keys[i], values[i]);
  }

  public T get(S key, T recover)
  {
    return containsKey(key) ? super.get(key) : recover;
  }

  public int index(S key)
  {
    int index = 0;
    for (S s : this.keySet())
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
    return it.hasNext() ? it.next() : defaultValue;
  }

  public T update(S key, T value)
  {
    return this.put(key, value);
  }

  public Mapper<S, T> update(Map<? extends S, ? extends T> map)
  {
    this.putAll(map);
    return this;
  }

  public Mapper<S, T> copy()
  {
    Mapper<S, T> copy = new Mapper<S, T>(name, defaultValue);
    copy.update(this);
    return copy;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\nMapper[").append(name).append("]\n");
    for (Map.Entry<S, T> e : Mapper.this.entrySet())
      sb.append(e.getKey()).append(" » ").append(e.getValue() instanceof Unicodes ? ((Unicodes) e.getValue()).toDescriptiveString() : e.getValue()).append("\n");
    return sb.toString();
  }

  public Wrapper wrap(PDFNode vo)
  {
    return new Wrapper(vo);
  }

  @Override
  public Iterator<Map.Entry<S, T>> iterator()
  {
    return this.entrySet().iterator();
  }

  protected class Wrapper extends PDFNode
  {
    public Wrapper(PDFNode vo)
    {
      super(Mapper.this.name, vo);
    }

    @Override
    public String sticker()
    {
      return Mapper.this.name + "[" + Mapper.this.size() + "]";
    }

    @Override
    public String toString()
    {
      return Mapper.this.toString();
    }
  }
}
