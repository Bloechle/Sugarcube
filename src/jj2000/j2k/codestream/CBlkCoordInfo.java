/*
 * CVS identifier:
 *
 * $Id: CBlkCoordInfo.java,v 1.9 2001/09/14 09:32:53 grosbois Exp $
 *
 * Class:                   CBlkCoordInfo
 *
 * Description:             Used to store the code-blocks coordinates.
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

import jj2000.j2k.image.Coord;

/**
 * This class is used to store the coordinates of code-blocks.
 *
 */
public class CBlkCoordInfo extends CoordInfo
{
  /**
   * The code-block horizontal and vertical indexes
   */
  public Coord idx;

  /**
   * Constructor. Creates a CBlkCoordInfo object.
     *
   */
  public CBlkCoordInfo()
  {
    this.idx = new Coord();
  }

  /**
   * Constructor. Creates a CBlkCoordInfo object width specified code-block vertical and horizontal indexes.
   *
   * @param m Code-block vertical index.
   *
   * @param n Code-block horizontal index.
     *
   */
  public CBlkCoordInfo(int m, int n)
  {
    this.idx = new Coord(n, m);
  }

  /**
   * Returns code-block's information in a String
   *
   * @return String with code-block's information
     *
   */
  public String toString()
  {
    return super.toString() + ",idx=" + idx;
  }
}
