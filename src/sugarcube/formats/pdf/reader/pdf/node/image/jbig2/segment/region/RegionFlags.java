package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Flags;

public class RegionFlags extends Flags {

	public static String EXTERNAL_COMBINATION_OPERATOR = "EXTERNAL_COMBINATION_OPERATOR";

	public void setFlags(int flagsAsInt) {
		this.flagsAsInt = flagsAsInt;

		/** extract EXTERNAL_COMBINATION_OPERATOR */
		flags.put(EXTERNAL_COMBINATION_OPERATOR, new Integer(flagsAsInt & 7));

		if (JBIG2StreamDecoder.debug)
			System.out.println(flags);
	}
}
