package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.formats.ocd.objects.font.SVGFont;

import java.io.File;
import java.util.Collections;

public class DirectoryTable extends Table
{

  public SVGFont font;
  public CmapTable cmap_table;
  public CvtTable cvt_table;
  public GaspTable gasp_table;
  public GdefTable gdef_table;
  public GlyfTable glyf_table;
  public GposTable gpos_table;
  public HeadTable head_table;
  public HheaTable hhea_table;
  public HmtxTable hmtx_table;
  public KernTable kern_table;
  public MaxpTable maxp_table;
  public NameTable name_table;
  public Os2Table os_2_table;
  public PostTable post_table;
  public LocaTable loca_table;

  public OffsetTable offset_table;

  public List3<Table> tables = new List3<>();

  public DirectoryTable(SVGFont font)
  {
    this.font = font;
    offset_table = new OffsetTable(this);

    loca_table = new LocaTable();
    gasp_table = new GaspTable();
    gdef_table = new GdefTable();
    glyf_table = new GlyfTable(loca_table, font);
    cmap_table = new CmapTable(glyf_table);
    cvt_table = new CvtTable();
    head_table = new HeadTable(glyf_table);
    hmtx_table = new HmtxTable(head_table, glyf_table);
    hhea_table = new HheaTable(glyf_table, head_table, hmtx_table);
    kern_table = new KernTable(glyf_table);
    gpos_table = new GposTable();
    maxp_table = new MaxpTable(glyf_table);
    name_table = new NameTable(font);
    os_2_table = new Os2Table(font);
    post_table = new PostTable(glyf_table);

    id = "Directory table";
  }

  public void process()
  {
    // generate font data
    glyf_table.process();
    gasp_table.process();
    gdef_table.process();
    cmap_table.process(glyf_table);
    cvt_table.process();
    hmtx_table.process();
    hhea_table.process();
    maxp_table.process();
    name_table.process();
    os_2_table.process(glyf_table);
    head_table.process();
    loca_table.process(glyf_table, head_table);
    post_table.process();
    kern_table.process();
    gpos_table.process(glyf_table);

    offset_table.process();
    process_directory(); // this table
  }

  public List3<Table> tables()
  {
    if (tables.size() == 0)
    {
      tables.add(offset_table);
      tables.add(this);

      tables.add(gpos_table);

      // tables.append (gdef_table); // invalid table
      tables.add(head_table);// some info about the font
      tables.add(os_2_table);

      tables.add(cmap_table);// unicode character to glyph index mapping
      // tables.append (cvt_table);
      // tables.append (gasp_table);
      tables.add(glyf_table);// array of glyphs (i.e., their paths)

      tables.add(hhea_table);// ascent, descent, line gap, etc.
      tables.add(hmtx_table);// table of glyph index widths

      // FIXME: Remove the kern table.
      // It looks like the old kerning table is no longer needed
      // since the most browsers uses the GPOS table
      // but Windows does not accept fonts without a kern table.

//      tables.add(kern_table);
      tables.add(loca_table);// general info
      tables.add(maxp_table);// maximum memory use of various variables and
                             // functions
      tables.add(name_table);// glyph table offset positions
      tables.add(post_table);// name to glyph index
    }

    return tables;
  }

  public void set_offset_table(OffsetTable ot)
  {
    offset_table = ot;
  }

  public boolean validate_tables(FontData dis, File file)
  {
    boolean valid = true;

    try
    {
      dis.seek(0);

      if (!validate_checksum_for_entire_font(dis, file))
      {
        Log.debug(this, ".validate_tables - file has invalid checksum");
      } else
      {
        Log.debug(this, ".validate_tables - Font file has valid checksum.\n");
      }

      // Skip validation of head table for now it should be simple but it seems
      // to
      // be broken in some funny way.

      if (!glyf_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - glyf_table has invalid checksum");
        valid = false;
      }

      if (!maxp_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - maxp_table has is invalid checksum");
        valid = false;
      }

      if (!loca_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - loca_table has invalid checksum");
        valid = false;
      }

      if (!cmap_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - cmap_table has invalid checksum");
        valid = false;
      }

      if (!hhea_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - hhea_table has invalid checksum");
        valid = false;
      }

      if (!hmtx_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - hmtx_table has invalid checksum");
        valid = false;
      }

      if (!name_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - name_table has invalid checksum");
        valid = false;
      }

      if (!os_2_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - os_2_table has invalid checksum");
        valid = false;
      }

      if (!post_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - post_table has invalid checksum");
        valid = false;
      }

      if (kern_table.has_data() && !kern_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - kern_table has invalid checksum");
        valid = false;
      }

      if (!gpos_table.validate(dis))
      {
        Log.debug(this, ".validate_tables - gpos_table has invalid checksum");

        if (gpos_table.font_data != null)
        {
          Log.debug(this, ".validate_tables - Length: $(((!)gpos_table.font_data).length ())\n");
        } else
        {
          Log.debug(this, ".validate_tables - font_data is null");
        }

        valid = false;
      }
    } catch (Exception e)
    {
      e.printStackTrace();
      valid = false;
    }

    return valid;
  }

  boolean validate_checksum_for_entire_font(FontData dis, File f)
  {
    int p = head_table.offset + head_table.get_checksum_position();
    int checksum_font, checksum_head;

    checksum_head = head_table.get_font_checksum();

    dis.seek(0);

    // zero out checksum entry in head table before validating it
    dis.write_at(p + 0, 0);
    dis.write_at(p + 1, 0);
    dis.write_at(p + 2, 0);
    dis.write_at(p + 3, 0);

    checksum_font = (int) (0xB1B0AFBA - dis.check_sum());

    if (checksum_font != checksum_head)
    {
      Log.debug(this, ".validate_checksum_for_entire_font - Fontfile checksum in head table does not match calculated checksum. checksum_font: "
          + checksum_font + "!=" + checksum_head);
      return false;
    }

    return true;
  }

  public long fontFileSize()
  {
    long length = 0;

    for (Table t : tables)
    {
      length += t.fontData().length_with_padding();
    }

    return length;
  }

  public void process_directory()
  {
    create_directory(); // create directory without offsets to calculate length
                        // of offset table and checksum for entire file
    create_directory(); // generate a valid directory
  }

  // Check sum adjustment for the entire font
  public int get_font_file_checksum()
  {
    int check_sum = 0;
    for (Table t : tables)
    {
      check_sum = t.fontData().continous_check_sum(check_sum);
    }
    return check_sum;
  }

  public void create_directory()
  {
    FontData fd;

    int table_offset = 0;
    int table_length = 0;

    int check_sum = 0;

    fd = new FontData();

    if (offset_table.num_tables <= 0)
      return;

    table_offset += offset_table.fontData().length_with_padding();

    if (this.font_data != null)
    {
      table_offset += this.fontData().length_with_padding();
    }

    head_table.set_check_sum_adjustment(0); // Set this to zero, calculate
                                            // checksums and update the value
    head_table.process();

    // write the directory
    for (Table t : tables)
    {

      if (t instanceof DirectoryTable || t instanceof OffsetTable)
      {
        continue;
      }

      // Log.debug(this, ".create_directory - "+t.id);

      table_length = t.fontData().length(); // without padding

      // fd.add_tag (t.get_id()); // name of table
      // fd.add_u32 (t.fontData().check_sum());
      // fd.add_u32 (table_offset);
      // fd.add_u32 (table_length);
      t.checksum = t.fontData().check_sum();
      t.offset = table_offset;
      t.length = table_length;

      table_offset += t.fontData().length_with_padding();
    }

    List3<Table> sorted = new List3<>(tables);
    Collections.sort(sorted, Table.idComparator());
    for (Table t : sorted)
    {

      if (t instanceof DirectoryTable || t instanceof OffsetTable)
      {
        continue;
      }

      // Log.debug(this, ".create_directory - "+t.id);

      table_length = t.fontData().length(); // without padding

      fd.add_tag(t.get_id()); // name of table
      fd.add_u32(t.checksum);
      fd.add_u32(t.offset);
      fd.add_u32(t.length);

      table_offset += t.fontData().length_with_padding();
    }

    // padding
    fd.pad();

    this.font_data = fd;

    check_sum = get_font_file_checksum();
    head_table.set_check_sum_adjustment((int) (0xB1B0AFBA - check_sum));
    head_table.process(); // update the value
  }

}
