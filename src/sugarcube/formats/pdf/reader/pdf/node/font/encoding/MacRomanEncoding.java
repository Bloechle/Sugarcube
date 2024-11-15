package sugarcube.formats.pdf.reader.pdf.node.font.encoding;

public class MacRomanEncoding extends LatinSet
{
  public MacRomanEncoding()
  {
    super("MacRomanEncoding");
  }

  @Override
  protected void add(int unicode, String name, int std, int mac, int win, int pdf)
  {
    if(mac!=0777)
      add((char)unicode, name, mac);
  }
  
  @Override
  public String name()
  {
    return "MacRomanEncoding";
  }
}
