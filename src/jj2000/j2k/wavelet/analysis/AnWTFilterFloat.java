/*
 * CVS identifier:
 *
 * $Id: AnWTFilterFloat.java,v 1.7 2000/09/05 09:25:37 grosbois Exp $
 *
 * Class:                   AnWTFilterFloat
 *
 * Description:             A specialized wavelet filter interface that
 *                          works on float data.
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
 * This extends the analysis wavelet filter general definitions of AnWTFilter by adding methods that work for float data specifically. Implementations that work
 * on float data should inherit from this class.
 *
 * <P>See the AnWTFilter class for details such as normalization, how to split odd-length signals, etc.
 *
 * <P>The advantage of using the specialized method is that no casts are performed.
 *
 * @see AnWTFilter
 *
 */
public abstract class AnWTFilterFloat extends AnWTFilter
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
  public abstract void analyze_lpf(float inSig[], int inOff, int inLen, int inStep,
    float lowSig[], int lowOff, int lowStep,
    float highSig[], int highOff, int highStep);

  /**
   * The general version of the analyze_lpf() method, it just calls the specialized version. See the description of the analyze_lpf() method of the AnWTFilter
   * class for more details.
   *
   * @param inSig This is the array that contains the input signal. It must be an float[].
   *
   * @param inOff This is the index in inSig of the first sugarcube.app.sample to filter.
   *
   * @param inLen This is the number of samples in the input signal to filter.
   *
   * @param inStep This is the step, or interleave factor, of the input signal samples in the inSig array.
   *
   * @param lowSig This is the array where the low-pass output signal is placed. It must be an float[].
   *
   * @param lowOff This is the index in lowSig of the element where to put the first low-pass output sugarcube.app.sample.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass output samples in the lowSig array.
   *
   * @param highSig This is the array where the high-pass output signal is placed. It must be an float[].
   *
   * @param highOff This is the index in highSig of the element where to put the first high-pass output sugarcube.app.sample.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass output samples in the highSig array.
   *
   * @see AnWTFilter#analyze_lpf
   *
   *
   *
   */
  public void analyze_lpf(Object inSig, int inOff, int inLen, int inStep,
    Object lowSig, int lowOff, int lowStep,
    Object highSig, int highOff, int highStep)
  {

    analyze_lpf((float[]) inSig, inOff, inLen, inStep,
      (float[]) lowSig, lowOff, lowStep,
      (float[]) highSig, highOff, highStep);
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
   */
  public abstract void analyze_hpf(float inSig[], int inOff, int inLen, int inStep,
    float lowSig[], int lowOff, int lowStep,
    float highSig[], int highOff, int highStep);

  /**
   * The general version of the analyze_hpf() method, it just calls the specialized version. See the description of the analyze_hpf() method of the AnWTFilter
   * class for more details.
   *
   * @param inSig This is the array that contains the input signal. It must be an float[].
   *
   * @param inOff This is the index in inSig of the first sugarcube.app.sample to filter.
   *
   * @param inLen This is the number of samples in the input signal to filter.
   *
   * @param inStep This is the step, or interleave factor, of the input signal samples in the inSig array.
   *
   * @param lowSig This is the array where the low-pass output signal is placed. It must be an float[].
   *
   * @param lowOff This is the index in lowSig of the element where to put the first low-pass output sugarcube.app.sample.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass output samples in the lowSig array.
   *
   * @param highSig This is the array where the high-pass output signal is placed. It must be an float[].
   *
   * @param highOff This is the index in highSig of the element where to put the first high-pass output sugarcube.app.sample.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass output samples in the highSig array.
   *
   * @see AnWTFilter#analyze_hpf
   *
   *
   *
   */
  public void analyze_hpf(Object inSig, int inOff, int inLen, int inStep,
    Object lowSig, int lowOff, int lowStep,
    Object highSig, int highOff, int highStep)
  {

    analyze_hpf((float[]) inSig, inOff, inLen, inStep,
      (float[]) lowSig, lowOff, lowStep,
      (float[]) highSig, highOff, highStep);
  }

  /**
   * Returns the type of data on which this filter works, as defined in the DataBlk interface, which is always TYPE_FLOAT for this class.
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
    return DataBlk.TYPE_FLOAT;
  }
}
