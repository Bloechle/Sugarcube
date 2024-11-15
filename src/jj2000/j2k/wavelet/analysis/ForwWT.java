/* 
 * CVS identifier:
 * 
 * $Id: ForwWT.java,v 1.9 2001/10/24 12:02:13 grosbois Exp $
 * 
 * Class:                   ForwWT
 * 
 * Description:             The interface for implementations of a forward
 *                          wavelet transform.
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.wavelet.WaveletTransform;

/**
 * This interface extends the WaveletTransform with the specifics of forward wavelet transforms. Classes that implement forward wavelet transfoms should
 * implement this interface.
 *
 * <p>This class does not define the methods to transfer data, just the specifics to forward wavelet transform. Different data transfer methods are evisageable
 * for different transforms.</p>
 *
 */
public interface ForwWT extends WaveletTransform, ForwWTDataProps
{
  /**
   * Returns the horizontal analysis wavelet filters used in each level, for the specified tile-component. The first element in the array is the filter used to
   * obtain the lowest resolution (resolution level 0) subbands (i.e. lowest frequency LL subband), the second element is the one used to generate the
   * resolution level 1 subbands, and so on. If there are less elements in the array than the number of resolution levels, then the last one is assumed to
   * repeat itself.
   *
   * <p>The returned filters are applicable only to the specified component and in the current tile.</p>
   *
   * <p>The resolution level of a subband is the resolution level to which a subband contributes, which is different from its decomposition level.</p>
   *
   * @param t The index of the tile for which to return the filters.
   *
   * @param c The index of the component for which to return the filters.
   *
   * @return The horizontal analysis wavelet filters used in each level.
     *
   */
  public AnWTFilter[] getHorAnWaveletFilters(int t, int c);

  /**
   * Returns the vertical analysis wavelet filters used in each level, for the specified tile-component. The first element in the array is the filter used to
   * obtain the lowest resolution (resolution level 0) subbands (i.e. lowest frequency LL subband), the second element is the one used to generate the
   * resolution level 1 subbands, and so on. If there are less elements in the array than the number of resolution levels, then the last one is assumed to
   * repeat itself.
   *
   * <p>The returned filters are applicable only to the specified component and in the current tile.</p>
   *
   * <p>The resolution level of a subband is the resolution level to which a subband contributes, which is different from its decomposition level.</p>
   *
   * @param t The index of the tile for which to return the filters.
   *
   * @param c The index of the component for which to return the filters.
   *
   * @return The vertical analysis wavelet filters used in each level.
     *
   */
  public AnWTFilter[] getVertAnWaveletFilters(int t, int c);

  /**
   * Returns the number of decomposition levels that are applied to obtain the LL band, in the specified tile-component. A value of 0 means that no wavelet
   * transform is applied.
   *
   * @param t The tile index
   *
   * @param c The index of the component.
   *
   * @return The number of decompositions applied to obtain the LL subband (0 for no wavelet transform).
     *
   */
  public int getDecompLevels(int t, int c);

  /**
   * Returns the wavelet tree decomposition. Only WT_DECOMP_DYADIC is supported by JPEG 2000 part I.
   *
   * @param t The tile index
   *
   * @param c The index of the component.
   *
   * @return The wavelet decomposition.
     *
   */
  public int getDecomp(int t, int c);
}
