/*
 * CVS identifier:
 *
 * $Id: ROIScaler.java,v 1.11 2001/09/20 12:42:20 grosbois Exp $
 *
 * Class:                   ROIScaler
 *
 * Description:             This class takes care of the scaling of the 
 *                          samples
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.roi.encoder;

import jj2000.j2k.ModuleSpec;
import jj2000.j2k.encoder.EncoderSpecs;
import jj2000.j2k.image.DataBlkInt;
import jj2000.j2k.image.ImgDataAdapter;
import jj2000.j2k.image.input.ImgReaderPGM;
import jj2000.j2k.quantization.quantizer.CBlkQuantDataSrcEnc;
import jj2000.j2k.quantization.quantizer.Quantizer;
import jj2000.j2k.roi.MaxShiftSpec;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.Subband;
import jj2000.j2k.wavelet.analysis.CBlkWTData;
import jj2000.j2k.wavelet.analysis.SubbandAn;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class deals with the ROI functionality.
 *
 * <p>The ROI method is the Maxshift method. The ROIScaler works by scaling the quantized wavelet coefficients that do not affect the ROI (i.e background
 * coefficients) so that these samples get a lower significance than the ROI ones. By scaling the coefficients sufficiently, the ROI coefficients can be
 * recognized by their amplitude alone and no ROI mask needs to be generated at the decoder side.
 *
 * <p>The source module must be a quantizer and code-block's data is exchange with thanks to CBlkWTData instances.
 *
 * @see Quantizer
 * @see CBlkWTData
 *
 */
public class ROIScaler extends ImgDataAdapter implements CBlkQuantDataSrcEnc
{
  /**
   * The prefix for ROI Scaler options: 'R'
   */
  public final static char OPT_PREFIX = 'R';
  /**
   * The list of parameters that are accepted for ROI coding. Options for ROI Scaler start with 'R'.
   */
  private final static String[][] pinfo =
  {
    {
      "Rroi", "[<component idx>] R <left> <top> <width> <height>"
      + " or [<component idx>] C <centre column> <centre row> "
      + "<radius> or [<component idx>] A <filename>",
      "Specifies ROIs shape and location. The shape can be either "
      + "rectangular 'R', or circular 'C' or arbitrary 'A'. "
      + "Each new occurrence of an 'R', a 'C' or an 'A' is a new ROI. "
      + "For circular and rectangular ROIs, all values are "
      + "given as their pixel values relative to the canvas origin. "
      + "Arbitrary shapes must be included in a PGM file where non 0 "
      + "values correspond to ROI coefficients. The PGM file must have "
      + "the size as the image. "
      + "The component idx specifies which components "
      + "contain the ROI. The component index is specified as described "
      + "by points 3 and 4 in the general comment on tile-component idx. "
      + "If this option is used, the codestream is layer progressive by "
      + "default unless it is overridden by the 'Aptype' option.",
      null
    },
    {
      "Ralign", "[on|off]",
      "By specifying this argument, the ROI mask will be "
      + "limited to covering only entire code-blocks. The ROI coding can "
      + "then be performed without any actual scaling of the coefficients "
      + "but by instead scaling the distortion estimates.", "off"
    },
    {
      "Rstart_level", "<level>",
      "This argument forces the lowest <level> resolution levels to "
      + "belong to the ROI. By doing this, it is possible to avoid only "
      + "getting information for the ROI at an early stage of "
      + "transmission.<level> = 0 means the lowest resolution level "
      + "belongs to the ROI, 1 means the two lowest etc. (-1 deactivates"
      + " the option)", "-1"
    },
    {
      "Rno_rect", "[on|off]",
      "This argument makes sure that the ROI mask generation is not done "
      + "using the fast ROI mask generation for rectangular ROIs "
      + "regardless of whether the specified ROIs are rectangular or not",
      "off"
    },
  };
  /**
   * The maximum number of magnitude bit-planes in any subband. One value for each tile-component
   */
  private int maxMagBits[][];
  /**
   * Flag indicating the presence of ROIs
   */
  private boolean roi;
  /**
   * Flag indicating if block aligned ROIs are used
   */
  private boolean blockAligned;
  /**
   * Number of resolution levels to include in ROI mask
   */
  private int useStartLevel;
  /**
   * The class generating the ROI mask
   */
  private ROIMaskGenerator mg;
  /**
   * The ROI mask
   */
  private DataBlkInt roiMask;
  /**
   * The source of quantized wavelet transform coefficients
   */
  private Quantizer src;

  /**
   * Constructor of the ROI scaler, takes a Quantizer as source of data to scale.
   *
   * @param src The quantizer that is the source of data.
   *
   * @param mg The mask generator that will be used for all components
   *
   * @param roi Flag indicating whether there are rois specified.
   *
   * @param sLev The resolution levels that belong entirely to ROI
   *
   * @param uba Flag indicating whether block aligning is used.
   *
   * @param encSpec The encoder specifications for addition of roi specs
     *
   */
  public ROIScaler(Quantizer src,
    ROIMaskGenerator mg,
    boolean roi,
    int sLev,
    boolean uba,
    EncoderSpecs encSpec)
  {
    super(src);
    this.src = src;
    this.roi = roi;
    this.useStartLevel = sLev;
    if (roi)
    {
      // If there is no ROI, no need to do this
      this.mg = mg;
      roiMask = new DataBlkInt();
      calcMaxMagBits(encSpec);
      blockAligned = uba;
    }
  }

  /**
   * Since ROI scaling is always a reversible operation, it calls isReversible() method of it source (the quantizer module).
   *
   * @param t The tile to test for reversibility
   *
   * @param c The component to test for reversibility
   *
   * @return True if the quantized data is reversible, false if not.
     *
   */
  public boolean isReversible(int t, int c)
  {
    return src.isReversible(t, c);
  }

  /**
   * Returns a reference to the subband tree structure representing the subband decomposition for the specified tile-component.
   *
   * @param t The index of the tile.
   *
   * @param c The index of the component.
   *
   * @return The subband tree structure, see SubbandAn.
   *
   * @see SubbandAn
   *
   * @see Subband
     *
   */
  public SubbandAn getAnSubbandTree(int t, int c)
  {
    return src.getAnSubbandTree(t, c);
  }

  /**
   * Returns the horizontal offset of the code-block partition. Allowable values are 0 and 1, nothing else.
     *
   */
  public int getCbULX()
  {
    return src.getCbULX();
  }

  /**
   * Returns the vertical offset of the code-block partition. Allowable values are 0 and 1, nothing else.
     *
   */
  public int getCbULY()
  {
    return src.getCbULY();
  }

  /**
   * Creates a ROIScaler object. The Quantizer is the source of data to scale.
   *
   * <p>The ROI Scaler creates a ROIMaskGenerator depending on what ROI information is in the ParameterList. If only rectangular ROI are used, the fast mask
   * generator for rectangular ROI can be used.</p>
   *
   * @param src The source of data to scale
   *
   * @param pl The parameter list (or options).
   *
   * @param encSpec The encoder specifications for addition of roi specs
   *
   * @exception IllegalArgumentException If an error occurs while parsing the options in 'pl'
     *
   */
  public static ROIScaler createInstance(Quantizer src,
    ParameterList pl,
    EncoderSpecs encSpec)
  {
    Vector roiVector = new Vector();
    ROIMaskGenerator maskGen = null;

    // Check parameters
    pl.checkList(OPT_PREFIX, pl.toNameArray(pinfo));

    // Get parameters and check if there are and ROIs specified 
    String roiopt = pl.value("Rroi");
    if (roiopt == null)
      // No ROIs specified! Create ROIScaler with no mask generator
      return new ROIScaler(src, null, false, -1, false, encSpec);

    // Check if the lowest resolution levels should belong to the ROI 
    int sLev = pl.integer("Rstart_level");

    // Check if the ROIs are block-aligned
    boolean useBlockAligned = pl.bool("Ralign");

    // Check if generic mask generation is specified 
    boolean onlyRect = !pl.bool("Rno_rect");

    // Parse the ROIs
    parseROIs(roiopt, src.getNumComps(), roiVector);
    ROI[] roiArray = new ROI[roiVector.size()];
    roiVector.copyInto(roiArray);

    // If onlyRect has been forced, check if there are any non-rectangular
    // ROIs specified.  Currently, only the presence of circular ROIs will
    // make this false
    if (onlyRect)
      for (int i = roiArray.length - 1; i >= 0; i--)
        if (!roiArray[i].rect)
        {
          onlyRect = false;
          break;
        }

    if (onlyRect)
      // It's possible to use the fast ROI mask generation when only
      // rectangular ROIs are specified.
      maskGen = new RectROIMaskGenerator(roiArray, src.getNumComps());
    else
      // It's necessary to use the generic mask generation
      maskGen = new ArbROIMaskGenerator(roiArray, src.getNumComps(), src);
    return new ROIScaler(src, maskGen, true, sLev, useBlockAligned, encSpec);
  }

  /**
   * This function parses the values given for the ROIs with the argument -Rroi. Currently only circular and rectangular ROIs are supported.
   *
   * <p>A rectangular ROI is indicated by a 'R' followed the coordinates for the upper left corner of the ROI and then its width and height.</p>
   *
   * <p>A circular ROI is indicated by a 'C' followed by the coordinates of the circle center and then the radius.</p>
   *
   * <p>Before the R and C values, the component that are affected by the ROI are indicated.</p>
   *
   * @param roiopt The info on the ROIs
   *
   * @param nc number of components
   *
   * @param roiVector The vcector containing the ROI parsed from the cmd line
   *
   * @return The ROIs specified in roiopt
     *
   */
  protected static Vector parseROIs(String roiopt, int nc, Vector roiVector)
  {
    ROI[] ROIs;
    ROI roi;
    StringTokenizer stok;
    char tok;
    int nrOfROIs = 0;
    char c;
    int comp, ulx, uly, w, h, x, y, rad;
    boolean[] roiInComp = null;

    stok = new StringTokenizer(roiopt);

    String word;
    while (stok.hasMoreTokens())
    {
      word = stok.nextToken();

      switch (word.charAt(0))
      {
        case 'c': // Components specification
          roiInComp = ModuleSpec.parseIdx(word, nc);
          break;
        case 'R': // Rectangular ROI to be read
          nrOfROIs++;
          try
          {
            word = stok.nextToken();
            ulx = (new Integer(word)).intValue();
            word = stok.nextToken();
            uly = (new Integer(word)).intValue();
            word = stok.nextToken();
            w = (new Integer(word)).intValue();
            word = stok.nextToken();
            h = (new Integer(word)).intValue();
          }
          catch (NumberFormatException e)
          {
            throw new IllegalArgumentException("Bad parameter for "
              + "'-Rroi R' option : "
              + word);
          }
          catch (NoSuchElementException f)
          {
            throw new IllegalArgumentException("Wrong number of "
              + "parameters for  "
              + "h'-Rroi R' option.");
          }

          // If the ROI is component-specific, check which comps.
          if (roiInComp != null)
          {
            for (int i = 0; i < nc; i++)
              if (roiInComp[i])
              {
                roi = new ROI(i, ulx, uly, w, h);
                roiVector.addElement(roi);
              }
          }
          else // Otherwise add ROI for all components
            for (int j = 0; j < nc; j++)
            {
              roi = new ROI(j, ulx, uly, w, h);
              roiVector.addElement(roi);
            }    
          break;
        case 'C': // Circular ROI to be read
          nrOfROIs++;

          try
          {
            word = stok.nextToken();
            x = (new Integer(word)).intValue();
            word = stok.nextToken();
            y = (new Integer(word)).intValue();
            word = stok.nextToken();
            rad = (new Integer(word)).intValue();
          }
          catch (NumberFormatException e)
          {
            throw new IllegalArgumentException("Bad parameter for "
              + "'-Rroi C' option : "
              + word);
          }
          catch (NoSuchElementException f)
          {
            throw new IllegalArgumentException("Wrong number of "
              + "parameters for "
              + "'-Rroi C' option.");
          }

          // If the ROI is component-specific, check which comps.
          if (roiInComp != null)
          {
            for (int i = 0; i < nc; i++)
              if (roiInComp[i])
              {
                roi = new ROI(i, x, y, rad);
                roiVector.addElement(roi);
              }
          }
          else // Otherwise add ROI for all components
            for (int i = 0; i < nc; i++)
            {
              roi = new ROI(i, x, y, rad);
              roiVector.addElement(roi);
            }
          break;
        case 'A': // ROI wth arbitrary shape
          nrOfROIs++;

          String filename;
          ImgReaderPGM maskPGM = null;

          try
          {
            filename = stok.nextToken();
          }
          catch (NoSuchElementException e)
          {
            throw new IllegalArgumentException("Wrong number of "
              + "parameters for "
              + "'-Rroi A' option.");
          }
          try
          {
            maskPGM = new ImgReaderPGM(filename);
          }
          catch (IOException e)
          {
            throw new Error("Cannot read PGM file with ROI");
          }

          // If the ROI is component-specific, check which comps.
          if (roiInComp != null)
          {
            for (int i = 0; i < nc; i++)
              if (roiInComp[i])
              {
                roi = new ROI(i, maskPGM);
                roiVector.addElement(roi);
              }
          }
          else // Otherwise add ROI for all components
            for (int i = 0; i < nc; i++)
            {
              roi = new ROI(i, maskPGM);
              roiVector.addElement(roi);
            }
          break;
        default:
          throw new Error("Bad parameters for ROI nr " + roiVector.size());
      }
    }

    return roiVector;
  }

  /**
   * This function gets a datablk from the entropy coder. The sugarcube.app.sample sin the block, which consists of the quantized coefficients from the quantizer, are scaled
   * by the values given for any ROIs specified.
   *
   * <p>The function calls on a ROIMaskGenerator to get the mask for scaling the coefficients in the current block.</p>
   *
   * <p>The data returned by this method is a copy of the orignal data. Therfore it can be modified "in place" without any problems after being returned. The
   * 'offset' of the returned data is 0, and the 'scanw' is the same as the code-block width. See the 'CBlkWTData' class.</p>
   *
   * @param c The component for which to return the next code-block.
   *
   * @param cblk If non-null this object will be used to return the new code-block. If null a new one will be allocated and returned. If the "data" array of the
   * object is non-null it will be reused, if possible, to return the data.
   *
   * @return The next code-block in the current tile for component 'n', or null if all code-blocks for the current tile have been returned.
   *
   * @see CBlkWTData
     *
   */
  public CBlkWTData getNextCodeBlock(int c, CBlkWTData cblk)
  {
    return getNextInternCodeBlock(c, cblk);
  }

  /**
   * This function gets a datablk from the entropy coder. The sugarcube.app.sample sin the block, which consists of the quantized coefficients from the quantizer, are scaled
   * by the values given for any ROIs specified.
   *
   * <p>The function calls on a ROIMaskGenerator to get the mask for scaling the coefficients in the current block.</p>
   *
   * @param c The component for which to return the next code-block.
   *
   * @param cblk If non-null this object will be used to return the new code-block. If null a new one will be allocated and returned. If the "data" array of the
   * object is non-null it will be reused, if possible, to return the data.
   *
   * @return The next code-block in the current tile for component 'n', or null if all code-blocks for the current tile have been returned.
   *
   * @see CBlkWTData
     *
   */
  public CBlkWTData getNextInternCodeBlock(int c, CBlkWTData cblk)
  {
    int mi, i, j, k, wrap;
    int ulx, uly, w, h;
    DataBlkInt mask = roiMask;        // local copy of mask
    int[] maskData;                   // local copy of mask data
    int[] data;                       // local copy of quantized data
    int tmp;
    int bitMask = 0x7FFFFFFF;
    SubbandAn root, sb;
    int maxBits = 0; // local copy
    boolean roiInTile;
    boolean sbInMask;
    int nROIcoeff = 0;

    // Get codeblock's data from quantizer
    cblk = src.getNextCodeBlock(c, cblk);

    // If there is no ROI in the image, or if we already got all
    // code-blocks
    if (!roi || cblk == null)
      return cblk;

    data = (int[]) cblk.getData();
    sb = cblk.sb;
    ulx = cblk.ulx;
    uly = cblk.uly;
    w = cblk.w;
    h = cblk.h;
    sbInMask = (sb.resLvl <= useStartLevel);

    // Check that there is an array for the mask and set it to zero
    maskData = mask.getDataInt(); // local copy of mask data
    if (maskData == null || w * h > maskData.length)
    {
      maskData = new int[w * h];
      mask.setDataInt(maskData);
    }
    else
      for (i = w * h - 1; i >= 0; i--)
        maskData[i] = 0;
    mask.ulx = ulx;
    mask.uly = uly;
    mask.w = w;
    mask.h = h;

    // Get ROI mask from generator
    root = src.getAnSubbandTree(tIdx, c);
    maxBits = maxMagBits[tIdx][c];
    roiInTile = mg.getROIMask(mask, root, maxBits, c);

    // If there is no ROI in this tile, return the code-block untouched
    if (!roiInTile && (!sbInMask))
    {
      cblk.nROIbp = 0;
      return cblk;
    }

    // Update field containing the number of ROI magnitude bit-planes
    cblk.nROIbp = cblk.magbits;

    // If the entire subband belongs to the ROI mask, The code-block is
    // set to belong entirely to the ROI with the highest scaling value
    if (sbInMask)
    {
      // Scale the wmse so that instead of scaling the coefficients, the
      // wmse is scaled.
      cblk.wmseScaling *= (float) (1 << (maxBits << 1));
      cblk.nROIcoeff = w * h;
      return cblk;
    }

    // In 'block aligned' mode, the code-block is set to belong entirely
    // to the ROI with the highest scaling value if one coefficient, at
    // least, belongs to the ROI
    if (blockAligned)
    {
      wrap = cblk.scanw - w;
      mi = h * w - 1;
      i = cblk.offset + cblk.scanw * (h - 1) + w - 1;
      int nroicoeff = 0;
      for (j = h; j > 0; j--)
      {
        for (k = w - 1; k >= 0; k--, i--, mi--)
          if (maskData[mi] != 0)
            nroicoeff++;
        i -= wrap;
      }
      if (nroicoeff != 0)
      { // Include the subband
        cblk.wmseScaling *= (float) (1 << (maxBits << 1));
        cblk.nROIcoeff = w * h;
      }
      return cblk;
    }

    // Scale background coefficients
    bitMask = (((1 << cblk.magbits) - 1) << (31 - cblk.magbits));
    wrap = cblk.scanw - w;
    mi = h * w - 1;
    i = cblk.offset + cblk.scanw * (h - 1) + w - 1;
    for (j = h; j > 0; j--)
    {
      for (k = w; k > 0; k--, i--, mi--)
      {
        tmp = data[i];
        if (maskData[mi] != 0)
        {
          // ROI coeff. We need to erase fractional bits to ensure
          // that they do not conflict with BG coeffs. This is only
          // strictly necessary for ROI coeffs. which non-fractional
          // magnitude is zero, but much better BG quality can be
          // achieved if done if reset to zero since coding zeros is
          // much more efficient (the entropy coder knows nothing
          // about ROI and cannot avoid coding the ROI fractional
          // bits, otherwise this would not be necessary).
          data[i] = (0x80000000 & tmp) | (tmp & bitMask);
          nROIcoeff++;
        }
        else
          // BG coeff. it is not necessary to erase fractional bits
          data[i] = (0x80000000 & tmp)
            | ((tmp & 0x7FFFFFFF) >> maxBits);
      }
      i -= wrap;
    }

    // Modify the number of significant bit-planes in the code-block
    cblk.magbits += maxBits;

    // Store the number of ROI coefficients present in the code-block
    cblk.nROIcoeff = nROIcoeff;

    return cblk;
  }

  /**
   * This function returns the ROI mask generator.
   *
   * @return The roi mask generator
   */
  public ROIMaskGenerator getROIMaskGenerator()
  {
    return mg;
  }

  /**
   * This function returns the blockAligned flag
   *
   * @return Flag indicating whether the ROIs were block aligned
   */
  public boolean getBlockAligned()
  {
    return blockAligned;
  }

  /**
   * This function returns the flag indicating if any ROI functionality used
   *
   * @return Flag indicating whether there are ROIs in the image
   */
  public boolean useRoi()
  {
    return roi;
  }

  /**
   * Returns the parameters that are used in this class and implementing classes. It returns a 2D String array. Each of the 1D arrays is for a different option,
   * and they have 3 elements. The first element is the option name, the second one is the synopsis, the third one is a long description of what the parameter
   * is and the fourth is its default value. The synopsis or description may be 'null', in which case it is assumed that there is no synopsis or description of
   * the option, respectively. Null may be returned if no options are supported.
   *
   * @return the options name, their synopsis and their explanation, or null if no options are supported.
     *
   */
  public static String[][] getParameterInfo()
  {
    return pinfo;
  }

  /**
   * Changes the current tile, given the new indexes. An IllegalArgumentException is thrown if the indexes do not correspond to a valid tile.
   *
   * @param x The horizontal index of the tile.
   *
   * @param y The vertical index of the new tile.
     *
   */
  public void setTile(int x, int y)
  {
    super.setTile(x, y);
    if (roi)
      mg.tileChanged();
  }

  /**
   * Advances to the next tile, in standard scan-line order (by rows then columns). An NoNextElementException is thrown if the current tile is the last one
   * (i.e. there is no next tile).
     *
   */
  public void nextTile()
  {
    super.nextTile();
    if (roi)
      mg.tileChanged();
  }

  /**
   * Calculates the maximum amount of magnitude bits for each tile-component, and stores it in the 'maxMagBits' array. This is called by the constructor
   *
   * @param encSpec The encoder specifications for addition of roi specs
     *
   */
  private void calcMaxMagBits(EncoderSpecs encSpec)
  {
    int tmp;
    MaxShiftSpec rois = encSpec.rois;

    int nt = src.getNumTiles();
    int nc = src.getNumComps();

    maxMagBits = new int[nt][nc];

    src.setTile(0, 0);
    for (int t = 0; t < nt; t++)
    {
      for (int c = nc - 1; c >= 0; c--)
      {
        tmp = src.getMaxMagBits(c);
        maxMagBits[t][c] = tmp;
        rois.setTileCompVal(t, c, new Integer(tmp));
      }
      if (t < nt - 1)
        src.nextTile();
    }
    // Reset to current initial tile position
    src.setTile(0, 0);
  }
}
