package sugarcube.common.graphics.geom;

import java.awt.geom.Point2D;

public class Square3 extends Rectangle3 implements Shape3
{
  public Square3()
  {
    super();
  }

  public Square3(Point2D position, double size)
  {
    this(position.getX(), position.getY(), size);
  }

  public Square3(double x, double y, double size)
  {
    super(x, y, size, size);
  }

  public Square3(Point2D position, double size, boolean isCenter)
  {
    this(position.getX(), position.getY(), size, isCenter);
  }

  public Square3(double x, double y, double size, boolean isCenter)
  {
    this(isCenter ? x - size / 2 : x, isCenter ? y - size / 2 : y, isCenter ? 2 * size : size);
  }

  public void setCenter(double x, double y, double radius)
  {
    this.set(x - radius, y - radius, 2 * radius, 2 * radius);
  }
}
