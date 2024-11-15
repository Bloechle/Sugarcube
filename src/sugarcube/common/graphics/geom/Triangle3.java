package sugarcube.common.graphics.geom;

public class Triangle3 extends Polygon3
{
  public Triangle3(Point3 center, double radius, double radian)
  {
    super(equilateralPoints(center, radius, radian));
  }

  public Triangle3(Point3 center, double radius, double radian, boolean rectangleLeft)
  {
    super(rectanglePoints(center, radius, radian, rectangleLeft));
  }

  public static Point3[] equilateralPoints(Point3 center, double radius, double radian)
  {
    Point3 c = center;
    double r = radius;
    double a = radian;
    Point3 p1 = new Point3(Math.cos(a = a + Math.PI / 2) * r + c.x, Math.sin(a) * r + c.y);
    Point3 p2 = new Point3(Math.cos(a = a + 2 * Math.PI / 3) * r + c.x, Math.sin(a) * r + c.y);
    Point3 p3 = new Point3(Math.cos(a = a + 2 * Math.PI / 3) * r + c.x, Math.sin(a) * r + c.y);
    return new Point3[]
      {
        p1, p2, p3
      };
  }

  public static Point3[] rectanglePoints(Point3 center, double radius, double radian, boolean rectangleLeft)
  {
    Point3 c = center;
    double r = radius;
    double a = radian;    
    Point3 p1 = new Point3(Math.cos(a = a + Math.PI / 2) * r + c.x, Math.sin(a) * r + c.y);
    Point3 p2 = new Point3(Math.cos(a = a + 2 * Math.PI / 3) * r + c.x, Math.sin(a) * r + c.y);
    Point3 p3 = new Point3(Math.cos(a = a + 2 * Math.PI / 3) * r + c.x, Math.sin(a) * r + c.y);

    if (rectangleLeft)
      return new Point3[]
        {
          p1, p2, p2.lineTo(p3).center()
        };
    else
      return new Point3[]
        {
          p1, p2.lineTo(p3).center(), p3
        };
  }
}
