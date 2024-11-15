package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.cff.StandardFonts;
import sugarcube.formats.pdf.reader.pdf.node.cff.T1Glyphs;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CFF
{
    /**
     * decoding table for Expert
     */
    private static final int[] ExpertSubCharset =
            { // 87
                    // elements
                    0, 1, 231, 232, 235, 236, 237, 238, 13, 14, 15, 99, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 27, 28, 249, 250, 251, 253, 254, 255, 256,
                    257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 109, 110, 267, 268, 269, 270, 272, 300, 301, 302, 305, 314, 315, 158, 155, 163, 320, 321, 322,
                    323, 324, 325, 326, 150, 164, 169, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345, 346};
    /**
     * lookup table for names for type 1C glyphs
     */
    public static final String type1CStdStrings[] =
            { // 391
                    // elements
                    ".notdef", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quoteright", "parenleft", "parenright", "asterisk",
                    "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon",
                    "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
                    "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "quoteleft", "a", "b",
                    "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar",
                    "braceright", "asciitilde", "exclamdown", "cent", "sterling", "fraction", "yen", "florin", "section", "currency", "quotesingle", "quotedblleft",
                    "guillemotleft", "guilsinglleft", "guilsinglright", "fi", "fl", "endash", "dagger", "daggerdbl", "periodcentered", "paragraph", "bullet",
                    "quotesinglbase", "quotedblbase", "quotedblright", "guillemotright", "ellipsis", "perthousand", "questiondown", "grave", "acute", "circumflex",
                    "tilde", "macron", "breve", "dotaccent", "dieresis", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "emdash", "AE", "ordfeminine",
                    "Lslash", "Oslash", "OE", "ordmasculine", "ae", "dotlessi", "lslash", "oslash", "oe", "germandbls", "onesuperior", "logicalnot", "mu",
                    "trademark", "Eth", "onehalf", "plusminus", "Thorn", "onequarter", "divide", "brokenbar", "degree", "thorn", "threequarters", "twosuperior",
                    "registered", "minus", "eth", "multiply", "threesuperior", "copyright", "Aacute", "Acircumflex", "Adieresis", "Agrave", "Aring", "Atilde",
                    "Ccedilla", "Eacute", "Ecircumflex", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Ntilde", "Oacute", "Ocircumflex",
                    "Odieresis", "Ograve", "Otilde", "Scaron", "Uacute", "Ucircumflex", "Udieresis", "Ugrave", "Yacute", "Ydieresis", "Zcaron", "aacute",
                    "acircumflex", "adieresis", "agrave", "aring", "atilde", "ccedilla", "eacute", "ecircumflex", "edieresis", "egrave", "iacute", "icircumflex",
                    "idieresis", "igrave", "ntilde", "oacute", "ocircumflex", "odieresis", "ograve", "otilde", "scaron", "uacute", "ucircumflex", "udieresis",
                    "ugrave", "yacute", "ydieresis", "zcaron", "exclamsmall", "Hungarumlautsmall", "dollaroldstyle", "dollarsuperior", "ampersandsmall",
                    "Acutesmall", "parenleftsuperior", "parenrightsuperior", "twodotenleader", "onedotenleader", "zerooldstyle", "oneoldstyle", "twooldstyle",
                    "threeoldstyle", "fouroldstyle", "fiveoldstyle", "sixoldstyle", "sevenoldstyle", "eightoldstyle", "nineoldstyle", "commasuperior",
                    "threequartersemdash", "periodsuperior", "questionsmall", "asuperior", "bsuperior", "centsuperior", "dsuperior", "esuperior", "isuperior",
                    "lsuperior", "msuperior", "nsuperior", "osuperior", "rsuperior", "ssuperior", "tsuperior", "ff", "ffi", "ffl", "parenleftinferior",
                    "parenrightinferior", "Circumflexsmall", "hyphensuperior", "Gravesmall", "Asmall", "Bsmall", "Csmall", "Dsmall", "Esmall", "Fsmall", "Gsmall",
                    "Hsmall", "Ismall", "Jsmall", "Ksmall", "Lsmall", "Msmall", "Nsmall", "Osmall", "Psmall", "Qsmall", "Rsmall", "Ssmall", "Tsmall", "Usmall",
                    "Vsmall", "Wsmall", "Xsmall", "Ysmall", "Zsmall", "colonmonetary", "onefitted", "rupiah", "Tildesmall", "exclamdownsmall", "centoldstyle",
                    "Lslashsmall", "Scaronsmall", "Zcaronsmall", "Dieresissmall", "Brevesmall", "Caronsmall", "Dotaccentsmall", "Macronsmall", "figuredash",
                    "hypheninferior", "Ogoneksmall", "Ringsmall", "Cedillasmall", "questiondownsmall", "oneeighth", "threeeighths", "fiveeighths", "seveneighths",
                    "onethird", "twothirds", "zerosuperior", "foursuperior", "fivesuperior", "sixsuperior", "sevensuperior", "eightsuperior", "ninesuperior",
                    "zeroinferior", "oneinferior", "twoinferior", "threeinferior", "fourinferior", "fiveinferior", "sixinferior", "seveninferior", "eightinferior",
                    "nineinferior", "centinferior", "dollarinferior", "periodinferior", "commainferior", "Agravesmall", "Aacutesmall", "Acircumflexsmall",
                    "Atildesmall", "Adieresissmall", "Aringsmall", "AEsmall", "Ccedillasmall", "Egravesmall", "Eacutesmall", "Ecircumflexsmall", "Edieresissmall",
                    "Igravesmall", "Iacutesmall", "Icircumflexsmall", "Idieresissmall", "Ethsmall", "Ntildesmall", "Ogravesmall", "Oacutesmall", "Ocircumflexsmall",
                    "Otildesmall", "Odieresissmall", "OEsmall", "Oslashsmall", "Ugravesmall", "Uacutesmall", "Ucircumflexsmall", "Udieresissmall", "Yacutesmall",
                    "Thornsmall", "Ydieresissmall", "001.000", "001.001", "001.002", "001.003", "Black", "Bold", "Book", "Light", "Medium", "Regular", "Roman",
                    "Semibold"};
    /**
     * lookup data to convert Expert values
     */
    private static final int ExpertCharset[] =
            { // 166
                    // elements
                    0, 1, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 13, 14, 15, 99, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 27, 28, 249, 250, 251,
                    252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 109, 110, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278,
                    279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307,
                    308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 158, 155, 163, 319, 320, 321, 322, 323, 324, 325, 326, 150, 164, 169, 327, 328, 329, 330,
                    331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359,
                    360, 361, 362, 363, 364, 365, 366, 367, 368, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378};

    private static final int[] ISOAdobeCharset;

    static
    {
        ISOAdobeCharset = new int[229];
        for (int i = 0; i < 229; i++)
            ISOAdobeCharset[i] = i;
    }

    // flag if we store reverse map for index to CMAP (use dbt OTF conversion)
    boolean trackIndices = false;
    protected String embeddedFontName = null, embeddedFamilyName = null, copyright = null;
    public T1Glyphs glyphs;

    protected int embeddedEnc = StandardFonts.STD;
    protected String[] charDifferences;

    //number of glyphs - 65536 for CID fonts
    protected int maxCharCount = 256;
    protected boolean hasEncoding = true;

    String[] charDifferencesTable;
    protected Map diffLookup = null;
    private double[] fontMatrix = null;
    private float[] fontBBox =
            {0f, 0f, 1000f, 1000f};

    protected int fontEnc = -1;
    protected int italicAngle = 0;

    static final boolean debugFont = false;
    static final boolean debugDictionary = false;
    int ROS = -1, CIDFontVersion = 0, CIDFontRevision = 0, CIDFontType = 0, CIDcount = 0, UIDBase = -1, FDArray = -1, FDSelect = -1;
    final static String[] OneByteCCFDict =
            {"version", "Notice", "FullName", "FamilyName", "Weight", "FontBBox", "BlueValues", "OtherBlues", "FamilyBlues", "FamilyOtherBlues", "StdHW",
                    "StdVW", "Escape", "UniqueID", "XUID", "charset", "Encoding", "CharStrings", "Private", "Subrs", "defaultWidthX", "nominalWidthX", "-reserved-",
                    "-reserved-", "-reserved-", "-reserved-", "-reserved-", "-reserved-", "shortint", "longint", "BCD", "-reserved-"};
    final static String[] TwoByteCCFDict =
            {"Copyright", "isFixedPitch", "ItalicAngle", "UnderlinePosition", "UnderlineThickness", "PaintType", "CharstringType", "FontMatrix", "StrokeWidth",
                    "BlueScale", "BlueShift", "BlueFuzz", "StemSnapH", "StemSnapV", "ForceBold", "-reserved-", "-reserved-", "LanguageGroup", "ExpansionFactor",
                    "initialRandomSeed", "SyntheticBase", "PostScript", "BaseFontName", "BaseFontBlend", "-reserved-", "-reserved-", "-reserved-", "-reserved-",
                    "-reserved-", "-reserved-", "ROS", "CIDFontVersion", "CIDFontRevision", "CIDFontType", "CIDCount", "UIDBase", "FDArray", "FDSelect",
                    "FontName"};
    // current location in file
    private int top, charset, enc, charstrings, stringIdx, stringStart, stringOffSize;
    private int privateDict = -1, privateDictOffset = -1;
    private int defaultWidthX = 0, nominalWidthX = 0;
    private String fontname;
    public boolean isCID = false;

    public CFF(String fontname, boolean isCID, byte[] bytes)
    {
        this.fontname = fontname;
        this.isCID = isCID;
        this.glyphs = new T1Glyphs(this.isCID);
        try
        {
            readType1CFontFile(bytes);
        } catch (Exception e)
        {
            Log.warn(this, " - unable to parse CFF font file: " + e);
        }
    }

    public double[] fontMatrix()
    {
        return fontMatrix == null ? new double[]{0.001, 0, 0, 0.001, 0, 0} : fontMatrix;
    }

    public String getBaseFontName()
    {
        return fontname;
    }

    /**
     * Handle encoding for type1C fonts. Also used for CIDFontType0C
     */
    private void readType1CFontFile(byte[] fontDataAsArray) throws Exception
    {

        glyphs.setis1C(true);

        int start; // pointers within table
        int size = 2;

        /**
         * read Header
         */
        int major, minor;

        major = fontDataAsArray[0];
        minor = fontDataAsArray[1];

        if (major != 1 || minor != 0)
            System.out.println("1C  format " + major + ':' + minor + " not fully supported");

        if (debugFont)
            System.out.println("major=" + major + " minor=" + minor);

        // read header size to workout start of names index

        top = fontDataAsArray[2];
        /**
         * read names index
         */
        // read name index for the first font
        int count, offsize;

        count = CFF.ReadWord(fontDataAsArray, top, size);
        offsize = fontDataAsArray[top + size];

        /**
         * get last offset and use to move to top dict index
         */
        top += (size + 1); // move pointer to start of font names
        start = top + (count + 1) * offsize - 1; // move pointer to end of offsets

        top = start + CFF.ReadWord(fontDataAsArray, top + count * offsize, offsize);
        /**
         * read the dict index
         */
        count = CFF.ReadWord(fontDataAsArray, top, size);
        offsize = fontDataAsArray[top + size];

        top += (size + 1); // update pointer
        start = top + (count + 1) * offsize - 1;

        int dicStart, dicEnd;

        dicStart = start + CFF.ReadWord(fontDataAsArray, top, offsize);
        dicEnd = start + CFF.ReadWord(fontDataAsArray, top + offsize, offsize);

        String[] strings = readStringIndex(fontDataAsArray, start, offsize, count);

        /**
         * read global subroutines (top set by Strings code)
         */
        readGlobalSubRoutines(fontDataAsArray);
        decodeDictionary(fontDataAsArray, dicStart, dicEnd, strings);
        /**
         * allow for subdictionaries in CID font
         */
        if (FDSelect != -1)
        {
            int nextDic = FDArray;
            count = CFF.ReadWord(fontDataAsArray, nextDic, size);
            offsize = fontDataAsArray[nextDic + size];
            nextDic += (size + 1); // update pointer
            start = nextDic + (count + 1) * offsize - 1;
            dicStart = start + CFF.ReadWord(fontDataAsArray, nextDic, offsize);
            dicEnd = start + CFF.ReadWord(fontDataAsArray, nextDic + offsize, offsize);
            decodeDictionary(fontDataAsArray, dicStart, dicEnd, strings);
        }

        /**
         * get number of glyphs from charstrings index
         */
        top = charstrings;

        int nGlyphs = CFF.ReadWord(fontDataAsArray, top, size); // start of glyph index

        if (debugFont)
            System.out.println("nGlyphs=" + nGlyphs);
        int[] names = CFF.ReadCharset(charset, nGlyphs, charstrings, fontDataAsArray);
        if (debugFont)
        {
            System.out.println("=======charset===============");
            int count2 = names.length;
            for (int jj = 0; jj < count2; jj++)
                System.out.println(jj + " " + names[jj]);
            System.out.println("=======Encoding===============");
        }

        /**
         * set encoding if not set
         */
        setEncoding(fontDataAsArray, nGlyphs, names);

        /**
         * read glyph index
         */
        top = charstrings;
        readGlyphs(fontDataAsArray, nGlyphs, names);

        if (privateDict != -1)
        {
            decodeDictionary(fontDataAsArray, privateDict, privateDictOffset + privateDict, strings);
            top = privateDict + privateDictOffset;
            int len, nSubrs;
            len = fontDataAsArray.length;
            if (top + 2 < len)
            {
                nSubrs = CFF.ReadWord(fontDataAsArray, top, size);
                if (nSubrs > 0)
                    readSubrs(fontDataAsArray, nSubrs);
            } else if (debugFont || debugDictionary)
                System.out.println("Private subroutine out of range");
        }
    }

    /**
     * pick up encoding from embedded font
     */
    private void setEncoding(byte[] fontDataAsArray, int nGlyphs, int[] names)
    {
        if (debugFont)
            System.out.println("Enc=" + enc);

        // read encoding (glyph -> code mapping)
        if (enc == 0)
        {
            embeddedEnc = StandardFonts.STD;
            if (fontEnc == -1)
                putFontEncoding(StandardFonts.STD);

            if (isCID)
                // store values for lookup on text
                try
                {
                    // Log.debug(this, ".setEncoding - CID: " + this.embeddedFontName);
                    String name;
                    for (int i = 1; i < nGlyphs; ++i)
                        if (names[i] < 391)
                        {
                            name = CFF.ReadString(fontDataAsArray, names[i], stringIdx, stringStart, stringOffSize);
                            putMappedChar(names[i], StandardFonts.getUnicodeName(name));
                        }
                } catch (Exception ee)
                {
                    ee.printStackTrace();
                }
        } else if (enc == 1)
        {
            embeddedEnc = StandardFonts.MACEXPERT;
            if (fontEnc == -1)
                putFontEncoding(StandardFonts.MACEXPERT);
        } else
        { // custom mapping
            if (debugFont)
                System.out.println("custom mapping");
            top = enc;
            int encFormat, c;
            encFormat = (fontDataAsArray[top++] & 0xff);
            String name;
            if ((encFormat & 0x7f) == 0)
            { // format 0
                int nCodes = 1 + (fontDataAsArray[top++] & 0xff);
                if (nCodes > nGlyphs)
                    nCodes = nGlyphs;
                for (int i = 1; i < nCodes; ++i)
                {
                    c = fontDataAsArray[top++] & 0xff;
                    name = CFF.ReadString(fontDataAsArray, names[i], stringIdx, stringStart, stringOffSize);
                    putChar(c, name);
                }
            } else if ((encFormat & 0x7f) == 1)
            { // format 1

                int nRanges = (fontDataAsArray[top++] & 0xff);
                int nCodes = 1;
                for (int i = 0; i < nRanges; ++i)
                {
                    c = (fontDataAsArray[top++] & 0xff);
                    int nLeft = (fontDataAsArray[top++] & 0xff);
                    for (int j = 0; j <= nLeft && nCodes < nGlyphs; ++j)
                    {
                        name = CFF.ReadString(fontDataAsArray, names[nCodes], stringIdx, stringStart, stringOffSize);
                        putChar(c, name);
                        nCodes++;
                        c++;
                    }
                }
            }

            if ((encFormat & 0x80) != 0)
            { // supplementary encodings
                int nSups = (fontDataAsArray[top++] & 0xff);
                for (int i = 0; i < nSups; ++i)
                {
                    c = (fontDataAsArray[top++] & 0xff);
                    int sid;
                    sid = CFF.ReadWord(fontDataAsArray, top, 2);
                    top += 2;
                    name = CFF.ReadString(fontDataAsArray, sid, stringIdx, stringStart, stringOffSize);
                    putChar(c, name);
                }
            }
        }
    }

    // LILYPONDTOOL
    private final void readSubrs(byte[] fontDataAsArray, int nSubrs) throws Exception
    {

        int subrOffSize;

        subrOffSize = fontDataAsArray[top + 2];

        top += 3;
        int subrIdx = top;
        int subrStart = top + (nSubrs + 1) * subrOffSize - 1;

        int nextTablePtr = top + nSubrs * subrOffSize;

        if (nextTablePtr < fontDataAsArray.length) // allow for table at end of file
            top = subrStart + CFF.ReadWord(fontDataAsArray, nextTablePtr, subrOffSize);
        else
            top = fontDataAsArray.length - 1;

        int[] subrOffset = new int[nSubrs + 2];
        int ii = subrIdx;
        for (int jj = 0; jj < nSubrs + 1; jj++)
        {
            if ((ii + subrOffSize) < fontDataAsArray.length)
                subrOffset[jj] = subrStart + CFF.ReadWord(fontDataAsArray, ii, subrOffSize);

            ii += subrOffSize;
        }
        subrOffset[nSubrs + 1] = top;

        glyphs.setLocalBias(CFF.CalculateSubroutineBias(nSubrs));

        // read the glyphs and store
        int current = subrOffset[0];

        for (int jj = 1; jj < nSubrs + 1; jj++)
        {

            // skip if out of bounds
            if (current == 0 || subrOffset[jj] > fontDataAsArray.length || subrOffset[jj] < 0 || subrOffset[jj] == 0)
                continue;

            int length = subrOffset[jj] - current;

            if (length > 0)
            {
                byte[] nextSub = new byte[length];

                System.arraycopy(fontDataAsArray, current, nextSub, 0, length);

                glyphs.setCharString("subrs" + (jj - 1), nextSub);
            }

            current = subrOffset[jj];

        }
    }

    private final void readGlyphs(byte[] fontDataAsArray, int nGlyphs, int[] names) throws Exception
    {

        int glyphOffSize = fontDataAsArray[top + 2];
        top += 3;
        int glyphIdx = top;
        int glyphStart = top + (nGlyphs + 1) * glyphOffSize - 1;

        top = glyphStart + CFF.ReadWord(fontDataAsArray, top + nGlyphs * glyphOffSize, glyphOffSize);

        int[] glyphoffset = new int[nGlyphs + 2];

        int ii = glyphIdx;

        // read the offsets
        for (int jj = 0; jj < nGlyphs + 1; jj++)
        {
            glyphoffset[jj] = glyphStart + CFF.ReadWord(fontDataAsArray, ii, glyphOffSize);
            ii = ii + glyphOffSize;
        }

        glyphoffset[nGlyphs + 1] = top;

        // read the glyphs and store
        int current = glyphoffset[0];
        String glyphName;
        byte[] nextGlyph;
        for (int jj = 1; jj < nGlyphs + 1; jj++)
        {

            nextGlyph = new byte[glyphoffset[jj] - current]; // read name of glyph

            // get data for the glyph
            for (int c = current; c < glyphoffset[jj]; c++)
                nextGlyph[c - current] = fontDataAsArray[c];

            if (isCID)
                glyphName = String.valueOf(names[jj - 1]);
            else
                glyphName = CFF.ReadString(fontDataAsArray, names[jj - 1], stringIdx, stringStart, stringOffSize);
            if (debugFont)
                System.out.println("glyph= " + glyphName + " start=" + current + " length=" + glyphoffset[jj] + " isCID=" + isCID);

            glyphs.setCharString(glyphName, nextGlyph);

            current = glyphoffset[jj];

            if (trackIndices)
                glyphs.setIndexForCharString(jj, glyphName);
        }
    }

    private final void readGlobalSubRoutines(byte[] fontDataAsArray) throws Exception
    {
        int subOffSize, count;

        subOffSize = (fontDataAsArray[top + 2] & 0xff);
        count = CFF.ReadWord(fontDataAsArray, top, 2);

        top += 3;
        if (count > 0)
        {

            int idx = top;
            int start = top + (count + 1) * subOffSize - 1;
            top = start + CFF.ReadWord(fontDataAsArray, top + count * subOffSize, subOffSize);

            int[] offset = new int[count + 2];

            int ii = idx;

            // read the offsets
            for (int jj = 0; jj < count + 1; jj++)
            {
                offset[jj] = start + CFF.ReadWord(fontDataAsArray, ii, subOffSize);
                ii = ii + subOffSize;

            }

            offset[count + 1] = top;

            glyphs.setGlobalBias(CFF.CalculateSubroutineBias(count));

            // read the subroutines and store
            int current = offset[0];
            for (int jj = 1; jj < count + 1; jj++)
            {

                ByteArrayOutputStream nextStream = new ByteArrayOutputStream();
                for (int c = current; c < offset[jj]; c++)
                    nextStream.write(fontDataAsArray[c]);
                nextStream.close();

                // store
                glyphs.setCharString("global" + (jj - 1), nextStream.toByteArray());

                // setGlobalSubroutine(new Integer(jj-1+bias),nextStream.toByteArray());
                current = offset[jj];

            }
        }
    }

    private void decodeDictionary(byte[] fontDataAsArray, int dicStart, int dicEnd, String[] strings)
    {

        boolean fdReset = false;

        int p = dicStart, nextVal, key;
        int i = 0;
        double[] op = new double[48]; // current operand in dictionary

        while (p < dicEnd)
        {

            nextVal = fontDataAsArray[p] & 0xFF;

            if (nextVal <= 27 || nextVal == 31)
            { // operator

                key = nextVal;

                p++;

                if (debugDictionary && key != 12)
                    System.out.println(key + " (1) " + OneByteCCFDict[key]);

                if (key == 0x0c)
                { // handle escaped keys

                    key = fontDataAsArray[p] & 0xFF;

                    if (debugDictionary)
                        System.out.println(key + " (2) " + TwoByteCCFDict[key]);

                    p++;

                    if (key != 36 && key != 37 && key != 7 && FDSelect != -1)
                    {
                        if (debugDictionary)
                        {
                            System.out.println("Ignored as part of FDArray ");

                            for (int ii = 0; ii < 6; ii++)
                                System.out.println(op[ii]);
                        }
                    } else if (key == 2)
                    { // italic

                        italicAngle = (int) op[0];
                        if (debugDictionary)
                            System.out.println("Italic=" + op[0]);

                    } else if (key == 7)
                    { // fontMatrix
                        //if (!hasFontMatrix)
                        //System.arraycopy(op, 0, fontMatrix, 0, 6);

                        if (fontMatrix == null)
                            fontMatrix = new double[]{op[0], op[1], op[2], op[3], op[4], op[5]};
                        else
                            for(int ii=0; ii<fontMatrix.length; ii++)
                                fontMatrix[ii] *= op[ii];

                        if (debugDictionary)
                        {
                            System.out.println("FontMatrix=" + op[0] + " " + op[1] + " " + op[2] + " " + op[3] + " " + op[4] + " " + op[5] + "(" + fontname + ")");
                        }

                    } else if (key == 30)
                    { // ROS
                        ROS = (int) op[0];
                        isCID = true;
                        if (debugDictionary)
                            System.out.println(op[0]);
                    } else if (key == 31)
                    { // CIDFontVersion
                        CIDFontVersion = (int) op[0];
                        if (debugDictionary)
                            System.out.println(op[0]);
                    } else if (key == 32)
                    { // CIDFontRevision
                        CIDFontRevision = (int) op[0];
                        if (debugDictionary)
                            System.out.println(op[0]);
                    } else if (key == 33)
                    { // CIDFontType
                        CIDFontType = (int) op[0];
                        if (debugDictionary)
                            System.out.println(op[0]);
                    } else if (key == 34)
                    { // CIDcount
                        CIDcount = (int) op[0];
                        if (debugDictionary)
                            System.out.println(op[0]);
                    } else if (key == 35)
                    { // UIDBase
                        UIDBase = (int) op[0];
                        if (debugDictionary)
                            System.out.println(op[0]);
                    } else if (key == 36)
                    { // FDArray
                        FDArray = (int) op[0];
                        if (debugDictionary)
                            System.out.println(op[0]);

                    } else if (key == 37)
                    { // FDSelect
                        FDSelect = (int) op[0];

                        fdReset = true;

                        if (debugDictionary)
                            System.out.println(op[0]);
                    } else if (key == 0)
                    { // copyright

                        int id = (int) op[0];
                        if (id > 390)
                            id = id - 390;
                        copyright = strings[id];
                        if (debugDictionary)
                            System.out.println("copyright= " + copyright);
                    } else if (key == 21)
                    { // Postscript

                        // postscriptFontName=strings[id];
                        if (debugDictionary)
                        {
                            int id = (int) op[0];
                            if (id > 390)
                                id = id - 390;

                            System.out.println("Postscript= " + strings[id]);
                            System.out.println(TwoByteCCFDict[key] + ' ' + op[0]);
                        }
                    } else if (key == 22)
                    { // BaseFontname

                        // baseFontName=strings[id];
                        if (debugDictionary)
                        {

                            int id = (int) op[0];
                            if (id > 390)
                                id = id - 390;

                            System.out.println("BaseFontname= " + embeddedFontName);
                            System.out.println(TwoByteCCFDict[key] + ' ' + op[0]);
                        }
                    } else if (key == 38)
                    { // fullname

                        // fullname=strings[id];
                        if (debugDictionary)
                        {

                            int id = (int) op[0];
                            if (id > 390)
                                id = id - 390;

                            System.out.println("fullname= " + strings[id]);
                            System.out.println(TwoByteCCFDict[key] + ' ' + op[0]);
                        }

                    } else if (debugDictionary)
                        System.out.println(op[0]);

                } else if (key == 2)
                { // fullname

                    int id = (int) op[0];
                    if (id > 390)
                        id = id - 390;
                    embeddedFontName = strings[id];
                    if (debugDictionary)
                    {
                        System.out.println("name= " + embeddedFontName);
                        System.out.println(OneByteCCFDict[key] + ' ' + op[0]);
                    }

                } else if (key == 3)
                { // familyname

                    // embeddedFamilyName=strings[id];
                    if (debugDictionary)
                    {

                        int id = (int) op[0];
                        if (id > 390)
                            id = id - 390;

                        System.out.println("FamilyName= " + embeddedFamilyName);
                        System.out.println(OneByteCCFDict[key] + ' ' + op[0]);
                    }

                } else if (key == 5)
                { // fontBBox
                    if (debugDictionary)
                        for (int ii = 0; ii < 4; ii++)
                            System.out.println(op[ii]);
                    for (int ii = 0; ii < 4; ii++)
                        // System.out.println(" "+ii+" "+op[ii]);
                        this.fontBBox[ii] = (float) op[ii];

                    // hasFontBBox=true;
                } else if (key == 0x0f)
                { // charset
                    charset = (int) op[0];

                    if (debugDictionary)
                        System.out.println(op[0]);

                } else if (key == 0x10)
                { // encoding
                    enc = (int) op[0];

                    if (debugDictionary)
                        System.out.println(op[0]);

                } else if (key == 0x11)
                { // charstrings
                    charstrings = (int) op[0];

                    if (debugDictionary)
                        System.out.println(op[0]);

                    // System.out.println("charStrings="+charstrings);
                } else if (key == 18 && glyphs.is1C())
                { // readPrivate
                    privateDict = (int) op[1];
                    privateDictOffset = (int) op[0];

                    if (debugDictionary)
                        System.out.println("privateDict=" + op[0] + " Offset=" + op[1]);

                } else if (key == 20)
                { // defaultWidthX
                    defaultWidthX = (int) op[0];
                    if (glyphs != null)
                        glyphs.setWidthValues(defaultWidthX, nominalWidthX);

                    if (debugDictionary)
                        System.out.println("defaultWidthX=" + op[0]);

                } else if (key == 21)
                { // nominalWidthX
                    nominalWidthX = (int) op[0];
                    if (glyphs != null)
                        glyphs.setWidthValues(defaultWidthX, nominalWidthX);

                    if (debugDictionary)
                        System.out.println("nominalWidthX=" + op[0]);

                } else if (debugDictionary)
                    // System.out.println(p+" "+key+" "+T1CcharCodes1Byte[key]+" <<<"+op);
                    System.out.println("Other value " + key);
                /**
                 * if(op <type1CStdStrings.length)
                 * System.out.println(type1CStdStrings[(int)op]); else if((op-390)
                 * <strings.length) System.out.println("interesting key:"+key);
                 */
                // System.out.println(p+" "+key+" "+raw1ByteValues[key]+" <<<"+op);
                i = 0;

            } else
            {
                p = glyphs.getNumber(fontDataAsArray, p, op, i, false);
                i++;
            }
        }

        // reset
        if (!fdReset)
            FDSelect = -1;

    }

    private String[] readStringIndex(byte[] fontDataAsArray, int start, int offsize, int count)
    {

        int nStrings;

        top = start + CFF.ReadWord(fontDataAsArray, top + count * offsize, offsize);
        // start of string index
        nStrings = CFF.ReadWord(fontDataAsArray, top, 2);
        stringOffSize = fontDataAsArray[top + 2];

        top += 3;
        stringIdx = top;
        stringStart = top + (nStrings + 1) * stringOffSize - 1;

        top = stringStart + CFF.ReadWord(fontDataAsArray, top + nStrings * stringOffSize, stringOffSize);

        int[] offsets = new int[nStrings + 2];
        String[] strings = new String[nStrings + 2];

        int ii = stringIdx;
        // read the offsets
        for (int jj = 0; jj < nStrings + 1; jj++)
        {

            offsets[jj] = CFF.ReadWord(fontDataAsArray, ii, stringOffSize);
            // content[ii] & 0xff;
            // getWord(content,ii,stringOffSize);
            ii = ii + stringOffSize;

        }

        offsets[nStrings + 1] = top - stringStart;

        // read the strings
        int current = 0;
        StringBuffer nextString;
        for (int jj = 0; jj < nStrings + 1; jj++)
        {

            int stringSize = (offsets[jj] - current);
            if (stringSize > 10000)
            {
                nextString = new StringBuffer();
                Log.warn(this, ".readStringIndex - string size out of bounds: " + stringSize);
            } else
            {
                nextString = new StringBuffer(stringSize);
                for (int c = current; c < offsets[jj]; c++)
                    nextString.append((char) fontDataAsArray[stringStart + c]);
            }

            if (debugFont)
                System.out.println("String " + jj + " =" + nextString);

            strings[jj] = nextString.toString();
            current = offsets[jj];

        }
        return strings;
    }

    /**
     * store encoding and load required mappings
     */
    final protected void putFontEncoding(int enc)
    {
        if (enc == StandardFonts.WIN && getBaseFontName().equalsIgnoreCase("Symbol"))
        {
            putFontEncoding(StandardFonts.SYMBOL);
            enc = StandardFonts.SYMBOL;
        }
        fontEnc = enc;
        StandardFonts.checkLoaded(enc);
    }

    /**
     * store embedded differences
     */
    protected final void putChar(int charInt, String mappedChar)
    {

        if (charDifferences == null)
            charDifferences = new String[maxCharCount];

        charDifferences[charInt] = mappedChar;

        if (!hasEncoding && !isCID && StandardFonts.getUnicodeName(mappedChar) != null)
            putMappedChar(charInt, mappedChar);
    }

    /**
     * Insert a new mapped char in the name mapping table
     */
    final protected void putMappedChar(int charInt, String mappedChar)
    {

        if (charDifferencesTable == null)
        {
            charDifferencesTable = new String[maxCharCount];
            diffLookup = new HashMap();
        }

        if (charInt > 255 && maxCharCount == 256)
        { // hack for odd file
            // System.out.println(charInt+" mappedChar="+mappedChar+"<");
            // if(1==1)
            // throw new RuntimeException("xxx");
        } else if (charDifferencesTable[charInt] == null && mappedChar != null && !mappedChar.startsWith("glyph"))
        {
            charDifferencesTable[charInt] = mappedChar;
            diffLookup.put(mappedChar, new Integer(charInt));
        }
    }


    /**
     * get standard charset or extract from type 1C font
     */
    public static int[] ReadCharset(int charset, int nGlyphs, int top, byte[] fontDataAsArray)
    {

        int[] glyphNames;
        int i, j;

        if (debugFont)
            System.out.println("charset=" + charset);

        /**
         * //handle CIDS first if(isCID){ glyphNames = new int[nGlyphs];
         * glyphNames[0] = 0;
         *
         * for (i = 1; i < nGlyphs; ++i) { glyphNames[i] = i;//getWord(fontData,
         * top, 2); //top += 2; }
         *
         *
         * // read appropriate non-CID charset }else
         */
        if (charset == 0)
            glyphNames = ISOAdobeCharset;
        else if (charset == 1)
            glyphNames = ExpertCharset;
        else if (charset == 2)
            glyphNames = ExpertSubCharset;
        else
        {
            glyphNames = new int[nGlyphs + 1];
            glyphNames[0] = 0;
            top = charset;

            int charsetFormat;

            charsetFormat = fontDataAsArray[top++] & 0xff;

            if (debugFont)
                System.out.println("charsetFormat=" + charsetFormat);

            if (charsetFormat == 0)
                for (i = 1; i < nGlyphs; ++i)
                {
                    glyphNames[i] = ReadWord(fontDataAsArray, top, 2);

                    top += 2;
                }
            else if (charsetFormat == 1)
            {
                i = 1;

                int c, nLeft;
                while (i < nGlyphs)
                {
                    c = ReadWord(fontDataAsArray, top, 2);
                    top += 2;
                    nLeft = fontDataAsArray[top++] & 0xff;

                    for (j = 0; j <= nLeft; ++j)
                        glyphNames[i++] = c++;

                }
            } else if (charsetFormat == 2)
            {
                i = 1;

                int c, nLeft;

                while (i < nGlyphs)
                {
                    c = ReadWord(fontDataAsArray, top, 2);

                    top += 2;

                    nLeft = ReadWord(fontDataAsArray, top, 2);

                    top += 2;
                    for (j = 0; j <= nLeft; ++j)
                        glyphNames[i++] = c++;
                }
            }
        }

        return glyphNames;
    }

    public static int ReadWord(byte[] fontDataAsArray, int index, int size)
    {
        int result = 0;
        for (int i = 0; i < size; i++)
            result = (result << 8) + (fontDataAsArray[index + i] & 0xff);
        return result;
    }

    public static String ReadString(byte[] fontDataAsArray, int sid, int idx, int start, int offsize)
    {

        int len;
        String result;

        if (sid < 391)
            result = type1CStdStrings[sid];
        else
        {
            sid -= 391;
            int idx0 = start + ReadWord(fontDataAsArray, idx + sid * offsize, offsize);
            int idxPtr1 = start + ReadWord(fontDataAsArray, idx + (sid + 1) * offsize, offsize);
            // System.out.println(sid+" "+idx0+" "+idxPtr1);
            if ((len = idxPtr1 - idx0) > 255)
                len = 255;

            result = new String(fontDataAsArray, idx0, len);

        }
        return result;
    }

    public static int CalculateSubroutineBias(int subroutineCount)
    {
        int bias;
        if (subroutineCount < 1240)
            bias = 107;
        else if (subroutineCount < 33900)
            bias = 1131;
        else
            bias = 32768;
        return bias;
    }

    public static CFF ParseFont(String fontname, boolean isCID, byte[] stream)
    {
        return new CFF(fontname, isCID, stream);
    }
}
