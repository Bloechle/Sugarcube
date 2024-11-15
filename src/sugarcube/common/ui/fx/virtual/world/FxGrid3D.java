package sugarcube.common.ui.fx.virtual.world;

import javafx.scene.paint.Color;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.virtual.FxPhong;
import sugarcube.common.ui.fx.virtual.shapes.FxBox;


public class FxGrid3D extends FxGroup
{
    private static FxPhong PHONG = FxPhong.Get(Color.color(0.3, 0.3, 0.3));
    public double y;
    public double radius;
    public double size;
    public double step;

    public FxGrid3D(double y, double radius, double size, double step)
    {
        this.y = y;
        this.radius = radius;
        this.size = size;
        this.step = step;
        update();
    }

    public FxGrid3D updateSize(double size)
    {
        this.size = size;
        return update();
    }

    public FxGrid3D update()
    {
        clear();
        for (double d = -size; d <= size; d += step)
            add(FxBox.Get(radius, d, y, -size, d, y, size).material(PHONG), FxBox.Get(radius, -size, y, d, size, y, d).material(PHONG));
        return this;
    }

}
