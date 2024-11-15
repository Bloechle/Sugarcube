package sugarcube.formats.pdf.writer.document.text.font;

import sugarcube.common.system.log.Log;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.font.SVGGlyph;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.object.Stream;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Font extends DictionaryObject
{
  private static boolean FONT_MSG = true;
  private SVGFont font;
  private int widthArrayID;
  /** private int fontDescriptorID; */
  private int charProcsID;
  private int encodingID;
  private int toUnicodeID;
  private int firstChar;
  private int lastChar;
  private HashMap<Integer, Integer> codesMap = new HashMap<Integer, Integer>();
  private LinkedHashMap<Integer, SVGGlyph> codeToGlyphMap;

  public Font(PDFWriter environment, SVGFont font) throws PDFException
  {
    super(environment);
    this.font = font;
    SVGGlyph[] glyphs = font.glyphs().toArray(new SVGGlyph[0]);
    int nbOfGlyphs = glyphs.length;
    // get codes
    ArrayList<Integer> codesList = new ArrayList<Integer>();
    codeToGlyphMap = new LinkedHashMap<Integer, SVGGlyph>();
  
    for (int g = 0; g < nbOfGlyphs && g<256; g++)
    {
      codesList.add(glyphs[g].code);
      codeToGlyphMap.put(glyphs[g].code, glyphs[g]);
    }
    int[] codes = new int[codesList.size()];
    int[] mappedCodes = new int[codes.length];
    // get codes and try to map them
    for (int g = 0; g < codesList.size(); g++)
    {
      codes[g] = codesList.get(g);
      mappedCodes[g] = map(codes[g]);
    }
    // calculate widths information
    for (int g = 0; g < mappedCodes.length; g++)
    {
      if (g == 0)
        firstChar = lastChar = mappedCodes[0];
      else if (mappedCodes[g] < firstChar)
        firstChar = mappedCodes[g];
      else if (mappedCodes[g] > lastChar)
        lastChar = mappedCodes[g];
    }
    // get widths
    Float[] widths = new Float[lastChar - firstChar + 1];
    for (int g = 0; g < codes.length; g++)
    {
      widths[mappedCodes[g] - firstChar] = codeToGlyphMap.get(codes[g]).width();
    }
    // save PDF objects
    widthArrayID = new WidthsArray(environment, widths).getID();
    /** fontDescriptorID = new FontDescriptor(environment, font).getID(); */
    CharProcs charProcs = new CharProcs(environment, font);
    charProcsID = charProcs.getID();
    encodingID = new Encoding(environment, charProcs.getGlyphsNames(), mappedCodes).getID();
    Stream toUnicode = new Stream(environment);
    toUnicodeID = toUnicode.getID();
    toUnicode.write(createUnicodesMap(FontManager.FONT_SUFFIX + getID(), glyphs));
    write();
  }

  public float getWidth(int code)
  {
    SVGGlyph glyph = codeToGlyphMap.get(code);
    if (glyph == null)
    {
      return font.missingGlyph.bounds().floatWidth();
    }
    return glyph.width();
  }

  @Override
  public void addDictionaryEntries()
  {
    addDictionaryEntry("Type", "Font", Writer.NAME);
    addDictionaryEntry("Subtype", "Type3", Writer.NAME);
    addDictionaryEntry("Name", font.fontname().replaceAll(" ", "_"), Writer.NAME);
    Float[] bounds = Util.rectangleToFloatArray(font.computeGlyphsBbox(), .001f, true);
    for (int b = 0; b < bounds.length; b++)
      bounds[b] = Util.format(bounds[b]);
    addDictionaryEntry("FontBBox", bounds, Writer.REAL_ARRAY);
    addDictionaryEntry("FontMatrix", new Float[]
    { 1f, 0f, 0f, -1f, 0f, 0f }, Writer.REAL_ARRAY);
    addDictionaryEntry("CharProcs", charProcsID, Writer.INDIRECT_REFERENCE);
    addDictionaryEntry("Encoding", encodingID, Writer.INDIRECT_REFERENCE);
    addDictionaryEntry("FirstChar", firstChar, Writer.INTEGER);
    addDictionaryEntry("LastChar", lastChar, Writer.INTEGER);
    addDictionaryEntry("Widths", widthArrayID, Writer.INDIRECT_REFERENCE);
    /**
     * addDictionaryEntry("FontDescriptor", fontDescriptorID,
     * Writer.INDIRECT_REFERENCE);
     */
    addDictionaryEntry("ToUnicode", toUnicodeID, Writer.INDIRECT_REFERENCE);

    if (FONT_MSG)
    {
      Log.debug(this, ".addDictionaryEntries - font descriptor ignored");
      FONT_MSG = false;
    }
  }

  private final int map(int code) throws PDFException
  {
    int counter = 255;
    while (codesMap.containsValue(counter))
      counter--;
    if (counter < 0)
    {
      counter = codesMap.size();
      Log.warn(this, ".map - assigning a code greater than 255 to a character: " +counter+", code="+ code + "(" + (char) code + ")");

    }
    codesMap.put(code, counter);
    return counter;
  }

  public int getMappedCode(int code)
  {
    if (codesMap.containsKey(code))
      return codesMap.get(code);
    return 32;
  }

  private StringBuilder createUnicodesMap(String id, SVGGlyph[] glyphs)
  {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(name("CIDInit") + Lexic.LINE_FEED);
    stringBuilder.append(name("ProcSet") + Lexic.SPACE + "findresource" + Lexic.SPACE + "begin" + Lexic.LINE_FEED);
    stringBuilder.append("12" + Lexic.SPACE + "dict" + Lexic.SPACE + "begin" + Lexic.LINE_FEED);
    stringBuilder.append("begincmap" + Lexic.LINE_FEED);
    stringBuilder.append(name("CIDSystemInfo") + Lexic.LINE_FEED);
    stringBuilder.append(Lexic.LESS_THAN + Lexic.LESS_THAN + Lexic.LINE_FEED);
    stringBuilder.append(name("Registry") + Lexic.SPACE + "(" + id + ")" + Lexic.LINE_FEED);
    stringBuilder.append(name("Ordering") + Lexic.SPACE + "(UCS)" + Lexic.LINE_FEED);
    stringBuilder.append(name("Supplement") + Lexic.SPACE + 0 + Lexic.LINE_FEED);
    stringBuilder.append(Lexic.GREATER_THAN + Lexic.GREATER_THAN + Lexic.SPACE + "def" + Lexic.LINE_FEED);
    stringBuilder.append(name("CMapName") + Lexic.SPACE + name(id) + Lexic.SPACE + "def" + Lexic.LINE_FEED);
    stringBuilder.append(name("CMapType") + Lexic.SPACE + "2" + Lexic.SPACE + "def" + Lexic.LINE_FEED);
    // codes space range
    stringBuilder.append("1 begincodespacerange" + Lexic.SPACE);
    stringBuilder.append("<0000> <FFFF>" + Lexic.SPACE);
    stringBuilder.append("endcodespacerange" + Lexic.LINE_FEED);
    String codes = "";
    int nbOfCodes = 0;
  
    String CIDvalue;
    String unicodeValue;
    for (int g = 0; g < glyphs.length; g++)
    {
      CIDvalue = Integer.toHexString(getMappedCode(glyphs[g].code));
      unicodeValue = Integer.toHexString(glyphs[g].code);
      while (unicodeValue.length() < 4)
        unicodeValue = '0' + unicodeValue;
      codes += Lexic.LESS_THAN + CIDvalue + Lexic.GREATER_THAN + Lexic.SPACE + Lexic.LESS_THAN + unicodeValue + Lexic.GREATER_THAN + Lexic.LINE_FEED;
      nbOfCodes++;
    }
    stringBuilder.append(nbOfCodes + Lexic.SPACE + "beginbfchar" + Lexic.LINE_FEED + codes + "endbfchar" + Lexic.LINE_FEED);
    stringBuilder.append("endcmap" + Lexic.LINE_FEED);
    stringBuilder.append("CMapName" + Lexic.SPACE + "currentdict" + Lexic.SPACE + name("CMap") + Lexic.SPACE + "defineresource" + Lexic.SPACE + "pop"
        + Lexic.LINE_FEED);
    stringBuilder.append("end" + Lexic.LINE_FEED);
    stringBuilder.append("end" + Lexic.LINE_FEED);
    return stringBuilder;
  }

  private final String name(String value)
  {
    return Lexic.SOLIDUS + value;
  }
}
