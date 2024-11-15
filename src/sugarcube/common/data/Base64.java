package sugarcube.common.data;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public class Base64
{
  public static final String UTF8 = "UTF-8";
  
  public char plus = '+';
  public char slash = '/';
  public char equal = '=';

  public Base64()
  {
  }

  public Base64(char plus, char slash)
  {
    this.plus = plus;
    this.slash = slash;
  }

  public Base64(char plus, char slash, char equal)
  {
    this.plus = plus;
    this.slash = slash;
    this.equal = equal;
  }

  public String encode(String text)
  {
    try
    {
      return encode(text.getBytes(UTF8), -1);
    } catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    return text;
  }

  public String decodeString(String base64)
  {
    try
    {
      return new String(decode(base64), UTF8);
    } catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    return base64;
  }

  public byte[] decode(String base64)
  {

    // strip whitespace from anywhere in the string. Not the most memory
    // efficient solution but elegant anyway :-)
    StringTokenizer tok = new StringTokenizer(base64, " \n\r\t", false);
    StringBuffer buf = new StringBuffer(base64.length());

    while (tok.hasMoreElements())
    {
      buf.append(tok.nextToken());
    }

    base64 = buf.toString();

    int pad = 0;

    for (int i = base64.length() - 1; (i > 0) && (base64.charAt(i) == '='); i--)
    {
      pad++;
    }

    int length = base64.length() / 4 * 3 - pad;
    byte[] raw = new byte[length];

    for (int i = 0, rawIndex = 0; i < base64.length(); i += 4, rawIndex += 3)
    {
      int block = (getValue(base64.charAt(i)) << 18) + (getValue(base64.charAt(i + 1)) << 12) + (getValue(base64.charAt(i + 2)) << 6)
          + (getValue(base64.charAt(i + 3)));

      for (int j = 2; j >= 0; j--)
      {
        if (rawIndex + j < raw.length)
        {
          raw[rawIndex + j] = (byte) (block & 0xff);
        }

        block >>= 8;
      }
    }

    return raw;
  }

  public String encode(byte[] raw)
  {
    return encode(raw, false);
  }

  public String encode(byte[] raw, boolean wrap)
  {
    return encode(raw, wrap ? 76 : -1);
  }

  /**
   * <p>
   * Encode a byte array in Base64 format and return an optionally wrapped line
   * </p>
   * 
   * @param raw
   *          <code>byte[]</code> data to be encoded
   * @param wrap
   *          <code>int<code> length of wrapped lines; No wrapping if less than 4.
   * @return a <code>String</code> with encoded data
   */
  public String encode(byte[] raw, int wrap)
  {

    // calculate length of encoded string
    int encLen = ((raw.length + 2) / 3) * 4;

    // adjust for newlines
    if (wrap > 3)
    {
      wrap -= wrap % 4;
      encLen += 2 * (encLen / wrap);
    } else
    { // disable wrapping
      wrap = Integer.MAX_VALUE;
    }

    StringBuffer encoded = new StringBuffer(encLen);
    int len3 = (raw.length / 3) * 3;
    int outLen = 0; // length of output line

    for (int i = 0; i < len3; i += 3, outLen += 4)
    {
      if (outLen + 4 > wrap)
      {
        encoded.append("\r\n");

        outLen = 0;
      }

      encoded.append(encodeFullBlock(raw, i));
    }

    if (outLen >= wrap)
    { // this will produce an extra newline if needed !? Sun had it this way...
      encoded.append("\r\n");
    }

    if (len3 < raw.length)
    {
      encoded.append(encodeBlock(raw, len3));
    }

    return encoded.toString();
  }

  public byte[] decode(BufferedReader reader) throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String line;
    while (null != (line = reader.readLine()))
    {
      byte[] bytes = decode(line);
      baos.write(bytes);
    }
    return baos.toByteArray();
  }

  protected char[] encodeBlock(byte[] raw, int offset)
  {
    int block = 0;
    int slack = raw.length - offset - 1;
    int end = (slack >= 2) ? 2 : slack;

    for (int i = 0; i < 3; i++)
    {
      byte b = (offset + i < raw.length) ? raw[offset + i] : 0;
      int neuter = (b < 0) ? b + 256 : b;

      block <<= 8;
      block += neuter;
    }

    char[] base64 = new char[4];

    for (int i = 3; i >= 0; i--)
    {
      int sixBit = block & 0x3f;

      base64[i] = getChar(sixBit);
      block >>= 6;
    }

    if (slack < 1)
    {
      base64[2] = equal;
    }

    if (slack < 2)
    {
      base64[3] = equal;
    }

    return base64;
  }

  protected char[] encodeFullBlock(byte[] raw, int offset)
  {
    int block = 0;

    for (int i = 0; i < 3; i++)
    {

      // byte b = raw[offset + i];
      // int neuter = (b < 0) ? b + 256 : b;
      block <<= 8;
      block += (0xff & raw[offset + i]);
    }

    block = ((raw[offset] & 0xff) << 16) + ((raw[offset + 1] & 0xff) << 8) + (raw[offset + 2] & 0xff);

    char[] base64 = new char[4];

    for (int i = 3; i >= 0; i--)
    {
      int sixBit = block & 0x3f;

      base64[i] = getChar(sixBit);
      block >>= 6;
    }

    return base64;
  }

  protected char getChar(int sixBit)
  {

    if ((sixBit >= 0) && (sixBit < 26))
    {
      return (char) ('A' + sixBit);
    }

    if ((sixBit >= 26) && (sixBit < 52))
    {
      return (char) ('a' + (sixBit - 26));
    }

    if ((sixBit >= 52) && (sixBit < 62))
    {
      return (char) ('0' + (sixBit - 52));
    }

    if (sixBit == 62)
    {
      return plus;
    }

    if (sixBit == 63)
    {
      return slash;
    }

    return '?';
  }

  protected int getValue(char c)
  {

    if ((c >= 'A') && (c <= 'Z'))
    {
      return c - 'A';
    }

    if ((c >= 'a') && (c <= 'z'))
    {
      return c - 'a' + 26;
    }

    if ((c >= '0') && (c <= '9'))
    {
      return c - '0' + 52;
    }

    if (c == plus)
    {
      return 62;
    }

    if (c == slash)
    {
      return 63;
    }

    if (c == equal)
    {
      return 0;
    }

    return -1;
  }

  public static Base64 file()
  {
    return new Base64('-', '_');
  }

  public static Base64 url()
  {
    return new Base64();
  }

  public static String encodeUrl(String text)
  {
    return url().encode(text);
  }

  public static String encodeUrl(byte[] data)
  {
    return url().encode(data);
  }

  public static byte[] decodeUrl(String base64)
  {
    return url().decode(base64);
  }

  public static String decodeUrlString(String base64)
  {
    return url().decodeString(base64);
  }

  public static String encodeFile(String text)
  {
    return file().encode(text);
  }

  public static String encodeFile(byte[] data)
  {
    return file().encode(data);
  }

  public static byte[] decodeFile(String base64)
  {
    return file().decode(base64);
  }
  
  public static String decodeFileString(String base64)
  {
    return file().decodeString(base64);
  }

  public static String encodeImage(byte[] data, String type)
  {
    return "data:image/" + type + ";base64," + encodeUrl(data);
  }

  public static void main(String... args)
  {
    Log.debug(Base64.class, "\n" + encodeImage(File3.Get("E:/a.png").bytes(), "png") + "\n");
  }
}