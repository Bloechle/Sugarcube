/*
 * $RCSfile: TIFFPackBitsCompressor.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.1 $
 * $Date: 2005-02-11 05:01:49 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFCompressor;

import java.io.IOException;

/**
 *
 */
public class TIFFPackBitsCompressor extends TIFFCompressor {

    public TIFFPackBitsCompressor() {
        super("PackBits", BaselineTIFFTagSet.COMPRESSION_PACKBITS, true);
    }

    /**
     * Performs PackBits compression for a single buffer of data.
     * This should be called for each row of each tile. The returned
     * value is the offset into the output buffer after compression.
     */
    private static int packBits(byte[] input, int inOffset, int inCount,
                                byte[] output, int outOffset) {
        int inMax = inOffset + inCount - 1;
        int inMaxMinus1 = inMax - 1;

        while(inOffset <= inMax) {
            int run = 1;
            byte replicate = input[inOffset];
            while(run < 127 && inOffset < inMax &&
                  input[inOffset] == input[inOffset+1]) {
                run++;
                inOffset++;
            }
            if(run > 1) {
                inOffset++;
                output[outOffset++] = (byte)(-(run - 1));
                output[outOffset++] = replicate;
            }

            run = 0;
            int saveOffset = outOffset;
            while(run < 128 &&
                  ((inOffset < inMax &&
                    input[inOffset] != input[inOffset+1]) ||
                   (inOffset < inMaxMinus1 &&
                    input[inOffset] != input[inOffset+2]))) {
                run++;
                output[++outOffset] = input[inOffset++];
            }
            if(run > 0) {
                output[saveOffset] = (byte)(run - 1);
                outOffset++;
            }

            if(inOffset == inMax) {
                if(run > 0 && run < 128) {
                    output[saveOffset]++;
                    output[outOffset++] = input[inOffset++];
                } else {
                    output[outOffset++] = (byte)0;
                    output[outOffset++] = input[inOffset++];
                }
            }
        }

        return outOffset;
    }

    public int encode(byte[] b, int off,
                      int width, int height,
                      int[] bitsPerSample,
                      int scanlineStride) throws IOException {
        int bitsPerPixel = 0;
        for (int i = 0; i < bitsPerSample.length; i++) {
            bitsPerPixel += bitsPerSample[i];
        }
        int bytesPerRow = (bitsPerPixel*width + 7)/8;
        int bufSize = (int)(bytesPerRow + (bytesPerRow + 127)/128);
        byte[] compData = new byte[bufSize];

        int bytesWritten = 0;

        for(int i = 0; i < height; i++) {
            int bytes = packBits(b, off, scanlineStride, compData, 0);
            off += scanlineStride;
            bytesWritten += bytes;
            stream.write(compData, 0, bytes);
        }

        return bytesWritten;
    }
}
