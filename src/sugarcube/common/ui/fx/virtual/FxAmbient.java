package sugarcube.common.ui.fx.virtual;

import javafx.scene.AmbientLight;
import sugarcube.common.graphics.Color3;

public class FxAmbient extends AmbientLight
{
  public FxAmbient()
  {

  }

  public FxAmbient color(Color3 color)
  {
    this.setColor(color.fx());
    return this;
  }

}
