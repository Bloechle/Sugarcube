package sugarcube.common.ui.fx.shapes;

import javafx.geometry.Point2D;

public class FxPoint extends Point2D
{

  public FxPoint(double x, double y)
  {
    super(x, y);
  }

  public FxCircle circle(double radius)
  {
    return new FxCircle(this.getX(), this.getY(), radius);
  }

}
