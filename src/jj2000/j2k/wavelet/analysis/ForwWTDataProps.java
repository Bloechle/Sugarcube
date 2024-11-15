/* 
 * CVS identifier:
 * 
 * $Id: ForwWTDataProps.java,v 1.10 2001/09/20 12:42:42 grosbois Exp $
 * 
 * Class:                   ForwWTDataProps
 * 
 * Description:             Extends ImgData with forward wavelet specific
 *                          things.
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.image.ImgData;
import jj2000.j2k.wavelet.Subband;

/**
 * This interface extends the ImgData interface with methods that are necessary for forward wavelet data (i.e. data that is produced by a forward wavelet
 * transform).
 */
public interface ForwWTDataProps extends ImgData
{
  /**
   * Returns the reversibility of the given tile-component. Data is reversible when it is suitable for lossless and lossy-to-lossless compression.
   *
   * @param t Tile index
   *
   * @param c Component index
   *
   * @return true is the data is reversible, false if not.
     *
   */
  public boolean isReversible(int t, int c);

  /**
   * Returns a reference to the root of subband tree structure representing the subband decomposition for the specified tile-component.
   *
   * @param t The index of the tile.
   *
   * @param c The index of the component.
   *
   * @return The root of the subband tree structure, see Subband.
   *
   * @see SubbandAn
   *
   * @see Subband
     *
   */
  public SubbandAn getAnSubbandTree(int t, int c);

  /**
   * Returns the horizontal offset of the code-block partition. Allowable values are 0 and 1, nothing else.
     *
   */
  public int getCbULX();

  /**
   * Returns the vertical offset of the code-block partition. Allowable values are 0 and 1, nothing else.
     *
   */
  public int getCbULY();
}
