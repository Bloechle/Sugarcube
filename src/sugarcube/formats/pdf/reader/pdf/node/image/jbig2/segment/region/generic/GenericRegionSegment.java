package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region.generic;


import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.JBIG2Exception;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.image.JBIG2Bitmap;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.pageinformation.PageInformationFlags;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.pageinformation.PageInformationSegment;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region.RegionFlags;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region.RegionSegment;

import java.io.IOException;

public class GenericRegionSegment extends RegionSegment {
    private GenericRegionFlags genericRegionFlags = new GenericRegionFlags();

    private boolean inlineImage;

    private boolean unknownLength = false;
    
    public GenericRegionSegment(JBIG2StreamDecoder streamDecoder, boolean inlineImage) {
        super(streamDecoder);

        this.inlineImage = inlineImage;
    }

    public void readSegment() throws IOException, JBIG2Exception {
    	
    	if(JBIG2StreamDecoder.debug)
    		System.out.println("==== Reading Immediate Generic Region ====");
    	
    	super.readSegment();
        
        /** read text region Segment flags */
        readGenericRegionFlags();

        boolean useMMR = genericRegionFlags.getFlagValue(GenericRegionFlags.MMR) != 0;
        int template = genericRegionFlags.getFlagValue(GenericRegionFlags.GB_TEMPLATE);
        
        short[] genericBAdaptiveTemplateX = new short[4];
    	short[] genericBAdaptiveTemplateY = new short[4];
        
        if (!useMMR) {
        	if (template == 0) {
        		genericBAdaptiveTemplateX[0] = readATValue();
        		genericBAdaptiveTemplateY[0] = readATValue();
        		genericBAdaptiveTemplateX[1] = readATValue();
        		genericBAdaptiveTemplateY[1] = readATValue();
        		genericBAdaptiveTemplateX[2] = readATValue();
        		genericBAdaptiveTemplateY[2] = readATValue();
        		genericBAdaptiveTemplateX[3] = readATValue();
        		genericBAdaptiveTemplateY[3] = readATValue();
        	} else {
        		genericBAdaptiveTemplateX[0] = readATValue();
        		genericBAdaptiveTemplateY[0] = readATValue();
        	}
        	
        	arithmeticDecoder.resetGenericStats(template, null);
        	arithmeticDecoder.start();
        }
        
        boolean typicalPredictionGenericDecodingOn = genericRegionFlags.getFlagValue(GenericRegionFlags.TPGDON) != 0;
        int length = segmentHeader.getSegmentDataLength();

        if(length == -1) { 
        	/** 
        	 * length of data is unknown, so it needs to be determined through examination of the data.
        	 * See 7.2.7 - Segment data length of the JBIG2 specification.
        	 */
        	
        	unknownLength = true;
        	
        	short match1;
        	short match2;
        	
        	if(useMMR) {
        		// look for 0x00 0x00 (0, 0)
        		
        		match1 = 0;
        		match2 = 0;
        	} else {
        		// look for 0xFF 0xAC (255, 172)
        		
        		match1 = 255;
        		match2 = 172;
        	}
        	
        	int bytesRead = 0;
    		while(true) {
    			short bite1 = decoder.readByte();
    			bytesRead++;
    			
    			if(bite1 == match1){
    				short bite2 = decoder.readByte();
    				bytesRead++;
    				
    				if(bite2 == match2){
    					length = bytesRead - 2;
    					break;
    				}
    			}
    		}
    		
    		decoder.movePointer(-bytesRead);
        }
        
        JBIG2Bitmap bitmap = new JBIG2Bitmap(regionBitmapWidth, regionBitmapHeight, arithmeticDecoder, huffmanDecoder, mmrDecoder);
        bitmap.clear(0);
        bitmap.readBitmap(useMMR, template, typicalPredictionGenericDecodingOn, false, null, genericBAdaptiveTemplateX, genericBAdaptiveTemplateY, useMMR ? 0 : length - 18);
        
        
        
        if (inlineImage) {
            PageInformationSegment pageSegment = decoder.findPageSegement(segmentHeader.getPageAssociation());
            JBIG2Bitmap pageBitmap = pageSegment.getPageBitmap();

            int extCombOp = regionFlags.getFlagValue(RegionFlags.EXTERNAL_COMBINATION_OPERATOR);
            
            if(pageSegment.getPageBitmapHeight() == -1 && regionBitmapYLocation + regionBitmapHeight > pageBitmap.getHeight()) {
            	pageBitmap.expand(regionBitmapYLocation + regionBitmapHeight, 
            			pageSegment.getPageInformationFlags().getFlagValue(PageInformationFlags.DEFAULT_PIXEL_VALUE));
            }
            
            pageBitmap.combine(bitmap, regionBitmapXLocation, regionBitmapYLocation, extCombOp);
        } else {
			bitmap.setBitmapNumber(getSegmentHeader().getSegmentNumber());
			decoder.appendBitmap(bitmap);
		}

        
        if(unknownLength) {
        	decoder.movePointer(4);
        }
        
    }

    private void readGenericRegionFlags() throws IOException {
        /** extract text region Segment flags */
        short genericRegionFlagsField = decoder.readByte();

        genericRegionFlags.setFlags(genericRegionFlagsField);
        
        if(JBIG2StreamDecoder.debug)
        	System.out.println("generic region Segment flags = " + genericRegionFlagsField);
    }

    public GenericRegionFlags getGenericRegionFlags() {
        return genericRegionFlags;
    }
}
