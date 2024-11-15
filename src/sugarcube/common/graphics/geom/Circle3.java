package sugarcube.common.graphics.geom;


import sugarcube.common.ui.fx.shapes.FxCircle;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Circle3 extends Ellipse2D.Double implements Shape3
{

  public Circle3(double ox, double oy, double radius)
  {
    super(ox - radius, oy - radius, 2 * radius, 2 * radius);
  }

  public Circle3(Point2D c, double radius)
  {
    this(c.getX(), c.getY(), radius);
  }

  public void setCX(double ox)
  {
    this.setFrame(ox - (width / 2), y, width, height);
  }

  public void setCY(double oy)
  {
    this.setFrame(x, oy - (height / 2), width, height);
  }

  public void setCXY(double ox, double oy)
  {
    this.setFrame(ox - (width / 2), oy - (height / 2), width, height);
  }
  
  public void set(double ox, double oy, double radius)
  {
    this.setFrame(ox - radius, oy - radius, 2 * radius, 2 * radius);
  }

  public double radius()
  {
    return (this.width + this.height) / 4.0;
  }

  public Point3 center()
  {
    return new Point3(centerX(), centerY());
  }

  public double cx()
  {
    return centerX();
  }

  public double cy()
  {
    return centerY();
  }

  public double centerX()
  {
    return this.x + this.width / 2.0;
  }

  public double centerY()
  {
    return this.y + this.height / 2.0;
  }

  @Override
  public Circle3 copy()
  {
    return new Circle3(centerX(), centerY(), radius());
  }

  public Line3 lineTo(Point3 p)
  {
    double r1 = this.radius();
    Point3 p1 = this.center();
    double a = p1.atan2(p);
    p1 = new Point3(p1.x + Math.cos(a) * r1, p1.y + Math.sin(a) * r1);
    return p1.lineTo(p);
  }

  public Line3 lineTo(Circle3 that)
  {
    double r1 = this.radius();
    double r2 = that.radius();
    Point3 p1 = this.center();
    Point3 p2 = that.center();
    double a = p1.atan2(p2);
    p1 = new Point3(p1.x + Math.cos(a) * r1, p1.y + Math.sin(a) * r1);
    a = Math.PI + a;
    p2 = new Point3(p2.x + Math.cos(a) * r2, p2.y + Math.sin(a) * r2);
    return p1.lineTo(p2);
  }

  @Override
  public void setExtent(Line3 line)
  {
    float ox = line.centerX();
    float oy = line.centerY();
    float radius = Math.min(Math.abs(line.deltaX()), Math.abs(line.deltaY())) / 2f;
    this.set(ox, oy, radius);
  }
  
  public FxCircle fx()
  {
    return new FxCircle(this.cx(), this.cy(), this.radius());
  }
}
