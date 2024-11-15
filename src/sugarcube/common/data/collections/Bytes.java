package sugarcube.common.data.collections;


public class Bytes extends ByteArray
{

  public Bytes(int capacity)
  {
    super(capacity);
  }
  
  public static int[] toInts(byte[] data)
  {
    int[] ints = new int[data.length];
    for(int i=0; i<data.length; i++)
      ints[i] = data[i] & 0xff;
    return ints;
  }

}
