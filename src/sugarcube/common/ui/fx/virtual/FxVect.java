package sugarcube.common.ui.fx.virtual;

import com.sun.javafx.geom.Vec3d;

public class FxVect extends Vec3d
{
  public FxVect()
  {
    super();
  }
  
  public FxVect(double x, double y, double z)
  {
    super(x, y, z);
  }
  
  public FxVect subtract(Vec3d v)
  {
    this.sub(v);
    return this;
  }
  
  public FxVect multiply(double v)
  {
    this.mul(v);
    return this;
  }
}
