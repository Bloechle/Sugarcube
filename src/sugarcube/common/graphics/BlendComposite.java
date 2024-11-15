package sugarcube.common.graphics;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public final class BlendComposite implements Composite
{
  public enum Mode
  {
    NORMAL, AVERAGE, MULTIPLY, MULTIPLY_CMYK, SCREEN, DARKEN, LIGHTEN, OVERLAY, HARD_LIGHT, SOFT_LIGHT, DIFFERENCE, NEGATION, EXCLUSION, COLOR_DODGE, INVERSE_COLOR_DODGE, SOFT_DODGE, COLOR_BURN, INVERSE_COLOR_BURN, SOFT_BURN, REFLECT, GLOW, FREEZE, HEAT, ADD, SUBTRACT, STAMP, RED, GREEN, BLUE, HUE, SATURATION, COLOR, LUMINOSITY
  }

  public static final String MODE_NORMAL = "normal";
  public static final String MODE_MULTIPLY = "multiply";
  public static final String MODE_SOFTLIGHT = "softlight";
  public static final String MODE_HARDLIGHT = "hardlight";
  public static final String MODE_OVERLAY = "overlay";
  public static final String MODE_SCREEN = "screen";

  public static final BlendComposite Normal = new BlendComposite(Mode.NORMAL);
  public static final BlendComposite Average = new BlendComposite(Mode.AVERAGE);
  public static final BlendComposite Multiply = new BlendComposite(Mode.MULTIPLY);
  public static final BlendComposite MultiplyCMYK = new BlendComposite(Mode.MULTIPLY_CMYK);
  public static final BlendComposite Screen = new BlendComposite(Mode.SCREEN);
  public static final BlendComposite Darken = new BlendComposite(Mode.DARKEN);
  public static final BlendComposite Lighten = new BlendComposite(Mode.LIGHTEN);
  public static final BlendComposite Overlay = new BlendComposite(Mode.OVERLAY);
  public static final BlendComposite HardLight = new BlendComposite(Mode.HARD_LIGHT);
  public static final BlendComposite SoftLight = new BlendComposite(Mode.SOFT_LIGHT);
  public static final BlendComposite Difference = new BlendComposite(Mode.DIFFERENCE);
  public static final BlendComposite Negation = new BlendComposite(Mode.NEGATION);
  public static final BlendComposite Exclusion = new BlendComposite(Mode.EXCLUSION);
  public static final BlendComposite ColorDodge = new BlendComposite(Mode.COLOR_DODGE);
  public static final BlendComposite InverseColorDodge = new BlendComposite(Mode.INVERSE_COLOR_DODGE);
  public static final BlendComposite SoftDodge = new BlendComposite(Mode.SOFT_DODGE);
  public static final BlendComposite ColorBurn = new BlendComposite(Mode.COLOR_BURN);
  public static final BlendComposite InverseColorBurn = new BlendComposite(Mode.INVERSE_COLOR_BURN);
  public static final BlendComposite SoftBurn = new BlendComposite(Mode.SOFT_BURN);
  public static final BlendComposite Reflect = new BlendComposite(Mode.REFLECT);
  public static final BlendComposite Glow = new BlendComposite(Mode.GLOW);
  public static final BlendComposite Freeze = new BlendComposite(Mode.FREEZE);
  public static final BlendComposite Heat = new BlendComposite(Mode.HEAT);
  public static final BlendComposite Add = new BlendComposite(Mode.ADD);
  public static final BlendComposite Subtract = new BlendComposite(Mode.SUBTRACT);
  public static final BlendComposite Stamp = new BlendComposite(Mode.STAMP);
  public static final BlendComposite Red = new BlendComposite(Mode.RED);
  public static final BlendComposite Green = new BlendComposite(Mode.GREEN);
  public static final BlendComposite Blue = new BlendComposite(Mode.BLUE);
  public static final BlendComposite Hue = new BlendComposite(Mode.HUE);
  public static final BlendComposite Saturation = new BlendComposite(Mode.SATURATION);
  public static final BlendComposite Color = new BlendComposite(Mode.COLOR);
  public static final BlendComposite Luminosity = new BlendComposite(Mode.LUMINOSITY);
  private float alpha;
  private Mode mode;

  private BlendComposite(Mode mode)
  {
    this(mode, 1.0f);
  }

  private BlendComposite(Mode mode, float alpha)
  {
    this.mode = mode;
    setAlpha(alpha);
  }

  public static BlendComposite getInstance(String name, float alpha)
  {
    for (Mode mode : Mode.values())
      if (mode.name().equalsIgnoreCase(name))
        return getInstance(mode, alpha);
    return getInstance(Mode.NORMAL, alpha);
  }

  public static BlendComposite getInstance(Mode mode)
  {
    return new BlendComposite(mode);
  }

  public static BlendComposite getInstance(Mode mode, float alpha)
  {
    return new BlendComposite(mode, alpha);
  }

  public BlendComposite derive(Mode mode)
  {
    return this.mode == mode ? this : new BlendComposite(mode, getAlpha());
  }

  public BlendComposite derive(float alpha)
  {
    return this.alpha == alpha ? this : new BlendComposite(getMode(), alpha);
  }

  public float getAlpha()
  {
    return alpha;
  }

  public Mode getMode()
  {
    return mode;
  }

  private void setAlpha(float alpha)
  {
    if (alpha < 0.0f || alpha > 1.0f)
      throw new IllegalArgumentException("alpha must be comprised between 0.0f and 1.0f");

    this.alpha = alpha;
  }

  @Override
  public int hashCode()
  {
    return Float.floatToIntBits(alpha) * 31 + mode.ordinal();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof BlendComposite))
      return false;

    BlendComposite bc = (BlendComposite) obj;

    if (mode != bc.mode)
      return false;

    return alpha == bc.alpha;
  }

  public BlendingContext createContext()
  {
    return new BlendingContext(this);
  }

  @Override
  public BlendingContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints)
  {
    return new BlendingContext(this);
  }

  public static final class BlendingContext implements CompositeContext
  {
    private final Blender blender;
    private final BlendComposite composite;

    private BlendingContext(BlendComposite composite)
    {
      this.composite = composite;
      this.blender = Blender.getBlenderFor(composite);
    }

    public Blender blender()
    {
      return blender;
    }

    public BlendComposite composite()
    {
      return composite;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
    {
      if (src.getSampleModel().getDataType() != DataBuffer.TYPE_INT || dstIn.getSampleModel().getDataType() != DataBuffer.TYPE_INT
          || dstOut.getSampleModel().getDataType() != DataBuffer.TYPE_INT)
        throw new IllegalStateException("Source and destination must store pixels as INT.");

      int width = Math.min(src.getWidth(), dstIn.getWidth());
      int height = Math.min(src.getHeight(), dstIn.getHeight());

      float alpha = composite.getAlpha();

      int[] result = new int[4];
      int[] srcRgba = new int[4];
      int[] dstRgba = new int[4];

      for (int y = 0; y < height; y++)
      {
        for (int x = 0; x < width; x++)
        {
          src.getPixel(x, y, srcRgba);
          dstIn.getPixel(x, y, dstRgba);

          blender.blend(srcRgba, dstRgba, result);

          // mixes the result with the opacity
          float alphaRes = (result[3]/255f)*alpha;
          for (int i = 0; i < 3; i++)
            dstRgba[i] = (int) (dstRgba[i] + (result[i] - dstRgba[i]) * alphaRes);

          dstRgba[3] = (int) 255;
          dstOut.setPixel(x, y, dstRgba);
        }

      }
    }
  }

  private static void RGBtoHSL(int r, int g, int b, float[] hsl)
  {
    float var_R = (r / 255f);
    float var_G = (g / 255f);
    float var_B = (b / 255f);

    float var_Min;
    float var_Max;
    float del_Max;

    if (var_R > var_G)
    {
      var_Min = var_G;
      var_Max = var_R;
    } else
    {
      var_Min = var_R;
      var_Max = var_G;
    }
    if (var_B > var_Max)
      var_Max = var_B;
    if (var_B < var_Min)
      var_Min = var_B;

    del_Max = var_Max - var_Min;

    float H, S, L;
    L = (var_Max + var_Min) / 2f;

    if (del_Max - 0.01f <= 0.0f)
    {
      H = 0;
      S = 0;
    } else
    {
      if (L < 0.5f)
        S = del_Max / (var_Max + var_Min);
      else
        S = del_Max / (2 - var_Max - var_Min);

      float del_R = (((var_Max - var_R) / 6f) + (del_Max / 2f)) / del_Max;
      float del_G = (((var_Max - var_G) / 6f) + (del_Max / 2f)) / del_Max;
      float del_B = (((var_Max - var_B) / 6f) + (del_Max / 2f)) / del_Max;

      if (var_R == var_Max)
        H = del_B - del_G;
      else if (var_G == var_Max)
        H = (1 / 3f) + del_R - del_B;
      else
        H = (2 / 3f) + del_G - del_R;
      if (H < 0)
        H += 1;
      if (H > 1)
        H -= 1;
    }

    hsl[0] = H;
    hsl[1] = S;
    hsl[2] = L;
  }

  private static void HSLtoRGB(float h, float s, float l, int[] rgb)
  {
    int R, G, B;

    if (s - 0.01f <= 0.0f)
    {
      R = (int) (l * 255.0f);
      G = (int) (l * 255.0f);
      B = (int) (l * 255.0f);
    } else
    {
      float var_1, var_2;
      if (l < 0.5f)
        var_2 = l * (1 + s);
      else
        var_2 = (l + s) - (s * l);
      var_1 = 2 * l - var_2;

      R = (int) (255.0f * hue2RGB(var_1, var_2, h + (1.0f / 3.0f)));
      G = (int) (255.0f * hue2RGB(var_1, var_2, h));
      B = (int) (255.0f * hue2RGB(var_1, var_2, h - (1.0f / 3.0f)));
    }

    rgb[0] = R;
    rgb[1] = G;
    rgb[2] = B;
  }

  private static float hue2RGB(float v1, float v2, float vH)
  {
    if (vH < 0.0f)
      vH += 1.0f;
    if (vH > 1.0f)
      vH -= 1.0f;
    if ((6.0f * vH) < 1.0f)
      return (v1 + (v2 - v1) * 6.0f * vH);
    if ((2.0f * vH) < 1.0f)
      return (v2);
    if ((3.0f * vH) < 2.0f)
      return (v1 + (v2 - v1) * ((2.0f / 3.0f) - vH) * 6.0f);
    return (v1);
  }

  public static abstract class Blender
  {
    public abstract void blend(int[] src, int[] dst, int[] result);

    public static Blender getBlenderFor(BlendComposite composite)
    {
      switch (composite.getMode())
      {
      case ADD:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = Math.min(255, src[0] + dst[0]);
            result[1] = Math.min(255, src[1] + dst[1]);
            result[2] = Math.min(255, src[2] + dst[2]);
            result[3] = Math.min(255, src[3] + dst[3]);
          }
        };
      case AVERAGE:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = (src[0] + dst[0]) >> 1;
            result[1] = (src[1] + dst[1]) >> 1;
            result[2] = (src[2] + dst[2]) >> 1;
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case BLUE:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0];
            result[1] = src[1];
            result[2] = dst[2];
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case COLOR:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            float[] srcHSL = new float[3];
            RGBtoHSL(src[0], src[1], src[2], srcHSL);
            float[] dstHSL = new float[3];
            RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

            HSLtoRGB(srcHSL[0], srcHSL[1], dstHSL[2], result);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case COLOR_BURN:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = src[0] == 0 ? 0 : Math.max(0, 255 - (((255 - dst[0]) << 8) / src[0]));
            result[1] = src[1] == 0 ? 0 : Math.max(0, 255 - (((255 - dst[1]) << 8) / src[1]));
            result[2] = src[2] == 0 ? 0 : Math.max(0, 255 - (((255 - dst[2]) << 8) / src[2]));
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case COLOR_DODGE:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = src[0] == 255 ? 255 : Math.min((dst[0] << 8) / (255 - src[0]), 255);
            result[1] = src[1] == 255 ? 255 : Math.min((dst[1] << 8) / (255 - src[1]), 255);
            result[2] = src[2] == 255 ? 255 : Math.min((dst[2] << 8) / (255 - src[2]), 255);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case DARKEN:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = Math.min(src[0], dst[0]);
            result[1] = Math.min(src[1], dst[1]);
            result[2] = Math.min(src[2], dst[2]);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case DIFFERENCE:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = Math.abs(dst[0] - src[0]);
            result[1] = Math.abs(dst[1] - src[1]);
            result[2] = Math.abs(dst[2] - src[2]);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case EXCLUSION:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0] + src[0] - (dst[0] * src[0] >> 7);
            result[1] = dst[1] + src[1] - (dst[1] * src[1] >> 7);
            result[2] = dst[2] + src[2] - (dst[2] * src[2] >> 7);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case FREEZE:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = src[0] == 0 ? 0 : Math.max(0, 255 - (255 - dst[0]) * (255 - dst[0]) / src[0]);
            result[1] = src[1] == 0 ? 0 : Math.max(0, 255 - (255 - dst[1]) * (255 - dst[1]) / src[1]);
            result[2] = src[2] == 0 ? 0 : Math.max(0, 255 - (255 - dst[2]) * (255 - dst[2]) / src[2]);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case GLOW:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0] == 255 ? 255 : Math.min(255, src[0] * src[0] / (255 - dst[0]));
            result[1] = dst[1] == 255 ? 255 : Math.min(255, src[1] * src[1] / (255 - dst[1]));
            result[2] = dst[2] == 255 ? 255 : Math.min(255, src[2] * src[2] / (255 - dst[2]));
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case GREEN:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0];
            result[1] = dst[1];
            result[2] = src[2];
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case HARD_LIGHT:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = src[0] < 128 ? dst[0] * src[0] >> 7 : 255 - ((255 - src[0]) * (255 - dst[0]) >> 7);
            result[1] = src[1] < 128 ? dst[1] * src[1] >> 7 : 255 - ((255 - src[1]) * (255 - dst[1]) >> 7);
            result[2] = src[2] < 128 ? dst[2] * src[2] >> 7 : 255 - ((255 - src[2]) * (255 - dst[2]) >> 7);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case HEAT:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0] == 0 ? 0 : Math.max(0, 255 - (255 - src[0]) * (255 - src[0]) / dst[0]);
            result[1] = dst[1] == 0 ? 0 : Math.max(0, 255 - (255 - src[1]) * (255 - src[1]) / dst[1]);
            result[2] = dst[2] == 0 ? 0 : Math.max(0, 255 - (255 - src[2]) * (255 - src[2]) / dst[2]);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case HUE:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            float[] srcHSL = new float[3];
            RGBtoHSL(src[0], src[1], src[2], srcHSL);
            float[] dstHSL = new float[3];
            RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

            HSLtoRGB(srcHSL[0], dstHSL[1], dstHSL[2], result);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case INVERSE_COLOR_BURN:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0] == 0 ? 0 : Math.max(0, 255 - (((255 - src[0]) << 8) / dst[0]));
            result[1] = dst[1] == 0 ? 0 : Math.max(0, 255 - (((255 - src[1]) << 8) / dst[1]));
            result[2] = dst[2] == 0 ? 0 : Math.max(0, 255 - (((255 - src[2]) << 8) / dst[2]));
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case INVERSE_COLOR_DODGE:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0] == 255 ? 255 : Math.min((src[0] << 8) / (255 - dst[0]), 255);
            result[1] = dst[1] == 255 ? 255 : Math.min((src[1] << 8) / (255 - dst[1]), 255);
            result[2] = dst[2] == 255 ? 255 : Math.min((src[2] << 8) / (255 - dst[2]), 255);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case LIGHTEN:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = Math.max(src[0], dst[0]);
            result[1] = Math.max(src[1], dst[1]);
            result[2] = Math.max(src[2], dst[2]);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case LUMINOSITY:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            float[] srcHSL = new float[3];
            RGBtoHSL(src[0], src[1], src[2], srcHSL);
            float[] dstHSL = new float[3];
            RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

            HSLtoRGB(dstHSL[0], dstHSL[1], srcHSL[2], result);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case MULTIPLY:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = (src[0] * dst[0]) >> 8;
            result[1] = (src[1] * dst[1]) >> 8;
            result[2] = (src[2] * dst[2]) >> 8;
            result[3] = (src[3] * dst[3]) >> 8;
          }
        };
      case MULTIPLY_CMYK:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {

            result[0] = 255 - (((255 - src[0]) * (255 - dst[0])) >> 8);
            result[1] = 255 - (((255 - src[1]) * (255 - dst[1])) >> 8);
            result[2] = 255 - (((255 - src[2]) * (255 - dst[2])) >> 8);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);

          }
        };
      case NEGATION:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = 255 - Math.abs(255 - dst[0] - src[0]);
            result[1] = 255 - Math.abs(255 - dst[1] - src[1]);
            result[2] = 255 - Math.abs(255 - dst[2] - src[2]);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case OVERLAY:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0] < 128 ? dst[0] * src[0] >> 7 : 255 - ((255 - dst[0]) * (255 - src[0]) >> 7);
            result[1] = dst[1] < 128 ? dst[1] * src[1] >> 7 : 255 - ((255 - dst[1]) * (255 - src[1]) >> 7);
            result[2] = dst[2] < 128 ? dst[2] * src[2] >> 7 : 255 - ((255 - dst[2]) * (255 - src[2]) >> 7);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);            
          }
        };
      case RED:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = src[0];
            result[1] = dst[1];
            result[2] = dst[2];
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case REFLECT:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = src[0] == 255 ? 255 : Math.min(255, dst[0] * dst[0] / (255 - src[0]));
            result[1] = src[1] == 255 ? 255 : Math.min(255, dst[1] * dst[1] / (255 - src[1]));
            result[2] = src[2] == 255 ? 255 : Math.min(255, dst[2] * dst[2] / (255 - src[2]));
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case SATURATION:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            float[] srcHSL = new float[3];
            RGBtoHSL(src[0], src[1], src[2], srcHSL);
            float[] dstHSL = new float[3];
            RGBtoHSL(dst[0], dst[1], dst[2], dstHSL);

            HSLtoRGB(dstHSL[0], srcHSL[1], dstHSL[2], result);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case SCREEN:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = 255 - ((255 - src[0]) * (255 - dst[0]) >> 8);
            result[1] = 255 - ((255 - src[1]) * (255 - dst[1]) >> 8);
            result[2] = 255 - ((255 - src[2]) * (255 - dst[2]) >> 8);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case SOFT_BURN:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0] + src[0] < 256 ? (dst[0] == 255 ? 255 : Math.min(255, (src[0] << 7) / (255 - dst[0]))) : Math.max(0,
                255 - (((255 - dst[0]) << 7) / src[0]));
            result[1] = dst[1] + src[1] < 256 ? (dst[1] == 255 ? 255 : Math.min(255, (src[1] << 7) / (255 - dst[1]))) : Math.max(0,
                255 - (((255 - dst[1]) << 7) / src[1]));
            result[2] = dst[2] + src[2] < 256 ? (dst[2] == 255 ? 255 : Math.min(255, (src[2] << 7) / (255 - dst[2]))) : Math.max(0,
                255 - (((255 - dst[2]) << 7) / src[2]));
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case SOFT_DODGE:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = dst[0] + src[0] < 256 ? (src[0] == 255 ? 255 : Math.min(255, (dst[0] << 7) / (255 - src[0]))) : Math.max(0,
                255 - (((255 - src[0]) << 7) / dst[0]));
            result[1] = dst[1] + src[1] < 256 ? (src[1] == 255 ? 255 : Math.min(255, (dst[1] << 7) / (255 - src[1]))) : Math.max(0,
                255 - (((255 - src[1]) << 7) / dst[1]));
            result[2] = dst[2] + src[2] < 256 ? (src[2] == 255 ? 255 : Math.min(255, (dst[2] << 7) / (255 - src[2]))) : Math.max(0,
                255 - (((255 - src[2]) << 7) / dst[2]));
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case SOFT_LIGHT:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            int mRed = src[0] * dst[0] / 255;
            int mGreen = src[1] * dst[1] / 255;
            int mBlue = src[2] * dst[2] / 255;
            result[0] = mRed + src[0] * (255 - ((255 - src[0]) * (255 - dst[0]) / 255) - mRed) / 255;
            result[1] = mGreen + src[1] * (255 - ((255 - src[1]) * (255 - dst[1]) / 255) - mGreen) / 255;
            result[2] = mBlue + src[2] * (255 - ((255 - src[2]) * (255 - dst[2]) / 255) - mBlue) / 255;
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case STAMP:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = Math.max(0, Math.min(255, dst[0] + 2 * src[0] - 256));
            result[1] = Math.max(0, Math.min(255, dst[1] + 2 * src[1] - 256));
            result[2] = Math.max(0, Math.min(255, dst[2] + 2 * src[2] - 256));
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      case SUBTRACT:
        return new Blender()
        {
          @Override
          public void blend(int[] src, int[] dst, int[] result)
          {
            result[0] = Math.max(0, src[0] + dst[0] - 256);
            result[1] = Math.max(0, src[1] + dst[1] - 256);
            result[2] = Math.max(0, src[2] + dst[2] - 256);
            result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
          }
        };
      }
      throw new IllegalArgumentException("Blender not implemented for " + composite.getMode().name());
    }
  }
}
