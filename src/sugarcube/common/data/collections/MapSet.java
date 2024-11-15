package sugarcube.common.data.collections;

public class MapSet<K, V> extends Map3<K, Set3<V>>
{
  public MapSet<K, V> add(K key, V value)
  {
    need(key).add(value);
    return this;
  }

  public Set3<V> need(K key)
  {
    Set3<V> set = this.get(key, null);
    if (set == null)
      this.put(key, set = new Set3<V>());
    return set;
  }
}
