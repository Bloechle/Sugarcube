/*
 * CVS identifier:
 *
 * $Id: CBlkRateDistStats.java,v 1.11 2001/09/14 09:23:51 grosbois Exp $
 *
 * Class:                   CBlkRateDistStats
 *
 * Description:             The coded (compressed) code-block with
 *                          rate-distortion statistics.
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.entropy.encoder;

import jj2000.j2k.entropy.CodedCBlk;
import jj2000.j2k.wavelet.analysis.SubbandAn;

/**
 * This class stores coded (compressed) code-blocks with their associated rate-distortion statistics. This object should always contain all the compressed data
 * of the code-block. It is applicable to the encoder engine only. Some data of the coded-block is stored in the super class, see CodedCBlk.
 *
 * <p>The rate-distortion statistics (i.e. R-D slope) is stored for valid points only. The set of valid points is determined by the entropy coder engine itself.
 * Normally they are selected so as to lye in a convex hull, which can be achived by using the 'selectConvexHull' method of this class, but some other
 * strategies might be employed.</p>
 *
 * <p>The rate (in bytes) for each truncation point (valid or not) is stored in the 'truncRates' array. The rate of a truncation point is the total number of
 * bytes in 'data' (see super class) that have to be decoded to reach the truncation point.</p>
 *
 * <p>The slope (reduction of distortion divided by the increase in rate) at each of the valid truncation points is stored in 'truncSlopes'.</p>
 *
 * <p>The index of each valid truncation point is stored in 'truncIdxs'. The index should be interpreted in the following way: a valid truncation point at
 * position 'n' has the index 'truncIdxs[n]', the rate 'truncRates[truncIdxs[n]]' and the slope 'truncSlopes[n]'. The arrays 'truncIdxs' and 'truncRates' have
 * at least 'nVldTrunc' elements. The 'truncRates' array has at least 'nTotTrunc' elements.</p>
 *
 * <p>In addition the 'isTermPass' array contains a flag for each truncation point (valid and non-valid ones) that tells if the pass is terminated or not. If
 * this variable is null then it means that no pass is terminated, except the last one which always is.</p>
 *
 * <p>The compressed data is stored in the 'data' member variable of the super class.</p>
 *
 * @see CodedCBlk
 *
 */
public class CBlkRateDistStats extends CodedCBlk
{
  /**
   * The subband to which the code-block belongs
   */
  public SubbandAn sb;
  /**
   * The total number of truncation points
   */
  public int nTotTrunc;
  /**
   * The number of valid truncation points
   */
  public int nVldTrunc;
  /**
   * The rate (in bytes) for each truncation point (valid and non-valid ones)
   */
  public int truncRates[];
  /**
   * The distortion for each truncation point (valid and non-valid ones)
   */
  public double truncDists[];
  /**
   * The negative of the rate-distortion slope for each valid truncation point
   */
  public float truncSlopes[];
  /**
   * The indices of the valid truncation points, in increasing order.
   */
  public int truncIdxs[];
  /**
   * Array of flags indicating terminated passes (valid or non-valid truncation points).
   */
  public boolean isTermPass[];
  /**
   * The number of ROI coefficients in the code-block
   */
  public int nROIcoeff = 0;
  /**
   * Number of ROI coding passes
   */
  public int nROIcp = 0;

  /**
   * Creates a new CBlkRateDistStats object without allocating any space for 'truncRates', 'truncSlopes', 'truncDists' and 'truncIdxs' or 'data'.
     *
   */
  public CBlkRateDistStats()
  {
  }

  /**
   * Creates a new CBlkRateDistStats object and initializes the valid truncation points, their rates and their slopes, from the 'rates' and 'dist' arrays. The
   * 'rates', 'dist' and 'termp' arrays must contain the rate (in bytes), the reduction in distortion (from nothing coded) and the flag indicating if
   * termination is used, respectively, for each truncation point.
   *
   * <p>The valid truncation points are selected by taking them as lying on a convex hull. This is done by calling the method selectConvexHull().</p>
   *
   * <p>Note that the arrays 'rates' and 'termp' are copied, not referenced, so they can be modified after a call to this constructor.</p>
   *
   * @param m The horizontal index of the code-block, within the subband.
   *
   * @param n The vertical index of the code-block, within the subband.
   *
   * @param skipMSBP The number of skipped most significant bit-planes for this code-block.
   *
   * @param data The compressed data. This array is referenced by this object so it should not be modified after.
   *
   * @param rates The rates (in bytes) for each truncation point in the compressed data. This array is modified by the method but no reference is kept to it.
   *
   * @param dists The reduction in distortion (with respect to no information coded) for each truncation point. This array is modified by the method but no
   * reference is kept to it.
   *
   * @param termp An array of boolean flags indicating, for each pass, if a pass is terminated or not (true if terminated). If null then it is assumed that no
   * pass is terminated except the last one which always is.
   *
   * @param np The number of truncation points contained in 'rates', 'dist' and 'termp'.
   *
   * @param inclast If false the convex hull is constructed as for lossy coding. If true it is constructed as for lossless coding, in which case it is ensured
   * that all bit-planes are sent (i.e. the last truncation point is always included).
     *
   */
  public CBlkRateDistStats(int m, int n, int skipMSBP, byte data[],
    int rates[], double dists[], boolean termp[],
    int np, boolean inclast)
  {
    super(m, n, skipMSBP, data);
    selectConvexHull(rates, dists, termp, np, inclast);
  }

  /**
   * Compute the rate-distorsion slopes and selects those that lie in a convex hull. It will compute the slopes, select the ones that form the convex hull and
   * initialize the 'truncIdxs' and 'truncSlopes' arrays, as well as 'nVldTrunc', with the selected truncation points. It will also initialize 'truncRates' and
   * 'isTermPass' arrays, as well as 'nTotTrunc', with all the truncation points (selected or not).
   *
   * <p> Note that the arrays 'rates' and 'termp' are copied, not referenced, so they can be modified after a call to this method.</p>
   *
   * @param rates The rates (in bytes) for each truncation point in the compressed data. This array is modified by the method.
   *
   * @param dists The reduction in distortion (with respect to no information coded) for each truncation point. This array is modified by the method.
   *
   * @param termp An array of boolean flags indicating, for each pass, if a pass is terminated or not (true if terminated). If null then it is assumed that no
   * pass is terminated except the last one which always is.
   *
   * @param n The number of truncation points contained in 'rates', 'dist' and 'termp'.
   *
   * @param inclast If false the convex hull is constructed as for lossy coding. If true it is constructed as for lossless coding, in which case it is ensured
   * that all bit-planes are sent (i.e. the last truncation point is always included).
     *
   */
  public void selectConvexHull(int rates[], double dists[], boolean termp[],
    int n, boolean inclast)
  {
    int first_pnt;    // The first point containing some coded data
    int p;            // last selected point
    int k;            // current point
    int i;            // current valid point
    int npnt;         // number of selected (i.e. valid) points
    int delta_rate;   // Rate difference
    double delta_dist; // Distortion difference
    float k_slope;    // R-D slope for the current point
    float p_slope;    // R-D slope for the last selected point
    int ll_rate;      // Rate for "lossless" coding (i.e. all coded info)

    // Convention: when a negative value is stored in 'rates' it meas an
    // invalid point. The absolute value is always the rate for that point.

    // Look for first point with some coded info (rate not 0)
    first_pnt = 0;
    while (first_pnt < n && rates[first_pnt] <= 0)
      first_pnt++;

    // Select the valid points
    npnt = n - first_pnt;
    p_slope = 0f; // To keep compiler happy
    ploop:
    do
    {
      p = -1;
      for (k = first_pnt; k < n; k++)
      {
        if (rates[k] < 0) // Already invalidated point
          continue;
        // Calculate decrease in distortion and rate
        if (p >= 0)
        {
          delta_rate = rates[k] - rates[p];
          delta_dist = dists[k] - dists[p];
        }
        else
        { // This is with respect to no info coded
          delta_rate = rates[k];
          delta_dist = dists[k];
        }
        // If exactly same distortion don't eliminate if the rates are 
        // equal, otherwise it can lead to infinite slope in lossless
        // coding.
        if (delta_dist < 0f || (delta_dist == 0f && delta_rate > 0))
        {
          // This point increases distortion => invalidate
          rates[k] = -rates[k];
          npnt--;
          continue; // Goto next point
        }
        k_slope = (float) (delta_dist / delta_rate);
        // Check that there is a decrease in distortion, slope is not
        // infinite (i.e. delta_dist is not 0) and slope is
        // decreasing.
        if (p >= 0
          && (delta_rate <= 0 || k_slope >= p_slope))
        {
          // Last point was not good
          rates[p] = -rates[p]; // Remove p from valid points
          npnt--;
          continue ploop; // Restart from the first one
        }
        else
        {
          p_slope = k_slope;
          p = k;
        }
      }
      // If we get to last point we are done
      break;
    }
    while (true); // We end the loop with the break statement

    // If in lossless mode make sure we don't eliminate any last
    // bit-planes from being sent.
    if (inclast && n > 0 && rates[n - 1] < 0)
    {
      rates[n - 1] = -rates[n - 1];
      // This rate can never be equal to any previous selected rate,
      // given the selection algorithm above, so no problem arises of
      // infinite slopes.
      npnt++;
    }

    // Initialize the arrays of this object
    nTotTrunc = n;
    nVldTrunc = npnt;
    truncRates = new int[n];
    truncDists = new double[n];
    truncSlopes = new float[npnt];
    truncIdxs = new int[npnt];
    if (termp != null)
    {
      isTermPass = new boolean[n];
      System.arraycopy(termp, 0, isTermPass, 0, n);
    }
    else
      isTermPass = null;
    System.arraycopy(rates, 0, truncRates, 0, n);
    for (k = first_pnt, p = -1, i = 0; k < n; k++)
      if (rates[k] > 0)
      { // A valid point
        truncDists[k] = dists[k];
        if (p < 0) // Only arrives at first valid point
          truncSlopes[i] = (float) (dists[k] / rates[k]);
        else
          truncSlopes[i] = (float) ((dists[k] - dists[p])
            / (rates[k] - rates[p]));
        truncIdxs[i] = k;
        i++;
        p = k;
      }
      else
      {
        truncDists[k] = -1;
        truncRates[k] = -truncRates[k];
      }
  }

  /**
   * Returns the contents of the object in a string. This is used for debugging.
   *
   * @return A string with the contents of the object
     *
   */
  public String toString()
  {
    String str = super.toString()
      + "\n nVldTrunc=" + nVldTrunc + ", nTotTrunc=" + nTotTrunc + ", num. ROI"
      + " coeff=" + nROIcoeff + ", num. ROI coding passes=" + nROIcp + ", sb="
      + sb.sbandIdx;
//          str += "\n\ttruncRates:\n";
//          for(int i=0; i<truncRates.length; i++) {
//              str += "\t "+i+": "+truncRates[i]+"\n";
//          }
    return str;
  }
}
