package sugarcube.common.graphics.geom;

import sugarcube.common.data.collections.List3;

public class Points extends List3<Point3>
{
  public Points()
  {

  }

  public Points(Point3[] points)
  {
    this.addAll3(points);
  }
  
  public Points add(double x, double y)
  {
    this.add(new Point3(x,y));
    return this;
  }
  
  public Points add(String name, double x, double y)
  {
    this.add(new NamedPoint(name, x,y));
    return this;
  }
  
  public Points add(String name, Point3 p)
  {
    this.add(new NamedPoint(name,p));
    return this;
  }

  public Point3[] array()
  {
    return this.toArray(new Point3[0]);
  }

}
