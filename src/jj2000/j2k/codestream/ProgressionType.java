/*
 * CVS identifier:
 *
 * $Id: ProgressionType.java,v 1.16 2001/07/25 16:21:06 grosbois Exp $
 *
 * Class:                   ProgressionType
 *
 * Description:             The definition of the different bit stream
 *                          profiles.
 *
 *
 *
 * COPYRIGHT:
 * 
 * This software module was originally developed by Raphael Grosbois and
 * Diego Santa Cruz (Swiss Federal Institute of Technology-EPFL); Joel
 * Askelef (Ericsson Radio Systems AB); and Bertrand Berthelot, David
 * Bouchard, Felix Henry, Gerard Mozelle and Patrice Onno (Canon Research
 * Centre France S.A) in the course of development of the JPEG2000
 * standard as specified by ISO/IEC 15444 (JPEG 2000 Standard). This
 * software module is an implementation of a part of the JPEG 2000
 * Standard. Swiss Federal Institute of Technology-EPFL, Ericsson Radio
 * Systems AB and Canon Research Centre France S.A (collectively JJ2000
 * Partners) agree not to assert against ISO/IEC and users of the JPEG
 * 2000 Standard (Users) any of their rights under the copyright, not
 * including other intellectual property rights, for this software module
 * with respect to the usage by ISO/IEC and Users of this software module
 * or modifications thereof for use in hardware or software products
 * claiming conformance to the JPEG 2000 Standard. Those intending to use
 * this software module in hardware or software products are advised that
 * their use may infringe existing patents. The original developers of
 * this software module, JJ2000 Partners and ISO/IEC assume no liability
 * for use of this software module or modifications thereof. No license
 * or right to this software module is granted for non JPEG 2000 Standard
 * conforming products. JJ2000 Partners have full right to use this
 * software module for his/her own purpose, assign or donate this
 * software module to any third party and to inhibit third parties from
 * using this software module for non JPEG 2000 Standard conforming
 * products. This copyright notice must be included in all copies or
 * derivative works of this software module.
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.codestream;

/**
 * This interface defines the identifiers for the different codestream profiles and progression types.
 *
 * <p>Each progressive type has a different number: 'LY_RES_COMP_POS_PROG', 'RES_LY_COMP_POS_PROG', 'RES_POS_COMP_LY_PROG', 'POS_COMP_RES_LY_PROG' or
 * 'COMP_POS_RES_LY_PROG'. These are the same identifiers are used in the codestream syntax.
 *
 * <p>This interface defines the constants only. In order to use the constants in any other class you can either use the fully qualified name (e.g.,
 * <tt>ProgressionType.LY_RES_COMP_POS_PROG</tt>) or declare this interface in the implements clause of the class and then access the identifier directly.</p>
 *
 */
public interface ProgressionType
{
  /**
   * The codestream is Layer/Resolution/Component/Position progressive : 0
     *
   */
  public final static int LY_RES_COMP_POS_PROG = 0;
  /**
   * The codestream is Resolution/Layer/Component/Position progressive : 1
     *
   */
  public final static int RES_LY_COMP_POS_PROG = 1;
  /**
   * The codestream is Resolution/Position/Component/Layer progressive : 2
     *
   */
  public final static int RES_POS_COMP_LY_PROG = 2;
  /**
   * The codestream is Position/Component/Resolution/Layer progressive : 3
     *
   */
  public final static int POS_COMP_RES_LY_PROG = 3;
  /**
   * The codestream is Component/Position/Resolution/Layer progressive : 4
     *
   */
  public final static int COMP_POS_RES_LY_PROG = 4;
}
