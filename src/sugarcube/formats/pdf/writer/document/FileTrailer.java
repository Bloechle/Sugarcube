package sugarcube.formats.pdf.writer.document;


import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.writer.AbstractWriter;
import sugarcube.formats.pdf.writer.core.writer.DummyWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class FileTrailer {
	//ignored: Prev, Encrypt, ID
	private int size;
	private int rootID;
	private int documentInformationID;
	private long crossReferenceTablePosition;
	private PDFWriter environment;
	private String fileID;
	private String updateID;
	
	public FileTrailer(PDFWriter environment) throws PDFException{
		this.environment = environment;
		/**DocumentInformation documentInformation = new DocumentInformation(environment);
		documentInformationID = documentInformation.getID();
		documentInformation.write();*/
	}

	public void write() throws PDFException {
//		Log.debug(this, ".write - document info not yet implemented");
		writeContent(environment.getWriter());
	}
	
	private final void writeContent(AbstractWriter writer) throws PDFException{
		writer.write("trailer" + Lexic.LINE_FEED);
		writer.openDictionary();
		writer.writeDictionaryPair("Size", size, Writer.INTEGER);
		writer.writeDictionaryPair("Root", rootID, Writer.INDIRECT_REFERENCE);
		/**writer.writeDictionaryPair("Info", documentInformationID, Writer.INDIRECT_REFERENCE);*/
		writer.writeDictionaryPair("ID", new String[]{fileID, updateID}, Writer.STRING_ARRAY);
		writer.closeDictionary();
		writer.write("startxref" + Lexic.LINE_FEED);
		writer.write(crossReferenceTablePosition + Lexic.LINE_FEED + Lexic.PERCENT_SIGN + Lexic.PERCENT_SIGN + "EOF");
	}
	
	public void setSize(int size){
		this.size = size;
	}

	public void setRootID(int rootID) {
		this.rootID = rootID;
	}

	public void setCrossReferenceTablePosition(long crossReferenceTablePosition) {
		this.crossReferenceTablePosition = crossReferenceTablePosition;
	}

	public long calculateSize() throws PDFException {
		DummyWriter writer = new DummyWriter();
		writeContent(writer);
		return writer.getWrittenBytes();
	}
	
	public void setFileIDs(String fileID, String updateID){
		this.fileID = fileID;
		this.updateID = updateID;
	}
}
