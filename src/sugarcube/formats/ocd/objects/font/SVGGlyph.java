package sugarcube.formats.ocd.objects.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.A;
import sugarcube.common.data.collections.Couple;
import sugarcube.common.data.collections.IntArray;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.interfaces.Glyph;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Nb;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDNode;

import java.awt.*;
import java.util.Collection;
import java.util.Comparator;

public class SVGGlyph extends OCDNode implements Glyph, Comparable<SVGGlyph>, Comparator<SVGGlyph>
{
  public static final String TAG = "glyph";
  // used when remapping glyph to unicodes leads to conflict
  public transient int fontOverrideIndex = -1;
  public transient Couple<Integer, Path3> cache = new Couple<>();
  protected final static Transform3 TM = new Transform3(300, 0, 0, 300, 100, 300);
  public SVGFont font;
  public Path3 path;// called "d" in svg
  // code should be unicode, if not possible, use remap
  public int code = -1;
  public int[] remap = new int[0];
  public String glyphName = "";
  public String orientation = null;// null,h,v
  public String arabicForm = null;// null,initial,medial,terminal,isolated
  public String lang = null;
  public float horizAdvX = 0;// i.e., glyph width
  public float vertAdvY = 0;// i.e, glyph height with horizontal writing mode
  public float vertOriginX = 0;
  public float vertOriginY = 0;
  public String replacement = null;

  public SVGGlyph()
  {
    this(null);
  }

  public SVGGlyph(SVGFont font)
  {
    super(TAG, font);
    this.font = font;
  }

  public SVGGlyph(SVGFont font, String glyphName, Shape path, double width, int unicode)
  {
    this(font, glyphName, path, new Point3(width, 0), null, unicode);
  }

  public SVGGlyph(SVGFont font, String glyphName, Shape path, Point3 advance, Point3 verOrigin, int unicode)
  {
    this(font);
    this.glyphName = glyphName;
    this.path = path instanceof Path3 ? (Path3) path : new Path3(path);
    if (this.path == null)
    {
      this.path = new Path3();
      Log.warn(this, " - path==null: unicode=" + unicode);
    }
    this.code = unicode;
    this.horizAdvX = advance.x;
    this.vertAdvY = advance.y;
    if (verOrigin != null)
    {
      this.vertOriginX = verOrigin.x;
      this.vertOriginY = verOrigin.y;
    }
  }

  @Override
  public int override()
  {
    return fontOverrideIndex;
  }

  public void closeSubpaths()
  {
    // fonts glyphs are closed paths !... sub paths should be closed too !!!
    this.path = path.closeSubpaths();
  }

  public SVGGlyph refont(SVGFont font)
  {
    this.font = font;
    return this;
  }

  public boolean isReplacement()
  {
    return replacement == null || replacement.isEmpty();
  }

  public boolean isPathEmpty()
  {
    return this.path.isEmpty();
  }

  public boolean isUnicode(int... uni)
  {
    return hasRemap() ? A.equals(remap, uni) : (uni.length == 1 && code == uni[0]);
  }

  public boolean hasCode()
  {
    return code >= 0;
  }

  public Point3 displacement(Transform3 tm)
  {
    return new Transform3(tm.scaleX(), tm.shearY(), tm.shearX(), tm.scaleY(), 0, 0).transform(new Point3(width(), height()));
  }

  public void setFont(SVGFont font)
  {
    this.font = font;
  }

  public void setCode(int code)
  {
    this.code = code;
  }

  public void setHorizAdvX(float horizAdvX)
  {
    this.setWidth(horizAdvX);
  }

  public void setWidth(float width)
  {
    this.horizAdvX = width;
  }

  public void setVertAdvY(float vertAdvY)
  {
    this.setHeight(vertAdvY);
  }

  public void setHeight(float height)
  {
    this.vertAdvY = height;
  }

  public float horizAdvX()
  {
    return width();
  }

  @Override
  public float width()
  {
    return horizAdvX;
  }

  @Override
  public float height()
  {
    return vertAdvY;
  }

  @Override
  public Point3 vertOrigin()
  {
    return new Point3(vertOriginX, vertOriginY);
  }

  public String pathData()
  {
    Path3 unitPath = path.transform(font.unitsPerEm, 0, 0, -font.unitsPerEm, 0, 0);
    return OCD.path2xml(unitPath, null);
  }

  public boolean setPathData(String data)
  {
    this.cache.clear();
    Path3 unitPath = OCD.xml2path(data, true);
    this.path = unitPath == null ? new Path3() : unitPath.transform(1f / font.unitsPerEm, 0, 0, -1f / font.unitsPerEm, 0, 0);
    return unitPath != null;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    // sugarcube api does XML escaping on the fly
    if (hasCode())
      xml.write("unicode", code());
    String uniName = "";
    for (int c : remap)
      uniName += "#" + c;
    xml.write("glyph-name", uniName.isEmpty() ? glyphName : glyphName + "," + uniName);
    xml.write("orientation", orientation);
    xml.write("arabic-form", arabicForm);
    xml.write("lang", lang);
    xml.write("horiz-adv-x", font.toUnits(horizAdvX));
    xml.write("vert-adv-y", vertAdvY == 0 ? null : font.toUnits(vertAdvY));
    xml.write("vert-origin-x", vertOriginX == 0 ? null : font.toUnits(vertOriginX));
    xml.write("vert-origin-y", vertOriginY == 0 ? null : font.toUnits(vertOriginY));
    xml.write("d", pathData());
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
    // since org.w3c.dom.Node does Entity Ref unescaping;
    String uni = e.value("unicode", " ");    
    this.code = uni.isEmpty() ? Unicodes.ASCII_SP : uni.codePointAt(0);
    this.glyphName = e.value("glyph-name", null);

    if (glyphName != null && glyphName.contains(","))
    {
      IntArray map = new IntArray(1);
      for (String name : glyphName.split(","))
      {
        name = name.trim();
        if (name.startsWith("#"))
        {
          for (String value : name.split("#"))
            if (!value.trim().isEmpty())
              map.add(Nb.Int(value));
        } else
          glyphName = name;
      }
      this.remap = map.array();
    }

    this.orientation = e.value("orientation", null);
    this.arabicForm = e.value("arabic-form", null);
    this.lang = e.value("lang", null);
    this.horizAdvX = font.fromUnits(e.value("horiz-adv-x", null));
    this.vertAdvY = font.fromUnits(e.value("vert-adv-y", null));
    this.vertOriginX = font.fromUnits(e.value("vert-origin-x", null));
    this.vertOriginY = font.fromUnits(e.value("vert-origin-y", null));
    this.setPathData(e.value("d", e.cdata()));
    this.path.setWind(e.value("wind", Path3.NONZERO));
  }

  public SVGFont font()
  {
    return font;
  }

  @Override
  public String name()
  {
    return this.glyphName;
  }

  public void setName(String name)
  {
    this.glyphName = name;
  }

  public Path3 path()
  {
    return path;
  }

  public Path3 path(double fontsize, boolean vertical)
  {
    return path(vertical ? -fontsize : fontsize);
  }

  public Path3 path(double fontsize, boolean vertical, double dx, double dy)
  {
    if (dx == 0 && dy == 0)
      return path(fontsize, vertical);
    else
      return vertical ? path.translate(dx - vertOriginX, dy + vertOriginY).scale(fontsize) : path.translate(dx, dy).scale(fontsize);
  }

  @Override
  public Path3 path(double fontsize)
  {
    if (fontsize <= 1 && fontsize >= -1)
      return fontsize < 0 ? path.translate(-vertOriginX, vertOriginY) : path;

    Path3 scaledPath = null;
    Integer key = (int) (fontsize * 100);
    if (this.cache.isFirst(key))
      scaledPath = this.cache.value();
    else
    {
      // fontsize < 0 if vertical writing (handled as horizontal writing in OCD)
      scaledPath = fontsize < 0 ? path.translate(-vertOriginX, vertOriginY).scale(-fontsize) : path.scale(fontsize);
      this.cache.set(key, scaledPath);
    }
    return scaledPath;
  }

  public void setPath(Path3 path)
  {
    this.path = path;
  }

  // public long unicodesLong()
  // {
  // return toUnicodesLong(this.unicodes());
  // }
  //
  // public static int[] toIntUnicodes(long uni)
  // {
  // int u1 = (int) (uni & 0x1fffff);
  // int u2 = (int) (uni >> 21 & 0x1fffff);
  // int u3 = (int) (uni >> 42 & 0x1fffff);
  // return u3 == 0 ? (u2 == 0 ? Zen.Array.ints(u1) : Zen.Array.ints(u1, u2)) :
  // Zen.Array.ints(u1, u2, u3);
  // }
  //
  // public static long toUnicodesLong(int... uni)
  // {
  // //since glyph may use up to 3 unicodes (never seen a bigger "uniglyph")
  // //we encode them using 21 bits (precisely allowing the whole unicode range
  // to be encoded !!!)
  // //first long bit is always zero (until I find something useful to do with
  // it)
  // switch (uni.length)
  // {
  // case 0:
  // return 0L;
  // case 1:
  // return (long) uni[0];
  // case 2:
  // return uni[0] | uni[1] << 21;
  // case 3:
  // return uni[0] | uni[1] << 21 | uni[2] << 42;
  // default:
  // OCD.LOG.warn(OCDGlyph.class, ".toLongUnicodes - unicodes.length>3: " +
  // Zen.Array.toString(uni));
  // return 0L;
  // }
  // }

  // public int[] codes()
  // {
  // return code;
  // }

  @Override
  public String code()
  {
    return ((char) code) + "";
  }

  @Override
  public String unicode()
  {
    return hasRemap() ? new String(remap, 0, remap.length) : code();
  }

  public int[] remap()
  {
    return this.remap;
  }

  public void setRemap(String unicodes)
  {
    int[] map = new int[unicodes.length()];
    for (int i = 0; i < map.length; i++)
      map[i] = unicodes.charAt(i);
    this.setRemap(map);
  }

  public void setRemap(int... unicodes)
  {
    this.remap = unicodes.length == 1 && unicodes[0] == code ? new int[0] : unicodes;
  }

  public static boolean isSmallCap(Glyph glyph)
  {
    String name = glyph.name();
    return name != null && name.toLowerCase().endsWith(".sc");
  }

  public static boolean isWhitespace(Glyph glyph)
  {
    String chars = glyph.code();
    int c = chars.length() == 1 ? chars.charAt(0) : -1;
    return c == Unicodes.ASCII_SP || c == Unicodes.NBSP;
  }

  public boolean isSmallCap()
  {
    return isSmallCap(this);
  }

  public boolean isWhitespace()
  {
    return isWhitespace(this);
  }

  public boolean hasRemap()
  {
    return remap != null && remap.length > 0;
  }

  public void cleanRemap()
  {
    if (isSmallCap() && remap.length == 1)
      remap = new int[]
      { Character.toUpperCase(remap[0]) };
    if (remap.length == 1 && remap[0] == code)
      remap = new int[0];
  }

  @Override
  public Rectangle3 bounds()
  {
    return new Rectangle3(TM.transform(new Rectangle3(0, -font().ascent(), horizAdvX, font().ascent() - font().descent())).getBounds2D());
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {
    g.setColor(Color.GREEN.darker().darker());
    g.fill(new Circle3(TM.x(), TM.y(), 10));
    g.draw(TM.transform(new Rectangle3(0, 0, horizAdvX, 0)));
    if (height() != 0)
    {
      g.fill(new Circle3(TM.x() + vertOriginX * TM.sx(), TM.y() - vertOriginY * TM.sy(), 10));
      g.draw(TM.transform(new Rectangle3(vertOriginX, -vertOriginY, 0, -vertAdvY)));
    }
    // g.draw(xt.transform(new XRectangle(0, -gstate.font().ascent(), width,
    // gstate.font().ascent() + gstate.font().descent())));
    g.setColor(Color.BLACK);
    // g.fill(path);
    g.fill(TM.transform(path));
  }

  @Override
  public String sticker()
  {
    return tag() + "[" + this.unicode() + "]";
  }

  @Override
  public int hashCode()
  {
    return code;
  }

  @Override
  public boolean equals(Object o)
  {
    return getClass() == o.getClass() ? code == ((SVGGlyph) o).code : false;
  }

  @Override
  public int compareTo(SVGGlyph g)
  {
    return this.compare(this, g);
  }

  @Override
  public int compare(SVGGlyph g0, SVGGlyph g1)
  {
    return g0.code - g1.code;
  }

  public SVGGlyph derive(SVGFont font, double sx, double sy)
  {
    return derive(font, sx, sy, false, false, -1);
  }

  public SVGGlyph derive(SVGFont font, double sx, double sy, boolean transparent, boolean light, int newCode)
  {
    SVGGlyph derived = new SVGGlyph(font);
    derived.code = newCode > -1 ? newCode : code;
    derived.glyphName = light ? "" : glyphName;
    derived.remap = light ? new int[0] : remap;
    derived.orientation = orientation;
    derived.arabicForm = arabicForm;
    derived.lang = lang;
    derived.horizAdvX = horizAdvX * (float) sx;
    derived.vertAdvY = vertAdvY;
    derived.vertOriginX = vertOriginX;
    derived.vertOriginY = vertOriginY;
    derived.replacement = replacement;
    if (transparent)
    {
      derived.path = new Path3(0, 0);
      derived.path.closePath();
    } else
      derived.path = Math.abs(sx - 1) < 0.001 && Math.abs(sy - 1) < 0.001 ? path.copy() : path.scale(sx, sy);
    return derived;
  }

  @Override
  public SVGGlyph copy()
  {
    SVGGlyph copy = new SVGGlyph();
    copy.path = this.path.copy();
    copy.code = this.code;
    copy.glyphName = this.glyphName;
    copy.remap = A.copy(this.remap);
    copy.orientation = this.orientation;
    copy.arabicForm = this.arabicForm;
    copy.lang = this.lang;
    copy.horizAdvX = this.horizAdvX;
    copy.vertAdvY = this.vertAdvY;
    copy.vertOriginX = this.vertOriginX;
    copy.vertOriginY = this.vertOriginY;
    copy.replacement = this.replacement;
    return copy;
  }
}
