package sugarcube.formats.pdf.reader;

import sugarcube.common.data.collections.A;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.numerics.Math3;
import sugarcube.common.data.xml.CharRef;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFont;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFGlyph;
import sugarcube.formats.ocd.analysis.text.Ligatures;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.font.SVGGlyph;
import sugarcube.formats.ocd.objects.font.SVGGlyphSP;

import java.util.Collection;

public class PDF2SVGFont
{
    private OCDDocument ocd;
    private StringMap<Integer> remap = new StringMap<>();

    public void setOCD(OCDDocument ocd)
    {
        this.ocd = ocd;
    }

    public int remap(String fontkey, int def)
    {
        return remap.has(fontkey) ? remap.get(fontkey) : def;
    }

    public SVGFont needFont(String ocdFontname, PDFFont pdfFont)
    {
        SVGFont svgFont = ocd.fontHandler.contains(ocdFontname) ? ocd.fontHandler.font(ocdFontname) : null;
        if (svgFont == null)
        {
            svgFont = new SVGFont();
            svgFont.setFontname(ocdFontname);
            svgFont.setAscent(pdfFont.ascent(0.75));
            svgFont.setDescent(pdfFont.descent(0.2));
            String lowername = ocdFontname.toLowerCase();
            svgFont.setWeight(lowername.contains("bold") ? "bold" : "normal");
            svgFont.setStyle(lowername.contains("italic") ? "italic" : lowername.contains("oblique") ? "oblique" : "normal");
            svgFont.add(new SVGGlyphSP(svgFont, pdfFont.spaceWidth(0.4)));// add white
            // space
            remap.put(ocdFontname + "#c" + pdfFont.spaceChar(), (int) Unicodes.ASCII_SP);
            ocd.fontHandler.map().put(ocdFontname, svgFont);
        }
        return svgFont;
    }


    public void addFonts(Collection<PDFFont> pdfFonts)
    {
        for (PDFFont pdfFont : pdfFonts)
        {
            String ocdFontname = pdfFont.ocdFontname();

            SVGFont svgFont = needFont(ocdFontname, pdfFont);
            int remapCode = 592;// phonetic alphabet which should not be used so much...

            for (int fontcode : pdfFont.showedCodes())
            {
                PDFGlyph glyph = pdfFont.glyph(fontcode);
                String fontKeyCode = ocdFontname + "#c" + fontcode;
                SVGGlyph g = svgFont.get(32);

                boolean isEmptyPath = glyph.path().isEmpty();
                if (isEmptyPath && g != null && g.isPathEmpty() && Math3.equals(glyph.width(), g.width(), 0.001))
                {
                    remap.put(fontKeyCode, 32);
                } else if (remap.has(fontKeyCode))
                {
                    // no problem, just check that path are identical
                    g = svgFont.glyph(remap.get(fontKeyCode));
                    if (glyph.origin() != null)
                    {
//                        Log.debug(this, ".addFonts - update glyph " + fontKeyCode);
                        g.vertOriginX = glyph.origin().x;
                        g.vertOriginY = glyph.origin().y;
                        g.vertAdvY = glyph.advance().y;
                    }
                } else
                {
                    String name = pdfFont.encoding().nameFromCode(fontcode);
                    boolean isSmallCap = name != null && name.endsWith(".sc");
                    int[] unicodes = pdfFont.encoding().unicodeFromCode(fontcode).ints();

                    g = svgFont.find(glyph.path(), unicodes);

//                    Log.debug(this, ".addFonts - " + ocdFontname + " " + name + ": u" + A.String(unicodes) + ", f" + fontcode + ", " + (g == null ? "new" : "old"));

                    if (g != null)
                    {
                        //ok, glyph already exists
                        if (g.code > -1)// which has to be true
                            remap.put(fontKeyCode, g.code);
                    } else
                    {
                        int ocdCode = Ligatures.ResolveChar(unicodes);
                        if (ocdCode < 0)//was not a ligature
                        {
                            ocdCode = unicodes[0];
                            // we want single unicode to avoid rendering conflicts, white space must correspond to empty path
                            if (unicodes.length > 1 || CharRef.IsCtrlOrInvalid(ocdCode) || isSmallCap || isEmptyPath != (ocdCode == 32 || ocdCode == 0x00A0))
                                ocdCode = ++remapCode;
                        }

                        while (svgFont.has(ocdCode))// code conflict
                            ocdCode = ++remapCode;

                        svgFont.add(g = new SVGGlyph(svgFont, name, glyph.path(), glyph.advance(), glyph.origin(), ocdCode));
                        remap.put(fontKeyCode, ocdCode);

                        if (unicodes.length == 1 && CharRef.IsPrivateUseArea(unicodes[0]))
                        {
                            // private use area with Ismall, Nsmall,...
                            if (name.endsWith("small"))
                            {
                                String str = name.replace("small", "");
                                if (str.length() == 1)
                                    unicodes = A.Ints(str.charAt(0));
                                else
                                {
                                    int uni = CharRef.entityUnicode(str);
                                    if (uni > 0)
                                        unicodes = A.Ints(uni);
                                }
                            }
                        }

                        if (unicodes.length == 1 && ocdCode == unicodes[0] && g.name() != null)
                            switch (g.name())
                            {
                                case "registerserif":
                                    if (ocdFontname.toLowerCase().startsWith("symbol"))
                                        unicodes = A.Ints(0x00D2);
                                    else
                                        unicodes = A.Ints(0x00AE);
                                    break;
                            }


                        g.setRemap(unicodes);
                        // should move closeSubpaths to cff reader?
                        // g.closeSubpaths();
                    }
                }
            }

        }
    }

}
