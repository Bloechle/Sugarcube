/*
 * CVS identifier:
 *
 * $Id: AnWTFilterFloatLift9x7.java,v 1.18 2002/01/22 13:31:31 grosbois Exp $
 *
 * Class:                   AnWTFilterFloatLift9x7
 *
 * Description:             An analyzing wavelet filter implementing the
 *                          lifting 9x7 transform.
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.wavelet.FilterTypes;

/**
 * This class inherits from the analysis wavelet filter definition for int data. It implements the forward wavelet transform specifically for the 9x7 filter.
 * The implementation is based on the lifting scheme.
 *
 * <P>See the AnWTFilter class for details such as normalization, how to split odd-length signals, etc. In particular, this method assumes that the low-pass
 * coefficient is computed first.
 *
 * @see AnWTFilter
 * @see AnWTFilterFloat
 *
 */
public class AnWTFilterFloatLift9x7 extends AnWTFilterFloat
{
  /**
   * The low-pass synthesis filter of the 9x7 wavelet transform
   */
  private final static float LPSynthesisFilter[] =
  {
    -0.091272f, -0.057544f, 0.591272f, 1.115087f,
    0.591272f, -0.057544f, -0.091272f
  };
  /**
   * The high-pass synthesis filter of the 9x7 wavelet transform
   */
  private final static float HPSynthesisFilter[] =
  {
    0.026749f, 0.016864f, -0.078223f, -0.266864f,
    0.602949f,
    -0.266864f, -0.078223f, 0.016864f, 0.026749f
  };
  /**
   * The value of the first lifting step coefficient
   */
  public final static float ALPHA = -1.586134342f;
  /**
   * The value of the second lifting step coefficient
   */
  public final static float BETA = -0.05298011854f;
  /**
   * The value of the third lifting step coefficient
   */
  public final static float GAMMA = 0.8829110762f;
  /**
   * The value of the fourth lifting step coefficient
   */
  public final static float DELTA = 0.4435068522f;
  /**
   * The value of the low-pass subband normalization factor
   */
  public final static float KL = 0.8128930655f;
  /**
   * The value of the high-pass subband normalization factor
   */
  public final static float KH = 1.230174106f;

  /**
   * An implementation of the analyze_lpf() method that works on int data, for the forward 9x7 wavelet transform using the lifting scheme. See the general
   * description of the analyze_lpf() method in the AnWTFilter class for more details.
   *
   * <P>The coefficients of the first lifting step are [ALPHA 1 ALPHA].
   *
   * <P>The coefficients of the second lifting step are [BETA 1 BETA].
   *
   * <P>The coefficients of the third lifting step are [GAMMA 1 GAMMA].
   *
   * <P>The coefficients of the fourth lifting step are [DELTA 1 DELTA].
   *
   * <P>The low-pass and high-pass subbands are normalized by respectively a factor of KL and a factor of KH
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
   */
  public void analyze_lpf(float inSig[], int inOff, int inLen, int inStep,
    float lowSig[], int lowOff, int lowStep,
    float highSig[], int highOff, int highStep)
  {
    int i, maxi;
    int iStep = 2 * inStep; //Subsampling in inSig
    int ik;    //Indexing inSig
    int lk;    //Indexing lowSig
    int hk;    //Indexing highSig

    // Generate intermediate high frequency subband

    //Initialize counters
    ik = inOff + inStep;
    lk = lowOff;
    hk = highOff;

    //Apply first lifting step to each "inner" sugarcube.app.sample
    for (i = 1, maxi = inLen - 1; i < maxi; i += 2)
    {
      highSig[hk] = inSig[ik]
        + ALPHA * (inSig[ik - inStep] + inSig[ik + inStep]);

      ik += iStep;
      hk += highStep;
    }

    //Handle head boundary effect if input signal has even length
    if (inLen % 2 == 0)
      highSig[hk] = inSig[ik] + 2 * ALPHA * inSig[ik - inStep];

    // Generate intermediate low frequency subband

    //Initialize counters
    ik = inOff;
    lk = lowOff;
    hk = highOff;

    if (inLen > 1)
      lowSig[lk] = inSig[ik] + 2 * BETA * highSig[hk];
    else
      lowSig[lk] = inSig[ik];

    ik += iStep;
    lk += lowStep;
    hk += highStep;

    //Apply lifting step to each "inner" sugarcube.app.sample
    for (i = 2, maxi = inLen - 1; i < maxi; i += 2)
    {
      lowSig[lk] = inSig[ik]
        + BETA * (highSig[hk - highStep] + highSig[hk]);

      ik += iStep;
      lk += lowStep;
      hk += highStep;
    }

    //Handle head boundary effect if input signal has odd length
    if ((inLen % 2 == 1) && (inLen > 2))
      lowSig[lk] = inSig[ik] + 2 * BETA * highSig[hk - highStep];

    // Generate high frequency subband

    //Initialize counters
    lk = lowOff;
    hk = highOff;

    //Apply first lifting step to each "inner" sugarcube.app.sample
    for (i = 1, maxi = inLen - 1; i < maxi; i += 2)
    {
      highSig[hk] += GAMMA * (lowSig[lk] + lowSig[lk + lowStep]);

      lk += lowStep;
      hk += highStep;
    }

    //Handle head boundary effect if input signal has even length
    if (inLen % 2 == 0)
      highSig[hk] += 2 * GAMMA * lowSig[lk];

    // Generate low frequency subband

    //Initialize counters
    lk = lowOff;
    hk = highOff;

    //Handle tail boundary effect
    //If access the overlap then perform the lifting step
    if (inLen > 1)
      lowSig[lk] += 2 * DELTA * highSig[hk];

    lk += lowStep;
    hk += highStep;

    //Apply lifting step to each "inner" sugarcube.app.sample
    for (i = 2, maxi = inLen - 1; i < maxi; i += 2)
    {
      lowSig[lk] +=
        DELTA * (highSig[hk - highStep] + highSig[hk]);

      lk += lowStep;
      hk += highStep;
    }

    //Handle head boundary effect if input signal has odd length
    if ((inLen % 2 == 1) && (inLen > 2))
      lowSig[lk] += 2 * DELTA * highSig[hk - highStep];

    // Normalize low and high frequency subbands

    //Re-initialize counters
    lk = lowOff;
    hk = highOff;

    //Normalize each sugarcube.app.sample
    for (i = 0; i < (inLen >> 1); i++)
    {
      lowSig[lk] *= KL;
      highSig[hk] *= KH;
      lk += lowStep;
      hk += highStep;
    }
    //If the input signal has odd length then normalize the last low-pass
    //coefficient (if input signal is length one filter is identity)
    if (inLen % 2 == 1 && inLen != 1)
      lowSig[lk] *= KL;
  }

  /**
   * An implementation of the analyze_hpf() method that works on int data, for the forward 9x7 wavelet transform using the lifting scheme. See the general
   * description of the analyze_hpf() method in the AnWTFilter class for more details.
   *
   * <P>The coefficients of the first lifting step are [ALPHA 1 ALPHA].
   *
   * <P>The coefficients of the second lifting step are [BETA 1 BETA].
   *
   * <P>The coefficients of the third lifting step are [GAMMA 1 GAMMA].
   *
   * <P>The coefficients of the fourth lifting step are [DELTA 1 DELTA].
   *
   * <P>The low-pass and high-pass subbands are normalized by respectively a factor of KL and a factor of KH
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
   */
  public void analyze_hpf(float inSig[], int inOff, int inLen, int inStep,
    float lowSig[], int lowOff, int lowStep,
    float highSig[], int highOff, int highStep)
  {

    int i, maxi;
    int iStep = 2 * inStep; //Subsampling in inSig
    int ik;    //Indexing inSig
    int lk;    //Indexing lowSig
    int hk;    //Indexing highSig

    // Generate intermediate high frequency subband

    //Initialize counters
    ik = inOff;
    lk = lowOff;
    hk = highOff;

    if (inLen > 1)
      // apply symmetric extension.
      highSig[hk] = inSig[ik] + 2 * ALPHA * inSig[ik + inStep];
    else
      // Normalize for Nyquist gain
      highSig[hk] = inSig[ik] * 2;

    ik += iStep;
    hk += highStep;

    //Apply first lifting step to each "inner" sugarcube.app.sample
    for (i = 2; i < inLen - 1; i += 2)
    {
      highSig[hk] = inSig[ik]
        + ALPHA * (inSig[ik - inStep] + inSig[ik + inStep]);
      ik += iStep;
      hk += highStep;
    }

    //If input signal has odd length then we perform the lifting step
    // i.e. apply a symmetric extension.
    if ((inLen % 2 == 1) && (inLen > 1))
      highSig[hk] = inSig[ik] + 2 * ALPHA * inSig[ik - inStep];

    // Generate intermediate low frequency subband

    //Initialize counters
    //ik = inOff + inStep;
    ik = inOff + inStep;
    lk = lowOff;
    hk = highOff;

    //Apply lifting step to each "inner" sugarcube.app.sample
    // we are at the component boundary
    for (i = 1; i < inLen - 1; i += 2)
    {
      lowSig[lk] = inSig[ik]
        + BETA * (highSig[hk] + highSig[hk + highStep]);

      ik += iStep;
      lk += lowStep;
      hk += highStep;
    }
    if (inLen > 1 && inLen % 2 == 0)
      // symetric extension
      lowSig[lk] = inSig[ik] + 2 * BETA * highSig[hk];

    // Generate high frequency subband

    //Initialize counters
    lk = lowOff;
    hk = highOff;

    if (inLen > 1)
      // symmetric extension.
      highSig[hk] += GAMMA * 2 * lowSig[lk];
    //lk += lowStep;   
    hk += highStep;

    //Apply first lifting step to each "inner" sugarcube.app.sample
    for (i = 2; i < inLen - 1; i += 2)
    {
      highSig[hk] += GAMMA * (lowSig[lk] + lowSig[lk + lowStep]);
      lk += lowStep;
      hk += highStep;
    }

    //Handle head boundary effect
    if (inLen > 1 && inLen % 2 == 1)
      // symmetric extension.
      highSig[hk] += GAMMA * 2 * lowSig[lk];

    // Generate low frequency subband

    //Initialize counters
    lk = lowOff;
    hk = highOff;

    // we are at the component boundary
    for (i = 1; i < inLen - 1; i += 2)
    {
      lowSig[lk] += DELTA * (highSig[hk] + highSig[hk + highStep]);
      lk += lowStep;
      hk += highStep;
    }

    if (inLen > 1 && inLen % 2 == 0)
      lowSig[lk] += DELTA * 2 * highSig[hk];

    // Normalize low and high frequency subbands

    //Re-initialize counters
    lk = lowOff;
    hk = highOff;

    //Normalize each sugarcube.app.sample
    for (i = 0; i < (inLen >> 1); i++)
    {
      lowSig[lk] *= KL;
      highSig[hk] *= KH;
      lk += lowStep;
      hk += highStep;
    }
    //If the input signal has odd length then normalize the last high-pass
    //coefficient (if input signal is length one filter is identity)
    if (inLen % 2 == 1 && inLen != 1)
      highSig[hk] *= KH;
  }

  /**
   * Returns the negative support of the low-pass analysis filter. That is the number of taps of the filter in the negative direction.
   *
   * @return 2
     *
   */
  public int getAnLowNegSupport()
  {
    return 4;
  }

  /**
   * Returns the positive support of the low-pass analysis filter. That is the number of taps of the filter in the negative direction.
   *
   * @return The number of taps of the low-pass analysis filter in the positive direction
     *
   */
  public int getAnLowPosSupport()
  {
    return 4;
  }

  /**
   * Returns the negative support of the high-pass analysis filter. That is the number of taps of the filter in the negative direction.
   *
   * @return The number of taps of the high-pass analysis filter in the negative direction
     *
   */
  public int getAnHighNegSupport()
  {
    return 3;
  }

  /**
   * Returns the positive support of the high-pass analysis filter. That is the number of taps of the filter in the negative direction.
   *
   * @return The number of taps of the high-pass analysis filter in the positive direction
     *
   */
  public int getAnHighPosSupport()
  {
    return 3;
  }

  /**
   * Returns the negative support of the low-pass synthesis filter. That is the number of taps of the filter in the negative direction.
   *
   * <P>A MORE PRECISE DEFINITION IS NEEDED
   *
   * @return The number of taps of the low-pass synthesis filter in the negative direction
     *
   */
  public int getSynLowNegSupport()
  {
    return 3;
  }

  /**
   * Returns the positive support of the low-pass synthesis filter. That is the number of taps of the filter in the negative direction.
   *
   * <P>A MORE PRECISE DEFINITION IS NEEDED
   *
   * @return The number of taps of the low-pass synthesis filter in the positive direction
     *
   */
  public int getSynLowPosSupport()
  {
    return 3;
  }

  /**
   * Returns the negative support of the high-pass synthesis filter. That is the number of taps of the filter in the negative direction.
   *
   * <P>A MORE PRECISE DEFINITION IS NEEDED
   *
   * @return The number of taps of the high-pass synthesis filter in the negative direction
     *
   */
  public int getSynHighNegSupport()
  {
    return 4;
  }

  /**
   * Returns the positive support of the high-pass synthesis filter. That is the number of taps of the filter in the negative direction.
   *
   * <P>A MORE PRECISE DEFINITION IS NEEDED
   *
   * @return The number of taps of the high-pass synthesis filter in the positive direction
     *
   */
  public int getSynHighPosSupport()
  {
    return 4;
  }

  /**
   * Returns the time-reversed low-pass synthesis waveform of the filter, which is the low-pass filter. This is the time-reversed impulse response of the
   * low-pass synthesis filter. It is used to calculate the L2-norm of the synthesis basis functions for a particular subband (also called energy weight).
   *
   * <P>The returned array may not be modified (i.e. a reference to the internal array may be returned by the implementation of this method).
   *
   * @return The time-reversed low-pass synthesis waveform of the filter.
     *
   */
  public float[] getLPSynthesisFilter()
  {
    return LPSynthesisFilter;
  }

  /**
   * Returns the time-reversed high-pass synthesis waveform of the filter, which is the high-pass filter. This is the time-reversed impulse response of the
   * high-pass synthesis filter. It is used to calculate the L2-norm of the synthesis basis functions for a particular subband (also called energy weight).
   *
   * <P>The returned array may not be modified (i.e. a reference to the internal array may be returned by the implementation of this method).
   *
   * @return The time-reversed high-pass synthesis waveform of the filter.
     *
   */
  public float[] getHPSynthesisFilter()
  {
    return HPSynthesisFilter;
  }

  /**
   * Returns the implementation type of this filter, as defined in this class, such as WT_FILTER_INT_LIFT, WT_FILTER_FLOAT_LIFT, WT_FILTER_FLOAT_CONVOL.
   *
   * @return WT_FILTER_INT_LIFT.
     *
   */
  public int getImplType()
  {
    return WT_FILTER_FLOAT_LIFT;
  }

  /**
   * Returns the reversibility of the filter. A filter is considered reversible if it is suitable for lossless coding.
   *
   * @return true since the 9x7 is reversible, provided the appropriate rounding is performed.
     *
   */
  public boolean isReversible()
  {
    return false;
  }

  /**
   * Returns true if the wavelet filter computes or uses the same "inner" subband coefficient as the full frame wavelet transform, and false otherwise. In
   * particular, for block based transforms with reduced overlap, this method should return false. The term "inner" indicates that this applies only with
   * respect to the coefficient that are not affected by image boundaries processings such as symmetric extension, since there is not reference method for this.
   *
   * <P>The result depends on the length of the allowed overlap when compared to the overlap required by the wavelet filter. It also depends on how overlap
   * processing is implemented in the wavelet filter.
   *
   * @param tailOvrlp This is the number of samples in the input signal before the first sugarcube.app.sample to filter that can be used for overlap.
   *
   * @param headOvrlp This is the number of samples in the input signal after the last sugarcube.app.sample to filter that can be used for overlap.
   *
   * @param inLen This is the lenght of the input signal to filter.The required number of samples in the input signal after the last sugarcube.app.sample depends on the
   * length of the input signal.
   *
   * @return true if both overlaps are greater than 2, and correct processing is applied in the analyze() method.
     *
   */
  public boolean isSameAsFullWT(int tailOvrlp, int headOvrlp, int inLen)
  {

    //If the input signal has even length.
    if (inLen % 2 == 0)
      if (tailOvrlp >= 4 && headOvrlp >= 3)
        return true;
      else
        return false;
    //Else if the input signal has odd length.
    else
      if (tailOvrlp >= 4 && headOvrlp >= 4)
        return true;
      else
        return false;
  }

  /**
   * Tests if the 'obj' object is the same filter as this one. Two filters are the same if the same filter code should be output for both filters by the
   * encodeFilterCode() method.
   *
   * <P>Currently the implementation of this method only tests if 'obj' is also of the class AnWTFilterFloatLift9x7
   *
   * @param The object against which to test inequality.
     *
   */
  public boolean equals(Object obj)
  {
    // To spped up test, first test for reference equality
    return obj == this
      || obj instanceof AnWTFilterFloatLift9x7;
  }

  /**
   * Returns the type of filter used according to the FilterTypes interface(W9x7).
   *
   * @see FilterTypes
   *
   * @return The filter type.
     *
   */
  public int getFilterType()
  {
    return FilterTypes.W9X7;
  }

  /**
   * Debugging method
   */
  public String toString()
  {
    return "w9x7";
  }
}
