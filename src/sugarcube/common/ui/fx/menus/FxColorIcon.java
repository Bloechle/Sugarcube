package sugarcube.common.ui.fx.menus;

import javafx.scene.Node;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.shapes.FxRect;

public class FxColorIcon implements FxIcon
{
  private Color3 color;
  private double size;
  private double radius = 0;

  public FxColorIcon(Color3 color, double size)
  {
    this.color = color;
    this.size = size;
  }

  public FxColorIcon corner(double radius)
  {
    this.radius = radius;
    return this;
  }

  @Override
  public Node node()
  {
    FxRect r = new FxRect(0, 0, size, size).fill(color);
    if (radius > 0)
    {
      r.setArcWidth(radius);
      r.setArcHeight(radius);
    }
    return r;
  }
}
