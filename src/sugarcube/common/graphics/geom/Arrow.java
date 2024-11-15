package sugarcube.common.graphics.geom;

public class Arrow extends Line3
{
  protected double radius;
  protected double size;
  protected double width;
  protected double angle;
  protected double sin;
  protected double cos;
  protected double hx;
  protected double hy;
  protected double cx;
  protected double cy;

  public Arrow(Line3 line, double radius, double size, double width)
  {
    super(line);
    this.radius = radius;
    this.size = size;
    this.width = width;
    double dx = x2 - x1;
    double dy = y2 - y1;
    this.hx = x1 + dx / 2;
    this.hy = y1 + dy / 2;
    this.angle = Math.atan2(dy, dx);
    double ext = Math.sqrt(dx * dx + dy * dy) / 5;
    this.sin = Math.sin(angle);
    this.cos = Math.cos(angle);
    this.cx = hx + sin * ext;
    this.cy = hy + cos * ext;
  }

  public Point3[] points()
  {
    Point3 p1 = new Point3((cx+hx)/2, (cy+hy)/2);
    Point3 p2 = new Point3(-width / 2, size).rotate(angle + Math.PI / 2).add(p1);
    Point3 p3 = new Point3(width / 2, size).rotate(angle + Math.PI / 2).add(p1);
    return new Point3[]
      {
        p1, p2, p3
      };
  }

  public Polygon3 point()
  {
    return new Polygon3(points());
  }

  public Path3 path()
  {
    Path3 path = new Path3();
    path.moveTo(x1 + cos * radius, y1 + sin * radius);
    path.quadTo(cx, cy, x2 - cos * radius, y2 - sin * radius);
    return path;
  }
}
