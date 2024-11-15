/*
 * CVS identifier:
 *
 * $Id: ByteInputBuffer.java,v 1.13 2001/10/17 17:01:57 grosbois Exp $
 *
 * Class:                   ByteInputBuffer
 *
 * Description:             Provides buffering for byte based input, similar
 *                          to the standard class ByteArrayInputStream
 *
 *                          the old jj2000.j2k.io.ByteArrayInput class by
 *                          Diego SANTA CRUZ, Apr-26-1999
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.entropy.decoder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides a byte input facility from byte buffers. It is similar to the ByteArrayInputStream class, but adds the possibility to add data to the
 * stream after the creation of the object.
 *
 * <p>Unlike the ByteArrayInputStream this class is not thread safe (i.e. no two threads can use the same object at the same time, but different objects may be
 * used in different threads).</p>
 *
 * <p>This class can modify the contents of the buffer given to the constructor, when the addByteArray() method is called.</p>
 *
 * @see InputStream
 *
 */
public class ByteInputBuffer
{
  /**
   * The byte array containing the data
   */
  private byte buf[];
  /**
   * The index one greater than the last valid character in the input stream buffer
   */
  private int count;
  /**
   * The index of the next character to read from the input stream buffer
     *
   */
  private int pos;

  /**
   * Creates a new byte array input stream that reads data from the specified byte array. The byte array is not copied.
   *
   * @param buf the input buffer.
     *
   */
  public ByteInputBuffer(byte buf[])
  {
    this.buf = buf;
    count = buf.length;
  }

  /**
   * Creates a new byte array input stream that reads data from the specified byte array. Up to length characters are to be read from the byte array, starting
   * at the indicated offset.
   *
   * <p>The byte array is not copied.</p>
   *
   * @param buf the input buffer.
   *
   * @param offset the offset in the buffer of the first byte to read.
   *
   * @param length the maximum number of bytes to read from the buffer.
     *
   */
  public ByteInputBuffer(byte buf[], int offset, int length)
  {
    this.buf = buf;
    pos = offset;
    count = offset + length;
  }

  /**
   * Sets the underlying buffer byte array to the given one, with the given offset and length. If 'buf' is null then the current byte buffer is assumed. If
   * 'offset' is negative, then it will be assumed to be 'off+len', where 'off' and 'len' are the offset and length of the current byte buffer.
   *
   * <p>The byte array is not copied.</p>
   *
   * @param buf the input buffer. If null it is the current input buffer.
   *
   * @param offset the offset in the buffer of the first byte to read. If negative it is assumed to be the byte just after the end of the current input buffer,
   * only permitted if 'buf' is null.
   *
   * @param length the maximum number of bytes to read frmo the buffer.
     *
   */
  public void setByteArray(byte buf[], int offset, int length)
  {
    // In same buffer?
    if (buf == null)
    {
      if (length < 0 || count + length > this.buf.length)
        throw new IllegalArgumentException();
      if (offset < 0)
      {
        pos = count;
        count += length;
      }
      else
      {
        count = offset + length;
        pos = offset;
      }
    }
    else
    { // New input buffer
      if (offset < 0 || length < 0 || offset + length > buf.length)
        throw new IllegalArgumentException();
      this.buf = buf;
      count = offset + length;
      pos = offset;
    }
  }

  /**
   * Adds the specified data to the end of the byte array stream. This method modifies the byte array buffer. It can also discard the already read input.
   *
   * @param data The data to add. The data is copied.
   *
   * @param off The index, in data, of the first element to add to the stream.
   *
   * @param len The number of elements to add to the array.
     *
   */
  public synchronized void addByteArray(byte data[], int off, int len)
  {
    // Check integrity
    if (len < 0 || off < 0 || len + off > buf.length)
      throw new IllegalArgumentException();
    // Copy new data
    if (count + len <= buf.length)
    { // Enough place in 'buf'
      System.arraycopy(data, off, buf, count, len);
      count += len;
    }
    else
    {
      if (count - pos + len <= buf.length)
        // Enough place in 'buf' if we move input data
        // Move buffer
        System.arraycopy(buf, pos, buf, 0, count - pos);
      else
      { // Not enough place in 'buf', use new buffer
        byte[] oldbuf = buf;
        buf = new byte[count - pos + len];
        // Copy buffer
        System.arraycopy(oldbuf, count, buf, 0, count - pos);
      }
      count -= pos;
      pos = 0;
      // Copy new data
      System.arraycopy(data, off, buf, count, len);
      count += len;
    }
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is returned as an int in the range 0 to 255. If no byte is available because the end of
   * the stream has been reached, the EOFException exception is thrown.
   *
   * <p>This method is not synchronized, so it is not thread safe.</p>
   *
   * @return The byte read in the range 0-255.
   *
   * @exception EOFException If the end of the stream is reached.
     *
   */
  public int readChecked() throws IOException
  {
    if (pos < count)
      return (int) buf[pos++] & 0xFF;
    else
      throw new EOFException();
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is returned as an int in the range 0 to 255. If no byte is available because the end of
   * the stream has been reached, -1 is returned.
   *
   * <p>This method is not synchronized, so it is not thread safe.</p>
   *
   * @return The byte read in the range 0-255, or -1 if the end of stream has been reached.
     *
   */
  public int read()
  {
    if (pos < count)
      return (int) buf[pos++] & 0xFF;
    else
      return -1;
  }
}
