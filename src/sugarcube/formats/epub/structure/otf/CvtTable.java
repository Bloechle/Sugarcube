package sugarcube.formats.epub.structure.otf;

public class CvtTable extends Table
{

  public CvtTable()
  {
    id = "cvt ";
  }

  // public void parse (FontData dis) throws Error {
  // }

  public void process()
  {
    FontData fd = new FontData();

    fd.add_ushort(0);
    fd.pad();

    this.font_data = fd;
  }

}
