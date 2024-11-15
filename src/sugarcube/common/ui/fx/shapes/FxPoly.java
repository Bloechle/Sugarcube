package sugarcube.common.ui.fx.shapes;

import javafx.scene.shape.Polygon;
import sugarcube.common.graphics.geom.Point3;

public class FxPoly extends Polygon
{
  public FxPoly(Point3... points)
  {
    super();
    for(Point3 p: points)
      this.getPoints().addAll((double)p.x,(double)p.y);
  }
}
