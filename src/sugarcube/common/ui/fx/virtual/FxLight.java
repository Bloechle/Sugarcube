package sugarcube.common.ui.fx.virtual;

import javafx.scene.PointLight;
import sugarcube.common.graphics.Color3;

public class FxLight extends PointLight
{
  public FxLight()
  {
  }

  public FxLight pos(double x, double y, double z)
  {
    this.setTranslateX(x);
    this.setTranslateY(y);
    this.setTranslateZ(z);
    return this;
  }
  
  public FxLight color(Color3 color)
  {
    this.setColor(color.fx());
    return this;
  }
}
