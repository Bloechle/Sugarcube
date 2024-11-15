package sugarcube.common.data.collections;

import java.util.Map;

public class TextMap extends StringMap<String>
{
  public TextMap()
  {
  }

  public TextMap(Map<String, String> map)
  {
    this.putAll(map);
  }

  public TextMap(String[] keys, String[] values)
  {
    this.putAll(keys, values);
  }

  public TextMap append(String key, String value)
  {
    this.put(key, this.get(key, "") + value);
    return this;
  }

  public TextMap putAll(String[] keys, String[] values)
  {
    for (int i = 0; i < keys.length && i < values.length; i++)
      this.put(keys[i], values[i]);
    return this;
  }

  public TextMap putPairs(String... pairs)
  {
    for (int i = 0; i < pairs.length; i += 2)
      this.put(pairs[i], pairs[i + 1]);
    return this;
  }

  @Override
  public StringList list()
  {
    StringList list = new StringList();
    list.addAll(this.values());
    return list;
  }

  @Override
  public StringSet set()
  {
    StringSet set = new StringSet();
    set.addAll(this.values());
    return set;
  }

  public StringPair[] pairs()
  {
    StringPair[] pairs = new StringPair[this.size()];
    int i = 0;
    for (Map.Entry<String, String> e : entrySet())
      pairs[i++] = new StringPair(e.getKey(), e.getValue());
    return pairs;
  }
}
