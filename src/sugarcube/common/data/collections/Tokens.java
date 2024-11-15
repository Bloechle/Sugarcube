package sugarcube.common.data.collections;

import java.util.Arrays;

public class Tokens
{
  public static final String SPLIT_ONESPACE = "\\s";
  public static final String SPLIT_SPACE = "\\s+";
  public static final String SPLIT_DOT = "\\.";
  public static final String SPLIT_BAR = "\\|";
  public static final String SPLIT_DASH = "-";
  public static final String SPLIT_UNDERSCORE= "_";
  protected int index = 0;
  protected Str[] tokens;

  public Tokens(String... tokens)
  {
    this.tokens = new Str[tokens.length];
    for (int i = 0; i < tokens.length; i++)
      this.tokens[i] = new Str(tokens[i]);
  }

  public Tokens(Str[] tokens)
  {
    this.tokens = tokens;
  }

  public Tokens add(Tokens tks)
  {
    Str[] texts = new Str[tokens.length + tks.tokens.length];
    System.arraycopy(tokens, 0, texts, 0, tokens.length);
    System.arraycopy(tks.tokens, 0, texts, tokens.length, tks.tokens.length);
    this.tokens = texts;
    return this;
  }

  public Tokens trim()
  {
    for (Str text : tokens)
      text.trim();
    return this;
  }

  public float[] floats()
  {
    float[] floats = new float[tokens.length];
    for (int i = 0; i < tokens.length; i++)
      if (tokens[i] != null)
        floats[i] = tokens[i].real();
    return floats;
  }
  
  public int[] ints()
  {
    int[] ints = new int[tokens.length];
    for (int i = 0; i < tokens.length; i++)
      if (tokens[i] != null)
        ints[i] = tokens[i].integer();
    return ints;
  }

  public boolean isFirst(String text)
  {
    return tokens[0].is(text);
  }

  public boolean isSecond(String text)
  {
    return tokens[1].is(text);
  }

  public boolean isThird(String text)
  {
    return tokens[2].is(text);
  }

  public boolean isFourth(String text)
  {
    return tokens[3].is(text);
  }

  public boolean isFifth(String text)
  {
    return tokens[4].is(text);
  }

  public boolean isSixth(String text)
  {
    return tokens[5].is(text);
  }

  public String firstString()
  {
    return this.string(0, "");
  }

  public String secondString()
  {
    return this.string(1, "");
  }

  public String thirdString()
  {
    return this.string(2, "");
  }

  public String firstString(String def)
  {
    return this.string(0, def);
  }

  public String secondString(String def)
  {
    return this.string(1, def);
  }

  public String thirdString(String def)
  {
    return this.string(2, def);
  }

  public Str first()
  {
    return get(0);
  }

  public Str first(String def)
  {
    return get(0, def);
  }

  public Str second()
  {
    return get(1);
  }

  public Str second(String def)
  {
    return get(1, def);
  }

  public Str third()
  {
    return get(2);
  }

  public Str third(String def)
  {
    return get(2, def);
  }

  public Str last()
  {
    return get(tokens.length - 1);
  }

  public Str last(String def)
  {
    return get(tokens.length - 1, def);
  }

  public Str get(int index)
  {
    return get(index, "");
  }

  public Str get(int index, String def)
  {
    return (this.index = index) < tokens.length && tokens[index] != null ? tokens[index] : new Str(def);
  }

  public Str read()
  {
    return read("");
  }

  public String next()
  {
    return read("").data();
  }

  public Str read(String def)
  {
    Str tk = get(index, def);
    index++;
    return tk;
  }

  public String string(int index, String def)
  {
    return get(index, def).data();
  }

  public boolean consume(String value)
  {
    Str tk = get(index, null);
    boolean is = value == null ? (tk == null || tk.data() == null) : value != null && value.equals(tk.data());
    index = is ? index + 1 : index;
    return is;
  }

  public Str[] array()
  {
    return tokens;
  }

  public String[] strings()
  {
    String[] array = new String[tokens.length];
    for (int i = 0; i < array.length; i++)
      array[i] = tokens[i].data();
    return array;
  }

  public int index()
  {
    return index;
  }

  public boolean isSize(int size)
  {
    return tokens.length == size;
  }

  public int size()
  {
    return tokens.length;
  }

  public int rest()
  {
    return tokens.length - index;
  }

  public boolean hasNext()
  {
    return index < tokens.length;
  }

  public Tokens reverse()
  {
    int size = tokens.length;
    for (int i = 0; i < size / 2; i++)
    {
      Str tmp = tokens[i];
      tokens[i] = tokens[size - 1 - i];
      tokens[size - 1 - i] = tmp;
    }
    return this;
  }

  public static String listing(String separator, String... tokens)
  {
    String listing = "";
    for (int i = 0; i < tokens.length; i++)
      listing += tokens[i] + (i == tokens.length - 1 ? "" : separator);
    return listing;
  }

  public static Tokens Split(String data)
  {
    return Split(data, SPLIT_SPACE);
  }

  public static Tokens SplitBar(String data)
  {
    return Split(data, SPLIT_BAR);
  }

  public static Tokens SplitDot(String data)
  {
    return Split(data, SPLIT_DOT);
  }

  public static Tokens SplitDash(String data)
  {
    return Split(data, SPLIT_DASH);
  }
  
  public static Tokens SplitUnderscore(String data)
  {
    return Split(data, SPLIT_UNDERSCORE);
  }

  public static Tokens Split(String data, String splitter)
  {
    return data == null || data.isEmpty() ? new Tokens() : new Tokens(data.trim().split(splitter));
  }

  @Override
  public String toString()
  {
    return Arrays.toString(tokens);
  }

}