package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;

public class LocaTable extends Table {
	
	int[] glyph_offsets = null;
	public int size = 0;
	
	public LocaTable() {
		id = "loca";
	}	
	
	public int get_offset (int i) {
		if (size == 0) {
			Log.debug(this, ".get_offset - No glyphs in loca table");
		}
		
		if (!(0 <= i && i < size + 1)) {
			Log.debug(this, ".get_offset - No offset for glyph $i. Requires 0 <= $i < "+ (size + 1));
		}
		
		return glyph_offsets [i];
	}
	
	/** Returns true if glyph at index i is empty and have no body to parse. */
	public boolean is_empty (int i) {

    if (size == 0) {
      Log.debug(this, ".get_offset - No glyphs in loca table");
    }
				
    if (!(0 <= i && i < size + 1)) {
      Log.debug(this, ".get_offset - No offset for glyph $i. Requires 0 <= $i < "+ (size + 1));
    }
		
		return glyph_offsets[i] == glyph_offsets[i + 1];
	}
	
//	public new void parse (FontData dis, HeadTable head_table, MaxpTable maxp_table) throws GLib.Error {
//		size = maxp_table.num_glyphs;
//		glyph_offsets = new uint32[size + 1];
//		
//		dis.seek (offset);
//		
//		printd (@"size: $size\n");
//		printd (@"length: $length\n");
//		printd (@"length/4-1: $(length / 4 - 1)\n");
//		printd (@"length/2-1: $(length / 2 - 1)\n");
//		printd (@"head_table.loca_offset_size: $(head_table.loca_offset_size)\n");
//		
//		switch (head_table.loca_offset_size) {
//			case 0:
//				for (long i = 0; i < size + 1; i++) {
//					glyph_offsets[i] = 2 * dis.read_ushort ();	
//					
//					if (0 < i < size && glyph_offsets[i - 1] > glyph_offsets[i]) {
//						warning (@"Invalid loca table, it must be sorted. ($(glyph_offsets[i - 1]) > $(glyph_offsets[i]))");
//					}
//				}
//				break;
//				
//			case 1:
//				for (long i = 0; i < size + 1; i++) {
//					glyph_offsets[i] = 	dis.read_ulong ();
//									
//					if (0 < i < size && glyph_offsets[i - 1] > glyph_offsets[i]) {
//						warning (@"Invalid loca table, it must be sorted. ($(glyph_offsets[i - 1]) > $(glyph_offsets[i]))");
//					}		
//				}
//
//				break;
//			
//			default:
//				warning ("unknown size for offset in loca table");
//				break;
//		}
//	}

	public void process (GlyfTable glyf_table, HeadTable head_table) {
		FontData fd = new FontData ();
		int last = 0;
		int prev = 0;
		int i = 0;
		
		for (int o : glyf_table.location_offsets) {
			if (i != 0 && (o - prev) % 4 != 0) {
				Log.debug (this, ".process - glyph length is not a multiple of four in gid: "+i);
			}
			
			if (o % 4 != 0) {
				Log.debug(this, ".process - glyph is not on a four byte boundary");
			}
			
			prev = o;
			i++;
		}
	
		if (head_table.loca_offset_size == 0) {
			for (int o : glyf_table.location_offsets) {
				fd.add_u16 ((int) (o / 2));
				
				if (o < last) {
					Log.debug(this, ".process - Loca table must be sorted.");
				}
				
				last = o;
			}
		} else if (head_table.loca_offset_size == 1) {
			for (int o : glyf_table.location_offsets) {
				fd.add_u32 (o);

				if (o < last) {
				  Log.debug(this, ".process - Loca table must be sorted.");
				}
				
				last = o;
			}
		} else {
		  Log.warn(this,  ".process - should never be reached");			
		}

		if (!(glyf_table.location_offsets.size() == glyf_table.glyphs.size() + 1)) {
			Log.debug(this, ".process - (glyf_table.location_offsets.length () == glyf_table.glyphs.length () + 1): "+glyf_table.location_offsets.size());
		}

		fd.pad ();		
		font_data = fd;		
	}
}

