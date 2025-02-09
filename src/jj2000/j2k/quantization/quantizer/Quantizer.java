/*
 * CVS identifier:
 *
 * $Id: Quantizer.java,v 1.38 2002/01/09 13:24:14 grosbois Exp $
 *
 * Class:                   Quantizer
 *
 * Description:             An abstract class for quantizers
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.quantization.quantizer;

import jj2000.j2k.encoder.EncoderSpecs;
import jj2000.j2k.image.ImgDataAdapter;
import jj2000.j2k.wavelet.Subband;
import jj2000.j2k.wavelet.analysis.CBlkWTDataSrc;
import jj2000.j2k.wavelet.analysis.SubbandAn;

/**
 * This abstract class provides the general interface for quantizers. The input of a quantizer is the output of a wavelet transform. The output of the quantizer
 * is the set of quantized wavelet coefficients represented in sign-magnitude notation (see below).
 *
 * <p>This class provides default implementation for most of the methods (wherever it makes sense), under the assumption that the image, component dimensions,
 * and the tiles, are not modifed by the quantizer. If it is not the case for a particular implementation, then the methods should be overriden.</p>
 *
 * <p>Sign magnitude representation is used (instead of two's complement) for the output data. The most significant bit is used for the sign (0 if positive, 1
 * if negative). Then the magnitude of the quantized coefficient is stored in the next M most significat bits. The rest of the bits (least significant bits) can
 * contain a fractional value of the quantized coefficient. This fractional value is not to be coded by the entropy coder. However, it can be used to compute
 * rate-distortion measures with greater precision.</p>
 *
 * <p>The value of M is determined for each subband as the sum of the number of guard bits G and the nominal range of quantized wavelet coefficients in the
 * corresponding subband (Rq), minus 1:</p>
 *
 * <p>M = G + Rq -1</p>
 *
 * <p>The value of G should be the same for all subbands. The value of Rq depends on the quantization step size, the nominal range of the component before the
 * wavelet transform and the analysis gain of the subband (see Subband).</p>
 *
 * <p>The blocks of data that are requested should not cross subband boundaries.</p>
 *
 * <p>NOTE: At the moment only quantizers that implement the 'CBlkQuantDataSrcEnc' interface are supported.</p>
 *
 * @see Subband
 *
 */
public abstract class Quantizer extends ImgDataAdapter
  implements CBlkQuantDataSrcEnc
{
  /**
   * The prefix for quantizer options: 'Q'
   */
  public final static char OPT_PREFIX = 'Q';
  /**
   * The list of parameters that is accepted for quantization. Options for quantization start with 'Q'.
   */
  private final static String[][] pinfo =
  {
    {
      "Qtype", "[<tile-component idx>] <id> "
      + "[ [<tile-component idx>] <id> ...]",
      "Specifies which quantization type to use for specified "
      + "tile-component. The default type is either 'reversible' or "
      + "'expounded' depending on whether or not the '-lossless' option "
      + " is specified.\n"
      + "<tile-component idx> : see general note.\n"
      + "<id>: Supported quantization types specification are : "
      + "'reversible' "
      + "(no quantization), 'derived' (derived quantization step size) and "
      + "'expounded'.\n"
      + "Example: -Qtype reversible or -Qtype t2,4-8 c2 reversible t9 "
      + "derived.", null
    },
    {
      "Qstep", "[<tile-component idx>] <bnss> "
      + "[ [<tile-component idx>] <bnss> ...]",
      "This option specifies the base normalized quantization step "
      + "size (bnss) for tile-components. It is normalized to a "
      + "dynamic range of 1 in the image domain. This parameter is "
      + "ignored in reversible coding. The default value is '1/128'"
      + " (i.e. 0.0078125).",
      "0.0078125"
    },
    {
      "Qguard_bits", "[<tile-component idx>] <gb> "
      + "[ [<tile-component idx>] <gb> ...]",
      "The number of bits used for each tile-component in the quantizer"
      + " to avoid overflow (gb).", "2"
    },
  };
  /**
   * The source of wavelet transform coefficients
   */
  protected CBlkWTDataSrc src;

  /**
   * Initializes the source of wavelet transform coefficients.
   *
   * @param src The source of wavelet transform coefficients.
     *
   */
  public Quantizer(CBlkWTDataSrc src)
  {
    super(src);
    this.src = src;
  }

  /**
   * Returns the number of guard bits used by this quantizer in the given tile-component.
   *
   * @param t Tile index
   *
   * @param c Component index
   *
   * @return The number of guard bits
     *
   */
  public abstract int getNumGuardBits(int t, int c);

  /**
   * Returns true if the quantizer of given tile-component uses derived quantization step sizes.
   *
   * @param t Tile index
   *
   * @param c Component index
   *
   * @return True if derived quantization is used.
     *
   */
  public abstract boolean isDerived(int t, int c);

  /**
   * Calculates the parameters of the SubbandAn objects that depend on the Quantizer. The 'stepWMSE' field is calculated for each subband which is a leaf in the
   * tree rooted at 'sb', for the specified component. The subband tree 'sb' must be the one for the component 'n'.
   *
   * @param sb The root of the subband tree.
   *
   * @param n The component index.
   *
   * @see SubbandAn#stepWMSE
     *
   */
  protected abstract void calcSbParams(SubbandAn sb, int n);

  /**
   * Returns a reference to the subband tree structure representing the subband decomposition for the specified tile-component.
   *
   * <P>This method gets the subband tree from the source and then calculates the magnitude bits for each leaf using the method calcSbParams().
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
   * @see #calcSbParams
     *
   */
  public SubbandAn getAnSubbandTree(int t, int c)
  {
    SubbandAn sbba;

    // Ask for the wavelet tree of the source
    sbba = src.getAnSubbandTree(t, c);
    // Calculate the stepWMSE
    calcSbParams(sbba, c);
    return sbba;
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
   * Creates a Quantizer object for the appropriate type of quantization specified in the options in the parameter list 'pl', and having 'src' as the source of
   * data to be quantized. The 'rev' flag indicates if the quantization should be reversible.
   *
   * NOTE: At the moment only sources of wavelet data that implement the 'CBlkWTDataSrc' interface are supported.
   *
   * @param src The source of data to be quantized
   *
   * @param encSpec Encoder specifications
   *
   * @exception IllegalArgumentException If an error occurs while parsing the options in 'pl'
     *
   */
  public static Quantizer createInstance(CBlkWTDataSrc src,
    EncoderSpecs encSpec)
  {
    // Instantiate quantizer
    return new StdQuantizer(src, encSpec);
  }

  /**
   * Returns the maximum number of magnitude bits in any subband in the current tile.
   *
   * @param c the component number
   *
   * @return The maximum number of magnitude bits in all subbands of the current tile.
     *
   */
  public abstract int getMaxMagBits(int c);
}
