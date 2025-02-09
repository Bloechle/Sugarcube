/*
 * $RCSfile: TIFFDeflateDecompressor.java,v $
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
 * $Date: 2005-02-11 05:01:45 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFDecompressor;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class TIFFDeflateDecompressor extends TIFFDecompressor {

    private static final boolean DEBUG = false;

    Inflater inflater = null;
    int predictor;

    public TIFFDeflateDecompressor(int predictor) throws IIOException {
        inflater = new Inflater();

        if (predictor != BaselineTIFFTagSet.PREDICTOR_NONE &&
            predictor != 
            BaselineTIFFTagSet.PREDICTOR_HORIZONTAL_DIFFERENCING) {
            throw new IIOException("Illegal value for Predictor in " +
                                   "TIFF file");
        }

        if(DEBUG) {
            System.out.println("Using horizontal differencing predictor");
        }

        this.predictor = predictor;
    }

    public synchronized void decodeRaw(byte[] b,
                                       int dstOffset,
                                       int bitsPerPixel,
                                       int scanlineStride) throws IOException {

        // Check bitsPerSample.
        if (predictor == 
            BaselineTIFFTagSet.PREDICTOR_HORIZONTAL_DIFFERENCING) {
            int len = bitsPerSample.length;
            for(int i = 0; i < len; i++) {
                if(bitsPerSample[i] != 8) {
                    throw new IIOException
                        (bitsPerSample[i] + "-bit samples "+
                         "are not supported for Horizontal "+
                         "differencing Predictor");
                }
            }
        }

        // Seek to current tile data offset.
        stream.seek(offset);

        // Read the deflated data.
        byte[] srcData = new byte[byteCount];
        stream.readFully(srcData);

        int bytesPerRow = (srcWidth*bitsPerPixel + 7)/8;
        byte[] buf;
        int bufOffset;
        if(bytesPerRow == scanlineStride) {
            buf = b;
            bufOffset = dstOffset;
        } else {
            buf = new byte[bytesPerRow*srcHeight];
            bufOffset = 0;
        }

        // Set the input to the Inflater.
        inflater.setInput(srcData);

        // Inflate the data.
        try {
            inflater.inflate(buf, bufOffset, bytesPerRow*srcHeight);
        } catch(DataFormatException dfe) {
            throw new IIOException(I18N.getString("TIFFDeflateDecompressor0"),
                                   dfe);
        }

        // Reset the Inflater.
        inflater.reset();

	if (predictor ==
            BaselineTIFFTagSet.PREDICTOR_HORIZONTAL_DIFFERENCING) {
	    
	    for (int j = 0; j < srcHeight; j++) {
		int count = bufOffset + samplesPerPixel * (j * srcWidth + 1);
		for (int i=samplesPerPixel; i<srcWidth*samplesPerPixel; i++) {
		    buf[count] += buf[count - samplesPerPixel];
		    count++;
		}
	    }
	}

        if(bytesPerRow != scanlineStride) {
            if(DEBUG) {
                System.out.println("bytesPerRow != scanlineStride");
            }
            int off = 0;
            for (int y = 0; y < srcHeight; y++) {
                System.arraycopy(buf, off, b, dstOffset, bytesPerRow);
                off += bytesPerRow;
                dstOffset += scanlineStride;
            }
        }
    }
}
