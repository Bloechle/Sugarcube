package sugarcube.common.ui.fx.base;

import javafx.scene.image.*;
import sugarcube.common.graphics.Image3;
import sugarcube.common.ui.fx.controls.FxImageView;

public class FxImage extends WritableImage
{

    public FxImage(int width, int height)
    {
        super(width, height);
    }

    public FxImage(double width, double height)
    {
        super((int) Math.round(width), (int) Math.round(height));
    }

    public FxImage(Image3 image)
    {
        this(image.width(), image.height());
        this.update(image);
    }

    public int width()
    {
        return (int) Math.round(getWidth());
    }

    public int height()
    {
        return (int) Math.round(getHeight());
    }

    public void update(Image3 image)
    {
        PixelWriter writer = this.getPixelWriter();
        int h = Math.min(image.height(), height());
        int w = Math.min(image.width(), width());
        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                writer.setArgb(x, y, image.getARGB(x, y));
            }
        }
    }

    public void updateGray(Image image)
    {
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = this.getPixelWriter();
        int h = Math.min((int) Math.round(image.getHeight()), height());
        int w = Math.min((int) Math.round(image.getWidth()), width());
        for (int y = 0; y < h; y++)
        {
            int argb, alpha, red, green, blue, gray;
            for (int x = 0; x < w; x++)
            {
                argb = reader.getArgb(x, y);
                alpha = ((argb >> 24) & 0xff);
                red = ((argb >> 16) & 0xff);
                green = ((argb >> 8) & 0xff);
                blue = (argb & 0xff);
                gray = Math.round(0.2126f * red + 0.7152f * green + 0.0722f * blue);
                if (gray > 255)
                    gray = 255;
                if (gray < 0)
                    gray = 0;
                writer.setArgb(x, y, gray | gray << 8 | gray << 16 | alpha << 24);
            }
        }
    }

    public FxImage magnify(int factor)
    {
        if (factor <= 1)
            return this;

        int width = width();
        int height = height();
        FxImage mag = new FxImage(width * factor, height * factor);
        PixelReader reader = getPixelReader();
        PixelWriter writer = mag.getPixelWriter();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int argb = reader.getArgb(x, y);
                for (int yi = 0; yi < factor; yi++)
                {
                    for (int xi = 0; xi < factor; xi++)
                    {
                        writer.setArgb(factor * x + xi, factor * y + yi, argb);
                    }
                }
            }
        }
        return mag;
    }

    public FxImage setARGB(int[] argb)
    {
        this.getPixelWriter().setPixels(0, 0, width(), height(), WritablePixelFormat.getIntArgbInstance(), argb, 0, width());
        return this;
    }

    public FxImageView view()
    {
        return new FxImageView(this);
    }

}
