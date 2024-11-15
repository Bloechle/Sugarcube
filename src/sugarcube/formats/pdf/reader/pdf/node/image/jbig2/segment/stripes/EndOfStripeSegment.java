package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.stripes;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.JBIG2Exception;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Segment;

import java.io.IOException;

public class EndOfStripeSegment extends Segment {

	public EndOfStripeSegment(JBIG2StreamDecoder streamDecoder) {
		super(streamDecoder);
	}

	public void readSegment() throws IOException, JBIG2Exception {
		for (int i = 0; i < this.getSegmentHeader().getSegmentDataLength(); i++) {
			decoder.readByte();
		}
	}
}
