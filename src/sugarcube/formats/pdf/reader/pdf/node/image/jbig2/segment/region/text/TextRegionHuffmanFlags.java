package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region.text;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Flags;

public class TextRegionHuffmanFlags extends Flags {

	public static String SB_HUFF_FS = "SB_HUFF_FS";
	public static String SB_HUFF_DS = "SB_HUFF_DS";
	public static String SB_HUFF_DT = "SB_HUFF_DT";
	public static String SB_HUFF_RDW = "SB_HUFF_RDW";
	public static String SB_HUFF_RDH = "SB_HUFF_RDH";
	public static String SB_HUFF_RDX = "SB_HUFF_RDX";
	public static String SB_HUFF_RDY = "SB_HUFF_RDY";
	public static String SB_HUFF_RSIZE = "SB_HUFF_RSIZE";

	public void setFlags(int flagsAsInt) {
		this.flagsAsInt = flagsAsInt;

		/** extract SB_HUFF_FS */
		flags.put(SB_HUFF_FS, new Integer(flagsAsInt & 3));

		/** extract SB_HUFF_DS */
		flags.put(SB_HUFF_DS, new Integer((flagsAsInt >> 2) & 3));

		/** extract SB_HUFF_DT */
		flags.put(SB_HUFF_DT, new Integer((flagsAsInt >> 4) & 3));

		/** extract SB_HUFF_RDW */
		flags.put(SB_HUFF_RDW, new Integer((flagsAsInt >> 6) & 3));

		/** extract SB_HUFF_RDH */
		flags.put(SB_HUFF_RDH, new Integer((flagsAsInt >> 8) & 3));

		/** extract SB_HUFF_RDX */
		flags.put(SB_HUFF_RDX, new Integer((flagsAsInt >> 10) & 3));

		/** extract SB_HUFF_RDY */
		flags.put(SB_HUFF_RDY, new Integer((flagsAsInt >> 12) & 3));

		/** extract SB_HUFF_RSIZE */
		flags.put(SB_HUFF_RSIZE, new Integer((flagsAsInt >> 14) & 1));

		if (JBIG2StreamDecoder.debug)
			System.out.println(flags);
	}
}
