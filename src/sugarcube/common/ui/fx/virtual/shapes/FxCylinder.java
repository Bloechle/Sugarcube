package sugarcube.common.ui.fx.virtual.shapes;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import sugarcube.common.graphics.geom.IPoint;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.virtual.Fx3D;
import sugarcube.common.ui.fx.virtual.FxPhong;

public class FxCylinder extends Cylinder implements Fx3D.FxNode3D
{
    private static final Point3D Y_AXIS = new Point3D(0, 1, 0);

    public FxCylinder(double radius, double height)
    {
        super(radius, height);
    }

    public FxCylinder(double radius, double height, Color color)
    {
        super(radius, height);
        color(color);
    }

    public FxCylinder(double radius, double height, int divisions)
    {
        super(radius, height, divisions);
    }

    public FxCylinder set(IPoint origin, IPoint target)
    {
        return set(origin.point3D(), target.point3D());
    }

    public FxCylinder set(Point3D origin, Point3D target)
    {
        Point3D delta = target.subtract(origin);
        double height = delta.magnitude();
        Point3D center = target.midpoint(origin);
        Translate moveToCenter = new Translate(center.getX(), center.getY(), center.getZ());
        Point3D axisOfRotation = delta.crossProduct(Y_AXIS);
        double angle = Math.acos(delta.normalize().dotProduct(Y_AXIS));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        this.setHeight(height);
        this.getTransforms().clear();
        this.getTransforms().addAll(moveToCenter, rotateAroundCenter);
        return this;
    }

    public FxCylinder show(boolean doShow)
    {
        this.setVisible(doShow);
        return this;
    }

    public FxCylinder show()
    {
        return show(true);
    }

    public FxCylinder hide()
    {
        return show(false);
    }

    public FxCylinder color(Color color)
    {
        Fx3D.Color(node(), color);
        return this;
    }

    public FxCylinder color(Color3 light)
    {
        color(light.fx());
        return this;
    }

    public FxCylinder phong(Color light, Color dark)
    {
        if (dark == null)
            color(light);
        else
            material(FxPhong.Get(light, dark));
        return this;
    }

    public FxCylinder phong(Color3 light, Color3 dark)
    {
        return phong(light.fx(), dark == null ? null : dark.fx());
    }

    public FxCylinder material(Material mat)
    {
        this.setMaterial(mat);
        return this;
    }

    public FxCylinder height(double h)
    {
        this.setHeight(h);
        return this;
    }

    public FxCylinder translate(IPoint p)
    {
        return setXYZ(p.x(), p.y(), p.z());
    }

    public FxCylinder translate(Point3D p)
    {
        return setXYZ(p.getX(), p.getY(), p.getZ());
    }

//    public FxCylinder translate(MotivePoint p)
//    {
//        return setXYZ(p.x, p.y, p.z);
//    }

    public FxCylinder setXYZ(double x, double y, double z)
    {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setTranslateZ(z);
        return this;
    }

    public FxCylinder rotate(Point3D axis, double degree)
    {
        this.setRotationAxis(axis);
        this.setRotate(degree);
        return this;
    }

    public FxCylinder x(double x)
    {
        this.setTranslateX(x);
        return this;
    }

    public FxCylinder y(double y)
    {
        this.setTranslateY(y);
        return this;
    }

    public FxCylinder z(double z)
    {
        this.setTranslateZ(z);
        return this;
    }

    public double radius()
    {
        return this.getRadius();
    }

    public FxCylinder radius(double radius)
    {
        if (radius() != radius)
            setRadius(radius);
        return this;
    }

    public FxCylinder rotateX(double degree)
    {
        return rotate(Rotate.X_AXIS, degree);
    }

    public FxCylinder rotateY(double degree)
    {
        return rotate(Rotate.Y_AXIS, degree);
    }

    public FxCylinder rotateZ(double degree)
    {
        return rotate(Rotate.Z_AXIS, degree);
    }

    public static FxCylinder Get(double radius, Point3D p0, Point3D p1, int divisions)
    {
        return new FxCylinder(radius, 1, divisions).set(p0, p1);
    }

    public static FxCylinder Get(double radius, Point3D p0, Point3D p1)
    {
        return new FxCylinder(radius, 1).set(p0, p1);
    }

    public static FxCylinder Get(double radius, Point3D p1)
    {
        return new FxCylinder(radius, 1).set(new Point3D(0, 0, 0), p1);
    }

    @Override
    public Shape3D node()
    {
        return this;
    }


}
