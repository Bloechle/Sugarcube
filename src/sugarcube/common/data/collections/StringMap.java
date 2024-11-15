package sugarcube.common.data.collections;

import java.util.Collections;
import java.util.Map;

public class StringMap<V> extends Map3<String, V>
{
  public StringMap()
  {
  }

  public StringMap(int capacity)
  {
    super(capacity);
  }

  public StringMap(Map<String, V> map)
  {
    this.putAll(map);
  }

  public StringMap(String[] keys, V[] values)
  {
    for (int i = 0; i < keys.length && i < values.length; i++)
      this.put(keys[i], values[i]);
  }

  public StringSet keys()
  {
    StringSet set = new StringSet();
    set.addAll(this.keySet());
    return set;
  }

  @Override
  public StringList keyList()
  {
    StringList list = new StringList();
    list.addAll(this.keySet());
    return list;
  }

  public String[] keyArray()
  {
    return keys().array();
  }

  public StringMap<V> sortByKey()
  {
    StringList keys = this.keyList();
    Collections.sort(keys);
    StringMap<V> sorted = new StringMap<V>();
    for (String key : keys)
      sorted.put(key, this.get(key));
    this.clear();
    this.putAll(sorted);
    return this;
  }

}
