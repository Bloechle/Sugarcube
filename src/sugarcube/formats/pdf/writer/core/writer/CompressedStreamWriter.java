package sugarcube.formats.pdf.writer.core.writer;

import sugarcube.formats.pdf.writer.exception.PDFException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class CompressedStreamWriter extends AbstractWriter {
	private ByteArrayOutputStream byteArrayOutputStream;
	private DeflaterOutputStream deflaterOutputStream;
	private long bytes = 0;
	
	public CompressedStreamWriter(){
		byteArrayOutputStream = new ByteArrayOutputStream();
		deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, new Deflater(Deflater.BEST_COMPRESSION));
	}
	
	public byte[] readBytes(){
		return byteArrayOutputStream.toByteArray();
	}
	
	public long getWrittenBytes(){
		return bytes;
	}
	
	public int getCompressedStreamSize(){
		return byteArrayOutputStream.size();
	}
	
	public void compress() throws PDFException{
		try {
			deflaterOutputStream.flush();
			deflaterOutputStream.finish();
		} catch (Exception e) {
			throw new PDFException("CompressedStreamWriter: unable to compress the stream stream");
		}
	}

	public void write(long value) throws PDFException {
		write("" + value);
	}

	public void write(Float value) throws PDFException {
		write("" + value);
	}

	public void write(String value) throws PDFException {
		byte[] bytes = value.getBytes();
		write(bytes, bytes.length);
	}

	public void write(byte value) throws PDFException {
		try {
			deflaterOutputStream.write(value);
			bytes++;
		} catch (IOException e) {
			throw new PDFException("CompressedStreamWriter: unable to write in the compressed stream");
		}
	}

	public void write(byte[] values, int numberOfBytes) throws PDFException {
		try {
			deflaterOutputStream.write(values, 0, numberOfBytes);
			bytes += numberOfBytes;
		} catch (IOException e) {
			throw new PDFException("CompressedStreamWriter: unable to write in the compressed stream");
		}
	}
}
