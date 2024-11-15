package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.pattern;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Flags;

public class PatternDictionaryFlags extends Flags {

	public static String HD_MMR = "HD_MMR";
	public static String HD_TEMPLATE = "HD_TEMPLATE";

	public void setFlags(int flagsAsInt) {
		this.flagsAsInt = flagsAsInt;

		/** extract HD_MMR */
		flags.put(HD_MMR, new Integer(flagsAsInt & 1));

		/** extract HD_TEMPLATE */
		flags.put(HD_TEMPLATE, new Integer((flagsAsInt >> 1) & 3));

		if (JBIG2StreamDecoder.debug)
			System.out.println(flags);
	}
}
