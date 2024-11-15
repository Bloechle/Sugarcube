package sugarcube.formats.pdf.writer.document;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CrossReferenceTable {
	public final static char IN_USE_ENTRY = 'n';
	public final static char FREE_ENTRY = 'f';	
	private ArrayList<Entry> entries = new ArrayList<Entry>();
	private PDFWriter environment;
	private int firstObjectID;
	
	public CrossReferenceTable(PDFWriter environment) throws PDFException{
		this(environment, 0);
	}
	
	public CrossReferenceTable(PDFWriter environment, int firstObjectID) throws PDFException{
		this.environment = environment;
		this.firstObjectID = firstObjectID;
		if (firstObjectID == 0)
			addEntry(-1, 0, 65535, FREE_ENTRY);
	}

	public void write() throws PDFException {
		Writer writer = environment.getWriter();
		writer.write("xref" + Lexic.LINE_FEED);
		writer.write(firstObjectID + Lexic.SPACE + entries.size() + Lexic.LINE_FEED);
		Collections.sort(entries, new Comparator<Entry>() {
			public int compare(Entry e1, Entry e2) {
				if (e1.objectID < e2.objectID)
					return -1;
				if (e1.objectID > e2.objectID)
					return 1;
				return 0;
			}
		});
		for (Entry entry: entries){
			writer.write(complete(entry.startOffset, 10)+ Lexic.SPACE);
			writer.write(complete(entry.generationNumber, 5) + Lexic.SPACE + entry.type + Lexic.CARRIAGE_RETURN +Lexic.LINE_FEED);	
		}
	}
	
	public long calculateSize(){
		return 7 + ("" + firstObjectID + entries.size()).length() + entries.size() * 20; 
	}
	
	public void updateEntriesOffset(long offset, int firstEntry, int numberOfEntries){
		for (int e = firstEntry, lastEntry = firstEntry + numberOfEntries; e < lastEntry; e++)
			entries.get(e).startOffset += offset;
	}
	
	public void addEntry(int objectID, long startOffset, int generationNumber, char type) throws PDFException{
		if (type != FREE_ENTRY && type != IN_USE_ENTRY)
			throw new PDFException("Invalid cross-reference table type '" + type + "'");
		Entry entry = new Entry();
		entry.objectID = objectID;
		entry.startOffset = startOffset;
		entry.generationNumber = generationNumber;
		entry.type = type;
		entries.add(entry);
	}
	
	private String complete(long value, int digits){
		String result = "" + value;
		while (result.length() < digits)
			result = '0' + result;
		return result;
	}
	
	private class Entry{
		protected int objectID;
		protected long startOffset; //or next free object
		protected int generationNumber;
		protected char type;
	}

	public int getNumberOfObjects() {
		return entries.size();
	}
}
