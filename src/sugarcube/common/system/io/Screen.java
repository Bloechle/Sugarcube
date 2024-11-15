package sugarcube.common.system.io;

import sugarcube.common.graphics.geom.Rectangle3;

import java.awt.*;

public class Screen
{
    public static GraphicsDevice[] screens()
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }

    public static GraphicsDevice screen()
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    public static int width()
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
    }

    public static int height()
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
    }

    public static Rectangle3 bounds()
    {
        return new Rectangle3(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
    }

    public static Rectangle3 bounds(double sw, double sh)
    {
        Rectangle3 bounds = bounds();
        return bounds.inflate(-(int) (0.5 + (1.0 - sw) * bounds.width), -(int) (0.5 + (1.0 - sh) * bounds.height), true);
    }
}
