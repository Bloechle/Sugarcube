package sugarcube.common.ui.fx.virtual.shapes;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import sugarcube.common.graphics.geom.IPoint;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.virtual.Fx3D;
import sugarcube.common.ui.fx.virtual.FxPhong;

public class FxBox extends Box implements Fx3D.FxNode3D
{

  public FxBox(double size)
  {
    this(size, size, size);
  }
  
  public FxBox(double width, double height, double depth)
  {
    super(width, height, depth);
  }

  public FxBox set(IPoint origin, IPoint target)
  {
    return set(origin.point3D(), target.point3D());
  }

  public FxBox set(Point3D origin, Point3D target)
  {
    Point3D delta = target.subtract(origin);
    double height = delta.magnitude();
    Point3D center = target.midpoint(origin);
    Translate moveToCenter = new Translate(center.getX(), center.getY(), center.getZ());
    Point3D axisOfRotation = delta.crossProduct(Rotate.Y_AXIS);
    double angle = Math.acos(delta.normalize().dotProduct(Rotate.Y_AXIS));
    Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
    this.setHeight(height);
    this.getTransforms().clear();
    this.getTransforms().addAll(moveToCenter, rotateAroundCenter);
    return this;
  }

  public FxBox show(boolean doShow)
  {
    this.setVisible(doShow);
    return this;
  }

  public FxBox show()
  {
    return show(true);
  }

  public FxBox hide()
  {
    return show(false);
  }

  public FxBox color(Color3 light)
  {
    color(light.fx());
    return this;
  }

  public FxBox phong(Color light, Color dark)
  {
    if (dark == null)
      color(light);
    else
      material(FxPhong.Get(light, dark));
    return this;
  }

  public FxBox phong(Color3 light, Color3 dark)
  {
    return phong(light.fx(), dark == null ? null : dark.fx());
  }

  public FxBox material(Material mat)
  {
    this.setMaterial(mat);
    return this;
  }

  public FxBox phong(Color3 light)
  {
    return phong(light, light.darker().darker());
  }

  public FxBox phongImage(Color3 diff, Image image)
  {
    return material(FxPhong.Get(diff.fx(), image));
  }

  public FxBox phong(Image image)
  {
    return phongImage(Color3.DUST_WHITE, image);
  }
  
  public FxBox resize(double w, double h, double d)
  {
    this.setWidth(w);
    this.setHeight(h);
    this.setDepth(d);
    return this;
  }
  
  public FxBox translate(Point3D p)
  {
    return setXYZ(p.getX(), p.getY(), p.getZ());
  }

//  public FxBox translate(MotivePoint p)
//  {
//    return setXYZ(p.x, p.y, p.z);
//  }

  public FxBox setXYZ(double x, double y, double z)
  {
    this.setTranslateX(x);
    this.setTranslateY(y);
    this.setTranslateZ(z);
    return this;
  }

  public FxBox xyz(double x, double y, double z)
  {
    return setXYZ(x,y,z);
  }

  public FxBox rotate(Point3D axis, double degree)
  {
    this.setRotationAxis(axis);
    this.setRotate(degree);
    return this;
  }

  public FxBox rotateX(double degree)
  {
    return rotate(Rotate.X_AXIS, degree);
  }

  public FxBox rotateY(double degree)
  {
    return rotate(Rotate.Y_AXIS, degree);
  }

  public FxBox rotateZ(double degree)
  {
    return rotate(Rotate.Z_AXIS, degree);
  }

  @Override
  public Shape3D node()
  {
    return this;
  }

  public static FxBox Get(double size, double x0, double y0, double z0, double x1, double y1, double z1)
  {
    return  new FxBox(size, size, size).set(new Point3D(x0, y0, z0), new Point3D(x1,y1,z1));
  }
}
