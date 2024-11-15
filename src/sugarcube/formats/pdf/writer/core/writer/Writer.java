package sugarcube.formats.pdf.writer.core.writer;

import sugarcube.formats.pdf.writer.exception.PDFException;

import java.io.DataOutputStream;
import java.io.IOException;

public class Writer extends AbstractWriter{
	private DataOutputStream writer;
	private long bytesCounter = 0;
		
	public Writer(DataOutputStream writer){
		this.writer = writer;
	}

	public void write(long value) throws PDFException {
		write("" + value);
	}

	public void write(Float value) throws PDFException {
		write("" + value);
	}
	
	public void write(String value) throws PDFException{
		try {
			int length = value.length();
			//value = new String(value.getBytes(), 0, length, "ASCII");			
			for (int c = 0; c < length; c++)
				writer.writeByte(value.charAt(c));
			bytesCounter += value.length();
		} catch (IOException e) {
			throw new PDFException("Unable to write text '" + value + "'");
		}
	}

	public void write(byte b) throws PDFException {
		try {
			writer.writeByte(b);
			bytesCounter++;
		} catch (IOException e) {
			throw new PDFException("Unable to write byte '" + b + "'");
		}
	}
	
	 public void write(byte[] bytes) throws PDFException{
	    try {
	      writer.write(bytes, 0, bytes.length);
	      bytesCounter += bytes.length;
	    } catch (IOException e) {
	      throw new PDFException("Unable to write bytes");
	    }   
	  }
	
	public void write(byte[] bytes, int numberOfBytes) throws PDFException{
		try {
			writer.write(bytes, 0, numberOfBytes);
			bytesCounter += numberOfBytes;
		} catch (IOException e) {
			throw new PDFException("Unable to write bytes");
		}		
	}

	public long getWrittenBytes() {
		return bytesCounter;
	}

	public void flush() throws PDFException {
		try {
			writer.flush();
		} catch (IOException e) {
			throw new PDFException("Unable to flush pdf file");
		}
	}

	public void close() throws PDFException {
		try {
			writer.close();
		} catch (IOException e) {
			throw new PDFException("Unable to close pdf file");
		}
	}
}
