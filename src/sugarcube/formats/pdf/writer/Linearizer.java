package sugarcube.formats.pdf.writer;

import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.document.CrossReferenceTable;
import sugarcube.formats.pdf.writer.document.FileTrailer;
import sugarcube.formats.pdf.writer.document.LinearizationDictionary;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class Linearizer {
	private final static int BUFFER_SIZE = 512;
	private PDFWriter environment;
	private LinearizationDictionary linearizationDictionary;
	private CrossReferenceTable firstPageCrossReferenceTable;
	private CrossReferenceTable mainCrossReferenceTable;
	private CrossReferenceTable currentCrossReferenceTable;
	private FileTrailer firstPageTrailer;
	private FileTrailer mainTrailer;
	private HintTable hintStreamTable;
	private int catalogID = -1;
	private long breakPoint;

	public Linearizer(PDFWriter environment) throws PDFException{
		this.environment = environment;
		linearizationDictionary = new LinearizationDictionary(environment);
		firstPageCrossReferenceTable = new CrossReferenceTable(environment, linearizationDictionary.getID());
		mainCrossReferenceTable = new CrossReferenceTable(environment);
		firstPageTrailer = new FileTrailer(environment);
		mainTrailer = new FileTrailer(environment);
		hintStreamTable = new HintTable(environment);
		currentCrossReferenceTable = firstPageCrossReferenceTable;
		firstPageCrossReferenceTable.addEntry(-1, 0, 0, CrossReferenceTable.IN_USE_ENTRY);
	}	

	public void write(File tempFile) throws PDFException{
		environment.createFile();
		firstPageTrailer.setRootID(catalogID);
		firstPageTrailer.setSize(firstPageCrossReferenceTable.getNumberOfObjects());
		mainTrailer.setRootID(catalogID);
		mainTrailer.setSize(firstPageCrossReferenceTable.getNumberOfObjects());
		updateSizes(tempFile);
		writeLinearizedFile(tempFile);
	}
	
	private final void updateSizes(File tempFile) throws PDFException{
		String fileID = environment.getFileID();
		firstPageTrailer.setFileIDs(fileID, fileID);
		mainTrailer.setFileIDs(fileID, fileID);
		long bytes = PDFWriter.HEADER.length() + 2;
		bytes += 6;
		firstPageCrossReferenceTable.updateEntriesOffset(bytes, 0, 1);
		long dictionarySize = linearizationDictionary.calculateSize();
		bytes += dictionarySize;
		firstPageTrailer.setCrossReferenceTablePosition(bytes);
		bytes += firstPageCrossReferenceTable.calculateSize();
		bytes += firstPageTrailer.calculateSize();
		bytes += hintStreamTable.calculateSize();
		firstPageCrossReferenceTable.updateEntriesOffset(bytes, 1, firstPageCrossReferenceTable.getNumberOfObjects() - 1);
		long endPageOffset = linearizationDictionary.getEndPageOffset();
		linearizationDictionary.setEndPageOffset(endPageOffset + bytes);
		bytes += tempFile.length();
		mainCrossReferenceTable.updateEntriesOffset(bytes, 1, mainCrossReferenceTable.getNumberOfObjects() - 1);
		bytes += mainCrossReferenceTable.calculateSize();
		bytes += mainTrailer.calculateSize();
		
		int fileLengthOffset = ("" + bytes).length() - firstPageCrossReferenceTable.getNumberOfObjects() - 1;
		bytes += fileLengthOffset;
		linearizationDictionary.setFileLength(bytes);
		
		
		int offset;
		//correct dictionary entries
		do{
			endPageOffset = linearizationDictionary.getEndPageOffset();
			linearizationDictionary.setEndPageOffset(endPageOffset + fileLengthOffset);
			System.out.println("correct: " + endPageOffset + ">>" + linearizationDictionary.getEndPageOffset());
			offset = ("" + linearizationDictionary.getEndPageOffset()).length() - ("" + endPageOffset).length();
			if (offset != 0){
				firstPageCrossReferenceTable.updateEntriesOffset(offset, 0, firstPageCrossReferenceTable.getNumberOfObjects());
				offset = ("" + (bytes + offset)).length() - ("" + bytes).length();
				System.out.println("correct: " + bytes + ">>" + (bytes + offset));
				bytes += offset;
				linearizationDictionary.setFileLength(bytes);
				fileLengthOffset = offset;
			}
		}while(offset != 0);
	}
	
	private final void writeLinearizedFile(File tempFile) throws PDFException{
		Writer writer = environment.getWriter();
		writer.writeComment(PDFWriter.HEADER);
		writer.write(new byte[]{37, (byte)255, (byte)254, (byte)253, (byte)252, 10}, 6);
		linearizationDictionary.write();
		firstPageCrossReferenceTable.write();
		firstPageTrailer.write();
		writer.write(Lexic.LINE_FEED);
		try {
			FileInputStream fileInputStream = new FileInputStream(tempFile);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			byte[] inputBytes = new byte[BUFFER_SIZE];
			int readBytes;
			long counter = 0;
			int bytesToRead = (int)Math.min(breakPoint, BUFFER_SIZE);
			while((readBytes = dataInputStream.read(inputBytes, 0, bytesToRead)) != -1){
				writer.write(inputBytes, readBytes);
				counter += readBytes;
				bytesToRead = (int)(Math.min(breakPoint, counter + BUFFER_SIZE) - counter);
				if (bytesToRead == 0)
					break;
			}
			hintStreamTable.write();
			while((readBytes = dataInputStream.read(inputBytes, 0, BUFFER_SIZE)) != -1){
				writer.write(inputBytes, readBytes);
			}			
		} catch (Exception e) {
			throw new PDFException("Linearizer: unable to copy temp file");
		}
		mainCrossReferenceTable.write();
		mainTrailer.write();
		writer.close();
		System.out.println(writer.getWrittenBytes());
	}
		
	public void addEntry(long startOffset, int generationNumber, char type) throws PDFException{
		System.out.println(this + " object ID not yet taken into account");
		currentCrossReferenceTable.addEntry(-1, startOffset, generationNumber, type);
	}
	
	protected void changeCrossReferenceTable(){
		
		
		currentCrossReferenceTable = mainCrossReferenceTable;
	}

	protected void setPrimaryHintOffset(int primaryHintOffset) {
		linearizationDictionary.setPrimaryHintOffset(primaryHintOffset);
	}

	protected void setPrimaryHintLength(int primaryHintLength) {
		linearizationDictionary.setPrimaryHintLength(primaryHintLength);
	}

	protected void setFirstPageObject(int firstPageObject) {
		linearizationDictionary.setFirstPageObject(firstPageObject);
	}

	protected void setEndFirstPageOffset(long endPageOffset) {
		linearizationDictionary.setEndPageOffset(endPageOffset);
		breakPoint = endPageOffset;
	}

	protected void setNumberOfPages(int numberOfPages) {
		linearizationDictionary.setNumberOfPages(numberOfPages);
	}

	protected void setFirstReference(long firstReference) {
		linearizationDictionary.setFirstReference(firstReference);
	}
	
	protected void setCatalogID(int catalogID){
		this.catalogID = catalogID;
	}
	
	public void setHints(int[] hints) throws PDFException{
		hintStreamTable.setHints(hints);
	}
}
