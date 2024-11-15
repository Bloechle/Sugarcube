package sugarcube.common.graphics;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.gui.FileChooser3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.formats.ocd.objects.ImageThumberCache;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.HashSet;
import java.util.Hashtable;

public class Image3 extends BufferedImage
{
    private static final int[] X_INTERPOL =
            {0, 1, 0, 1};
    private static final int[] Y_INTERPOL =
            {0, 0, 1, 1};
    public static final String KEY_TRANSFORM = "ImageTransform";
    public static final String KEY_BOUNDS = "ImageBounds";
    public static final String KEY_FILEPATH = "FilePath";
    protected StringMap<Object> properties = new StringMap<>();
    private long timestamp = 0;

    public enum Type
    {
        BINARY(TYPE_BYTE_BINARY),
        GRAY(TYPE_BYTE_GRAY),
        RGB(TYPE_INT_RGB),
        ARGB(TYPE_INT_ARGB),
        INDEXED(TYPE_BYTE_INDEXED);
        public final int value;

        private Type(int value)
        {
            this.value = value;
        }

        public static Type get(int value)
        {
            for (Type type : Type.values())
                if (type.value == value)
                    return type;
            return RGB;
        }
    }

    public Image3()
    {
        super(256, 256, TYPE_INT_RGB);
    }

    public Image3(double width, double height)
    {
        this((int) width, (int) height);
    }

    public Image3(double width, double height, boolean hasAlpha)
    {
        this((int) width, (int) height, hasAlpha);
    }

    public Image3(int width, int height)
    {
        this(width, height, TYPE_INT_RGB);
    }

    public Image3(int width, int height, boolean hasAlpha)
    {
        this(width, height, hasAlpha ? TYPE_INT_ARGB : TYPE_INT_RGB);
    }

    public Image3(int width, int height, int type)
    {
        super(width, height, type);
    }

    public Image3(int width, int height, Type type)
    {
        super(width, height, type.value);
    }

    public Image3(int width, int height, Color color)
    {
        this(width, height, TYPE_INT_RGB, color);
    }

    public Image3(int width, int height, int type, Color color)
    {
        this(width, height, type);
        Graphics3 g = this.graphics();
        g.clear(color);
        g.dispose();
    }

    public Image3(BufferedImage image)
    {
        super(image.getColorModel(), image.getRaster(), image.isAlphaPremultiplied(), Props(image));
    }

    public Image3(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties)
    {
        super(cm, raster, isRasterPremultiplied, properties);
    }

    public Image3(BufferedImage image, double alpha)
    {
        this(image.getWidth(), image.getHeight(), true);
        Graphics3 g = this.graphics();
        // Log.debug(this, " alpha="+alpha+": "+this);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
        g.draw(image, null);
        g.dispose();
    }

    public Image3(Image image)
    {
        this(image.getWidth(null), image.getHeight(null), Image3.Type.ARGB);
        Graphics3 g = this.graphics();
        g.context().drawImage(image, 0, 0, null);
        g.dispose();
    }

    public Image3 timestamp(long timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }

    public long timestamp()
    {
        return this.timestamp;
    }

    public int nbOfComponents()
    {
        return this.getSampleModel().getNumBands();
    }

    public Image3 clear(Color3 color)
    {
        Graphics3 g = this.graphics();
        g.clear(color);
        g.dispose();
        return this;
    }

    public Image3 clear()
    {
        if (getType() == Image3.TYPE_INT_ARGB)
        {
            for (int y = 0; y < height(); y++)
                for (int x = 0; x < width(); x++)
                    this.setRGB(x, y, 0);
        } else
            this.clear(Color3.BLACK);
        return this;
    }

    public Image3 clearBlack()
    {
        this.clear(Color3.BLACK);
        return this;
    }

    public Image3 clearWhite()
    {
        this.clear(Color3.WHITE);
        return this;
    }

    public Image3 whiteMargins(int margin)
    {
        return this.whiteMargins(margin, margin);
    }

    public Image3 whiteMargins(int topBottom, int leftRight)
    {
        return whiteMargins(topBottom, leftRight, topBottom, leftRight);
    }

    public Image3 whiteMargins(int top, int right, int bottom, int left)
    {
        Graphics3 g = this.graphics();
        g.rect(0, 0, left, height(), Color3.white);
        g.rect(width() - right, 0, right, height(), Color3.white);
        g.rect(0, 0, width(), top, Color3.white);
        g.rect(0, height() - bottom, width(), bottom, Color3.white);
        g.dispose();
        return this;
    }

    public void setPropBounds(Rectangle3 bounds)
    {
        this.setProperty(KEY_BOUNDS, bounds);
    }

    public void setPropTransform(Transform3 transform)
    {
        this.setProperty(KEY_TRANSFORM, transform);
    }

    public Rectangle3 propBounds()
    {
        return (Rectangle3) this.prop(KEY_BOUNDS, new Rectangle3(0, 0, width(), height()));
    }

    public Transform3 propTransform()
    {
        return (Transform3) this.prop(KEY_TRANSFORM, new Transform3());
    }

    public boolean has(int x, int y)
    {
        return x > -1 && y > -1 && x < width() && y < height();
    }

    public void setBlackPixel(int x, int y)
    {
        this.setPixel(x, y, Color3.BLACK);
    }

    public void setWhitePixel(int x, int y)
    {
        this.setPixel(x, y, Color3.WHITE);
    }

    public int getGray(double x, double y)
    {
        // bilinear interpolation
        int tx = (int) x;
        int ty = (int) y;
        float dx = x < 0 ? 1 - tx + (float) x : (float) x - tx;
        float dy = y < 0 ? 1 - ty + (float) y : (float) y - ty;

        float dx_ = 1 - dx;
        float dy_ = 1 - dy;
        int w = width();
        int h = height();
        float gray = 0;
        float f;
        int nx;
        int ny;

        for (int i = 0; i < 4; i++)
        {
            nx = tx + X_INTERPOL[i];
            ny = ty + Y_INTERPOL[i];
            f = (X_INTERPOL[i] == 0 ? dx_ : dx) * (Y_INTERPOL[i] == 0 ? dy_ : dy);
            if (nx >= 0 && nx < w && ny >= 0 && ny < h)
                gray += getValue(nx, ny, 0) * f;
        }

        return gray < 0 ? 0 : gray > 255 ? 255 : (int) (0.5 + gray);
    }

    public int getRGB(double x, double y)
    {
        // bilinear interpolation
        int tx = (int) x;
        int ty = (int) y;
        float dx = x < 0 ? 1 - tx + (float) x : (float) x - tx;
        float dy = y < 0 ? 1 - ty + (float) y : (float) y - ty;

        float dx_ = 1 - dx;
        float dy_ = 1 - dy;
        int w = width();
        int h = height();
        float[] rgb = new float[3];
        float f;
        int nx;
        int ny;

        for (int i = 0; i < 4; i++)
        {
            nx = tx + X_INTERPOL[i];
            ny = ty + Y_INTERPOL[i];
            if (nx >= 0 && nx < w && ny >= 0 && ny < h)
            {
                f = (X_INTERPOL[i] == 0 ? dx_ : dx) * (Y_INTERPOL[i] == 0 ? dy_ : dy);
                int v = getRGB(nx, ny);
                rgb[0] += ((v >> 16) & 0xff) * f;
                rgb[1] += ((v >> 8) & 0xff) * f;
                rgb[2] += (v & 0xff) * f;
            }
        }

        return (rgb[0] < 0 ? 0 : rgb[0] > 255 ? 255 : (int) (0.5 + rgb[0])) << 16 | (rgb[1] < 0 ? 0 : rgb[1] > 255 ? 255 : (int) (0.5 + rgb[1])) << 8
                | (rgb[2] < 0 ? 0 : rgb[2] > 255 ? 255 : (int) (0.5 + rgb[2]));
    }

    public int getARGB(double x, double y)
    {
        // bilinear interpolation
        int tx = (int) x;
        int ty = (int) y;
        float dx = x < 0 ? 1 - tx + (float) x : (float) x - tx;
        float dy = y < 0 ? 1 - ty + (float) y : (float) y - ty;

        float dx_ = 1 - dx;
        float dy_ = 1 - dy;
        int w = width();
        int h = height();
        float[] rgba = new float[4];
        float f;
        int nx;
        int ny;

        for (int i = 0; i < 4; i++)
        {
            nx = tx + X_INTERPOL[i];
            ny = ty + Y_INTERPOL[i];
            if (nx >= 0 && nx < w && ny >= 0 && ny < h)
            {
                f = (X_INTERPOL[i] == 0 ? dx_ : dx) * (Y_INTERPOL[i] == 0 ? dy_ : dy);
                int v = getARGB(nx, ny);
                rgba[0] += ((v >> 16) & 0xff) * f;
                rgba[1] += ((v >> 8) & 0xff) * f;
                rgba[2] += (v & 0xff) * f;
                rgba[3] += ((v >> 24) & 0xff) * f;
            }
        }

        return (rgba[3] < 0 ? 0 : rgba[3] > 255 ? 255 : (int) (0.5 + rgba[3])) << 24
                | (rgba[0] < 0 ? 0 : rgba[0] > 255 ? 255 : (int) (0.5 + rgba[0])) << 16 | (rgba[1] < 0 ? 0 : rgba[1] > 255 ? 255 : (int) (0.5 + rgba[1])) << 8
                | (rgba[2] < 0 ? 0 : rgba[2] > 255 ? 255 : (int) (0.5 + rgba[2]));
    }

    public int luminosity(double x, double y)
    {
        switch (getType())
        {
            case TYPE_BYTE_GRAY:
                return getGray(x, y);
            default:
                return Math.round(Color3.luminosity(getRGB(x, y)));
        }
    }

    public int luminosity(int x, int y)
    {
        switch (getType())
        {
            case TYPE_BYTE_GRAY:
                return getRaster().getSample(x, y, 0);
            default:
                return Math.round(Color3.luminosity(getRGB(x, y)) * alpha(x, y) / 255f);
        }
    }

    public int alphaLuminosity(int x, int y)
    {
        switch (getType())
        {
            case TYPE_BYTE_GRAY:
                return getRaster().getSample(x, y, 0);
            default:
                return Math.max(Color3.luminosity(this.getRGB(x, y)), 255 - this.alpha(x, y));
        }
    }

    public int[][] intLuminosities()
    {
        return intLuminosities(0, 0, this.width(), this.height());
    }

    public int[][] intLuminosities(int x0, int y0, int w, int h)
    {
        x0 = x0 < 0 ? 0 : x0;
        y0 = y0 < 0 ? 0 : y0;
        int x1 = x0 + w - 1;
        int y1 = y0 + h - 1;
        x1 = x1 < width() ? x1 : width() - 1;
        y1 = y1 < height() ? y1 : height() - 1;
        w = x1 - x0 + 1;
        h = y1 - y0 + 1;
        int[][] m = new int[h][w];
        switch (getType())
        {
            case TYPE_BYTE_GRAY:
                WritableRaster raster = this.getRaster();
                for (int y = y0; y <= y1; y++)
                    for (int x = x0; x <= x1; x++)
                        m[y - y0][x - x0] = raster.getSample(x, y, 0);
                break;
            default:
                for (int y = y0; y <= y1; y++)
                    for (int x = x0; x <= x1; x++)
                        m[y - y0][x - x0] = Color3.luminosity(this.getRGB(x, y));
                break;
        }
        return m;
    }

    public float[][] floatLuminosities()
    {
        return floatLuminosities(0, 0, this.width(), this.height());
    }

    public float[][] floatLuminosities(int x0, int y0, int w, int h)
    {
        x0 = x0 < 0 ? 0 : x0;
        y0 = y0 < 0 ? 0 : y0;
        int x1 = x0 + w - 1;
        int y1 = y0 + h - 1;
        x1 = x1 < width() ? x1 : width() - 1;
        y1 = y1 < height() ? y1 : height() - 1;
        w = x1 - x0 + 1;
        h = y1 - y0 + 1;
        float[][] m = new float[h][w];
        switch (getType())
        {
            case TYPE_BYTE_GRAY:
                WritableRaster raster = this.getRaster();
                for (int y = y0; y <= y1; y++)
                    for (int x = x0; x <= x1; x++)
                        m[y - y0][x - x0] = raster.getSample(x, y, 0) / 255f;
                break;
            default:
                for (int y = y0; y <= y1; y++)
                    for (int x = x0; x <= x1; x++)
                        m[y - y0][x - x0] = Color3.luminosity(this.getRGB(x, y));
                break;
        }
        return m;
    }

    public byte[][] byteLuminosities()
    {
        return byteLuminosities(0, 0, this.width(), this.height());
    }

    public byte[][] byteLuminosities(int x0, int y0, int w, int h)
    {
        x0 = x0 < 0 ? 0 : x0;
        y0 = y0 < 0 ? 0 : y0;
        int x1 = x0 + w - 1;
        int y1 = y0 + h - 1;
        x1 = x1 < width() ? x1 : width() - 1;
        y1 = y1 < height() ? y1 : height() - 1;
        w = x1 - x0 + 1;
        h = y1 - y0 + 1;
        byte[][] m = new byte[h][w];
        switch (getType())
        {
            case TYPE_BYTE_GRAY:
                WritableRaster raster = this.getRaster();
                for (int y = y0; y <= y1; y++)
                    for (int x = x0; x <= x1; x++)
                        m[y - y0][x - x0] = (byte) (raster.getSample(x, y, 0) - 128);
                break;
            default:
                for (int y = y0; y <= y1; y++)
                    for (int x = x0; x <= x1; x++)
                        m[y - y0][x - x0] = (byte) (Color3.luminosity(this.getRGB(x, y)) - 128);
                break;
        }
        return m;
    }

    public int getValue(int x, int y, int component)
    {
        return this.getRaster().getSample(x, y, component);
    }

    public int getARGB(int x, int y)
    {
        return hasTransparency() ? getRGB(x, y) & 0x00ffffff | (this.getRaster().getSample(x, y, 3) & 0xff) << 24 : getRGB(x, y);
    }

    public int getRGBA(int x, int y)
    {
        int argb = getARGB(x, y);
        return (argb << 8) & 0xffffff00 | (argb >> 24) & 0x000000ff;
    }

    public Image3 alpha(double alpha)
    {
        return new Image3(this, alpha);
    }

    public int alpha(int x, int y)
    {
        return this.hasTransparency() ? this.getRaster().getSample(x, y, 3) : 255;
    }

    public Color3 getPixel(int x, int y)
    {
        return hasTransparency() ? new Color3(getRGBA(x, y), true) : new Color3(this.getRGB(x, y), false);
    }

    public int[] values(int x, int y, int[] values)
    {
        return this.getRaster().getPixel(x, y, values == null ? new int[this.nbOfComponents()] : values);
    }

    public int[] rgba(int x, int y, int[] rgba)
    {
        if (rgba == null)
            rgba = new int[3];
        int argb = this.getRGB(x, y);
        rgba[0] = (argb >> 16) & 0xff;
        rgba[1] = (argb >> 8) & 0xff;
        rgba[2] = (argb & 0xff);
        if (rgba.length > 3)
            rgba[3] = (argb >> 24) & 0xff;
        return rgba;
    }

    public Image3 setRaster(int b, byte[][] data, boolean castIsTrue_falseIsShift)
    {
        WritableRaster raster = this.getRaster();
        if (castIsTrue_falseIsShift)
        {
            for (int y = 0; y < this.height() && y < data.length; y++)
                for (int x = 0; x < this.width() && x < data[0].length; x++)
                {
                    raster.setSample(x, y, b, data[y][x] & 0xff);
                }
        } else
        {
            for (int y = 0; y < this.height() && y < data.length; y++)
                for (int x = 0; x < this.width() && x < data[0].length; x++)
                {
                    raster.setSample(x, y, b, data[y][x] + 128);
                }
        }
        return this;
    }

    public void setComponents(int x, int y, int... c)
    {
        this.getRaster().setPixel(x, y, c);
    }

    public void setComponents(boolean checkBounds, int x, int y, int... c)
    {
        if (checkBounds && (x < 0 || y < 0 || x >= this.width() || y >= this.height()))
            return;
        setComponents(x, y, c);
    }

    public void setCMYK(int x, int y, float... cmyk)
    {
        int l = cmyk.length;
        float k_ = l > 3 ? 1 - cmyk[3] : 1;
        this.setPixel(x, y, (1 - (l > 0 ? cmyk[0] : 0)) * k_, (1 - (l > 1 ? cmyk[1] : 0)) * k_, (1 - (l > 2 ? cmyk[2] : 0)) * k_, l > 4 ? cmyk[4] : 1);
    }

    public void setRGB(int x, int y, float... rgba)
    {
        this.setPixel(x, y, rgba);
    }

    public void setPixel(int x, int y, float... rgba)
    {
        this.setRGB(x, y, new Color3(rgba).getRGB());
    }

    public void setPixel(int x, int y, int[] rgba)
    {
        int value = ((rgba.length < 4 ? 255 : rgba[3] & 0xFF) << 24) | ((rgba[0] & 0xFF) << 16) | ((rgba.length < 3 ? rgba[0] : rgba[1] & 0xFF) << 8)
                | ((rgba.length < 3 ? rgba[0] : rgba[2] & 0xFF) << 0);
        this.setPixel(x, y, value);
    }

    public void setPixel(int x, int y, int argb)
    {
        this.setRGB(x, y, argb);
    }

    public void setPixel(int x, int y, Color color)
    {
        this.setRGB(x, y, color.getRGB());
    }

    public void setAlpha(int x, int y, int alpha)
    {
        this.setRGB(x, y, getARGB(x, y) & 0x00ffffff | (alpha << 24));
    }

    public void multiplyAlpha(int x, int y, int alpha)
    {
        this.setAlpha(x, y, (int) (alpha * alpha(x, y) / 255f));
    }

    public void composeAlpha(int x, int y, int alpha)
    {
        this.setAlpha(x, y, (int) (((255 - alpha) * alpha(x, y) / 255f + alpha) / 255f));
    }

    public Dimension dimension()
    {
        return new Dimension(width(), height());
    }

    public String widthHeight()
    {
        return width() + "x" + height();
    }

    public int width()
    {
        return this.getWidth();
    }

    public int height()
    {
        return this.getHeight();
    }

    public static Image3 fromHexaString(String data)
    {
        data = data.trim();
        if (!data.isEmpty())
            try
            {

                byte[] bytes = new byte[data.length() / 2];
                for (int i = 0; i < bytes.length; i++)
                    bytes[i] = (byte) (0xff & Integer.parseInt(data.charAt(2 * i) + "" + data.charAt(2 * i + 1), 16));
                ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                BufferedImage image = ImageIO.read(stream);
                if (image == null)
                    Log.warn(Image3.class, ".instance - string parsing exception");
                else
                    return new Image3(image);
            } catch (Exception e)
            {
                Log.warn(Image3.class, ".instance - string parsing exception: " + e);
            }
        return null;
    }

    public Image3 softClip(Shape clip)
    {
        Image3 translucent = new Image3(this.width(), this.height(), true);
        Graphics3 g = translucent.graphics();
        g.graphics().setComposite(AlphaComposite.Clear);
        g.graphics().fillRect(0, 0, this.width(), this.height());// all pixels have
        // zero alpha
        g.graphics().setComposite(AlphaComposite.Src);
        g.graphics().setColor(Color.WHITE);
        g.graphics().fill(clip);
        // We use SrcAtop, which effectively uses the alpha value as a coverage
        // value for each pixel stored in the
        // destination. For the areas outside our clip shape, the destination alpha
        // will be zero, so nothing is rendered in those areas. For
        // the areas inside our clip shape, the destination alpha will be fully
        // opaque, so the full color is rendered. At the edges, the original
        // antialiasing is carried over to give us the desired soft clipping effect.
        g.graphics().setComposite(AlphaComposite.SrcAtop);
        g.graphics().drawImage(this, null, null);
        g.dispose();
        return translucent;
    }

    public File3 file()
    {
        String file = prop(KEY_FILEPATH, "");
        return Str.IsVoid(file) ? null : File3.Get(file);
    }

    @Override
    public Object getProperty(String name)
    {
        return properties.has(name) ? properties.get(name) : super.getProperty(name);
    }

    public String prop(String name)
    {
        return prop(name, "");
    }

    public Object prop(String name, Object def)
    {
        Object value = getProperty(name);
        return value == null ? def : value;
    }

    public String prop(String name, String def)
    {
        Object value = getProperty(name);
        return value == null ? def : value.toString();
    }

    public Object setProperty(String name, Object value)
    {
        return this.properties.put(name, value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        Image3 xi = (Image3) o;
        if (this.getWidth() != xi.getWidth() || this.getHeight() != xi.getHeight())
            return false;
        for (int y = 0; y < this.getHeight(); y++)
            for (int x = 0; x < this.getWidth(); x++)
                if (this.getRGB(x, y) != xi.getRGB(x, y))
                    return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        int w = getWidth();
        int h = getHeight();
        int result = 1;
        result = 31 * result + w;
        result = 31 * result + h;
        int dw = w / 13 + 1;
        int dh = h / 13 + 1;
        for (int y = 0; y < h; y += dh)
            for (int x = 0; x < w; x += dw)
                result = 31 * result + this.getRGB(x, y);
        return result;
    }

    public Image3 scale(double ratio)
    {
        int w = Math.max((int) (width() * ratio), 1);
        int h = Math.max((int) (height() * ratio), 1);
        int type = this.getType();
        if (type == 0)
        {
            Log.debug(this, ".scale - image type is 0");
        }
        Image3 scaled = new Image3(w < 1 ? 1 : w, h < 1 ? 1 : h, type == 0 ? BufferedImage.TYPE_INT_ARGB : type);
        Graphics3 g = scaled.graphics();
        g.draw(this, new Transform3(ratio, 0, 0, ratio, 0, 0));
        g.dispose();
        return scaled;
    }

    public Image3 scale(double ratio, Boolean bicubic)
    {
        int w = Math.max((int) (width() * ratio), 1);
        int h = Math.max((int) (height() * ratio), 1);
        int type = getType();
        Image3 scaled = new Image3(w < 1 ? 1 : w, h < 1 ? 1 : h, type == 0 ? BufferedImage.TYPE_INT_ARGB : type);
        Graphics3 g = scaled.graphics();
        g.setInterpolation(bicubic);
        g.draw(this, new Transform3(ratio, 0, 0, ratio, 0, 0));
        g.dispose();
        return scaled;
    }

    public Image3 scaleToFit(int maxWidth, int maxHeight)
    {
        double ratio = Math.min(maxWidth / (double) width(), maxHeight / (double) height());
        return ratio < 1 ? scale(ratio) : this;
    }

    public Image3 compose(Image3 image, double mix, boolean hasAlpha)
    {
        return compose(image, mix, hasAlpha, this);
    }

    public Image3 compose(Image3 image, double mix, boolean hasAlpha, Image3 out)
    {
        int w = Math.min(width(), image.width());
        int h = Math.min(height(), image.height());
        int[] rgb1 = new int[hasAlpha ? 4 : 3];
        int[] rgb2 = new int[rgb1.length];
        double mux = 1.0 - mix;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                rgba(x, y, rgb1);
                image.rgba(x, y, rgb2);
                for (int i = 0; i < rgb1.length; i++)
                    rgb2[i] = (int) (0.5 + mux * rgb1[i] + mix * rgb2[i]);
                out.setRGB(x, y, rgb2[2] | rgb2[1] << 8 | rgb2[0] << 16 | (rgb2.length < 4 ? 255 : rgb2[3]) << 24);
            }
        return this;
    }

    public Graphics3 graphics()
    {
        return new Graphics3(this.createGraphics(), this.getWidth(), this.getHeight());
    }

    public javafx.scene.image.Image fx()
    {
        return Fx.toFXImage(this);
    }

    public boolean isPaletted(int maxColors)
    {
        HashSet<Integer> colors = new HashSet<Integer>();
        for (int y = 0; y < this.getHeight(); y++)
            for (int x = 0; x < this.getWidth(); x++)
            {
                colors.add(this.getRGB(x, y));
                if (colors.size() > maxColors)
                    return false;
            }
        return true;
    }

    public boolean isOpaque()
    {
        return getTransparency() == OPAQUE;
    }

    public boolean hasTransparency()
    {
        return getTransparency() != OPAQUE;
    }

    public boolean isFullyTransparent()
    {
        return isFullyTransparent(this);
    }

    public boolean isFullyOpaque()
    {
        return isFullyOpaque(this);
    }

    public boolean isMonochrome()
    {
        return isMonochrome(this);
    }

    public static boolean isFullyTransparent(BufferedImage image)
    {
        if (image.getTransparency() != OPAQUE)
        {
            int w = image.getWidth();
            int h = image.getHeight();
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                    if ((image.getRGB(x, y) >> 24 & 0xff) > 0)
                        return false;
            return true;
        }
        return false;
    }

    public static boolean isFullyOpaque(BufferedImage image)
    {
        if (image.getTransparency() != OPAQUE)
        {
            int w = image.getWidth();
            int h = image.getHeight();
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                    if ((image.getRGB(x, y) >> 24 & 0xff) < 255)
                        return false;
            return true;
        }
        return true;
    }

    public static boolean isMonochrome(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        int rgb = w > 0 && h > 0 ? image.getRGB(0, 0) : 0;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                if (image.getRGB(x, y) != rgb)
                    return false;
            }
        return true;
    }

    public String format()
    {
        return hasTransparency() || isPaletted(256) ? "png" : "jpg"; // "jpeg 2000";
    }



    public PNGImage PNGImage()
    {
        return PNGImage.create(this);
    }

    public void write(String path)
    {
        this.write(new File(path));
    }

    public void writePng(String path)
    {
        this.write(new File3(path).extense(".png"), "png");
    }

    public void writeJpg(String path)
    {
        this.write(new File3(path).extense(".jpg"), "jpg");
    }

    public void writeTiff(String path)
    {
        this.write(new File3(path).extense(".tif"), "tiff");
    }

    public void write(String path, String type)
    {
        this.write(new File(path), type);
    }

    public void write(File file)
    {
        String name = file.getName().toLowerCase();
        write(file, name.endsWith(".jpg") || name.endsWith(".jpeg") ? "jpg" : "png");
    }

    public void writePng(File file)
    {
        this.write(new File3(file).extense(".png"), "png");
    }

    public void writeJpg(File file)
    {
        this.write(new File3(file).extense(".jpg"), "jpg");
    }

    public void writeTiff(File file)
    {
        this.write(new File3(file).extense(".tif"), "tiff");
    }

    public void write(File file, String type)
    {
        type = type == null || type.isEmpty() ? "png" : type.startsWith(".") ? type.substring(1) : type;
        try
        {
            ImageIO.write(this, type, File3.NeedDirs(file));
        } catch (IOException ex)
        {
            Log.warn(this, ".write - write error: " + ex);
        }
    }

    public Image3 write(File file, double quality)
    {
        try
        {
            FileOutputStream fs = new FileOutputStream(File3.NeedDirs(file));
            write(fs, quality);
            fs.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public Image3 write(OutputStream stream, double quality)
    {
        try
        {
            if (quality <= 0.01 || quality > 1.01)
            {
                ImageIO.write(this, "png", stream);
            } else
                try
                {
                    ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                    JPEGImageWriteParam params = (JPEGImageWriteParam) writer.getDefaultWriteParam();
                    params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    // an integer between 0 and 1, 1 specifies minimum compression and
                    // maximum quality
                    params.setCompressionQuality(quality > 1.0 ? 1f : (float) quality);
                    writer.setOutput(ImageIO.createImageOutputStream(stream));
                    writer.write(null, new IIOImage(this, null, null), params);
                    writer.dispose();
                } catch (Exception e)
                {
                    Log.debug(this, ".write - " + e.getMessage());
                    ImageIO.write(this, "jpg", stream);
                }
        } catch (Exception e)
        {
            Log.warn(this, ".write - exception thrown: " + e.getMessage());
            e.printStackTrace();
        }
        return this;
    }

    public Image3 convolve(Kernel kernel)
    {
        Image3 img = new Image3(width(), height(), getType());
        new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null).filter(this, img);
        return img;
    }

    public byte[] write()
    {
        return write(-1);
    }

    public byte[] write(double quality)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(1000, width() * height() / 4));
        write(out, quality);
        return out.toByteArray();
    }

    // public void writeJPEG(String path, float quality)
    // {
    // this.writeJPEG(new File(path), quality);
    // }
    // public void writeJPEG(File file, float quality)
    // {
    // BufferedOutputStream out;
    // try
    // {
    // file.mkdirs();
    // out = new BufferedOutputStream(new FileOutputStream(file));
    // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    // JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(this);
    // param.setQuality(quality < 0 ? 0f : quality > 1 ? 1f : quality, false);
    // encoder.setJPEGEncodeParam(param);
    // encoder.encode(this);
    // out.close();
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    // }
    public Type type()
    {
        return Type.get(this.getType());
    }

    public String stringType()
    {
        switch (this.getType())
        {
            case Image3.TYPE_BYTE_BINARY:
                return "BINARY";
            case Image3.TYPE_BYTE_GRAY:
                return "GRAY";
            // case Image3.TYPE_BYTE_INDEXED:
            // return "INDEXED";
            case Image3.TYPE_INT_RGB:
                return "RGB";
            case Image3.TYPE_INT_ARGB:
                return "ARGB";
            default:
                return "T" + this.getType();
        }
    }

    @Override
    public String toString()
    {
        return "Image3[" + this.getWidth() + "," + this.getHeight() + "]" + "\nType[" + stringType() + "]" + "\nPaletted[" + isPaletted(256) + "]";
    }

    public static Image3 Read(String path)
    {
        return Image3.Read(File3.Get(path));
    }

    public static Image3 Read(File file)
    {
        Image3 image = null;
        try
        {
            image = new Image3(ImageIO.read(file));
            image.setProperty(Image3.KEY_FILEPATH, File3.Wrap(file).path());
        } catch (Exception ex)
        {
            Log.debug(Image3.class, ".Read - error: " + file);
            ex.printStackTrace();
        }
        return image;
    }

    public static Image3 read(InputStream stream)
    {
        Image3 image = null;
        if (stream != null)
            try
            {
                image = new Image3(ImageIO.read(stream));
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        IO.Close(stream);
        return image;
    }

    public static Image3 Read(byte[] data)
    {
        if (data == null)
            return null;
        try
        {
            BufferedImage image = data == null ? null : ImageIO.read(new ByteArrayInputStream(data));
            if (image == null)
                return null;
            else
                return new Image3(image);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Image3 mirror(boolean flipX, boolean flipY)
    {
        int w = width();
        int h = height();
        Image3 img = new Image3(w, h, this.type());
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                img.setRGB(x, y, this.getARGB(flipX ? w - x - 1 : x, flipY ? h - y - 1 : y));
        return img;
    }

    public Image3 decimate(double ratio)
    {
        return decimate(ratio, null);
    }

    public Image3 decimate(double ratio, ImageThumberCache thumber)
    {
        return decimate(width() * ratio, height() * ratio, thumber);
    }

    public Image3 decimate(double width, double height)
    {
        return decimate(width, height, null);
    }

    public Image3 decimate(double width, double height, ImageThumberCache thumber)
    {
        int w = width <= 0 ? -1 : (int) (0.5 + width);
        int h = height <= 0 ? -1 : (int) (0.5 + height);

        if (w <= 0)
            w = (int) (0.5 + h / (double) height() * width());
        if (h <= 0)
            h = (int) (0.5 + w / (double) width() * height());

        boolean alpha = this.hasTransparency();
//    Log.debug(this, ".decimate - alpha=" + alpha + ", thumber=" + thumber);
        int[][] src = thumber == null ? new int[height()][width()] : thumber.needSrc(width(), height());
        for (int y = 0; y < src.length; y++)
            for (int x = 0; x < src[0].length; x++)
                src[y][x] = getARGB(x, y);

        int[][] res = thumber == null ? new int[h][w] : thumber.needRes(w, h);
        for (int i = 0; i < (alpha ? 4 : 3); i++)
            decimate(src, res, i);

        Image3 out = thumber == null ? new Image3(w, h, alpha) : thumber.needOut(w, h, alpha ? TYPE_INT_ARGB : TYPE_INT_RGB);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                out.setRGB(x, y, res[y][x]);
        return out;
    }

    // private static int outer(int[][] argb, int x, int y, int i)
    // {
    // int v = argb[y >= 0 && y < argb.length ? y : y < 0 ? 0 : argb.length - 1][x
    // >= 0 && x < argb[0].length ? x : x < 0 ? 0 : argb[0].length - 1];
    // return (v >> (i * 8)) & 0xff;
    // }

    private static void decimate(int[][] src, int[][] res, int i)
    {
        int iw = src[0].length;
        int ih = src.length;
        int w = res[0].length;
        int h = res.length;
        double sx = w / (double) src[0].length;
        double sy = h / (double) src.length;
        // Dimension dimension = scale(new Dimension(src[0].length, src.length), sx,
        // sy);
        double tx, ty, tx0, ty0, tx1, ty1, xsize, ysize;
        int ix, iy, ix0, iy0, ix1, iy1;

        double r, area;

        sx = 1.0 / sx;
        sy = 1.0 / sy;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                tx0 = x * sx;
                ty0 = y * sy;
                tx1 = (x + 1) * sx;
                ty1 = (y + 1) * sy;

                ix0 = (int) Math.floor(tx0);
                iy0 = (int) Math.floor(ty0);
                ix1 = (int) Math.ceil(tx1);
                iy1 = (int) Math.ceil(ty1);

                r = 0;
                area = 0;

                for (iy = iy0; iy <= iy1; iy++)
                {
                    ty = iy;
                    ysize = 1.0;
                    if (ty < ty0)
                        ysize = 1.0 - ty0 + ty;
                    if (ty > ty1)
                        ysize *= 1.0 - ty + ty1;

                    for (ix = ix0; ix <= ix1; ix++)
                    {
                        tx = ix;
                        xsize = ysize;
                        if (tx < tx0)
                            xsize *= 1.0 - tx0 + tx;
                        if (tx > tx1)
                            xsize *= 1.0 - tx + tx1;

                        r += ((src[iy >= 0 && iy < ih ? iy : iy < 0 ? 0 : ih - 1][ix >= 0 && ix < iw ? ix : ix < 0 ? 0 : iw - 1] >> (i * 8)) & 0xff) * xsize;
                        area += xsize;
                    }
                }

                r /= area;
                // System.out.print(" "+r);

                res[y][x] |= ((r < 0 ? 0 : r > 255 ? 255 : (int) (0.5 + r)) << i * 8);
            }
    }

    public Image3 crop(float x, float y, float w, float h)
    {
        return crop(new Rectangle3(x, y, w, h));
    }

    public Image3 crop(Rectangle3 box)
    {
        if (box == null)
            return this;
        try
        {
            int x = box.x < 0 ? 0 : box.intX();
            int y = box.y < 0 ? 0 : box.intY();
            int w = box.intWidth();
            int h = box.intHeight();
            int iw = width();
            int ih = height();
            w = w + x > iw ? iw - x : w;
            h = h + y > ih ? ih - y : h;
            return new Image3(this.getSubimage(x, y, w, h));
        } catch (Exception e)
        {
            e.printStackTrace();
            return new Image3();
        }
    }

    public Image3 binary(boolean graylevel, int threshold)
    {
        Image3 image = new Image3(width(), height(), graylevel ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height(); y++)
            for (int x = 0; x < width(); x++)
                image.getRaster().setSample(x, y, 0, luminosity(x, y) > threshold ? graylevel ? 255 : 1 : 0);
        return image;
    }

    public Image3 toRGB()
    {
        Image3 image = new Image3(width(), height(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(this, 0, 0, null);
        g.dispose();
        return image;
    }

    public Image3 rotate90()
    {
        int oldW = this.width();
        int oldH = this.height();
        Image3 image = new Image3(oldH, oldW, this.getType());
        for (int y = 0; y < image.height(); y++)
            for (int x = 0; x < image.width(); x++)
                image.setPixel(x, y, getRGB(y, oldH - x - 1));
        return image;
    }

    public Image3 transparentShading()
    {
        int w = this.width();
        int h = this.height();
        Image3 image = new Image3(w, h, Image3.TYPE_INT_ARGB);

        int[] rgba = new int[4];
        for (int y = 0; y < image.height(); y++)
            for (int x = 0; x < image.width(); x++)
            {
                rgba = image.rgba(x, y, rgba);
                rgba[0] = 255;
                rgba[1] = 255;
                rgba[2] = 255;
                rgba[3] = Math.round(255f * y / h);
                image.setPixel(x, y, rgba);
            }
        return image;
    }

    public Rectangle3 bounds()
    {
        return new Rectangle3(0, 0, width(), height());
    }

    public Image3 copy()
    {
        Image3 img = new Image3(getColorModel(), copyData(null), this.isAlphaPremultiplied(), Props(this));
        img.timestamp = this.timestamp;
        img.properties.putAll(this.properties);
        return img;
    }

    private static Hashtable<String, Object> Props(BufferedImage image)
    {
        String[] names = image.getPropertyNames();
        Hashtable<String, Object> props = new Hashtable<>();
        if (names != null)
            for (String name : names)
                props.put(name, image.getProperty(name));
        return props;
    }

    public static Image3 wrap(BufferedImage image)
    {
        return new Image3(image);
    }

    public static void main(String... args)
    {
        Zen.LAF();
        FileChooser3 chooser = new FileChooser3();
        if (chooser.acceptOpenDialog())
        {
            File file = chooser.file();
            Image3 in = Image3.Read(file);
            Image3 out = in.decimate(1000, 1000);
            out.write(File3.postfix(file.getAbsolutePath(), "_dec_"));
        }
    }
}
