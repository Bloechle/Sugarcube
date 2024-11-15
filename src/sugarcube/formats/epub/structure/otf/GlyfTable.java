package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.font.SVGGlyph;

public class GlyfTable extends Table
{
  // Flags for composite glyph
  public static final int BOTH_ARE_WORDS = 1 << 0;
  public static final int BOTH_ARE_XY_VALUES = 1 << 1;
  public static final int ROUND_TO_GRID = 1 << 2;
  public static final int SCALE = 1 << 3;
  public static final int RESERVED = 1 << 4;
  public static final int MORE_COMPONENTS = 1 << 5;
  public static final int SCALE_X_Y = 1 << 6;
  public static final int SCALE_WITH_ROTATTION = 1 << 7;
  public static final int INSTRUCTIONS = 1 << 8;

  public int xmin = Integer.MAX_VALUE;
  public int ymin = Integer.MAX_VALUE;
  public int xmax = Integer.MIN_VALUE;
  public int ymax = Integer.MIN_VALUE;

  public FontData dis;
  public HeadTable head_table;
  public HmtxTable hmtx_table;
  public LocaTable loca_table;
  public CmapTable cmap_table; // cmap and post is null when inistialized and
                               // set in parse method
  public PostTable post_table;
  public KernTable kern_table;

  public SVGFont font;

  public List3<Integer> location_offsets;

  // a list of glyphs sorted in the order we expect to find them in a
  // ttf font. notdef is the first glyph followed by null and nonmarkingreturn.
  // after that will all assigned glyphs appear in sorted (unicode) order, all
  // remaining unassigned glyphs will be added in the last section of the file.
  public List3<Glyph> glyphs;

  int max_points = 0;
  int max_contours = 0;

  double total_width = 0;
  int non_zero_glyphs = 0;

  public GlyfTable(LocaTable l, SVGFont font)
  {
    id = "glyf";
    this.font = font;
    loca_table = l;
    location_offsets = new List3<Integer>();
    glyphs = new List3<Glyph>();
  }

//  public int gid(String name)
//  {
//    int i = 0;
//
//    for (Glyph g : glyphs)
//    {
//      if (g.name == name)
//      {
//        return i;
//      }
//      i++;
//    }
//    return -1;
//  }

  public int avgWidth()
  {
    return (int) (total_width / non_zero_glyphs);
  }

  public int firstChar()
  {
//    Log.debug(this, ".get_first_char: " + glyphs.first().unicode);
    return glyphs.first().unicode; // space
  }

  public int lastChar()
  {
//    Log.debug(this, ".get_last_char: " + glyphs.last().unicode);
    return glyphs.last().unicode;
  }

  public void process()
  {
    // add notdef. character at index zero + other special chars first
    glyphs.add(Glyph.notdefCharacter());
    glyphs.add(Glyph.nullCharacter());
    glyphs.add(Glyph.nonMarkingReturn());
    glyphs.add(Glyph.spaceCharacter(font));

    if (font.nbOfGlyphs() == 0)
    {
      Log.debug(OTF.DEBUG, this, ".create_glyph_table - No glyphs in font.");
    }

    // add glyphs, first all assigned then the unassigned ones
    int index = 3;
    for (SVGGlyph glyph : font.sortedGlyphs())
    {
      Glyph g = new Glyph(index++, glyph);
      if (g.unicode <= 27 || g.unicode == 32 || g.name.equals(".notdef"))
      { // skip control characters and space
        continue;
      }
      glyphs.add(g);
    }

    if (glyphs.size() == 0)
    {
      Log.debug(OTF.DEBUG, this, ".process - No glyphs in glyf table.");
    }

    
    FontData fd = new FontData();
    int last_len = 0;    
    for (Glyph g : glyphs)
    {
      Rectangle3 box = g.bounds();

      // set values for loca table
      // assert (fd.length () % 4 == 0);
      location_offsets.add(fd.length());
      process_glyph(g, fd);

      Log.debug(OTF.DEBUG, this, ".process - adding glyph: " + g.name);
      Log.debug(OTF.DEBUG, this, ".process - glyf length: " + (fd.length() - last_len));
      Log.debug(OTF.DEBUG, this, ".process - loca fd.length (): " + fd.length());

      last_len = fd.length();
    }

    location_offsets.add(fd.length()); // last entry in loca table is special

    // every glyph is padded, no padding to be done here
    // assert (fd.length () % 4 == 0);
    if (fd.length() % 4 != 0)
      Log.debug(OTF.DEBUG, this, ".process - fontdata is not padded correctly");

    font_data = fd;
  }

  public void process_glyph(Glyph g, FontData fd)
  {
    int end_point;
    int npoints;
    int ncontours;
    int nflags;
    int glyph_offset;
    int len;
    int coordinate_length;
    GlyfData glyf_data;

    fd.seek_end(); // append glyph

    glyph_offset = (int) fd.length();

    Log.debug(OTF.DEBUG, this, ".process_glyph - glyph_offset: " + glyph_offset);

    glyf_data = new GlyfData(g);

    // part of average width calculation for OS/2 table
    total_width += OTF.ZUnit(glyf_data.glyph.advanceX);
    non_zero_glyphs++;

    if (g.segs.length == 0)
    {
      // location_offsets will be equal to location_offset + 1 for
      // all empty glyphs
      return;
    }

    ncontours = glyf_data.nbContours();
    fd.add_short(ncontours);

    // bounding box
    fd.add_16(glyf_data.minX);
    fd.add_16(glyf_data.minY);
    fd.add_16(glyf_data.maxX);
    fd.add_16(glyf_data.maxY);

    // end points
    for (int end : glyf_data.endPts)
    {
      fd.add_u16(end);
    }

    fd.add_u16(0); // instruction length

    int glyph_header = 12 + ncontours * 2;

    Log.debug(OTF.DEBUG, this, ".process_glyph - next glyf: " + g.name);
    Log.debug(OTF.DEBUG, this, ".process_glyph - glyf header length: " + glyph_header);

    nflags = glyf_data.flags.size();
    ncontours = glyf_data.nbContours();
    npoints = (ncontours > 0) ? nflags : 0; // +1?

    if (npoints > max_points)
    {
      max_points = npoints;
    }

    if (ncontours > max_contours)
    {
      max_contours = ncontours;
    }

    for (int flag : glyf_data.flags)
    {
      fd.add_byte(flag);
    }

    Log.debug(OTF.DEBUG, this, ".process_glyph - flags: " + nflags);

    // x coordinates
    for (int x : glyf_data.xCoords)
    {
      fd.add_16(x);
    }

    // y coordinates
    for (int y : glyf_data.yCoords)
    {
      fd.add_16(y);
    }

    len = fd.length();
    coordinate_length = fd.length() - nflags - glyph_header;
    Log.debug(OTF.DEBUG, this, ".process_glyph - coordinate_length: " + coordinate_length);
    Log.debug(OTF.DEBUG, this, ".process_glyph - fd.length (): " + fd.length());
    assert (fd.length() > nflags + glyph_header);

    Log.debug(OTF.DEBUG, this, ".process_glyph - glyph_offset: " + glyph_offset);
    Log.debug(OTF.DEBUG, this, ".process_glyph - len: " + len);

    // save bounding box for head table
    if (glyf_data.minX < this.xmin)
    {
      this.xmin = glyf_data.minX;
    }

    if (glyf_data.minY < this.ymin)
    {
      this.ymin = glyf_data.minY;
    }

    if (glyf_data.maxX > this.xmax)
    {
      this.xmax = glyf_data.maxX;
    }

    if (glyf_data.maxY > this.ymax)
    {
      this.ymax = glyf_data.maxY;
    }

    Log.debug(OTF.DEBUG, this, ".process_glyph - length before padding: " + fd.length());

    // all glyphs needs padding for loca table to be correct
    while (fd.length() % 4 != 0)
    {
      fd.add(0);
    }
    Log.debug(OTF.DEBUG, this, ".process_glyph - length after padding: " + fd.length());
  }
}
