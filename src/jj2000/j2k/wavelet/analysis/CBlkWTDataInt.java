/* 
 * CVS identifier:
 * 
 * $Id: CBlkWTDataInt.java,v 1.10 2001/08/15 17:18:51 grosbois Exp $
 * 
 * Class:                   CBlkWTDataInt
 * 
 * Description:             Implementation of CBlkWTData for 'int' data
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.image.DataBlk;

/**
 * This is an implementation of the 'CBlkWTData' abstract class for signed 32 bit integer data.
 *
 * <p>The methods in this class are declared final, so that they can be inlined by inlining compilers.</p>
 *
 * @see CBlkWTData
 *
 */
public class CBlkWTDataInt extends CBlkWTData
{
  /**
   * The array where the data is stored
   */
  public int[] data;

  /**
   * Returns the data type of this object, always DataBlk.TYPE_INT.
   *
   * @return The data type of the object, always DataBlk.TYPE_INT
     *
   */
  public final int getDataType()
  {
    return DataBlk.TYPE_INT;
  }

  /**
   * Returns the array containing the data, or null if there is no data array. The returned array is an int array.
   *
   * @return The array of data (a int[]) or null if there is no data.
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
  public final int[] getDataInt()
  {
    return data;
  }

  /**
   * Sets the data array to the specified one. The provided array must be a int array, otherwise a ClassCastException is thrown. The size of the array is not
   * checked for consistency with the code-block dimensions.
   *
   * @param arr The data array to use. Must be an int array.
     *
   */
  public final void setData(Object arr)
  {
    data = (int[]) arr;
  }

  /**
   * Sets the data array to the specified one. The size of the array is not checked for consistency with the code-block dimensions. This method is more
   * efficient than 'setData()'.
   *
   * @param arr The data array to use.
     *
   */
  public final void setDataInt(int[] arr)
  {
    data = arr;
  }
}
