package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

import java.awt.color.ColorSpace;

public class CalRGB extends PDFColorSpace
{
  private static ColorSpace rgbCS = ColorSpace.getInstance(ColorSpace.CS_sRGB);
  private static ColorSpace cieCS = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
  private static final float[] vonKriesM =
  {
    0.40024F, -0.2263F, 0.0F, 0.7076F, 1.16532F, 0.0F, -0.08081F, 0.0457F, 0.91822F
  };
  private static final float[] vonKriesMinv =
  {
    1.859936F, 0.361191F, 0.0F, -1.129382F, 0.638812F, 0.0F, 0.219897F, -6.E-006F, 1.089064F
  };
  private static final float[] xyzToSRGB =
  {
    3.24071F, -0.969258F, 0.0556352F, -1.53726F, 1.87599F, -0.203996F, -0.498571F, 0.0415557F, 1.05707F
  };
  private static final float[] xyzToRGB =
  {
    2.04148F, -0.969258F, 0.013446F, -0.564977F, 1.87599F, -0.118373F, -0.344713F, 0.0415557F, 1.01527F
  };
  private float[] scale;
  private float[] max;
  private float[] white =
  {
    1f, 1f, 1f
  };
  private float[] black =
  {
    0f, 0f, 0f
  };
  private float[] gamma =
  {
    1f, 1f, 1f
  };
  private float[] matrix =
  {
    1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f
  };

  public CalRGB(PDFNode parent, PDFDictionary map)
  {
    super(parent, "CalRGB", 3);
    this.white = map.get("WhitePoint").toPDFArray().floatValues(white);
    this.black = map.get("BlackPoint").toPDFArray().floatValues(black);
    this.gamma = map.get("Gamma").toPDFArray().floatValues(gamma);
    this.matrix = map.get("Matrix").toPDFArray().floatValues(matrix);

    float[] cieWhite = rgbCS.toCIEXYZ(new float[]
      {
        1f, 1f, 1f
      });

    float[] sourceWhite = matrixMult(this.white, vonKriesM, 3);
    float[] destWhite = matrixMult(cieWhite, vonKriesM, 3);

    this.scale = new float[]
    {
      destWhite[0] / sourceWhite[0], 0f, 0f, 0f, destWhite[1] / sourceWhite[1], 0f, 0f, 0f, destWhite[2] / sourceWhite[2]
    };

    this.scale = matrixMult(vonKriesM, this.scale, 3);
    this.scale = matrixMult(this.scale, vonKriesMinv, 3);

    this.max = matrixMult(this.white, this.scale, 3);
    this.max = ciexyzToSRGB(this.max);

    this.colorSpace = new ColorSpaceCalRGB();
  }

  private float[] matrixMult(float[] a, float[] b, int len)
  {
    int rows = a.length / len;
    int cols = b.length / len;

    float[] out = new float[rows * cols];

    for (int i = 0; i < rows; i++)
      for (int k = 0; k < cols; k++)
        for (int j = 0; j < len; j++)
          out[(i * cols + k)] += a[(i * len + j)] * b[(j * cols + k)];

    return out;
  }

  private float[] ciexyzToSRGB(float[] xyz)
  {
    float[] rgb = matrixMult(xyz, xyzToSRGB, 3);

    for (int i = 0; i < rgb.length; i++)
    {
      if (rgb[i] < 0.0D)
        rgb[i] = 0.0F;
      else if (rgb[i] > 1.0D)
        rgb[i] = 1.0F;

      if (rgb[i] < 0.003928D)
      {
        int tmp60_59 = i;
        float[] tmp60_58 = rgb;
        tmp60_58[tmp60_59] = (float) (tmp60_58[tmp60_59] * 12.92D);
      }
      else
        rgb[i] = (float) (Math.pow(rgb[i], 0.4166666666666667D) * 1.055D - 0.055D);

    }

    return rgb;
  }

  public class ColorSpaceCalRGB extends ColorSpace
  {
    public ColorSpaceCalRGB()
    {
      super(1000, 3);
    }

    @Override
    public float[] toRGB(float[] comp)
    {
      if (comp.length == 3)
      {
        float a = (float) Math.pow(comp[0], gamma[0]);
        float b = (float) Math.pow(comp[1], gamma[1]);
        float c = (float) Math.pow(comp[2], gamma[2]);

        float[] xyz =
        {
          matrix[0] * a + matrix[3] * b + matrix[6] * c, matrix[1] * a + matrix[4] * b + matrix[7] * c, matrix[2] * a + matrix[5] * b + matrix[8] * c
        };

        xyz = matrixMult(xyz, scale, 3);

        float[] rgb = ciexyzToSRGB(xyz);

        for (int i = 0; i < rgb.length; i++)
        {
          rgb[i] = PDFFunction.interpolate(rgb[i], 0.0F, max[i], 0f, 1f);
          if (rgb[i] > 1)
            rgb[i] = 1;
        }

        return rgb;
      }
      return black;
    }

    @Override
    public float[] fromRGB(float[] rgbvalue)
    {
      Log.warn(this, ".fromRGB - should never be used !");
      return new float[3];
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue)
    {
      Log.warn(this, ".fromCIEXYZ - should never be used !");
      return new float[3];
    }

    @Override
    public int getType()
    {
      return 5;
    }

    @Override
    public int getNumComponents()
    {
      return 3;
    }

    @Override
    public float[] toCIEXYZ(float[] colorvalue)
    {
      Log.warn(this, ".toCIEXYZ - should never be used !");
      return new float[3];
    }
  }
}
