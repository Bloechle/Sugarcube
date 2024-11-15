package sugarcube.common.data.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Array3<T> extends ArrayList<T>
{
  public Array3()
  {
    
  }

  public Array3(Collection<T> collection)
  {
    super(collection);
  }

  public Array3(int initialCapacity)
  {
    super(initialCapacity);
  }
  
  public Array3<T> add3(T value)
  {
    this.add(value);
    return this;
  }

  public Array3<T> addAll(T... values)
  {
    for (T value : values)
      this.add(value);
    return this;
  }

  public boolean isPopulated()
  {
    return !this.isEmpty();
  }

  public boolean hasIndex(int index)
  {
    return index >= 0 && index < size();
  }

  public T $(int index)
  {
    return get(index);
  }

  public T get(int index, T def)
  {
    return hasIndex(index) ? get(index) : def;
  }

  public T first()
  {
    return this.isEmpty() ? null : this.get(0);
  }

  public T last()
  {
    return this.isEmpty() ? null : this.get(size() - 1);
  }

  public int insert(int i, T o)
  {
    i = i < 0 ? 0 : i > size() ? size() : i;
    super.add(i, o);
    return i;
  }

  public T remove()
  {
    return remove(size() - 1);
  }

  public T removeLast()
  {
    return remove();
  }

  public T removeFirst()
  {
    return remove(0);
  }
  
  
  public Array3<T> reverse()
  {
    Collections.reverse(this);
    return this;
  }
    


  public String string(String separator, String escape)
  {
    int counter = 0;
    int size = this.size();

    T first = this.first();
    T last = this.last();

    StringBuilder sb = new StringBuilder(
        ((first == null ? 10 : first.toString().length()) + (last == null ? 10 : last.toString().length())) * this.size());

    for (T value : this)
    {
      sb.append(escape == null ? (value == null ? "" : value.toString()) : (value == null ? "" : value.toString()).replace(separator, escape));
      sb.append(++counter < size ? separator : "");
    }

    return sb.toString();
  }

  public static String[] concat(String[] a, String... b)
  {
    if (a == null || a.length == 0)
      return b;
    else if (b == null || b.length == 0)
      return a;
    String[] c = new String[a.length + b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, c.length, b.length);
    return c;
  }

  public static String[] copy(String[] data)
  {
    if (data == null)
      return null;
    String[] copy = new String[data.length];
    System.arraycopy(data, 0, copy, 0, data.length);
    return copy;
  }

  public static boolean has(Object[] data, Object value)
  {
    for (Object o : data)
      if (o.equals(value))
        return true;
    return false;
  }

  public static boolean has(int[] data, int value)
  {
    for (int o : data)
      if (o == value)
        return true;
    return false;
  }

  public static int indexOf(Object[] data, Object value)
  {
    for (int i = 0; i < data.length; i++)
      if (data[i].equals(value))
        return i;
    return -1;
  }

}
