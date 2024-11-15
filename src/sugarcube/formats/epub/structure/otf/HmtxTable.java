package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;

public class HmtxTable extends Table
{
  int nmetrics;
  int nmonospaced;

  int[] advance_width = null;
  int[] left_side_bearing = null;
  int[] left_side_bearing_monospaced = null;

  public int max_advance = 0;
  public int max_extent = 0;
  public int min_lsb = 0;
  public int min_rsb = 0;

  HeadTable head_table;
  GlyfTable glyf_table;

  public HmtxTable(HeadTable h, GlyfTable gt)
  {
    head_table = h;
    glyf_table = gt;
    id = "hmtx";
  }

  public double get_advance(int i)
  {
    if (i >= nmetrics)
    {
      Log.debug(this, ".get_advance - i>=nmetrics: " + nmetrics);
      return 0;
    }
    return advance_width[i] * 1000 / head_table.get_units_per_em();
  }

  /** Get left side bearing relative to xmin. */
  public double get_lsb(int i)
  {
    if (i < nmetrics)
      return 0.0;
    return left_side_bearing[i] * 1000 / head_table.get_units_per_em();
  }

  // public void parse (FontData dis, HheaTable hhea_table, LocaTable
  // loca_table) {
  // nmetrics = hhea_table.num_horizontal_metrics;
  // nmonospaced = loca_table.size - nmetrics;
  //
  // dis.seek (offset);
  //
  // if (nmetrics > loca_table.size) {
  // Log.warn (this,
  // ". parse - (nmetrics > loca_table.size) ($nmetrics > $(loca_table.size))");
  // return;
  // }
  //
  // printd (@"nmetrics: $nmetrics\n");
  // printd (@"loca_table.size: $(loca_table.size)\n");
  //
  // advance_width = new uint16[nmetrics];
  // left_side_bearing = new uint16[nmetrics];
  // left_side_bearing_monospaced = new uint16[nmonospaced];
  //
  // for (int i = 0; i < nmetrics; i++) {
  // advance_width[i] = dis.read_ushort ();
  // left_side_bearing[i] = dis.read_short ();
  // }
  //
  // for (int i = 0; i < nmonospaced; i++) {
  // left_side_bearing_monospaced[i] = dis.read_short ();
  // }
  // }

  public void process()
  {
    FontData fd = new FontData();

    int advance;
    int extent;
    int rsb;
    int lsb;
   
    // advance and lsb
    for (Glyph g : glyf_table.glyphs)
    {
      Rectangle3 box = g.bounds();
      double _lsb = box.minX();
      double _advance =g.advanceX;
      double _extent =_lsb + box.width;
      double _rsb = box.maxX() - _advance;

      lsb = OTF.ZUnit(_lsb);
      advance = OTF.ZUnit(_advance);
      extent = OTF.ZUnit(_extent)-1;
      rsb = OTF.ZUnit(_rsb);
      rsb = 20;
      
      fd.add_u16(advance);
      fd.add_16(lsb);

      if (advance > max_advance)
      {
        max_advance = advance;
      }

      if (extent > max_extent)
      {
        max_extent = extent;
      }

      if (rsb < min_rsb)
      {
        min_rsb = rsb;
      }

      if (lsb < min_lsb)
      {
        min_lsb = lsb;
      }
    }

    // monospaced lsb ...

    font_data = fd;
  }
}
