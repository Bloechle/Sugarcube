package sugarcube.common.ui.fx.base;

import javafx.scene.transform.Affine;
import sugarcube.common.graphics.geom.Transform3;

public class FxTransform extends Affine
{

  public FxTransform()
  {

  }

  public FxTransform(double sx, double hy, double hx, double sy, double tx, double ty)
  {
    super(sx, hx, tx, hy, sy, ty);
  }

  public FxTransform update(double sx, double hy, double hx, double sy, double tx, double ty)
  {
    super.setToTransform(sx, hx, tx, hy, sy, ty);
    return this;
  }

  public FxTransform update(Transform3 tm)
  {
    return update(tm.sx(), tm.hy(), tm.hx(), tm.sy(), tm.x(), tm.y());
  }
}
