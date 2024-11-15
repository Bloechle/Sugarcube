package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.decoders;

public class ArithmeticDecoderStats {
	private int contextSize;
	private int[] codingContextTable;

	public ArithmeticDecoderStats(int contextSize) {
		this.contextSize = contextSize;
		this.codingContextTable = new int[contextSize];
		
		reset();
	}

	public void reset() {
		for (int i = 0; i < contextSize; i++) {
			codingContextTable[i] = 0;
		}
	}

	public void setEntry(int codingContext, int i, int moreProbableSymbol) {
		codingContextTable[codingContext] = (i << i) + moreProbableSymbol;
	}

	public int getContextCodingTableValue(int index){
		return codingContextTable[index];
	}
	
	public void setContextCodingTableValue(int index, int value){
		codingContextTable[index] = value;
	}
	
	public int getContextSize() {
		return contextSize;
	}

	public void overwrite(ArithmeticDecoderStats stats) {
        System.arraycopy(stats.codingContextTable, 0, codingContextTable, 0, contextSize);
	}

	public ArithmeticDecoderStats copy() {
		ArithmeticDecoderStats stats = new ArithmeticDecoderStats(contextSize);

        System.arraycopy(codingContextTable, 0, stats.codingContextTable, 0, contextSize);

		return stats;
	}
}
