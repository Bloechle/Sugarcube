package sugarcube.formats.pdf.reader.pdf.node.image;

public class ZoubitReader
{
  public byte[] data;
  public int bpc;
  public boolean reverse;
  public long pos;//in bits (not bytes)

  public ZoubitReader(byte[] data, int bpc)
  {
    this.data = data;
    this.bpc = bpc;
  }

  public int[] read(int size)
  {
    int[] values = new int[size];
    for (int i = 0; i < size; i++)
      values[i] = read();
    return values;
  }
  
  public int[] read(int size, int factor)
  {
    int[] values = new int[size];
    for (int i = 0; i < size; i++)
      values[i] = read()*factor;
    return values;
  }  

  public int view()
  {
    int read = read();
    pos -= bpc;
    return read;
  }

  public int read()
  {
//    if(data.length==0)
//      return -1;
    int bits = 0;
    if (bpc == 8)
    {
      int byteIndex = (int) (pos / 8);
      bits = data[byteIndex < data.length ? byteIndex : data.length - 1] & 0xff;
    }
    else if (bpc == 4)
    {
      int byteIndex = (int) (pos / 8);
      bits = data[byteIndex < data.length ? byteIndex : data.length - 1] >> (pos % 8 == 0 ? 4 : 0) & 0x0f;
    }
    else
      for (int i = 0; i < bpc; i++)
      {
        int byteIndex = (int) ((pos + i) / 8);
        int bitIndex = (int) (8 - (pos + i) % 8);
        bits |= (data[byteIndex] & (1L << bitIndex));
      }
    pos += bpc;
    return bits;
  }

  public int byteAlign()
  {
    long old = pos;
    if (pos % 8 != 0)
      pos = (pos / 8) * 8 + 8;
    return (int) (pos - old);
  }
}
