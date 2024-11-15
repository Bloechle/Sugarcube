package sugarcube.formats.epub.structure.otf;

public class HheaTable extends Table {

	int version;
	int ascender;
	int descender;
	int linegap;
	int max_advance;
	int min_lsb;
	int min_rsb;
	int xmax_extent;
	int carret_slope;
	int carret_slope_run;
	int carret_offset;
	
	int metric_format;
	public int num_horizontal_metrics;
		
	GlyfTable glyf_table;
	HeadTable head_table;
	HmtxTable hmtx_table;
	
	public HheaTable (GlyfTable g, HeadTable h, HmtxTable hm) {
		glyf_table = g;
		head_table = h;
		hmtx_table = hm;
		id = "hhea";
	}
	
	public double get_ascender () {
		return ascender * 1000 / head_table.get_units_per_em ();
	}

	public double get_descender () {
		return descender * 1000 / head_table.get_units_per_em ();
	}
	
//	public void parse (FontData dis) throws GLib.Error {
//		dis.seek (offset);
//		
//		version = dis.read_fixed ();
//		
//		if (!version.equals (1, 0)) {
//			warning (@"wrong version in hhea table $(version.get_string ())");
//		}
//		
//		ascender = dis.read_short ();
//		descender = dis.read_short ();
//		linegap = dis.read_short ();
//		max_advance = dis.read_ushort ();
//		min_lsb = dis.read_short ();
//		min_rsb = dis.read_short ();
//		xmax_extent = dis.read_short ();
//		carret_slope = dis.read_short ();
//		carret_slope_run = dis.read_short ();
//		carret_offset = dis.read_short ();
//		
//		// reserved x 4
//		dis.read_short ();
//		dis.read_short ();
//		dis.read_short ();
//		dis.read_short ();
//		
//		metric_format = dis.read_short ();
//		num_horizontal_metrics = dis.read_short ();
//	}
	
	public void process () throws Error {
		int ascender, descender;
		FontData fd = new FontData ();
		int version = 1 << 16;
		
		fd.add_fixed (version); // table version
		
		ascender = glyf_table.ymax;
		descender = glyf_table.ymin; // FIXME: look up, should it be -descender or descender?
		
		fd.add_16 (ascender); // Ascender
		fd.add_16 (descender); // Descender
		fd.add_16 (0); // LineGap
				
		fd.add_u16 (hmtx_table.max_advance); // maximum advance width value in 'hmtx' table.
		
		fd.add_16 (hmtx_table.min_lsb); // min left side bearing
		fd.add_16 (hmtx_table.min_rsb); // min right side bearing
		fd.add_16 (hmtx_table.max_extent); // x max extent Max(lsb + (xMax - xMin))
		
		fd.add_16 (1); // caretSlopeRise
		fd.add_16 (0); // caretSlopeRun
		fd.add_16 (0); // caretOffset
		
		// reserved
		fd.add_16 (0);
		fd.add_16 (0);
		fd.add_16 (0);
		fd.add_16 (0);
		
		fd.add_16 (0); // metricDataFormat 0 for current format.
		
		fd.add_u16 (glyf_table.glyphs.size()); // numberOfHMetrics Number of hMetric entries in 'hmtx' table

		// padding
		fd.pad ();
		this.font_data = fd;
	}
}
