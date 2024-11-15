package sugarcube.formats.epub.structure.otf;

public class OffsetTable extends Table {
	DirectoryTable directory_table;
		
	public int num_tables = 0;
	int search_range = 0;
	int entry_selector = 0;
	int range_shift = 0;
	
	public OffsetTable (DirectoryTable t) {
		id = "Offset table";
		directory_table = t;
	}
		
//	public void parse (FontData dis) throws Error {
//		Fixed version;
//		
//		dis.seek (offset);
//		
//		version = dis.read_fixed ();
//		num_tables = dis.read_ushort ();
//		search_range = dis.read_ushort ();
//		entry_selector = dis.read_ushort ();
//		range_shift = dis.read_ushort ();
//		
//		printd (@"Font file version $(version.get_string ())\n");
//		printd (@"Number of tables $num_tables\n");		
//	}
	
	public void process ()  {
		FontData fd = new FontData ();
		int version = 0x00010000; // sfnt version 1.0 for TTF CFF else use OTTO

		
		num_tables = directory_table.tables().size() - 2; // number of tables, skip DirectoryTable and OffsetTable
		
		search_range = max_pow_2_less_than_i (num_tables) * 16;
		entry_selector = max_log_2_less_than_i (num_tables);
		range_shift = 16 * num_tables - search_range;

		fd.add_fixed (version); 
		fd.add_u16 (num_tables);
		fd.add_u16 (search_range);
		fd.add_u16 (entry_selector);
		fd.add_u16 (range_shift);
		
		// skip padding for offset table 
		
		this.font_data = fd;
	}
}

