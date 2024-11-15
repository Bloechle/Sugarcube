package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class PDFMatrix extends PDFNode
{
  private float[] values;

  public PDFMatrix(float[] values)
  {
    super("PDFMatrix", null);
    if (values != null && values.length == 6)
      this.values = Zen.Array.copy(values);
    else
      this.values = Zen.Array.Floats(1, 0, 0, 1, 0, 0);
  }

  public PDFMatrix(double... values)
  {
    super("PDFMatrix", null);
    if (values != null && values.length == 6)
      this.values = Zen.Array.toFloats(values);
    else
      this.values = Zen.Array.Floats(1, 0, 0, 1, 0, 0);
  }

  public PDFMatrix(List<PDFObject> params)
  {
    super("PDFMatrix", null);
    this.values = new float[6];
    int index = 0;

    if (params.size() == 6)
      for (PDFObject pdfObject : params)
        this.values[index++] = pdfObject.floatValue(0f);
    else
    {
      Log.warn(this, " - wrong number of elements: " + params.size() + ", params=" + params);
      this.values = Zen.Array.Floats(1, 0, 0, 1, 0, 0);
    }
  }

  public boolean isIdentity()//should use with care
  {
    return is(0, 1) && is(1, 0) && is(2, 0) && is(3, 1) && is(4, 0) && is(5, 0);
  }

  public boolean isOnlyTranslation()//should use with care
  {
    return is(0, 1) && is(1, 0) && is(2, 0) && is(3, 1);
  }

  public final boolean is(int index, float value)
  {
    return values != null && index < values.length && Math.abs(values[index] - value) < 0.00001;//dummy epsilon
  }

  public PDFMatrix concat(float... m)
  {
    return new PDFMatrix(
      values[0] * m[0] + values[1] * m[2],
      values[0] * m[1] + values[1] * m[3],
      values[2] * m[0] + values[3] * m[2],
      values[2] * m[1] + values[3] * m[3],
      values[4] * m[0] + values[5] * m[2] + m[4],
      values[4] * m[1] + values[5] * m[3] + m[5]);
  }

  public PDFMatrix concat(PDFMatrix m)
  {
    return concat(m.values);
  }

  public PDFMatrix preConcat(float... m)
  {
    return new PDFMatrix(
      m[0] * values[0] + m[1] * values[2],
      m[0] * values[1] + m[1] * values[3],
      m[2] * values[0] + m[3] * values[2],
      m[2] * values[1] + m[3] * values[3],
      m[4] * values[0] + m[5] * values[2] + values[4],
      m[4] * values[1] + m[5] * values[3] + values[5]);
  }

  public PDFMatrix preConcat(PDFMatrix m)
  {
    return preConcat(m.values);
  }

  public PDFMatrix translate(double x, double y)
  {
    return new PDFMatrix(values[0], values[1], values[2], values[3], values[4] + x, values[5] + y);
  }

  public PDFMatrix translateIt(double x, double y)
  {
    this.values[4] += x;
    this.values[5] += y;
    return this;
  }

  public Point3 toOrigin()
  {
    Point3 p = new Point3(this.values[4], this.values[5]);
    this.values[4] = 0f;
    this.values[5] = 0f;
    return p;
  }

  public PDFMatrix reverse()
  {
    return new PDFMatrix(values[0], -values[1], -values[2], values[3], values[4], values[5]);
  }

  public PDFMatrix reverse(double x, double y)
  {
    return new PDFMatrix(values[0], -values[1], -values[2], values[3], values[4] - x, y - values[5]);
  }

  public Transform3 transform()
  {
    return new Transform3(Zen.Array.toDoubles(values));
  }

  public Shape transform(Shape shape)
  {
    return transform().transform(shape);
  }

  public Coords transform(Coords coords)
  {
    return coords.transform(transform());
  }

  public PDFMatrix newPosition(Point2D p)
  {
    return new PDFMatrix(values[0], values[1], values[2], values[3], p.getX(), p.getY());
  }

  public PDFMatrix setPosition(double x, double y)
  {
    this.values[4] = (float) x;
    this.values[5] = (float) y;
    return this;
  }

  public Point2D.Double getPosition()
  {
    return new Point2D.Double(values[4], values[5]);
  }
  
  public double sx()
  {
    return values[0];
  }

  public double scaleX()
  {
    return values[0];
  }

  public double getScaleX()
  {
    return values[0];
  }
  
  public double sy()
  {
    return values[3];
  }  

  public double scaleY()
  {
    return values[3];
  }

  public double getScaleY()
  {
    return values[3];
  }
  
  public double hx()
  {
    return values[1];
  }

  public double shearX()
  {
    return values[1];
  }

  public double getShearX()
  {
    return values[1];
  }
  
  public double hy()
  {
    return values[2];
  }

  public double shearY()
  {
    return values[2];
  }

  public double getShearY()
  {
    return values[2];
  }
  
  public double x()
  {
    return values[4];
  }

  public double translateX()
  {
    return values[4];
  }

  public double getTranslateX()
  {
    return values[4];
  }
  
  public double y()
  {
    return values[5];
  }

  public double translateY()
  {
    return values[5];
  }

  public double getTranslateY()
  {
    return values[5];
  }

  public double get(int index)
  {
    return values[index];
  }

  public PDFMatrix set(int index, double value)
  {
    this.values[index] = (float) value;
    return this;
  }

  public float[] values()
  {
    return values;
  }

  public PDFMatrix copy()
  {
    return new PDFMatrix(values);
  }

  @Override
  public String sticker()
  {
    return "PDFMatrix";
  }

  @Override
  public String toString()
  {
    if (this.isIdentity())
      return "[Identity]";
    String s = "[";
    for (int i = 0; i < values.length; i++)
      s += values[i] + (i == values.length - 1 ? "]" : ",");
    return s;
  }
}
