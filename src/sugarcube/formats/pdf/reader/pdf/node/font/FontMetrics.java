package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

import static java.lang.Math.abs;

public class FontMetrics
{
    public static class Flags
    {
        public final static int FIXED_PITCH = 1; // 1;
        public final static int SERIF = 2; // 2;
        // A font is classified as either nonsymbolic or symbolic according to
        // whether all of its characters are members of the StandardEncoding set.
        public final static int SYMBOLIC = 4; // 3;
        public final static int SCRIPT = 8; // 4;
        public final static int NON_SYMBOLIC = 32; // 6;
        public final static int ITALIC = 64; // 7;
        public final static int ALL_CAP = 65536; // 17;
        public final static int SMALL_CAP = 131072; // 18;
        public final static int FORCE_BOLD = 262144; // 19;
        public boolean fixed, serif, symbolic, script, italic, allCap, smallCap, bold;

        public Flags(int value)
        {
            fixed = (value & FIXED_PITCH) == FIXED_PITCH;
            serif = (value & SERIF) == SERIF;
            symbolic = (value & SYMBOLIC) == SYMBOLIC;
            script = (value & SCRIPT) == SCRIPT;
            italic = (value & ITALIC) == ITALIC;
            allCap = (value & ALL_CAP) == ALL_CAP;
            smallCap = (value & SMALL_CAP) == SMALL_CAP;
            bold = (value & FORCE_BOLD) == FORCE_BOLD;
        }
    }

    public String weight = "Normal";
    public boolean isFixedPitch;
    public Flags flags;
    public Rectangle3 fontBBox = new Rectangle3();
    public double ascent, descent, capHeight, stemV, italicAngle, leading, xHeight, stemH, averageWidth, maxWidth, missingWidth;

    public FontMetrics populateFromMap(PDFDictionary map)
    {
        this.flags = new Flags(map.get("Flags").intValue());
        this.fontBBox = new Rectangle3(map.get("FontBBox").doubleValues(0, 0, 1, 1));
        this.italicAngle = map.get("ItalicAngle").doubleValue();
        this.ascent = abs(map.get("Ascent").doubleValue(750));
        this.descent = abs(map.get("Descent").doubleValue(250));
        this.leading = map.get("Leading").doubleValue(-1);
        this.capHeight = map.get("CapHeight").doubleValue(-1);
        this.xHeight = map.get("XHeight").doubleValue(-1);
        this.stemV = map.get("StemV").doubleValue(-1);
        this.stemH = map.get("StemH").doubleValue(-1);
        this.averageWidth = map.get("AvgWidth").doubleValue(-1);
        this.maxWidth = map.get("MaxWidth").doubleValue(-1);
        this.missingWidth = map.get("MissingWidth").intValue(-1);
        return this;
    }

    @Override
    public String toString()
    {
        return "\nAscent[" + ascent + "]" + "\nDescent[" + descent + "]" + "\nCapHeight[" + capHeight + "]" + "\nStemV[" + stemV + "]";
    }
}
