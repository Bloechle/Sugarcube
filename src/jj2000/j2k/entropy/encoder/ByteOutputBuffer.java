/*
 * CVS identifier:
 *
 * $Id: ByteOutputBuffer.java,v 1.10 2001/05/17 15:21:16 grosbois Exp $
 *
 * Class:                   ByteOutputBuffer
 *
 * Description:             Provides buffering for byte based output, similar
 *                          to the standard class ByteArrayOutputStream
 *
 *                          the old jj2000.j2k.io.ByteArrayOutput class by
 *                          Diego SANTA CRUZ, Apr-26-1999
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.entropy.encoder;

/**
 * This class provides a buffering output stream similar to ByteArrayOutputStream, with some additional methods.
 *
 * <p>Once an array has been written to an output stream or to a byte array, the object can be reused as a new stream if the reset() method is called.</p>
 *
 * <p>Unlike the ByteArrayOutputStream class, this class is not thread safe.</p>
 *
 * @see #reset
 *
 */
public class ByteOutputBuffer
{
  /**
   * The buffer where the data is stored
   */
  byte buf[];
  /**
   * The number of valid bytes in the buffer
   */
  int count;
  /**
   * The buffer increase size
   */
  public final static int BUF_INC = 512;
  /**
   * The default initial buffer size
   */
  public final static int BUF_DEF_LEN = 256;

  /**
   * Creates a new byte array output stream. The buffer capacity is initially BUF_DEF_LEN bytes, though its size increases if necessary.
     *
   */
  public ByteOutputBuffer()
  {
    buf = new byte[BUF_DEF_LEN];
  }

  /**
   * Creates a new byte array output stream, with a buffer capacity of the specified size, in bytes.
   *
   * @param size the initial size.
     *
   */
  public ByteOutputBuffer(int size)
  {
    buf = new byte[size];
  }

  /**
   * Writes the specified byte to this byte array output stream. The functionality provided by this implementation is the same as for the one in the superclass,
   * however this method is not synchronized and therefore not safe thread, but faster.
   *
   * @param b The byte to write
     *
   */
  public final void write(int b)
  {
    if (count == buf.length)
    { // Resize buffer
      byte tmpbuf[] = buf;
      buf = new byte[buf.length + BUF_INC];
      System.arraycopy(tmpbuf, 0, buf, 0, count);
    }
    buf[count++] = (byte) b;
  }

  /**
   * Copies the specified part of the stream to the 'outbuf' byte array.
   *
   * @param off The index of the first element in the stream to copy.
   *
   * @param len The number of elements of the array to copy
   *
   * @param outbuf The destination array
   *
   * @param outoff The index of the first element in 'outbuf' where to write the data.
     *
   */
  public void toByteArray(int off, int len, byte outbuf[], int outoff)
  {
    // Copy the data
    System.arraycopy(buf, off, outbuf, outoff, len);
  }

  /**
   * Returns the number of valid bytes in the output buffer (count class variable).
   *
   * @return The number of bytes written to the buffer
     *
   */
  public int size()
  {
    return count;
  }

  /**
   * Discards all the buffered data, by resetting the counter of written bytes to 0.
     *
   */
  public void reset()
  {
    count = 0;
  }

  /**
   * Returns the byte buffered at the given position in the buffer. The position in the buffer is the index of the 'write()' method call after the last call to
   * 'reset()'.
   *
   * @param pos The position of the byte to return
   *
   * @return The value (betweeb 0-255) of the byte at position 'pos'.
     *
   */
  public int getByte(int pos)
  {
    if (pos >= count)
      throw new IllegalArgumentException();
    return buf[pos] & 0xFF;
  }
}
