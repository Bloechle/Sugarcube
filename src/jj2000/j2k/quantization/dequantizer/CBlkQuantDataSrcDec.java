/* 
 * CVS identifier:
 * 
 * $Id: CBlkQuantDataSrcDec.java,v 1.9 2001/09/14 08:58:36 grosbois Exp $
 * 
 * Class:                   CBlkQuantDataSrcDec
 * 
 * Description:             Interface that defines a source of
 *                          quantized wavelet data to be transferred in a
 *                          code-block by code-block basis (decoder side).
 * 
 * 
 * 
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.quantization.dequantizer;

import jj2000.j2k.entropy.decoder.EntropyDecoder;
import jj2000.j2k.image.DataBlk;
import jj2000.j2k.wavelet.synthesis.CBlkWTDataSrcDec;
import jj2000.j2k.wavelet.synthesis.InvWTData;
import jj2000.j2k.wavelet.synthesis.SubbandSyn;

/**
 * This interface defines a source of quantized wavelet coefficients and methods to transfer them in a code-block by code-block basis, fro the decoder side. In
 * each call to 'getCodeBlock()' or 'getInternCodeBlock()' a new code-block is returned.
 *
 * <P>This class is the source of data for the dequantizer. See the 'Dequantizer' class.
 *
 * <P>Code-block data is returned in sign-magnitude representation, instead of the normal two's complement one. Only integral types are used. The sign magnitude
 * representation is more adequate for entropy coding. In sign magnitude representation, the most significant bit is used for the sign (0 if positive, 1 if
 * negative) and the magnitude of the coefficient is stored in the next M most significant bits. The rest of the bits (least significant bits) can contain a
 * fractional value of the quantized coefficient. The number 'M' of magnitude bits is communicated in the 'magbits' member variable of the 'CBlkWTData'.
 *
 * @see InvWTData
 * @see CBlkWTDataSrcDec
 * @see Dequantizer
 * @see EntropyDecoder
 *
 */
public interface CBlkQuantDataSrcDec extends InvWTData
{
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
   * @return The next code-block in the current tile for component 'n', or null if all code-blocks for the current tile have been returned.
   *
   * @see DataBlk
     *
   */
  public DataBlk getCodeBlock(int c, int m, int n, SubbandSyn sb, DataBlk cblk);

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
   * @return The next code-block in the current tile for component 'n', or null if all code-blocks for the current tile have been returned.
   *
   * @see DataBlk
     *
   */
  public DataBlk getInternCodeBlock(int c, int m, int n, SubbandSyn sb,
    DataBlk cblk);
}
