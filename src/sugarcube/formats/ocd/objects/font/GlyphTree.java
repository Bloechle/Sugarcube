package sugarcube.formats.ocd.objects.font;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.MapInt;

import java.util.Collections;
import java.util.Iterator;

public class GlyphTree implements Iterable<SVGGlyph>
{
  // public static class GNode
  // {
  // MapInt<GNode> map = null;
  // SVGGlyph glyph = null;
  //
  // public boolean contains(int key)
  // {
  // return map != null && map.has(key);
  // }
  //
  // public GNode get(int key)
  // {
  // return map.get(key);
  // }
  //
  // public List3<SVGGlyph> glyphs(List3<SVGGlyph> glyphs)
  // {
  // if (glyphs == null)
  // glyphs = new List3<>();
  // if (glyph != null)
  // glyphs.add(glyph);
  // if (map != null)
  // for (GNode node : map.values())
  // node.glyphs(glyphs);
  // return glyphs;
  // }
  //
  // public int size(Counter counter)
  // {
  // if (counter == null)
  // counter = new Counter();
  // if (glyph != null)
  // counter.inc();
  // if (map != null)
  // for (GNode node : map.values())
  // node.size(counter);
  // return counter.value();
  // }
  //
  // @Override
  // public String toString()
  // {
  // StringBuilder sb = new StringBuilder();
  // sb.append("\nglyph=").append(glyph == null ? "null" : glyph.glyphName);
  // sb.append(map.toString());
  // return sb.toString();
  // }
  // }

  // root.glyph is defined as .undef glyph which may be null
  public MapInt<SVGGlyph> map = new MapInt<>();

  public GlyphTree()
  {
  }

  public boolean has(int c)
  {
    return map.has(c);
  }

  public boolean contains(int... codes)
  {
    for (int c : codes)
      if (!map.has(c))
        return false;
    return true;
  }

  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  public int size()
  {
    return map.size();
  }

  public SVGGlyph get(int c)
  {
    return map.get(c);
  }

  // public SVGGlyph get(int index, int[] text)
  // {
  // return get(text[index]);
  // }
  //
  // public SVGGlyph get(int index, char[] text)
  // {
  // return get(text[index]);
  // }
  //
  // public SVGGlyph get(int index, String text)
  // {
  // return get(text.charAt(index));
  // }

  @Override
  public Iterator<SVGGlyph> iterator()
  {
    return toList().iterator();
  }

  public List3<SVGGlyph> toList()
  {
    return map.list();
  }

  public SVGGlyph[] toArray()
  {
    return toList().toArray(new SVGGlyph[0]);
  }

  public List3<SVGGlyph> toSortedList()
  {
    List3<SVGGlyph> list = toList();
    Collections.sort(list);
    return list;
  }

  public boolean add(SVGGlyph glyph)
  {
    boolean exists = map.has(glyph.code);
    map.put(glyph.code, glyph);
    return exists;
  }

  public SVGGlyph remove(SVGGlyph glyph)
  {
    return map.remove(glyph.code);
  }
}
