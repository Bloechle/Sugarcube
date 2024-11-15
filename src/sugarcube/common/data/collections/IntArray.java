package sugarcube.common.data.collections;

import sugarcube.common.data.Zen;

import java.util.Arrays;
import java.util.Collection;

public class IntArray extends GrowingArray<Integer>
{
  protected int[] values = null;

  public IntArray()
  {
    this(15);
  }

  public IntArray(int capacity)
  {
    this.values = new int[capacity];
  }

  public IntArray(int[] values)
  {
    super(0, values.length);
    this.values = values;
  }

  public IntArray(Collection<Integer> codes)
  {
    super(0, codes.size());
    this.values = new int[codes.size()];
    int index = 0;
    for (Integer code : codes)
      this.values[index++] = code;
  }

  @Override
  public int capacity()
  {
    return this.values.length;
  }

  @Override
  public void setValueAt(int index, Integer value)
  {
    this.values[index] = value;
  }

  @Override
  public Integer valueAt(int index)
  {
    return this.values[index];
  }

  public int[] values()
  {
    return values;
  }

  private void ensureCapacity(int addedSize)
  {
    if (end + addedSize > this.values.length)
    {
      int[] newCodes = new int[(end - begin) * 2 + addedSize];
      System.arraycopy(values, begin, newCodes, 0, end - begin);
      this.values = newCodes;
      this.end -= this.begin;
      this.begin = 0;
    }
  }

  public IntArray add(IntArray codes)
  {
    if (codes != null)
    {
      int[] newCodes = codes.array();
      this.ensureCapacity(newCodes.length);
      for (int i = 0; i < newCodes.length; i++)
        this.values[end++] = newCodes[i];
    }
    return this;
  }

  public IntArray add(int... newCodes)
  {
    this.ensureCapacity(newCodes.length);
    for (int i = 0; i < newCodes.length; i++)
      this.values[end++] = newCodes[i];
    return this;
  }

//  public IntArray add(float[] newCodes)
//  {
//    this.ensureCapacity(newCodes.length);
//    for (int i = 0; i < newCodes.length; i++)
//      this.values[end++] = Float.floatToRawIntBits(newCodes[i]);
//    return this;
//  }
  // et oui, nous avons à faire à un array borné à gauche et à droite
  public IntArray split(int index)
  {
    int[] firstCodes = new int[index];
    System.arraycopy(this.values, begin, firstCodes, 0, index);
    this.begin += index;
    return new IntArray(firstCodes);
  }

  public float floatValueAtIndex(int arrayIndex)
  {
    return Float.intBitsToFloat(this.values[arrayIndex]);
  }

  public float floatValueAt(int shiftedIndex)
  {
    return Float.intBitsToFloat(this.values[begin + shiftedIndex]);
  }

  public void trim()
  {
    this.values = array();
    this.begin = 0;
    this.end = values.length;
  }
  
  public void trim(int index, int size)
  {
    this.values = array(index, size);
    this.begin = 0;
    this.end = values.length;
  }    
  
  public void sort()
  {
    trim();
    Arrays.sort(values);    
  }    
  
  public float mean()
  {
    float mean = 0;
    for (int i = begin; i < end; i++)
      mean += values[i];
    return mean / (end - begin);
  }  

  public int[] array()
  {
    int[] trimmed = new int[end - begin];
    System.arraycopy(this.values, begin, trimmed, 0, end - begin);
    return trimmed;
  }
  
  public int[] array(int index, int size)
  {
    int[] trimmed = new int[size];
    System.arraycopy(this.values, begin+index, trimmed, 0, size);
    return trimmed;
  }    

  @Override
  public int hashCode()
  {
    int result = 1;
    for (int i = begin; i < end; i++)
      result = 31 * result + values[i];
    return result;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    IntArray that = (IntArray) o;
    if (this.length() != that.length())
      return false;
    for (int i = 0; i < this.length(); i++)
      if (this.values[this.begin + i] != that.values[that.begin + i])
        return false;
    return true;
  }

  public IntArray copy()
  {
    return new IntArray(array());
  }

  @Override
  public String toString()
  {
    return Zen.Array.String(array());
  }
}
