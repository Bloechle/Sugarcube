package sugarcube.formats.pdf.reader.pdf.node.font.encoding;

import sugarcube.common.data.collections.IdMap;
import sugarcube.common.data.collections.IntMap;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.util.Mapper;

import java.util.Iterator;
import java.util.Map;

public class Encoding extends Glyphlist implements Iterable<Map.Entry<String, Integer>>
{
  public static final String NO_ENCODING = "NoEncoding";
  //StandarEncoding, MacRomanEncoding, etc.
  public final String encoding;
  // maps found in predefined encodings, i.e,. StandardEncoding, MacRomanEncoding, ..., NoEncoding
  public final IdMap<Integer> nameToCode = new IdMap<>("NameToCode", UNDEF);
  public final IntMap<String> codeToName = new IntMap<>("CodeToName", NOTDEF);
  public final Mapper<Integer, Unicodes> codeToUnicode = new Mapper<>("CodeToUnicode", Glyphlist.MISSING_UNICODE);
  // maps found in PDF CMaps and overriding existing values, "_" means "overriding"
  public final IdMap<Integer> nameToCode_ = new IdMap<>("NameToCode_", UNDEF); //difference from encoding
  public final IntMap<String> codeToName_ = new IntMap<>("CodeToName_", NOTDEF); //         idem
  public final Mapper<Integer, Unicodes> codeToUnicode_ = new Mapper<>("CodeToUnicode_", Glyphlist.MISSING_UNICODE);
  public final Set3<Integer> showedCodes = new Set3<>();
  public boolean isCID = false;

  protected Encoding(String encoding)
  {
    this.encoding = encoding;
  }

  protected Encoding(String encoding, PDFObject po)
  {
    this.encoding = encoding;
    if (!this.encoding.equals(NO_ENCODING))
      this.isCID = true; //      byte[] cmapBytes = Resources.loadAsBytes(Resources.DIR_CMAP + encoding);
    //      if (cmapBytes != null)
    //      {
    //        CMapReader.readResourceCMap(new PDFStream(po, cmapBytes), codeToUnicode);
    //        for (Map.Entry<Integer, Unicodes> entry : this.codeToUnicode)
    //        {
    //          int code = entry.getKey();
    //          Unicodes uni = entry.getValue();
    //          String name = UNICODE_TO_NAME.containsKey(uni) ? UNICODE_TO_NAME.get(uni) : uni.stringValue();
    //          this.codeToName.put(code, name);
    //          this.nameToCode.put(name, code);
    //        }
    //      }
  }

  public static Encoding Generate(String encoding, PDFObject po)
  {
    if (encoding.equals("StandardEncoding"))
      return new StandardEncoding();
    else if (encoding.equals("MacRomanEncoding"))
      return new MacRomanEncoding();
    else if (encoding.equals("WinAnsiEncoding"))
      return new WinAnsiEncoding();
    else if (encoding.equals("PDFDocEncoding"))
      return new PDFDocEncoding();
    else if (encoding.equals("MacExpertEncoding"))
      return new ExpertSet();
    else if (encoding.equals("SymbolEncoding"))
      return new SymbolSet();
    else if (encoding.equals("ZapfDingbatsEncoding"))
      return new ZapfDingbatsSet();
    else
      return new Encoding(encoding, po);
  }

  @Override
  public Iterator<Map.Entry<String, Integer>> iterator()
  {
    return nameToCode.entryIterator();
  }

  public boolean is(String encodingName)
  {
    return this.encoding.equals(encodingName);
  }

  public String encoding()
  {
    return encoding;
  }

  public boolean isCID()
  {
    return this.isCID;
  }

  public boolean isIdentityEncoding()
  {
    return (isCID || is("Identity-H") || is("Identity-V"));
  }

  public boolean isHorizontal()
  {
    return this.encoding.endsWith("-H");
  }

  public boolean hasNoEncoding()
  {
    return this.encoding.equals(NO_ENCODING);
  }

  public void add(char character, String name, int code)
  {
    this.nameToCode.put(name, code);
    this.codeToName.put(code, name);
    this.codeToUnicode.put(code, new Unicodes(character));
//    if(code==32)
//      System.out.println("Encoding.add - code="+32+" unicode="+character);
  }

  public String nameFromCode(int code)
  {
    return nameFromCode(code, NAME(new Unicodes(code), NOTDEF));
  }

  public String nameFromCode(int code, String def)
  {
    if (codeToName_.contains(code))
      return codeToName_.get(code);
    else if (codeToName.contains(code))
      return codeToName.get(code);
    else if (codeToUnicode_.containsKey(code) && UNICODE_TO_NAME.containsKey(codeToUnicode_.get(code)))
      return NAME(codeToUnicode_.get(code));
    return def;
  }

  public int codeFromName(String name)
  {
    return codeFromName(name, -1);
  }

  public int codeFromName(String name, int def)
  {
    if (this.nameToCode_.has(name))
      return this.nameToCode_.get(name);
    else if (this.nameToCode.has(name))
      return this.nameToCode.get(name);
    else
      return def;
  }

  public Unicodes unicodeFromName(String name)
  {
    if (name != null && !name.isEmpty())
    {
      if (NAME_TO_UNICODE.containsKey(name))
        return NAME_TO_UNICODE.get(name);
      if (nameToCode.has(name))
      {
        int code = nameToCode.get(name);
        if (codeToUnicode_.containsKey(code))
          return codeToUnicode_.get(code);
      }
      if (name.indexOf('.') > 0)
        return unicodeFromName(name.substring(0, name.indexOf('.')));
      if (name.startsWith("uni"))
        try
        {
          return new Unicodes(Integer.valueOf(name.substring(3, name.length()), 16));
        }
        catch (Exception e)
        {
        }
    }
    return null;
  }

  public Unicodes unicodeFromCode(int code, Unicodes def)
  {
    if (false && code == 32)
    {
      System.out.println("=============================================");
      System.out.println("code=" + code);
      if (this.codeToUnicode_.containsKey(code))
        System.out.println("codeToUnicode_=" + this.codeToUnicode_.get(code).stringValue());
      if (this.codeToUnicode.containsKey(code))
        System.out.println("codeToUnicode=" + this.codeToUnicode.get(code).stringValue());
      String name = null;
      if (this.codeToName_.containsKey(code))
        System.out.println("codeToName_=" + (name = this.codeToName_.get(code)));
      if (this.codeToName.containsKey(code))
        System.out.println("codeToName=" + (name = this.codeToName.get(code)));
      if (name != null && NAME_TO_UNICODE.containsKey(name))
        System.out.println("NAME_TO_UNICODE=" + NAME_TO_UNICODE.get(name).stringValue());
    }

    if (this.codeToName_.contains(code))//added to avoid wrong Unicode CMaps (n=>n, N=>n !?!)
    {
      String name = this.codeToName_.get(code);
      if (NAME_TO_UNICODE.containsKey(name))
        return NAME_TO_UNICODE.get(name);
    }
    else if (this.codeToName.contains(code))//else is mandatory
    {
      String name = this.codeToName.get(code);
      if (NAME_TO_UNICODE.containsKey(name))
        return NAME_TO_UNICODE.get(name);
    }

    if (this.codeToUnicode_.containsKey(code))
      return this.codeToUnicode_.get(code);
    return def;
  }

  public Unicodes unicodeFromCode(int code)
  {
    Unicodes codes = unicodeFromCode(code, null);
    if (codes == null)
    {
      if (this.codeToName_.contains(code))
        codes = unicodeFromName(this.codeToName_.get(code));
      else if (this.codeToName.contains(code))
        codes = unicodeFromName(this.codeToName.get(code));
      if (codes == null && this.codeToUnicode.containsKey(code))
        codes = this.codeToUnicode.get(code);
      if (codes == null)
        codes = new Unicodes(code);
    }
    return codes;
  }

  public Unicodes unicodesFromCodes(int... codes)
  {
    Unicodes unicodes = new Unicodes(codes.length, true);
    for (int code : codes)
      unicodes.append(unicodeFromCode(code));
    return unicodes;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\nEncoding[").append(encoding).append("]");
    sb.append("\nisCID[").append(isCID).append("]");
    sb.append("\nchar\tuni\tcode\tname\n");
    for (int code : this.showedCodes)
    {
      String name = this.nameFromCode(code);
      Unicodes uni = this.unicodeFromCode(code);
      sb.append(uni).append("\t").append("u" + uni.toIntegerString()).append("\t").append(code).append("\t").append(name).append("\n");
    }
    return sb.toString();
  }

  public PDFEncoding pdfEncoding(PDFNode vo)
  {
    return new PDFEncoding(vo);
  }

  public static Encoding NoEncoding()
  {
    return new Encoding(Encoding.NO_ENCODING);
  }

  protected class PDFEncoding extends PDFNode
  {
    public PDFEncoding(PDFNode vo)
    {
      super("Encoding", vo);
    }

    @Override
    public String sticker()
    {
      return "Encoding[" + Encoding.this.encoding + "," + nameToCode.size() + "]";
    }

    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      sb.append(Encoding.this.toString());
      sb.append(nameToCode_);
      sb.append(codeToName_);
      sb.append(codeToUnicode_);
      sb.append(nameToCode);
      sb.append(codeToName);
      return sb.toString();
    }
  }
}
