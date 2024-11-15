package sugarcube.formats.pdf.writer.core.writer;

import sugarcube.formats.pdf.writer.exception.PDFException;

public class StringWriter extends AbstractWriter{
	private StringBuilder stringBuilder = new StringBuilder();

	public void write(long value) throws PDFException {
		stringBuilder.append(value);
	}

	public void write(Float value) throws PDFException {
		stringBuilder.append(value);		
	}

	public void write(String value) throws PDFException {
		stringBuilder.append(value);
	}

	public void write(byte value) throws PDFException {
		stringBuilder.append(value);		
	}

	public void write(byte[] values, int numberOfBytes) throws PDFException {
		for (int b = 0; b < numberOfBytes; b++)
			stringBuilder.append(values[b]);		
	}
	
	public String toString(){
		return stringBuilder.toString();
	}

	public long getWrittenBytes() {
		return stringBuilder.length();
	}
	
}
