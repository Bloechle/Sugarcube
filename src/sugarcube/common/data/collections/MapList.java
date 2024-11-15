package sugarcube.common.data.collections;

public class MapList<K, V> extends Map3<K, List3<V>>
{

  public MapList()
  {
  }   

  public V listFirst(K key, V def)
  {
    List3<V> list = this.get(key, null);
    return list == null || list.isEmpty() ? def : list.first();
  }

  // public boolean contains(K... keys)
  // {
  // for (K key : keys)
  // if (super.containsKey(key))
  // return true;
  // return false;
  // }

  // public List3<V> net(K... keys)
  // {
  // Set<K> set = new HashSet<K>();
  // for (K key : keys)
  // set.add(key);
  // List3<V> list = new List3<V>();
  // for (K key : super.keySet())
  // if (!set.contains(key))
  // list.addAll(super.get(key));
  // return list;
  // }
  //
  // public List3<V> get(K... keys)
  // {
  // List3<V> list = new List3<V>();
  // if (keys.length == 0)
  // for (List3<V> value : super.values())
  // list.addAll(value);
  // else
  // for (K key : keys)
  // if (super.containsKey(key))
  // list.addAll(super.get(key));
  // return list;
  // }

  public List3<V> need(K key)
  {
    List3<V> list = this.get(key, null);
    if (list == null)
      this.put(key, list = new List3<V>());
    return list;
  }

  public List3<V> add(K key, V value)
  {
    List3<V> list = need(key);
    list.add(value);
    return list;
  }

}
