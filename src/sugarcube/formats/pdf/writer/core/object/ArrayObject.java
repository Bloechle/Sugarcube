package sugarcube.formats.pdf.writer.core.object;

import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

public abstract class ArrayObject<K> extends ContainerObject{
	public final static int INTEGER_TYPE = 0;
	public final static int REAL_TYPE = 1;
	private ObjectStream objectStream;
	
	public ArrayObject(PDFWriter environment, ObjectStream objectStream){
		super(environment);
		this.objectStream = objectStream;
	}

	public ArrayObject(PDFWriter environment){
		this(environment, null);
	}

	public void write() throws PDFException{
		PDFWriter environment = pdfWriter();
		Writer writer = environment.getWriter();
		if (objectStream == null)
			environment.registerEntry(getID(), writer.getWrittenBytes());
		else
			environment.registerEntry(getID(), objectStream.getID());
		writer.openObject(getID());
		writer.openArray();
		K[] entries = getArrayEntries();
		writeUncompressedStream(writer, entries);
		writer.closeArray();
		writer.closeObject();
	}
	
	private final int detectType(K[] objects){
		if (objects instanceof Float[])
			return REAL_TYPE;
		if (objects instanceof Long[])
			return INTEGER_TYPE;
		return -1;
	}
	
	private final void writeUncompressedStream(Writer writer, K[] entries) throws PDFException{
		//detect object type
		int type = detectType(entries);
		for (int e = 0; e < entries.length; e++) {
			writer.write(Lexic.SPACE);
			switch(type){
				case INTEGER_TYPE:
					writer.writeInteger((Long)entries[e]);
					continue;
				case REAL_TYPE:
					writer.writeReal(Util.format((Float)entries[e]));
					continue;
			}
		}
		
	}

	public abstract K[] getArrayEntries() throws PDFException;
}
