package sugarcube.common.ui.fx.virtual;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.virtual.shapes.FxCylinder;


public class FxAxes extends FxGroup
{
    private FxCylinder[] xyz = new FxCylinder[3];

    public FxAxes(double size, double radius)
    {
        this(size, radius, 16);
    }

    public FxAxes(double size, double radius, int divisions)
    {
//        xyz[0] = axis(radius, size, 0, 0, OptiColors.RED, divisions);
//        xyz[1] = axis(radius, 0, size, 0, OptiColors.GREEN, divisions);
//        xyz[2] = axis(radius, 0, 0, size, OptiColors.BLUE, divisions);
//        add(xyz);
    }

    public FxAxes hide()
    {
        super.hide();
        return this;
    }

    private FxCylinder axis(double radius, double x, double y, double z, Color color, int divisions)
    {
        return FxCylinder.Get(radius, Point3D.ZERO, new Point3D(x, y, z), divisions).color(color);
    }

    public FxAxes rotateTranslate(double[] axis, double degrees, double x, double y, double z)
    {
        Point3D p = new Point3D(axis[0], axis[1], axis[2]);
        for (int i = 0; i < xyz.length; i++)
        {
            xyz[i].setRotationAxis(p);
            xyz[i].setRotate(degrees);
        }
        translate(x, y, z);
        return this;
    }
}
