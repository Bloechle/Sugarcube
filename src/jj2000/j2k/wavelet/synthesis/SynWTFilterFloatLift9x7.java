/*
 * CVS identifier:
 *
 * $Id: SynWTFilterFloatLift9x7.java,v 1.15 2002/05/22 15:01:56 grosbois Exp $
 *
 * Class:                   SynWTFilterFloatLift9x7
 *
 * Description:             A synthetizing wavelet filter implementing the
 *                          lifting 9x7 transform.
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 *  */
package jj2000.j2k.wavelet.synthesis;

/**
 * This class inherits from the synthesis wavelet filter definition for int data. It implements the inverse wavelet transform specifically for the 9x7 filter.
 * The implementation is based on the lifting scheme.
 *
 * <P>See the SynWTFilter class for details such as normalization, how to split odd-length signals, etc. In particular, this method assumes that the low-pass
 * coefficient is computed first.
 *
 * @see SynWTFilter
 * @see SynWTFilterFloat
 *
 */
public class SynWTFilterFloatLift9x7 extends SynWTFilterFloat
{
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
   * An implementation of the synthetize_lpf() method that works on int data, for the inverse 9x7 wavelet transform using the lifting scheme. See the general
   * description of the synthetize_lpf() method in the SynWTFilter class for more details.
   *
   * <P>The low-pass and high-pass subbands are normalized by respectively a factor of 1/KL and a factor of 1/KH
   *
   * <P>The coefficients of the first lifting step are [-DELTA 1 -DELTA].
   *
   * <P>The coefficients of the second lifting step are [-GAMMA 1 -GAMMA].
   *
   * <P>The coefficients of the third lifting step are [-BETA 1 -BETA].
   *
   * <P>The coefficients of the fourth lifting step are [-ALPHA 1 -ALPHA].
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
   */
  public void synthetize_lpf(float[] lowSig, int lowOff, int lowLen, int lowStep,
    float[] highSig, int highOff, int highLen,
    int highStep,
    float[] outSig, int outOff, int outStep)
  {

    int i;
    int outLen = lowLen + highLen; //Length of the output signal
    int iStep = 2 * outStep; //Upsampling in outSig
    int ik; //Indexing outSig
    int lk; //Indexing lowSig
    int hk; //Indexing highSig

    // Generate intermediate low frequency subband
    float sample = 0;

    //Initialize counters
    lk = lowOff;
    hk = highOff;
    ik = outOff;

    //Handle tail boundary effect. Use symmetric extension
    if (outLen > 1)
      outSig[ik] = lowSig[lk] / KL - 2 * DELTA * highSig[hk] / KH;
    else
      outSig[ik] = lowSig[lk];

    lk += lowStep;
    hk += highStep;
    ik += iStep;

    //Apply lifting step to each "inner" sugarcube.app.sample
    for (i = 2; i < outLen - 1; i += 2, ik += iStep, lk += lowStep, hk += highStep)
      outSig[ik] = lowSig[lk] / KL
        - DELTA * (highSig[hk - highStep] + highSig[hk]) / KH;

    //Handle head boundary effect if input signal has odd length
    if (outLen % 2 == 1)
      if (outLen > 2)
        outSig[ik] = lowSig[lk] / KL
          - 2 * DELTA * highSig[hk - highStep] / KH;

    // Generate intermediate high frequency subband

    //Initialize counters
    lk = lowOff;
    hk = highOff;
    ik = outOff + outStep;

    //Apply lifting step to each "inner" sugarcube.app.sample
    for (i = 1; i < outLen - 1; i += 2, ik += iStep, hk += highStep, lk += lowStep)
      outSig[ik] = highSig[hk] / KH
        - GAMMA * (outSig[ik - outStep] + outSig[ik + outStep]);

    //Handle head boundary effect if output signal has even length
    if (outLen % 2 == 0)
      outSig[ik] = highSig[hk] / KH - 2 * GAMMA * outSig[ik - outStep];

    // Generate even samples (inverse low-pass filter)

    //Initialize counters
    ik = outOff;

    //Handle tail boundary effect
    //If access the overlap then perform the lifting step.
    if (outLen > 1)
      outSig[ik] -= 2 * BETA * outSig[ik + outStep];
    ik += iStep;

    //Apply lifting step to each "inner" sugarcube.app.sample
    for (i = 2; i < outLen - 1; i += 2, ik += iStep)
      outSig[ik] -= BETA * (outSig[ik - outStep] + outSig[ik + outStep]);

    //Handle head boundary effect if input signal has odd length
    if (outLen % 2 == 1 && outLen > 2)
      outSig[ik] -= 2 * BETA * outSig[ik - outStep];

    // Generate odd samples (inverse high pass-filter)

    //Initialize counters
    ik = outOff + outStep;

    //Apply first lifting step to each "inner" sugarcube.app.sample
    for (i = 1; i < outLen - 1; i += 2, ik += iStep)
      outSig[ik] -= ALPHA * (outSig[ik - outStep] + outSig[ik + outStep]);

    //Handle head boundary effect if input signal has even length
    if (outLen % 2 == 0)
      outSig[ik] -= 2 * ALPHA * outSig[ik - outStep];
  }

  /**
   * An implementation of the synthetize_hpf() method that works on int data, for the inverse 9x7 wavelet transform using the lifting scheme. See the general
   * description of the synthetize_hpf() method in the SynWTFilter class for more details.
   *
   * <P>The low-pass and high-pass subbands are normalized by respectively a factor of 1/KL and a factor of 1/KH
   *
   * <P>The coefficients of the first lifting step are [-DELTA 1 -DELTA].
   *
   * <P>The coefficients of the second lifting step are [-GAMMA 1 -GAMMA].
   *
   * <P>The coefficients of the third lifting step are [-BETA 1 -BETA].
   *
   * <P>The coefficients of the fourth lifting step are [-ALPHA 1 -ALPHA].
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
   */
  public void synthetize_hpf(float[] lowSig, int lowOff, int lowLen, int lowStep,
    float[] highSig, int highOff, int highLen,
    int highStep, float[] outSig, int outOff,
    int outStep)
  {

    int i;
    int outLen = lowLen + highLen; //Length of the output signal
    int iStep = 2 * outStep; //Upsampling in outSig
    int ik; //Indexing outSig
    int lk; //Indexing lowSig
    int hk; //Indexing highSig

    // Initialize counters
    lk = lowOff;
    hk = highOff;

    if (outLen != 1)
    {
      int outLen2 = outLen >> 1;
      // "Inverse normalize" each sugarcube.app.sample
      for (i = 0; i < outLen2; i++)
      {
        lowSig[lk] /= KL;
        highSig[hk] /= KH;
        lk += lowStep;
        hk += highStep;
      }
      // "Inverse normalise" last high pass coefficient
      if (outLen % 2 == 1)
        highSig[hk] /= KH;
    }
    else
      // Normalize for Nyquist gain
      highSig[highOff] /= 2;

    // Generate intermediate low frequency subband

    //Initialize counters
    lk = lowOff;
    hk = highOff;
    ik = outOff + outStep;

    //Apply lifting step to each "inner" sugarcube.app.sample
    for (i = 1; i < outLen - 1; i += 2)
    {
      outSig[ik] = lowSig[lk]
        - DELTA * (highSig[hk] + highSig[hk + highStep]);
      ik += iStep;
      lk += lowStep;
      hk += highStep;
    }

    if (outLen % 2 == 0 && outLen > 1)
      //Use symmetric extension
      outSig[ik] = lowSig[lk] - 2 * DELTA * highSig[hk];

    // Generate intermediate high frequency subband

    //Initialize counters
    hk = highOff;
    ik = outOff;

    if (outLen > 1)
      outSig[ik] = highSig[hk] - 2 * GAMMA * outSig[ik + outStep];
    else
      outSig[ik] = highSig[hk];

    ik += iStep;
    hk += highStep;

    //Apply lifting step to each "inner" sugarcube.app.sample
    for (i = 2; i < outLen - 1; i += 2)
    {
      outSig[ik] = highSig[hk]
        - GAMMA * (outSig[ik - outStep] + outSig[ik + outStep]);
      ik += iStep;
      hk += highStep;
    }

    //Handle head boundary effect if output signal has even length
    if (outLen % 2 == 1 && outLen > 1)
      //Use symmetric extension
      outSig[ik] = highSig[hk] - 2 * GAMMA * outSig[ik - outStep];

    // Generate even samples (inverse low-pass filter)

    //Initialize counters
    ik = outOff + outStep;

    //Apply lifting step to each "inner" sugarcube.app.sample
    for (i = 1; i < outLen - 1; i += 2)
    {
      outSig[ik] -= BETA * (outSig[ik - outStep] + outSig[ik + outStep]);
      ik += iStep;
    }

    if (outLen % 2 == 0 && outLen > 1)
      // symmetric extension.
      outSig[ik] -= 2 * BETA * outSig[ik - outStep];

    // Generate odd samples (inverse high pass-filter)

    //Initialize counters
    ik = outOff;

    if (outLen > 1)
      // symmetric extension.
      outSig[ik] -= 2 * ALPHA * outSig[ik + outStep];
    ik += iStep;

    //Apply first lifting step to each "inner" sugarcube.app.sample
    for (i = 2; i < outLen - 1; i += 2)
    {
      outSig[ik] -= ALPHA * (outSig[ik - outStep] + outSig[ik + outStep]);
      ik += iStep;
    }

    //Handle head boundary effect if input signal has even length
    if ((outLen % 2 == 1) && (outLen > 1))
      //Use symmetric extension 
      outSig[ik] -= 2 * ALPHA * outSig[ik - outStep];
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
   *
   *
   */
  public boolean isSameAsFullWT(int tailOvrlp, int headOvrlp, int inLen)
  {

    //If the input signal has even length.
    if (inLen % 2 == 0)
      if (tailOvrlp >= 2 && headOvrlp >= 1)
        return true;
      else
        return false;
    //Else if the input signal has odd length.
    else
      if (tailOvrlp >= 2 && headOvrlp >= 2)
        return true;
      else
        return false;
  }

  /**
   * Returns a string of information about the synthesis wavelet filter
   *
   * @return wavelet filter type.
   *
   *
   */
  public String toString()
  {
    return "w9x7 (lifting)";
  }
}
