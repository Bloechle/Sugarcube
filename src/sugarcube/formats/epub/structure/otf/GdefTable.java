package sugarcube.formats.epub.structure.otf;

public class GdefTable extends Table
{

  public GdefTable()
  {
    id = "GDEF";
  }

  // public override void parse (FontData dis){
  // }

  public void process()
  {
    FontData fd = new FontData();

    fd.add_ulong(0x00010002);
    fd.add_ushort(0); // class def
    fd.add_ushort(0); // attach list
    fd.add_ushort(0); // ligature carret
    fd.add_ushort(0); // mark attach
    fd.add_ushort(0); // mark glyf
    fd.add_ushort(0); // mark glyf set def

    fd.pad();

    this.font_data = fd;
  }

}
