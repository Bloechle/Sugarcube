package sugarcube.formats.pdf.writer.document.text.font;

import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.HashMap;

public class FontManager
{
  public final static String FONT_SUFFIX = "F";
  private PDFWriter environment;
  private HashMap<String, Font> map = new HashMap<String, Font>();

  public FontManager(PDFWriter environment)
  {
    this.environment = environment;
  }

  public Font resolveFontID(String fontName) throws PDFException
  {
    if (map.containsKey(fontName))
      return map.get(fontName);
    SVGFont font = environment.getDocument().fontHandler.font(fontName);
    Font newFont = new Font(environment, font);
    map.put(fontName, newFont);
    return newFont;
  }

  public void dispose()
  {
    this.map.clear();
  }
}
