package sugarcube.formats.pdf.writer.document;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.writer.AbstractWriter;
import sugarcube.formats.pdf.writer.core.writer.DummyWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class LinearizationDictionary extends DictionaryObject {
	private long fileLength;
	private int primaryHintOffset;
	private int primaryHintLength;
	private int firstPageObject;
	private long endPageOffset;
	private int numberOfPages;
	private long firstReference;

	public LinearizationDictionary(PDFWriter environment) throws PDFException {
		super(environment);
	}

	public void addDictionaryEntries() throws PDFException {
		addDictionaryEntry("Linearized", 1, Writer.INTEGER);
		addDictionaryEntry("L", fileLength, Writer.INTEGER);
		addDictionaryEntry("H", new Integer[]{primaryHintOffset, primaryHintLength}, Writer.INTEGER_ARRAY);
		addDictionaryEntry("O", firstPageObject, Writer.INTEGER);
		addDictionaryEntry("E", endPageOffset, Writer.INTEGER);
		addDictionaryEntry("N", numberOfPages, Writer.INTEGER);
		addDictionaryEntry("T", firstReference, Writer.INTEGER);		
	}
	
	public void write() throws PDFException{
		PDFWriter environment = pdfWriter();
		Writer writer = environment.getWriter();
		
		//environment.registerEntry(writer.getWrittenBytes());
		
		
		write(writer);
	}
	
	public long calculateSize() throws PDFException{
		DummyWriter writer = new DummyWriter();
		write(writer);
		cleanDictionary();
		return writer.getWrittenBytes();
	}

	private final void write(AbstractWriter writer) throws PDFException{
		writer.openObject(getID());
		writer.openDictionary();
		addDictionaryEntries();
		writeDictionaryEntries(writer);
		writer.closeDictionary();
		writer.closeObject();
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public void setPrimaryHintOffset(int primaryHintOffset) {
		this.primaryHintOffset = primaryHintOffset;
	}

	public void setPrimaryHintLength(int primaryHintLength) {
		this.primaryHintLength = primaryHintLength;
	}

	public void setFirstPageObject(int firstPageObject) {
		this.firstPageObject = firstPageObject;
	}

	public void setEndPageOffset(long endPageOffset) {
		this.endPageOffset = endPageOffset;
	}

	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public void setFirstReference(long firstReference) {
		this.firstReference = firstReference;
	}

	public long getEndPageOffset() {
		return endPageOffset;
	}

}
