package sugarcube.common.data.collections;

import java.io.Serializable;

public abstract class GrowingArray<T> implements Serializable, Cloneable
{
  protected int begin = 0; // included
  protected int end = 0; // not included

  public GrowingArray()
  {
  }

  public GrowingArray(int begin, int end)
  {
    this.begin = begin;
    this.end = end;
  }

  // public GrowingArray(int capacity, boolean zeroLength)
  // {
  // this.begin = 0;
  // this.end = zeroLength ? 0 : capacity;
  // }

  public abstract int capacity();

  public abstract T valueAt(int index);

  public abstract void setValueAt(int index, T o);

  public int begin()
  {
    return this.begin;
  }

  public int end()
  {
    return this.end;
  }

  public boolean isEmpty()
  {
    return begin == end;
  }

  public int length()
  {
    return end - begin;
  }

  public void removeLast()
  {
    if (end > begin)
      end--;
  }

  public void removeFirst()
  {
    if (begin < end)
      begin++;
  }

  public T first()
  {
    return this.valueAt(begin);
  }

  public T last()
  {
    return this.valueAt(end - 1);
  }

  public boolean endsWith(T... lasts)
  {
    for (int i = 0; i < lasts.length; i++)
    {
      int j = end - i - 1;
      if (!(j >= begin && j < end && lasts[lasts.length - i - 1].equals(valueAt(j))))
      {
        return false;
      }
    }
    return true;
  }

  public String stringValue()
  {
    return toString();
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    GrowingArray that = (GrowingArray) o;
    if (this.length() != that.length())
      return false;
    for (int i = 0; i < this.length(); i++)
      if (!this.valueAt(this.begin + i).equals(that.valueAt(that.begin + i)))
        return false;
    return true;
  }

  @Override
  public String toString()
  {
    return "GrowingArray.toString... needs to be implemented :-p";
  }
}