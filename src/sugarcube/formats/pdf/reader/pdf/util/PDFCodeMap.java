package sugarcube.formats.pdf.reader.pdf.util;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PDFCodeMap<T extends PDFNode> extends LinkedHashMap<Integer, T>
{
  private static transient Set<Integer> NOT_FOUND = new HashSet<Integer>();
  private final String name;
  private final T defaultValue;

  public PDFCodeMap(String name)
  {
    this.name = name;
    this.defaultValue = null;
  }

  public PDFCodeMap(String name, T defaultValue)
  {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public PDFCodeMap(String name, Integer[] keys, T[] values)
  {
    this.name = name;
    this.defaultValue = null;
    for (int i = 0; i < keys.length && i < values.length; i++)
      this.put(keys[i], values[i]);
  }

  public T get(Integer key)
  {
    if (containsKey(key))
      return super.get(key);
    else
    {
      if (!NOT_FOUND.contains(key))
      {
        Log.debug(this, ".get - key not found: key=" + key + " defaultValue=" + defaultValue);
        NOT_FOUND.add(key);
      }
      return defaultValue;
    }
  }

  public PDFCodeMap<T> copy()
  {
    PDFCodeMap copy = new PDFCodeMap<T>(name, defaultValue);
    copy.update(this);
    return copy;
  }

  public T update(Integer key, T value)
  {
    return this.put(key, value);
  }

  public PDFCodeMap update(Map<? extends Integer, ? extends T> map)
  {
    this.putAll(map);
    return this;
  }

  public Wrapper wrap(PDFNode vo)
  {
    return new Wrapper(vo);
  }

  protected class Wrapper extends PDFNode
  {
    public Wrapper(PDFNode vo)
    {
      super(PDFCodeMap.this.name, vo);
      for (Map.Entry<Integer, ? extends PDFNode> e : PDFCodeMap.this.entrySet())
        this.add(e.getValue());
    }

    @Override
    public String sticker()
    {
      return PDFCodeMap.this.name + "[" + PDFCodeMap.this.size() + "]";
    }

    @Override
    public String toString()
    {
      String s = "";
      int i = 0;
      for (Map.Entry<Integer, T> e : PDFCodeMap.this.entrySet())
        s += e.getKey() + " » " + e.getValue() + (i++ != PDFCodeMap.this.size() - 1 ? "\n" : "");
      return s;
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
      for (PDFNode vo : PDFCodeMap.this.values())
        vo.paint(g, props);
    }
  }
}
