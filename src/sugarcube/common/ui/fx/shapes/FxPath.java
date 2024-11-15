package sugarcube.common.ui.fx.shapes;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import sugarcube.common.graphics.Color3;

public class FxPath extends Path
{

  public FxPath fill(Color3 c)
  {
    return fill(c.fx());
  }
  
  public FxPath fill(Paint p)
  {
    this.setFill(p);
    return this;
  }
  
  public FxPath stroke(Color3 c)
  {
    return stroke(c.fx());
  }
  
  public FxPath stroke(Paint p)
  {
    this.setStroke(p);
    return this;
  }
  
  public FxPath opacity(double value)
  {
    this.setOpacity(value);
    return this;
  }
  
  public FxPath mouseTransparent()
  {
    this.setMouseTransparent(true);
    return this;
  }
}
