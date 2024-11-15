package sugarcube.formats.epub.structure.otf;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HeadTable extends Table
{

  int adjusted_checksum = 0;

  int mac_style;
  int lowest_PPEM;
  int font_direction_hint;

  public int loca_offset_size = 1; // 0 for int16 1 for int32
  int glyph_data_format;

  int version;
  int font_revision;

  int magic_number;

  int flags;

  int created;
  int modified;

  public static int units_per_em;
  public static double UNITS;

  public static final int BASELINE_AT_ZERO = 1 << 0;
  public static final int LSB_AT_ZERO = 1 << 1;

  GlyfTable glyf_table;

  public HeadTable(GlyfTable gt)
  {
    glyf_table = gt;
    id = "head";
    init();
  }

  public static void init()
  {
    units_per_em = 1024;
    UNITS = 10 * (units_per_em / 1000.0);
  }

  public int get_adjusted_checksum()
  {
    return adjusted_checksum;
  }

  public double get_units_per_em()
  {
    return units_per_em * 10;
  }


  public int get_font_checksum()
  {
    return adjusted_checksum;
  }

  public void set_check_sum_adjustment(int csa)
  {
    this.adjusted_checksum = csa;
  }

  public int get_checksum_position()
  {
    return 8;
  }

  public void process()
  {
    FontData font_data = new FontData();
    int version = 1 << 16;
    int font_revision = 1 << 16;

    font_data.add_fixed(version);
    font_data.add_fixed(font_revision);

    // Zero on the first run and updated by directory tables checksum
    // calculation
    // for the entire font.
    font_data.add_u32(adjusted_checksum);

    font_data.add_u32(0x5F0F3CF5); // magic number

    // font_data.add_u16 (BASELINE_AT_ZERO | LSB_AT_ZERO);
    font_data.add_u16(0); // flags

    font_data.add_u16(units_per_em); // units per em (should be a power of two
                                     // for ttf fonts)

    DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm aaa");
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    long sec = 0;
    try
    {
      Date date0 = formatter.parse("1904/01/01 12:00 AM");
      Date date1 = new Date();
      sec = (date1.getTime() - date0.getTime()) / 1000;

    } catch (ParseException e)
    {
      e.printStackTrace();
    }

    font_data.add_64(sec); // creation time since 1904-01-01
    font_data.add_64(sec); // modified time since 1904-01-01


    font_data.add_short(glyf_table.xmin);
    font_data.add_short(glyf_table.ymin);
    font_data.add_short(glyf_table.xmax);
    font_data.add_short(glyf_table.ymax);

//    if (this.glyf_table.font.isBold())
//      mac_style |= 1;
//
//    if (this.glyf_table.font.isItalic())
//      mac_style |= 2;

    font_data.add_u16(0); // mac style
    font_data.add_u16(8); // smallest recommended size in pixels, ppem
    font_data.add_16(2); // deprecated direction hint
    font_data.add_16(loca_offset_size); // long offset
    font_data.add_16(0); // Use current glyph data format

    font_data.pad();

    this.font_data = font_data;
  }
}
