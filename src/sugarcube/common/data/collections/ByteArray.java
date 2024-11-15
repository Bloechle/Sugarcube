
package sugarcube.common.data.collections;


import java.util.Collection;

public class ByteArray extends GrowingArray<Byte>
{
  protected byte[] values = null;

  public ByteArray(int capacity)
  {
    this.values = new byte[capacity];
  }

  public ByteArray(byte... codes)
  {
    super(0,codes.length);
    this.values = codes;
  }

  public ByteArray(Collection<Byte> codes)
  {
    super(0,codes.size());
    this.values = new byte[codes.size()];
    int index = 0;
    for (Byte code : codes)
      this.values[index++] = code;
  }
  
  @Override
  public int capacity()
  {
    return this.values.length;
  }  
  
  @Override
  public void setValueAt(int index, Byte value)
  {
    this.values[index] = value;
  }

  @Override
  public Byte valueAt(int index)
  {
    return this.values[index];
  }  

  public byte[] values()
  {
    return values;
  }

  private void ensureCapacity(int addedSize)
  {
    if (end + addedSize > this.values.length)
    {
      byte[] newCodes = new byte[(end - begin) * 2 + addedSize];
      System.arraycopy(values, begin, newCodes, 0, end - begin);
      this.values = newCodes;
      this.end -= this.begin;
      this.begin = 0;
    }
  }

  public ByteArray add(ByteArray codes)
  {
    if (codes != null)
    {
      byte[] newCodes = codes.array();
      this.ensureCapacity(newCodes.length);
      for (int i = 0; i < newCodes.length; i++)
        this.values[end++] = newCodes[i];
    }
    return this;
  }

  public ByteArray add(byte... newCodes)
  {
    this.ensureCapacity(newCodes.length);
    for (int i = 0; i < newCodes.length; i++)
      this.values[end++] = newCodes[i];
    return this;
  }

  // et oui, nous avons à faire à un array borné à gauche et à droite
  public ByteArray split(int index)
  {
    byte[] firstCodes = new byte[index];
    System.arraycopy(this.values, begin, firstCodes, 0, index);
    this.begin += index;
    return new ByteArray(firstCodes);
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

  public byte[] array()
  {
    byte[] trimmed = new byte[end - begin];
    System.arraycopy(this.values, begin, trimmed, 0, end - begin);
    return trimmed;
  }
  
  public byte[] array(int index, int size)
  {
    byte[] trimmed = new byte[size];
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

  public ByteArray copy()
  {
    return new ByteArray(array());
  }

  @Override
  public String toString()
  {
    return "ByteArray.toString... needs to be implemented :-p";
  }
}
