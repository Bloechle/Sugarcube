package sugarcube.formats.pdf.reader.pdf.node.font.encoding;

public class WinAnsiEncoding extends LatinSet
{
  public WinAnsiEncoding()
  {
    super("WinAnsiEncoding");
  }

  @Override
  protected void init()
  {
    //adds unspecified but used codes (before init to ensure original unicodes precedence !
    add((char) 160, "nonbreakingspace", 160);
    add((char) 149, "bullet", 129);//2013-10-25, 129 used as bullet in CFF without mapping?!?
    super.init();
  }

  @Override
  protected void add(int unicode, String name, int std, int mac, int win, int pdf)
  {
    if (win != 0777)
      add((char) unicode, name, win);
  }

  @Override
  public String name()
  {
    return "WinAnsiEncoding";
  }
}
