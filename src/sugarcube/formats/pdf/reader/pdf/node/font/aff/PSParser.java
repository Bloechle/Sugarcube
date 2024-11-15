package sugarcube.formats.pdf.reader.pdf.node.font.aff;

public class PSParser
{
    byte[] data;
    int loc;

    public PSParser(byte[] data, int start)
    {
        this.data = data;
        this.loc = start;
    }

    public String read()
    {
        while (IsWhiteSpace(this.data[this.loc]))
            this.loc += 1;
        int start = this.loc;
        while (!IsWhiteSpace(this.data[this.loc]))
        {
            this.loc += 1;
            if (!IsCharacter(this.data[this.loc]))
                break;
        }
        return new String(this.data, start, this.loc - start);
    }

    public float[] readArray(int count)
    {
        float[] ary = new float[count];
        int idx = 0;
        while (idx < count)
        {
            String token = read();
            if (token.charAt(0) == '[')
                token = token.substring(1);
            if (token.endsWith("]"))
                token = token.substring(0, token.length() - 1);
            if (token.length() > 0)
                ary[(idx++)] = Float.valueOf(token).floatValue();
        }
        return ary;
    }

    public byte[] getNEncodedBytes(int n, int key, int skip)
    {
        byte[] result = AFF.Decrypt(this.data, this.loc, this.loc + n, key, skip);
        this.loc += n;
        return result;
    }


    public static boolean IsWhiteSpace(int c)
    {
        switch (c)
        {
            case 0:
            case 9:
            case 10:
            case 12:
            case 13:
            case 32:
                return true;
        }
        return false;
    }

    private static boolean IsDelimiter(int c)
    {
        switch (c)
        {
            case 37:
            case 40:
            case 41:
            case 47:
            case 60:
            case 62:
            case 91:
            case 93:
            case 123:
            case 125:
                return true;
        }
        return false;
    }

    public static boolean IsCharacter(int c)
    {
        return (!IsWhiteSpace(c)) && (!IsDelimiter(c));
    }
}
