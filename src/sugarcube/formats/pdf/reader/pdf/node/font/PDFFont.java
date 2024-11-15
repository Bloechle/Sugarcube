package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.*;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.ui.gui.Font3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.font.encoding.Encoding;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.PDFString;
import sugarcube.formats.pdf.reader.pdf.util.Wrapper;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.pdf.resources.fonts.FONTS;

import java.awt.*;
import java.util.Map;
import java.util.Set;

public abstract class PDFFont extends PDFNode
{

    public static class Height
    {
        public double height;
        public double ox;
        public double oy;

        public Height(double height, double ox, double oy)
        {
            this.height = height;
            this.ox = ox;
            this.oy = oy;
        }

        @Override
        public String toString()
        {
            return "[" + height + ", " + ox + ", " + oy + "]";
        }
    }

    public static final StringSet ERROR_MSG = new StringSet();
    public static final StringMap<Integer> SPACES = new StringMap<>();
    public static final StringMap<Integer> RENAME = new StringMap<>();
    protected String resourceID;
    protected String basefont = null; // raw font name with prefix and postfix
    protected String fontname = "Helvetica"; // trimmed font name
    protected String fontHash = "Helvetica"; // normalized and unique fontname encoding
    protected FontFormat format = FontFormat.TTF14;
    protected FontDescriptor descriptor = null;
    protected Encoding encoding = Encoding.NoEncoding();
    protected IntMap<Integer> cid2gid = new IntMap<>("CodeToGID", Encoding.UNDEF);
    protected IntMap<Double> widths = new IntMap<>("Widths", 0.0);
    protected IntMap<Height> heights = new IntMap<>("Heights");
    protected Double defaultWidth = null;
    protected Height defaultHeight = null;
    protected PDFGlyph.Interface outlines = null;
    protected Cache<String, PDFGlyph> cache = new Cache<>("Outlines", 300);
    protected int firstChar = -1;
    protected int lastChar = -1;
    protected int spaceChar = -1;
    protected PDFDictionary fontMap;
    protected boolean verticalMode = false;

    static
    {
        SPACES.put("space", 32);
        SPACES.put("spacehackarabic", 32);
        SPACES.put("nonbreakingspace", 160);
        SPACES.put("nbspace", 160);
    }

    protected PDFFont(PDFNode parent, PDFDictionary fontMap)
    {
        super("Font", parent);
        this.fontMap = fontMap;
        this.reference = fontMap.reference();

        if (fontMap.contains("BaseFont"))
            this.basefont = fontMap.get("BaseFont").stringValue();

        if (Str.IsVoid(basefont) && fontMap.contains("Name"))
            this.basefont = fontMap.get("Name").stringValue();

        this.firstChar = fontMap.get("FirstChar").intValue(-1);
        this.lastChar = fontMap.get("LastChar").intValue(-1);

        int charIndex = firstChar;
        for (double width : fontMap.get("Widths").toPDFArray().doubleValues())
            this.widths.put(charIndex++, width);

        // font encoding
        String encName = Encoding.NO_ENCODING;
        String encDiff = "";
        PDFObject encObj = fontMap.get("Encoding").unreference();
        if (encObj.type == PDFObject.Type.Dictionary)
        {
            PDFDictionary encodingMap = encObj.toPDFDictionary();
            encName = encodingMap.get("BaseEncoding").stringValue(encName);
            this.encoding = Encoding.Generate(encName, encObj); // StandardEncoding or NoEncoding

            int currentCode = 0;
            StringBuilder sb = new StringBuilder();
            for (PDFObject difference : encodingMap.get("Differences").toPDFArray())
                if (difference.type == PDFObject.Type.Number)
                    currentCode = difference.intValue();
                else if (difference.type == PDFObject.Type.Name)
                {
                    String name = difference.stringValue();
                    this.encoding.codeToName_.put(currentCode, name);
                    this.encoding.nameToCode_.put(name, currentCode);
                    sb.append(currentCode).append("=").append(name).append(";");
                    currentCode++;
                }
            encDiff = sb.toString();
        } else if (encObj.type == PDFObject.Type.Name)
            this.encoding = Encoding.Generate(encName = encObj.stringValue(), encObj);
        else
            this.encoding = Encoding.Generate(Encoding.NO_ENCODING, encObj);
        // TODO verify this last statement "NoEncoding" or "StandardEncoding" ?
        // toUnicode map
        CMapReader.ReadInternalCMap(fontMap.get("ToUnicode").toPDFStream(), this.encoding.codeToUnicode_);

        if (fontMap.has("FontDescriptor"))
            this.descriptor = new FontDescriptor(this, fontMap.get("FontDescriptor").toPDFDictionary());

        this.fontname = Str.HasData(basefont) ? TrimPrefix(basefont) : (basefont = "Null");

//        Log.debug(this, " - BaseFont=" + basefont + ", fontname=" + fontname);
        this.fontHash = fontname;

        add(this.encoding.pdfEncoding(this));
        add(this.encoding.codeToUnicode_.wrap(this));
        add(this.descriptor);
        add(Wrapper.Wrap(this, this.widths));
        add(Wrapper.Wrap(this, this.heights));

        this.verticalMode = this.encoding.encoding.endsWith("-V");

        if (encName.startsWith("Identity") || encName.equals(Encoding.NO_ENCODING))
            encName = "Identity";

        this.fontHash += "-enc=" + encName + ";";
        if (!encObj.reference().isUndef())
            this.fontHash += "-ref_id=" + encObj.reference().id() + ";";
        if (!encDiff.isEmpty())
            this.fontHash += "-diff=" + encDiff + ";";
    }

    public boolean isCID()
    {
        return !this.cid2gid.isEmpty() || this.encoding.isCID;
    }

    public boolean isFont0IdentityEncoding()
    {
        return isFontType0() && this.encoding.isIdentityEncoding();
    }

    public boolean isFontType0()
    {
        return false;
    }

    public void finalizeConstruction()
    {
        if (this.descriptor != null && !this.descriptor.reference().isUndef())
            this.fontHash += "-desc_id=" + this.descriptor.reference().id() + ";";
        // Log.debug(this, " - fontHash: descriptor="+this.descriptor);
        if (RENAME.hasnt(fontHash))
            RENAME.put(fontHash, 1 + RENAME.size());
    }

    public boolean isVerticalMode()
    {
        return this.verticalMode;
    }

    public int spaceChar()
    {
        return this.spaceChar;
    }

    protected void scaleWidth(double scale)
    {
        if (scale != 1.0)
        {
            IntMap<Double> oldWidths = this.widths;
            double dw = this.widths.def();
            this.widths = new IntMap<>("Widths", dw < 0 ? dw : scale * dw);
            for (Map.Entry<Integer, Double> entry : oldWidths.entrySet())
                this.widths.put(entry.getKey(), entry.getValue() * scale);
        }
    }

    public Unicodes fontCodes(PDFString string)
    {
        return string.codes();
    }

    public Encoding encoding()
    {
        return encoding;
    }

    public static String TrimPrefix(String font)
    {
        return font.length() > 6 && font.charAt(6) == '+' ? font.substring(7) : font;
    }

    public static PDFFont Instance(PDFNode node, String resourceID, PDFObject obj)
    {
        PDFDictionary map = obj.toPDFDictionary();
        PDFFont font = null;
        String type = map.get("Type").stringValue();
        String subtype = map.get("Subtype").stringValue();
        switch (subtype)
        {
            case "Type0":
                font = new PDFFontType0(node, map);
                break;
            case "Type1":
            case "MMType1":
            case "TrueType":
                font = new PDFFontType1(node, map);
                break;
            case "Type3":
                font = new PDFFontType3(node, map);
                break;
            default:
            {
                Log.warn(PDFFont.class, ".Instance - unknown font: " + resourceID + ", ref=" + obj.reference());
                break;
            }
        }

        if (font != null)
            font.resourceID = resourceID;
        return font;
    }

    // public static PDFFont recover(PDFNode node, String resourceID, PDFObject
    // obj)
    // {
    // Reference ref = obj.reference();
    //
    // PDFDocument doc = node.document();
    // PDFEnvironment env = doc == null ? null : doc.env();
    // if (env != null)
    // {
    // Map3<Reference, PDFObject> objects = env.objects();
    // Reference nref = ref;
    // do
    // {
    // nref = ref.prev();
    // PDFObject nobj = objects.get(nref, null);
    // nobj = nobj == null ? null : nobj.unreference();
    // if (nobj != null && nobj.isPDFDictionary())
    // {
    // PDFDictionary map = nobj.toPDFDictionary();
    // if (map.is("Type", "Font"))
    // return PDFFont.instance(node, resourceID, nobj);
    // }
    // } while (nref.id() > -1);
    // }
    //
    // return null;
    // }

    /**
     * This method returns the name used by the resource to identify this font.
     * This is not the "real" name of this font, just a name used as a key for the
     * mapping. The name may be "F1" but not "Times New Roman".
     *
     * @return The identification name of this font
     */
    public String fontID()
    {
        return resourceID;
    }

    public String basefont()
    {
        return basefont;
    }

    public String fontname()
    {
        return fontname;
    }

    // public String ocdFontname()//ensures that different fonts having same
    // fontname are not merged
    // {
    // return SVGFont.normalize(fontname + SVGFont.SEPARATOR +
    // RENAME.get(fontnorm, 0));
    // }

    public String ocdFontname()// ensures that different fonts having same fontname
    // are not merged
    {
        return SVGFont.Normalize(fontname + "_" + RENAME.get(fontHash, 0));
    }

    public double ascent(double min)
    {
        return Math.max(min, ascent());
    }

    public double descent(double min)
    {
        return Math.max(min, descent());
    }

    public double ascent()
    {
        return descriptor == null ? 0.75 : descriptor.metrics.ascent * 0.001;
    }

    public double descent()
    {
        return descriptor == null ? 0.25 : descriptor.metrics.descent * 0.001;
    }

    public double spaceWidth(double recover)
    {
        for (Map.Entry<Integer, Double> entry : this.widths)
        {
            int code = entry.getKey();
            Unicodes unicode = this.encoding.unicodeFromCode(code, null);
            if (unicode != null && unicode.is(Unicodes.ASCII_SP))
            {
                this.spaceChar = code;
                return 0.001f * entry.getValue();
            }
        }
        return recover;
    }

    public static boolean isSpace(Unicodes unicode)
    {
        if (unicode != null && unicode.length() == 1)
            switch (unicode.codeAt(0))
            {
                case 0x000F:// Shift in, yes yes, may be used as space
                case 0x0020:// Space
                case 0x00A0:// No-Break Space
                case 0x1680:// Ogham Space Mark
                case 0x180E:// Mongolian Vowel Separator (MVS)
                case 0x2000:// En quad
                case 0x2001:// Em quad
                case 0x2002:// En Space
                case 0x2003:// Em Space
                case 0x2004:// Three-Per-Em Space
                case 0x2005:// Four-Per-Em Space
                case 0x2006:// Six-Per-Em Space
                case 0x2007:// Figure Space
                case 0x2008:// Punctuation Space
                case 0x2009:// Thin Space
                case 0x200A:// Hair Space
                case 0x200B:// Zero Width Space (ZWSP)
                case 0x200C:// Zero Width Non Joiner (ZWNJ)
                case 0x200D:// Zero Width Joiner (ZWJ)
                case 0x202F:// Narrow No-Break Space
                case 0x205F:// Medium Mathematical Space (MMSP)
                case 0x2060:// Word Joiner (WJ)
                case 0x3000:// Ideographic Space
                case 0xFEFF:// Zero Width No-Break Space
                    return true;
                default:
                    return false;
            }
        return false;
    }

    public static boolean isSpace(String name, Unicodes unicode)
    {
        if (unicode != null && unicode.length() == 1 && !isSpace(unicode))
            return false;
        return SPACES.has(name);
    }

    public PDFGlyph render(int code, float size)
    {
        return glyph(code).render(size, verticalMode);
    }

    // code refer to char code or char CID if this font is a CID one
    public PDFGlyph glyph(int code)
    {
        String cacheKey = "" + code;
        if (this.cache.containsKey(cacheKey))
            return this.cache.get(cacheKey);
        else
        {
            String name = this.encoding.nameFromCode(code, Encoding.NOTDEF);
            Unicodes unicode = this.encoding.unicodeFromCode(code, null);

            boolean isSpace = isSpace(name, unicode);
            if (isSpace)
                unicode = new Unicodes(SPACES.get(name, 32));

            boolean isFont0Id = this.isFont0IdentityEncoding();

//            Log.debug(this, ".glyph - " + fontname + ", code=" + code + ", name=" + name + ", unicode=[" + unicode+"]" + (isSpace ? " <space>" : ""));

            PDFGlyph glyph = null;
            if (!isSpace && this.outlines != null)
            {
                glyph = this.outlines.outline(name, unicode, code);
                // if (this.reference.id() == 6)
                // Log.debug(this, ".glyph - font=" + fontname + ", code=" + code +
                // ", name=" + name + ", uni=" + (unicode == null ? "null" :
                // unicode.string()) + (outline == null ? ", glyph==null" : "") +
                // (isSpace ? ", space" : ""));
                if (glyph == null)
                {
                    glyph = this.tryCompoundOutline(name);
                    if (!isFont0Id && glyph == null)
                        glyph = this.tryOperatingSystemOutline(name, code);
                }
            }

            if (glyph == null)
            {
                glyph = new PDFGlyph();
                if (!isSpace && !isFont0Id)
                {
                    Font3 replaceFont = FONTS.Load(fontname);
                    if (replaceFont == null || replaceFont.replacement() != null)
                    {
                        String msg = code + " in " + this.fontname;
                        if (!ERROR_MSG.yet(msg))
                            Log.debug(this, ".glyph - code " + msg + " not present");
                    }

                    Unicodes uni = this.encoding.unicodeFromCode(code);
                    // unicodes can be < 32 (non drawing control codes, e.g., ETX)
                    if (replaceFont.canDisplayUpTo(uni.stringValue()) < 0)
                    {
                        glyph.path = replaceFont.glyph(uni.characters());
                        glyph.advance = replaceFont.advance(uni.characters());
                    }
                }
            }

            if (glyph.path == null)
                glyph.path = new Path3();
            if (glyph.advance == null)
                glyph.advance = new Point3();

            glyph.fontcode = code;

            double width = 0;
            if (this.widths.has(code))
                glyph.setWidth(0.001f * (width = this.widths.get(code)));
            else if (this.widths.hasDefault() && glyph.width() == 0)
                glyph.setWidth(0.001f * (width = this.widths.def()));

            if (this.heights.has(code))
                glyph.setHeight(this.heights.get(code), 0.001);
            else if (this.defaultHeight != null)
                glyph.setHeight(new Height(defaultHeight.height, width / 2, defaultHeight.oy), 0.001);

            this.cache.put(cacheKey, glyph);
            return glyph;
        }
    }

    public PDFGlyph tryOperatingSystemOutline(String name, int code)
    {
        Font3 font = FONTS.Load(fontname);
        if (font != null)
        {
            PDFGlyph outline = new PDFGlyph();
            Unicodes uni = this.encoding.unicodeFromCode(code);
            // unicodes can be < 32 (non drawing control codes, e.g., ETX)
            if (font.canDisplayUpTo(uni.stringValue()) < 0)
            {
                outline.path = font.glyph(uni.characters());
                outline.advance = font.advance(uni.characters());
                return outline;
            }
        }

        return null;
    }

    public PDFGlyph tryCompoundOutline(String name)
    {
        if (name == null || name.length() < 2)
            return null;
        PDFGlyph letter = null;
        String prefix = name.substring(0, 1);
        String suffix = name.substring(1);
        if (suffix != null && Encoding.NAME_TO_UNICODE.containsKey(prefix) && Encoding.NAME_TO_UNICODE.containsKey(suffix))
        {
            letter = this.outlines.outline(prefix, encoding.unicodeFromName(prefix), encoding.codeFromName(prefix));
            PDFGlyph accent = this.outlines.outline(suffix, encoding.unicodeFromName(suffix), encoding.codeFromName(suffix));
            if (letter != null && !letter.isEmpty() && accent != null && !accent.isEmpty())
                letter.path.append(accent.path, false);
        }
        return letter;
    }

    public Set<Integer> showedCodes()
    {
        return this.encoding.showedCodes;
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        int size = 48;
        int x = size / 2;
        int y = size;
        g.setFont(g.getFont().deriveFont(11f));
        g.setColor(Color.BLACK);
        for (int code : encoding.showedCodes)
        {
            String name = encoding.nameFromCode(code);
            Unicodes uni = encoding.unicodeFromName(name);

            if (uni == null)
                uni = encoding.unicodeFromCode(code);

            if (x > g.width() - 2 * size / 2)
            {
                x = size / 2;
                y += (2.8 * size / 2);
            }

            PDFGlyph glyph = this.glyph(code).render(size, verticalMode);
            // System.out.println("font="+this.fontname+" glyph="+code+" path="+glyph.path.stringValue(0));
            // XED.LOG.warn(this,".paint - empty glyph: fontname="+fontname+" code="+code+" path="+glyph.path.stringValue(0.1f));

            g.setColor(Color.BLACK);
            g.fill(new Transform3(1, 0, 0, 1, x, y).transform(glyph.path));
            g.setColor(Color.GREEN.darker());
            g.fill(new Circle3(x, y, 2));
            g.draw(new Line3(x, y, x + (int) (glyph.width()), y));
            g.drawCenter(name + " " + (uni == null ? "null" : uni.stringValue()) + " " + code, x + glyph.width() / 2, y + size / 3, g.getFont(),
                    Color3.GREEN_DARK);
            x += 2 * size;
        }
    }

    @Override
    public String sticker()
    {
        return resourceID + " » " + this.getClass().getSimpleName() + "[" + format + "] - " + fontname + " " + reference;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(this.getClass().getSimpleName());
        sb.append("\nFormat[").append(this.format).append("]");
        sb.append("\nFontName[").append(resourceID).append("]");
        sb.append("\nBaseFont[").append(basefont).append("]");
        sb.append("\nFontname[").append(fontname).append("]");
        sb.append("\nFonthash[").append(fontHash).append("]");
        sb.append("\nAscent[").append(ascent()).append("]");
        sb.append("\nDescent[").append(descent()).append("]");
        sb.append("\nTransform[").append(outlines == null ? "null" : outlines.transform()).append("]");
        sb.append("\nWidths[").append(widths.size()).append("]");
        sb.append("\nDefaultWidth[").append(widths.hasDefault() ? widths.def() : "-1").append("]");
        sb.append("\nHeights[").append(heights.size()).append("]");
        sb.append("\nDefaultHeight[").append(heights.hasDefault() ? heights.def() : "-1").append("]");
        sb.append("\nFirstChar[").append(this.firstChar).append("]");
        sb.append("\nLastChar[").append(this.lastChar).append("]");
        sb.append(encoding);
        return sb.toString();
    }

//    private static Font3 ReplaceFont(String name)
//    {
//        name = name.replace("CourierNewPSMT", "Courier New");
//
//        if (OS_FONTS.has(name))
//            return OS_FONTS.get(name);
//
//        String fontname = Str.ReplacePairs(name, "Bold", "", "Italic", "", "_", " ", "-", " ", "  ", " ").trim();
//        switch (fontname)
//        {
//            case "CourierNewPSMT":
//            case "CourierNew":
//            case "Courier":
//                fontname = "Courier New";
//                break;
//        }
//
//        if (Font3.ExistsOSFont(fontname))
//        {
//            String style = name.toLowerCase().replace("_", "").replace("-", "").replace(" ", "");
//            int flags = 0;
//            if (style.contains("bold"))
//                flags |= Font.BOLD;
//            if (style.contains("italic"))
//                flags |= Font.ITALIC;
//            Font3 font = new Font3(fontname, flags, 1);
//            OS_FONTS.put(name, font);
//            return font;
//        }
//        return null;
//    }
//
//    private static Font3 DefaultFont(String name)
//    {
//        name = name.toLowerCase().replace("_", "").replace("-", "").replace(" ", "");
//        if (name.contains("bolditalic"))
//            return DEFAULT_FONT_BOLDITALIC;
//        else if (name.contains("bold"))
//            return DEFAULT_FONT_BOLD;
//        else if (name.contains("italic"))
//            return DEFAULT_FONT_ITALIC;
//        else
//            return DEFAULT_FONT;
//    }
}
