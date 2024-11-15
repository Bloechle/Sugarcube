package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders;

public class DecodeIntResult {

	private int intResult;
	private boolean booleanResult;

	public DecodeIntResult(int intResult, boolean booleanResult) {
		this.intResult = intResult;
		this.booleanResult = booleanResult;
	}

	public int intResult() {
		return intResult;
	}

	public boolean booleanResult() {
		return booleanResult;
	}
}
