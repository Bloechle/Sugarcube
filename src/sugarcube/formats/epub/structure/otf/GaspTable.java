package sugarcube.formats.epub.structure.otf;

public class GaspTable extends Table
{

  public GaspTable()
  {
    id = "gasp";
  }

  // public void parse (FontData dis) throws Error {
  // }

  public void process()
  {
    FontData fd = new FontData();

    fd.add_ushort(0);
    fd.add_ushort(0);

    fd.pad();

    this.font_data = fd;
  }

}
