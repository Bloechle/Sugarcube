/*
 * CVS identifier:
 *
 * $Id: SubbandRectROIMask.java,v 1.3 2001/02/28 14:53:12 grosbois Exp $
 *
 * Class:                   ROI
 *
 * Description:             This class describes the ROI mask for a subband
 *
 *
 *
 * COPYRIGHT:
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.roi.encoder;

import jj2000.j2k.wavelet.Subband;
import jj2000.j2k.wavelet.WaveletFilter;

/**
 * This class describes the ROI mask for a single subband. Each object of the class contains the mask for a particular subband and also has references to the
 * masks of the children subbands of the subband corresponding to this mask. This class describes subband masks for images containing only rectangular ROIS
 *
 */
public class SubbandRectROIMask extends SubbandROIMask
{
  /**
   * The upper left x coordinates of the applicable ROIs
   */
  public int[] ulxs;
  /**
   * The upper left y coordinates of the applicable ROIs
   */
  public int[] ulys;
  /**
   * The lower right x coordinates of the applicable ROIs
   */
  public int[] lrxs;
  /**
   * The lower right y coordinates of the applicable ROIs
   */
  public int[] lrys;

  /**
   * The constructor of the SubbandROIMask takes the dimensions of the subband as parameters. A tree of masks is generated from the subband sb. Each Subband
   * contains the boundaries of each ROI.
   *
   * @param sb The subband corresponding to this Subband Mask
   *
   * @param ulxs The upper left x coordinates of the ROIs
   *
   * @param ulys The upper left y coordinates of the ROIs
   *
   * @param lrxs The lower right x coordinates of the ROIs
   *
   * @param lrys The lower right y coordinates of the ROIs
   *
   * @param lrys The lower right y coordinates of the ROIs
   *
   * @param nr Number of ROIs that affect this tile
     *
   */
  public SubbandRectROIMask(Subband sb, int[] ulxs, int[] ulys, int[] lrxs,
    int[] lrys, int nr)
  {
    super(sb.ulx, sb.uly, sb.w, sb.h);
    this.ulxs = ulxs;
    this.ulys = ulys;
    this.lrxs = lrxs;
    this.lrys = lrys;
    int r;

    if (sb.isNode)
    {
      isNode = true;
      // determine odd/even - high/low filters
      int horEvenLow = sb.ulcx % 2;
      int verEvenLow = sb.ulcy % 2;

      // Get filter support lengths
      WaveletFilter hFilter = sb.getHorWFilter();
      WaveletFilter vFilter = sb.getVerWFilter();
      int hlnSup = hFilter.getSynLowNegSupport();
      int hhnSup = hFilter.getSynHighNegSupport();
      int hlpSup = hFilter.getSynLowPosSupport();
      int hhpSup = hFilter.getSynHighPosSupport();
      int vlnSup = vFilter.getSynLowNegSupport();
      int vhnSup = vFilter.getSynHighNegSupport();
      int vlpSup = vFilter.getSynLowPosSupport();
      int vhpSup = vFilter.getSynHighPosSupport();

      // Generate arrays for children
      int x, y;
      int[] lulxs = new int[nr];
      int[] lulys = new int[nr];
      int[] llrxs = new int[nr];
      int[] llrys = new int[nr];
      int[] hulxs = new int[nr];
      int[] hulys = new int[nr];
      int[] hlrxs = new int[nr];
      int[] hlrys = new int[nr];
      for (r = nr - 1; r >= 0; r--)
      { // For all ROI calculate ...
        // Upper left x for all children
        x = ulxs[r];
        if (horEvenLow == 0)
        {
          lulxs[r] = (x + 1 - hlnSup) / 2;
          hulxs[r] = (x - hhnSup) / 2;
        }
        else
        {
          lulxs[r] = (x - hlnSup) / 2;
          hulxs[r] = (x + 1 - hhnSup) / 2;
        }
        // Upper left y for all children
        y = ulys[r];
        if (verEvenLow == 0)
        {
          lulys[r] = (y + 1 - vlnSup) / 2;
          hulys[r] = (y - vhnSup) / 2;
        }
        else
        {
          lulys[r] = (y - vlnSup) / 2;
          hulys[r] = (y + 1 - vhnSup) / 2;
        }
        // lower right x for all children
        x = lrxs[r];
        if (horEvenLow == 0)
        {
          llrxs[r] = (x + hlpSup) / 2;
          hlrxs[r] = (x - 1 + hhpSup) / 2;
        }
        else
        {
          llrxs[r] = (x - 1 + hlpSup) / 2;
          hlrxs[r] = (x + hhpSup) / 2;
        }
        // lower right y for all children
        y = lrys[r];
        if (verEvenLow == 0)
        {
          llrys[r] = (y + vlpSup) / 2;
          hlrys[r] = (y - 1 + vhpSup) / 2;
        }
        else
        {
          llrys[r] = (y - 1 + vlpSup) / 2;
          hlrys[r] = (y + vhpSup) / 2;
        }
      }
      // Create children
      hh = new SubbandRectROIMask(sb.getHH(), hulxs, hulys, hlrxs, hlrys, nr);
      lh = new SubbandRectROIMask(sb.getLH(), lulxs, hulys, llrxs, hlrys, nr);
      hl = new SubbandRectROIMask(sb.getHL(), hulxs, lulys, hlrxs, llrys, nr);
      ll = new SubbandRectROIMask(sb.getLL(), lulxs, lulys, llrxs, llrys, nr);

    }
  }
}
