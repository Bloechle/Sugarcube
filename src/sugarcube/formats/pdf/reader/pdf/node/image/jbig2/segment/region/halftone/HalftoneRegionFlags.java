package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.region.halftone;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders.JBIG2StreamDecoder;
import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment.Flags;

public class HalftoneRegionFlags extends Flags {

    public static String H_MMR = "H_MMR";
    public static String H_TEMPLATE = "H_TEMPLATE";
    public static String H_ENABLE_SKIP = "H_ENABLE_SKIP";
    public static String H_COMB_OP = "H_COMB_OP";
    public static String H_DEF_PIXEL = "H_DEF_PIXEL";

    public void setFlags(int flagsAsInt) {
        this.flagsAsInt = flagsAsInt;

        /** extract H_MMR */
		flags.put(H_MMR, new Integer(flagsAsInt & 1));
		
		/** extract H_TEMPLATE */
		flags.put(H_TEMPLATE, new Integer((flagsAsInt >> 1) & 3));
		
		/** extract H_ENABLE_SKIP */
		flags.put(H_ENABLE_SKIP, new Integer((flagsAsInt >> 3) & 1));
		
		/** extract H_COMB_OP */
		flags.put(H_COMB_OP, new Integer((flagsAsInt >> 4) & 7));
		
		/** extract H_DEF_PIXEL */
		flags.put(H_DEF_PIXEL, new Integer((flagsAsInt >> 7) & 1));

		
		if(JBIG2StreamDecoder.debug)
			System.out.println(flags);
    }
}
