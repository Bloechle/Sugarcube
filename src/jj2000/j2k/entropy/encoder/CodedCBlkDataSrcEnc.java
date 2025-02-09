/* 
 * CVS identifier:
 * 
 * $Id: CodedCBlkDataSrcEnc.java,v 1.15 2001/09/14 09:23:22 grosbois Exp $
 * 
 * Class:                   CodedCBlkDataSrcEnc
 * 
 * Description:             Interface that defines a source of entropy coded
 *                          data that is transferred in a code-block by
 *                          code-block basis.
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
package jj2000.j2k.entropy.encoder;

import jj2000.j2k.wavelet.analysis.ForwWTDataProps;

/**
 * This interface defines a source of entropy coded data and methods to transfer it in a code-block by code-block basis. In each call to 'getNextCodeBlock()' a
 * new coded code-block is returned. The code-block are retruned in no specific-order.
 *
 * <p>This interface is the source of data for the rate allocator. See the 'PostCompRateAllocator' class.</p>
 *
 * <p>For each coded-code-block the entropy-coded data is returned along with the rate-distortion statistics in a 'CBlkRateDistStats' object.</p>
 *
 * @see PostCompRateAllocator
 * @see CBlkRateDistStats
 * @see EntropyCoder
 *
 */
public interface CodedCBlkDataSrcEnc extends ForwWTDataProps
{
  /**
   * Returns the next coded code-block in the current tile for the specified component, as a copy (see below). The order in which code-blocks are returned is
   * not specified. However each code-block is returned only once and all code-blocks will be returned if the method is called 'N' times, where 'N' is the
   * number of code-blocks in the tile. After all the code-blocks have been returned for the current tile calls to this method will return 'null'.
   *
   * <p>When changing the current tile (through 'setTile()' or 'nextTile()') this method will always return the first code-block, as if this method was never
   * called before for the new current tile.</p>
   *
   * <p>The data returned by this method is always a copy of the internal data of this object, if any, and it can be modified "in place" without any problems
   * after being returned.</p>
   *
   * @param c The component for which to return the next code-block.
   *
   * @param ccb If non-null this object might be used in returning the coded code-block in this or any subsequent call to this method. If null a new one is
   * created and returned. If the 'data' array of 'cbb' is not null it may be reused to return the compressed data.
   *
   * @return The next coded code-block in the current tile for component 'c', or null if all code-blocks for the current tile have been returned.
   *
   * @see CBlkRateDistStats
     *
   */
  public CBlkRateDistStats getNextCodeBlock(int c, CBlkRateDistStats ccb);

  /**
   * Returns the width of a packet for the specified tile-component and resolution level.
   *
   * @param t The tile
   *
   * @param c The component
   *
   * @param r The resolution level
   *
   * @return The width of a packet for the specified tile- component and resolution level.
     *
   */
  public int getPPX(int t, int c, int r);

  /**
   * Returns the height of a packet for the specified tile-component and resolution level.
   *
   * @param t The tile
   *
   * @param c The component
   *
   * @param r The resolution level
   *
   * @return The height of a packet for the specified tile- component and resolution level.
     *
   */
  public int getPPY(int t, int c, int r);

  /**
   * Returns true if the precinct partition is used for the specified component and tile, returns false otherwise
   *
   * @param c The component
   *
   * @param t The tile
     *
   */
  public boolean precinctPartitionUsed(int c, int t);
}
