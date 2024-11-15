package sugarcube.common.data.collections;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class TreeList<K,V>
{
  private TreeMap<K,LinkedList<V>> map = new TreeMap<K,LinkedList<V>>();

  public TreeList()
  {
  }

  public LinkedList<V> get(K key)
  {
    return map.get(key);
  }

  public LinkedList<V> put(K key, V value)
  {
    if(map.containsKey(key))
    {
      LinkedList<V> list = map.get(key);
      list.add(value);
      return list;
    }
    else
    {
      LinkedList<V> list = new LinkedList<V>();
      list.add(value);
      map.put(key,list);
      return null;
    }
  }

  public V first()
  {
    return map.firstEntry().getValue().getFirst();
  }

  public V last()
  {
    return map.lastEntry().getValue().getLast();
  }

  public LinkedList<V> pollFirsts(int n)
  {
    LinkedList<V> firsts = new LinkedList<V>();
    while(firsts.size()<n && map.size()>0)
    {
      Map.Entry<K,LinkedList<V>> entry = map.pollFirstEntry();
      LinkedList<V> list = entry.getValue();
      while(firsts.size()<n && list.size()>0)
        firsts.add(list.pollFirst());

      if(!list.isEmpty())
        map.put(entry.getKey(),list);
    }
    return firsts;
  }

  public LinkedList<V> pollLasts(int n)
  {
    LinkedList<V> lasts = new LinkedList<V>();
    while(lasts.size()<n && map.size()>0)
    {
      Map.Entry<K,LinkedList<V>> entry = map.pollLastEntry();
      LinkedList<V> list = entry.getValue();
      while(lasts.size()<n && list.size()>0)
        lasts.add(list.pollLast());

      if(!list.isEmpty())
        map.put(entry.getKey(),list);
    }
    return lasts;
  }

}
