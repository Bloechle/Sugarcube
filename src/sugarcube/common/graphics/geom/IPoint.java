package sugarcube.common.graphics.geom;

import javafx.geometry.Point3D;

public interface IPoint
{
    double x();

    double y();

    double z();

    default P3D copyAsP3D()
    {
        return new P3D(x(), y(), z());
    }

    default Point3D point3D()
    {
        return new Point3D(x(), y(), z());
    }

    default double angle(IPoint p1, IPoint p2)
    {
        final double x = x();
        final double y = y();
        final double z = z();
        final double ax = p1.x() - x;
        final double ay = p1.y() - y;
        final double az = p1.z() - z;
        final double bx = p2.x() - x;
        final double by = p2.y() - y;
        final double bz = p2.z() - z;

        final double delta = (ax * bx + ay * by + az * bz) / Math.sqrt(
                (ax * ax + ay * ay + az * az) * (bx * bx + by * by + bz * bz));

        return delta > 1.0 ? 0.0 : (delta < -1.0 ? 180.0 : Math.toDegrees(Math.acos(delta)));
    }

    static double Distance2(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return Distance2(x2 - x1, y2 - y1, z2 - z1);
    }

    static double Distance2(double[] p1, double[] p2)
    {
        return Distance2(p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]);
    }

    static double Distance2(float[] p1, float[] p2)
    {
        return Distance2(p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]);
    }

    static double Distance2(double dx, double dy, double dz)
    {
        return dx * dx + dy * dy + dz * dz;
    }

    static double Distance2(double a, double b)
    {
        return a * a + b * b;
    }

    static double Distance2(IPoint p0, IPoint p1)
    {
        return IPoint.Distance2(p0.x() - p1.x(), p0.y() - p1.y(), p0.z() - p1.z());
    }
}
