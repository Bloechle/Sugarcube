/* 
 * CVS identifier:
 * 
 * $Id: CodedCBlkDataSrcDec.java,v 1.17 2001/09/14 09:26:23 grosbois Exp $
 * 
 * Class:                   CodedCBlkDataSrcDec
 * 
 * Description:             Interface that defines a source of entropy coded
 *                          data that is transferred in a code-block by
 *                          code-block basis (decoder side).
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 *  */
package jj2000.j2k.entropy.decoder;

import jj2000.j2k.wavelet.synthesis.InvWTData;
import jj2000.j2k.wavelet.synthesis.SubbandSyn;

/**
 * This interface defines a source of entropy coded data and methods to transfer it in a code-block by code-block basis. In each call to 'geCodeBlock()' a
 * specified coded code-block is returned.
 *
 * <p>This interface is the source of data for the entropy decoder. See the 'EntropyDecoder' class.</p>
 *
 * <p>For each coded-code-block the entropy-coded data is returned along with its truncation point information in a 'DecLyrdCBlk' object.</p>
 *
 * @see EntropyDecoder
 *
 * @see DecLyrdCBlk
 *
 * @see jj2000.j2k.codestream.reader.BitstreamReaderAgent
 *
 */
public interface CodedCBlkDataSrcDec extends InvWTData
{
  /**
   * Returns the specified coded code-block, for the specified component, in the current tile. The first layer to return is indicated by 'fl'. The number of
   * layers that is returned depends on 'nl' and the amount of data available.
   *
   * <p>The argument 'fl' is to be used by subsequent calls to this method for the same code-block. In this way supplamental data can be retrieved at a later
   * time. The fact that data from more than one layer can be returned means that several packets from the same code-block, of the same component, and the same
   * tile, have been concatenated.</p>
   *
   * <p>The returned compressed code-block can have its progressive attribute set. If this attribute is set it means that more data can be obtained by
   * subsequent calls to this method (subject to transmission delays, etc). If the progressive attribute is not set it means that the returned data is all the
   * data that can be obtained for the specified subblock.</p>
   *
   * <p>The compressed code-block is uniquely specified by the current tile, the component (identified by 'c'), the subband (indentified by 'sb') and the
   * code-bock vertical and horizontal indexes 'm' and 'n'.</p>
   *
   * <p>The 'ulx' and 'uly' members of the returned 'DecLyrdCBlk' object contain the coordinates of the top-left corner of the block, with respect to the tile,
   * not the subband.</p>
   *
   * @param c The index of the component, from 0 to N-1.
   *
   * @param m The vertical index of the code-block to return, in the specified subband.
   *
   * @param n The horizontal index of the code-block to return, in the specified subband.
   *
   * @param sb The subband in whic the requested code-block is.
   *
   * @param fl The first layer to return.
   *
   * @param nl The number of layers to return, if negative all available layers are returned, starting at 'fl'.
   *
   * @param ccb If not null this object is used to return the compressed code-block. If null a new object is created and returned. If the data array in ccb is
   * not null then it can be reused to return the compressed data.
   *
   * @return The compressed code-block, with a certain number of layers determined by the available data and 'nl'.
     *
   */
  public DecLyrdCBlk getCodeBlock(int c, int m, int n,
    SubbandSyn sb, int fl, int nl,
    DecLyrdCBlk ccb);
}
