package sugarcube.formats.epub.fonts;

import java.io.*;

public class FontOutputStream extends DataOutputStream {


    public FontOutputStream() throws IOException {
        super(new ByteArrayOutputStream());
    }

    public byte[] bytes() {
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    public void shorts(int... a) throws IOException {
        for (int value : a)
            super.writeShort(value);

    }

    public void ints(int... a) throws IOException {
        for (int value : a)
            super.writeInt(value);
    }

    public void longs(long... a) throws IOException {
        for (long value : a)
            super.writeLong(value);
    }
}
