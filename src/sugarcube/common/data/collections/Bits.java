package sugarcube.common.data.collections;

import sugarcube.common.system.log.Log;

import java.util.BitSet;

public class Bits extends BitSet
{
  // the number of bits effectively in use...
  private int size = 0;

  // TODO override parent method that modifies list size!!!
  // should optimize parts of code, with ensureCapacity
  public Bits()
  {
    super();
  }

  public Bits(byte[] bytes)
  {
    this(bytes, bytes.length * 8);
  }

  public Bits(byte[] bytes, int nbOfBits)
  {
    super(bytes.length * 8 < nbOfBits ? bytes.length * 8 : nbOfBits);
    for (int i = 0; i < bytes.length * 8 && i < nbOfBits; i++)
      if ((bytes[bytes.length - i / 8 - 1] & (1 << 8 - i % 8)) > 0)
        set(i);
    this.size = bytes.length * 8 < nbOfBits ? bytes.length * 8 : nbOfBits;
  }

  public byte byteChunk(int nbOfBits)
  {
    int index = 0;
    byte chunk = 0;
    for (int i = 0; i < nbOfBits; i++)
      if (get(index + i))
        chunk |= 1 << (nbOfBits - 1 - i % nbOfBits);
    index += nbOfBits;
    return chunk;
  }

  public byte[] byteChunks(int nbOfBits, int nbOfChunks)
  {
    byte[] chunks = new byte[nbOfChunks];
    for (int i = 0; i < chunks.length; i++)
      chunks[i] = byteChunk(nbOfBits);
    return chunks;
  }

  public int intChunk(int nbOfBits)
  {
    int index = 0;
    int chunk = 0;
    for (int i = 0; i < nbOfBits; i++)
      if (get(index + i))
        chunk |= 1 << (nbOfBits - 1 - i % nbOfBits);
    index += nbOfBits;
    return chunk;
  }

  public int[] intChunks(int nbOfBits, int nbOfChunks)
  {
    int[] chunks = new int[nbOfChunks];
    for (int i = 0; i < chunks.length; i++)
      chunks[i] = intChunk(nbOfBits);
    return chunks;
  }

  public Bits append(byte bits)
  {
    for (int i = 0; i < 8; i++)
      this.append(((bits >> i) & 1) > 0);
    return this;
  }

  public Bits append(byte[] bits, int length)
  {
    int index = 0;
    for (int b = 0; b < bits.length; b++)
      for (int i = 0; i < 8 && index++ < length; i++)
        this.append(((bits[b] >> i) & 1) > 0);
    return this;
  }

  public Bits append(Bits bits)
  {
    if (bits == null)
      Log.error(this, "append, tried to append null BitList");
    for (int i = 0; i < bits.size; i++)
      append(bits.get(i));
    return this;
  }

  public Bits append(boolean... values)
  {
    for (boolean value : values)
      append(value);
    return this;
  }

  public Bits append(boolean value)
  {
    set(size++, value);
    return this;
  }

  @Override
  public int size()
  {
    return size;
  }

  public void setSize(int size)
  {
    this.size = size;
  }

  public Bits copy()
  {
    Bits bits = new Bits();
    return bits.append(this);
  }

  /**
   * Returns a byte array of at least length 1. The most significant bit in the
   * result is guaranteed not to be a 1 (since BitSet does not support sign
   * extension). The byte-ordering of the result is big-endian which means the
   * most significant bit is in element 0. The bit at index 0 of the bit set is
   * assumed to be the least significant bit.
   * 
   * @return byte[] the byte values of this bit list.
   */
  public byte[] byteValues()
  {
    byte[] bytes = new byte[(size - 1) / 8 + 1];
    for (int i = 0; i < size; i++)
      if (get(i))
        bytes[bytes.length - i / 8 - 1] |= 1 << (8 - i % 8);
    return bytes;
  }

  @Override
  public String toString()
  {
    StringBuilder bits = new StringBuilder(size());
    for (int i = 0; i < size(); i++)
      bits.append(get(i) ? '1' : '0');
    return bits.toString();
  }

  public static boolean bit(int bits, int index)
  {
    return (bits & (1 << index)) > 0;
  }

  public static int setBit(int bits, int index, boolean value)
  {
    int mask = 1 << index;
    boolean b = (bits & mask) > 0;
    return b == value ? bits : b ? bits - mask : bits + mask;
  }

  /**
   * Returns the {@code float} value corresponding to a given bit
   * representation. The argument is considered to be a representation of a
   * floating-point value according to the IEEE 754 floating-point
   * "single format" bit layout.
   * 
   * <p>
   * If the argument is {@code 0x7f800000}, the result is positive infinity.
   * 
   * <p>
   * If the argument is {@code 0xff800000}, the result is negative infinity.
   * 
   * <p>
   * If the argument is any value in the range {@code 0x7f800001} through
   * {@code 0x7fffffff} or in the range {@code 0xff800001} through
   * {@code 0xffffffff}, the result is a NaN. No IEEE 754 floating-point
   * operation provided by Java can distinguish between two NaN values of the
   * same type with different bit patterns. Distinct values of NaN are only
   * distinguishable by use of the {@code Float.floatToRawIntBits} method.
   * 
   * <p>
   * Note that this method may not be able to return a {@code float} NaN with
   * exactly same bit pattern as the {@code int} argument. IEEE 754
   * distinguishes between two kinds of NaNs, quiet NaNs and <i>signaling
   * NaNs</i>. The differences between the two kinds of NaN are generally not
   * visible in Java. Arithmetic operations on signaling NaNs turn them into
   * quiet NaNs with a different, but often similar, bit pattern. However, on
   * some processors merely copying a signaling NaN also performs that
   * conversion. In particular, copying a signaling NaN to return it to the
   * calling method may perform this conversion. So {@code intBitsToFloat} may
   * not be able to return a {@code float} with a signaling NaN bit pattern.
   * Consequently, for some {@code int} values,
   * {@code floatToRawIntBits(intBitsToFloat(start))} may <i>not</i> equal
   * {@code start}. Moreover, which particular bit patterns represent signaling
   * NaNs is platform dependent; although all NaN bit patterns, quiet or
   * signaling, must be in the NaN range identified above.
   * 
   * @param bits
   *          an integer.
   * @return the {@code float} floating-point value with the same bit pattern.
   */
  public static float intBitsToFloat(int bits)
  {
    if (bits == 0x7f800000)
      return Float.POSITIVE_INFINITY;
    if (bits == 0xff800000)
      return Float.NEGATIVE_INFINITY;

    if ((bits >= 0x7f800001 && bits <= 0x7fffffff) || (bits >= 0xff800001 && bits <= 0xffffffff))
      return Float.NaN;

    int s = ((bits >> 31) == 0) ? 1 : -1;
    int e = ((bits >> 23) & 0xff);
    int m = bits & 0x7fffff;

    double mf = m / java.lang.Math.pow(2, 23) + 1;
    double result = s * mf * java.lang.Math.pow(2, e - 127);

    return (float) result;
  }

  /**
   * Returns a representation of the specified floating-point value according to
   * the IEEE 754 floating-point "single format" bit layout.
   * 
   * <p>
   * Bit 31 (the bit that is selected by the mask {@code 0x80000000}) represents
   * the sign of the floating-point number. Bits 30-23 (the bits that are
   * selected by the mask {@code 0x7f800000}) represent the exponent. Bits 22-0
   * (the bits that are selected by the mask {@code 0x007fffff}) represent the
   * significand (sometimes called the mantissa) of the floating-point number.
   * 
   * <p>
   * If the argument is positive infinity, the result is {@code 0x7f800000}.
   * 
   * <p>
   * If the argument is negative infinity, the result is {@code 0xff800000}.
   * 
   * <p>
   * If the argument is NaN, the result is {@code 0x7fc00000}.
   * 
   * <p>
   * In all cases, the result is an integer that, when given to the
   * {@link #intBitsToFloat(int)} method, will produce a floating-point value
   * the same as the argument to {@code floatToIntBits} (except all NaN values
   * are collapsed to a single "canonical" NaN value).
   * 
   * @param value
   *          a floating-point number.
   * @return the bits that represent the floating-point number.
   */
  public static int floatToIntBits(float orig_value)
  {

    double value = orig_value; // Keeps more precision.

    if (orig_value == Float.POSITIVE_INFINITY)
      return 0x7f800000;
    if (orig_value == Float.NEGATIVE_INFINITY)
      return 0xff800000;
    if (Float.isNaN(orig_value))
      return 0x7fc00000;

    // Extract sign.
    boolean negative = false;
    if (value < 0)
    {
      negative = true;
      value = -value;
    }

    int e = (int) java.lang.Math.floor(java.lang.Math.log(value) / java.lang.Math.log(2));
    double m = value / java.lang.Math.pow(2, e); // Scale between [0,1[

    int bias_e = 127 + e;
    int bias_m = (int) java.lang.Math.floor((m - 1) * java.lang.Math.pow(2, 23));
    if (bias_e < 0)
      bias_e = 0; // This fixes something, don't know why.

    int result = 0;

    // Sign
    if (negative)
      result |= 0x80000000;

    // Exponent
    result |= (bias_e << 23);

    // Mantissa
    result |= bias_m;

    return result;
  }

  public static byte Reverse(byte b)
  {
    return (byte) (Integer.reverse(b) >>> (Integer.SIZE - Byte.SIZE));
  }

}
