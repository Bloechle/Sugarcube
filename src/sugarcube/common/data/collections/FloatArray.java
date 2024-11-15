package sugarcube.common.data.collections;

import sugarcube.common.data.Zen;

import java.util.Arrays;
import java.util.Collection;

public class FloatArray extends GrowingArray<Float>
{
  protected float[] values = null;

  public FloatArray(int capacity)
  {
    this.values = new float[capacity];
  }

  public FloatArray(float[] data)
  {
    super(0, data.length);
    this.values = data;
  }

  public FloatArray(Collection<Float> data)
  {
    super(0, data.size());
    this.values = new float[data.size()];
    int index = 0;
    for (Float d : data)
      this.values[index++] = d;
  }

  @Override
  public int capacity()
  {
    return this.values.length;
  }
  
  public float get(int index)
  {
    return values[index-begin];
  }

  @Override
  public Float valueAt(int index)
  {
    return values[index];
  }

  @Override
  public void setValueAt(int index, Float value)
  {
    this.values[index] = value;
  }

  public float[] values()
  {
    return values;
  }

  private void ensureCapacity(int addedSize)
  {
    if (end + addedSize > this.values.length)
    {
      float[] newCodes = new float[(end - begin) * 2 + addedSize];
      System.arraycopy(values, begin, newCodes, 0, end - begin);
      this.values = newCodes;
      this.end -= this.begin;
      this.begin = 0;
    }
  }

  public FloatArray add(FloatArray codes)
  {
    if (codes != null)
    {
      float[] newCodes = codes.array();
      this.ensureCapacity(newCodes.length);
      for (int i = 0; i < newCodes.length; i++)
        this.values[end++] = newCodes[i];
    }
    return this;
  }

  public FloatArray add(float... newCodes)
  {
    this.ensureCapacity(newCodes.length);
    for (int i = 0; i < newCodes.length; i++)
      this.values[end++] = newCodes[i];
    return this;
  }

  // et oui, nous avons à faire à un array borné à gauche et à droite
  public FloatArray split(int index)
  {
    float[] firstCodes = new float[index];
    System.arraycopy(this.values, begin, firstCodes, 0, index);
    this.begin += index;
    return new FloatArray(firstCodes);
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

  public float[] array()
  {
    float[] trimmed = new float[end - begin];
    System.arraycopy(this.values, begin, trimmed, 0, end - begin);
    return trimmed;
  }

  public float[] array(int index, int size)
  {
    float[] trimmed = new float[size];
    System.arraycopy(this.values, begin + index, trimmed, 0, size);
    return trimmed;
  }

  @Override
  public int hashCode()
  {
    int result = 1;
    for (int i = begin; i < end; i++)
      result = 31 * result + Float.floatToRawIntBits(values[i]);
    return result;
  }

  @Override
  public Object clone()
  {
    return copy();
  }

  public FloatArray copy()
  {
    return new FloatArray(array());
  }

  @Override
  public String toString()
  {
    return Zen.Array.String(array());
  }
}