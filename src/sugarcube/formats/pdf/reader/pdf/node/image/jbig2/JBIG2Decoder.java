package sugarcube.formats.pdf.reader.pdf.node.image.jbig2;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.image.JBIG2Bitmap;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Segment;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.pageinformation.PageInformationSegment;

import java.awt.image.*;
import java.io.*;
import java.util.Iterator;
import java.util.List;

public class JBIG2Decoder {
	
	private JBIG2StreamDecoder streamDecoder;

	/**
	 * Constructor for jbig
	 */
	public JBIG2Decoder() {
		streamDecoder = new JBIG2StreamDecoder();
	}

	/**
	 * If the data stream is taken from a PDF, there may be some global data. Pass any global data
	 * in here.  Call this method before decodeJBIG2(...)
	 * @param data global data
	 * @throws IOException
	 * @throws JBIG2Exception
	 */
	public void setGlobalData(byte[] data) throws IOException, JBIG2Exception {
		streamDecoder.setGlobalData(data);
	}

	/**
	 * Decodes a JBIG2 image from a File object
	 * @param file File to decode
	 * @throws IOException
	 * @throws JBIG2Exception
	 */
	public void decodeJBIG2(File file) throws IOException, JBIG2Exception {
		decodeJBIG2(file.getAbsolutePath());
	}

	/**
	 * Decodes a JBIG2 image from a String path
	 * @param file Must be the full path to the image
	 * @throws IOException
	 * @throws JBIG2Exception
	 */
	public void decodeJBIG2(String file) throws IOException, JBIG2Exception {
		decodeJBIG2(new FileInputStream(file));
	}

	/**
	 * Decodes a JBIG2 image from an InputStream
	 * @param inputStream InputStream
	 * @throws IOException
	 * @throws JBIG2Exception
	 */
	public void decodeJBIG2(InputStream inputStream) throws IOException, JBIG2Exception {
		int availiable = inputStream.available();

		byte[] bytes = new byte[availiable];
		inputStream.read(bytes);

		decodeJBIG2(bytes);
	}
	
	/**
	 * Decodes a JBIG2 image from a DataInput
	 * @param dataInput DataInput
	 * @throws IOException
	 * @throws JBIG2Exception
	 */
	public void decodeJBIG2(DataInput dataInput) throws IOException, JBIG2Exception {
//		long availiable = inputStream.length();
//
//		byte[] bytes = new byte[availiable];
//		inputStream.read(bytes);
//
//		decodeJBIG2(bytes);
	}

	/**
	 * Decodes a JBIG2 image from a byte array 
	 * @param data the raw data stream
	 * @throws IOException
	 * @throws JBIG2Exception
	 */
	public void decodeJBIG2(byte[] data) throws IOException, JBIG2Exception {
		streamDecoder.decodeJBIG2(data);
	}

	/**
	 * 
	 * @param page
	 * @return
	 */
	public BufferedImage getPageAsBufferedImage(int page) {
		page++;
		JBIG2Bitmap pageBitmap = streamDecoder.findPageSegement(page).getPageBitmap();

		byte[] bytes = pageBitmap.getData(true);

		if (bytes == null)
			return null;

		// make a a DEEP copy so we cant alter
		int len = bytes.length;
		byte[] copy = new byte[len];
		System.arraycopy(bytes, 0, copy, 0, len);

		// byte[] data = pageBitmap.getData(true).clone();
		int width = pageBitmap.getWidth();
		int height = pageBitmap.getHeight();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		// <start-me>
		/** create an image from the raw data */
		DataBuffer db = new DataBufferByte(copy, copy.length);
		WritableRaster raster = Raster.createPackedRaster(db, width, height, 1, null);
		/* <end-me>
        WritableRaster raster = image.getRaster();
        int[] intrgb = new int[copy.length];
        for(int i=0;i<copy.length;i++){
            intrgb[i] = (int)copy[i];
        }
        raster.setPixels(0, 0, width, height, intrgb);
        /**/
		image.setData(raster);


		return image;
	}

	public boolean isNumberOfPagesKnown() {
		return streamDecoder.isNumberOfPagesKnown();
	}

	public int getNumberOfPages() {
		int pages = streamDecoder.getNumberOfPages();
		if (streamDecoder.isNumberOfPagesKnown() && pages != 0)
			return pages;

		int noOfPages = 0;

		List segments = getAllSegments();
		for (Iterator it = segments.iterator(); it.hasNext();) {
			Segment segment = (Segment) it.next();
			if (segment.getSegmentHeader().getSegmentType() == Segment.PAGE_INFORMATION)
				noOfPages++;
		}

		return noOfPages;
	}

	public List getAllSegments() {
		return streamDecoder.getAllSegments();
	}

	public PageInformationSegment findPageSegement(int page) {
		page++;
		return streamDecoder.findPageSegement(page);
	}

	public Segment findSegment(int segmentNumber) {
		return streamDecoder.findSegment(segmentNumber);
	}

	public JBIG2Bitmap getPageAsJBIG2Bitmap(int page) {
		page++;
		return streamDecoder.findPageSegement(page).getPageBitmap();
	}

	public boolean isRandomAccessOrganisationUsed() {
		return streamDecoder.isRandomAccessOrganisationUsed();
	}
}
