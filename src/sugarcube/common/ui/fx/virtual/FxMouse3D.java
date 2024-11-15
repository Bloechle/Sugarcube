package sugarcube.common.ui.fx.virtual;

import javafx.scene.input.MouseEvent;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.ui.fx.transform.FxRotate;

public class FxMouse3D
{
  public Point3 old = new Point3();
  public Point3 pos = new Point3();
  public FxRotate rotateX = FxRotate.X(-20);
  public FxRotate rotateY = FxRotate.Y(-20);
  public boolean isPicking = false;
  public FxVect vecIni = new FxVect(0, 0, 0);
  public FxVect vecPos = new FxVect(0, 0, 0);
  public double distance;

  public FxMouse3D()
  {

  }

  public FxMouse3D unpick()
  {
    if (isPicking)
      isPicking = false;
    return this;
  }

  public FxMouse3D pos(MouseEvent e)
  {
    return pos(e.getSceneX(), e.getSceneY());
  }

  public FxMouse3D pos(double x, double y)
  {
    pos.setLocation(x, y);
    return this;
  }

  public FxMouse3D pos(Point3 p)
  {
    return pos(p.x, p.y);
  }

  public double dx()
  {
    return pos.x - old.x;
  }

  public double dy()
  {
    return pos.y - old.y;
  }

  public FxMouse3D updateOld()
  {
    this.old.setLocation(pos);
    return this;
  }

}
