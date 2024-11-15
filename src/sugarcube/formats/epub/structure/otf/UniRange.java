package sugarcube.formats.epub.structure.otf;

public class UniRange
{
  public int start;
  public int stop;
  
  public int length()
  {
    return stop-start;
  }
  
}
