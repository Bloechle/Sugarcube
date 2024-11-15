package sugarcube.common.data.collections;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Byte;
import sugarcube.common.data.xml.CharRef;
import sugarcube.common.data.xml.Nb;
import sugarcube.formats.ocd.objects.font.SVGFont.Remap;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Unicodes implements Serializable, Cloneable, CharSequence, Iterable<Integer>
{
  public static String SUP_DIGITS = "\u2070\u00B9\u00B2\u00B3\u2074\u2075\u2076\u2077\u2078\u2079";
  
  public final static int EOS = -1;// java stream eos
  public final static char ASCII_NUL = 0;// pdf null
  public final static char ASCII_BACKSPACE = 8;
  public final static char ASCII_TAB = 9;
  public final static char ASCII_LF = 10;// '\n'
  public final static char ASCII_VTAB = 11;
  public final static char ASCII_FF = 12;
  public final static char ASCII_CR = 13;// '\r' keyboard return
  public final static char ASCII_ESC = 27;
  public final static char ASCII_SP = 32;
  public final static char ASCII_DEL = 127;
  public final static char NBSP = 160;
  public final static char BULLET = '\u2022';
  // fontcodes or unicodes (or >0 unicodes & <0 fontcodes)... given the context
  private char[] values = null;
  private int begin = 0; // included
  private int end = 0; // not included

  public Unicodes()
  {
    this.values = new char[0];
  }

  public Unicodes(int capacity, boolean zeroLength)
  {
    this.values = new char[capacity];
    this.begin = 0;
    this.end = zeroLength ? 0 : capacity;
  }

  public Unicodes(int... codes)
  {
    this.set(codes);
  }

  public Unicodes(char[] codes)
  {
    this.set(codes);
  }

  public Unicodes(Collection<Integer> codes)
  {
    this.set(codes);
  }

  public Unicodes(String unicodes)
  {
    this.set(unicodes);
  }

  public Unicodes(CharSequence sequence)
  {
    this(sequence.toString());
  }

  public Unicodes set(Collection<Integer> codes)
  {
    this.values = new char[codes.size()];
    int index = 0;
    for (Integer code : codes)
      this.values[index++] = (char) (int) code;
    this.begin = 0;
    this.end = this.values.length;
    return this;
  }

  public Unicodes set(String unicodes)
  {
    this.values = new char[unicodes.length()];
    this.begin = 0;
    this.end = 0;
    for (int offset = 0; offset < unicodes.length();)
    {
      char codepoint = unicodes.charAt(offset);
      this.ensureCapacity(1);
      this.values[end++] = codepoint;
      offset += Character.charCount(codepoint);
    }
    return this;
  }

  public Unicodes set(int... codes)
  {
    this.values = new char[codes.length];
    for (int i = 0; i < codes.length; i++)
      this.values[i] = (char) codes[i];
    this.begin = 0;
    this.end = this.values.length;
    return this;
  }

  public Unicodes set(char[] codes)
  {
    this.values = new char[codes.length];
    for (int i = 0; i < codes.length; i++)
      this.values[i] = (char) codes[i];
    this.begin = 0;
    this.end = this.values.length;
    return this;
  }

  public Unicodes prepend(int... codes)
  {
    return this.insert(0, codes);
  }

  public Unicodes prepend(char[] codes)
  {
    return this.insert(0, codes);
  }

  public Unicodes prepend(String text)
  {
    return this.prepend(text.toCharArray());
  }

  public Unicodes insert(int index, int... codes)
  {
    int size = codes.length;
    char[] chars = this.characters();
    char[] res = new char[chars.length + size];
    for (int i = 0; i < res.length; i++)
      res[i] = i < index ? chars[i] : (i - index < size ? (char) codes[i - index] : chars[i - size]);
    return this.set(res);
  }

  public Unicodes insert(int index, char[] codes)
  {
    int size = codes.length;
    char[] chars = this.characters();
    char[] res = new char[chars.length + size];
    for (int i = 0; i < res.length; i++)
      res[i] = i < index ? chars[i] : (i - index < size ? codes[i - index] : chars[i - size]);
    return this.set(res);
  }

  public Unicodes delete(int start, int end)
  {
    int length = this.length();
    int deleted = end - start;
    int size = length - deleted;
    char[] chars = characters();
    char[] res = new char[size];
    for (int i = 0; i < start; i++)
      res[i] = chars[i];
    for (int i = end; i < length; i++)
      res[i - deleted] = chars[i];
    return this.set(res);
  }
  
  public boolean isRTL()
  {
    for (int c : this)
      switch (Character.getDirectionality(c))
      {
      case Character.DIRECTIONALITY_RIGHT_TO_LEFT:
      case Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
      case Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING:
      case Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE:
        return false;
      }
    return true;
  }

  public int nbOfSpaces()
  {
    int counter = 0;
    for (int i = begin; i < end; i++)
      if (values[i] == ASCII_SP || values[i] == NBSP)
        counter++;
    return counter;
  }

  public boolean endsWithEOL()
  {
    return this.last() == ASCII_LF || this.last() == ASCII_CR;
  }

  public int begin()
  {
    return this.begin;
  }

  public int end()
  {
    return this.end;
  }

  public char[] values()
  {
    return this.values;
  }

  public Unicodes increment(int value)
  {
    Unicodes unicodes = this.copy();
    for (int i = unicodes.begin; i < unicodes.end; i++)
      unicodes.values[i] += value;
    return unicodes;
  }

  public boolean isEmpty()
  {
    return this.values == null || this.values.length == 0 || begin == end;
  }

  public boolean isWhiteSpace()
  {
    return this.length() == 1 && Character.isWhitespace(this.values[0]);
  }

  @Override
  public int length()
  {
    return end - begin;
  }

  public int capacity()
  {
    return this.values.length;
  }


  public void remove(int index)
  {
    index = index + begin;
    if (index == begin)
      removeFirst();
    else if (index == end - 1)
      removeLast();
    else
    {
      char[] a = new char[end - begin - 1];
      for (int i = begin; i < index; i++)
        a[i - begin] = values[i];
      for (int i = index + 1; i < end; i++)
        a[i - begin - 1] = values[i];
      this.values = a;
      this.begin = 0;
      this.end = values.length;
    }
  }

  public int removeLast()
  {
    if (end > begin)
      end--;
    return values[end < values.length ? end : values.length - 1];
  }

  public int removeFirst()
  {
    if (begin < end)
      begin++;
    return values[begin > 0 ? begin - 1 : 0];
  }

  private void ensureCapacity(int addedSize)
  {
    if (end + addedSize > this.values.length)
    {
      // Log.trace(this, ".ensureCapacity - size: " + this.values.length);
      char[] newCodes = new char[(end - begin) * 2 + addedSize];
      System.arraycopy(values, begin, newCodes, 0, end - begin);
      this.values = newCodes;
      this.end -= this.begin;
      this.begin = 0;
    }
  }

  public Unicodes append(Unicodes codes)
  {
    if (codes != null)
    {
      char[] newCodes = codes.characters();
      this.ensureCapacity(newCodes.length);
      for (int i = 0; i < newCodes.length; i++)
        this.values[end++] = newCodes[i];
    }
    return this;
  }

  public Unicodes append(int... newCodes)
  {
    this.ensureCapacity(newCodes.length);
    for (int i = 0; i < newCodes.length; i++)
      this.values[end++] = (char) newCodes[i];
    return this;
  }

  public Unicodes append(CharSequence sequence)
  {
    this.ensureCapacity(sequence.length());
    for (int i = 0; i < sequence.length(); i++)
      this.values[end++] = sequence.charAt(i);
    return this;
  }

  public int first()
  {
    return begin < values.length && begin < end ? this.values[begin] : -1;
  }

  public int last()
  {
    return end - 1 < values.length && end > begin ? this.values[end - 1] : -1;
  }

  public boolean startWithSpace()
  {
    return this.first() == Unicodes.ASCII_SP;
  }

  public boolean endsWithSpace()
  {
    return this.last() == Unicodes.ASCII_SP;
  }

  public Unicodes split(int index)
  {
    char[] firstCodes = new char[index];
    System.arraycopy(this.values, begin, firstCodes, 0, index);
    this.begin += index;
    return new Unicodes(firstCodes);
  }

  public Unicodes remap(Remap map)
  {
    if (map == null)
      return this;
    char[] remap = new char[length()];
    for (int i = 0; i < remap.length; i++)
      remap[i] = map.get(values[i + begin]);
    return new Unicodes(remap);
  }

  public void set(int index, int code)
  {
    int i = begin + index;
    if (i > -1 && i < values.length)
      this.values[i] = (char) code;
    else
      Log.warn(this, ".setCodeAt - index " + index + " out of bounds: " + this.string() + ">" + (char) code);
  }

  public int codeAt(int index)
  {
    return this.values[begin + index];
  }

  @Override
  public char charAt(int index)
  {
    return (char) codeAt(index);
  }

  public void trim()
  {
    if (begin > 0 || end < values.length)
    {
      values = characters();
      begin = 0;
      end = values.length;
    }
  }
  

  public Unicodes trim(int startIndex, int endIndex)
  {
    this.trim();
    this.begin = startIndex;
    this.end = endIndex;
    this.trim();
    return this;
  }

  public char[] characters()
  {
    if (begin > 0 || end < values.length)
    {
      char[] trimmed = new char[end - begin];
      System.arraycopy(this.values, begin, trimmed, 0, end - begin);
      return trimmed;
    } else
      return this.values;
  }

  public char[] chars(int startIndex, int endIndex)
  {
    char[] trimmed = new char[endIndex - startIndex];
    System.arraycopy(this.values, begin + startIndex, trimmed, 0, endIndex - startIndex);
    return trimmed;
  }

  public String string(int startIndex, int endIndex)
  {
    return new String(chars(startIndex, endIndex));
  }

  public int[] codes()
  {
    return ints();
  }

  public int[] ints()
  {
    int[] unicodes = new int[end - begin];
    int index = 0;
    for (int i = begin; i < end; i++)
      unicodes[index++] = (int) values[i];
    return unicodes;
  }

  public byte[] bytes()
  {
    byte[] bytes = new byte[end - begin];
    for (int i = begin; i < end; i++)
      bytes[i] = (byte) (values[i] & 0xff);
    return bytes;
  }

  // public String hexaCodes()
  // {
  // StringBuilder hexas = new StringBuilder();
  // for (int i = begin; i < end; i++)
  // hexas.append(codes[i] < 0x10 ? '0' + Integer.toHexString(codes[i]) :
  // Integer.toHexString(codes[i]));
  // return hexas.toString();
  // }
  @Override
  public int hashCode()
  {
    int result = 1;
    for (int i = begin; i < end; i++)
      result = 31 * result + values[i];
    return result;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    Unicodes uni = (Unicodes) o;
    int length = this.length();
    if (length != uni.length())
      return false;
    try
    {
      for (int i = 0; i < length; i++)
        if (this.values[this.begin + i] != uni.values[uni.begin + i])
          return false;
    } catch (Exception e)
    {
      Log.warn(this, ".equals - exception: begin=" + begin + ", end=" + end + ", values=" + Arrays.toString(values) + ", uni.begin=" + uni.begin
          + ", uni.end=" + uni.end + ", uni.values=" + Arrays.toString(uni.values));
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public Object clone()
  {
    return copy();
  }

  public Unicodes copy()
  {
    return new Unicodes(characters());
  }

  public int compareTo(Unicodes out)
  {
    return this.string().compareTo(out.string());
  }

  // public boolean isWhiteSpace()
  // {
  // return this.isEmpty() || Character.isWhitespace(this.first());
  // //this.isEmpty() is not really clever, remove it? any side effect?
  // }
  public String stringValue()
  {
    return toString();
  }

  public String string()
  {
    return toString();
  }

  @Override
  public String toString()
  {
    // which is exactly the string itself, never change this (implements
    // CharSequence)
    try
    {
      return new String(values, begin, end - begin);
    } catch (Exception e)
    {
      StringBuilder sb = new StringBuilder();
      for (int i = begin; i < end; i++)
        sb.appendCodePoint(values[i] > 0 ? values[i] : '*');
      return sb.toString();
    }
  }

  public String toIntegerString()
  {
    StringBuilder sb = new StringBuilder();
    for (int i = begin; i < end; i++)
      sb.append((int) values[i]).append(' ');
    if (sb.length() > 0)
      sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  public String toDescriptiveString()
  {
    return this.toString() + " u" + this.toIntegerString();
  }

  @Override
  public CharSequence subSequence(int start, int end)
  {
    return toString().subSequence(start, end);
  }

  public String toCharRef()
  {
    return CharRef.Html(characters());
  }

  public String toCharRef(int start, int end)
  {
    return CharRef.Html(chars(start, end));
  }

  public static Unicodes fromCharRef(String data)
  {
    return new Unicodes(CharRef.UnHtml(data));
  }

  public String toHexaXML()
  {
    StringBuilder sb = new StringBuilder(4 * this.length());
    for (int i = begin; i < end; i++)
      sb.append(Byte.int2hex(values[i])).append(i == end - 1 ? "" : " ");
    return sb.toString();
  }

  public static Unicodes fromHexaXML(String data)
  {
    try
    {
      String[] tokens = data.split("\\s+");
      int[] codes = new int[tokens.length];
      for (int i = 0; i < tokens.length; i++)
        if (tokens[i].isEmpty())
          codes[i] = Unicodes.ASCII_SP;
        else
          codes[i] = Byte.hex2int(tokens[i].trim());
      return new Unicodes(codes);
    } catch (Exception e)
    {
      Log.warn(Unicodes.class, ".fromHexaXML - XML parsing error: text=" + data);
      e.printStackTrace();
    }
    return null;
  }

  public boolean containsCode(int code)
  {
    for (int i = begin; i < end; i++)
      if (values[i] == code)
        return true;
    return false;
  }

  public boolean equalsCodes(int... codes)
  {
    if (this.length() != codes.length)
      return false;
    for (int i = 0; i < codes.length; i++)
      if (this.values[this.begin + i] != codes[i])
        return false;
    return true;
  }

  public boolean are(String codes)
  {
    return this.string().equals(codes);
  }

  public boolean are(int... codes)
  {
    return this.equalsCodes(codes);
  }

  public boolean is(int code)
  {
    return this.equalsCodes(code);
  }

  public int firstSpace(boolean excludeEnds)
  {
    int start = excludeEnds ? begin + 1 : begin;
    int stop = excludeEnds ? end - 1 : end;
    for (int i = start; i < stop; i++)
      if (values[i] == ASCII_SP)
        return i - begin;
    return -1;
  }

  @Override
  public Iterator<Integer> iterator()
  {
    return new Iterator<Integer>()
    {
      int i = begin - 1;

      @Override
      public boolean hasNext()
      {
        return i + 1 < end;
      }

      @Override
      public Integer next()
      {
        return (int) values[++i];
      }

      @Override
      public void remove()
      {
        if (i >= begin && i < end)
        {
          for (int j = i + 1; j < end; j++)
            values[j - 1] = values[j];
          end--;
        }
      }
    };
  }

  public static boolean isCharCode(int c)
  {
    return c >= ASCII_SP && c != ASCII_DEL;
  }
  
  public static String Sup(String text)
  {
    String sup = "";
    for(char c: text.toCharArray())
    {
      sup += SupDigit(Nb.Int(c+"", -1));
    }
    return sup;
  }
  
  public static char SupDigit(int digit)
  {
    if(digit>=0 && digit<=9)
      return SUP_DIGITS.charAt(digit);
    return '\u207A';
  }
}
