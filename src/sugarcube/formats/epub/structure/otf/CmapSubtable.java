package sugarcube.formats.epub.structure.otf;

public class CmapSubtable extends Table {
  
  // Override these methods in subtables for each format.
  
  /** Obtain length of subtable in bytes. */
  public int get_length () {
    return 0;
  }
  
  /** Get char code for a glyph id. */
  public int get_char (int i) {   
    return 0;
  }
  
  public void print_cmap () {
    StringBuilder s;
    int c;
    for (int i = 0; i < get_length (); i++) {
      s = new StringBuilder ();
      c = get_char (i);
      s.appendCodePoint(c);
//      printd (@"Char: $(s.str)  val ($((uint32)c))\tindice: $(i)\n");
    }
  }
}