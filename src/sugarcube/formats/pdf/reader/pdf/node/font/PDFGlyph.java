package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFont.Height;

public class PDFGlyph
{
  public interface Interface
  {
    PDFGlyph outline(String name, Unicodes uni, int code);

    Transform3 transform();
  }
  protected Path3 path;
  protected Point3 advance;
  protected Point3 origin;
  protected int fontcode;

  public PDFGlyph()
  {
  }

  public PDFGlyph(Path3 path, float... advance)
  {
    this.path = path;    
    if (advance != null && advance.length > 0)
      this.advance = new Point3(advance[0], advance.length > 1 ? advance[1] : 0);
  }

  public int fontcode()
  {
    return fontcode;
  }

  public Path3 path()
  {
    return path;
  }

  public Path3 pathCopy()
  {
    return path.copy();
  }
  
  public void setPath(Path3 path)
  {
    this.path = path;
  }

  public Point3 advance()
  {
    return advance;
  }

  public float width()
  {
    return advance == null ? 0 : advance.x;
  }

  public float height()
  {
    return advance == null ? 0 : advance.y;
  }

  public Point3 origin()
  {
    return origin;
  }

  public void setWidth(double width)
  {
    this.advance = new Point3(width, this.advance == null ? 0 : this.advance.y);
  }

  public void setHeight(double height)
  {
    this.advance = new Point3(this.advance == null ? 0 : this.advance.x, height);
  }

  public void setHeight(Height height, double scale)
  {
    this.setHeight(height.height * scale);
    this.origin = new Point3(height.ox * scale, height.oy * scale);
  }

  public PDFGlyph reverseY()
  {
    this.path = path.reverseY();
    return this;
  }

  public boolean isEmpty()
  {
    return this.path == null || this.path.isEmpty();
  }

  protected PDFGlyph render(float size, boolean vert)
  {
    PDFGlyph g = new PDFGlyph();
    g.fontcode = fontcode;
    g.path = origin == null || !vert ? path : path.translate(-origin.x, origin.y);
    g.path = g.path.scale(size);
    g.advance = new Point3(advance.x * size, advance.y * size);
    g.origin = origin;
    return g;
  }

  public String toString()
  {
    return "fontcode="+fontcode+", path="+path.nbOfSegments(true, false);
  }
}
