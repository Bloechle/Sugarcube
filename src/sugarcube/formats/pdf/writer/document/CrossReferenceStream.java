package sugarcube.formats.pdf.writer.document;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;
import java.util.HashMap;

public class CrossReferenceStream {
	public final static int FREE_OBJECT = 0;
	public final static int NORMAL_OBJECT = 1;
	public final static int STREAM_OBJECT = 2;
	private final static int SIZE_1 = 1;
	private final static int SIZE_2 = 10;
	private final static int SIZE_3 = 5;
	private int rootID;
	private int maxValue = 65535;
	private int maxValueBytes = 5;
	private final HashMap<Long, Integer> subobjects = new HashMap<Long, Integer>();
	private ArrayList<Integer> ids = new ArrayList<Integer>();
	
	private ArrayList<Entry> entries = new ArrayList<Entry>();
	private PDFWriter environment;
	
	public CrossReferenceStream(PDFWriter environment) throws PDFException{
		this.environment = environment;
		/*for (int v = 0; v < SIZE_3; v++)
			maxValue *= 16;
		maxValue--;
		maxValueBytes = 0;
		for(int v = maxValue; v > 0; v /= 10)
			maxValueBytes++;*/
		addEntry(0, FREE_OBJECT, 0);
		throw new PDFException("Cross reference streams not yet implemented");
	}

	public void write() throws PDFException {
		int id = environment.computeID();
		Writer writer = environment.getWriter();
		long position = writer.getWrittenBytes();
		/**
		environment.registerEntry(id, NORMAL_OBJECT, position );		
		*/
		writer.openObject(id);
		writer.openDictionary();
		int streamSize = (SIZE_1 + SIZE_2 + maxValueBytes + 2) * entries.size();
		writer.writeDictionaryPair("Type", "XRef", Writer.NAME);
		writer.writeDictionaryPair("Size", entries.size(), Writer.INTEGER);
		writer.writeDictionaryPair("Root", rootID, Writer.INDIRECT_REFERENCE);
		writer.writeDictionaryPair("Length", streamSize, Writer.INTEGER);
		//writer.writeDictionaryPair("DL", streamSize, Writer.INTEGER);
		Integer[] widths = new Integer[]{SIZE_1, SIZE_2, maxValueBytes};
		writer.writeDictionaryPair("W", widths, Writer.INTEGER_ARRAY);
		writer.writeDictionaryPair("Index", new Integer[]{0, entries.size()}, Writer.INTEGER_ARRAY);
		writer.closeDictionary();
		writer.openStream();
		Entry entry;
		for (int e = 0; e < entries.size(); e++){
			entry = entries.get(e);//ids.indexOf(e));
			writer.write(complete(entry.type, SIZE_1).getBytes(), SIZE_1);
			writer.write(Lexic.SPACE);
			writer.write(complete(entry.startOffset, SIZE_2).getBytes(), SIZE_2);
			writer.write(Lexic.SPACE);
			writer.write(complete(entry.complement, maxValueBytes).getBytes(), SIZE_3);
			writer.write(Lexic.CARRIAGE_RETURN);
			writer.write(Lexic.LINE_FEED);
		}
		
		writer.closeStream();
		writer.closeObject();
		writer.write("startxref" + Lexic.LINE_FEED);
		writer.write(position + Lexic.LINE_FEED + Lexic.PERCENT_SIGN + Lexic.PERCENT_SIGN + "EOF");
	}
	
	public void addEntry(int id, int type, long position) throws PDFException{
		ids.add(id);
		Entry entry = new Entry();		
		entry.type = type;
		entry.startOffset = position;
		if (type == STREAM_OBJECT){
			if (subobjects.containsKey(position))
				entry.complement = subobjects.get(position);
			subobjects.put(position, entry.complement + 1);
		}else if (type == FREE_OBJECT){
			entry.complement = maxValue;
		}
		entries.add(entry);
	}

	public void setRootID(int rootID) {
		this.rootID = rootID;
	}
	
	private String complete(long value, int digits){
		String result = "" + value;
		while (result.length() < digits)
			result = '0' + result;
		System.out.println(result);
		return result;
	}
		
	/*private byte[] complete(long value, int digits){
		byte[] result = new byte[digits];
		for (int b = digits - 1, offset = 0; b >= 0; b--, offset += 8){
			result[b] = (byte)(value >>> offset);
			System.out.print(result[b]);
		}
		System.out.print(" ");
		return result;
	}*/
	
	private class Entry{
		protected int type;
		protected long startOffset;
		protected int complement;
	}

	public int getNumberOfObjects() {
		return entries.size();
	}
}
