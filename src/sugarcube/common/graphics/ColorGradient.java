package sugarcube.common.graphics;

import sugarcube.common.system.io.File3;

import java.util.Arrays;

public class ColorGradient
{
  public static class ColorPoint implements Comparable<ColorPoint>
  {
    public final float r;
    public final float g;
    public final float b;
    public final float grad;

    public ColorPoint(float r, float g, float b, float grad)
    {
      this.r = r;
      this.g = g;
      this.b = b;
      this.grad = grad;
    }

    public Color3 color()
    {
      return new Color3(r, g, b);
    }

    @Override
    public int compareTo(ColorPoint cp)
    {
      return Float.compare(grad, cp.grad);
    }

  }

  public ColorPoint[] points = new ColorPoint[0];

  public ColorPoint addColorPoint(float r, float g, float b, float pos)
  {
    ColorPoint[] newPoints = new ColorPoint[points.length + 1];
    System.arraycopy(points, 0, newPoints, 0, points.length);
    ColorPoint cp = new ColorPoint(r, g, b, pos);
    newPoints[newPoints.length - 1] = cp;
    Arrays.sort(newPoints);
    this.points = newPoints;
    return cp;
  }

  public void clear()
  {
    this.points = new ColorPoint[0];
  }
  
  public Color3 color(double grad)
  {
    return  color((float)grad);
  }

  public Color3 color(float grad)
  {
    if (points.length == 0)
      return null;

    for (int i = 0; i < points.length; i++)
    {
      ColorPoint p1 = points[i];
      if (grad < p1.grad)
      {
        ColorPoint p0 = points[i == 0 ? 0 : i - 1];
        float delta = (p0.grad - p1.grad);
        float interpol = (delta == 0) ? 0 : (grad - p1.grad) / delta;
        return new Color3((p0.r - p1.r) * interpol + p1.r, (p0.g - p1.g) * interpol + p1.g, (p0.b - p1.b) * interpol + p1.b);
      }
    }
    return points[points.length - 1].color();
  }

  public static ColorGradient HeatMap()
  {
    ColorGradient gradient = new ColorGradient();
    gradient.addColorPoint(0, 0, 1, 0.0f); // Blue
    gradient.addColorPoint(0, 1, 1, 0.25f); // Cyan
    gradient.addColorPoint(0, 1, 0, 0.5f); // Green
    gradient.addColorPoint(1, 1, 0, 0.75f); // Yellow
    gradient.addColorPoint(1, 0, 0, 1.0f); // Red
    return gradient;
  }
  

  public static void main(String... args)
  {
    int width = 1000;
    int height = width/8;
    Image3 img = new Image3(width, height);

    ColorGradient gradient = ColorGradient.HeatMap();

    for (int x = 0; x < width; x++)    
    {
      Color3 c=gradient.color(x/(float)width);
      for (int y = 0; y < height; y++)
        img.setPixel(x, y, c);
    }
    
    img.write(File3.Desk("Wheel.png"));
    System.out.println("Wheel written");
  }
}
