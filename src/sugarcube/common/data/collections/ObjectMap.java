package sugarcube.common.data.collections;

import java.util.Map;

public class ObjectMap extends StringMap<Object>
{
  public ObjectMap()
  {
  }

  public ObjectMap(Map<String, Object> map)
  {
    this.putAll(map);
  }

  public ObjectMap(String[] keys, Object[] values)
  {
    for (int i = 0; i < keys.length && i < values.length; i++)
      this.put(keys[i], values[i]);
  }
}
