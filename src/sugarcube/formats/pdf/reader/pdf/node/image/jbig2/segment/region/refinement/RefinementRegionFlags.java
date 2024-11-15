package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region.refinement;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Flags;

public class RefinementRegionFlags extends Flags {

	public static String GR_TEMPLATE = "GR_TEMPLATE";
	public static String TPGDON = "TPGDON";

	public void setFlags(int flagsAsInt) {
		this.flagsAsInt = flagsAsInt;

		/** extract GR_TEMPLATE */
		flags.put(GR_TEMPLATE, new Integer(flagsAsInt & 1));

		/** extract TPGDON */
		flags.put(TPGDON, new Integer((flagsAsInt >> 1) & 1));

		if (JBIG2StreamDecoder.debug)
			System.out.println(flags);
	}
}
