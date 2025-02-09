/* 
 * CVS identifier:
 * 
 * $Id: StdEntropyCoderOptions.java,v 1.10 2001/03/27 09:57:20 grosbois Exp $
 * 
 * Class:                   StdEntropyCoderOptions
 * 
 * Description:             Entropy coding engine of stripes in
 *                          code-blocks options
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.entropy;

/**
 * This interface define the constants that identify the possible options for the entropy coder, as well some fixed parameters of the JPEG 2000 entropy coder.
 *
 */
public interface StdEntropyCoderOptions
{
  /**
   * The flag bit to indicate that selective arithmetic coding bypass should be used. In this mode, the significance propagation and magnitude refinement passes
   * bypass the arithmetic encoder in the fourth bit-plane and latter ones (but not the cleanup pass). Note that the transition between raw and AC segments
   * needs terminations (whether or not OPT_TERM_PASS is used).
   */
  public final static int OPT_BYPASS = 1;
  /**
   * The flag bit to indicate that the MQ states for all contexts should be reset at the end of each (non-bypassed) coding pass.
   */
  public final static int OPT_RESET_MQ = 1 << 1;
  /**
   * The flag bit to indicate that a termination should be performed after each coding pass. Note that terminations are applied to both * * arithmetically coded
   * and bypassed (i.e. raw) passes .
   */
  public final static int OPT_TERM_PASS = 1 << 2;
  /**
   * The flag bit to indicate the vertically stripe-causal context formation should be used.
   */
  public final static int OPT_VERT_STR_CAUSAL = 1 << 3;
  /**
   * The flag bit to indicate that error resilience info is embedded on MQ termination. This corresponds to the predictable termination described in Annex D.4.2
   * of the FDIS
   */
  public final static int OPT_PRED_TERM = 1 << 4;
  /**
   * The flag bit to indicate that an error resilience segmentation symbol is to be inserted at the end of each cleanup coding pass. The segmentation symbol is
   * the four symbol sequence 1010 that are sent through the MQ coder using the UNIFORM context (as explained in annex D.5 of FDIS).
   */
  public final static int OPT_SEG_SYMBOLS = 1 << 5;
  /**
   * The minimum code-block dimension. The nominal width or height of a code-block must never be less than this. It is 4.
   */
  public static final int MIN_CB_DIM = 4;
  /**
   * The maximum code-block dimension. No code-block should be larger, either in width or height, than this value. It is 1024.
   */
  public static final int MAX_CB_DIM = 1024;
  /**
   * The maximum code-block area (width x height). The surface covered by a nominal size block should never be larger than this. It is 4096
   */
  public static final int MAX_CB_AREA = 4096;
  /**
   * The stripe height. This is the nominal value of the stripe height. It is 4.
   */
  public static final int STRIPE_HEIGHT = 4;
  /**
   * The number of coding passes per bit-plane. This is the number of passes per bit-plane. It is 3.
   */
  public static final int NUM_PASSES = 3;
  /**
   * The number of most significant bit-planes where bypass mode is not to be used, even if bypass mode is on: 4.
   */
  public static final int NUM_NON_BYPASS_MS_BP = 4;
  /**
   * The number of empty passes in the most significant bit-plane. It is 2.
   */
  public static final int NUM_EMPTY_PASSES_IN_MS_BP = 2;
  /**
   * The index of the first "raw" pass, if bypass mode is on.
   */
  public static final int FIRST_BYPASS_PASS_IDX =
    NUM_PASSES * NUM_NON_BYPASS_MS_BP - NUM_EMPTY_PASSES_IN_MS_BP;
}
