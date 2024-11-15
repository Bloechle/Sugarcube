package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.segment;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Flags {

	protected int flagsAsInt;

	protected Map flags = new LinkedHashMap();

	public int getFlagValue(String key) {
		Integer value = (Integer) flags.get(key);
		return value.intValue();
	}

	public abstract void setFlags(int flagsAsInt);
}
