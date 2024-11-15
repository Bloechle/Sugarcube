package sugarcube.common.numerics;

import sugarcube.common.graphics.geom.Rectangle3;

import java.util.Random;

public class Math3
{
  public static Random RAND = new Random();
  public static final double LOG_2 = Math.log(2);

  public static boolean RandomBool()
  {
    return RAND.nextBoolean();
  }

  public static int Random(int min, int maxExcluded)
  {
    return RAND.nextInt((maxExcluded - min)) + min;
  }

  public static double Range(double value, double minMax)
  {
    return value < -minMax ? -minMax : (value > minMax ? minMax : value);
  }

  public static float Range(float value, float minMax)
  {
    return value < -minMax ? -minMax : (value > minMax ? minMax : value);
  }

  public static int Range(int value, int minMax)
  {
    return value < -minMax ? -minMax : (value > minMax ? minMax : value);
  }

  public static double Range(double value, double min, double max)
  {
    return value < min ? min : (value > max ? max : value);
  }

  public static float Range(float value, float min, float max)
  {
    return value < min ? min : (value > max ? max : value);
  }

  public static double Range(int value, int min, int max)
  {
    return value < min ? min : (value > max ? max : value);
  }

  public static double Max(double... v)
  {
    double max = v[0];
    for (int i = 1; i < v.length; i++)
      if (v[i] > max)
        max = v[i];
    return max;
  }

  public static double Min(double... v)
  {
    double min = v[0];
    for (int i = 1; i < v.length; i++)
      if (v[i] < min)
        min = v[i];
    return min;
  }

  public static boolean Eq(double a, double b, double epsilon)
  {
    return equals(a, b, epsilon);
  }

  public static boolean equals(double a, double b, double epsilon)
  {
    return epsilon <= 0 ? a == b : Math.abs(a - b) < epsilon;
  }

  public static double Distance2(double x1, double y1, double x2, double y2)
  {
    double i = x2 - x1;
    return i * i + (i = (y2 - y1)) * i;
  }

  public static double Distance(double x1, double y1, double x2, double y2)
  {
    return Math.sqrt(Distance2(x1, y1, x2, y2));
  }

  public static double log2(double a)
  {
    return Math.log(a) / LOG_2;
  }

  public static int logInt2(double a)
  {
    return (int) Math.floor(log2(a));
  }

  public static int Floor(double a)
  {
    return (int) Math.floor(a);
  }

  public static int Ceil(double a)
  {
    return (int) Math.ceil(a);
  }

  public static int Round(double a)
  {
    return (int) Math.round(a);
  }

  public static boolean EqSign(double a, double b)
  {
    return a < 0 && b < 0 || a > 0 && b > 0 || a == b;
  }

  public static int Sign(double v)
  {
    return v < 0 ? -1 : v > 0 ? 1 : 0;
  }

  public static int SignInvert(double v)
  {
    return v < 0 ? 1 : v > 0 ? -1 : 0;
  }

  public static int Sign(double v, boolean invert)
  {
    return invert ? Sign(v) : SignInvert(v);
  }

  public static int Sign(long v, boolean invert)
  {
    return v > 0 ? (invert ? -1 : 1) : v < 0 ? (invert ? 1 : -1) : 0;
  }

  public static boolean r1ContainsR2(Rectangle3 r1, Rectangle3 r2)
  {
    return r1ContainsR2(r1, r2, 0);
  }

  public static boolean r1ContainsR2(Rectangle3 r1, Rectangle3 r2, double toleranceThreshold)
  {
    double x11 = r1.x - toleranceThreshold;
    double y11 = r1.y - toleranceThreshold;
    double x12 = r1.x + r1.width + toleranceThreshold;
    double y12 = r1.y + r1.height + toleranceThreshold;
    double x21 = r2.x;
    double y21 = r2.y;
    double x22 = r2.x + r2.width;
    double y22 = r2.y + r2.height;
    return x11 <= x21 && x12 >= x22 && y11 <= y21 && y12 >= y22;
  }

  public static final boolean r1EqualsR2(Rectangle3 r1, Rectangle3 r2, float acceptedPointsDifference)
  {
    return Math.abs(r1.x - r2.x) <= acceptedPointsDifference && Math.abs(r1.y - r2.y) <= acceptedPointsDifference
        && Math.abs(r1.x + r1.width - r2.x - r2.width) <= acceptedPointsDifference
        && Math.abs(r1.y + r1.height - r2.y - r2.height) <= acceptedPointsDifference;
  }

  public static final boolean r1EqualsR2(Rectangle3 r1, Rectangle3 r2)
  {
    return r1EqualsR2(r1, r2, 0);
  }

}
