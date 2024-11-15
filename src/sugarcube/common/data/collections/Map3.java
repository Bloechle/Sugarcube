package sugarcube.common.data.collections;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Map3<K, V> extends LinkedHashMap<K, V> implements Iterable<V>
{
  public Map3()
  {
  }

  public Map3(int initialCapacity)
  {
    super(initialCapacity);
  }

  public Map3(Map<K, V> map)
  {
    this.putAll(map);
  }

  public Map3(K[] keys, V[] values)
  {
    for (int i = 0; i < keys.length && i < values.length; i++)
      this.put(keys[i], values[i]);
  }

  public void sort(Comparator<K> comparator)
  {
    List3<K> keys = keyList();
    keys.sort(comparator);
    Map3<K, V> sorted = new Map3<K, V>();
    for (K key : keys)
      sorted.put(key, get(key));
    this.clear();
    this.putAll(sorted);
  }

  public boolean isPopulated()
  {
    return !this.isEmpty();
  }

  public Map.Entry<K, V> entryAt(int index)
  {
    Iterator<Map.Entry<K, V>> iterator = this.entrySet().iterator();
    int counter = 0;
    Map.Entry<K, V> entry = null;
    while (iterator.hasNext() && counter++ < index + 1)
      entry = iterator.next();
    return entry;
  }

  public K keyAt(int index)
  {
    Map.Entry<K, V> entry = entryAt(index);
    return entry == null ? null : entry.getKey();
  }

  public V valueAt(int index)
  {
    Map.Entry<K, V> entry = entryAt(index);
    return entry == null ? null : entry.getValue();
  }

  public Map.Entry<K, V> firstEntry()
  {
    return this.isEmpty() ? null : this.entrySet().iterator().next();
  }

  public V firstValue()
  {
    return this.isEmpty() ? null : this.entrySet().iterator().next().getValue();
  }

  public K firstKey()
  {
    return this.isEmpty() ? null : this.entrySet().iterator().next().getKey();
  }

  public V removeFirst()
  {
    return this.isEmpty() ? null : this.remove(this.firstKey());
  }

  public Map.Entry<K, V> lastEntry()
  {
    Map.Entry<K, V> last = null;
    Iterator<Map.Entry<K, V>> it = this.entrySet().iterator();
    while (it.hasNext())
      last = it.next();
    return last;
  }

  public K lastKey()
  {
    Map.Entry<K, V> last = lastEntry();
    return last == null ? null : last.getKey();
  }

  public V lastValue()
  {
    Map.Entry<K, V> last = lastEntry();
    return last == null ? null : last.getValue();
  }

  public V removeLast()
  {
    return this.isEmpty() ? null : this.remove(this.lastKey());
  }

  public V ensureSize(int maxSize)
  {
    return this.size() < maxSize ? null : this.remove(this.firstKey());
  }

  public Map3<K, V> renameKey(K oldKey, K newKey)
  {
    if (has(oldKey) && oldKey != newKey && (oldKey == null || !oldKey.equals(newKey)))
    {
      put(newKey, get(oldKey));
      remove(oldKey);
    }
    return this;
  }

  public Map3 removeAll(K... keys)
  {
    for (K key : keys)
      this.remove(key);
    return this;
  }

  public Map3 removeAll(Iterable<K> keys)
  {
    for (K key : keys)
      this.remove(key);
    return this;
  }

  public Map3 removeValues(Iterable<V> values)
  {
    Set3<V> set = new Set3<V>().addIterable(values);

    Iterator<Map.Entry<K, V>> it = this.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry<K, V> entry = it.next();
      if (entry != null && set.has(entry.getValue()))
        it.remove();
    }
    return this;
  }

  public void putNonNull(K key, V value)
  {
    if (value != null)
      this.put(key, value);
  }

  public void putNonVoid(K key, V value)
  {
    if (value != null && !value.toString().isEmpty())
      this.put(key, value);
  }

  public void putNonNull(Map<K, V> map)
  {
    for (Map.Entry<K, V> p : map.entrySet())
      putNonNull(p.getKey(), p.getValue());
  }

  public void putNonVoid(Map<K, V> map)
  {
    for (Map.Entry<K, V> p : map.entrySet())
      putNonVoid(p.getKey(), p.getValue());
  }

  public V need(K key, V def)
  {
    if (hasnt(key))
      put(key, def);
    return get(key);
  }

  public V get(K key, V def)
  {
    return has(key) ? get(key) : def;
  }

  public V first(K... keys)
  {
    if(keys.length==0)
      return firstValue();
    for (K key : keys)
      if (has(key))
        return get(key);
    return null;
  }

  public K check(K key)
  {
    return has(key) ? key : null;
  }

  public K check(K key, K def)
  {
    return has(key) ? key : def;
  }

  public boolean has(K key)
  {
    return this.containsKey(key);
  }

  public boolean hasnt(K key)
  {
    return !this.containsKey(key);
  }

  public boolean hasOne(K... keys)
  {
    for (K key : keys)
      if (this.containsKey(key))
        return true;
    return false;
  }

  public boolean hasAll(K... keys)
  {
    for (K key : keys)
      if (!this.containsKey(key))
        return false;
    return true;
  }

  public boolean has(Map<K, V> map)
  {
    for (Map.Entry<K, V> entry : map.entrySet())
      if (!has(entry.getKey(), entry.getValue()))
        return false;
    return true;
  }

  public boolean has(K key, V value)
  {
    if (hasnt(key))
      return false;
    V v = get(key);
    return v == value || v != null && v.equals(value);
  }

  public boolean hasnt(K key, V value)
  {
    return !has(key, value);
  }

  public List3<K> keyList()
  {
    List3<K> list = new List3<>();
    list.addAll(this.keySet());
    return list;
  }

  public List3<V> list()
  {
    List3<V> list = new List3<>();
    list.addAll(this.values());
    return list;
  }

  public Set3<V> set()
  {
    Set3<V> set = new Set3<>();
    set.addAll(this.values());
    return set;
  }

  public Couple<K, V>[] couples()
  {
    Couple<K, V>[] couples = new Couple[this.size()];
    int i = 0;
    for (Map.Entry<K, V> e : this.entrySet())
      couples[i++] = new Couple<K, V>(e.getKey(), e.getValue());
    return couples;
  }

  @Override
  public Iterator<V> iterator()
  {
    return this.values().iterator();
  }

  public Iterator<Map.Entry<K, V>> entryIterator()
  {
    return this.entrySet().iterator();
  }

}
