/*
 * $RCSfile: ChannelImageInputStreamSpi.java,v $
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
 * $Date: 2005-02-11 05:01:53 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.stream;

import com.sun.media.imageio.stream.FileChannelImageInputStream;
import com.sun.media.imageioimpl.common.PackageUtil;

import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;

public class ChannelImageInputStreamSpi extends ImageInputStreamSpi {
    public ChannelImageInputStreamSpi() {
        super(PackageUtil.getVendor(),
              PackageUtil.getVersion(),
	      ReadableByteChannel.class);
    }

    public ImageInputStream createInputStreamInstance(Object input,
						      boolean useCache,
						      File cacheDir)
	throws IOException {

	if(input == null ||
	   !(input instanceof ReadableByteChannel)) {
	    throw new IllegalArgumentException("XXX");
	}

	ImageInputStream stream = null;

	if(input instanceof FileChannel) {
	    stream = new FileChannelImageInputStream((FileChannel)input);
	} else {
	    InputStream inStream =
		Channels.newInputStream((ReadableByteChannel)input);

	    if(useCache) {
		try {
		    stream = new FileCacheImageInputStream(inStream,
							   cacheDir);
		} catch(IOException e) {
		    // Cache file could not be created.
		}
	    }

	    if(stream == null) {
		stream = new MemoryCacheImageInputStream(inStream);
	    }
	}

	return stream;
    }

    public String getDescription(Locale locale) {
	return "NIO Channel ImageInputStream";
    }
}
