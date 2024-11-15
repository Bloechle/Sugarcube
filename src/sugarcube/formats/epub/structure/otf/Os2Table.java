package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.formats.ocd.objects.font.SVGFont;

public class Os2Table extends Table
{

  public static final int ITALIC = 1;
  public static final int UNDERSCORE = 1 << 1;
  public static final int NEGATIVE = 1 << 2;
  public static final int OUTLINED = 1 << 3;
  public static final int STRIKEOUT = 1 << 4;
  public static final int BOLD = 1 << 5;
  public static final int REGULAR = 1 << 6;
  public static final int TYPO_METRICS = 1 << 7;
  public static final int WWS = 1 << 8;
  public static final int OBLIQUE = 1 << 9;
  public SVGFont font;

  public Os2Table(SVGFont font)
  {
    id = "OS/2";
    this.font = font;
  }

  // public override void parse (FontData dis) throws Error {
  // }

  public void process(GlyfTable glyf_table)
  {
    FontData fd = new FontData();
    // Font font = OpenFontFormatWriter.get_current_font ();
    int ascender;
    int descender;
    int style = 0;

    fd.add_u16(0x0002); // version

    fd.add_16(glyf_table.avgWidth()); // xAvgCharWidth

    fd.add_u16(font.intWeight()); // usWeightClass (400 is normal, 700 is bold)

    fd.add_u16(5); // usWidthClass (5 is normal)
    fd.add_u16(0); // fsType

    fd.add_16(200); // ySubscriptXSize
    fd.add_16(200); // ySubscriptYSize
    fd.add_16(200); // ySubscriptXOffset
    fd.add_16(200); // ySubscriptYOffset
    fd.add_16(200); // ySuperscriptXSize
    fd.add_16(200); // ySuperscriptYSize
    fd.add_16(200); // ySuperscriptXOffset
    fd.add_16(200); // ySuperscriptYOffset
    fd.add_16(200); // yStrikeoutSize
    fd.add_16(200); // yStrikeoutPosition
    fd.add_16(0); // sFamilyClass

    // FIXME: PANOSE
    fd.add(2);
    fd.add(0);
    fd.add(0);
    fd.add(0);
    fd.add(0);
    fd.add(0);
    fd.add(0);
    fd.add(0);
    fd.add(0);
    fd.add(0);

    // FIXME:
    fd.add_u32(0); // ulUnicodeRange1 Bits 0-31
    fd.add_u32(0); // ulUnicodeRange2 Bits 32-63
    fd.add_u32(0); // ulUnicodeRange3 Bits 64-95
    fd.add_u32(0); // ulUnicodeRange4 Bits 96-127

    fd.add_tag("Vend"); // VendID

    // fsSelection (1 for italic 0 for upright)

    // if (!font.isBold() && !font.isItalic()) {
    // style |= REGULAR;
    // }
    //
    // if (font.isBold()) {
    // style |= BOLD;
    // }
    //
    // if (font.isItalic()) {
    // style |= ITALIC;
    // }

    style |= REGULAR;

    fd.add_u16(style);

    fd.add_u16(0); // null char... usFirstCharIndex
    fd.add_u16(glyf_table.lastChar()); // usLastCharIndex

    ascender = glyf_table.ymax;
    descender = glyf_table.ymin;

    if (descender > 0)
      descender = 0;

    fd.add_16(ascender); // sTypoAscender
    fd.add_16(descender); // sTypoDescender
    fd.add_16(10); // sTypoLineGap

    fd.add_u16(ascender); // usWinAscent

    if (descender > 0)
    {
      Log.debug(this, ".process - usWinDescent is unsigned.");
      fd.add_u16(0);
    } else
    {
      fd.add_u16(-descender); // usWinDescent (not like sTypoDescender)
    }

    // FIXME:
    fd.add_u32(0); // ulCodePageRange1 Bits 0-31
    fd.add_u32(0); // ulCodePageRange2 Bits 32-63

    fd.add_16(ascender); // sHeight
    fd.add_16(ascender); // sCapHeight

    fd.add_16(0); // usDefaultChar
    fd.add_16(0x0020); // usBreakChar also known as space

    fd.add_16(2); // usMaxContext (two becase it has kernings but not
                  // ligatures).

    // padding
    fd.pad();

    this.font_data = fd;
  }

}
