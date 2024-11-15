package sugarcube.formats.pdf.reader.pdf.node.image.jbig2.io;

import sugarcube.formats.pdf.reader.pdf.node.image.jbig2.examples.pdf.PDFSegment;

import java.io.IOException;

public class StreamReader {
	private byte[] data;

	private int bitPointer = 7;

	private int bytePointer = 0;

	public StreamReader(byte[] data) {
		this.data = data;
	}

	public short readByte(PDFSegment pdfSeg) {
		short bite = (short) (data[bytePointer++] & 255);

		if (pdfSeg != null)
			pdfSeg.writeToHeader(bite);

		return bite;
	}

	public void readByte(short[] buf, PDFSegment pdfSeg) throws IOException {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (short) (data[bytePointer++] & 255);
		}

		if (pdfSeg != null)
			pdfSeg.writeToHeader(buf);
	}

	public short readByte() {
		short bite = (short) (data[bytePointer++] & 255);

		return bite;
	}

	public void readByte(short[] buf) {
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (short) (data[bytePointer++] & 255);
		}
	}

	public int readBit() {
		short buf = readByte();
		short mask = (short) (1 << bitPointer);

		int bit = (buf & mask) >> bitPointer;

		bitPointer--;
		if (bitPointer == -1) {
			bitPointer = 7;
		} else {
			movePointer(-1);
		}

		return bit;
	}

	public int readBits(int num) {
		int result = 0;

		for (int i = 0; i < num; i++) {
			result = (result << 1) | readBit();
		}

		return result;
	}

	public void movePointer(int ammount) {
		bytePointer += ammount;
	}

	public void consumeRemainingBits() {
		if (bitPointer != 7)
			readBits(bitPointer + 1);
	}

	public boolean isFinished() {
		return bytePointer == data.length;
	}
}
