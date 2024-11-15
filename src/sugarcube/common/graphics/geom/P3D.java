package sugarcube.common.graphics.geom;

import javafx.geometry.Point3D;

public class P3D implements IPoint
{
    public double x, y, z;

    public P3D()
    {
    }

    public P3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public P3D(IPoint p)
    {
        this.x = p.x();
        this.y = p.y();
        this.z = p.z();
    }

    public void reset()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    @Override
    public double x()
    {
        return x;
    }

    @Override
    public double y()
    {
        return y;
    }

    @Override
    public double z()
    {
        return z;
    }

    public boolean xIsInRange(double min, double max)
    {
        return min < max ? x >= min && x <= max : x >= max && x <= min;
    }

    public boolean yIsInRange(double min, double max)
    {
        return min < max ? x >= min && x <= max : x >= max && x <= min;
    }

    public boolean zIsInRange(double min, double max)
    {
        return min < max ? x >= min && x <= max : x >= max && x <= min;
    }

    public P3D mean(IPoint p)
    {
        x = (x + p.x()) / 2.0;
        y = (y + p.y()) / 2.0;
        z = (z + p.z()) / 2.0;
        return this;
    }

    public P3D add(IPoint p)
    {
        x += p.x();
        y += p.y();
        z += p.z();
        return this;
    }

    public P3D add(double dx, double dy, double dz)
    {
        x += dx;
        y += dy;
        z += dz;
        return this;
    }

    public P3D addAll(IPoint... points)
    {
        for (IPoint p : points)
            add(p);
        return this;
    }

    public P3D subtract(IPoint p)
    {
        x -= p.x();
        y -= p.y();
        z -= p.z();
        return this;
    }

    public P3D subtract(double dx, double dy, double dz)
    {
        x -= dx;
        y -= dy;
        z -= dz;
        return this;
    }

    public P3D subtractAll(IPoint... points)
    {
        for (IPoint p : points)
            subtract(p);
        return this;
    }

    public P3D multiply(double f)
    {
        x *= f;
        y *= f;
        z *= f;
        return this;
    }

    public P3D divide(double f)
    {
        if (f != 0)
        {
            x /= f;
            y /= f;
            z /= f;
        }
        return this;
    }

    public P3D scale(double f)
    {
        x *= f;
        y *= f;
        z *= f;
        return this;
    }

    public void set(IPoint p)
    {
        this.x = p.x();
        this.y = p.y();
        this.z = p.z();
    }

    public void setXYZIf(boolean condition, double x, double y, double z)
    {
        if (condition)
            setXYZ(x, y, z);
    }

    public void setXYZ(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double norm2()
    {
        return x * x + y * y + z * z;
    }

    public double norm()
    {
        return Math.sqrt(norm2());
    }

    public P3D normalize()
    {
        double norm = norm();
        x /= norm;
        y /= norm;
        z /= norm;
        return this;
    }

    public P3D resize(double size)
    {
        double norm = norm() / size;
        x /= norm;
        y /= norm;
        z /= norm;
        return this;
    }

    public double distanceXZ(IPoint p)
    {
        return p == null ? -1 : Math.sqrt(distance2XZ(p));
    }

    public double distance2XZ(IPoint p)
    {
        if (p == null)
            return -1;
        double d = (p.x() - x);
        return d * d + (d = (p.z() - z)) * d;
    }

    public double distance(IPoint p)
    {
        return Math.sqrt(distance2(p));
    }

    public double distance2(IPoint p)
    {
        return distance2(p.x(), p.y(), p.z());
    }

    public double distance2(double x, double y, double z)
    {
        return IPoint.Distance2(this.x - x, this.y - y, this.z - z);
    }

    public Point3 point2D()
    {
        return new Point3(x, z);
    }

    public Point3D point3D(double scale)
    {
        return new Point3D(scale * x, scale * y, scale * z);
    }

    public P3D copy()
    {
        return new P3D(x, y, z);
    }

    public String s(int decimals)
    {
        return string(decimals);
    }

    public String string(int decimals)
    {
        return String.format("(%." + decimals + "f, %." + decimals + "f, %." + decimals + "f)", x, y, z);
    }

    public static P3D Center(IPoint p, IPoint q)
    {
        return new P3D((p.x() + q.x()) / 2, (p.y() + q.y()) / 2, (p.z() + q.z()) / 2);
    }

    public static P3D Mean(IPoint... points)
    {
        return new P3D().addAll(points).divide(points.length);
    }


}
