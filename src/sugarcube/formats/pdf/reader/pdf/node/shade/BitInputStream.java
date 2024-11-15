package sugarcube.formats.pdf.reader.pdf.node.shade;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends FilterInputStream
{
  private int bitPos;
  private int bpc;
  private int byteValue;
  private int pos;
  private final boolean doReverse;

  public BitInputStream(InputStream stream)
  {
    this(stream, false);
  }

  public BitInputStream(InputStream paramInputStream, boolean doReverse)
  {
    super(paramInputStream);
    this.setBpc(8);
    this.bitPos = 0;
    this.doReverse = doReverse;
  }

  public void setBpc(int bpc) throws IllegalArgumentException
  {
    if ((bpc < 1) || (bpc > 31))
      throw new IllegalArgumentException("BitField size " + bpc + " not between 1 and 31");
    this.bpc = bpc;
  }

  public int bpc()
  {
    return this.bpc;
  }

  public int readBpc()
    throws IOException
  {
    return readbits(this.bpc);
  }

  public int readbits(int size)
    throws IOException
  {
    if ((size < 1) || (size > 31))
      throw new IllegalArgumentException("numbits=" + size);
    int i = 0;
    int j = size;
    while (j > 0)
    {
      if (this.bitPos == 0)
      {
        if ((this.byteValue = this.in.read()) < 0)
          return -1;
        this.bitPos = 8;
      }
      int k = 0;
      int m = Math.min(j, this.bitPos);
      if (m == 8)
        k = this.byteValue;
      else
      {
        int n = (1 << m) - 1;
        int i1 = this.doReverse ? 8 - this.bitPos : this.bitPos - m;
        k = (this.byteValue & n << i1) >> i1;
      }
      this.bitPos -= m;
      i |= (k & 0xFF) << (this.doReverse ? size - j : j - m);
      j -= m;
    }
    this.pos += size;
    return i;
  }

  public long readLong(int size)
    throws IOException
  {
    if ((size < 1) || (size > 63))
      throw new IllegalArgumentException("numbits=" + size);
    long l1 = 0L;
    int i = size;
    while (i > 0)
    {
      if (this.bitPos == 0)
      {
        if ((this.byteValue = this.in.read()) < 0)
          return -1;
        this.bitPos = 8;
      }
      long l2 = 0;
      int j = Math.min(i, this.bitPos);
      if (j == 8)
        l2 = this.byteValue;
      else
      {
        int k = (1 << j) - 1;
        int m = this.doReverse ? 8 - this.bitPos : this.bitPos - j;
        l2 = (this.byteValue & k << m) >> m;
      }
      this.bitPos -= j;
      l1 |= (l2 & 0xFF) << (this.doReverse ? size - i : i - j);
      i -= j;
    }
    this.pos += size;
    return l1;
  }

  public int read()
    throws IOException
  {
    int i = bpc();
    setBpc(8);
    int j;
    try
    {
      j = readBpc();
    }
    finally
    {
      setBpc(i);
    }
    return j;
  }

//  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException
//  {
//    if (paramInt2 <= 0)
//      return 0;
//    int i = read();
//    if (i == -1)
//      return -1;
//    paramArrayOfByte[paramInt1] = (byte) i;
//    for (int j = 1; j < paramInt2; j++)
//    {
//      i = read();
//      if (i == -1)
//        break;
//      if (paramArrayOfByte == null)
//        continue;
//      paramArrayOfByte[(paramInt1 + j)] = (byte) i;
//    }
//    return j;
//  }
  @Override
  public int read(byte[] stream) throws IOException
  {
    return read(stream, 0, stream.length);
  }

  public int tell()
  {
    return this.pos;
  }

  @Override
  public long skip(long size) throws IOException
  {
    long i = 0;
    while ((i < size) && (read() >= 0))
      i++;
    return i;
  }
}

/* Location:           C:\data\archive\bfopdf-2.11.15\bfopdf.jar
 * Qualified Name:     org.faceless.util.BitStreamInputStream
 * JD-Core Version:    0.6.0
 */