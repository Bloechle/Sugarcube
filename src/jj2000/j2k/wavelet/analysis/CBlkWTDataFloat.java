/* 
 * CVS identifier:
 * 
 * $Id: CBlkWTDataFloat.java,v 1.11 2001/08/15 17:18:43 grosbois Exp $
 * 
 * Class:                   CBlkWTDataFloat
 * 
 * Description:             Implementation of CBlkWTData for 'float' data
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.image.DataBlk;

/**
 * This is an implementation of the 'CBlkWTData' abstract class for 32 bit floating point data (float).
 *
 * <p>The methods in this class are declared final, so that they can be inlined by inlining compilers.</p>
 *
 * @see CBlkWTData
 *
 */
public class CBlkWTDataFloat extends CBlkWTData
{
  /**
   * The array where the data is stored
   */
  public float[] data;

  /**
   * Returns the identifier of this data type, <tt>TYPE_FLOAT</tt>, as defined in <tt>DataBlk</tt>.
   *
   * @return The type of data stored. Always <tt>DataBlk.TYPE_FLOAT</tt>
   *
   * @see DataBlk#TYPE_FLOAT
     *
   */
  public final int getDataType()
  {
    return DataBlk.TYPE_FLOAT;
  }

  /**
   * Returns the array containing the data, or null if there is no data array. The returned array is a float array.
   *
   * @return The array of data (a float[]) or null if there is no data.
     *
   */
  public final Object getData()
  {
    return data;
  }

  /**
   * Returns the array containing the data, or null if there is no data array.
   *
   * @return The array of data or null if there is no data.
     *
   */
  public final float[] getDataFloat()
  {
    return data;
  }

  /**
   * Sets the data array to the specified one. The provided array must be a float array, otherwise a ClassCastException is thrown. The size of the array is not
   * checked for consistency with the code-block dimensions.
   *
   * @param arr The data array to use. Must be a float array.
     *
   */
  public final void setData(Object arr)
  {
    data = (float[]) arr;
  }

  /**
   * Sets the data array to the specified one. The size of the array is not checked for consistency with the code-block dimensions. This method is more
   * efficient than 'setData()'.
   *
   * @param arr The data array to use.
     *
   */
  public final void setDataFloat(float[] arr)
  {
    data = arr;
  }
}
