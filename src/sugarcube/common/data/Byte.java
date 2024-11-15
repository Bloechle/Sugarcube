package sugarcube.common.data;

import java.util.List;

public class Byte
{
    public static String int2hex(int value)
    {
        return value < 0 ? "-" + Integer.toHexString(-value) : Integer.toHexString(value);
    }

    public static int hex2int(String value)
    {
        return value.startsWith("-") ? -(int) Long.parseLong(value.substring(1, value.length()), 16) : (int) Long.parseLong(value, 16);
    }

    public static byte[] intsToBytes(List<Integer> ints)
    {
        byte[] bytes = new byte[ints.size()];
        int index = 0;
        for (int v : ints)
            bytes[index++] = (byte) v;
        return bytes;
    }

    /**
     * BigEndian : 0x0A0B0C0D => 0x0A 0x0B 0x0C 0x0D LittleEndian : 0x0A0B0C0D
     * => 0x0D 0x0C 0x0B 0x0A
     */
    public static byte[] intToBytes(int v, boolean bigEndian)
    {
        if (bigEndian)
            return new byte[]
                    {(byte) (v >> 24 & 0xff), (byte) (v >> 16 & 0xff), (byte) (v >> 8 & 0xff), (byte) (v & 0xff)};
        else
            return new byte[]
                    {(byte) (v & 0xff), (byte) (v >> 8 & 0xff), (byte) (v >> 16 & 0xff), (byte) (v >> 24 & 0xff)};
    }

    public static byte[] intToBytesBE(int v)
    {
        return intToBytes(v, true);
    }

    public static byte[] intToBytesLE(int v)
    {
        return intToBytes(v, false);
    }

    public static String bytesToASCII(byte... bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.appendCodePoint(b & 0xff);
        return sb.toString();
    }

    public static String bytesToHexa(byte... bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            String hexa = Integer.toHexString(b & 0xff);
            sb.append(hexa.length() > 1 ? hexa : hexa.length() > 0 ? "0" + hexa : "00");
        }
        return sb.toString();
    }

    /*
     * byte[] bytes = new byte[4]; for (int i = 0; i <bytes.length; i++)
     * bytes[i] = (byte)((value >> (i * 8)) & 0xFF); return bytes;
     */

    public static int bytesToInt(byte[] v)
    {
        return v[3] | v[2] << 8 | v[1] << 16 | v[0] << 24;
    }

    public static byte toByte(int v)
    {
        return (byte) v;
    }

    public static int toInt(byte v)
    {
        return v & 0xff;
    }
}
