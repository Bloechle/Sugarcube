package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;

public class GposTable extends Table
{

  GlyfTable glyf_table;
  List3<Kern> kerning_pairs = new List3<Kern>();
  List3<PairFormat1> pairs = new List3<PairFormat1>();

  public GposTable()
  {
    id = "GPOS";
  }

  public void process(GlyfTable glyf_table)
  {
    FontData fd = new FontData();

    this.glyf_table = glyf_table;

    // Log.debug(this, ".process - Process GPOS\n");

    fd.add_ulong(0x00010000); // table version
    fd.add_ushort(10); // offset to script list
    fd.add_ushort(30); // offset to feature list
    fd.add_ushort(44); // offset to lookup list

    // script list ?
    fd.add_ushort(1); // number of items in script list
    fd.add_tag("DFLT"); // default script
    fd.add_ushort(8); // offset to script table from script list

    // script table
    fd.add_ushort(4); // offset to default language system
    fd.add_ushort(0); // number of languages

    // LangSys table
    fd.add_ushort(0); // reserved
    fd.add_ushort(0); // required features (0xFFFF is none)
    fd.add_ushort(1); // number of features
    fd.add_ushort(0); // feature index

    // feature table
    fd.add_ushort(1); // number of features

    fd.add_tag("kern"); // feature tag
    fd.add_ushort(8); // offset to feature

    fd.add_ushort(0); // feature prameters (null)
    fd.add_ushort(1); // number of lookups
    fd.add_ushort(0); // lookup indice

    // lookup table
    fd.add_ushort(1); // number of lookups
    fd.add_ushort(4); // offset to lookup 1

    fd.add_ushort(2); // lookup type // FIXME
    fd.add_ushort(0); // lookup flags
    fd.add_ushort(1); // number of subtables
    fd.add_ushort(8); // array of offsets to subtables

    // MarkFilteringSet

    fd.append(get_pair_pos_format1());

    fd.pad();
    this.font_data = fd;
  }

  // PairPosFormat1 subtable
  FontData get_pair_pos_format1()
  {
    FontData fd = new FontData();
    int pair_set_count;
    int coverage_offset;

    create_kerning_pairs();

    coverage_offset = 10 + pairs_offset_length() + pairs_set_length();

    // FIXME: add more then current maximum of pairs

    if (pairs.size() > 65535 || coverage_offset > 65535)
    {
      print_pairs();
      Log.warn(this, ". get_pair_pos_format1 - Too many kerning pairs:" + pairs.size());
    }

    pair_set_count = pairs.size();

    fd.add_ushort(1); // position format
    // offset to coverage table from beginning of kern pair table
    fd.add_ushort(coverage_offset);
    fd.add_ushort(0x0004); // ValueFormat1 (0x0004 is x advance)
    fd.add_ushort(0); // ValueFormat2 (0 is null)
    fd.add_ushort(pair_set_count); // n pairs

    // pair offsets orderd by coverage index
    int pair_set_offset = 10 + pairs_offset_length();
    for (PairFormat1 k : pairs)
    {
      fd.add_ushort(pair_set_offset);
      pair_set_offset += 2;

      for (Kern pk : k.pairs)
      {
        pair_set_offset += 4;
      }
    }

    // pair table
    for (PairFormat1 p : pairs)
    {
      fd.add_ushort(p.pairs.size());
      for (Kern k : p.pairs)
      {
        // pair value record
        fd.add_ushort(k.right); // gid to second glyph
        fd.add_short(k.kerning); // value of ValueFormat1, horizontal adjustment
                                 // for advance
        // value of ValueFormat2 is null
      }
    }

    // ProgressBar.set_progress (0); // reset progress bar

    if (fd.length() != coverage_offset)
    {
      Log.debug(this, ". get_pair_pos_format1 - Bad coverage offset, coverage_offset: " + coverage_offset + ", " + fd.length());
      Log.debug(this, ".get_pair_pos_format1 - pairs_offset_length: " + pairs_offset_length() + ", pairs_set_length: " + pairs_set_length());
    }

    // coverage
    fd.add_ushort(1); // format
    fd.add_ushort(pairs.size());
    for (PairFormat1 p : pairs)
    {
      fd.add_ushort(p.left); // gid
    }

    return fd;
  }

  public void print_pairs()
  {
    for (PairFormat1 p : pairs)
    {
      Log.debug(this, ".print_pairs - \nGid: $(p.left)\n");
      for (Kern k : p.pairs)
      {
        Log.debug(this, ".print_pairs - \tKern $(k.right)\t$(k.kerning)\n");
      }
    }
  }

  public int pairs_set_length()
  {
    int len = 0;
    for (PairFormat1 p : pairs)
    {
      len += 2 + 4 * p.pairs.size();
    }
    return len;
  }

  public int pairs_offset_length()
  {
    return 2 * pairs.size();
  }

  public int get_pair_index(int gid)
  {
    int i = 0;
    for (PairFormat1 p : pairs)
    {
      if (p.left == gid)
      {
        return i;
      }
      i++;
    }
    return -1;
  }

  /** Create kerning pairs from classes. */
  public void create_kerning_pairs()
  {
    // while (kerning_pairs.size() > 0) {
    // kerning_pairs.remove(kerning_pairs.first ());
    // }
    //
    // //TODO : modify if kerning exists
    // for(Kern kern: new KernList())
    // {
    // int gid1, gid2;
    // PairFormat1 pair;
    // int pair_index;
    //
    // gid1 = glyf_table.get_gid(kern.left);
    // gid2 = glyf_table.get_gid(kern.right);
    //
    // if (gid1 == -1) {
    // Log.debug(this, ".create_kerning_pairs - gid is -1 for: "+kern.left);
    // return;
    // }
    //
    // if (gid2 == -1) {
    // Log.debug(this, ".create_kerning_pairs - gid is -1 for: "+kern.right);
    // return;
    // }
    //
    // pair_index = get_pair_index (gid1);
    // if (pair_index == -1) {
    // pair = new PairFormat1 ();
    // pair.left = gid1;
    // pairs.append (pair);
    // } else {
    // pair = pairs.nth (pair_index).data;
    // }
    //
    // pair.pairs.append (new Kern (gid1, gid2, (int16)(kerning *
    // HeadTable.UNITS)));
    // }
  }
}
