package sugarcube.formats.pdf.reader.pdf.object;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;

import java.awt.geom.Rectangle2D;

public class PDFRectangle extends PDFObject
{
  public static final PDFRectangle NULL_PDFRECTANGLE = new PDFRectangle();
  private static final Rectangle3 NULL_RECTANGLE = new Rectangle3();
  private Rectangle3 rectangle;

  private PDFRectangle()
  {
    super(Type.Rectangle);
    this.rectangle = NULL_RECTANGLE;
  }

  public PDFRectangle(PDFObject pdfObject, double[] oppositePoints)
  {
    super(Type.Rectangle, pdfObject);
    if (oppositePoints.length == 4)
      this.rectangle = new Rectangle3(oppositePoints);
    else
    {
      this.rectangle = NULL_RECTANGLE;
      this.invalidate();
    }
  }

  public PDFRectangle(PDFObject pdfObject, double x, double y, double width, double height)
  {
    super(Type.Rectangle, pdfObject);
    this.rectangle = new Rectangle3(x, y, width, height);
  }

  public PDFRectangle(PDFObject pdfObject, Rectangle2D rectangle)
  {
    super(Type.Rectangle, pdfObject);
    this.rectangle = new Rectangle3(rectangle);
  }

  public Rectangle3 rectangle()
  {
    return rectangle;
  }
  
  public Path3 path(Rectangle2D bounds)
  {
    return new Path3(new Transform3(1, 0, 0, -1, -bounds.getMinX(), bounds.getMaxY()).transform(rectangle));
  }

  public PDFRectangle copy()
  {
    return new PDFRectangle(this, this.rectangle);
  }

  @Override
  public String stringValue()
  {
    return this.rectangle.toString();
  }

  @Override
  public String sticker()
  {
    return nodeNamePrefix() + stringValue();
  }

  public double maxX()
  {
    return rectangle.maxX();
  }

  public double maxY()
  {
    return rectangle.maxY();
  }

  public double minX()
  {
    return rectangle.minX();
  }

  public double minY()
  {
    return rectangle.minY();
  }

  public double width()
  {
    return rectangle.getWidth();
  }

  public double height()
  {
    return rectangle.getHeight();
  }

  public double x()
  {
    return rectangle.getX();
  }

  public double y()
  {
    return rectangle.getY();
  }

  public int intWidth()
  {
    return (int) (width() + 0.5);
  }

  public int intHeight()
  {
    return (int) (height() + 0.5);
  }

  public int intX()
  {
    return (int) (x() + 0.5);
  }

  public int intY()
  {
    return (int) (y() + 0.5);
  }

  @Override
  public String toString()
  {
    Rectangle3 r = rectangle;
    return r == null ? "[none]" : "[" + r.intX() + "," + r.intY() + "," + r.intWidth() + "," + r.intHeight() + "]";
  }

  public Rectangle2D getBounds()
  {
    return rectangle;
  }
}
