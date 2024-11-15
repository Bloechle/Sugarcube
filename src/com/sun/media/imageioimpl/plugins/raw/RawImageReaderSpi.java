/*
 * $RCSfile: RawImageReaderSpi.java,v $
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
 * $Revision: 1.2 $
 * $Date: 2006-03-31 19:43:40 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.raw;

import com.sun.media.imageio.stream.RawImageInputStream;
import com.sun.media.imageioimpl.common.PackageUtil;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import java.io.IOException;
import java.util.Locale;

public class RawImageReaderSpi extends ImageReaderSpi {

    private static String [] writerSpiNames =
        {"com.sun.media.imageioimpl.plugins.raw.RawImageWriterSpi"};
    private static String[] formatNames = {"raw", "RAW"};
    private static String[] entensions = {""};
    private static String[] mimeType = {""};

    private boolean registered = false;

    public RawImageReaderSpi() {
        super(PackageUtil.getVendor(),
              PackageUtil.getVersion(),
              formatNames,
              entensions,
              mimeType,
              "com.sun.media.imageioimpl.plugins.raw.RawImageReader",
              STANDARD_INPUT_TYPE,
              writerSpiNames,
              true,
              null, null, null, null,
              true,
              null, null, null, null);
    }

    public void onRegistration(ServiceRegistry registry,
                               Class category) {
        if (registered) {
            return;
        }
        registered = true;
    }

    public String getDescription(Locale locale) {
	String desc = PackageUtil.getSpecificationTitle() + 
	    " Raw Image Reader";
	return desc;
    }

    public boolean canDecodeInput(Object source) throws IOException {
        if (!(source instanceof RawImageInputStream)) {
            return false;
        }

        return true;
    }

    public ImageReader createReaderInstance(Object extension)
        throws IIOException {
        return new RawImageReader(this);
    }
}
