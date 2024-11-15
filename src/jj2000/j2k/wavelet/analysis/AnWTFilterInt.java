/*
 * CVS identifier:
 *
 * $Id: AnWTFilterInt.java,v 1.7 2000/09/05 09:25:42 grosbois Exp $
 *
 * Class:                   AnWTFilterInt
 *
 * Description:             A specialized wavelet filter interface that
 *                          works on int data.
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
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.image.DataBlk;

/**
 * This extends the analysis wavelet filter general definitions of AnWTFilter by adding methods that work for int data specifically. Implementations that work
 * on int data should inherit from this class.
 *
 * <P>See the AnWTFilter class for details such as normalization, how to split odd-length signals, etc.
 *
 * <P>The advantage of using the specialized method is that no casts are performed.
 *
 * @see AnWTFilter
 *
 */
public abstract class AnWTFilterInt extends AnWTFilter
{
  /**
   * A specific version of the analyze_lpf() method that works on int data. See the general description of the analyze_lpf() method in the AnWTFilter class for
   * more details.
   *
   * @param inSig This is the array that contains the input signal.
   *
   * @param inOff This is the index in inSig of the first sugarcube.app.sample to filter.
   *
   * @param inLen This is the number of samples in the input signal to filter.
   *
   * @param inStep This is the step, or interleave factor, of the input signal samples in the inSig array.
   *
   * @param lowSig This is the array where the low-pass output signal is placed.
   *
   * @param lowOff This is the index in lowSig of the element where to put the first low-pass output sugarcube.app.sample.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass output samples in the lowSig array.
   *
   * @param highSig This is the array where the high-pass output signal is placed.
   *
   * @param highOff This is the index in highSig of the element where to put the first high-pass output sugarcube.app.sample.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass output samples in the highSig array.
   *
   * @see AnWTFilter#analyze_lpf
   *
   *
   *
   *
   *
   */
  public abstract void analyze_lpf(int inSig[], int inOff, int inLen, int inStep,
    int lowSig[], int lowOff, int lowStep,
    int highSig[], int highOff, int highStep);

  /**
   * The general version of the analyze_lpf() method, it just calls the specialized version. See the description of the analyze_lpf() method of the AnWTFilter
   * class for more details.
   *
   * @param inSig This is the array that contains the input signal. It must be an int[].
   *
   * @param inOff This is the index in inSig of the first sugarcube.app.sample to filter.
   *
   * @param inLen This is the number of samples in the input signal to filter.
   *
   * @param inStep This is the step, or interleave factor, of the input signal samples in the inSig array.
   *
   * @param lowSig This is the array where the low-pass output signal is placed. It must be an int[].
   *
   * @param lowOff This is the index in lowSig of the element where to put the first low-pass output sugarcube.app.sample.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass output samples in the lowSig array.
   *
   * @param highSig This is the array where the high-pass output signal is placed. It must be an int[].
   *
   * @param highOff This is the index in highSig of the element where to put the first high-pass output sugarcube.app.sample.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass output samples in the highSig array.
   *
   * @see AnWTFilter#analyze_lpf
   *
   *
   *
   *
   *
   */
  public void analyze_lpf(Object inSig, int inOff, int inLen, int inStep,
    Object lowSig, int lowOff, int lowStep,
    Object highSig, int highOff, int highStep)
  {

    analyze_lpf((int[]) inSig, inOff, inLen, inStep,
      (int[]) lowSig, lowOff, lowStep,
      (int[]) highSig, highOff, highStep);
  }

  /**
   * A specific version of the analyze_hpf() method that works on int data. See the general description of the analyze_hpf() method in the AnWTFilter class for
   * more details.
   *
   * @param inSig This is the array that contains the input signal.
   *
   * @param inOff This is the index in inSig of the first sugarcube.app.sample to filter.
   *
   * @param inLen This is the number of samples in the input signal to filter.
   *
   * @param inStep This is the step, or interleave factor, of the input signal samples in the inSig array.
   *
   * @param lowSig This is the array where the low-pass output signal is placed.
   *
   * @param lowOff This is the index in lowSig of the element where to put the first low-pass output sugarcube.app.sample.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass output samples in the lowSig array.
   *
   * @param highSig This is the array where the high-pass output signal is placed.
   *
   * @param highOff This is the index in highSig of the element where to put the first high-pass output sugarcube.app.sample.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass output samples in the highSig array.
   *
   * @see AnWTFilter#analyze_hpf
   *
   *
   *
   *
   *
   */
  public abstract void analyze_hpf(int inSig[], int inOff, int inLen, int inStep,
    int lowSig[], int lowOff, int lowStep,
    int highSig[], int highOff, int highStep);

  /**
   * The general version of the analyze_hpf() method, it just calls the specialized version. See the description of the analyze_hpf() method of the AnWTFilter
   * class for more details.
   *
   * @param inSig This is the array that contains the input signal. It must be an int[].
   *
   * @param inOff This is the index in inSig of the first sugarcube.app.sample to filter.
   *
   * @param inLen This is the number of samples in the input signal to filter.
   *
   * @param inStep This is the step, or interleave factor, of the input signal samples in the inSig array.
   *
   * @param lowSig This is the array where the low-pass output signal is placed. It must be an int[].
   *
   * @param lowOff This is the index in lowSig of the element where to put the first low-pass output sugarcube.app.sample.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass output samples in the lowSig array.
   *
   * @param highSig This is the array where the high-pass output signal is placed. It must be an int[].
   *
   * @param highOff This is the index in highSig of the element where to put the first high-pass output sugarcube.app.sample.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass output samples in the highSig array.
   *
   * @see AnWTFilter#analyze_hpf
   *
   *
   *
   *
   *
   */
  public void analyze_hpf(Object inSig, int inOff, int inLen, int inStep,
    Object lowSig, int lowOff, int lowStep,
    Object highSig, int highOff, int highStep)
  {

    analyze_hpf((int[]) inSig, inOff, inLen, inStep,
      (int[]) lowSig, lowOff, lowStep,
      (int[]) highSig, highOff, highStep);
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
