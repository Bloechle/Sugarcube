package sugarcube.common.ui.fx.transform;

import com.sun.javafx.geom.Vec3f;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import sugarcube.common.system.log.Log;
import sugarcube.common.ui.fx.virtual.FxVect;

public class FxCamera extends PerspectiveCamera
{
    public final FxVect FORWARD = new FxVect(0, 0, 1);
    public final FxVect UP = new FxVect(0, -1, 0);
    public final FxVect RIGHT = new FxVect(1, 0, 0);

    private final Affine transform = new Affine();
    private final FxVect tm[] = new FxVect[]
            {new FxVect(), new FxVect(), new FxVect(),};

    public final DoubleProperty upX = camDouble(0);
    public final DoubleProperty upY = camDouble(-1);
    public final DoubleProperty upZ = camDouble(0);
    public final DoubleProperty targetX = camDouble(0);
    public final DoubleProperty targetY = camDouble(0);
    public final DoubleProperty targetZ = camDouble(1);
    public final DoubleProperty posX = camDouble(0);
    public final DoubleProperty posY = camDouble(0);
    public final DoubleProperty posZ = camDouble(0);

    public double mouseX = 0;
    public double mouseY = 0;

    private double dxScale = 0.1;
    private double dyScale = 0.1;

    private boolean updateLookup = true;

    public FxCamera()
    {
        super(true);
        // this.setVerticalFieldOfView(true);
        getTransforms().add(transform);
        updateLookup();
    }

    public FxCamera nearFarClips(double near, double far)
    {
        this.setNearClip(near);
        this.setFarClip(far);
        return this;
    }

    public FxCamera pos(Point3D p)
    {
        return pos(p.getX(), p.getY(), p.getZ());
    }

    public FxCamera pos(double x, double y, double z)
    {
        this.updateLookup = false;
        x(x).y(y).z(z);
        this.updateLookup = true;
        this.updateLookup();
        return this;
    }

    public FxCamera target(Point3D p)
    {
        return p == null ? this : target(p.getX(), p.getY(), p.getZ());
    }

//    public FxCamera target(MotivePoint p)
//    {
//        return p == null ? this : target(p.x, p.y, p.z);
//    }

    public FxCamera target(double x, double y, double z)
    {
        this.updateLookup = false;
        targetX(x).targetY(y).targetZ(z);
        this.updateLookup = true;
        this.updateLookup();
        return this;
    }

    public FxCamera up(double x, double y, double z)
    {
        this.updateLookup = false;
        upX(x).upY(y).upZ(z);
        this.updateLookup = true;
        this.updateLookup();
        return this;
    }

    public FxCamera xUp()
    {
        return up(1, 0, 0);
    }

    public FxCamera yUp()
    {
        return up(0, 1, 0);
    }

    public FxCamera zUp()
    {
        return up(0, 0, 1);
    }

    public double x()
    {
        return posX.get();
    }

    public double y()
    {

        return posY.get();
    }

    public double z()
    {
        return posZ.get();
    }

    public double upX()
    {
        return upX.get();
    }

    public double upY()
    {

        return upY.get();
    }

    public double upZ()
    {
        return upZ.get();
    }

    public double targetX()
    {
        return targetX.get();
    }

    public double targetY()
    {

        return targetY.get();
    }

    public double targetZ()
    {
        return targetZ.get();
    }

    public double X()
    {
        return targetX.get();
    }

    public double Y()
    {

        return targetY.get();
    }

    public double Z()
    {
        return targetZ.get();
    }

    public FxCamera x(double x)
    {
        posX.set(x);
        return this;
    }

    public FxCamera y(double y)
    {
        posY.set(y);
        return this;
    }

    public FxCamera z(double z)
    {
        posZ.set(z);
        return this;
    }

    public FxCamera dx(double dx)
    {
        x(x() + dx);
        return this;
    }

    public FxCamera dy(double dy)
    {
        y(y() + dy);
        return this;
    }

    public FxCamera dz(double dz)
    {
        z(z() + dz);
        return this;
    }

    public FxCamera targetX(double x)
    {
        targetX.set(x);
        return this;
    }

    public FxCamera targetY(double y)
    {
        targetY.set(y);
        return this;
    }

    public FxCamera targetZ(double z)
    {
        targetZ.set(z);
        return this;
    }

    public FxCamera upX(double x)
    {
        upX.set(x);
        return this;
    }

    public FxCamera upY(double y)
    {
        upY.set(y);
        return this;
    }

    public FxCamera upZ(double z)
    {
        upZ.set(z);
        return this;
    }

    public double fov()
    {
        return this.getFieldOfView();
    }

    public FxCamera fov(double value)
    {
        this.setFieldOfView(value);
        return this;
    }

    public SimpleDoubleProperty camDouble(double d)
    {
        return new SimpleDoubleProperty(d)
        {
            @Override
            protected void invalidated()
            {
                updateLookup();
            }
        };
    }

    ;

    private void updateLookup()
    {
        if (updateLookup)
        {
            Vec3f pos = new Vec3f((float) x(), (float) y(), (float) z());
            Vec3f target = new Vec3f((float) targetX(), (float) targetY(), (float) targetZ());
            Vec3f up = new Vec3f((float) upX(), (float) upY(), (float) upZ());
            tm[2].sub(target, pos); // z looks to the target
            tm[1].set(-up.x, -up.y, -up.z); // y looks down
            tm[0].cross(tm[1], tm[2]); // x = y ^ z;
            tm[1].cross(tm[2], tm[0]); // y = z ^ x

            for (int i = 0; i != 3; ++i)
            {
                tm[i].normalize();
            }
            // Vec3f pos, Vec3f tm[], Affine tma) {
            transform.setMxx(tm[0].x);
            transform.setMxy(tm[1].x);
            transform.setMxz(tm[2].x);
            transform.setMyx(tm[0].y);
            transform.setMyy(tm[1].y);
            transform.setMyz(tm[2].y);
            transform.setMzx(tm[0].z);
            transform.setMzy(tm[1].z);
            transform.setMzz(tm[2].z);
            transform.setTx(pos.x);
            transform.setTy(pos.y);
            transform.setTz(pos.z);
        }
    }

    public Affine getTransform()
    {
        return transform;
    }

    /*
     * returns 3D direction from the Camera position to the mouse in the Scene
     * space
     */

    public FxVect unProjectDirection(double sceneX, double sceneY, double sWidth, double sHeight)
    {
        FxVect vMouse = null;

        if (isVerticalFieldOfView())
        {
            Log.warn(this, ".unProjectDirection - verticalFOV not yet implemented");
            // TODO: implement for Vfov
        } else
        {
            double tanHFov = Math.tan(Math.toRadians(fov()) * 0.5f);
            vMouse = new FxVect(2 * sceneX / sWidth - 1, 2 * sceneY / sWidth - sHeight / sWidth, 1);
            vMouse.x *= tanHFov;
            vMouse.y *= tanHFov;
        }

        FxVect result = localToSceneDirection(vMouse, new FxVect());
        result.normalize();
        return result;
    }

    public Point3D position()
    {
        return new Point3D(x(), y(), z());
    }

    public Point3D target()
    {
        return new Point3D(targetX(), targetY(), targetZ());
    }

    public FxVect localToScene(FxVect pt, FxVect result)
    {
        Point3D res = localToParentTransformProperty().get().transform(pt.x, pt.y, pt.z);
        if (getParent() != null)
        {
            res = getParent().localToSceneTransformProperty().get().transform(res);
        }
        result.set(res.getX(), res.getY(), res.getZ());
        return result;
    }

    public FxVect localToSceneDirection(FxVect dir, FxVect result)
    {
        localToScene(dir, result);
        result.sub(localToScene(new FxVect(0, 0, 0), new FxVect()));
        return result;
    }

    public FxVect getForward()
    {
        FxVect res = localToSceneDirection(FORWARD, new FxVect());
        res.normalize();
        return res;
    }

    public FxVect getUp()
    {
        FxVect res = localToSceneDirection(UP, new FxVect());
        res.normalize();
        return res;
    }

    public FxVect getRight()
    {
        FxVect res = localToSceneDirection(RIGHT, new FxVect());
        res.normalize();
        return res;
    }

    public FxCamera onScroll(ScrollEvent e)
    {
        double f = e.getDeltaY() > 0 ? 0.9 : 1.1;
        pos((x() - X()) * f + X(), (y() - Y()) * f + Y(), (z() - Z()) * f + Z());
        return this;
    }

    public FxCamera onMouseMoved(MouseEvent e)
    {
        this.mouseX = e.getSceneX();
        this.mouseY = e.getSceneY();
        return this;
    }

    public FxCamera onMouseDragged(MouseEvent e)
    {
        double x = e.getSceneX();
        double y = e.getSceneY();

        double dx = (x - mouseX) * dxScale;
        double dy = (y - mouseY) * dyScale;

        mouseX = x;
        mouseY = y;

        boolean ctrl = e.isControlDown();
        if (ctrl)
            target(FxRotate.Y().pivot(x(), y(), z()).angle(-dx).transform(X(), Y(), Z()));
        else
        {
            pos(FxRotate.Y().pivot(X(), Y(), Z()).angle(-dx).transform(x(), y(), z()));
        }

        if (ctrl)
            targetY(Y() - dy);
        else
            y(y() - dy);

        return this;
    }

    public FxCamera keyEvent(KeyEvent e)
    {
        // Log.debug(this, ".keyEvent - " + e);
        KeyCode k = e.getCode();
        boolean ctrl = e.isControlDown();
        switch (k)
        {
            case ADD:
                this.fov(fov() * 1.01);
                break;
            case SUBTRACT:
                this.fov(fov() * 0.99);
                break;
            default:
                break;
        }
        return this;
    }

    @Override
    public String toString()
    {
        return "camera3D.setPos(" + posX.get() + ", " + posY.get() + ", " + posZ.get() + ");\n" + "camera3D.setTarget(" + targetX.get() + ", "
                + targetY.get() + ", " + targetZ.get() + ");\n" + "camera3D.setUp(" + upX.get() + ", " + upY.get() + ", " + upZ.get() + ");";
    }
}
