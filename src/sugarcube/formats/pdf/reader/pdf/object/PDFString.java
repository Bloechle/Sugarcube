package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.ByteArray;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.data.Byte;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class PDFString extends PDFObject
{
    public static final PDFString NULL_PDFSTRING = new PDFString();
    protected Unicodes codes = new Unicodes();
    protected boolean isHexadecimal = false;
    private byte[] originalBuffer = new byte[0];

    private PDFString()
    {
        super(Type.String);
    }

    public PDFString(PDFObject po)
    {
        super(Type.String, po);
        this.codes = new Unicodes();
        this.streamLocator = new StreamLocator();
    }

    public PDFString(PDFObject po, String token, StreamReader reader)
    {
        super(Type.String, po);
        long pos = reader.pos();

        if (token.equals("("))
            this.parseLiteral(reader);
        else if (token.equals("<"))
            this.parseHexadecimal(reader);
        else
            Log.warn(this, " - neither literal nor hexadecimal string, opening symbol: " + token);
        this.streamLocator = new StreamLocator(pos, reader.pos() - pos, reader.streamReference(pos));
    }

    /**
     * take a string and determine if it is unicode by looking at the lead
     * characters, and that the string must be a multiple of 2 chars long. Convert
     * a unicoded string's characters into the true unicode.
     *
     * @param input
     * @return
     */
    private String unicode(String input)
    {
        // determine if we have unicode, if so, translate it
        if (input.length() < 2 || (input.length() % 2) != 0)
            return input;
        int c0 = input.charAt(0) & 0xFF;
        int c1 = input.charAt(1) & 0xFF;
        if ((c0 == 0xFE && c1 == 0xFF) || (c0 == 0xFF && c1 == 0xFE))
        {
            // we have unicode
            boolean bigEndian = (input.charAt(1) == 0xFFFF);
            StringBuilder out = new StringBuilder();
            for (int i = 2; i < input.length(); i += 2)
                if (bigEndian)
                    out.append((char) (((input.charAt(i + 1) & 0xFF) << 8) + (input.charAt(i) & 0xFF)));
                else
                    out.append((char) (((input.charAt(i) & 0xFF) << 8) + (input.charAt(i + 1) & 0xFF)));
            return out.toString();
        } else
            return input;
    }

    protected StreamReader parseLiteral2(StreamReader reader)
    {
        List<Integer> bytes = new LinkedList<>();

        int stack = 1;
        int code = reader.read();

        while (stack > 0)
            switch (code)
            {
                case '\\':
                    code = reader.read();
                    switch (code)
                    {
                        case '\\':
                        case '(':
                        case ')':
                            bytes.add(code);
                            code = reader.read();
                            break;
                    }
                    break;
                case '(':
                    stack++;
                    bytes.add(code);
                    code = reader.read();
                    break;
                case ')':
                    stack--;
                    if (stack > 0)
                    {
                        bytes.add(code);
                        code = reader.read();
                    }
                    break;
                default:
                    bytes.add(code);
                    code = reader.read();
                    break;
            }

        return new StreamReader(new PDFStream(this, Byte.intsToBytes(bytes)));
    }

    private void parseLiteral(StreamReader reader)
    {
        int stack = 1;
        ByteArray originalBuffer = new ByteArray();
        int c = reader.read();
        originalBuffer.add((byte) c);
        while (stack > 0)
            if (c == '\\')
            {
                c = reader.read();
                originalBuffer.add((byte) c);
                if (c == '\r')
                {
                    c = reader.read();
                    originalBuffer.add((byte) c);
                    if (c == '\n')
                    {
                        c = reader.read();
                        originalBuffer.add((byte) c);
                    }
                } else if (c == '\n')
                {// strings on two lines separated by a backslash => if CRLF else if LF
                    c = reader.read();
                    originalBuffer.add((byte) c);
                } else if (isDigit(c))
                {
                    StringBuilder octal = new StringBuilder();
                    octal.append((char) c);
                    c = reader.read();
                    originalBuffer.add((byte) c);
                    if (isDigit(c))
                    {
                        octal.append((char) c);
                        c = reader.read();
                        originalBuffer.add((byte) c);
                        if (isDigit(c))
                        {
                            octal.append((char) c);
                            c = reader.read();
                            originalBuffer.add((byte) c);
                        }
                    }

                    try
                    {
                        codes.append(Integer.valueOf(octal.toString(), 8));
                    } catch (Exception e)
                    {
                        Log.warn(this, ".parseLiteral - parse octal failed " + octal);
                    }

                } else
                {
                    switch (c)
                    {
                        case '(':
                        case ')':
                        case '\\':
                            break;
                        case 'n':
                            c = '\n';
                            break;
                        case 'r':
                            c = '\r';
                            break;
                        case 't':
                            c = '\t';
                            break;
                        case 'b':
                            c = '\b';
                            break;
                        case 'f':
                            c = '\f';
                            break;
                        default:
                            continue;
                    }
                    codes.append((char) c);
                    c = reader.read();
                    originalBuffer.add((byte) c);
                }
            } else // CRLF added as a new line
                if (c == '\r')
                {
                    codes.append('\n');
                    c = reader.read();
                    originalBuffer.add((byte) c);
                    if (c == '\n')
                    {
                        c = reader.read();
                        originalBuffer.add((byte) c);
                    }
                } else // if LF alone automatically added here
                {
                    if (c == '(')
                        stack++;
                    else if (c == ')')
                        stack--;
                    else if (c < 0)
                        stack = 0;

                    if (stack > 0)
                    {
                        codes.append(c);
                        c = reader.read();
                        originalBuffer.add((byte) c);
                    }
                } // end while
        this.originalBuffer = originalBuffer.array();
    }

    private void parseHexadecimal(StreamReader reader)
    {
        ByteArray originalBuffer = new ByteArray();
        this.isHexadecimal = true;
        StringBuilder hexa = new StringBuilder();

        int code;
        while ((code = reader.read()) != '>')
        {
            originalBuffer.add((byte) code);
            if (isHexa(code))
                hexa.append((char) code);
        }

        if (hexa.length() % 2 == 1)
            hexa.append('0');

        for (int i = 0; i < hexa.length(); i += 2)
            try
            {
                code = Integer.valueOf(hexa.substring(i, i + 2), 16);
                codes.append(code);
            } catch (Exception e)
            {
                Log.warn(this, ".parseHexadecimal - strange parse hexadecimal at " + reader.streamReference() + " : " + hexa.substring(i, i + 2));
            }
        this.originalBuffer = originalBuffer.array();
    }

    protected boolean isDigit(int c)
    {
        return c >= '0' && c <= '9';
    }

    protected boolean isHexa(int c)
    {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    public char charAt(int index)
    {
        return codes.charAt(index);
    }

    public char lastCode()
    {
        return codes.charAt(codes.length() - 1);
    }

    @Override
    public String stringValue()
    {
        try
        {
            // 2 bytes -> 1 unicode, first two 0xfeff bytes are use as marker (254,255)
            if (codes.length() > 2 && codes.codeAt(0) == 0xfe && codes.codeAt(1) == 0xff)
            {
                StringBuilder sb = new StringBuilder(codes.length() / 2);
                for (int i = 2; i < codes.length(); i += 2)
                    sb.append((char) (((codes.codeAt(i) & 0xff) << 8) | (codes.codeAt(i + 1) & 0xff)));
                return sb.toString();
            } else
                return new String(intValues(), 0, length());
        } catch (Exception e)
        {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < length(); i++)
                s.append("*");
            return s.toString();
        }
    }

    public byte[] getOriginalBytes()
    {
        return originalBuffer;
    }

    public int[] getOriginalInts()
    {
        int[] bytes = new int[originalBuffer.length];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = originalBuffer[i] & 0xff;
        return bytes;
    }

    public int hexa()
    {
        if (!isHexadecimal)
            Log.warn(this, ".hexa - hexa retrieved from non-hexa string: " + this.stringValue());
        switch (codes.length())
        {
            case 1:
                return codes.codeAt(0);
            case 2:
                return codes.codeAt(0) * 256 + codes.codeAt(1);
            case 3:
                return codes.codeAt(0) * 65536 + codes.codeAt(1) * 256 + codes.codeAt(2);
            case 4:
                return codes.codeAt(0) * 16777216 + codes.codeAt(1) * 65536 + codes.codeAt(2) * 256 + codes.codeAt(3);
        }
        Log.warn(this, ".hexa - hexa code seems quite long: " + codes.stringValue());
        return -1;
    }

    public int[] hexa1B()
    {
        return this.intValues();
    }

    public int[] hexa2B()
    {
        int[] hexa1B = hexa1B();
        if (hexa1B.length <= 1)
            return hexa1B;
        int[] hexa2B = new int[(hexa1B.length + 1) / 2];
        for (int i = 0; i < hexa1B.length; i += 2)
            hexa2B[i / 2] = hexa1B[i] * 256 + (i + 1 < hexa1B.length ? hexa1B[i + 1] : 0);
        return hexa2B;
    }

    public Unicodes codes()
    {
        return this.codes;
    }

    public int codeAt(int index)
    {
        return this.codes.codeAt(index);
    }

    public char[] charValues()
    {
        char[] values = new char[length()];
        for (int i = 0; i < codes.length(); i++)
            values[i] = codes.charAt(i);
        return values;
    }

    @Override
    public int[] intValues()
    {
        int[] values = new int[length()];
        for (int i = 0; i < codes.length(); i++)
            values[i] = codes.codeAt(i);
        return values;
    }

    @Override
    public int intValue()
    {
        int value = 0;
        int factor = 1;
        for (int i = codes.length() - 1; i >= 0; i--)
            value += codes.codeAt(i) * (factor *= 256) / 256;
        return value;
    }

    public byte[] byteValues()
    {
        byte[] byteCodes = new byte[length()];
        for (int i = 0; i < codes.length(); i++)
            byteCodes[i] = (byte) (codes.codeAt(i) & 0xff);
        return byteCodes;
    }

    public ByteBuffer byteBuffer()
    {
        return ByteBuffer.wrap(byteValues());
    }

    public int length()
    {
        return codes.length();
    }

    @Override
    public String toString()
    {
        return stringValue();
    }

    @Override
    public String sticker()
    {
        return nodeNamePrefix() + "[" + stringValue() + "]";
    }

    public boolean isHexadecimal()
    {
        return isHexadecimal;
    }
}
