package sugarcube.formats.pdf.resources.fontmapping;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CharMap
{
  public interface Listener
  {
    public void selectCodes(Codes codes);
  }
  
  public class Codes
  {
    public String unicode;
    public String html;
    public String htmlNum;

    public Codes(String unicode, String html, String htmlNum)
    {
      this.unicode = unicode;
      this.html = html;
      this.htmlNum = htmlNum;
    }

  }

  public static final String[] FILES = new String[]
  { "basic_latin", "latin_1", "latin_extended_a", "latin_extended_b", "currency", "letterlike", "number_symbols", "arrow", "mathematical_operator",
      "greek", "math", "math_super", "math_sub", "fractions" };
  public static final String[] NAMES = new String[]
  { "Basic Latin", "Latin 1", "Latin Extended-A", "Latin Extended-B", "Currency Symbols", "Letterlike Symbols", "Number Symbols", "Arrows",
      "Mathematical Operators", "Greek", "Mathematics general", "Mathematics superscript", "Mathematics subscript", "Mathematics fractions" };
  private static final char EOF = (char) -1;
  private final static char NEW_LINE = (char) 10;
  private final static char CARRIAGE_RETURN = (char) 13;
  private final static char SPACE = (char) 32;
  private static final int BUFFER_SIZE = 1024;
  private InputStreamReader inputStream;
  private char[] buffer;
  private int currentByte = BUFFER_SIZE;
  private int currentBufferSize = 0;
  private ArrayList<String> codes = new ArrayList<String>();
  private ArrayList<String> htmlCodes = new ArrayList<String>();
  private ArrayList<String> htmlNumericCodes = new ArrayList<String>();
  private Codes[] codings;

  public CharMap(String filename) throws Exception
  {
    loadMap(filename);
  }

  public Codes[] codes()
  {
    return codings;
  }

  public ArrayList<String> getCodes()
  {
    return codes;
  }

  public ArrayList<String> getHTMLCodes()
  {
    return htmlCodes;
  }

  public ArrayList<String> getHTMLNumericCodes()
  {
    return htmlNumericCodes;
  }

  private void loadMap(String filename) throws Exception
  {
    List3<Codes> list = new List3<>();
    if (!filename.endsWith(".map"))
      filename += ".map";
    inputStream = new InputStreamReader(getClass().getResourceAsStream(filename), "UTF-8");
    String code = null;
    String htmlCode = null;
    String htmlNumericCode = null;
    buffer = new char[BUFFER_SIZE];
    String token = "";
    char c;
    while ((c = read()) != EOF)
    {
      if (c == SPACE)
      {
        if (code == null)
        {
          code = token;
        } else
          htmlCode = token;
        token = "";
      } else if ((c == CARRIAGE_RETURN || c == NEW_LINE) && token.length() > 0)
      {
        htmlNumericCode = token;
        token = "";
        codes.add(code);
        htmlCodes.add(htmlCode);
        htmlNumericCodes.add(htmlNumericCode);
        list.add(new Codes(code, htmlCode, htmlNumericCode));
        // System.out.println(code + " / " + htmlCode + " / " +
        // htmlNumericCode);
        code = null;
      } else if (c != NEW_LINE && c != CARRIAGE_RETURN)
        token += c;
    }
    if (token.length() > 0)
    {
      htmlNumericCode = token;
      token = "";
      codes.add(code);
      htmlCodes.add(htmlCode);
      htmlNumericCodes.add(htmlNumericCode);
      list.add(new Codes(code, htmlCode, htmlNumericCode));

      // System.out.println(code + " / " + htmlCode + " / " + htmlNumericCode);

      code = null;
    }
    inputStream = null;
    buffer = null;
    codings = list.toArray(new Codes[0]);
  }

  private char read() throws IOException
  {
    if (currentBufferSize < BUFFER_SIZE && currentByte == currentBufferSize)
      return EOF;
    if (currentByte == BUFFER_SIZE)
    {
      currentBufferSize = inputStream.read(buffer);
      if (currentBufferSize == -1)
        return EOF;
      currentByte = 0;
    }
    return buffer[currentByte++];
  }

  public static CharMap[] loadAll()
  {
    CharMap[] maps = new CharMap[CharMap.FILES.length];
    for (int m = 0; m < CharMap.FILES.length; m++)
      try
      {
        maps[m] = new CharMap(CharMap.FILES[m]);
      } catch (Exception e)
      {
        Log.debug(CharMap.class, ".loadAll - exception: " + e.getMessage());
      }
    return maps;
  }
}
