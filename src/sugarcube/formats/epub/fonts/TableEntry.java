package sugarcube.formats.epub.fonts;

import java.nio.ByteBuffer;

public class TableEntry {
    byte[] data;
    int checksum;
    int offset;

    TableEntry(byte[] data) {
        this.data = data;
        this.checksum = calculateChecksum(data);
    }

    private static int calculateChecksum(byte[] data) {
        int sum = 0;
        int nLongs = (data.length + 3) / 4;
        ByteBuffer buffer = ByteBuffer.allocate(nLongs * 4);
        buffer.put(data);
        buffer.rewind();

        for (int i = 0; i < nLongs; i++) {
            sum += buffer.getInt();
        }
        return sum;
    }
}
