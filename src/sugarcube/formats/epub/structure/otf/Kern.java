package sugarcube.formats.epub.structure.otf;

public class Kern
{
  public int left;
  public int right;
  public int kerning;

  public Kern(int l, int r, int k)
  {
    left = l;
    right = r;
    kerning = k;
  }
}
