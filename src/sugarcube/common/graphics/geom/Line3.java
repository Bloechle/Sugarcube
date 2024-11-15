package sugarcube.common.graphics.geom;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.interfaces.Range2D;
import sugarcube.common.numerics.Math3;
import sugarcube.common.ui.fx.shapes.FxLine;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Line3 extends Line2D.Float implements Shape3, Range2D
{
  public Line3()
  {
  }

  public Line3(float[] a)
  {
    this(a.length > 0 ? a[0] : 0, a.length > 1 ? a[1] : 0, a.length > 2 ? a[2] : 0, a.length > 3 ? a[3] : 0);
  }

  public Line3(double x2, double y2)
  {
    super(0, 0, (float) x2, (float) y2);
  }

  public Line3(double x1, double y1, double x2, double y2)
  {
    super((float) x1, (float) y1, (float) x2, (float) y2);
  }

  public Line3(Line2D line)
  {
    this(line.getX1(), line.getY1(), line.getX2(), line.getY2());
  }

  public Line3(Point2D p1, Point2D p2)
  {
    this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  public Line3 set(Line3 line)
  {
    if (line != null)
      this.setLine(line);
    return this;
  }
  
  public Line3 set(float x1, float y1, float x2, float y2)
  {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    return this;
  }
  
  public Line3 set(double x1, double y1, double x2, double y2)
  {
    this.x1 = (float)x1;
    this.y1 = (float)y1;
    this.x2 = (float)x2;
    this.y2 = (float)y2;
    return this;
  }

  @Override
  public void setExtent(Line3 line)
  {
    this.setLine(line);
  }

  public Line3 move(Point3 p)
  {
    return move(p.x, p.y);
  }

  public Line3 move(float dx, float dy)
  {
    this.x1 += dx;
    this.y1 += dy;
    this.x2 += dx;
    this.y2 += dy;
    return this;
  }

  public Line3 scale(double scale)
  {
    this.x1 *= scale;
    this.y1 *= scale;
    this.x2 *= scale;
    this.y2 *= scale;
    return this;
  }    

  public Line3 trim(double norm)
  {
    double a = atan2();
    return new Line3(x1, y1, x1 + Math.cos(a) * norm, y1 + Math.sin(a) * norm);
  }

  public float absRatio()
  {
    return Math.abs(ratio());
  }

  public float ratio()
  {
    return this.deltaX() / this.deltaY();
  }

  public float[] array()
  {
    return Zen.Array.Floats(x1, y1, x2, y2);
  }

  public double atan2()
  {
    return Math.atan2(deltaY(), deltaX());
  }

  public float x()
  {
    return minX();
  }

  public float y()
  {
    return minY();
  }

  public float w()
  {
    return width();
  }

  public float h()
  {
    return height();
  }
  
  public int intWidth()
  {
    return Math.round(width());
  }

  public int intHeight()
  {
    return Math.round(height());
  }

  public float width()
  {
    return Math.abs(deltaX());
  }

  public float height()
  {
    return Math.abs(deltaY());
  }

  public float dx()
  {
    return deltaX();
  }

  public float dy()
  {
    return deltaY();
  }

  public float deltaX()
  {
    return x2 - x1;
  }

  public float deltaY()
  {
    return y2 - y1;
  }

  public float ux()
  {
    return x1 > x2 ? -1 : 1;
  }

  public float uy()
  {
    return y1 > y2 ? -1 : 1;
  }

  public float cx()
  {
    return centerX();
  }

  public float cy()
  {
    return centerY();
  }

  public float centerX()
  {
    return (x1 + x2) / 2f;
  }

  public float centerY()
  {
    return (y1 + y2) / 2f;
  }

  public Point3 center()
  {
    return new Point3(cx(), cy());
  }

  public Point3 p1()
  {
    return getP1();
  }

  public Point3 p2()
  {
    return getP2();
  }

  @Override
  public Point3 getP1()
  {
    return new Point3(x1, y1);
  }

  @Override
  public Point3 getP2()
  {
    return new Point3(x2, y2);
  }

  public void setP1(float x, float y)
  {
    this.x1 = x;
    this.y1 = y;
  }

  public void setP2(float x, float y)
  {
    this.x2 = x;
    this.y2 = y;
  }

  public void setP1(Point3 p)
  {
    this.x1 = p.x;
    this.y1 = p.y;
  }

  public void setP2(Point3 p)
  {
    this.x2 = p.x;
    this.y2 = p.y;
  }

  public void setX1(float x1)
  {
    this.x1 = x1;
  }

  public void setX2(float x2)
  {
    this.x2 = x2;
  }

  public void setY1(float y1)
  {
    this.y1 = y1;
  }

  public void setY2(float y2)
  {
    this.y2 = y2;
  }

  public void setX1X2(float x1, float x2)
  {
    this.x1 = x1;
    this.x2 = x2;
  }

  public void setY1Y2(float y1, float y2)
  {
    this.y1 = y1;
    this.y2 = y2;
  }

  public void setX1Y2(float x, float y)
  {
    this.x1 = x;
    this.y2 = y;
  }

  public void setX2Y1(float x, float y)
  {
    this.x2 = x;
    this.y1 = y;
  }

  @Override
  public float minX()
  {
    return x1 < x2 ? x1 : x2;
  }

  @Override
  public float minY()
  {
    return y1 < y2 ? y1 : y2;
  }

  @Override
  public float maxX()
  {
    return x1 > x2 ? x1 : x2;
  }

  @Override
  public float maxY()
  {
    return y1 > y2 ? y1 : y2;
  }

  public void setMinX(float x)
  {
    if (x1 < x2)
      x1 = x;
    else
      x2 = x;
  }

  public void setMinY(float y)
  {
    if (y1 < y2)
      y1 = y;
    else
      y2 = y;
  }

  public void setMaxX(float x)
  {
    if (x1 > x2)
      x1 = x;
    else
      x2 = x;
  }

  public void setMaxY(float y)
  {
    if (y1 > y2)
      y1 = y;
    else
      y2 = y;
  }

  public void setBounds(Rectangle3 r)
  {
    this.x1 = r.x;
    this.y1 = r.y;
    this.x2 = r.x + r.width;
    this.y2 = r.y + r.height;
  }

  public void setBounds(double x, double y, double w, double h)
  {
    this.x1 = (float) x;
    this.y1 = (float) y;
    this.x2 = (float) (x + w);
    this.y2 = (float) (y + h);
  }

  public Rectangle3 bounds()
  {
    return new Rectangle3(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2, x1 < x2 ? x2 - x1 : x1 - x2, y1 < y2 ? y2 - y1 : y1 - y2);
  }

  public Line3 shift(Point2D p)
  {
    return shift(p.getX(), p.getY());
  }

  public Line3 shiftBack(Point2D p)
  {
    return shift(-p.getX(), -p.getY());
  }
  
  public Line3 add(Point2D p)
  {
    return shift(p);
  }
  
  public Line3 sub(Point2D p)
  {
    return shiftBack(p);
  }

  public Line3 shift(double dx, double dy)
  {
    return new Line3(x1 + dx, y1 + dy, x2 + dx, y2 + dy);
  }

  public Point3 intersection(Line3 line)
  {
    float dx1 = x1 - x2;
    float dy1 = y1 - y2;
    float dx2 = line.x1 - line.x2;
    float dy2 = line.y1 - line.y2;
    float d = dx1 * dy2 - dy1 * dx2;
    if (Math.abs(d) < 0.00001)
      return null;
    float f1 = x1 * y2 - y1 * x2;
    float f2 = line.x1 * line.y2 - line.y1 * line.x2;
    return new Point3((dx2 * f1 - dx1 * f2) / d, (dy2 * f1 - dy1 * f2) / d);
  }

  @Override
  public Line3 copy()
  {
    return new Line3(x1, y1, x2, y2);
  }

  public FxLine fx()
  {
    return new FxLine(x1, y1, x2, y2);
  }

  public float length2()
  {
    return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
  }

  public float length()
  {
    return (float) Math.sqrt(length2());
  }

  public Arrow arrow(double radius, double size, double width)
  {
    return new Arrow(this, radius, size, width);
  }

  public Point3[] arrowHeadPoints(double size, double width, double alpha, boolean center)
  {
    double angle = atan2() + (alpha < 0.5 ? -Math.PI / 2 : Math.PI / 2);
    Point3 p0 = interpolate(alpha);
    Point3 p1 = new Point3(0, center ? -size / 2 : 0).rotate(angle).add(p0);
    Point3 p2 = new Point3(-width / 2, center ? size / 2 : size).rotate(angle).add(p0);
    Point3 p3 = new Point3(width / 2, center ? size / 2 : size).rotate(angle).add(p0);
    return new Point3[]
    { p1, p2, p3 };
  }

  public Polygon3 arrowHead(double size, double width, double alpha, boolean center)
  {
    return new Polygon3(arrowHeadPoints(size, width, alpha, center));
  }

  public Point3 interpolate(double alpha)
  {
    return new Point3(x1 + (x2 - x1) * alpha, y1 + (y2 - y1) * alpha);
  }

  public Point3 interpolateDist(double dist)
  {
    return interpolate(dist / this.length());
  }

  public void merge(Line3 line, boolean horizontal)
  {
    if (horizontal)
      mergeX(line);
    else
      mergeY(line);
  }

  public void mergeX(Line3 line)
  {
    if (line.minX() < this.minX())
      this.setMinX(line.minX());
    if (line.maxX() > this.maxX())
      this.setMaxX(line.maxX());
  }

  public void mergeY(Line3 line)
  {
    if (line.minY() < this.minY())
      this.setMinY(line.minY());
    if (line.maxY() > this.maxY())
      this.setMaxY(line.maxY());
  }

  public double distanceX(Line3 line)
  {
    if (this.hasOverlapX(line))
      return 0;
    return this.x1 < line.x1 ? line.minX() - this.maxX() : this.minX() - line.maxX();
  }

  public double distanceY(Line3 line)
  {
    if (this.hasOverlapY(line))
      return 0;
    return this.y1 < line.y1 ? line.minY() - this.maxY() : this.minY() - line.maxY();
  }

  public boolean hasOverlapY(Range2D r)
  {
    return this.maxY() >= r.minY() && this.minY() <= r.maxY();
  }

  public boolean hasOverlapX(Range2D r)
  {
    return this.maxX() >= r.minX() && this.minX() <= r.maxX();
  }

  public float[] overlapXs(Range2D r)
  {
    float minA = minX();
    float maxA = maxX();
    float minB = r.minX();
    float maxB = r.maxX();
    float min = Math.max(minA, minB);
    float max = Math.min(maxA, maxB);
    return max <= min ? null : Zen.Array.Floats(min, max);
  }

  public float overlapX(Range2D r)
  {
    return overlapX(r, true);
  }

  public float overlapX(Range2D r, Boolean minNorm)
  {
    float minA = minX();
    float maxA = maxX();
    float minB = r.minX();
    float maxB = r.maxX();
    float min = Math.max(minA, minB);
    float max = Math.min(maxA, maxB);
    // if vertical projections intersect and intersection is bigger than 0.5
    // minimum height, then same line is true
    if (max <= min)
      return 0;
    else if (minNorm == null)
      return max - min;
    else
      return (max - min) / (minNorm ? Math.min(maxA - minA, maxB - minB) : Math.max(maxA - minA, maxB - minB));
  }

  public float[] overlapYs(Range2D r)
  {
    float minA = minY();
    float maxA = maxY();
    float minB = r.minY();
    float maxB = r.maxY();
    float min = Math.max(minA, minB);
    float max = Math.min(maxA, maxB);
    return max <= min ? null : Zen.Array.Floats(min, max);
  }

  public float overlapY(Range2D r)
  {
    return overlapY(r, true);
  }

  public float overlapY(Range2D r, Boolean minNorm)
  {
    float minA = minY();
    float maxA = maxY();
    float minB = r.minY();
    float maxB = r.maxY();
    float min = Math.max(minA, minB);
    float max = Math.min(maxA, maxB);
    // if vertical projections intersect and intersection is bigger than 0.5
    // minimum height, then same line is true
    if (max <= min)
      return 0;
    else if (minNorm == null)
      return max - min;
    else
      return (max - min) / (minNorm ? Math.min(maxA - minA, maxB - minB) : Math.max(maxA - minA, maxB - minB));
  }

  @Override
  public String toString()
  {
    return "Line3[" + x1 + " " + y1 + " " + x2 + " " + y2 + "]";
  }

  public boolean equals(Line2D l, double e)
  {
    return e(l.getX1(), x1, e) && e(l.getX2(), x2, e) && e(l.getY1(), y1, e) && e(l.getY2(), y2, e);
  }

  public boolean equalsDelta(Line3 l, double e)
  {
    return e(l.deltaX(), deltaX(), e) && e(l.deltaY(), deltaY(), e);
  }

  
  public Line3 swapX()
  {
    return new Line3(x2, y1, x1, y2);
  }
  
  
  public Line3 swapY()
  {
    return new Line3(x1, y2, x2, y1);
  }
  
  public Line3 swapXY()
  {
    return new Line3(x2, y2, x1, y1);
  }

  public Line3 vert(double x)
  {
    return new Line3(x, y1, x, y2);
  }

  public Line3 horiz(double y)
  {
    return new Line3(x1, y, x2, y);
  }

  public double radian()
  {
    return Math.atan2(y2 - y1, x2 - x1);
  }

  public double degree()
  {
    return Math.toDegrees(radian());
  }

  public double balance()
  {
    double deg = degree();
    while (deg > 90)
      deg -= 180;
    while (deg < -90)
      deg += 180;
    return deg;
  }

  public boolean isHorizontal(double degreeEpsilon)
  {
    return balance() < degreeEpsilon;
  }

  public boolean isVertical(double degreeEpsilon)
  {
    double bal = balance();
    return Math.abs(bal > 0 ? bal - 90 : bal + 90) < degreeEpsilon;
  }

  public boolean equals(Line3 line, double epsilon)
  {
    return Math3.equals(x1, line.x1, epsilon) && Math3.equals(y1, line.y1, epsilon) && Math3.equals(x2, line.x2, epsilon)
        && Math3.equals(y2, line.y2, epsilon);
  }

  private static boolean e(double a, double b, double epsilon)
  {
    return Math.abs(a - b) <= epsilon;
  }

  public static Line3 vert(double x, double y1, double y2)
  {
    return new Line3(x, y1, x, y2);
  }

  public static Line3 horiz(double x1, double x2, double y)
  {
    return new Line3(x1, y, x2, y);
  }

  public static Line3 New(Point3 p1, Point3 p2)
  {
    return new Line3(p1, p2);
  }

  public static void main(String... args)
  {
    Log.info(Line3.class, ".main - " + Math.atan2(0, 1));
  }

}
