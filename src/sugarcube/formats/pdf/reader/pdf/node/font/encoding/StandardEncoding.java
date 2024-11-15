package sugarcube.formats.pdf.reader.pdf.node.font.encoding;

// also named "Adobe Standard Encoding"
public class StandardEncoding extends LatinSet
{
  public StandardEncoding()
  {
    super("StandardEncoding");
  }

  public StandardEncoding(String name)
  {
    super(name);
  }

  @Override
  public void add(int unicode, String name, int std, int mac, int win, int pdf)
  {
    if (std != 0777)
      add((char)unicode, name, std);
  }

  @Override
  public String name()
  {
    return "StandardEncoding";
  }
}
