package sugarcube.formats.pdf.resources.fonts;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Cache;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.ui.gui.Font3;
import sugarcube.formats.pdf.resources.RS;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;

public class FONTS
{
    private final static String[] FONT_14 =
            {"Times", "Helvetica", "Arial", "Courier", "Symbol", "ZapfDingbats"};
    private final static StringMap<String> MAP_14 = new StringMap<String>();
    private final static Cache<String, Font3> CACHE = new Cache<>("FontCache", 100, 15);

    static
    {
        String[][] fonts =
                {
                        {"Times", "TimesNewRomanPSMT", "TimesNewRomanPS", "TimesNewRoman"},
                        {"Helvetica", "Arial", "ArialMT"},
                        {"Courier", "CourierNew", "CourierNewPSMT"},
                        {"Symbol", "SymbolMT"},
                        {"ZapfDingbats"}};
        for (int i = 0; i < fonts.length; i++)
            for (int j = 0; j < fonts[i].length; j++)
                MAP_14.put(fonts[i][j], fonts[i][0]);
    }


//  public static final String UNICODE = "arial";
//  public static final String TREBUCHET = "Trebuchet.ttf";

//  public static final String[] FONTS =
//  { TREBUCHET };

    public static boolean IsFont14(String baseFont)
    {
        for (String font : FONT_14)
            if (baseFont.contains(font))
                return true;
        return false;
    }

    public static String RenameTo14(String base)
    {
        if(!IsFont14(base))
            return base;

        switch (base.toLowerCase())
        {
            case "arial-black":
            case "arialblack":
                return "ArialBlack";
        }

        // Log.debug(Reader14_OS.class, ".renameTo - "+base);
        base = base.replace("-", "_");
        if (base.toLowerCase().endsWith("_regular"))
            base = base.substring(0, base.length() - 8);

        for (String font : FONT_14)
            if (base.contains(font))
            {
                font = MAP_14.get(font);
                if (base.contains("BoldItalic") || base.contains("BoldItalicMT") || base.contains("BoldOblique"))
                    return font + "_BoldItalic";
                else if (base.contains("Bold") || base.contains("BoldMT"))
                    return font + "_Bold";
                else if (base.contains("Italic") || base.contains("ItalicMT") || base.contains("Oblique"))
                    return font + "_Italic";
                return font;
            }
        return base;
    }

    public static Font3 Load(String fontname)
    {
        Font3 font;
        if ((font = CACHE.get(fontname, null)) != null)
            return font;

        if (IsFont14(fontname) && (font = Font3.Load(Font3.TRUETYPE_FONT, FONTS.Stream(RenameTo14(fontname)))) != null)
            return font;

//        if ((font = Font3.Load(Font3.TRUETYPE_FONT, FONTS.Stream(fontname))) != null)
//            return font;

        String lower = fontname.toLowerCase();
        boolean bold = Str.Has(lower, "bold", "black");
        boolean italic = Str.Has(lower, "italic", "oblique");

        StringSet osFonts = Font3.OSFonts();
        String osFontname = Str.ReplacePairs(fontname, "_", " ", "-", " ").trim();
        
        HashMap<String, String> fontSystemRenamed = new HashMap<String, String>();
        for (String systemFontName: osFonts) {
        	String keyName = Str.ReplacePairs(systemFontName, "_", " ", "-", " ").trim();
        	keyName = keyName.replaceAll(" ", "");
        	fontSystemRenamed.put(keyName, systemFontName);
        }
        
        while (!osFonts.has(osFontname) && osFontname.length() > 3) {
            osFontname = osFontname.substring(0, osFontname.length() - 1);
        }
        
        if (fontSystemRenamed.containsKey(fontname)) {
            System.out.println("MR - REMAP " + fontname + " >> " + fontSystemRenamed.get(fontname));
            font = new Font3(fontSystemRenamed.get(fontname), (bold ? Font.BOLD : 0) + (italic ? Font.ITALIC : 0), 1);
        }else  if (osFontname.length() > 2) {
            font = new Font3(osFontname, (bold ? Font.BOLD : 0) + (italic ? Font.ITALIC : 0), 1);
        }

        if (font == null)
        {
            font = Font3.CALIBRI_FONT.derive(1);
            if (bold || italic)
                font = font.derive(bold, italic);
            font.setReplacement(font.getFamily());
        }

        CACHE.put(fontname, font);
        return font;
    }


    public static final void main(String... args)
    {
    }

    public static byte[] Bytes(String filename)
    {
        return RS.ReadBytes(Stream(filename));
    }

    public static InputStream Stream(String filename)
    {
        try
        {
            return FONTS.class.getResourceAsStream(filename);
        } catch (Exception e)
        {
            return null;
        }
    }

    private static void Debug(String msg)
    {
        Log.debug(FONTS.class, msg);
    }
}
