package sugarcube.formats.pdf.reader.pdf.util;

import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class PDFNameMap<T extends PDFNode> extends LinkedHashMap<String, T>
{
  private final String name;
  private final T defaultValue;

  public PDFNameMap(String name)
  {
    this.name = name;
    this.defaultValue = null;
  }

  public PDFNameMap(String name, T defaultValue)
  {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public PDFNameMap(String name, String[] keys, T[] values)
  {
    this.name = name;
    this.defaultValue = null;
    for (int i = 0; i < keys.length && i < values.length; i++)
      this.put(keys[i], values[i]);
  }

  public boolean has(String key)
  {
    return this.containsKey(key);
  }

  public T get(String key)
  {
    return containsKey(key) ? super.get(key) : defaultValue;
  }

  public PDFNameMap<T> copy()
  {
    PDFNameMap copy = new PDFNameMap<T>(name, defaultValue);
    copy.update(this);
    return copy;
  }

  public T update(String key, T value)
  {
    return this.put(key, value);
  }

  public PDFNameMap update(Map<? extends String, ? extends T> map)
  {
    this.putAll(map);
    return this;
  }

  public Wrapper wrap(PDFNode node)
  {
    return new Wrapper(node);
  }

  protected class Wrapper extends PDFNode
  {
    public Wrapper(PDFNode vo)
    {
      super(PDFNameMap.this.name, vo);
      for (Map.Entry<String, ? extends PDFNode> e : PDFNameMap.this.entrySet())
        this.add(e.getValue());
    }

    @Override
    public String sticker()
    {
      return PDFNameMap.this.name + "[" + PDFNameMap.this.size() + "]";
    }

    @Override
    public String toString()
    {
      String s = "";
      int i = 0;
      for (Map.Entry<String, T> e : PDFNameMap.this.entrySet())
        s += e.getKey() + " » " + e.getValue() + (i++ != PDFNameMap.this.size() - 1 ? "\n" : "");
      return s;
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
      if (this.nbOfChildren() > 0)
        ((PDFNode) this.iterator().next()).paint(g, props);
    }
  }
}
