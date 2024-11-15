package sugarcube.formats.epub.structure.otf;

import sugarcube.common.data.collections.List3;

public class GlyphRange
{  
  public int start = -1;
  public int stop = -1;

  public List3<Glyph> glyphs = new List3<>();

  public void add(Glyph g)
  {
    int c = g.unicode;
    // Log.debug(this, ".addChar - "+c);
    if (start < 0 || c < start)
      start = c;
    if (stop < 0 || c > stop)
      stop = c;    
    glyphs.add(g);
  }

  public List3<GlyphRange> clusters()
  {
    List3<GlyphRange> ranges = new List3<>();
    GlyphRange range = null;
    int lastCode = -1;
    for (Glyph g: glyphs)
    {
      int c = g.unicode;
      if (lastCode < 0 || (lastCode + 1 != c))
      {
        ranges.add(range = new GlyphRange());
      }
      range.add(g);
      lastCode = c;
    }

    return ranges;
  }
  
  public int delta()
  {
    return glyphs.first().index-start;
  }

  public int length()
  {
    return stop - start;
  }
}
