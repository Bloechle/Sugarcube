package sugarcube.formats.ocd.objects.handlers;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.io.ZipItem;
import sugarcube.common.ui.gui.Font3;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.svg.SVG;
import sugarcube.common.data.xml.svg.SVGRoot;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.font.SVGFont;

public class OCDFontHandler extends OCDHandler<SVGFont>
{
  public static final String TAG = "fonts";

  public OCDFontHandler(OCDDocument ocd)
  {
    super(TAG, ocd);
  }

  @Override
  public boolean contains(String fontname)
  {
    String norm = SVGFont.Normalize(fontname);
    return map.hasOne(fontname, norm, fontname + ".svg", norm + ".svg");
  }

  @Override
  public SVGFont get(String fontname)
  {
    return this.font(fontname);
  }

  public SVGFont font(String fontname)
  {
    if (fontname == null)
      return null;
    if (fontname.contains("/"))
      fontname = File3.Filename(fontname, true);
    return map.first(fontname, SVGFont.Normalize(fontname));
  }

  public SVGFont needFont(String fontname, String text)
  {
    return needFont(fontname, text, false);
  }

  public SVGFont needFont(String fontname, String text, boolean refont)
  {
    SVGFont font = font(fontname);
    if (refont || font == null || !font.canDisplay(text))
    {
      if (font == null)
        font = newFontEntry(SVGFont.Normalize(fontname));
      if (refont || font.fonter == null)      
        font.fonter = Font3.Seek(fontname, ocd.defaultFont);
    }
    return font;
  }

  public SVGFont createFont(String fontname, String chars)
  {
    SVGFont font = font(fontname);
    if (font == null)
    {
      if (font == null)
        font = newFontEntry(SVGFont.Normalize(fontname));
      if (font.fonter == null)
        font.fonter = Font3.Seek(fontname, ocd.defaultFont);
      font.glyphs(chars, true);
    }
    return font;
  }

  @Override
  public SVGFont add(SVGFont entry)
  {
    String name = entry == null ? null : entry.fontname();
    if (name != null)
    {
      if (map.has(name))
        map.get(name).update(entry);
      else
        map.put(name, entry);
      SVGFont font = map.get(name, entry);
      font.entryPath = File3.Extense(font.entryPath, SVG.EXT);
    }
    // Log.debug(this, ".add - "+fontname);
    return entry;
  }

  @Override
  public SVGFont remove(SVGFont entry)
  {
    return this.map.remove(entry.fontname());
  }

  @Override
  public SVGFont remove(String fontname)
  {
    return this.map.remove(fontname);
  }

  public void addFontEntry(SVGFont font)
  {
    this.add(font);
    if (font.fonter == null)
      font.fonter = Font3.Seek(font.fontname(), null);
  }

  public SVGFont newFontEntry(String fontname)
  {
    SVGFont font = new SVGFont(this);
    font.setFontname(fontname);
    return this.add(font);
  }

  // public void addFontEntry(String fontname)
  // {
  // OCDDocument ocd = this.document();
  // String path = fontname;
  // if (!path.startsWith(OCD.FONTS_DIR))
  // path = OCD.FONTS_DIR + path;
  // if (!path.endsWith(".svg"))
  // path = path + ".svg";
  // ZipFile3 zip = ocd.zipFile();
  // if (zip != null)
  // addFontEntry(zip.entry(path));
  // }
  @Override
  public SVGFont addEntry(ZipItem entry)
  {
    SVGFont font = null;
    if (entry != null)
      try
      {
        SVGRoot svg = new SVGRoot(null);
        IO.Close(Xml.Load(svg, entry.stream()));
        List3<SVGFont> fonts = svg.defs().fonts();
        if (fonts.isEmpty())
          Log.warn(this, ".addFont - svg fonts def is empty: " + entry.path());
        if (fonts.size() > 1)
          Log.info(this, ".addFont - more than one font found in zipEntry: " + entry.path());
        // OCD.LOG.debug(this,".loadFont - font successfully loaded:
        // "+Xml.toString(fonts.first()));
        font = fonts.first();
        if (font != null)
        {
          font.setEntryPath(entry.path());
          this.add(font);
        }
      } catch (Exception ex)
      {
        Log.warn(this, ".addFont - font loading failed: " + entry.toString() + ", " + ex.getMessage());
        ex.printStackTrace();
      }
    return font;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for (SVGFont font : this)
      sb.append(font.filename()).append(", ");
    return "\nOCDFontHandler" + "\n" + "\nFonts[" + sb.toString() + "]" + "";
  }
}
