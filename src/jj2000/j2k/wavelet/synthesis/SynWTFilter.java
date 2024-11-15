/*
 * CVS identifier:
 *
 * $Id: SynWTFilter.java,v 1.9 2001/08/02 10:05:58 grosbois Exp $
 *
 * Class:                   SynWTFilter
 *
 * Description:             The abstract class for all synthesis wavelet
 *                          filters.
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.wavelet.synthesis;

import jj2000.j2k.codestream.Markers;
import jj2000.j2k.wavelet.WaveletFilter;

/**
 * This abstract class defines the methods of all synthesis wavelet filters. Specialized abstract classes that work on particular data types (int, float)
 * provide more specific method calls while retaining the generality of this one. See the SynWTFilterInt and SynWTFilterFloat classes. Implementations of
 * snythesis filters should inherit from one of those classes.
 *
 * <p>The length of the output signal is always the sum of the length of the low-pass and high-pass input signals.</p>
 *
 * <p>All synthesis wavelet filters should follow the following conventions:
 *
 * <ul>
 *
 * <li>The first sugarcube.app.sample of the output corresponds to the low-pass one. As a consequence, if the output signal is of odd-length then the low-pass input signal is
 * one sugarcube.app.sample longer than the high-pass input one. Therefore, if the length of output signal is N, the low-pass input signal is of length N/2 if N is even and
 * N/2+1/2 if N is odd, while the high-pass input signal is of length N/2 if N is even and N/2-1/2 if N is odd.</li>
 *
 * <li>The normalization of the analysis filters is 1 for the DC gain and 2 for the Nyquist gain (Type I normalization), for both reversible and non-reversible
 * filters. The normalization of the synthesis filters should ensure prefect reconstruction according to this normalization of the analysis wavelet
 * filters.</li>
 *
 * </ul>
 *
 * <p>The synthetize method may seem very complicated, but is designed to minimize the amount of data copying and redundant calculations when used for
 * block-based or line-based wavelet transform implementations, while being applicable to full-frame transforms as well.</p>
 *
 * @see SynWTFilterInt
 * @see SynWTFilterFloat
 *
 */
public abstract class SynWTFilter implements WaveletFilter, Markers
{
  /**
   * Reconstructs the output signal by the synthesis filter, recomposing the low-pass and high-pass input signals in one output signal. This method performs the
   * upsampling and fitering with the low pass first filtering convention.
   *
   * <p>The input low-pass (high-pass) signal resides in the lowSig array. The index of the first sugarcube.app.sample to filter (i.e. that will generate the first (second)
   * output sugarcube.app.sample). is given by lowOff (highOff). This array must be of the same type as the one for which the particular implementation works with (which is
   * returned by the getDataType() method).</p>
   *
   * <p>The low-pass (high-pass) input signal can be interleaved with other signals in the same lowSig (highSig) array, and this is determined by the lowStep
   * (highStep) argument. This means that the first sugarcube.app.sample of the low-pass (high-pass) input signal is lowSig[lowOff] (highSig[highOff]), the second is
   * lowSig[lowOff+lowStep] (highSig[highOff+highStep]), the third is lowSig[lowOff+2*lowStep] (highSig[highOff+2*highStep]), and so on. Therefore if lowStep
   * (highStep) is 1 there is no interleaving. This feature allows to filter columns of a 2-D signal, when it is stored in a line by line order in lowSig
   * (highSig), without having to copy the data, in this case the lowStep (highStep) argument should be the line width of the low-pass (high-pass) signal.</p>
   *
   * <p>The output signal is placed in the outSig array. The outOff and outStep arguments are analogous to the lowOff and lowStep ones, but they apply to the
   * outSig array. The outSig array must be long enough to hold the low-pass output signal.</p>
   *
   * @param lowSig This is the array that contains the low-pass input signal. It must be of the correct type (e.g., it must be int[] if getDataType() returns
   * TYPE_INT).
   *
   * @param lowOff This is the index in lowSig of the first sugarcube.app.sample to filter.
   *
   * @param lowLen This is the number of samples in the low-pass input signal to filter.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass input signal samples in the lowSig array. See above.
   *
   * @param highSig This is the array that contains the high-pass input signal. It must be of the correct type (e.g., it must be int[] if getDataType() returns
   * TYPE_INT).
   *
   * @param highOff This is the index in highSig of the first sugarcube.app.sample to filter.
   *
   * @param highLen This is the number of samples in the high-pass input signal to filter.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass input signal samples in the highSig array. See above.
   *
   * @param outSig This is the array where the output signal is placed. It must be of the same type as lowSig and it should be long enough to contain the output
   * signal.
   *
   * @param outOff This is the index in outSig of the element where to put the first output sugarcube.app.sample.
   *
   * @param outStep This is the step, or interleave factor, of the output samples in the outSig array. See above.
     *
   */
  public abstract void synthetize_lpf(Object lowSig, int lowOff, int lowLen, int lowStep,
    Object highSig, int highOff, int highLen, int highStep,
    Object outSig, int outOff, int outStep);

  /**
   * Reconstructs the output signal by the synthesis filter, recomposing the low-pass and high-pass input signals in one output signal. This method performs the
   * upsampling and fitering with the high pass first filtering convention.
   *
   * <p>The input low-pass (high-pass) signal resides in the lowSig array. The index of the first sugarcube.app.sample to filter (i.e. that will generate the first (second)
   * output sugarcube.app.sample). is given by lowOff (highOff). This array must be of the same type as the one for which the particular implementation works with (which is
   * returned by the getDataType() method).</p>
   *
   * <p>The low-pass (high-pass) input signal can be interleaved with other signals in the same lowSig (highSig) array, and this is determined by the lowStep
   * (highStep) argument. This means that the first sugarcube.app.sample of the low-pass (high-pass) input signal is lowSig[lowOff] (highSig[highOff]), the second is
   * lowSig[lowOff+lowStep] (highSig[highOff+highStep]), the third is lowSig[lowOff+2*lowStep] (highSig[highOff+2*highStep]), and so on. Therefore if lowStep
   * (highStep) is 1 there is no interleaving. This feature allows to filter columns of a 2-D signal, when it is stored in a line by line order in lowSig
   * (highSig), without having to copy the data, in this case the lowStep (highStep) argument should be the line width of the low-pass (high-pass) signal.</p>
   *
   * <p>The output signal is placed in the outSig array. The outOff and outStep arguments are analogous to the lowOff and lowStep ones, but they apply to the
   * outSig array. The outSig array must be long enough to hold the low-pass output signal.</p>
   *
   * @param lowSig This is the array that contains the low-pass input signal. It must be of the correct type (e.g., it must be int[] if getDataType() returns
   * TYPE_INT).
   *
   * @param lowOff This is the index in lowSig of the first sugarcube.app.sample to filter.
   *
   * @param lowLen This is the number of samples in the low-pass input signal to filter.
   *
   * @param lowStep This is the step, or interleave factor, of the low-pass input signal samples in the lowSig array. See above.
   *
   * @param highSig This is the array that contains the high-pass input signal. It must be of the correct type (e.g., it must be int[] if getDataType() returns
   * TYPE_INT).
   *
   * @param highOff This is the index in highSig of the first sugarcube.app.sample to filter.
   *
   * @param highLen This is the number of samples in the high-pass input signal to filter.
   *
   * @param highStep This is the step, or interleave factor, of the high-pass input signal samples in the highSig array. See above.
   *
   * @param outSig This is the array where the output signal is placed. It must be of the same type as lowSig and it should be long enough to contain the output
   * signal.
   *
   * @param outOff This is the index in outSig of the element where to put the first output sugarcube.app.sample.
   *
   * @param outStep This is the step, or interleave factor, of the output samples in the outSig array. See above.
     *
   */
  public abstract void synthetize_hpf(Object lowSig, int lowOff, int lowLen,
    int lowStep, Object highSig, int highOff,
    int highLen, int highStep,
    Object outSig, int outOff, int outStep);
}
