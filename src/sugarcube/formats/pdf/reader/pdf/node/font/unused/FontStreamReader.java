package sugarcube.formats.pdf.reader.pdf.node.font.unused;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;
import sugarcube.formats.pdf.reader.pdf.object.StreamReader;

public class FontStreamReader extends StreamReader
{
  public FontStreamReader(PDFStream stream)
  {
    super(stream);
  }

  public final long getFilePointer()
  {
    return this.pos - this.begin;
  }

  public final byte readByte()
  {
    return (byte) read(pos);
  }

  public final int readUnsignedByte()
  {
    return this.read();
  }

  public final short readShort()
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
      Log.warn(this, ".readShort - negative value should not happend");
    return (short) ((ch1 << 8) + ch2);
  }

  public final short readShortLE()
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
      Log.warn(this, ".readShortLE - negative value should not happend");
    return (short) ((ch2 << 8) + (ch1 << 0));
  }

  public int readUnsignedShort()
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
      Log.warn(this, ".readUnsignedShort - negative value should not happend");
    return (ch1 << 8) + ch2;
  }

  public final int readUnsignedShortLE()
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
      Log.warn(this, ".readUnsignedShortLE - negative value should not happend");
    return (ch2 << 8) + (ch1 << 0);
  }

  public char readChar()
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
      Log.warn(this, ".readChar - negative value should not happend");
    return (char) ((ch1 << 8) + ch2);
  }

  public final char readCharLE()
  {
    int ch1 = this.read();
    int ch2 = this.read();
    if ((ch1 | ch2) < 0)
      Log.warn(this, ".readCharLE - negative value should not happend");
    return (char) ((ch2 << 8) + (ch1 << 0));
  }

  public final int readIntLE()
  {
    int ch1 = this.read();
    int ch2 = this.read();
    int ch3 = this.read();
    int ch4 = this.read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      Log.warn(this, ".readIntLE - negative value should not happend");
    return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
  }

  public final long readUnsignedInt()
  {
    long ch1 = this.read();
    long ch2 = this.read();
    long ch3 = this.read();
    long ch4 = this.read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      Log.warn(this, ".readUnsignedInt - negative value should not happend");
    return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
  }

  public final long readUnsignedIntLE()
  {
    long ch1 = this.read();
    long ch2 = this.read();
    long ch3 = this.read();
    long ch4 = this.read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      Log.warn(this, ".readUnsignedIntLE - negative value should not happend");
    return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
  }

  public long readLong()
  {
    return ((long) (readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
  }

  public final long readLongLE()
  {
    int i1 = readIntLE();
    int i2 = readIntLE();
    return ((long) i2 << 32) + (i1 & 0xFFFFFFFFL);
  }

  public float readFloat()
  {
    return Float.intBitsToFloat(readInt());
  }

  public final float readFloatLE()
  {
    return Float.intBitsToFloat(readIntLE());
  }

  public double readDouble()
  {
    return Double.longBitsToDouble(readLong());
  }

  public final double readDoubleLE()
  {
    return Double.longBitsToDouble(readLongLE());
  }

  public int readInt()
  {
    int ch1 = this.read();
    int ch2 = this.read();
    int ch3 = this.read();
    int ch4 = this.read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      Log.warn(this, ".readInt - negative value should not happend");
    return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4);
  }
}
