package sugarcube.common.graphics;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import sugarcube.common.data.Zen;
import sugarcube.common.data.Zen.Array;
import sugarcube.common.data.collections.A;
import sugarcube.common.data.collections.Str;
import sugarcube.common.interfaces.Colorable;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.File3;
import sugarcube.common.numerics.Stat;
import sugarcube.common.numerics.MoreMath;
import sugarcube.common.numerics.Math3;

import java.awt.*;
import java.awt.color.ColorSpace;

public class Color3 extends Color implements Unjammable
{
  public static final ColorSpace CS_GRAY = ColorSpace.getInstance(ColorSpace.CS_GRAY);
  // partial list of colors from http://en.wikipedia.org/wiki/List_of_colors
  public static final Color3 TRANSPARENT = new Color3(0x00000000);
  public static final Color3 GLASS = new Color3(0x00000000);
  public static final Color3 ANTHRACITE = new Color3(0xFF2D2D2D);
  public static final Color3 AMBER = new Color3(0xFFFFBF00);
  public static final Color3 ASPARAGUS_GREEN = new Color3(0xFF87A96B);
  public static final Color3 BLACK = new Color3(Color.BLACK);
  public static final Color3 BLUE = new Color3(Color.BLUE);
  public static final Color3 BLUE_PIGMENT = new Color3(0xFF333399);
  public static final Color3 BROWN = new Color3(0xFF964B00);
  public static final Color3 BURNT_ORANGE = new Color3(0xFFCC5500);
  public static final Color3 CAROLINA_BLUE = new Color3(0xFF99BADD);
  public static final Color3 CARROT_ORANGE = new Color3(0xFFED9121);
  public static final Color3 CERULEAN = new Color3(0xFF007BA7);
  public static final Color3 CERULEAN_BLUE = new Color3(0xFF2A52BE);
  public static final Color3 CHOCOLATE = new Color3(0xFF7B3F00);
  public static final Color3 CLOUD_WHITE = new Color3(0xFFE8E8E8);
  public static final Color3 COBALT = new Color3(0xFF0047AB);
  public static final Color3 COLUMBIA_BLUE = new Color3(0xFF9BDDFF);
  public static final Color3 CORNFLOWER_BLUE = new Color3(0xFF6495ED);
  public static final Color3 CYAN = new Color3(Color.CYAN);
  public static final Color3 DARK_GRAY = new Color3(Color.DARK_GRAY);
  public static final Color3 DUST_WHITE = new Color3(0xFFF0F0F0);
  public static final Color3 FOREST_GREEN = new Color3(0xFF228B22);
  public static final Color3 GRAY = new Color3(Color.GRAY);
  public static final Color3 GRAY_WHITE = new Color3(0xFFE0E0E0);
  public static final Color3 GREEN = new Color3(Color.GREEN);
  public static final Color3 GREEN_DARK = new Color3(Color.GREEN.darker());
  public static final Color3 GREEN_LEAF = new Color3(0xFF55AE3A);
  public static final Color3 LEMON = new Color3(0xFFFDE910);
  public static final Color3 LIGHT_BLUE = new Color3(0xFFADD8E6);
  public static final Color3 LIGHT_GRAY = new Color3(Color.LIGHT_GRAY);
  public static final Color3 LIGHT_SEA_GREEN = new Color3(0xFF20B2AA);
  public static final Color3 LIME = new Color3(0xFFBFFF00);
  public static final Color3 MAGENTA = new Color3(Color.MAGENTA);
  public static final Color3 MID_GRAY = new Color3(0xFFA0A0A0);
  public static final Color3 MIDNIGHT_BLUE = new Color3(0xFF191970);
  public static final Color3 OFFICE_GREEN = new Color3(0xFF008000);
  public static final Color3 ORANGE = new Color3(Color.ORANGE);
  public static final Color3 ORANGE_PURE = new Color3(255, 127, 0);
  public static final Color3 ORANGE_RED = new Color3(0xFFFF4500);
  public static final Color3 ORANGE_BRIGHT = ORANGE_PURE.brighter();
  public static final Color3 PALE_CORNFLOWER_BLUE = new Color3(0xFFABCDEF);
  public static final Color3 PUMPKIN = new Color3(0xFFFF7518);
  public static final Color3 PURPLE = new Color3(0xFF800080);
  public static final Color3 RED = new Color3(Color.RED);
  public static final Color3 SCARLET = new Color3(0xFFFF2400);
  public static final Color3 SEA_GREEN = new Color3(0xFF2E8B57);
  public static final Color3 STEEL_BLUE = new Color3(0xFF4682B4);
  public static final Color3 TEAL = new Color3(0xFF008080);
  public static final Color3 TOMATO = new Color3(0xFFFF6347);
  public static final Color3 VIOLET = new Color3(0xFF8F00FF);
  public static final Color3 WHITE = new Color3(0xFFFFFFFF);
  public static final Color3 YELLOW = new Color3(Color.YELLOW);
  public static final Color3 YELLOW_FOLDER = new Color3(0xFFFFCC00);
  public static final Color3 YELLOW_GREEN = new Color3(0xFF9ACD32);
  public static final Color3 SC_BLUE = STEEL_BLUE.darker();
  public static final Color3 BLUE_BRIGHT = new Color3(0x77BBDD);
  // new
  // Color3(0xFF008C96);

  public Color3(String hexa)
  {
    this((int) Long.parseLong(hexa.startsWith("#") ? hexa.substring(1) : hexa, 16) & 0xffffffff, hexa.length() >= 8);
  }

  public Color3(int arbg)
  {
    super(arbg, true);
  }

  public Color3(int argb, boolean hasAlpha)
  {
    super(argb, hasAlpha);
  }

  public Color3(Color color)
  {
    super(color.getRGB(), true);
  }

  public Color3()
  {
    super(0, 0, 0);
  }

  public Color3(int red, int green, int blue)
  {
    super(red, green, blue);
  }

  public Color3(int red, int green, int blue, int alpha)
  {
    super(red, green, blue, alpha);
  }

  public Color3(int[] c)
  {
    super(c[0], c.length < 3 ? c[0] : c[1], c.length < 3 ? c[0] : c[2], c.length < 4 ? 255 : c[3]);
  }

  public Color3(float[] c, float alpha)
  {
    super(c[0], c[1], c[2], alpha);
  }

  public Color3(float... c)
  {
    super(c[0], c.length < 3 ? c[0] : c[1], c.length < 3 ? c[0] : c[2], c.length < 4 ? 1f : c[3]);
  }

  public Color3(double[] c)
  {
    this(Zen.Array.toFloats(c));
  }

  public Colorable colorable()
  {
    return () -> this;
  }

  public int squareDist(int... rgb)
  {
    int r = this.getRed() - rgb[0];
    int g = this.getGreen() - rgb[1];
    int b = this.getBlue() - rgb[2];
    return r * r + g * g + b * b;
  }

  public static int toInt(float c)
  {
    return (int) ((0.5f + c) * 255f);
  }

  public double manhattanDistance(Color3 color)
  {
    return (MoreMath.abs(this.getRed() - color.getRed()) + MoreMath.abs(this.getGreen() - color.getGreen()) + MoreMath.abs(this.getBlue() - color.getBlue()))
        / 765.0;
  }

  public double luminosity()
  {
    return (max() + min()) / 510.0;
  }

  public int max()
  {
    return Math.max(Math.max(this.getRed(), this.getGreen()), this.getBlue());
  }

  public int min()
  {
    return Math.min(Math.min(this.getRed(), this.getGreen()), this.getBlue());
  }

  public static Color3 hsl(double... hsl)
  {
    return hsl(Array.toFloats(hsl));
  }

  public static Color3 hsl(float... hsl)
  {
    float[] rgb = hsl2rgb(hsl);
    return new Color3(rgb[0], rgb[1], rgb[2], 1f);
  }

  public Color3 shift(float... hsl)
  {
    float[] HSL = rgb2hsl(Zen.Array.add(this.rgb(), hsl));
    return hsl(HSL[0] + hsl[0], HSL[1] + hsl[1], HSL[2] + hsl[2]);
  }

  public static Color3 hue(float hue)
  {
    return hsl(hue, 0.65f, 0.5f);
  }

  public double lightness()
  {
    return luminosity();
  }

  public float alpha()
  {
    return this.getAlpha() / 255f;
  }

  public float red()
  {
    return this.getRed() / 255f;
  }

  public float green()
  {
    return this.getGreen() / 255f;
  }

  public float blue()
  {
    return this.getBlue() / 255f;
  }

  public float gray()
  {
    return Color3.luminosity(this.rgb());
  }

  public Color3 grayColor()
  {
    int lum = (int) (0.5f + 0.21f * getRed() + 0.72f * getGreen() + 0.07f * getBlue());
    return new Color3(lum, lum, lum, this.getAlpha()); // using sRGB
  }

  public int argb()
  {
    return getRGB();
  }

  public float[] rgba()
  {
    return Zen.Array.Floats(red(), green(), blue(), alpha());
  }

  public float[] rgb()
  {
    return Zen.Array.Floats(red(), green(), blue());
  }

  public String cssHexValue()
  {
    StringBuilder sb = new StringBuilder("#");
    for (int value : rgbValues())
      sb.append((value &= 0xff) < 0x10 ? "0" + Integer.toHexString(value) : Integer.toHexString(value));
    return sb.toString();
  }

  public String cssRGBAValue()
  {
    if (getAlpha() == 255)
      return "rgb(" + getRed() + "," + getGreen() + "," + getBlue() + ")";
    else
      return "rgba(" + getRed() + "," + getGreen() + "," + getBlue() + "," + getAlpha() / 255f + ")";
  }

  public String hexValue()
  {
    StringBuilder sb = new StringBuilder();
    for (int value : intValues())
      sb.append((value &= 0xff) < 0x10 ? "0" + Integer.toHexString(value) : Integer.toHexString(value));
    return sb.toString();
  }

  public int[] intValues()
  {
    return Zen.Array.Ints(getAlpha(), getRed(), getGreen(), getBlue());
  }

  public int[] rgbValues()
  {
    return Zen.Array.Ints(getRed(), getGreen(), getBlue());
  }

  public Color3 darker(int iterations)
  {
    if (iterations == 0)
      return this;
    else if (iterations < 0)
      return brighter(-iterations);
    Color c = super.darker();
    for (int i = 1; i < iterations; i++)
      c = c.darker();
    return new Color3(c.getRed(), c.getGreen(), c.getBlue(), this.getAlpha());
  }

  public Color3 brighter(int iterations)
  {
    if (iterations == 0)
      return this;
    else if (iterations < 0)
      return darker(-iterations);
    Color c = super.brighter();
    for (int i = 1; i < iterations; i++)
      c = c.brighter();
    return new Color3(c.getRed(), c.getGreen(), c.getBlue(), this.getAlpha());
  }

  @Override
  public Color3 darker()
  {
    Color c = super.darker();
    return new Color3(c.getRed(), c.getGreen(), c.getBlue(), this.getAlpha());
  }

  @Override
  public Color3 brighter()
  {
    Color c = super.brighter();
    return new Color3(c.getRed(), c.getGreen(), c.getBlue(), this.getAlpha());
  }

  public Color3 opaque()
  {
    return alpha(1.0);
  }

  public Color3 a(double a)
  {
    return alpha(a);
  }

  public Color3 glass(double a)
  {
    return alpha(a);
  }

  public Color3 alpha(double a)
  {
    return this.getAlpha() == (int) (0.5 + a * 255) ? this : new Color3(red(), green(), blue(), a >= 1f ? 1f : a <= 0f ? 0f : (float) a);
  }

  public boolean isTransparent()
  {
    return this.getAlpha() == 0;
  }

  public boolean isOpaque()
  {
    return this.getAlpha() == 255;
  }

  public boolean isWhite()
  {
    return isOpaque() && getRed() == 255 && getGreen() == 255 && getBlue() == 255;
  }

  public boolean isBlack()
  {
    return isOpaque() && getRed() == 0 && getGreen() == 0 && getBlue() == 0;
  }

  public Color3 interpol(Color3 c, double interpol)
  {
    if (interpol < 0)
      interpol = 0;
    else if (interpol > 1)
      interpol = 1;
    float r = (float) ((c.red() - red()) * interpol + red());
    float g = (float) ((c.green() - green()) * interpol + green());
    float b = (float) ((c.blue() - blue()) * interpol + blue());
    float a = (float) ((c.alpha() - alpha()) * interpol + alpha());

    return new Color3(r, g, b, a);
  }

  public Color3 interpol(Color3 before, Color3 after, double interpol)
  {
    if (interpol < -1)
      interpol = -1;
    else if (interpol > 1)
      interpol = 1;
    if (interpol < 0)
    {
      return before.interpol(this, 1 - MoreMath.abs(interpol));
    } else if (interpol > 0)
    {
      return this.interpol(after, interpol);
    } else
      return this;
  }

  public javafx.scene.paint.Color fx()
  {
    return new javafx.scene.paint.Color(red(), green(), blue(), alpha());
  }

  public javafx.scene.paint.Color fxColor()
  {
    return fx();
  }

  public Background fxBackground()
  {
    return fxBackground(0, 0);
  }

  public Background fxBackground(double radius, double insets)
  {
    return new Background(new BackgroundFill(fx(), radius <= 0 ? null : new CornerRadii(radius), insets <= 0 ? null : new Insets(insets)));
  }

  public boolean equals(Color3 color, int d)
  {
    return Math3.Eq(getAlpha(), color.getAlpha(), d) && Math3.Eq(getRed(), color.getRed(), d) && Math3.Eq(getGreen(), color.getGreen(), d)
        && Math3.Eq(getBlue(), color.getBlue(), d);
  }

  public Color3 copy()
  {
    return new Color3(this);
  }

  @Override
  public String toString()
  {
    return cssHexValue();
  }

  public static Color3 Get(javafx.scene.paint.Color fx)
  {
    return fx == null ? null : new Color3(Zen.Array.toFloats(fx.getRed(), fx.getGreen(), fx.getBlue(), fx.getOpacity()));
  }
  
  public static int[] Unpack(int argb, int[] rgba)
  {
      if(rgba==null)
          rgba = new int[4];

      rgba[0] = argb >> 16 & 0xff;
      rgba[1] = argb >> 8 & 0xff;
      rgba[2] = argb & 0xff;
      if(rgba.length>3)
          rgba[3] = argb >> 16 & 0xff;

      return rgba;
  }

  public static int Pack(int[] rgba)
  {
      return rgba[2] | rgba[1] << 8 | rgba[0] << 16 | (rgba.length>3 ? rgba[3] : 0xff) << 24;
  }

  public static int rgb2int(int r, int g, int b)
  {
    return (b > 255 ? 255 : b < 0 ? 0 : b) | (g > 255 ? 255 : g < 0 ? 0 : g) << 8 | (r > 255 ? 255 : r < 0 ? 0 : r) << 16 | 255 << 24;
  }

  public static int rgb2int(int[] rgb)
  {
    return (rgb[2] > 255 ? 255 : rgb[2] < 0 ? 0 : rgb[2]) | (rgb[1] > 255 ? 255 : rgb[1] < 0 ? 0 : rgb[1]) << 8
        | (rgb[0] > 255 ? 255 : rgb[0] < 0 ? 0 : rgb[0]) << 16 | (rgb.length < 4 ? 255 : (rgb[3] > 255 ? 255 : rgb[3] < 0 ? 0 : rgb[3])) << 24;
  }

  public static int rgb2int(float[] rgb)
  {
    return rgb2int(ints(rgb));
  }

  // int is supposed to be 00RRGGBB
  public static float[] int2rgb(int rgb)
  {
    return A.Floats(((rgb >> 16) & 0xff) / 255f, ((rgb >> 8) & 0xff) / 255f, (rgb & 0xff) / 255f);
  }

  // int is supposed to be AARRGGBB
  public static float[] int2rgba(int argb)
  {
    return A.Floats(((argb >> 16) & 0xff) / 255f, ((argb >> 8) & 0xff) / 255f, (argb & 0xff) / 255f, ((argb >> 24) & 0xff) / 255f);
  }

  public static int[] ints(float[] c)
  {
    int[] values = new int[c.length];
    for (int i = 0; i < values.length; i++)
      values[i] = (int) (0.5 + 255f * c[i]);
    return values;
  }

  public static float[] floats(int[] c)
  {
    float[] values = new float[c.length];
    for (int i = 0; i < values.length; i++)
      values[i] = c[i] / 255f;
    return values;
  }

  public static float luminosity(float... rgb)
  {
    return 0.21f * rgb[0] + 0.72f * rgb[1] + 0.07f * rgb[2]; // using sRGB
  }

  public static int luminosity(int rgb)
  {
    // or (int) (0.5 + 0.11 * (rgb & 0xff) + 0.59 * (rgb >> 8 & 0xff) + 0.3 *
    // (rgb >> 16 & 0xff));
    int lum = (int) (0.5 + 0.21f * (rgb & 0xff) + 0.72f * (rgb >> 8 & 0xff) + 0.07f * (rgb >> 16 & 0xff));
    return lum < 0 ? 0 : lum > 255 ? 255 : lum;
  }

  public static float rgb2lum(float... rgb)
  {
    float r = rgb[0];
    float g = rgb[1];
    float b = rgb[2];
    float min = r < g ? (r < b ? r : b) : (g < b ? g : b);
    float max = r > g ? (r > b ? r : b) : (g > b ? g : b);
    return (max + min) / 2;
  }

  public static float[] rgb2satLum(float... rgb)
  {
    float r = rgb[0];
    float g = rgb[1];
    float b = rgb[2];
    float min = r < g ? (r < b ? r : b) : (g < b ? g : b);
    float max = r > g ? (r > b ? r : b) : (g > b ? g : b);
    float delta = max - min;

    float sat = 0;
    float lum = (max + min) / 2;

    if (delta != 0)
      sat = lum < 0.5 ? delta / (max + min) : delta / (2 - max - min);

    return A.Floats(sat, lum);
  }

  public static float[] rgb2hsl(float... rgb)
  {
    float r = rgb[0];
    float g = rgb[1];
    float b = rgb[2];
    float min = r < g ? (r < b ? r : b) : (g < b ? g : b);
    float max = r > g ? (r > b ? r : b) : (g > b ? g : b);
    float delta = max - min;

    float hue = 0;
    float sat = 0;
    float lum = (max + min) / 2;

    if (delta != 0)
    {
      sat = lum < 0.5 ? delta / (max + min) : delta / (2 - max - min);

      float deltaR = (((max - r) / 6) + (delta / 2)) / delta;
      float deltaG = (((max - g) / 6) + (delta / 2)) / delta;
      float deltaB = (((max - b) / 6) + (delta / 2)) / delta;

      if (r == max)
        hue = deltaB - deltaG;
      else if (g == max)
        hue = deltaR - deltaB + 1f / 3f;
      else if (b == max)
        hue = deltaG - deltaR + 2f / 3f;
    }

    return A.Floats(hue < 0 ? hue + 1 : hue > 1 ? hue - 1 : hue, sat, lum);
  }

  public static float[] hsl2rgb(float... hsl)
  {
    if (hsl.length == 1)
      hsl = A.Floats(hsl[0], 1f, 0.5f);

    float h = hsl[0] > 1 ? (hsl[0] - (int) hsl[0]) : hsl[0] < 0 ? (1 + hsl[0] - (int) hsl[0]) : hsl[0];
    float s = hsl[1] > 1 ? 1 : hsl[1] < 0 ? 0 : hsl[1];
    float l = hsl[2] > 1 ? 1 : hsl[2] < 0 ? 0 : hsl[2];

    float[] rgb = new float[3];
    if (s != 0)
    {
      float w = l < 0.5 ? l * (1 + s) : l + s - l * s;
      float u = 2 * l - w;
      rgb[0] = hue2rgb(u, w, h + 1f / 3f);
      rgb[1] = hue2rgb(u, w, h);
      rgb[2] = hue2rgb(u, w, h - 1f / 3f);
    } else
      rgb[0] = rgb[1] = rgb[2] = l;

    for (int i = 0; i < rgb.length; i++)
    {
      if (rgb[i] > 1)
        rgb[i] = 1;
      if (rgb[i] < 0)
        rgb[i] = 0;
    }
    return rgb;
  }

  private static float hue2rgb(float u, float w, float h)
  {
    if (h < 0)
      h += 1;
    else if (h > 1)
      h -= 1;

    if (6 * h < 1)
      return u + (w - u) * 6 * h;
    else if (2 * h < 1)
      return w;
    else if (3 * h < 2)
      return u + (w - u) * (2f / 3f - h) * 6;
    else
      return u;
  }

  // easyrgb.com
  public static float[] hsv2rgb(float[] hsv)
  {
    if (hsv[1] == 0)
      return new float[]
      { hsv[2], hsv[2], hsv[2] };
    else
    {
      float h = hsv[0] * 6 % 6;
      float f = h - (int) h;
      float p = hsv[2] * (1f - hsv[1]);
      float q = hsv[2] * (1f - hsv[1] * f);
      float t = hsv[2] * (1f - hsv[1] * (1 - f));

      switch ((int) h)
      {
      case 0:
        return new float[]
        { hsv[2], t, p };
      case 1:
        return new float[]
        { q, hsv[2], p };
      case 2:
        return new float[]
        { p, hsv[2], t };
      case 3:
        return new float[]
        { p, q, hsv[2] };
      case 4:
        return new float[]
        { t, p, hsv[2] };
      default:
        return new float[]
        { hsv[2], p, q };
      }
    }
  }

  // easyrgb.com
  public static float[] rgb2hsv(float[] rgb)
  {
    float min = Stat.min(rgb);
    float max = Stat.max(rgb);
    float delta = max - min;
    float h;
    if (max == min)
      h = 0;
    else if (max == rgb[0])
      if (rgb[1] < rgb[2])
        h = (rgb[1] - rgb[2]) / 6f / delta + 1f;
      else
        h = (rgb[1] - rgb[2]) / 6f / delta;
    else if (max == rgb[1])
      h = (rgb[2] - rgb[0]) / 6f / delta + 1f / 3f;
    else
      h = (rgb[0] - rgb[1]) / 6f / delta + 2f / 3f;
    return new float[]
    { h, max == 0 ? 0 : (1 - min / max), max };
  }

  // mandelbrot-dazibao.com
  public static double[] hsb2rgb(double[] hsl)
  {
    double angle = (hsl[0] - 5.0 / 12.0) * 2 * MoreMath.PI;
    double l = hsl[2];
    double radius = l * Math.tan(hsl[1] * MoreMath.ATAN_SQRT_6);
    double rx = radius * Math.cos(angle) / MoreMath.SQRT_2;
    double ry = radius * Math.sin(angle) / MoreMath.SQRT_6;
    double r = l - rx - ry;
    double g = l + rx - ry;
    double b = l + ry + ry;
    double rdim;

    if (r < 0.0)
    {
      rdim = l / (rx + ry);
      r = 0.0;
      g = l + (rx - ry) * rdim;
      b = l + 2.0 * ry * rdim;
    } else if (g < 0.0)
    {
      rdim = -l / (rx - ry);
      r = l - (rx + ry) * rdim;
      g = 0.0;
      b = l + 2.0 * ry * rdim;
    } else if (b < 0.0)
    {
      rdim = -l / (ry + ry);
      r = l - (rx + ry) * rdim;
      g = l + (rx - ry) * rdim;
      b = 0.0;
    }

    if (r > 1.0)
    {
      rdim = (l - 1.0) / (rx + ry);
      r = 1.0;
      g = l + (rx - ry) * rdim;
      b = l + 2.0 * ry * rdim;
    }

    if (g > 1.0)
    {
      rdim = (1.0 - l) / (rx - ry);
      r = l - (rx + ry) * rdim;
      g = 1.0;
      b = l + 2.0 * ry * rdim;
    }

    if (b > 1.0)
    {
      rdim = (1.0 - l) / (ry + ry);
      r = l - (rx + ry) * rdim;
      g = l + (rx - ry) * rdim;
      b = 1.0;
    }
    return new double[]
    { r, g, b };
  }

  // mandelbrot-dazibao.com
  public static double[] rgb2hsz(double[] rgb)
  {
    double lum = (rgb[0] + rgb[1] + rgb[2]) / 3.0;
    double xa = (rgb[1] - rgb[0]) / MoreMath.SQRT_2;
    double ya = (2.0 * rgb[2] - rgb[0] - rgb[1]) / MoreMath.SQRT_6;
    double hue = Math.atan2(ya, xa) / (2 * MoreMath.PI) + 5.0 / 12.0;
    double sat = Math.atan2(Zen.Array.norm(rgb[0] - lum, rgb[1] - lum, rgb[2] - lum), lum) / MoreMath.ATAN_SQRT_6;
    return new double[]
    { sat == 0 && lum == 0 ? 0 : hue < 0 ? hue + 1 : hue >= 1 ? hue - 1 : hue, sat, lum };
  }

  public static String colorSpace(int type)
  {
    switch (type)
    {
    case 0:
      return "TYPE_XYZ";
    case 1:
      return "TYPE_Lab";
    case 2:
      return "TYPE_Luv";
    case 3:
      return "TYPE_YCbCr";
    case 4:
      return "TYPE_Yxy";
    case 5:
      return "TYPE_RGB";
    case 6:
      return "TYPE_GRAY";
    case 7:
      return "TYPE_HSV";
    case 8:
      return "TYPE_HLS";
    case 9:
      return "TYPE_CMYK";
    case 11:
      return "TYPE_CMY";
    case 12:
      return "TYPE_2CLR";
    case 13:
      return "TYPE_3CLR";
    case 14:
      return "TYPE_4CLR";
    case 15:
      return "TYPE_5CLR";
    case 16:
      return "TYPE_6CLR";
    case 17:
      return "TYPE_7CLR";
    case 18:
      return "TYPE_8CLR";
    case 19:
      return "TYPE_9CLR";
    case 20:
      return "TYPE_ACLR";
    case 21:
      return "TYPE_BCLR";
    case 22:
      return "TYPE_CCLR";
    case 23:
      return "TYPE_DCLR";
    case 24:
      return "TYPE_ECLR";
    case 25:
      return "TYPE_FCLR";
    case 1000:
      return "CS_sRGB";
    case 1001:
      return "CS_CIEXYZ";
    case 1002:
      return "CS_PYCC";
    case 1003:
      return "CS_GRAY";
    case 1004:
      return "CS_LINEAR_RGB";
    default:
      return "TYPE_UNKNOWN";
    }
  }

  public static Color3 ParseHex(String hexa, Color def)
  {
    hexa = hexa.startsWith("#") ? hexa.substring(1).trim() : hexa.trim();
    for (int i = 0; i < hexa.length(); i++)
      if (hexa.charAt(i) < '0' || hexa.charAt(i) > '9')
        return new Color3(def);
    return new Color3(hexa);
  }

  public static Color3 ParseCss(String css, Color def)
  {
    if (Str.IsVoid(css))
      return def instanceof Color3 ? (Color3) def : new Color3(def);

    Str text = new Str(css);
    if (text.containsOne("rgb", "rgba"))
    {
      Str[] rgb = text.remove("rgba", "rgb", "(", ")", ";").spacify(",").splitSpace().array();
      if (rgb.length == 3)
        return new Color3(rgb[0].integer(), rgb[1].integer(), rgb[2].integer());
      else if (rgb.length == 4)
        return new Color3(rgb[0].integer(), rgb[1].integer(), rgb[2].integer(), (int) (0.5 + 255 * rgb[3].real()));
      else
        return new Color3(def);
    } else
      return ParseHex(css, def);
  }

  public static Color3[] array(Color... c)
  {
    Color3[] colors = new Color3[c.length];
    for (int i = 0; i < c.length; i++)
      colors[i] = c[i] instanceof Color3 ? (Color3) c[i] : new Color3(c[i]);
    return colors;
  }

  public static Color3[] RainbowWheel(int size)
  {
    double freq = 2 * MoreMath.PI / (size + size / 5);
    double delta = 1.2;
    return Wheel(freq, freq, freq, 0 + delta, 2 + delta, 4 + delta, 127, 128, size);
  }

  public static Color3[] Wheel(double freqR, double freqG, double freqB, double phaseR, double phaseG, double phaseB, int width, int center, int size)
  {
    Color3[] gradient = new Color3[size];
    for (int i = 0; i < size; ++i)
    {
      int r = (int) (Math.sin(freqR * i + phaseR) * width + center);
      int g = (int) (Math.sin(freqG * i + phaseG) * width + center);
      int b = (int) (Math.sin(freqB * i + phaseB) * width + center);
      gradient[i] = new Color3(r, g, b);
    }
    return gradient;
  }

  public static String toHex(int argb) {
    int alpha = (argb >> 24) & 0xFF;
    int red = (argb >> 16) & 0xFF;
    int green = (argb >> 8) & 0xFF;
    int blue = argb & 0xFF;

    // If alpha is 255 (fully opaque), return #RRGGBB; otherwise, return #AARRGGBB
    if (alpha == 255) {
      return String.format("#%02X%02X%02X", red, green, blue);
    } else {
      return String.format("#%02X%02X%02X%02X", alpha, red, green, blue);
    }
  }


  public static void main(String... args)
  {
    int width = 1000;
    int height = width / 8;
    Image3 img = new Image3(width, height);

    Color3[] colors = Color3.RainbowWheel(width);

    for (int x = 0; x < width; x++)
    {

      for (int y = 0; y < height; y++)
        img.setPixel(x, y, colors[x]);
    }

    img.write(File3.Desk("Wheel.png"));
    System.out.println("Wheel written");
  }

}
