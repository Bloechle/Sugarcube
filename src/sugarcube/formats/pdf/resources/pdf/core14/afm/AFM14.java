package sugarcube.resources.pdf.core14.afm;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.system.io.IO;
import sugarcube.formats.pdf.reader.pdf.node.font.FontMetrics;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.abs;

public class AFM14
{
    private static StringMap<AFM14> AFM_MAP = new StringMap<>();

    public static class AFMWidth
    {
        public final int code;
        public final String name;
        public final int width;

        public AFMWidth(int code, String name, int width)
        {
            this.code = code;
            this.name = name;
            this.width = width;
        }
    }

    public List3<AFMWidth> widths = new List3<>();
    public String fontname;
    public String weight;
    public int ascent, descent, italicAngle;
    public boolean isFixedPitch;
    public Rectangle3 fontBBox;

    public AFM14(String fontname)
    {
        try
        {
            InputStream reader = AFM14.Stream(fontname + ".afm");
            if (reader != null)
            {
                String name = "";
                int code14 = 0;// built-in code
                int width = 0;
                String token = Word(reader); //skip first token
                while ((token = Word(reader)) != null)
                {
                    switch (token)
                    {
                        case "FontName":
                            // this.fontName = word(reader);
                            break;
                        case "Weight":
                            this.weight = Word(reader);
                            break;
                        case "Ascender":
                            this.ascent = abs(Int(reader));
                            break;
                        case "Descender":
                            this.descent = abs(Int(reader));
                            break;
                        case "ItalicAngle":
                            this.italicAngle = Int(reader);
                            break;
                        case "IsFixedPitch":
                            this.isFixedPitch = Boolean.parseBoolean(Word(reader));
                            break;
                        case "FontBBox":
                            this.fontBBox = new Rectangle3(Ints(reader, 4));
                            break;
                        case "StartCharMetrics":
                            do
                            {
                                switch (token)
                                {
                                    case "C":
                                        code14 = Int(reader);
                                        break;
                                    case "WX":
                                        width = Int(reader);
                                        break;
                                    case "N":
                                        this.widths.add(new AFMWidth(code14, name = Word(reader), width));
                                        break;
                                }
                            }
                            while ((token = Word(reader)) != null && !token.equals("EndCharMetrics"));
                            break;
                    }
                }
                IO.Close(reader);
            }

        } catch (Exception e)
        {
            Log.error(this, ".parse - font afm error: name=" + fontname + " exception=" + e);
            e.printStackTrace();
        }
    }

    public AFM14 populateMetrics(FontMetrics metrics)
    {
        if (weight != null)
            metrics.weight = weight;
        if (ascent != 0)
            metrics.ascent = ascent;
        if (descent != 0)
            metrics.descent = descent;
        if (italicAngle != 0)
            metrics.italicAngle = italicAngle;
        if (isFixedPitch)
            metrics.isFixedPitch = true;
        if (fontBBox != null)
            metrics.fontBBox = fontBBox;
        return this;
    }


    private static int Int(InputStream reader)
    {
        return (int) Double.parseDouble(Word(reader));
    }

    private static int[] Ints(InputStream reader, int size)
    {
        int[] integers = new int[size];
        for (int i = 0; i < integers.length; i++)
            integers[i] = Int(reader);
        return integers;
    }

    private static int Read(InputStream reader)
    {
        try
        {
            return reader.read();
        } catch (IOException ex)
        {
            ex.printStackTrace();
            return -1;
        }
    }

    private static String Word(InputStream reader)
    {
        StringBuilder token = new StringBuilder();
        int c = ' ';

        while (EOW(c) && c != -1)
            c = Read(reader);

        while (!EOW(c))
        {
            token.appendCodePoint(c);
            c = Read(reader);
        }

        if (token.length() == 0)
            return null;

        return token.toString();
    }

    private static boolean EOW(int c)
    {
        return c == 10 || c == 13 || c == 32 || c == -1;
    }

    public static InputStream Stream(String filename)
    {
        try
        {
            return AFM14.class.getResourceAsStream(filename);
        } catch (Exception e)
        {
            return null;
        }
    }

    public static AFM14 Get(String fontname)
    {
        AFM14 afm = AFM_MAP.get(fontname, null);
        if (afm == null)
            AFM_MAP.put(fontname, afm = new AFM14(fontname));
        return afm;
    }
}