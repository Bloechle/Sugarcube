package sugarcube.common.ui.fx.transform;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

public class FxRotate extends Rotate
{
  
  public FxRotate()
  {
    
  }

  public FxRotate pivot(double x, double y, double z)
  {
    this.setPivotX(x);
    this.setPivotY(y);
    this.setPivotZ(z);
    return this;
  }
  
  public double angle()
  {
    return this.getAngle();
  }
  
  public FxRotate angle(double angle)
  {
    this.setAngle(angle);
    return this;
  }
  
  public FxRotate delta(double delta)
  {
    return angle(angle()+delta);
  }
  
  public FxRotate axis(Point3D p)
  {
    this.setAxis(p);
    return this;
  }
  
  public static FxRotate X()
  {
    return new FxRotate().axis(X_AXIS);
  }
  
  public static FxRotate Y()
  {
    return new FxRotate().axis(Y_AXIS);
  }
  
  
  public static FxRotate Z()
  {
    return new FxRotate().axis(Z_AXIS);
  }

  
  public static FxRotate X(double angle)
  {
    return X().angle(angle);
  }
  
  public static FxRotate Y(double angle)
  {
    return Y().angle(angle);
  }
  
  
  public static FxRotate Z(double angle)
  {
    return Z().angle(angle);
  }

}
