/*
 * CVS identifier:
 *
 * $Id: PrecInfo.java,v 1.2 2001/09/20 10:03:45 grosbois Exp $
 *
 * Class:                   PrecInfo
 *
 * Description:             Keeps information about a precinct
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
 * Class that holds precinct coordinates and references to contained code-blocks in each subband. 
 *
 */
public class PrecInfo
{
  /**
   * Precinct horizontal upper-left coordinate in the reference grid
   */
  public int rgulx;
  /**
   * Precinct vertical upper-left coordinate in the reference grid
   */
  public int rguly;
  /**
   * Precinct width reported in the reference grid
   */
  public int rgw;
  /**
   * Precinct height reported in the reference grid
   */
  public int rgh;
  /**
   * Precinct horizontal upper-left coordinate in the corresponding resolution level
   */
  public int ulx;
  /**
   * Precinct vertical upper-left coordinate in the corresponding resolution level
   */
  public int uly;
  /**
   * Precinct width in the corresponding resolution level
   */
  public int w;
  /**
   * Precinct height in the corresponding resolution level
   */
  public int h;
  /**
   * Resolution level index
   */
  public int r;
  /**
   * Code-blocks belonging to this precinct in each subbands of the resolution level
   */
  public CBlkCoordInfo[][][] cblk;
  /**
   * Number of code-blocks in each subband belonging to this precinct
   */
  public int[] nblk;

  /**
   * Class constructor.
   *
   * @param r Resolution level index.
   * @param ulx Precinct horizontal offset.
   * @param uly Precinct vertical offset.
   * @param w Precinct width.
   * @param h Precinct height.
   * @param rgulx Precinct horizontal offset in the image reference grid.
   * @param rguly Precinct horizontal offset in the image reference grid.
   * @param rgw Precinct width in the reference grid.
   * @param rgh Precinct height in the reference grid.
     *
   */
  public PrecInfo(int r, int ulx, int uly, int w, int h, int rgulx, int rguly,
    int rgw, int rgh)
  {
    this.r = r;
    this.ulx = ulx;
    this.uly = uly;
    this.w = w;
    this.h = h;
    this.rgulx = rgulx;
    this.rguly = rguly;
    this.rgw = rgw;
    this.rgh = rgh;

    if (r == 0)
    {
      cblk = new CBlkCoordInfo[1][][];
      nblk = new int[1];
    }
    else
    {
      cblk = new CBlkCoordInfo[4][][];
      nblk = new int[4];
    }
  }

  /**
   * Returns PrecInfo object information in a String
   *
   * @return PrecInfo information 
     *
   */
  public String toString()
  {
    return "ulx=" + ulx + ",uly=" + uly + ",w=" + w + ",h=" + h + ",rgulx=" + rgulx
      + ",rguly=" + rguly + ",rgw=" + rgw + ",rgh=" + rgh;
  }
}
