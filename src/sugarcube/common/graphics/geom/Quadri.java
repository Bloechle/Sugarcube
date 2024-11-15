package sugarcube.common.graphics.geom;


import sugarcube.common.ui.fx.shapes.FxQuadri;

import java.awt.geom.Point2D;

public class Quadri extends Polygon3
{
  public Quadri(Point2D... p)
  {
    super(p);
  }
  
  public Point3[] points()
  {
    Point3[] points = new Point3[xpoints.length];
    for(int i=0; i<points.length; i++)
      points[i] = new Point3(xpoints[i], ypoints[i]);
    return points;
  }
  
  
  public FxQuadri fx()
  {
    return new FxQuadri(points());
  }
}
