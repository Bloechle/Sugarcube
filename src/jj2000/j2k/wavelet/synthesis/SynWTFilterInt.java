/*
 * CVS identifier:
 *
 * $Id: SynWTFilterInt.java,v 1.7 2000/09/05 09:26:35 grosbois Exp $
 *
 * Class:                   SynWTFilterInt
 *
 * Description:             A specialized synthesis wavelet filter interface
 *                          that works on int data.
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
package jj2000.j2k.wavelet.synthesis;

import jj2000.j2k.image.DataBlk;

/**
 * This extends the synthesis wavelet filter general definitions of SynWTFilter by adding methods that work for int data specifically. Implementations that work
 * on int data should inherit from this class.
 *
 * <P>See the SynWTFilter class for details such as normalization, how to split odd-length signals, etc.
 *
 * <P>The advantage of using the specialized method is that no casts are performed.
 *
 * @see SynWTFilter
 *
 */
public abstract class SynWTFilterInt extends SynWTFilter
{
  /**
   * A specific version of the synthetize_lpf() method that works on int data. See the general description of the synthetize_lpf() method in the SynWTFilter
   * class for more details.
   *
   * @param lowSig This is the array that contains the low-pass input signal.
   *
   * @param lowOff This is the index in lowSig of the first sugarcube.app.sample to filter.
   *
   * @param lowLen This is the number of samples in the low-pass input signal to filter.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass input signal samples in the lowSig array.
   *
   * @param highSig This is the array that contains the high-pass input signal.
   *
   * @param highOff This is the index in highSig of the first sugarcube.app.sample to filter.
   *
   * @param highLen This is the number of samples in the high-pass input signal to filter.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass input signal samples in the highSig array.
   *
   * @param outSig This is the array where the output signal is placed. It should be long enough to contain the output signal.
   *
   * @param outOff This is the index in outSig of the element where to put the first output sugarcube.app.sample.
   *
   * @param outStep This is the step, or interleave factor, of the output samples in the outSig array.
   *
   * @see SynWTFilter#synthetize_lpf
   *
   *
   *
   *
   *
   */
  public abstract void synthetize_lpf(int[] lowSig, int lowOff, int lowLen, int lowStep,
    int[] highSig, int highOff, int highLen, int highStep,
    int[] outSig, int outOff, int outStep);

  /**
   * The general version of the synthetize_lpf() method, it just calls the specialized version. See the description of the synthetize_lpf() method of the
   * SynWTFilter class for more details.
   *
   * @param lowSig This is the array that contains the low-pass input signal. It must be an int[].
   *
   * @param lowOff This is the index in lowSig of the first sugarcube.app.sample to filter.
   *
   * @param lowLen This is the number of samples in the low-pass input signal to filter.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass input signal samples in the lowSig array.
   *
   * @param highSig This is the array that contains the high-pass input signal. Itmust be an int[].
   *
   * @param highOff This is the index in highSig of the first sugarcube.app.sample to filter.
   *
   * @param highLen This is the number of samples in the high-pass input signal to filter.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass input signal samples in the highSig array.
   *
   * @param outSig This is the array where the output signal is placed. It should be and int[] and long enough to contain the output signal.
   *
   * @param outOff This is the index in outSig of the element where to put the first output sugarcube.app.sample.
   *
   * @param outStep This is the step, or interleave factor, of the output samples in the outSig array.
   *
   * @see SynWTFilter#synthetize_lpf
   *
   *
   *
   *
   *
   */
  public void synthetize_lpf(Object lowSig, int lowOff, int lowLen, int lowStep,
    Object highSig, int highOff, int highLen, int highStep,
    Object outSig, int outOff, int outStep)
  {

    synthetize_lpf((int[]) lowSig, lowOff, lowLen, lowStep,
      (int[]) highSig, highOff, highLen, highStep,
      (int[]) outSig, outOff, outStep);
  }

  /**
   * A specific version of the synthetize_hpf() method that works on int data. See the general description of the synthetize_hpf() method in the SynWTFilter
   * class for more details.
   *
   * @param lowSig This is the array that contains the low-pass input signal.
   *
   * @param lowOff This is the index in lowSig of the first sugarcube.app.sample to filter.
   *
   * @param lowLen This is the number of samples in the low-pass input signal to filter.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass input signal samples in the lowSig array.
   *
   * @param highSig This is the array that contains the high-pass input signal.
   *
   * @param highOff This is the index in highSig of the first sugarcube.app.sample to filter.
   *
   * @param highLen This is the number of samples in the high-pass input signal to filter.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass input signal samples in the highSig array.
   *
   * @param outSig This is the array where the output signal is placed. It should be long enough to contain the output signal.
   *
   * @param outOff This is the index in outSig of the element where to put the first output sugarcube.app.sample.
   *
   * @param outStep This is the step, or interleave factor, of the output samples in the outSig array.
   *
   * @see SynWTFilter#synthetize_hpf
   *
   *
   *
   *
   *
   */
  public abstract void synthetize_hpf(int[] lowSig, int lowOff, int lowLen, int lowStep,
    int[] highSig, int highOff, int highLen, int highStep,
    int[] outSig, int outOff, int outStep);

  /**
   * The general version of the synthetize_hpf() method, it just calls the specialized version. See the description of the synthetize_hpf() method of the
   * SynWTFilter class for more details.
   *
   * @param lowSig This is the array that contains the low-pass input signal. It must be an int[].
   *
   * @param lowOff This is the index in lowSig of the first sugarcube.app.sample to filter.
   *
   * @param lowLen This is the number of samples in the low-pass input signal to filter.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass input signal samples in the lowSig array.
   *
   * @param highSig This is the array that contains the high-pass input signal. Itmust be an int[].
   *
   * @param highOff This is the index in highSig of the first sugarcube.app.sample to filter.
   *
   * @param highLen This is the number of samples in the high-pass input signal to filter.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass input signal samples in the highSig array.
   *
   * @param outSig This is the array where the output signal is placed. It should be and int[] and long enough to contain the output signal.
   *
   * @param outOff This is the index in outSig of the element where to put the first output sugarcube.app.sample.
   *
   * @param outStep This is the step, or interleave factor, of the output samples in the outSig array.
   *
   * @see SynWTFilter#synthetize_hpf
   *
   *
   *
   *
   *
   */
  public void synthetize_hpf(Object lowSig, int lowOff, int lowLen, int lowStep,
    Object highSig, int highOff, int highLen, int highStep,
    Object outSig, int outOff, int outStep)
  {

    synthetize_hpf((int[]) lowSig, lowOff, lowLen, lowStep,
      (int[]) highSig, highOff, highLen, highStep,
      (int[]) outSig, outOff, outStep);
  }

  /**
   * Returns the type of data on which this filter works, as defined in the DataBlk interface, which is always TYPE_INT for this class.
   *
   * @return The type of data as defined in the DataBlk interface.
   *
   * @see jj2000.j2k.image.DataBlk
   *
   *
   *
   */
  public int getDataType()
  {
    return DataBlk.TYPE_INT;
  }
}