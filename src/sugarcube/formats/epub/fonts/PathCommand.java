package sugarcube.formats.epub.fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// Path command types
public abstract class PathCommand {
    abstract byte[] toCFFFormat() throws IOException;

    public static void writeCFFNumber(ByteArrayOutputStream baos, int number) throws IOException {
        if (number >= -107 && number <= 107) {
            baos.write(number + 139);
        } else if (number >= 108 && number <= 1131) {
            number -= 108;
            baos.write((number >> 8) + 247);
            baos.write(number & 0xFF);
        } else if (number >= -1131 && number <= -108) {
            number = -number - 108;
            baos.write((number >> 8) + 251);
            baos.write(number & 0xFF);
        } else {
            baos.write(28);
            baos.write((number >> 8) & 0xFF);
            baos.write(number & 0xFF);
        }
    }

    public static class MoveTo extends PathCommand {
        int x, y;

        public MoveTo(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        byte[] toCFFFormat() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writeCFFNumber(baos, x);
            writeCFFNumber(baos, y);
            baos.write(21); // rmoveto = 21
            return baos.toByteArray();
        }
    }

    public static class LineTo extends PathCommand {
        int x, y;

        public LineTo(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        byte[] toCFFFormat() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writeCFFNumber(baos, x);
            writeCFFNumber(baos, y);
            baos.write(5); // rlineto = 5
            return baos.toByteArray();
        }
    }

    public static class CurveTo extends PathCommand {
        int x1, y1, x2, y2, x3, y3;

        public CurveTo(int x1, int y1, int x2, int y2, int x3, int y3) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.x3 = x3;
            this.y3 = y3;
        }

        @Override
        byte[] toCFFFormat() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writeCFFNumber(baos, x1);
            writeCFFNumber(baos, y1);
            writeCFFNumber(baos, x2);
            writeCFFNumber(baos, y2);
            writeCFFNumber(baos, x3);
            writeCFFNumber(baos, y3);
            baos.write(8); // rrcurveto = 8
            return baos.toByteArray();
        }
    }
}
