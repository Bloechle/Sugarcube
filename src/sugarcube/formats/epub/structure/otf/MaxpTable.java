package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;

public class MaxpTable extends Table
{

  GlyfTable glyf_table;

  public int num_glyphs = 0;

  public MaxpTable(GlyfTable g)
  {
    glyf_table = g;
    id = "maxp";
  }

  public void process()
  {
    FontData fd = new FontData();

    // Version 0.5 for fonts with cff data and 1.0 for ttf
    fd.add_u32(0x00010000);

    if (glyf_table.glyphs.size() == 0)
    {
      Log.debug(this, ".process - Zero glyphs in maxp table.");
    }

    fd.add_u16(glyf_table.glyphs.size()); // numGlyphs in the font

    fd.add_u16(glyf_table.max_points); // max points
    fd.add_u16(glyf_table.max_contours); // max contours
    fd.add_u16(0); // max composite points
    fd.add_u16(0); // max composite contours
    fd.add_u16(1); // max zones
    fd.add_u16(0); // twilight points
    fd.add_u16(0); // max storage
    fd.add_u16(0); // max function defs
    fd.add_u16(0); // max instruction defs
    fd.add_u16(0); // max stack elements
    fd.add_u16(0); // max size of instructions
    fd.add_u16(0); // max component elements
    fd.add_u16(0); // component depth

    fd.pad();

    this.font_data = fd;
  }
}
