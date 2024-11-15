package sugarcube.formats.pdf.writer.document.text.font;

import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.font.SVGGlyph;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.object.Stream;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.document.graphics.GraphicsProducer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;
import java.util.HashMap;

public class CharProcs extends DictionaryObject
{
  private HashMap<String, Integer> glyphs = new HashMap<String, Integer>();
  private ArrayList<String> glyphsOrder = new ArrayList<String>();

  public CharProcs(PDFWriter pdfWriter, SVGFont font) throws PDFException
  {
    super(pdfWriter);
    Stream stream;
    int counter = 0;
    String name;
    StringBuilder sb = new StringBuilder(100 + font.nbOfGlyphs() * 100);
    GraphicsProducer graphicsProducer = pdfWriter.graphicsProducer;
    for (SVGGlyph glyph : font.glyphs())
    {
      sb.delete(0, sb.length());
      stream = new Stream(pdfWriter);
      float width = Util.format(glyph.width());
      sb.append(width + Lexic.SPACE + 0 + Lexic.SPACE + "d0" + Lexic.LINE_FEED);
      graphicsProducer.writeFont(glyph.path(), width, sb);
      stream.write(sb);
      name = "c" + counter++;
      this.glyphs.put(name, stream.getID());
      glyphsOrder.add(name);
    }
    write();
  }

  @Override
  public void addDictionaryEntries()
  {
    for (String name : glyphsOrder)
      addDictionaryEntry(name, glyphs.get(name), Writer.INDIRECT_REFERENCE);
  }

  public String[] getGlyphsNames()
  {
    return glyphsOrder.toArray(new String[0]);
  }

}
