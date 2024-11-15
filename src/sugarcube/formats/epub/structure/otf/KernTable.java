package sugarcube.formats.epub.structure.otf;

import sugarcube.common.data.collections.List3;

public class KernTable extends Table
{

  public static final int HORIZONTAL = 1;
  public static final int MINIMUM = 1 << 1;
  public static final int CROSS_STREAM = 1 << 2;
  public static final int OVERRIDE = 1 << 3;
  public static final int FORMAT = 1 << 8;

  GlyfTable glyf_table;

  public List3<Kern> kerning = new List3<Kern>();
  public int kerning_pairs = 0;

  public KernTable(GlyfTable gt)
  {
    glyf_table = gt;
    id = "kern";
  }

  // public override void parse (FontData dis) throws GLib.Error {
  // uint16 version;
  // uint16 sub_tables;
  //
  // uint16 subtable_version;
  // uint16 subtable_length;
  // uint16 subtable_flags;
  //
  // uint16 search_range;
  // uint16 entry_selector;
  // uint16 range_shift;
  //
  // uint16 n_pairs;
  //
  // dis.seek (offset);
  //
  // version = dis.read_ushort ();
  // warn_if_fail (version == 0);
  // sub_tables = dis.read_ushort ();
  //
  // for (uint16 i = 0; i < sub_tables; i++) {
  // subtable_version = dis.read_ushort ();
  // subtable_length = dis.read_ushort ();
  // subtable_flags = dis.read_ushort ();
  //
  // n_pairs = dis.read_ushort ();
  // search_range = dis.read_ushort ();
  // entry_selector = dis.read_ushort ();
  // range_shift = dis.read_ushort ();
  //
  // // TODO: check more flags
  // if ((subtable_flags & HORIZONTAL) > 0 && (subtable_flags & CROSS_STREAM) ==
  // 0 && (subtable_flags & MINIMUM) == 0) {
  // parse_pairs (dis, n_pairs);
  // }
  // }
  // }

  public void parse_pairs(FontData dis, int n_pairs) throws Error
  {
    int left;
    int right;
    int k;

    for (int i = 0; i < n_pairs; i++)
    {
      left = dis.read_ushort();
      right = dis.read_ushort();
      k = dis.read_short();

      kerning.add(new Kern(left, right, k));
    }
  }

  public void process ()  {
    FontData fd = new FontData ();
    int n_pairs = 0;
    
    int gid_left;
    
    int range_shift = 0;
    int entry_selector = 0;
    int search_range = 0;
    
    int i;
    
    fd.add_ushort (0); // version 
    fd.add_ushort (1); // n subtables

    fd.add_ushort (0); // subtable version 

//    KerningClasses.get_instance ().all_pairs ((left, right, k) => {
//      n_pairs++;
//    });
    
//    if (n_pairs > (uint16.MAX - 14) / 6.0) {
//      warning ("Too many kerning pairs!"); 
//      n_pairs = (uint16) ((uint16.MAX - 14) / 6.0);
//    }
    
    this.kerning_pairs = n_pairs;
    
    fd.add_ushort (6 * n_pairs + 14); // subtable length
    fd.add_ushort (HORIZONTAL); // subtable flags

    fd.add_ushort (n_pairs);
    
    search_range = 6 * Cmap.largest_pow2 (n_pairs);
    entry_selector = Cmap.largest_pow2_exponent (n_pairs);
    range_shift = 6 * n_pairs - search_range;
    
    fd.add_ushort (search_range);
    fd.add_ushort (entry_selector);
    fd.add_ushort (range_shift);

    gid_left = 0;
    
    i = 0;
    
//    KerningClasses.get_instance ().all_pairs ((left, right, k) => { uint16 gid1, gid2;
//      
//      try {
//        // n_pairs is used to truncate this table to prevent buffer overflow
//        if (n_pairs > i++) {
//          gid1 = (uint16) glyf_table.get_gid (left);
//          gid2 = (uint16) glyf_table.get_gid (right);
//          
//          fd.add_ushort (gid1);
//          fd.add_ushort (gid2);
//          fd.add_short ((int16) (k * HeadTable.UNITS));
//        }
//      } catch (GLib.Error e) {
//        warning (e.message);
//      }
//    });
    
    fd.pad ();
    this.font_data = fd;
  }
}