package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;

public class CmapTable extends Table
{

  GlyfTable glyf_table;
  List3<CmapSubtable> subtables;

  public CmapTable(GlyfTable gt)
  {
    glyf_table = gt;
    subtables = new List3<CmapSubtable>();
    id = "cmap";
  }

  public int get_char(int i)
  {
    return get_prefered_table().get_char(i);
  }

  CmapSubtable get_prefered_table()
  {
    if (subtables.size() == 0)
    {
      Log.debug(this, ".get_prefered_table - No cmap table has been parsed.");
      return new CmapSubtable();
    }

    return subtables.first();
  }

  @Override
  public String get_id()
  {
    return "cmap";
  }

  /** Character to glyph mapping */
  public void process(GlyfTable glyf_table) throws Error
  {
    FontData fd = new FontData();
    CmapSubtableWinUni cmap = new CmapSubtableWinUni();
    int n_encoding_tables;
    int subtable_offset = 0;

    n_encoding_tables = 1;

    fd.add_u16(0); // table version
    fd.add_u16(n_encoding_tables);

    fd.add_u16(3); // platform
    fd.add_u16(1); // encoding format 1=Unicode BMP (UCS-2), 10=Unicode UCS-4

    subtable_offset = fd.length() + 4;
    // Log.debug (this, ".process - subtable_offset: "+subtable_offset);

    fd.add_ulong(subtable_offset);
    cmap.process(fd, glyf_table);

    // padding
    fd.pad();

    this.font_data = fd;
  }
}