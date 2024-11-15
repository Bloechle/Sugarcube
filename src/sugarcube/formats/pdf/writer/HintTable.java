package sugarcube.formats.pdf.writer;

import sugarcube.formats.pdf.writer.core.object.Stream;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class HintTable extends Stream {
	public final static int MIN_OBJECTS_AMOUNT = 0;
	public final static int FIRST_PAGE_LOCATION = 1;
	public final static int OBJECTS_DIFFERENCE_BITS = 2;
	public final static int MIN_STREAM_SIZE = 3;
	public final static int BYTES_DIFFERENCE_BITS = 4;
	public final static int FIRST_CONTENT_OFFSET = 5;
	public final static int OFFSETS_DIFFERENCE_BITS = 6;
	public final static int MIN_CONTENT_SIZE = 7;
	public final static int CONTENT_DIFFERENCE_BITS = 8;
	public final static int SHARED_REFERENCES = 9;
	public final static int MAX_SHARED_OBJECT = 10;
	public static final int NUMBER_OF_HINTS = 13;
	private int[] hints;

	public HintTable(PDFWriter environment) {
		super(environment);
	}

	public long calculateSize() throws PDFException {
		fillDictionary();
		return calculateSize(createTable());
	}

	public void write() throws PDFException {
		fillDictionary();
		write(createTable());
	}
	
	private final void fillDictionary(){
		cleanDictionary();
		addDictionaryEntry("S", 0, Writer.INTEGER);
	}
	
	private StringBuilder createTable(){
		StringBuilder builder = new StringBuilder();
		return builder;
	}
	
	public void setHints(int[] hints) throws PDFException{
		this.hints = hints;
	}
}
