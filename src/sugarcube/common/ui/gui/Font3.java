package sugarcube.common.ui.gui;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.data.xml.CharRef;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.font.SVGGlyph;
import sugarcube.formats.ocd.objects.font.SVGGlyphSP;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

public class Font3 extends Font
{
    public static final StringSet NOT_FOUND = new StringSet();

    public static final StringSet PATHS = new StringSet();
    public static final Set3<Class> CLASSES = new Set3<Class>();

    // fonts are often described with integer numbers scaled by a factor of 1000
    private static final Transform3 TM = new Transform3(1000, 0, 0, 1000, 0, 0);
    public static final FontRenderContext CTX = new FontRenderContext(TM, false, false);
    public static Font3 SERIF_FONT = new Font3(Font.SERIF);
    public static Font3 SANS_FONT = new Font3(Font.SANS_SERIF);
    public static Font3 MONO_FONT = new Font3(Font.MONOSPACED);
    public static Font3 GUI_FONT = calibri(12);
    public static Font3 MENU_FONT = GUI_FONT;

    public static Font3 CALIBRI_FONT = calibri(1);
    public static Font3 SERIF_FONT_1 = SERIF_FONT.derive(1);
    private boolean isUnicodeFont = true;
    private String replacement = null;

    public Font3(String name, int style, int size)
    {
        super(name, style, size);
    }

    public Font3(String name, int size)
    {
        super(name, Font.PLAIN, size);
    }

    public Font3(String name)
    {
        super(name, Font.PLAIN, 12);
    }

    public Font3(Font font)
    {
        super(font);
    }

    public static Font3 Seek(String fontname, Font3 def)
    {
        Font3 font = def;

        if ((font = OSFont(fontname)) != null)
            return font;

        for (String path : PATHS)
            if ((font = Load(path, SVGFont.Normalize(fontname))) != null)
                return font;

        for (Class path : CLASSES)
            if ((font = Load(path, SVGFont.Normalize(fontname))) != null)
                return font;

        if (font == null)
            Log.debug(Font3.class, ".Seek - font not found: " + fontname);

        return font == null ? def : font;
    }

    public static String normalize(String fontname, String ext)
    {
        fontname = fontname.replace(" ", "_").replace("-", "_").replace("Arial_Black", "ArialBlack");
        fontname = fontname.replace("_Regular", "");
        fontname = fontname.replace("bold", "Bold").replace("italic", "Italic").replace("oblique", "Italic").replace("Oblique", "Italic");
        return File3.Extense(fontname, ext == null ? "" : ext.trim());
    }

    public String replacement()
    {
        return replacement;
    }

    public void setReplacement(String replacement)
    {
        this.replacement = replacement;
    }

    public boolean canDisplay(char[] chars)
    {
        for (char c : chars)
            if (!this.canDisplay((int) c))
                return false;
        return true;
    }

    public boolean canDisplay(int[] chars)
    {
        for (int c : chars)
            if (!this.canDisplay(c))
                return false;
        return true;
    }

    public void setUnicodeFont(boolean isUnicodeFont)
    {
        this.isUnicodeFont = isUnicodeFont;
    }

    public boolean isUnicodeFont()
    {
        return this.isUnicodeFont;
    }

    public static Font3 Load(int format, File file)
    {
        try
        {
            return new Font3(Font.createFont(format, file));
        } catch (Exception e)
        {
            Log.warn(Font3.class, ".Load: " + e.getMessage());
        } finally
        {
        }
        return null;
    }

    public static Font3 Load(int format, InputStream stream)
    {
        if (stream != null)
            try
            {
                return new Font3(Font.createFont(format, stream));
            } catch (Exception e)
            {
                Log.warn(Font3.class, ".Load: " + e.getMessage());
                e.printStackTrace();
            } finally
            {
                IO.Close(stream);
            }
        return null;
    }

    public static Font3 Load(Iterable<Class> paths, String filename)
    {
        Font3 font;
        for (Class path : paths)
            if ((font = Load(path, filename)) != null)
                return font;
        return null;
    }

    public static Font3 Load(Class path, String filename)
    {
        try
        {
            InputStream stream = Class3.Stream(path, filename);
            if (stream == null)
            {
                Log.debug(Font3.class, ".Load - font not found: " + filename);
                return null;
            }
            Font3 font = new Font3(Font.createFont(TRUETYPE_FONT, stream));
            IO.Close(stream);
            return font;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            // if (NOT_FOUND.notYet(filename))
            Log.debug(Font3.class, ".Load - font not found: " + filename);
        }
        return null;
    }

    public static Font3 Load(String path, String filename)
    {
        File3 file = new File3(path, filename);
        if (file.exists())
            return Font3.Load(Font3.TRUETYPE_FONT, file);
        else
        {
            try
            {
                InputStream stream = null;
                Class3 c3 = new Class3(path);
                if (c3.exists())
                    stream = c3.stream(filename);
                if (stream == null)
                    stream = Class3.class.getResourceAsStream((path.endsWith("/") ? path : path + "/") + filename);
                if (stream != null)
                {
                    Font3 font = new Font3(Font.createFont(TRUETYPE_FONT, stream));
                    IO.Close(stream);
                    return font;
                }
            } catch (Exception ex)
            {
                Log.debug(Font3.class, ".Load - font not found: " + filename);
            }
            return null;
        }

    }

    public static StringSet OSFonts()
    {
        return new StringSet(ListOSFonts(false));
    }

    public static String[] ListOSFonts()
    {
        return ListOSFonts(true);
    }

    public static String[] ListOSFonts(boolean sort)
    {
        try
        {
            String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            if (sort)
                Arrays.sort(fonts);
            return fonts;
        } catch (Exception e)
        {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static void PrintOSFonts()
    {
        StringBuilder sb = new StringBuilder();
        for (String font : ListOSFonts())
            sb.append(font + "\n");
        Log.debug(Font3.class, ".printOSFonts:\n" + sb);
    }

    public static boolean ExistsOSFont(String fontname)
    {
        for (String font : ListOSFonts())
            if (font.equals(fontname))
                return true;
        return false;
    }

    public static Font3 calibri(int size)
    {
        if (ExistsOSFont("Calibri"))
            return CreateFont("Calibri", new Font3(Font.SANS_SERIF), size);
        else if (ExistsOSFont("Helvetica"))
            return CreateFont("Helvetica", new Font3(Font.SANS_SERIF), size);
        else
            return CreateFont("Arial", new Font3(Font.SANS_SERIF), size);
    }

    public static Font3 CreateFont(String fontname, Font3 def, int size)
    {
        boolean exists = ExistsOSFont(fontname);
        if (!exists)
            Log.debug(Font3.class, ".createFont - system font not found: " + fontname);
        return exists ? new Font3(fontname, size) : def == null ? null : def.derive(size);
    }

    public static String FirstOSFont(String... fontnames)
    {
        StringSet osFonts = OSFonts();

        for (String font : fontnames)
            if (osFonts.has(font))
                return font;

        for (String font : fontnames)
        {
            font = font.toLowerCase().replace(" ", "");
            for (String osFont : osFonts)
                if (font.equals(osFont.toLowerCase().replace(" ", "")))
                    return osFont;
        }

        return fontnames[0];
    }

    public static Font3 OSFont(String fontname)
    {
        if (fontname == null || fontname.isEmpty())
            return null;
        String norm = SVGFont.FontFamily(fontname.replaceAll(".ttf\\z", "").replaceAll(".svg\\z", ""));
        Font3 f = CreateFont(norm.replace('_', ' '), null, 1);
        return f == null ? null
                : SVGFont.isBoldItalic(fontname) ? f.boldItalic() : SVGFont.isBold(fontname) ? f.bold() : SVGFont.isItalic(fontname) ? f.italic() : f;
    }

    public static SVGFont svgFont(String fontname)
    {
        Font3 font = OSFont(fontname);
        Log.debug(Font3.class, ".svgFont - " + (font == null ? "null" : font.getFamily()));
        return font == null ? null : font.toSVG(fontname);
    }

    public Font3 size(double size)
    {
        return new Font3(this.deriveFont((float) size));
    }

    public Font3 derive(double size)
    {
        return new Font3(this.deriveFont((float) size));
    }

    public Font3 bold()
    {
        return new Font3(deriveFont(Font.BOLD));
    }

    public Font3 italic()
    {
        return new Font3(deriveFont(Font.ITALIC));
    }

    public Font3 boldItalic()
    {
        return new Font3(deriveFont(Font.BOLD + Font.ITALIC));
    }

    public Font3 derive(boolean bold, boolean italic)
    {
        return new Font3(deriveFont((bold ? Font.BOLD : 0) + (italic ? Font.ITALIC : 0)));
    }

    public Font3 plain()
    {
        return new Font3(this.deriveFont(Font.PLAIN));
    }

    public Font3 decrease()
    {
        return new Font3(this.name, this.style, this.size - 1);
    }

    public Font3 decrease(int delta)
    {
        return new Font3(this.name, this.style, this.size - delta);
    }

    public Font3 increase()
    {
        return new Font3(this.name, this.style, this.size + 1);
    }

    public Font3 increase(int delta)
    {
        return new Font3(this.name, this.style, this.size + 1);
    }

    public double width(char[] unicodes)
    {
        return this.width(new String(unicodes, 0, unicodes.length));
    }

    public double width(int... unicodes)
    {
        return this.width(new String(unicodes, 0, unicodes.length));
    }

    public double width(String text)
    {
        return this.getStringBounds(text, CTX).getWidth();
    }

    public Point3 advance(char[] unicodes)
    {
        return new Point3(width(unicodes), 0);
    }

    public Point3 advance(int[] unicodes)
    {
        return new Point3(width(unicodes), 0);
    }

    public Point3 advance(String text)
    {
        return new Point3(width(text), 0);
    }

    public Path3 glyph(int... unicodes)
    {
        char[] chars = new char[unicodes.length];
        for (int i = 0; i < chars.length; i++)
            chars[i] = (char) unicodes[i];
        return this.glyph(chars);
    }

    public Path3 glyph(char[] unicodes)
    {
        // canDisplayUpTo mandatory to avoid unpredictable native fontmanager.dll
        // crash on windows
        // using chars for unicode codes
        if (this.canDisplayUpTo(unicodes, 0, unicodes.length) == -1)
            return new Path3(this.createGlyphVector(CTX, unicodes).getOutline());
        else
            return new Path3(CALIBRI_FONT.createGlyphVector(CTX, unicodes).getOutline());
    }

    public Path3 glyph(String text)
    {
        // canDisplayUpTo mandatory to avoid native fontmanager.dll crash on windows
        if (this.canDisplayUpTo(text) == -1)
            return new Path3(this.createGlyphVector(CTX, text).getOutline());
        else
            return new Path3(CALIBRI_FONT.createGlyphVector(CTX, text).getOutline());
    }

    public SVGFont toSVG()
    {
        return toSVG(null);
    }

    public SVGFont toSVG(String fontname, char[] chars)
    {
        int[] ints = new int[chars.length];
        for (int i = 0; i < chars.length; i++)
            ints[i] = chars[i];
        return toSVG(fontname, ints);
    }

    public SVGFont toSVG(String fontname, int... chars)
    {
        SVGFont svg = new SVGFont();
        if (fontname == null || fontname.isEmpty())
            fontname = this.getPSName();
        if (fontname == null || fontname.isEmpty())
            fontname = this.getFontName();
        svg.setFontname(fontname);
        svg.setAscent(0.8);
        svg.setDescent(-0.2);
        String lowername = fontname.toLowerCase();
        svg.setWeight(lowername.contains("bold") ? "bold" : "normal");
        svg.setStyle(lowername.contains("italic") ? "italic" : lowername.contains("oblique") ? "oblique" : "normal");
        if (chars == null || chars.length == 0)
        {
            for (char c = 0; c < 0xffff; c++)
                if (CharRef.IsValid(c) && canDisplay(c))
                    svg.add(svgGlyph(c));
            Log.debug(this, ".toSVG - fully populating " + fontname + ": " + svg.nbOfGlyphs() + " glyphs, chars=" + Zen.Array.String(chars));
        } else
            for (int c : chars)
            {
                if (CharRef.IsValid(c) && canDisplay(c))
                    svg.add(svgGlyph(c));
                if (!svg.hasWS() && canDisplay(Unicodes.ASCII_SP))
                    svg.add(svgGlyph(Unicodes.ASCII_SP));
            }
        if (!svg.hasWS())
            svg.add(new SVGGlyphSP(svg, 0.4));

        return svg;
    }

    // public SVGGlyph svgGlyph(String chars)
    // {
    // return svgGlyph(chars.toCharArray());
    // }

    // public SVGGlyph svgGlyph(char[] chars)
    // {
    // int[] c = new int[chars.length];
    // for (int i = 0; i < chars.length; i++)
    // c[i] = chars[i];
    // return svgGlyph(c);
    // }

    public SVGGlyph svgGlyph(int c)
    {
        if (c < 32)
            return new SVGGlyph(null, "" + ((char) c), new Path3(), 0, c);

        GlyphVector vector = createGlyphVector(Font3.CTX, "" + ((char) c));
        Shape shape = vector.getGlyphOutline(0);

        double w = 0;
        double h = 0;

        for (int i = 0; i < vector.getNumGlyphs(); i++)
        {
            GlyphMetrics metrics = vector.getGlyphMetrics(i);
            w += metrics.getAdvanceX();
            h += metrics.getAdvanceY();
        }

        return new SVGGlyph(null, "" + ((char) c), shape, new Point3(w, h), null, c);
    }

    public Glyph3 glyph3(int... chars)
    {

        String charname = "";
        char[] cArray = new char[chars.length];
        for (int i = 0; i < chars.length; i++)
        {
            cArray[i] = (char) chars[i];
            charname += (char) chars[i];
        }
        // Log.debug(this, ".generateSVG - chars: "+charname);

        GlyphVector vector = createGlyphVector(Font3.CTX, cArray);
        Shape shape = cArray.length == 1 ? vector.getGlyphOutline(0) : vector.getOutline();

        double w = 0;
        double h = 0;

        for (int i = 0; i < vector.getNumGlyphs(); i++)
        {
            GlyphMetrics metrics = vector.getGlyphMetrics(i);
            w += metrics.getAdvanceX();
            h += metrics.getAdvanceY();
        }

        return new Glyph3(charname, shape, new Point3(w, h), null, chars);
    }

    // public Path3 glyph(int unicode, int width)
    // {
    // Path3 path = glyph(unicode);
    // double w = width / 1000.0;
    // double max = path.getBounds2D().getMaxX();
    //
    // if (unicode != 'I' && unicode != 'l' && unicode != 'i' && unicode != 'j' &&
    // unicode != 'J')
    // {
    // double d = w - max;
    // path = path.scaleX(d > 0 ? 1 + d / w : 1 + d / max);
    // }
    // else
    // {
    // double d = w - max;
    // path = path.scaleX(d > 0 ? 1 + (d / w) / 3 : 1);
    // }
    // return path;
    // }

    public static void main(String... args)
    {
        for (String font : Font3.ListOSFonts())
            Log.debug(Font3.class, " - " + font);

    }
}
