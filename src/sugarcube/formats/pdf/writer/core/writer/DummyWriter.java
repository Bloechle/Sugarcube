package sugarcube.formats.pdf.writer.core.writer;

import sugarcube.formats.pdf.writer.exception.PDFException;

public class DummyWriter extends AbstractWriter {
	private int bytes = 0;

	public void write(long value) throws PDFException {
		write("" + value);
	}

	public void write(Float value) throws PDFException {
		write("" + value);
	}

	public void write(String value) throws PDFException {
		bytes += value.length(); 
	}

	public void write(byte value) throws PDFException {
		bytes++;
	}

	public void write(byte[] values, int numberOfBytes) throws PDFException {
		bytes += numberOfBytes;
	}

	public long getWrittenBytes() {
		return bytes;
	}

}
