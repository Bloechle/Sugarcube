package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region;


import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.JBIG2Exception;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Segment;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.util.BinaryOperation;

import java.io.IOException;

public abstract class RegionSegment extends Segment {
	protected int regionBitmapWidth, regionBitmapHeight;
	protected int regionBitmapXLocation, regionBitmapYLocation;

	protected RegionFlags regionFlags = new RegionFlags();

	public RegionSegment(JBIG2StreamDecoder streamDecoder) {
		super(streamDecoder);
	}

	public void readSegment() throws IOException, JBIG2Exception {
		short[] buff = new short[4];
		decoder.readByte(buff);
		regionBitmapWidth = BinaryOperation.getInt32(buff);

		buff = new short[4];
		decoder.readByte(buff);
		regionBitmapHeight = BinaryOperation.getInt32(buff);

		if (JBIG2StreamDecoder.debug)
			System.out.println("Bitmap size = " + regionBitmapWidth + 'x' + regionBitmapHeight);

		buff = new short[4];
		decoder.readByte(buff);
		regionBitmapXLocation = BinaryOperation.getInt32(buff);

		buff = new short[4];
		decoder.readByte(buff);
		regionBitmapYLocation = BinaryOperation.getInt32(buff);

		if (JBIG2StreamDecoder.debug)
			System.out.println("Bitmap location = " + regionBitmapXLocation + ',' + regionBitmapYLocation);

		/** extract region Segment flags */
		short regionFlagsField = decoder.readByte();

		regionFlags.setFlags(regionFlagsField);

		if (JBIG2StreamDecoder.debug)
			System.out.println("region Segment flags = " + regionFlagsField);
	}
}
