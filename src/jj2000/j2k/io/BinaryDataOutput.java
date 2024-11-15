/*
 * CVS Identifier:
 *
 * $Id: BinaryDataOutput.java,v 1.11 2000/09/05 09:24:33 grosbois Exp $
 *
 * Interface:           BinaryDataOutput
 *
 * Description:         Stream like interface for bit as well as byte
 *                      level output to a stream or file.
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * 
 * 
 * 
 */
package jj2000.j2k.io;

import java.io.IOException;

/**
 * This interface defines the output of binary data to streams and/or files.
 *
 * <P>Byte level output (i.e., for byte, int, long, float, etc.) should always be byte aligned. For example, a request to write an <tt>int</tt> should always
 * realign the output at the byte level.
 *
 * <P>The implementation of this interface should clearly define if multi-byte output data is written in little- or big-endian byte ordering (least significant
 * byte first or most significant byte first, respectively).
 *
 * @see EndianType
 *
 */
public interface BinaryDataOutput
{
  /**
   * Should write the byte value of <tt>v</tt> (i.e., 8 least significant bits) to the output. Prior to writing, the output should be realigned at the byte
   * level.
   *
   * <P>Signed or unsigned data can be written. To write a signed value just pass the <tt>byte</tt> value as an argument. To write unsigned data pass the
   * <tt>int</tt> value as an argument (it will be automatically casted, and only the 8 least significant bits will be written).
   *
   * @param v The value to write to the output
   *
   * @exception IOException If an I/O error ocurred.
   *
   *
   *
   */
  public void writeByte(int v) throws IOException;

  /**
   * Should write the short value of <tt>v</tt> (i.e., 16 least significant bits) to the output. Prior to writing, the output should be realigned at the byte
   * level.
   *
   * <P>Signed or unsigned data can be written. To write a signed value just pass the <tt>short</tt> value as an argument. To write unsigned data pass the
   * <tt>int</tt> value as an argument (it will be automatically casted, and only the 16 least significant bits will be written).
   *
   * @param v The value to write to the output
   *
   * @exception IOException If an I/O error ocurred.
   *
   *
   *
   */
  public void writeShort(int v) throws IOException;

  /**
   * Should write the int value of <tt>v</tt> (i.e., the 32 bits) to the output. Prior to writing, the output should be realigned at the byte level.
   *
   * @param v The value to write to the output
   *
   * @exception IOException If an I/O error ocurred.
   *
   *
   *
   */
  public void writeInt(int v) throws IOException;

  /**
   * Should write the long value of <tt>v</tt> (i.e., the 64 bits) to the output. Prior to writing, the output should be realigned at the byte level.
   *
   * @param v The value to write to the output
   *
   * @exception IOException If an I/O error ocurred.
   *
   *
   *
   */
  public void writeLong(long v) throws IOException;

  /**
   * Should write the IEEE float value <tt>v</tt> (i.e., 32 bits) to the output. Prior to writing, the output should be realigned at the byte level.
   *
   * @param v The value to write to the output
   *
   * @exception IOException If an I/O error ocurred.
   *
   *
   *
   */
  public void writeFloat(float v) throws IOException;

  /**
   * Should write the IEEE double value <tt>v</tt> (i.e., 64 bits) to the output. Prior to writing, the output should be realigned at the byte level.
   *
   * @param v The value to write to the output
   *
   * @exception IOException If an I/O error ocurred.
   *
   *
   *
   */
  public void writeDouble(double v) throws IOException;

  /**
   * Returns the endianness (i.e., byte ordering) of the implementing class. Note that an implementing class may implement only one type of endianness or both,
   * which would be decided at creatiuon time.
   *
   * @return Either <tt>EndianType.BIG_ENDIAN</tt> or <tt>EndianType.LITTLE_ENDIAN</tt>
   *
   * @see EndianType
   *
   *
   *
   */
  public int getByteOrdering();

  /**
   * Any data that has been buffered must be written, and the stream should be realigned at the byte level.
   *
   * @exception IOException If an I/O error ocurred.
   *
   *
   *
   */
  public void flush() throws IOException;
}
