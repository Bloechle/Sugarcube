package sugarcube.formats.pdf.reader.pdf.node.font.encoding;

public class PDFDocEncoding extends LatinSet
{
  public PDFDocEncoding()
  {
    super("PDFDocEncoding");
  }

  @Override
  protected void add(int unicode, String name, int std, int mac, int win, int pdf)
  {
    if(pdf!=0777)
      add((char)unicode, name, pdf);
  }
  
  @Override
  public String name()
  {
    return "PDFDocEncoding";
  }
}
