package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.pageinformation;


import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.JBIG2Exception;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.image.JBIG2Bitmap;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Segment;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.util.BinaryOperation;

import java.io.IOException;

public class PageInformationSegment extends Segment {

	private int pageBitmapHeight, pageBitmapWidth;
	private int yResolution, xResolution;

	PageInformationFlags pageInformationFlags = new PageInformationFlags();
	private int pageStriping;

	private JBIG2Bitmap pageBitmap;

	public PageInformationSegment(JBIG2StreamDecoder streamDecoder) {
		super(streamDecoder);
	}

	public PageInformationFlags getPageInformationFlags() {
		return pageInformationFlags;
	}

	public JBIG2Bitmap getPageBitmap() {
		return pageBitmap;
	}

	public void readSegment() throws IOException, JBIG2Exception {

		if (JBIG2StreamDecoder.debug)
			System.out.println("==== Reading Page Information Dictionary ====");

		short[] buff = new short[4];
		decoder.readByte(buff);
		pageBitmapWidth = BinaryOperation.getInt32(buff);

		buff = new short[4];
		decoder.readByte(buff);
		pageBitmapHeight = BinaryOperation.getInt32(buff);

		if (JBIG2StreamDecoder.debug)
			System.out.println("Bitmap size = " + pageBitmapWidth + 'x' + pageBitmapHeight);

		buff = new short[4];
		decoder.readByte(buff);
		xResolution = BinaryOperation.getInt32(buff);

		buff = new short[4];
		decoder.readByte(buff);
		yResolution = BinaryOperation.getInt32(buff);

		if (JBIG2StreamDecoder.debug)
			System.out.println("Resolution = " + xResolution + 'x' + yResolution);

		/** extract page information flags */
		short pageInformationFlagsField = decoder.readByte();

		pageInformationFlags.setFlags(pageInformationFlagsField);

		if (JBIG2StreamDecoder.debug)
			System.out.println("symbolDictionaryFlags = " + pageInformationFlagsField);

		buff = new short[2];
		decoder.readByte(buff);
		pageStriping = BinaryOperation.getInt16(buff);

		if (JBIG2StreamDecoder.debug)
			System.out.println("Page Striping = " + pageStriping);

		int defPix = pageInformationFlags.getFlagValue(PageInformationFlags.DEFAULT_PIXEL_VALUE);

		int height;

		if (pageBitmapHeight == -1) {
			height = pageStriping & 0x7fff;
		} else {
			height = pageBitmapHeight;
		}

		pageBitmap = new JBIG2Bitmap(pageBitmapWidth, height, arithmeticDecoder, huffmanDecoder, mmrDecoder);
		pageBitmap.clear(defPix);
	}

	public int getPageBitmapHeight() {
		return pageBitmapHeight;
	}
}
