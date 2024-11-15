
package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;

public class FontData {

	//uint Read pointer
	int rp = 0;
	//uint Write pointer
	int wp = 0;
	
	//uint 32 length without padding
	int len = 0;
	int padding = 0;
	
	int capacity;
	
	
	public byte[] table_data = null;
	
	public FontData()
	{
	  this(1024);
	}
	
	//uint32 1024
	public FontData (int size) {
		capacity = size;
		table_data = new byte[size];
	}
	
	
  public FontData write(int index, int value)
  {
    table_data[index]=(byte)(value & 0xff);
    return this;
  }
  
  public int read(int index)
  {
    return table_data[index] & 0xff;
  }
	
	//int -> uint, int -> uint8
	public void write_at (int pos, int new_data)		
	{		
	  write(pos,  new_data);		
	}
	
//	public void write_table (OtfInputStream dis, uint32 offset, uint32 length) throws Error {
//		uint32 l = length + (length % 4);  // padding after end of table
//		uint8 b;
//		
//		if (length >= l) {
//			expand (l);
//		}
//		
//		if (table_data == null) {
//			warning ("Failed to allocate memory for ttf data.");
//			return;
//		}
//		
//		seek (0);
//		dis.seek (offset);
//		
//		wp = 0;
//		while (wp < l) {
//			b = dis.read_byte ();
//			add (b);
//		}
//		
//		rp = 0;
//	}
	
	public int length_with_padding () {
		return len;
	}	
	
	public int length () {
		return len - padding;
	}
	
	public void pad () {
		while (len % 4 != 0) {
			add (0);
			padding++;
		}
	}
	
	/** Add additional checksum data to this checksum. */
	public int continous_check_sum (int current_check_sum) {
		int trp = rp;
		int l;
		
		if (length_with_padding () % 4 != 0) {
			Log.warn(this, "Table is not padded to correct size.");
		}
				
		seek (0);

		l = (length () % 4 > 0) ? length () / 4 + 1 : length () / 4; 

		for (int i = 0; i < l; i++) {
			current_check_sum += read_uint32 ();
		}
		
		rp = trp;
		
		return current_check_sum;
	}

	public int check_sum () {
		int sum = 0;
		sum = continous_check_sum (sum);
		return sum;
	}
	
	public void seek_end () {
		seek (len);
	}
		
	public void seek (int i) {
		rp = i;
		wp = i;
	}

	public void seek_relative (int i) {
		rp += i;
		wp += i;
	}

	/** Returns true if next byte is a CFF operator */
	public boolean next_is_operator () {
		int o = read ();
		seek_relative (-1);
		return (0 <= o  && o <=21);
	}
	
	//uint8
	public int read () {

		return read(rp++);
	}
		//
	public int read_fixed () {
		return read_uint32 ();
	}

	public int read_ulong () {
		return read_uint32 ();
	}

	public int read_ushort () {
		return read()<<8 | read();
	}
	
	public int read_int16 () {
		return read()<<8 | read();
	}
	
	public int read_short () throws Error {
		return read_int16 ();
	}
	
	public int read_uint32 () {
		return read () << 24 | read() << 16 | read() << 8 | read();
	}

	public long read_uint64 () 
	{	 
	  return read () << 56 | read() << 48 | read() << 40 | read() <<32 | read () << 24 | read() << 16 | read() << 8 | read();
	}

	public int read_f2dot14 () throws Error {
		return read_int16 ();		
	}

	public long read_udate () throws Error {
		return read_uint64 ();
	}

	public int read_byte () throws Error {
		return read ();
	}

	public char read_char () throws Error {
		return (char) read_byte ();
	}

	public String read_string (int length) throws Error {
		StringBuilder str = new StringBuilder ();
		char c;
		for (int i = 0; i < length; i++) {
			c = read_char ();
			str.append((char)c);
		}
		return str.toString();
	}

	public int read_charstring_value () throws Error {
		int a, b;
		a = read ();
		
		if (32 <= a && a <= 246) {
			return a - 139;
		}
		
		b = read ();
		
		if (247 <= a && a <= 250) {	
			return (a - 247) * 256 + b + 108;
		}
		
		if (251 <= a && a <= 254) {
			return -((a - 251) * 256) - b - 108;
		}

		if (a == 255) {
			// Implement it
			Log.warn(this, ".read_charstring_value - fractions not implemented yet.");
		}

		Log.warn(this, ".read_charstring_value - should never reach this point.");		
		
		return 0;
	}
	
	public void add_fixed (int f) throws Error {
		add_u32 (f);
	}

	public void add_short (int d) throws Error {
		add_16 (d);
	}
	
	public void add_ushort (int d) throws Error {
		add_u16 (d);
	}
		
	public void add_ulong (long d) throws Error {
		add_u32 (d);
	}
		
	public void add_byte (int b) throws Error {
		add (b);
	}
	
	private void expand (int extra_bytes) {
		capacity += extra_bytes;		
		byte[] new_data = new byte[capacity];		
		System.arraycopy(table_data, 0, new_data, 0, table_data.length);		
		table_data = new_data;
	}	
	
	public void add (int d) {
		if (len >= capacity) {
			expand (1024);
		}
		
		table_data[wp] = (byte)(d & 0xff);

		if (wp == len) {
			len++;
		}
				
		wp++;
	}

	public void add_littleendian_u16 (int i) {
		add ( (i & 0x00FF));
		add (((i & 0xFF00) >> 8));
	}
		
	public void add_u16 (int d) {
		int n = d >> 8;
		add (n);
		add (d - (n << 8));
	}

	public void add_16 (int i) {
	  int s = i >> 8;		
		add (s);
		add (i - (s << 8));
	}

	public void add_littleendian_u32 (int i) {
		add  (i & 0x000000FF);
		add ((i & 0x0000FF00) >> 8);
		add ((i & 0x00FF0000) >> 16);
		add ((i & 0xFF000000) >> 24);
	}
		
	public void add_u32 (long i) {
		int s = (int)(i >> 16);
		
		add_u16 ((int)s);
		add_u16 ((int)(i - (s << 16)));
	}

	public void add_64(long i) {
		long s = i >> 32;
		
		add_u32 ((int)(s & 0xffffffff));
		add_u32 ((int)((i - (s << 32))& 0xffffffff));		
	}
	
	public void add_str_littleendian_utf16 (String s) {
		add_str_utf16 (s, true);
	}
	
	public void add_str_utf16(String s)
	{
	  this.add_str_utf16(s, false);
	}
	
	//false
  public void add_str_utf16 (String s, boolean little_endian) {
    int c0;
    int c1;

    // FIXME: gconvert it instead.
    
    for(char c: s.toCharArray())
    {
      c0 = (c >> 8);
      c1 = (c - (c0 << 8));
      
      if (little_endian) {
        add (c1);
        add (c0);
      } else {
        add (c0);
        add (c1);
      }
    }
  }
	
	public void add_str (String s) {
		char[] data = s.toCharArray();
		for (int n = 0; n < data.length; n++) { 
			add (data[n]);
		}		
	}
	
	public void add_tag (String s) 		
	{
		add_str (s);
	}

	public void add_charstring_value (int v) throws Error {
		int w;
		int t;
		
		if (!(-1131 <= v && v<= 1131)) {
			Log.warn(this, ".add - charstring value out of range");
			v = 0;		
		}
		
		if (-107 <= v && v <= 107) {
			add_byte ((v + 139));
		} else if (107 < v && v <= 1131) {
			// positive value
			w = v;
			v -= 108;
			
			t = 0;
			while (v >= 256) {
				v -= 256;
				t++;
			}
			
			add_byte (t + 247);
			add_byte ( (w - 108 - (t * 256)));		
		} else if (-1131 <= v && v < -107) {
			// negative value
			v = -v - 108;
			add_byte ( ((v >> 8) + 251));
			add_byte ( (v & 0xFF));
		} else {
			// Todo add fraction
		}
	}
	
	public void append (FontData fd) {
		fd.seek (0);
		for (int i = 0; i < fd.length (); i++) {
			add (fd.read ());
		}
	}
	
	public void dump () {
		for (int i = 0; i < length_with_padding (); i++) {
			System.out.print(table_data[i]+" ");
		}
		System.out.println();
	}
	
	public byte[] data()
	{
	  if(len==table_data.length)
	    return table_data;
	  byte[] data = new byte[len];
	  System.arraycopy(table_data,  0,  data,  0,  Math.min(table_data.length,  len));
	  return data;
	}
}


