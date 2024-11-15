package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.pattern;


import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.JBIG2Exception;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.image.JBIG2Bitmap;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Segment;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.util.BinaryOperation;

import java.io.IOException;

public class PatternDictionarySegment extends Segment {

	PatternDictionaryFlags patternDictionaryFlags = new PatternDictionaryFlags();
	private int width;
	private int height;
	private int grayMax;
	private JBIG2Bitmap[] bitmaps;
	private int size;

	public PatternDictionarySegment(JBIG2StreamDecoder streamDecoder) {
		super(streamDecoder);
	}

	public void readSegment() throws IOException, JBIG2Exception {
		/** read text region Segment flags */
		readPatternDictionaryFlags();

		width = decoder.readByte();
		height = decoder.readByte();

		if (JBIG2StreamDecoder.debug)
			System.out.println("pattern dictionary size = " + width + " , " + height);

		short[] buf = new short[4];
		decoder.readByte(buf);
		grayMax = BinaryOperation.getInt32(buf);

		if (JBIG2StreamDecoder.debug)
			System.out.println("grey max = " + grayMax);

		boolean useMMR = patternDictionaryFlags.getFlagValue(PatternDictionaryFlags.HD_MMR) == 1;
		int template = patternDictionaryFlags.getFlagValue(PatternDictionaryFlags.HD_TEMPLATE);

		if (!useMMR) {
			arithmeticDecoder.resetGenericStats(template, null);
			arithmeticDecoder.start();
		}

		short[] genericBAdaptiveTemplateX = new short[4], genericBAdaptiveTemplateY = new short[4];

		genericBAdaptiveTemplateX[0] = (short) -width;
		genericBAdaptiveTemplateY[0] = 0;
		genericBAdaptiveTemplateX[1] = -3;
		genericBAdaptiveTemplateY[1] = -1;
		genericBAdaptiveTemplateX[2] = 2;
		genericBAdaptiveTemplateY[2] = -2;
		genericBAdaptiveTemplateX[3] = -2;
		genericBAdaptiveTemplateY[3] = -2;

		size = grayMax + 1;

		JBIG2Bitmap bitmap = new JBIG2Bitmap(size * width, height, arithmeticDecoder, huffmanDecoder, mmrDecoder);
		bitmap.clear(0);
		bitmap.readBitmap(useMMR, template, false, false, null, genericBAdaptiveTemplateX, genericBAdaptiveTemplateY, segmentHeader.getSegmentDataLength() - 7);

		JBIG2Bitmap bitmaps[] = new JBIG2Bitmap[size];

		int x = 0;
		for (int i = 0; i < size; i++) {
			bitmaps[i] = bitmap.getSlice(x, 0, width, height);
			x += width;
		}

        this.bitmaps = bitmaps;
	}


	public JBIG2Bitmap[] getBitmaps() {
		return bitmaps;
	}

	private void readPatternDictionaryFlags() throws IOException {
		short patternDictionaryFlagsField = decoder.readByte();

		patternDictionaryFlags.setFlags(patternDictionaryFlagsField);

		if (JBIG2StreamDecoder.debug)
			System.out.println("pattern Dictionary flags = " + patternDictionaryFlagsField);
	}

	public PatternDictionaryFlags getPatternDictionaryFlags() {
		return patternDictionaryFlags;
	}

	public int getSize() {
		return size;
	}
}
