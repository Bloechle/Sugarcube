/*
 * CVS identifier:
 *
 * $Id: ROIDeScaler.java,v 1.39 2001/10/24 12:02:51 grosbois Exp $
 *
 *
 * Class:                   ROIDeScaler
 *
 * Description:             The class taking care of de-scaling ROI coeffs.
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.roi;

import jj2000.j2k.decoder.DecoderSpecs;
import jj2000.j2k.image.DataBlk;
import jj2000.j2k.quantization.dequantizer.CBlkQuantDataSrcDec;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.synthesis.MultiResImgDataAdapter;
import jj2000.j2k.wavelet.synthesis.SubbandSyn;

/**
 * This class takes care of the de-scaling of ROI coefficients. The de-scaler works on a tile basis and any mask that is generated is for the current mask only
 *
 * <p>Default implementations of the methods in 'MultiResImgData' are provided through the 'MultiResImgDataAdapter' abstract class.</p>
 *
 * <p>Sign-magnitude representation is used (instead of two's complement) for the output data. The most significant bit is used for the sign (0 if positive, 1
 * if negative). Then the magnitude of the quantized coefficient is stored in the next most significat bits. The most significant magnitude bit corresponds to
 * the most significant bit-plane and so on.</p>
 *
 */
public class ROIDeScaler extends MultiResImgDataAdapter
  implements CBlkQuantDataSrcDec
{
  /**
   * The MaxShiftSpec containing the scaling values for all tile-components
     *
   */
  private MaxShiftSpec mss;
  /**
   * The prefix for ROI decoder options: 'R'
   */
  public final static char OPT_PREFIX = 'R';
  /**
   * The list of parameters that is accepted by the entropy decoders. They start with 'R'.
   */
  private final static String[][] pinfo =
  {
    {
      "Rno_roi", null,
      "This argument makes sure that the no ROI de-scaling is performed. "
      + "Decompression is done like there is no ROI in the image", null
    },
  };
  /**
   * The entropy decoder from where to get the compressed data (the source)
     *
   */
  private CBlkQuantDataSrcDec src;

  /**
   * Constructor of the ROI descaler, takes EntropyDEcoder as source of data to de-scale.
   *
   * @param src The EntropyDecoder that is the source of data.
   *
   * @param mss The MaxShiftSpec containing the scaling values for all tile-components
     *
   */
  public ROIDeScaler(CBlkQuantDataSrcDec src, MaxShiftSpec mss)
  {
    super(src);
    this.src = src;
    this.mss = mss;
  }

  /**
   * Returns the subband tree, for the specified tile-component. This method returns the root element of the subband tree structure, see Subband and SubbandSyn.
   * The tree comprises all the available resolution levels.
   *
   * <P>The number of magnitude bits ('magBits' member variable) for each subband is not initialized.
   *
   * @param t The index of the tile, from 0 to T-1.
   *
   * @param c The index of the component, from 0 to C-1.
   *
   * @return The root of the tree structure.
     *
   */
  public SubbandSyn getSynSubbandTree(int t, int c)
  {
    return src.getSynSubbandTree(t, c);
  }

  /**
   * Returns the horizontal code-block partition origin. Allowable values are 0 and 1, nothing else.
     *
   */
  public int getCbULX()
  {
    return src.getCbULX();
  }

  /**
   * Returns the vertical code-block partition origin. Allowable values are 0 and 1, nothing else.
     *
   */
  public int getCbULY()
  {
    return src.getCbULY();
  }

  /**
   * Returns the parameters that are used in this class and implementing classes. It returns a 2D String array. Each of the 1D arrays is for a different option,
   * and they have 3 elements. The first element is the option name, the second one is the synopsis and the third one is a long description of what the
   * parameter is. The synopsis or description may be 'null', in which case it is assumed that there is no synopsis or description of the option, respectively.
   * Null may be returned if no options are supported.
   *
   * @return the options name, their synopsis and their explanation, or null if no options are supported.
     *
   */
  public static String[][] getParameterInfo()
  {
    return pinfo;
  }

  /**
   * Returns the specified code-block in the current tile for the specified component, as a copy (see below).
   *
   * <p>The returned code-block may be progressive, which is indicated by the 'progressive' variable of the returned 'DataBlk' object. If a code-block is
   * progressive it means that in a later request to this method for the same code-block it is possible to retrieve data which is a better approximation, since
   * meanwhile more data to decode for the code-block could have been received. If the code-block is not progressive then later calls to this method for the
   * same code-block will return the exact same data values.</p>
   *
   * <p>The data returned by this method is always a copy of the internal data of this object, if any, and it can be modified "in place" without any problems
   * after being returned. The 'offset' of the returned data is 0, and the 'scanw' is the same as the code-block width. See the 'DataBlk' class.</p>
   *
   * <p>The 'ulx' and 'uly' members of the returned 'DataBlk' object contain the coordinates of the top-left corner of the block, with respect to the tile, not
   * the subband.</p>
   *
   * @param c The component for which to return the next code-block.
   *
   * @param m The vertical index of the code-block to return, in the specified subband.
   *
   * @param n The horizontal index of the code-block to return, in the specified subband.
   *
   * @param sb The subband in which the code-block to return is.
   *
   * @param cblk If non-null this object will be used to return the new code-block. If null a new one will be allocated and returned. If the "data" array of the
   * object is non-null it will be reused, if possible, to return the data.
   *
   * @return The next code-block in the current tile for component 'c', or null if all code-blocks for the current tile have been returned.
   *
   * @see DataBlk
     *
   */
  public DataBlk getCodeBlock(int c, int m, int n, SubbandSyn sb, DataBlk cblk)
  {
    return getInternCodeBlock(c, m, n, sb, cblk);
  }

  /**
   * Returns the specified code-block in the current tile for the specified component (as a reference or copy).
   *
   * <p>The returned code-block may be progressive, which is indicated by the 'progressive' variable of the returned 'DataBlk' object. If a code-block is
   * progressive it means that in a later request to this method for the same code-block it is possible to retrieve data which is a better approximation, since
   * meanwhile more data to decode for the code-block could have been received. If the code-block is not progressive then later calls to this method for the
   * same code-block will return the exact same data values.</p>
   *
   * <p>The data returned by this method can be the data in the internal buffer of this object, if any, and thus can not be modified by the caller. The 'offset'
   * and 'scanw' of the returned data can be arbitrary. See the 'DataBlk' class.</p>
   *
   * <p>The 'ulx' and 'uly' members of the returned 'DataBlk' object contain the coordinates of the top-left corner of the block, with respect to the tile, not
   * the subband.</p>
   *
   * @param c The component for which to return the next code-block.
   *
   * @param m The vertical index of the code-block to return, in the specified subband.
   *
   * @param n The horizontal index of the code-block to return, in the specified subband.
   *
   * @param sb The subband in which the code-block to return is.
   *
   * @param cblk If non-null this object will be used to return the new code-block. If null a new one will be allocated and returned. If the "data" array of the
   * object is non-null it will be reused, if possible, to return the data.
   *
   * @return The requested code-block in the current tile for component 'c'.
   *
   * @see DataBlk
     *
   */
  public DataBlk getInternCodeBlock(int c, int m, int n, SubbandSyn sb,
    DataBlk cblk)
  {
    int mi, i, j, k, wrap;
    int ulx, uly, w, h;
    int[] data;                       // local copy of quantized data
    int tmp;
    int limit;

    // Get data block from entropy decoder
    cblk = src.getInternCodeBlock(c, m, n, sb, cblk);

    // If there are no ROIs in the tile, Or if we already got all blocks
    boolean noRoiInTile = false;
    if (mss == null || mss.getTileCompVal(getTileIdx(), c) == null)
      noRoiInTile = true;

    if (noRoiInTile || cblk == null)
      return cblk;
    data = (int[]) cblk.getData();
    ulx = cblk.ulx;
    uly = cblk.uly;
    w = cblk.w;
    h = cblk.h;

    // Scale coefficients according to magnitude. If the magnitude of a
    // coefficient is lower than 2 pow 31-magbits then it is a background
    // coeff and should be up-scaled
    int boost = ((Integer) mss.getTileCompVal(getTileIdx(), c)).intValue();
    int mask = ((1 << sb.magbits) - 1) << (31 - sb.magbits);
    int mask2 = (~mask) & 0x7FFFFFFF;

    wrap = cblk.scanw - w;
    i = cblk.offset + cblk.scanw * (h - 1) + w - 1;
    for (j = h; j > 0; j--)
    {
      for (k = w; k > 0; k--, i--)
      {
        tmp = data[i];
        if ((tmp & mask) == 0) // BG
          data[i] = (tmp & 0x80000000) | (tmp << boost);
        else // ROI
          if ((tmp & mask2) != 0)
            // decoded more than magbits bit-planes, set
            // quantization mid-interval approx. bit just after
            // the magbits.
            data[i] = (tmp & (~mask2)) | (1 << (30 - sb.magbits));
      }
      i -= wrap;
    }
    return cblk;
  }

  /**
   * Creates a ROIDeScaler object. The information needed to create the object is the Entropy decoder used and the parameters.
   *
   * @param src The source of data that is to be descaled
   *
   * @param pl The parameter list (or options).
   *
   * @param decSpec The decoding specifications
   *
   * @exception IllegalArgumentException If an error occurs while parsing the options in 'pl'
     *
   */
  public static ROIDeScaler createInstance(CBlkQuantDataSrcDec src,
    ParameterList pl,
    DecoderSpecs decSpec)
  {
    String noRoi;
    int i;

    // Check parameters
    pl.checkList(OPT_PREFIX, pl.toNameArray(pinfo));

    // Check if no_roi specified in command line or no roi signalled
    // in bit stream
    noRoi = pl.value("Rno_roi");
    if (noRoi != null || decSpec.rois == null)
      // no_roi specified in commandline!
      return new ROIDeScaler(src, null);

    return new ROIDeScaler(src, decSpec.rois);
  }
}
