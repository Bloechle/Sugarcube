package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.extensions;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.JBIG2Exception;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Segment;

import java.io.IOException;

public class ExtensionSegment extends Segment {

	public ExtensionSegment(JBIG2StreamDecoder streamDecoder) {
		super(streamDecoder);
	}

	public void readSegment() throws IOException, JBIG2Exception {
		for (int i = 0; i < getSegmentHeader().getSegmentDataLength(); i++) {
			decoder.readByte();
		}
	}
}
