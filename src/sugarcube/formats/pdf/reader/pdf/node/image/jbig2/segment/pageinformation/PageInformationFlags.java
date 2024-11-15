package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.pageinformation;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Flags;

public class PageInformationFlags extends Flags {

	public static String DEFAULT_PIXEL_VALUE = "DEFAULT_PIXEL_VALUE";
	public static String DEFAULT_COMBINATION_OPERATOR = "DEFAULT_COMBINATION_OPERATOR";

	public void setFlags(int flagsAsInt) {
		this.flagsAsInt = flagsAsInt;

		/** extract DEFAULT_PIXEL_VALUE */
		flags.put(DEFAULT_PIXEL_VALUE, new Integer((flagsAsInt >> 2) & 1));

		/** extract DEFAULT_COMBINATION_OPERATOR */
		flags.put(DEFAULT_COMBINATION_OPERATOR, new Integer((flagsAsInt >> 3) & 3));

		if (JBIG2StreamDecoder.debug)
			System.out.println(flags);
	}
}
