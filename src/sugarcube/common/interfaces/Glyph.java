package sugarcube.common.interfaces;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Point3;

public interface Glyph
{
    String code();

    String name();

    Path3 path(double fontsize);

    float width();

    float height();

    Point3 vertOrigin();

    default int override()
    {
        return -1;
    }

    default String unicode()
    {
        return code();
    }

    default Path3 path()
    {
        return path(1);
    }
}
