package sugarcube.common.graphics.geom;

public class NamedPoint extends Point3
{
  public String name;

  public NamedPoint(String name, Point3 p)
  {
    super(p);
    this.name = name;
  }
  
  public NamedPoint(String name, double x, double y)
  {
    super(x,y);
    this.name = name;
  }
  
  
  public String name()
  {
    return name;
  }
}
