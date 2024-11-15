package sugarcube.formats.epub.structure.otf;


public class Cmap
{

  /** Largest power of two less than max. */
  public static int largest_pow2(int max)
  {
    int x = 1;
    int l = 0;
    while (x <= max)
    {
      l = x;
      x = x << 1;
    }
    return l;
  }

  public static int largest_pow2_exponent(int max)
  {
    int exp = 0;
    int l = 0;
    int x = 0;
    while (x <= max)
    {
      l = exp;
      exp++;
      x = 1 << exp;
    }
    return l;
  }
}
