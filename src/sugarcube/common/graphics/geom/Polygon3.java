package sugarcube.common.graphics.geom;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Arrays;

public class Polygon3 implements Shape3
{
  public static final float CIRCLE_R = (float) (1 - 4.0 * (Math.sqrt(2.0) - 1.0) / 3.0);
  public int npoints;
  public float[] xpoints;
  public float[] ypoints;
  public float[] ins;//0-1
  public float[] outs;//0-1
  private static final int MIN_LENGTH = 4;

  public Polygon3()
  {
    xpoints = new float[MIN_LENGTH];
    ypoints = new float[MIN_LENGTH];
    ins = new float[MIN_LENGTH];
    outs = new float[MIN_LENGTH];
  }

  public Polygon3(Point2D... p)
  {
    npoints = p.length;
    xpoints = new float[npoints];
    ypoints = new float[npoints];
    ins = new float[npoints];
    outs = new float[npoints];
    for (int i = 0; i < npoints; i++)
    {
      xpoints[i] = (float) p[i].getX();
      ypoints[i] = (float) p[i].getY();
    }
  }

  public Polygon3(double... p)
  {
    npoints = p.length / 2;
    xpoints = new float[npoints];
    ypoints = new float[npoints];
    ins = new float[npoints];
    outs = new float[npoints];
    for (int i = 0; i < npoints; i++)
    {
      xpoints[i] = (float) p[2 * i];
      ypoints[i] = (float) p[2 * i + 1];
    }
  }

  public Polygon3(float[] xpoints, float[] ypoints)
  {
    this(xpoints, ypoints, new float[xpoints.length], new float[xpoints.length], xpoints.length);
  }

  public Polygon3(float[] xpoints, float[] ypoints, float[] ins, float[] outs, int npoints)
  {
    // Fix 4489009: should throw IndexOutofBoundsException instead
    // of OutofMemoryException if npoints is huge and > {x,y}points.length
    if (npoints > xpoints.length || npoints > ypoints.length)
      throw new IndexOutOfBoundsException("npoints > xpoints.length || "
        + "npoints > ypoints.length");
    // Fix 6191114: should throw NegativeArraySizeException with 
    // negative npoints 
    if (npoints < 0)
      throw new NegativeArraySizeException("npoints < 0");
    // Fix 6343431: Applet compatibility problems if arrays are not
    // exactly npoints in length
    this.npoints = npoints;
    this.xpoints = Arrays.copyOf(xpoints, npoints);
    this.ypoints = Arrays.copyOf(ypoints, npoints);
    this.ins = Arrays.copyOf(ins, npoints);
    this.outs = Arrays.copyOf(outs, npoints);
  }

  public static Polygon3 regularConvex(int size, double phase)
  {
    if (size < 3)
      size = 3;

    phase -= Math.PI / 2;//starts at north, not east...

    float[] x = new float[size];
    float[] y = new float[size];

    float r = 100;
    for (int i = 0; i < size; i++)
    {
      double a = 2 * Math.PI * i / size;
      x[i] = (float) Math.cos(a + phase) * r;
      y[i] = (float) Math.sin(a + phase) * r;
//      Log.debug(Polygon3.class, ".circle - xy:"+ (int)x[i]+", "+(int)y[i]);
    }
    return new Polygon3(x, y);
  }

  public static Polygon3 regularComplex(int size, double depth, double phase)
  {
    if (size < 3)
      size = 3;

    phase -= Math.PI / 2;//starts at north, not east...
    depth = 1 - depth;

    float[] x = new float[size * 2];
    float[] y = new float[size * 2];

    float r = 100;
    for (int i = 0; i < size; i++)
    {
      double a = 2 * Math.PI * i / size;
      double da = Math.PI / size;
      x[2 * i] = (float) Math.cos(a + phase) * r;
      y[2 * i] = (float) Math.sin(a + phase) * r;
      x[2 * i + 1] = (float) (Math.cos(a + da + phase) * r * depth);
      y[2 * i + 1] = (float) (Math.sin(a + da + phase) * r * depth);
    }
    return new Polygon3(x, y);
  }

  @Override
  public void setExtent(Line3 line)
  {
    //TODO;
  }

  public Polygon3 round(double... r)
  {
    return this.copy().setRound(r);
  }

  public Polygon3 setRound(double... r)
  {
    for (int i = 0; i < npoints; i++)
      ins[i] = outs[i] = i < r.length ? (float) r[i] : (float) r[0];
    return this;
  }

  public void reset()
  {
    npoints = 0;
  }

  public void interpolate(int end, float r, float[] coords, int i)
  {
    int i0 = index(end - 1);
    int i1 = index(end);
    coords[i] = xpoints[i0] + (xpoints[i1] - xpoints[i0]) * r;
    coords[i + 1] = ypoints[i0] + (ypoints[i1] - ypoints[i0]) * r;
  }

  public void interpolate(int end, float r, double[] coords, int i)
  {
    int i0 = index(end - 1);
    int i1 = index(end);
    coords[i] = xpoints[i0] + (xpoints[i1] - xpoints[i0]) * r;
    coords[i + 1] = ypoints[i0] + (ypoints[i1] - ypoints[i0]) * r;
  }

  public int index(int i)
  {
    while (i >= npoints)
      i -= npoints;
    while (i < 0)
      i += npoints;
    return i;
  }

  public void translate(float deltaX, float deltaY)
  {
    for (int i = 0; i < npoints; i++)
    {
      xpoints[i] += deltaX;
      ypoints[i] += deltaY;
    }
  }

  public void addPoint(float x, float y)
  {
    if (npoints >= xpoints.length || npoints >= ypoints.length)
    {
      int newLength = npoints * 2;
      // Make sure that newLength will be greater than MIN_LENGTH and 
      // aligned to the power of 2
      if (newLength < MIN_LENGTH)
        newLength = MIN_LENGTH;
      else if ((newLength & (newLength - 1)) != 0)
        newLength = Integer.highestOneBit(newLength);

      xpoints = Arrays.copyOf(xpoints, newLength);
      ypoints = Arrays.copyOf(ypoints, newLength);
      ins = Arrays.copyOf(ins, newLength);
      outs = Arrays.copyOf(outs, newLength);
    }
    xpoints[npoints] = x;
    ypoints[npoints] = y;
    npoints++;
  }

  public void addPoint(float x, float y, float ratio)
  {
    addPoint(x, y, ratio, ratio);
  }

  public void addPoint(float x, float y, float in, float out)
  {
    addPoint(x, y);
    ins[npoints - 1] = in;
    outs[npoints - 1] = out;
  }

  @Override
  public boolean contains(Point2D p)
  {
    return contains(p.getX(), p.getY());
  }

  public boolean contains(int x, int y)
  {
    return contains((double) x, (double) y);
  }

  @Override
  public Rectangle getBounds()
  {
    return this.getBounds2D().getBounds();
  }

  @Override
  public Rectangle3 getBounds2D()
  {
    if (npoints == 0)
      return new Rectangle3();
    float minX = Integer.MAX_VALUE;
    float minY = Integer.MAX_VALUE;
    float maxX = Integer.MIN_VALUE;
    float maxY = Integer.MIN_VALUE;
    for (int i = 0; i < npoints; i++)
    {
      float x = xpoints[i];
      minX = Math.min(minX, x);
      maxX = Math.max(maxX, x);
      float y = ypoints[i];
      minY = Math.min(minY, y);
      maxY = Math.max(maxY, y);
    }
    return new Rectangle3(minX, minY, maxX - minX, maxY - minY);
  }

  @Override
  public boolean contains(double x, double y)
  {
    if (npoints <= 2 || !getBounds2D().contains(x, y))
      return false;
    int hits = 0;

    float lastx = xpoints[npoints - 1];
    float lasty = ypoints[npoints - 1];
    float curx, cury;

    // Walk the edges of the polygon
    for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++)
    {
      curx = xpoints[i];
      cury = ypoints[i];

      if (cury == lasty)
        continue;

      float leftx;
      if (curx < lastx)
      {
        if (x >= lastx)
          continue;
        leftx = curx;
      }
      else
      {
        if (x >= curx)
          continue;
        leftx = lastx;
      }

      double test1, test2;
      if (cury < lasty)
      {
        if (y < cury || y >= lasty)
          continue;
        if (x < leftx)
        {
          hits++;
          continue;
        }
        test1 = x - curx;
        test2 = y - cury;
      }
      else
      {
        if (y < lasty || y >= cury)
          continue;
        if (x < leftx)
        {
          hits++;
          continue;
        }
        test1 = x - lastx;
        test2 = y - lasty;
      }

      if (test1 < (test2 / (lasty - cury) * (lastx - curx)))
        hits++;
    }

    return ((hits & 1) != 0);
  }

  @Override
  public boolean intersects(double x, double y, double w, double h)
  {
    if (npoints <= 0 || !getBounds2D().intersects(x, y, w, h))
      return false;
    else
      return new Path3(this).intersects(x, y, w, h);
  }

  @Override
  public boolean intersects(Rectangle2D r)
  {
    return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
  }

  @Override
  public boolean contains(double x, double y, double w, double h)
  {
    if (npoints <= 0 || !getBounds2D().intersects(x, y, w, h))
      return false;
    else
      return new Path3(this).contains(x, y, w, h);
  }

  @Override
  public boolean contains(Rectangle2D r)
  {
    return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
  }

  @Override
  public PathIterator getPathIterator(AffineTransform at)
  {
    return new PolygonPathIterator(at);
  }

  @Override
  public PathIterator getPathIterator(AffineTransform at, double flatness)
  {
    return getPathIterator(at);
  }

  public class PolygonPathIterator implements PathIterator, Serializable
  {
    private AffineTransform tm;
    private int index;

    public PolygonPathIterator(AffineTransform tm)
    {
      this.tm = tm;
      if (npoints == 0)
        index = 1;// Prevent a spurious SEG_CLOSE segment
    }

    @Override
    public int getWindingRule()
    {
      return WIND_EVEN_ODD;
    }

    @Override
    public boolean isDone()
    {
      return index > 2 * (npoints) + 1;
    }

    @Override
    public void next()
    {
      index++;
    }

    @Override
    public int currentSegment(float[] coords)
    {
//      Log.debug(this, ".currentSegment: index="+index+", npoints="+npoints);
      int seg = SEG_CLOSE;
      if (index >= 2 * (npoints) + 1)
        return seg;


      boolean isOdd = index % 2 != 0;
      int i = (isOdd ? index + 1 : index) / 2;

      seg = index == 0 ? SEG_MOVETO : isOdd ? SEG_LINETO : SEG_CUBICTO;

      if (seg == SEG_MOVETO)
        interpolate(1, outs[index(0)], coords, 0);
      else if (seg == SEG_LINETO)
        interpolate(i, 1 - ins[index(i)], coords, 0);
      else
      {
        float out = outs[index(i)];
        if (Math.abs(out) < 0.00001)//if bezier not used
        {
          seg = SEG_LINETO;
          interpolate(i + 1, out, coords, 0);
        }
        else
        {
          interpolate(i, 1 - (ins[index(i)] * CIRCLE_R), coords, 0);
          interpolate(i + 1, out * CIRCLE_R, coords, 2);
          interpolate(i + 1, out, coords, 4);
        }
//        Log.debug(this, ".currentSegment " + i + "- quadto: " + (int) coords[0] + ", " + (int) coords[1] + ", " + (int) coords[2] + ", " + (int) coords[3]);
      }

      if (tm != null)
        tm.transform(coords, 0, coords, 0, 1);
      return seg;
    }

    @Override
    public int currentSegment(double[] coords)
    {
      int seg = SEG_CLOSE;
      if (index >= 2 * (npoints) + 1)
        return seg;

      boolean isOdd = index % 2 != 0;
      int i = (isOdd ? index + 1 : index) / 2;

      seg = index == 0 ? SEG_MOVETO : isOdd ? SEG_LINETO : SEG_CUBICTO;

      if (seg == SEG_MOVETO)
        interpolate(1, outs[index(0)], coords, 0);
      else if (seg == SEG_LINETO)
        interpolate(i, 1 - ins[index(i)], coords, 0);
      else
      {
        float out = outs[index(i)];
        if (Math.abs(out) < 0.00001)//if bezier not used
        {
          seg = SEG_LINETO;
          interpolate(i + 1, out, coords, 4);
        }
        else
        {
          interpolate(i, 1 - (ins[index(i)] * CIRCLE_R), coords, 0);
          interpolate(i + 1, out * CIRCLE_R, coords, 2);
          interpolate(i + 1, out, coords, 4);
        }
//        Log.debug(this, ".currentSegment " + i + "- quadto: " + (int) coords[0] + ", " + (int) coords[1] + ", " + (int) coords[2] + ", " + (int) coords[3]);
      }

      if (tm != null)
        tm.transform(coords, 0, coords, 0, 1);
      return seg;
    }
  }

  @Override
  public Polygon3 copy()
  {
    return new Polygon3(xpoints, ypoints, ins, outs, npoints);
  }
}
