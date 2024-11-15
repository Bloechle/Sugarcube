package sugarcube.formats.epub.structure.otf;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Path3.Seg;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.font.SVGGlyph;

public class Glyph
{
  public static final String NAME_NOTDEF = ".notdef";
  public static final String NAME_NULL = ".null";
  public static final String NAME_NM_RETURN = "CR";
  public static final String NAME_SPACE = "space";
  public int index = -1;
  public String name;
  public int unicode;
  public double advanceX;
  public double advanceY;
  public Path3 quadPath;
  public boolean unassigned = false;
  public int baseline = 0;
  public Seg[] segs;
  public String fontname = "noname";

  public Glyph(int index, SVGGlyph svgGlyph)
  {
    this.index = index;
    unicode = svgGlyph.code;
    name = "c_"+index;
//    name = svgGlyph.name();
//    if (name == null)
//      name = "c" + unicode;

//    Log.debug(this,  " - glyph: "+svgGlyph.unicodeString());
    quadPath = svgGlyph.path;
    quadPath = quadPath.reverseY();
    quadPath = quadPath.toZoubiQuadraticPath();    
    
//    quadPath = quadPath.toQuadraticPath();
    
    
    
    quadPath = quadPath.closeSubpaths(0.01);

    segs = quadPath.segments();
    advanceX = svgGlyph.horizAdvX;
    advanceY = svgGlyph.vertAdvY;
    
    this.fontname = svgGlyph.font.fontFamily;
    
  }

  public Glyph(int index, String name, int unichar_code, double width)
  {
    this.index = index;
    this.name = name;
    this.unicode = unichar_code;
    segs = new Seg[0];
  }

  public Rectangle3 bounds()
  {
    return quadPath == null ? new Rectangle3() : quadPath.bounds();
  }

  public boolean isUnassigned()
  {
    return unassigned;
  }

  public static Glyph notdefCharacter()
  {
    Glyph g = new Glyph(0, NAME_NOTDEF, -1, 0.5);
    g.unassigned = true;
    return g;
  }

  public static Glyph nullCharacter()
  {
    return new Glyph(1, NAME_NULL, '\0', 0);
  }

  public static Glyph nonMarkingReturn()
  {
    return new Glyph(2, NAME_NM_RETURN, '\r', 0);
  }

  public static Glyph spaceCharacter(SVGFont font)
  {
    Glyph g = new Glyph(3, NAME_SPACE, ' ', 0.5);
    if (font.canDisplay(' '))
      g.advanceX = font.glyph(' ').width();
    return g;
  }

}
