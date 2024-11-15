package sugarcube.formats.pdf.writer.core.object;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.writer.AbstractWriter;
import sugarcube.formats.pdf.writer.core.writer.StringWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;

public class ObjectStream {
	private int id = -1;
	private PDFWriter environment;
	//private CompressedStreamWriter compressedStreamWriter = new CompressedStreamWriter();
	StringWriter compressedStreamWriter = new StringWriter();
	private ArrayList<Integer> ids = new ArrayList<Integer>();
	private ArrayList<Integer> offsets = new ArrayList<Integer>();

	public ObjectStream(PDFWriter environment) {
		this.environment = environment;
		id = environment.computeID();
	}
	
	public void write() throws PDFException{
		//create the header
		String references = "";
		for (int i = 0; i < ids.size(); i++) {
			references += ids.get(i) + Lexic.SPACE + offsets.get(i);
			if (i < ids.size() - 1)
				references += Lexic.SPACE;
			else
				references += Lexic.LINE_FEED;
		}
		//finalize the stream
		/**
		compressedStreamWriter.compress();
		*/
		
		
		//write the dictionary
		/**int streamSize = compressedStreamWriter.getCompressedStreamSize();
		 * 
		 */
		int streamSize = (int)compressedStreamWriter.getWrittenBytes();
		Writer writer = environment.getWriter();
		
		
		
		environment.registerEntry(getID(), writer.getWrittenBytes());
		//write object
		writer.openObject(id);
		writer.openDictionary();
		writer.writeDictionaryPair("Type", "ObjStm", Writer.NAME);
		writer.writeDictionaryPair("N", ids.size(), Writer.INTEGER);
		writer.writeDictionaryPair("First", references.length() + 1, Writer.INTEGER);
		writer.writeDictionaryPair("Length", streamSize, Writer.INTEGER);
		/**writer.writeDictionaryPair("DL", compressedStreamWriter.getCurrentStreamSize(), Writer.INTEGER);
		writer.writeDictionaryPair("Filter", "FlateDecode", Writer.NAME);*/
		writer.closeDictionary();
		writer.openStream();
		writer.write(references);
		/**
		writer.write(compressedStreamWriter.readBytes(), streamSize);
		*/
		writer.write(compressedStreamWriter.toString().getBytes(), streamSize);
		writer.closeStream();
		writer.closeObject();
		writer.flush();
	}

	public Integer getID() {
		return id;
	}

	public AbstractWriter getWriter() {
		return compressedStreamWriter;
	}

	public void register(int id) {
		ids.add(id);
		/**offsets.add(compressedStreamWriter.getCurrentStreamSize());*/
		offsets.add(compressedStreamWriter.toString().length());
	}
	

}
