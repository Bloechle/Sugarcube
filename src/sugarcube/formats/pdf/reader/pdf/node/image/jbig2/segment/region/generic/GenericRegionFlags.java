package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region.generic;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Flags;

public class GenericRegionFlags extends Flags {

	public static String MMR = "MMR";
	public static String GB_TEMPLATE = "GB_TEMPLATE";
	public static String TPGDON = "TPGDON";

	public void setFlags(int flagsAsInt) {
		this.flagsAsInt = flagsAsInt;
		
		/** extract MMR */
		flags.put(MMR, new Integer(flagsAsInt & 1));
		
		/** extract GB_TEMPLATE */
		flags.put(GB_TEMPLATE, new Integer((flagsAsInt >> 1) & 3));
		
		/** extract TPGDON */
		flags.put(TPGDON, new Integer((flagsAsInt >> 3) & 1));
		
		
		if(JBIG2StreamDecoder.debug)
			System.out.println(flags);
	}
}
