package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Map3;

/** Format 4 cmap subtable */
public class CmapSubtableWinUni extends CmapSubtable
{
  int format = 0;
  Map3<Long, Integer> table = new Map3<Long, Integer>();

  public CmapSubtableWinUni()
  {
  }

  @Override
  public int get_length()
  {
    return table.size();
  }

  @Override
  public int get_char(int indice)
  {
    int c = table.get(indice);

    if (c == 0 && indice == 0)
    {
      return 0;
    }

    if (c == 0)
    {
      while (table.get(--indice) == 0)
      {
        if (indice == 0)
        {
          return 0;
        }
      }

      Log.debug(this, ".get_char - There is no char for glyph number $indice in cmap table. table.size: $(table.size ()))");
      return 0;
    }

    return c;
  }

  public void process(FontData fd, GlyfTable glyf_table) throws Error
  {
    int seg_count_2;
    int seg_count;
    int search_range;
    int entry_selector;
    int range_shift;

    int gid_length = 0;

    GlyphRange range = new GlyphRange();    
    for (Glyph g : glyf_table.glyphs)
    {
      {
        if (!g.isUnassigned())
        {
          range.add(g);
        }
      }
    }

    // glyph_range.print_all ();

    List3<GlyphRange> ranges = range.clusters();
    seg_count = ranges.size() + 1;
    seg_count_2 = seg_count * 2;
    search_range = 2 * Cmap.largest_pow2(seg_count);
    entry_selector = Cmap.largest_pow2_exponent(seg_count);
    range_shift = seg_count_2 - search_range;

    // format
    fd.add_ushort(4);

    // length of subtable
    fd.add_ushort(16 + 8 * seg_count + gid_length);

    // language
    fd.add_ushort(0);

    fd.add_ushort(seg_count_2);
    fd.add_ushort(search_range);
    fd.add_ushort(entry_selector);
    fd.add_ushort(range_shift);

    // end codes
    for (GlyphRange u : ranges)
    {
      if (u.start>=0xFFFF || u.stop >= 0xFFFF)
      {
        Log.debug(this, ".process - unicode > 0xFFFF not yet implemented.");
      }
//      Log.debug(this, ".process - range: " + u.start + "-" + u.stop);
      fd.add_ushort(u.stop);
    }
    fd.add_ushort(0xFFFF);
    fd.add_ushort(0); // Reserved

    //since first glyph are notdef, null and nonmarkingreturn
    for (GlyphRange u : ranges)
    {
      fd.add_ushort(u.start);
    }
    fd.add_ushort(0xFFFF);

    // delta
    for (GlyphRange u : ranges)
    {
      fd.add_ushort(u.delta());
    }
    fd.add_ushort(1);

    // id range offset
    for (GlyphRange u : ranges)
    {
      if (u.stop <= 0xFFFF)
      {
        fd.add_ushort(0);
      } else
      {
        Log.debug(this, "Not implemented yet.");
      }
    }
    fd.add_ushort(0);

    // FIXME: implement the rest of type 4 (mind gid_length in length field)
  }
}