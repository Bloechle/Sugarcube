package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.JBIG2Exception;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.ArithmeticDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.HuffmanDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.MMRDecoder;

import java.io.IOException;

public abstract class Segment {

	public static final int SYMBOL_DICTIONARY = 0;
	public static final int INTERMEDIATE_TEXT_REGION = 4;
	public static final int IMMEDIATE_TEXT_REGION = 6;
	public static final int IMMEDIATE_LOSSLESS_TEXT_REGION = 7;
	public static final int PATTERN_DICTIONARY = 16;
	public static final int INTERMEDIATE_HALFTONE_REGION = 20;
	public static final int IMMEDIATE_HALFTONE_REGION = 22;
	public static final int IMMEDIATE_LOSSLESS_HALFTONE_REGION = 23;
	public static final int INTERMEDIATE_GENERIC_REGION = 36;
	public static final int IMMEDIATE_GENERIC_REGION = 38;
	public static final int IMMEDIATE_LOSSLESS_GENERIC_REGION = 39;
	public static final int INTERMEDIATE_GENERIC_REFINEMENT_REGION = 40;
	public static final int IMMEDIATE_GENERIC_REFINEMENT_REGION = 42;
	public static final int IMMEDIATE_LOSSLESS_GENERIC_REFINEMENT_REGION = 43;
	public static final int PAGE_INFORMATION = 48;
	public static final int END_OF_PAGE = 49;
	public static final int END_OF_STRIPE = 50;
	public static final int END_OF_FILE = 51;
	public static final int PROFILES = 52;
	public static final int TABLES = 53;
	public static final int EXTENSION = 62;
	public static final int BITMAP = 70;

	protected SegmentHeader segmentHeader;

	protected HuffmanDecoder huffmanDecoder;

	protected ArithmeticDecoder arithmeticDecoder;

	protected MMRDecoder mmrDecoder;

	protected JBIG2StreamDecoder decoder;

	public Segment(JBIG2StreamDecoder streamDecoder) {
		this.decoder = streamDecoder;

//		try {
			//huffDecoder = HuffmanDecoder.getInstance();
//			arithmeticDecoder = ArithmeticDecoder.getInstance();
			
			huffmanDecoder = decoder.getHuffmanDecoder();
			arithmeticDecoder = decoder.getArithmeticDecoder();
			mmrDecoder = decoder.getMMRDecoder();
			
//		} catch (JBIG2Exception e) {
//			e.printStackTrace();
//		}
	}

	protected short readATValue() throws IOException {
		short atValue;
		short c0 = atValue = decoder.readByte();

		if ((c0 & 0x80) != 0) {
			atValue |= -1 - 0xff;
		}

		return atValue;
	}

	public SegmentHeader getSegmentHeader() {
		return segmentHeader;
	}

	public void setSegmentHeader(SegmentHeader segmentHeader) {
		this.segmentHeader = segmentHeader;
	}

	public abstract void readSegment() throws IOException, JBIG2Exception;
}
