package sugarcube.common.data;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.ByteArray;

import java.io.UnsupportedEncodingException;

public class Base
{  
  public static final Base x64 = new Base("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_");
  public static final Base x32 = new Base("0123456789abcdefghkmnpqrstuvwxyz");
  public static final Base x24 = new Base("abcdefghijkmnpqrstuvwxyz");
  public static final Base x16 = new Base("0123456789abcdef");
  public static final Base x10 = new Base("0123456789");
  private int[] index = new int[128];// ascii chars
  private char[] base;
  private char zero;
  private int size;

  public Base(String baseChars)
  {
    this.base = baseChars.toCharArray();
    this.zero = base[0];
    this.size = base.length;
    for (int i = 0; i < base.length; i++)
      index[base[i]] = i;
  }

  public static String encode16(String data)
  {
    try
    {
      return encode16(data.getBytes(Base64.UTF8));
    } catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    return data;
  }
  
  public static String encode16(byte[] data)
  {
    StringBuilder sb = new StringBuilder(data.length);
    for (byte b : data)
    {
      sb.append(x16.base[((b >> 4) & 0xf)]).append(x16.base[b & 0xf]);
    }
    return sb.toString();
  }

  public static byte[] decode16(String data)
  {
    ByteArray bytes = new ByteArray(data.length());
    for (int i = 0; i < data.length(); i += 2)
    {
      bytes.add((byte) (((x16.index[data.charAt(i)] << 4) | x16.index[data.charAt(i + 1)]) & 0xff));
    }
    return bytes.array();
  }

  public static String decode16(String data, boolean decode)
  {
    if (decode)
    {
      byte[] bytes = decode16(data);
      try
      {
        return new String(bytes, Base64.UTF8);
      } catch (UnsupportedEncodingException e)
      {
        e.printStackTrace();
      }
    }
    return data;
  }

  public String get(long value)
  {
    boolean negative = value < 0;
    if (negative)
      value = Math.abs(value);
    String res = "";
    while (value > 0)
    {
      int r = (int) (value % size);
      res = base[r] + res;
      value /= size;
    }
    return res.isEmpty() ? "" + zero : negative ? zero + res : res;
  }

  public String random6()
  {
    return random(6);
  }

  public String random8()
  {
    return random(8);
  }

  public String random12()
  {
    return random(12);
  }

  public String random16()
  {
    return random(16);
  }
  
  public String random32()
  {
    return random(32);
  }
  
  public String random64()
  {
    return random(64);
  }   
  
  public String random128()
  {
    return random(128);
  }   
  
  public String random256()
  {
    return random(256);
  }    
  

  public String random(int length)
  {
    StringBuilder sb = new StringBuilder();
    while (length-- > 0)
      sb.append(base[(int) (size * Math.random())]);
    return sb.toString();
  }

  public long parse(String s)
  {
    boolean negative = s.length() > 1 && s.charAt(0) == zero;
    long value = 0;
    long factor = 1;
    for (int i = s.length() - 1; i >= 0; i--)
    {
      value += index[s.charAt(i)] * factor;
      factor *= size;
    }
    return negative ? -value : value;
  }

  public String toString(long value)
  {
    return get(value);
  }

  public static void main(String... args) throws Exception
  {
    // Base base = Base.x64;
    // long value = 0xFFFFFFFFFFFFFFFFL;
    // Log.debug(base, " - value=" + value + ", base=" + base.get(value) +
    // ", parse=" + base.parse(base.get(value)));

    byte[] data = "Oh là là c'est pô facile".getBytes(Base64.UTF8);
    String encoded = encode16(data);

    Log.debug(Base.class, "- bytes=" + Zen.Array.String(data));
    Log.debug(Base.class, "- encoded=" + encoded);
    Log.debug(Base.class, "- bytes=" + Zen.Array.String(decode16(encoded)));
  }
}