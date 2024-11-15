package sugarcube.formats.ocd.objects.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.*;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.ui.gui.Font3;
import sugarcube.common.data.xml.*;
import sugarcube.common.data.xml.svg.SVG;
import sugarcube.common.data.xml.svg.SVGRoot;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDEntry;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.document.OCDItem;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Character.UnicodeBlock;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SVGFont extends OCDEntry implements Iterable<SVGGlyph>
{
  public static final String TAG = "font";
  public static final String EXT = ".svg";

  public static class Remap extends Map3<Integer, Integer>
  {
    public boolean has(char code)
    {
      return this.has((int) code);
    }

    public char get(char code)
    {
      return get(code, code);
    }

    public char get(char code, char recover)
    {
      return (char) (int) super.get((int) code, (int) recover);
    }
  }

  public static Path3 DUMMY = null;
  public static final String STYLE_NORMAL = "normal";
  public static final String STYLE_ITALIC = "italic";
  public static final String STYLE_OBLIQUE = "oblique";
  public static final String WEIGHT_NORMAL = "normal";
  public static final String WEIGHT_BOLD = "bold";
  public static final String[] WEIGHTS =
  { WEIGHT_NORMAL, WEIGHT_BOLD };
  public static final String[] STYLES =
  { STYLE_NORMAL, STYLE_ITALIC, STYLE_OBLIQUE };
  public static final float DEFAULT_ASCENT = 0.75f;
  public static final float DEFAULT_DESCENT = -0.25f;
  // never modify these constants
  public static final String _BI = "_BoldItalic";
  public static final String _B = "_Bold";
  public static final String _I = "_Italic";
  public static int CELL_SIZE = 80;
  // temp map PDF codes to Unicodes in Dexter and codes to
  // new codes in EPUBWriter
  public transient Remap remap = new Remap();
  // generating font on demand
  public transient Font3 fonter = null;
  //
  public SVGFontFace fontFace;
  public SVGMissingGlyph missingGlyph;
  private GlyphTree glyphs = new GlyphTree();
  public float unitsPerEm = 1000f;
  public float ascent = DEFAULT_ASCENT;
  public float descent = DEFAULT_DESCENT;
  public String fontFamily = "";
  public String fontStyle = "normal"; // normal | italic | oblique
  public String fontWeight = "normal"; // normal | bold |100 | 200 | 300 | 400 |
                                       // 500 | 600 | 700 | 800 | 900
  public String replacement = null;

  public SVGFont()
  {
    this((OCDNode) null);
  }

  public SVGFont(OCDNode parent)
  {
    super(TAG, parent, OCD.FONTS_DIR);
    this.fontFace = new SVGFontFace(this);
    this.missingGlyph = new SVGMissingGlyph(this);
  }

  public SVGFont(Font3 fonter)
  {
    this();
    this.fontFamily = fonter.getFontName();
    this.fonter = fonter;
  }

  public SVGFont fonter(Font3 fonter)
  {
    this.fonter = fonter;
    return this;
  }

  public float ascent(float max)
  {
    return ascent > max ? max : ascent;
  }

  public float descent(float min)
  {
    return descent < min ? min : descent;
  }

  public boolean isReplacement()
  {
    return replacement == null || replacement.isEmpty();
  }

  @Override
  public OCDItem item()
  {
    return new OCDItem(entryPath, OCDItem.TYPE_FONT, "total", "" + glyphs.size());
  }

  public static SVGFont load(String directory, String filename)
  {
    return load(File3.directorize(directory) + filename);
  }

  public static SVGFont load(File3 file)
  {
    InputStream stream = null;
    try
    {
      if (!file.exists())
        // Log.debug(SVGFont.class,
        // ".load(File3 file) - file not found: "+file.path());
        return null;
      boolean zipped = file.isExtension(SVG.EXTZ);
      if (zipped)
      {
        ZipFile zip = new ZipFile(file);
        // Log.debug(SVGFont.class, ".load(File3 file) - zip fontname=" +
        // file.path());
        ZipEntry entry = zip.getEntry(file.extense(SVG.EXT).name());
        if (entry == null)
          return null;
        else
          stream = new BufferedInputStream(zip.getInputStream(entry));
      } else
        stream = file.inputStream();
      if (stream == null)
        return null;
      SVGFont font = read(stream);
      if (font != null)
        font.setEntryPath(OCD.FONTS_DIR + file.name());
      return font;
    } catch (Exception e)
    {
      Log.warn(SVGFont.class, ".load - font reading exception: " + e.getMessage());
      e.printStackTrace();
    } finally
    {
      IO.Close(stream);
    }
    return null;
  }

  public static SVGFont load(String path)
  {
    if (!File3.HasExtension(path, SVG.EXTZ, SVG.EXT))
      if (!File3.Exists(path = File3.Extense(path, SVG.EXTZ)))
        path = File3.Extense(path, SVG.EXT);
    return load(new File3(path));
  }

  public static SVGFont load(Class classPath, String filename)
  {
    InputStream stream = null;
    try
    {
      // todo swap resource to tmp file when zipped in order to read the zip
      // entry correctly...
      stream = Class3.Stream(classPath, filename);
      if (stream == null)
        stream = Class3.Stream(classPath, filename = File3.Extense(filename, SVG.EXT));
      if (stream == null)
        stream = Class3.Stream(classPath, filename = File3.Extense(filename, SVG.EXTZ));

      if (stream == null)
        return null;
      if (filename.endsWith(SVG.EXTZ))
      {
        File3 file = File3.TempFile(filename, stream, true);
        // Log.debug(SVGFont.class, ".load - createTempFile: " + file.path());
        SVGFont font = load(file);
        if (font != null)
          font.setEntryPath(OCD.FONTS_DIR + filename);
        // Log.debug(SVGFont.class,".load - font: "+font);
        file.delete();
        return font;
      } else if (filename.endsWith(SVG.EXT))
      {
        SVGFont font = read(stream);
        if (font != null)
          font.setEntryPath(OCD.FONTS_DIR + filename);
        return font;
      } else
      {
        Font3 font3 = Font3.Load(Font.TRUETYPE_FONT, stream);
        if (font3 != null)
        {
          SVGFont font = font3.toSVG();
          if (font != null)
            font.setEntryPath(OCD.FONTS_DIR + filename);
          return font;
        }
      }
    } catch (Exception e)
    {
      Log.warn(SVGFont.class, ".load - font reading exception: " + e.getMessage());
      e.printStackTrace();
    } finally
    {
      IO.Close(stream);
    }
    return null;
  }

  public static SVGFont read(InputStream stream)
  {
    try
    {
      SVGFont font = new SVGFont();
      font.readEntry(stream);
      return font;
    } catch (Exception e)
    {
      Log.warn(SVGFont.class, ".read - font reading exception: " + e.getMessage());
      e.printStackTrace();
    } finally
    {
      IO.Close(stream);
    }
    return null;
  }

  public SVGFont specimen(boolean ensureOne, String unicodes)
  {
    Log.debug(this, ".specimen - font: " + this.fontname());
    char[] chars = unicodes.toCharArray();
    SVGGlyph nonEmpty = null;
    boolean found = false;
    for (SVGGlyph glyph : glyphs)
    {
      for (int c : chars)
        if (glyph.isUnicode(c))
        {
          Rectangle3 box = glyph.path.bounds();

          double h = box.height();
          Path3 slash = new Path3(new Rectangle3(-0.02, -h / 2, 0.04, h));
          slash = Transform3.rotateInstance(Math.PI / 8).transform(slash);
          slash = Transform3.translateInstance(box.cx(), box.cy()).transform(slash);
          glyph.path.append(slash, false);
          // glyph.path = glyph.path.reverseX().translate(box.minX() +
          // box.maxX(), 0);
          found = true;
        }

      if (nonEmpty == null && !glyph.path.isEmpty())
        nonEmpty = glyph;
    }

    if (!found && nonEmpty != null && this.nbOfGlyphs() > 12)
    {
      Rectangle3 box = nonEmpty.path.bounds();
      nonEmpty.path = nonEmpty.path.reverseX().translate(box.minX() + box.maxX(), 0);
    }
    return this;
  }

  public SVGFont remap(String newFilename, double sx, double sy, boolean transparent, boolean ligatureSupported, int fontOverrideIndex,
      boolean fillWithDummyChars)
  {
    SVGFont newFont = new SVGFont();
    newFont.remap = new Remap();
    newFont.entryPath = this.entryDirectory() + newFilename + SVG.EXT;
    newFont.fontFamily = newFilename;
    newFont.unitsPerEm = unitsPerEm;
    newFont.ascent = ascent;
    newFont.descent = descent;
    newFont.fontStyle = fontStyle;
    newFont.fontWeight = fontWeight;

    IntSet used = new IntSet();

    for (SVGGlyph glyph : glyphs)
      if (fontOverrideIndex == glyph.override() || glyph.isWhitespace())
      {
        // override==glyph.override is true (0==0) if font has not been
        // fragmented because of overriding unicodes :-)
        int code = glyph.code;
        int[] uni = glyph.remap;

        final int originalCode = code;

        if (fontOverrideIndex > 0 && code > -1 && uni.length == 1 && CharRef.IsValid(uni[0]))
        {
          newFont.remap.put(originalCode, uni[0]);
          code = uni[0];
        }

        // kindlegen does not support ligature unicodes like ff, ffi, ffl (each
        // unicode being split into its constitutive unicodes)
        if (!ligatureSupported)
        {
          int lig = Ligature.ligature(uni);
          if (lig > 0)
          {
            int c = CharRef.PRIVATE_USE_OFFSET;
            while (!CharRef.IsValid(c) || contains(c) || newFont.contains(c))
              ++c;
            newFont.remap.put(originalCode, c);
            code = c;
          }
        }

        boolean doShift = CharRef.isCtrl(code) || !CharRef.IsValid(code);

        // 599
        boolean debug = this.fontname().contains("Memphis-Medium-Identity-H") && glyph.code == 599;
        if (debug)
        {
          Log.debug(this, ".remap - " + this.fontname() + " to " + newFont.fontname() + ", chars=" + code + ", empty=" + glyph.isPathEmpty());
        }

        // white space must correspond to empty path
        if (!doShift && (code == 32 || code == 0x00A0))
          doShift = !glyph.isPathEmpty();

        if (doShift)
          Log.debug(this, ".remap - doShift: " + code + " from " + this.fontname() + " to " + newFont.fontname());

        UnicodeBlock b = code < 0 ? null : UnicodeBlock.of(code);
        // 65289 because it is a closing parenthesis dynamically changed by
        // webkit into opening one given the context
        if (doShift || code == 65289 || b == UnicodeBlock.ARABIC || b == UnicodeBlock.ARABIC_PRESENTATION_FORMS_A
            || b == UnicodeBlock.ARABIC_PRESENTATION_FORMS_B || b == UnicodeBlock.ARABIC_SUPPLEMENT)
        {
          int c = CharRef.PRIVATE_USE_OFFSET;
          while (!CharRef.IsValid(c) || this.contains(c) || newFont.contains(c))
            ++c;
          newFont.remap.put(originalCode, c);
          code = c;
        }

        used.add(code);

        newFont.glyphs.add(glyph.derive(newFont, sx, sy, transparent, true, code));
      }

    if (fillWithDummyChars)
    {
      int c = 0x1E00;
      for (int i = used.size(); i < 256; i++)
      {
        while (used.has(c))
          c++;

        if (DUMMY == null)
          DUMMY = Font3.SERIF_FONT.size(1.0).glyph("W");

        newFont.glyphs.add(new SVGGlyph(newFont, "c" + c, DUMMY, 0.6, c));
      }

    }
    return newFont;
  }

  public SVGRoot svgRoot()
  {
    SVGRoot svg = new SVGRoot(null);
    svg.defs().addDefs(false, this);
    return svg;
  }

  @Override
  public void writeEntry(OutputStream stream)
  {
    svgRoot().writeNode(stream);
  }

  @Override
  public void readEntry(InputStream stream)
  {
    SVGRoot svg = new SVGRoot(null);
    svg.readNode(stream);
    List3<SVGFont> fonts = svg.defs().fonts();
    if (fonts.size() > 1)
      Log.info(this, ".readEntry - more than one font found in svg def");
    if (fonts.isEmpty())
      Log.warn(this, ".readEntry - no font found in svg def");
    else
    {
      SVGFont font = fonts.first();
      this.glyphs = font.glyphs;
      this.entryPath = font.entryPath;
      this.unitsPerEm = font.unitsPerEm;
      this.ascent = font.ascent;
      this.descent = font.descent;
      this.fontStyle = font.fontStyle;
      this.fontWeight = font.fontWeight;
      this.id = this.fontname();
    }
  }

  public String toUnits(float value)
  {
    return "" + (int) Math.round(unitsPerEm * value);
  }

  public float fromUnits(String value)
  {
    return value == null || value.isEmpty() ? 0 : Float.valueOf(value) / unitsPerEm;
  }

  public Rectangle3 computeGlyphsBbox()
  {
    if (glyphs.isEmpty())
      return new Rectangle3();
    Rectangle3 r = this.iterator().next().path().bounds();
    double minX = r.minX();
    double minY = r.minY();
    double maxX = r.maxX();
    double maxY = r.maxY();
    for (SVGGlyph glyph : this)
    {
      r = glyph.path().bounds();
      if (r.minX() < minX)
        minX = r.minX();
      if (r.minY() < minY)
        minY = r.minY();
      if (r.maxX() > maxX)
        maxX = r.maxX();
      if (r.maxY() > maxY)
        maxY = r.maxY();
    }
    return new Rectangle3(true, minX, minY, maxX, maxY);
  }

  public float maxWidth()
  {
    float max = 0;
    for (SVGGlyph glyph : this)
      if (glyph.width() > max)
        max = glyph.width();
    return max;
  }

  public float maxHeight()
  {
    float max = 0;
    for (SVGGlyph glyph : this)
      if (glyph.height() > max)
        max = glyph.height();
    return max;
  }

  public String missing(String text)
  {
    String missing = "";
    for (int i = 0; i < text.length(); i++)
    {
      int c = text.charAt(i);
      if (c > 31 && !glyphs.has(c))// skip control chars such as CR, LF, FF
        missing += text.charAt(i);
    }
    return missing;
  }

  public boolean hasWS()
  {
    return glyphs.contains(SVGGlyphSP.UNICODE);
  }

  public boolean has(boolean fonterEnabled, int... unicode)
  {
    return fonterEnabled ? canDisplay(unicode) : glyphs.contains(unicode);
  }
  
  public boolean has(int code)
  {
    return glyphs.has(code);
  }

  public boolean contains(int... unicode)
  {
    return glyphs.contains(unicode);
  }

  public boolean canDisplay(int... unicode)
  {    
    return contains(unicode) || fonter != null && fonter.canDisplay(unicode);
  }
  
  public boolean canDisplay(String chars)
  {
    if (chars != null)
      for (char c : chars.toCharArray())
        if (!canDisplay(c))
          return false;
    return true;
  }

  public SVGGlyph get(int code)
  {
    SVGGlyph g = glyphs.get(code);
    return g == null ? fonter(code) : g;
  }

  public SVGGlyph glyph(int code)
  {
    return get(code);
  }

  public SVGGlyph fonter(int c)
  {
    SVGGlyph g = null;
    if (fonter != null && (g = fonter.svgGlyph(c)) != null)
    {
      g.font = this;
      glyphs.add(g);
    }
    return g;
  }
  
  public SVGGlyph[] glyphs(char[] text)
  {
    return glyphs(text, false);
  }
  
  public SVGGlyph[] glyphs(String text, boolean refont)
  {
    return glyphs(text.toCharArray(), refont);
  }

  public SVGGlyph[] glyphs(char[] text, boolean refont)
  {
    if (text.length == 0)
      return new SVGGlyph[0];
    int[] chars = new int[text.length];
    for (int i = 0; i < text.length; i++)
      chars[i] = text[i];
    return glyphs(chars, refont);
  }

  public SVGGlyph[] glyphs(int[] text, boolean refont)
  {
    if (text.length == 0)
      return new SVGGlyph[0];

    SVGGlyph[] glyphs = new SVGGlyph[text.length];
    for (int i = 0; i < text.length; i++)
    {
      SVGGlyph g = get(text[i]);
      if (fonter != null && (refont || g == null))
        g = fonter(text[i]);
      glyphs[i] = g == null ? new SVGGlyph(this, "noname", new Path3(), 0, text[i]) : g;
    }
    return glyphs;
  }

  public boolean isBoldOrItalic()
  {
    return isBold() || isItalic();
  }

  public boolean isBoldAndItalic()
  {
    return isBold() && isItalic();
  }

  public boolean isBold()
  {
    switch (fontWeight.charAt(0))
    {
    case 'b':// bold
    case '6':// 600
    case '7':// 700
    case '8':// 800
    case '9':// 900
      return true;
    default:
      return false;
    }
  }

  public int intWeight()
  {
    return fontWeight.equals("normal") ? 400 : fontWeight.equals("bold") ? 700 : Nb.Int(fontWeight, 400);
  }

  public String weight()
  {
    return this.fontWeight;
  }

  public String style()
  {
    return this.fontStyle;
  }

  public boolean isItalic()
  {
    return !this.fontStyle.equals("normal");
  }

  public void setWeight(String weight)
  {
    this.fontWeight = weight;
  }

  public void setStyle(String style)
  {
    this.fontStyle = style;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("id", this.fontname());
    xml.write("horiz-adv-x", unitsPerEm);
    return this.children();
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    List3<OCDNode> children = new List3<OCDNode>();
    children.add(fontFace);
    children.add(missingGlyph);
    children.addAll(glyphs.toSortedList());
    return children;
  }

  @Override
  public OCDNode newChild(DomNode child)
  {
    if (OCD.isTag(child, SVGGlyph.TAG))
      return new SVGGlyph(this);
    else if (OCD.isTag(child, SVGMissingGlyph.TAG))
      return this.missingGlyph;
    else if (OCD.isTag(child, SVGFontFace.TAG))
      return this.fontFace;
    else
      return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;

    if (OCD.isTag(child, SVGGlyph.TAG))
      add((SVGGlyph) child);
  }

  @Override
  public void readAttributes(DomNode e)
  {
    this.id = e.id();
  }

  public int nbOfGlyphs()
  {
    return this.glyphs.size();
  }

  public GlyphTree glyphTree()
  {
    return this.glyphs;
  }

  public List3<SVGGlyph> glyphs()
  {
    return this.glyphs.toList();
  }

  public List3<SVGGlyph> sortedGlyphs()
  {
    List3<SVGGlyph> sorted = glyphs();
    Collections.sort(sorted, (g1, g2) -> g1.compareTo(g2));
    return sorted;
  }

  public SVGGlyph[] glyphArray()
  {
    return glyphs().toArray(new SVGGlyph[0]);
  }

  public boolean add(SVGGlyph glyph)
  {
    glyph.setFont(this);
    glyph.setParent(this);
    return glyphs.add(glyph);
    // Zen.LOG.debug(this, ".add - glyph=" + glyph.glyphName + " uni=" +
    // Zen.Array.toString(glyph.unicodes()));
  }

  public SVGGlyph remove(SVGGlyph glyph)
  {
    return this.glyphs.remove(glyph);
  }

  public SVGGlyph find(Path3 path, int... uni)
  {
    try
    {
      for (SVGGlyph g : this)
        if (g.isUnicode(uni) && g.path().equalsPath(path, 0.01f))
          return g;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  // public void update(SVGFont font)
  // {
  // for (SVGGlyph glyph : font)
  // if (!this.contains(glyph.unicode))
  // this.add(glyph);
  // }
  public String fontFamily()
  {
    return fontFamily;
  }

  public String fontname()
  {
    return File3.Filename(entryPath, true);
  }

  public String filename()
  {
    return this.entryFilename();
  }

  public float ascent()
  {
    return ascent < 0 ? -ascent : ascent;
  }

  public int ascent1000()
  {
    return Math.round(ascent() * 1000);
  }

  public float descent()
  {
    return descent > 0 ? -descent : descent;
  }

  public int descent1000()
  {
    return Math.round(descent() * 1000);
  }

  public void setFontname(String fontname)
  {
    this.entryPath = entryDirectory() + (fontname.endsWith(SVG.EXT) ? fontname : fontname + SVG.EXT);
  }

  public void setAscent(double ascent)
  {
    this.ascent = (float) (ascent > 0 ? ascent : -ascent);
    if (this.ascent > 1f)
      this.ascent = 1f;
    else if (this.ascent < 0.5f)
      this.ascent = 0.5f;
  }

  public void setDescent(double descent)
  {
    this.descent = (float) (descent < 0 ? descent : -descent);
    if (this.descent < -0.5f)
      this.descent = -0.5f;
    else if (this.descent > -0.2f)
      this.descent = -0.2f;
  }

  public void setAscent1000(int ascent)
  {
    this.setAscent(ascent / 1000.0);
  }

  public void setDescent1000(int descent)
  {
    this.setDescent(descent / 1000.0);
  }

  @Override
  public boolean equals(Object font)
  {
    if (this == font)
      return true;
    if (font == null || this.getClass() != font.getClass())
      return false;
    return this.fontname().equals(((SVGFont) font).fontname());
  }

  @Override
  public int hashCode()
  {
    return this.fontname().hashCode();
  }

  @Override
  public Iterator<SVGGlyph> iterator()
  {
    return glyphs().iterator();
  }

  @Override
  public String sticker()
  {
    return tag() + "[" + entryFilename() + "]";
  }

  @Override
  public String toString()
  {
    Stringer sb = new Stringer();
    sb.prop("Font", entryFilename());
    sb.prop("FontFamily", fontFamily);
    sb.prop("Ascent", ascent);
    sb.prop("Descent", descent);
    sb.prop("FontWeight", fontWeight);
    sb.prop("FontStyle", fontStyle);
    SVGGlyph[] a = glyphs.toArray();
    sb.append("\nGlyphs[");
    for (int i = 0; i < a.length; i++)
      sb.append("\n").append(a[i].glyphName).append(" u").append(a[i].code()).append(" w").add((int) (1000 * a[i].horizAdvX));
    sb.append("\n]");
    return sb.toString();
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    int cellSize = CELL_SIZE;
    int size = cellSize / 2;
    int half = size / 2;
    int x = 0;
    int y = 0;
    g.setFont(g.getFont().deriveFont(11f));
    g.setColor(Color.BLACK);
    for (SVGGlyph glyph : this)
    {
      if (x + cellSize >= g.width())
      {
        x = 0;
        y += cellSize;
      }

      int gx = x + half;
      int gy = y + size;
      // XED.LOG.warn(this,".paint - empty glyph: fontname="+fontname+"
      // code="+code+" path="+glyph.path.stringValue(0.1f));
      g.setColor(Color.BLACK);
      g.fill(new Transform3(size, 0, 0, size, gx, gy).transform(glyph.path));
      g.setColor(Color.GREEN.darker());
      g.fill(new Circle3(gx, gy, 2));
      g.draw(new Line3(gx, gy, gx + (int) (glyph.width() * size), gy));

      float h = glyph.height() * size;
      float ox = glyph.vertOriginX * size;
      float oy = glyph.vertOriginY * size;
      if (h != 0)
      {
        // Log.debug(this, ".paint - " + this.fontname() + ": " + h + ", ox=" +
        // ox + ", oy=" + oy);
        g.fill(new Circle3(gx + ox, gy - oy, 2));
        g.draw(new Line3(gx + ox, gy - oy, gx + ox, gy - oy - h));
      }

      g.draw(glyph.glyphName + " " + glyph.code(), gx, gy + cellSize / 3, g.getFont(), Color3.GREEN_DARK);
      x += cellSize;
    }
  }

  public SVGFont update(SVGFont font)
  {
    this.ascent = font.ascent;
    this.descent = font.descent;
    this.fontStyle = font.fontStyle;
    this.fontWeight = font.fontWeight;
    this.unitsPerEm = font.unitsPerEm;
    this.entryPath = font.entryPath;
    this.replacement = font.replacement;
    for (SVGGlyph glyph : font)
      this.add(glyph.copy());
    return this;
  }

  public SVGFont addGlyphs(SVGFont font, int... unicodes)
  {
    for (int uni : unicodes)
      if (!contains(uni) && font.canDisplay(uni))
        add(font.glyph(uni).copy());

    return this;
  }

  public SVGFont subset(int... text)// text==null != text==""
  {
    SVGFont copy = new SVGFont();
    copy.ascent = this.ascent;
    copy.descent = this.descent;
    copy.entryPath = this.entryPath;
    copy.fontFamily = this.fontFamily;
    copy.fontWeight = this.fontWeight;
    copy.fontStyle = this.fontStyle;
    copy.replacement = this.replacement;
    if (text == null || text.length == 0)
      return copy;
    else if (text.length == 1 && text[0] < 0)
      for (SVGGlyph glyph : this)
        copy.add(glyph.copy());
    else
      for (SVGGlyph glyph : this.glyphs(text, false))
        copy.add(glyph.copy());
    return copy;
  }

  public SVGFont copy(String newFilename)
  {
    SVGFont copy = this.copy();
    copy.entryPath = this.entryDirectory() + newFilename + SVG.EXT;
    copy.fontFamily = newFilename;
    return copy;
  }

  @Override
  public SVGFont copy()
  {
    return subset(-1);
  }

  public static boolean isBoldItalic(String fontname)
  {
    return fontname.contains(_BI);
  }

  public static boolean isBold(String fontname)
  {
    return isBoldItalic(fontname) || fontname.contains(_B);
  }

  public static boolean isItalic(String fontname)
  {
    return isBoldItalic(fontname) || fontname.contains(_I);
  }

  public static String Rename(String fontname, boolean bold, boolean italic)
  {
    return FontFamily(fontname) + (bold && italic ? _BI : bold ? _B : italic ? _I : "");
  }

  public static String Normalize(String fontname)
  {
    if (fontname != null)
    {
      fontname = fontname.replace(' ', '_').replace(',', '_').replace("Times_New_Roman", "Times").replace("Courier_New", "Courier");
      for (char c : ".:;/\\<>?*|\"".toCharArray())
        if (fontname.indexOf(c) > 0)
          fontname = fontname.replace(c, '-');
    }
    return fontname;
  }

  public static String FontFamily(String fontname)
  {
    return File3.Filename(fontname, true).replace(_BI, "").replace(_B, "").replace(_I, "");
  }

  public static String[] fontVersions(String fontname)
  {
    return fontVersions(fontname, true, true, true, true);
  }

  public static String[] fontVersions(String fontname, boolean normal, boolean bold, boolean italic, boolean boldItalic)
  {
    StringList fonts = new StringList();
    String family = FontFamily(fontname);
    if (normal)
      fonts.add(family);
    if (bold)
      fonts.add(family + _B);
    if (italic)
      fonts.add(family + _I);
    if (boldItalic)
      fonts.add(family + _BI);
    return fonts.array();
  }

  public static String Postfix(String fontname, boolean bold, boolean italic)
  {
    return FontFamily(fontname) + Postfix(bold, italic);
  }

  public static String Postfix(boolean bold, boolean italic)
  {
    return bold && italic ? _BI : (bold ? _B : (italic ? _I : ""));
  }

  public void updateGlyphRemapOverrides()
  {
    Occurrences<Integer> occ = new Occurrences<>();

    for (SVGGlyph glyph : this)
    {
      glyph.cleanRemap();
      if (glyph.code > -1)
        occ.inc(glyph.code);
    }

    for (SVGGlyph glyph : this)
    {
      if (glyph.remap.length == 1 && occ.has(glyph.remap[0]) && glyph.remap[0] != glyph.code)
      {
        glyph.fontOverrideIndex = occ.get(glyph.remap[0]);
        occ.inc(glyph.remap[0]);
      }
    }
  }

  // public int codeConflicts(int[] code, int... remap)
  // {
  // int override = 0;
  // if (remap == null || remap.length == 0 || Arrays.equals(code, remap))
  // return override;
  //
  // for (SVGGlyph glyph : this)
  // {
  // int[] c = glyph.code;
  // if (Arrays.equals(c, code))
  // {
  // return override;
  // }
  // if (Arrays.equals(remap, c) || Arrays.equals(remap, glyph.remap))
  // {
  // override++;
  // // Log.debug(this, ".override - code=" + Arrays.toString(code) +
  // // ", remap=" + Arrays.toString(remap));
  // }
  // }
  // return -1;
  // }

  @Override
  public boolean isInMemory()
  {
    return this.inMemory = !this.glyphs.isEmpty();
  }

  @Override
  public String xmlString()
  {
    // Log.debug(this, ".xmlString - " + Xml.toString(this.svgRoot()));
    return Xml.toString(this.svgRoot());
  }
}
