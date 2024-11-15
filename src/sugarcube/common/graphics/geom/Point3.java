package sugarcube.common.graphics.geom;


import sugarcube.common.ui.fx.shapes.FxCircle;
import sugarcube.common.ui.fx.shapes.FxPoint;

import java.awt.geom.Point2D;

public class Point3 extends Point2D.Float
{
  public Point3()
  {
    super(0, 0);
  }

  public Point3(double x, double y)
  {
    super((float) x, (float) y);
  }

  public Point3(Point2D p)
  {
    this(p.getX(), p.getY());
  }
  
  public String name()
  {
    return null;
  }

  public Point3 round()
  {
    return new Point3(Math.round(x), Math.round(y));
  }

  public Point3 reverse()
  {
    return new Point3(-x, -y);
  }

  public Point3 reverseX()
  {
    return new Point3(-x, y);
  }

  public Point3 reverseY()
  {
    return new Point3(x, -y);
  }

  public int rx()
  {
    return Math.round(x);
  }

  public int ry()
  {
    return Math.round(y);
  }

  public float x()
  {
    return x;
  }

  public float y()
  {
    return y;
  }

  public void setX(double x)
  {
    this.x = (float) x;
  }

  public void setY(double y)
  {
    this.y = (float) y;
  }

  public FxPoint fx()
  {
    return new FxPoint(x, y);
  }

  public FxCircle fxCircle(double radius)
  {
    return new FxCircle(this.x, this.y, radius);
  }

  public Point3 copy()
  {
    return new Point3(x, y);
  }

  public Point3 multiply(double sx, double sy)
  {
    return new Point3(x * sx, y * sy);
  }

  public Point3 divide(double sx, double sy)
  {
    return new Point3(x / sx, y / sy);
  }
  
  public Point3 divide(double s)
  {
    return new Point3(x / s, y / s);
  }

  public Point3 scale(double sx, double sy)
  {
    return multiply(sx, sy);
  }

  public Point3 unscale(double sx, double sy)
  {
    return divide(sx, sy);
  }

  public Point3 addX(double dx)
  {
    return new Point3(x + dx, y);
  }

  public Point3 addY(double dy)
  {
    return new Point3(x, y + dy);
  }

  public Point3 add(Point2D p)
  {
    return new Point3(x + p.getX(), y + p.getY());
  }

  public Point3 add(double dx, double dy)
  {
    return new Point3(x + dx, y + dy);
  }

  public Point3 sub(Point2D p)
  {
    return this.subtract(p);
  }

  public Point3 sub(double dx, double dy)
  {
    return this.subtract(dx, dy);
  }

  public Point3 subtract(Point2D p)
  {
    return new Point3(x - p.getX(), y - p.getY());
  }

  public Point3 subtract(double dx, double dy)
  {
    return new Point3(x - dx, y - dy);
  }

  public Line3 lineTo(Point3 p)
  {
    return new Line3(this, p);
  }

  public Line3 closestLineTo(Point3... points)
  {
    Line3 line = new Line3(points[0], this);
    double min = Math.abs(points[0].distanceSq(this));
    for (Point3 p : points)
    {
      double d = Math.abs(p.distanceSq(this));
      if (d < min)
      {
        min = d;
        line = new Line3(p, this);
      }
    }
    return line;
  }

  public float norm2()
  {
    return x * x + y * y;
  }

  public double norm()
  {
    return Math.sqrt(x * x + y * y);
  }

  public double atan2()
  {
    return Math.atan2(y, x);
  }

  public double atan2(Point3 p)
  {
    return Math.atan2(p.y - y, p.x - x);
  }

  public Point3 rotate(double radian)
  {
    double a = atan2();
    double r = norm();
    return new Point3(Math.cos(a + radian) * r, Math.sin(a + radian) * r);
  }

  public Point3 transform(Transform3 transform)
  {
    return transform.transform(this);
  }

  public boolean equals(double x, double y, double epsilon)
  {
    return Math.abs(this.x - x) < epsilon && Math.abs(this.y - y) < epsilon;
  }

  public boolean equals(Point3 p, double epsilon)
  {
    return equals(p.x, p.y, epsilon);
  }

  @Override
  public String toString()
  {
    return ("(" + x + "," + y + ")").replace(".0,", ",").replace(".0)", ")");
  }

  public static Point3 Get(double x, double y)
  {
    return new Point3(x, y);
  }

}
