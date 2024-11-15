package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;

import java.util.LinkedList;
import java.util.List;

/** Type2, PostScript outlines */
public class CffTable extends Table {

	public final static int HSTEM = 1;
	public final static int VSTEM = 3;
	public final static int VMOVETO = 4;
	public final static int HLINETO = 6;
	public final static int VLINETO = 7;
	public final static int ENDCHAR = 14;
	public final static int HMOVETO = 22;
	public final static int RMOVETO = 21;
	
	public final static int CHARSET = 15;
	public final static int ENCODING = 16;

	public final static int VERSION = 0;

	public int version;

	FontData dis;

	public CffTable () {
		id = "CFF ";
	}

	public int read_offset (int size) throws Error {
		switch (size) {
			case 0:
				Log.debug(this, ".read_offset - offset size is zero");
				return dis.read_byte ();
			case 1:
				return dis.read_byte ();
			case 2:
				return dis.read_ushort ();
			case 4:
				return dis.read_ulong ();
			default:
			  Log.debug(this, ".read_offset - should never reach this point");
				break;
		}
		
    Log.debug(this, ".read_offset - offset size is zero (default value)");
		return 0;
	}

	public List<Integer> read_index () throws Error {
		int offset_size, off;
		int entries;
		List<Integer> offsets = new LinkedList<Integer> ();
		
		entries = dis.read_ushort ();
		Log.debug(this, ".read_index - number of entries: "+entries);
		
		if (entries == 0) {
			Log.debug(this, ". read_index - skip index");
			return offsets;
		}
		
		offset_size = dis.read ();
		Log.debug (this, ".read_index - Offset size: "+offset_size);
		
		// read the end offset as well
		for (int i = 0; i <= entries; i++) {
			off = read_offset (offset_size);
			Log.debug(this, ".read_index - offset: "+off);
			offsets.add (off);
		}
		
		return offsets;
	}

//	public override void parse (FontData dis) throws Error {
//		uint v1, v2, offset_size, header_size, len;
//		string data;
//		List<uint32> offsets, dict_index;
//		int id, val;
//		int off; // offset relative to table position
//		
//		dis.seek (offset);
//		this.dis = dis;
//		
//		printd ("Parse CFF.\n");
//		v1 = dis.read ();
//		v2 = dis.read ();
//		printd (@"Version $v1.$v2\n");
//		header_size = dis.read ();
//		printd (@"Header size $(header_size)\n");
//		offset_size = dis.read ();
//		printd (@"Offset size $(offset_size)\n");
//			
//		// name index
//		offsets = read_index ();
//		
//		// name data
//		for (int i = 0; i < offsets.length () - 1; i++) {
//			off = (int) offsets.nth (i).data;
//			len = offsets.nth (i + 1).data - off;
//			//dis.seek (offset + off + header_size);
//			data = dis.read_string (len);
//			print (@"Found name $data\n");		
//		}	
//
//		// dict index
//		print (@"dict index\n");
//		dict_index = read_index ();
//
//		// dict data
//		id = 0;
//		val = 0;
//		for (int i = 0; i < dict_index.length () - 1; i++) {
//			off = (int) offsets.nth (i).data;
//			len = dict_index.nth (i + 1).data - dict_index.nth (i).data;
//			//dis.seek (offset + off + header_size);
//			
//			//for (int j = 0; j < len; j++) {
//				
//				if (dis.next_is_operator ()) {
//					id = dis.read ();
//		
//					if (id == 12) {
//						id = dis.read ();
//					} else {
//						switch (id) {
//							case 0:
//								version = val;
//								break;
//							default:
//								stderr.printf ("unknown operator");
//								break;
//						}
//					}			
//				} else {
//					val = dis.read_charstring_value ();	
//				}
//
//				printd (@"$i: id $(id)\n");
//				printd (@"val $(val)\n");
//				//printd (@"B $(dis.read ())\n");
//			//}	
//		}		
//
//		// string index
//		read_index ();
//	}
//	
	public void process () throws Error {
		FontData fd = new FontData ();
		String name = "typeface";
		
		// header
		fd.add_byte (1); // format version (1.0)
		fd.add_byte (0);
	
		fd.add_byte (4); // header size
		fd.add_byte (2); // offset field size - ushort
		
		// name index:
		fd.add_ushort (1);	// number of entries
		fd.add_byte (2); 	// offset field size
		fd.add_ushort (1);	// offset			
		fd.add (name.length()); // length of string
		fd.add_str (name);
	
		// top dict index
		fd.add_ushort (1);	// number of entries
		fd.add_byte (2); 	// offset field size
		fd.add_ushort (1);	// offset
		fd.add_ushort (2);	// offset

		fd.add_charstring_value (0);
		fd.add_byte (CHARSET);

		// string index
		fd.add_byte (0);

		// TODO: glyph gid to cid map
		fd.add_byte (0);
		
		fd.pad ();
	
		this.font_data = fd;
	}
}


