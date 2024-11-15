package sugarcube.common.graphics.geom;

import sugarcube.common.data.Zen;
import sugarcube.common.interfaces.Range2D;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Coords implements Range2D
{
  private float[] xy;

  public Coords(int size)
  {
    this.xy = new float[size * 2];
  }

  public Coords(float[] points)
  {
    this(points.length / 2);
    System.arraycopy(points, 0, xy, 0, points.length);
  }

  public Coords(float[] points, boolean copy)
  {
    if (copy)
    {
      xy = new float[points.length];
      System.arraycopy(points, 0, xy, 0, points.length);
    } else
      this.xy = points;
  }

  public Coords(double... points)
  {
    this(points.length / 2);
    for (int i = 0; i < points.length; i++)
      xy[i] = (float) points[i];
  }

  public Coords(Point3[] points)
  {
    this(points.length);
    for (int i = 0; i < points.length; i++)
    {
      this.xy[2 * i] = points[i].x();
      this.xy[2 * i + 1] = points[i].y();
    }
  }

  public double rotation()
  {
    return Math.atan2(this.lastY() - this.firstY(), this.lastX() - this.firstX());
  }

  public Point3 pointAt(int index)
  {
    return new Point3(xy[index * 2], xy[index * 2 + 1]);
  }

  public Coords pointsAt(int... indexes)
  {
    Coords coords = new Coords(indexes.length);
    for (int i = 0; i < indexes.length; i++)
    {
      coords.xy[i * 2] = xy[indexes[i] * 2];
      coords.xy[i * 2 + 1] = xy[indexes[i] * 2 + 1];
    }
    return coords;
  }

  public void setX(int i, float x)
  {
    xy[i * 2] = x;
  }

  public void setY(int i, float y)
  {
    xy[i * 2 + 1] = y;
  }

  public void setXY(int i, float x, float y)
  {
    xy[i * 2] = x;
    xy[i * 2 + 1] = y;
  }

  public void setXY(int i, double x, double y)
  {
    this.setXY(i, (float) x, (float) y);
  }

  public void set(int i, Point3 p)
  {
    this.setXY(i, p.x, p.y);
  }

  public float[] xy()
  {
    return xy;
  }

  public float[] array()
  {
    return xy;
  }

  public float[] floats()
  {
    return xy;
  }

  public Coords add(double x, double y)
  {
    float[] op = this.xy;
    this.xy = new float[op.length + 2];
    System.arraycopy(op, 0, xy, 0, op.length);
    this.xy[xy.length - 2] = (float) x;
    this.xy[xy.length - 1] = (float) y;
    return this;
  }

  public Coords add(Coords coords)
  {
    float[] op = this.xy;
    this.xy = new float[this.xy.length + coords.xy.length];
    System.arraycopy(op, 0, this.xy, 0, op.length);
    System.arraycopy(coords.xy, 0, this.xy, op.length, coords.xy.length);
    return this;
  }

  public Coords add(Point2D p)
  {
    return add(p.getX(), p.getY());
  }

  public Coords translate(double dx, double dy)
  {
    Coords coords = new Coords(size());
    for (int i = 0; i < xy.length; i += 2)
    {
      coords.xy[i] = xy[i] + (float) dx;
      coords.xy[i + 1] = xy[i + 1] + (float) dy;
    }
    return coords;
  }

  public Coords translate(Point2D point)
  {
    return this.translate(point.getX(), point.getY());
  }

  public Coords translateBack(double dx, double dy)
  {
    Coords coords = new Coords(size());
    for (int i = 0; i < xy.length; i += 2)
    {
      coords.xy[i] = xy[i] - (float) dx;
      coords.xy[i + 1] = xy[i + 1] - (float) dy;
    }
    return coords;
  }

  public Coords translateBack(Point2D point)
  {
    return this.translateBack(point.getX(), point.getY());
  }

  public Point3 get(int index)
  {
    return new Point3(xy[2 * index], xy[2 * index + 1]);
  }

  public float x(int index)
  {
    return xy[2 * index];
  }

  public float y(int index)
  {
    return xy[2 * index + 1];
  }

  public Point3 lastDelta()
  {
    return delta(this.size() - 1);
  }

  public Point3 firstDelta()
  {
    return delta(0);
  }

  public Point3 delta(int index)
  {
    Point3 p2 = get(index);
    Point3 p1 = get(index - 1);
    return new Point3(p2.x - p1.x, p2.y - p1.y);
  }

  public float lastDx()
  {
    return dx(this.size() - 1);
  }

  public float firstDx()
  {
    return dx(0);
  }

  public float lastDy()
  {
    return dy(this.size() - 1);
  }

  public float firstDy()
  {
    return dy(0);
  }

  public float dx(int index)
  {
    return xy[2 * index] - xy[2 * (index - 1)];
  }

  public float dy(int index)
  {
    return xy[2 * index + 1] - xy[2 * (index - 1) + 1];
  }

  public float cx(int index)
  {
    return (xy[2 * index] + xy[2 * (index - 1)]) / 2f;
  }

  public float cy(int index)
  {
    return (xy[2 * index + 1] + xy[2 * (index - 1) + 1]) / 2f;
  }

  public Point3 remove(int index)
  {
    index = index * 2;
    if (index == 0)
      return removeFirst();
    if (index >= xy.length - 2)
      return removeLast();
    float[] op = xy;
    this.xy = new float[op.length - 2];
    for (int i = 0; i < index; i += 2)
    {
      xy[i] = op[i];
      xy[i + 1] = op[i + 1];
    }
    for (int i = index; i < xy.length; i += 2)
    {
      xy[i] = op[i + 2];
      xy[i + 1] = op[i + 3];
    }
    return new Point3(op[index], op[index + 1]);
  }

  public Point3 removeFirst()
  {
    float[] op = xy;
    this.xy = new float[op.length - 2];
    System.arraycopy(op, 2, xy, 0, op.length - 2);
    return new Point3(op[0], op[1]);
  }

  public Point3 removeLast()
  {
    float[] op = xy;
    this.xy = new float[op.length - 2];
    System.arraycopy(op, 0, xy, 0, op.length - 2);
    return new Point3(op[op.length - 2], op[op.length - 1]);
  }

  public float x()
  {
    return xy[0];
  }

  public float y()
  {
    return xy[1];
  }

  public Point3[] points()
  {
    Point3[] points = new Point3[xy.length / 2];
    for (int i = 0; i < points.length; i++)
      points[i] = new Point3(xy[2 * i], xy[2 * i + 1]);
    return points;
  }

  public float width()
  {
    return this.lastX() - this.firstX();
  }

  public float height()
  {
    return this.lastY() - this.firstY();
  }

  public float firstX()
  {
    return xy[0];
  }

  public float firstY()
  {
    return xy[1];
  }

  public float lastX()
  {
    return xy[xy.length - 2];
  }

  public float lastY()
  {
    return xy[xy.length - 1];
  }

  public Point3 last()
  {
    return new Point3(lastX(), lastY());
  }

  public Point3 first()
  {
    return new Point3(firstX(), firstY());
  }

  public Coords split(int index)
  {
    float[] points = new float[index * 2 + 2];
    System.arraycopy(xy, 0, points, 0, points.length);
    Coords coords = new Coords(points, false);
    points = new float[xy.length - 2 * index];
    System.arraycopy(xy, 2 * index, points, 0, points.length);
    this.xy = points;
    return coords;
  }

  public int size()
  {
    return xy.length / 2;
  }

  @Override
  public Object clone()
  {
    return copy();
  }

  public Coords copy()
  {
    float[] points = new float[xy.length];
    System.arraycopy(xy, 0, points, 0, xy.length);
    return new Coords(points, false);
  }

  public Coords rotate(double radians)
  {
    return Transform3.rotateInstance(radians).transform(this);
  }

  public Coords transform(AffineTransform transform)
  {
    Coords coords = new Coords(this.xy);
    transform.transform(coords.xy, 0, coords.xy, 0, coords.xy.length / 2);
    return coords;
  }
  
  public Coords shift(Point3 p)
  {
    return shift(p.x, p.y);
  }

  public Coords shift(double dx, double dy)
  {

    Coords c = new Coords(size());
//    System.out.println("this.length="+this.xy.length+", that.length="+c.xy.length);
    for (int i = 0; i < xy.length; i += 2)
    {
      c.xy[i] = xy[i] + (float) dx;
      c.xy[i + 1] = xy[i + 1] + (float) dy;
    }
    return c;
  }

  public Coords multiply(double factorX, double factorY)
  {
    Coords c = new Coords(size());
    for (int i = 0; i < xy.length; i += 2)
    {
      c.xy[i] = xy[i] * (float) factorX;
      c.xy[i + 1] = xy[i + 1] * (float) factorY;
    }
    return c;
  }

  public Coords divide(double factorX, double factorY)
  {
    Coords c = new Coords(size());
    for (int i = 0; i < xy.length; i += 2)
    {
      c.xy[i] = xy[i] / (float) factorX;
      c.xy[i + 1] = xy[i + 1] / (float) factorY;
    }
    return c;
  }

  public Coords reverseY(double height)
  {
    Coords coords = new Coords(size());
    for (int i = 0; i < xy.length; i += 2)
    {
      coords.xy[i] = xy[i];
      coords.xy[i + 1] = (float) height - xy[i + 1];
    }
    return coords;
  }

  public Coords times(double factor)
  {
    return multiply(factor, factor);
  }

  public Coords divide(double factor)
  {
    return divide(factor, factor);
  }

  public Line3[] segments()
  {
    Line3[] lines = new Line3[xy.length / 2 - 1];
    for (int i = 0; i < lines.length; i++)
      lines[i] = new Line3(xy[2 * i], xy[2 * i + 1], xy[2 * i + 2], xy[2 * i + 3]);
    return lines;
  }

  public Point3[] derivate()
  {
    Point3[] points = new Point3[xy.length / 2 - 1];
    for (int i = 0; i < points.length; i++)
      points[i] = new Point3(xy[2 * i + 2] - xy[2 * i], xy[2 * i + 3] - xy[2 * i + 1]);
    return points;
  }

  public float[] derivateX()
  {
    float[] a = new float[xy.length / 2 - 1];
    for (int i = 0; i < a.length; i++)
      a[i] = xy[2 * i + 2] - xy[2 * i];
    return a;
  }

  public float[] derivateY()
  {
    float[] a = new float[xy.length / 2 - 1];
    for (int i = 0; i < a.length; i++)
      a[i] = xy[2 * i + 3] - xy[2 * i + 1];
    return a;
  }

  public Point3 delta()
  {
    if (size() == 0)
      return new Point3(0, 0);
    else
    {
      Point3 p1 = min();
      Point3 p2 = max();
      return new Point3(p2.getX() - p1.getX(), p2.getY() - p1.getY());
    }
  }

  public Point3 min()
  {
    return new Point3(minX(), minY());
  }

  public Point3 max()
  {
    return new Point3(maxX(), maxY());
  }

  @Override
  public float minX()
  {
    float minX = xy.length > 0 ? xy[0] : 0;
    for (int i = 2; i < xy.length; i += 2)
      if (xy[i] < minX)
        minX = xy[i];
    return minX;
  }

  @Override
  public float minY()
  {
    float minY = xy.length > 1 ? xy[1] : 0;
    for (int i = 3; i < xy.length; i += 2)
      if (xy[i] < minY)
        minY = xy[i];
    return minY;
  }

  @Override
  public float maxX()
  {
    float maxX = xy.length > 0 ? xy[0] : 0;
    for (int i = 2; i < xy.length; i += 2)
      if (xy[i] > maxX)
        maxX = xy[i];
    return maxX;
  }

  @Override
  public float maxY()
  {
    float maxY = xy.length > 1 ? xy[1] : 0;
    for (int i = 3; i < xy.length; i += 2)
      if (xy[i] > maxY)
        maxY = xy[i];
    return maxY;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < xy.length; i += 2)
      sb.append(Zen.toString(xy[i], 3)).append(',').append(Zen.toString(xy[i + 1], 3)).append(',');
    if (sb.charAt(sb.length() - 1) == ',')
      sb.deleteCharAt(sb.length() - 1);
    return sb.append("]").toString();
  }

  public String xStrInt()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < xy.length; i += 2)
      sb.append(Math.round(xy[i])).append(',');
    if (sb.charAt(sb.length() - 1) == ',')
      sb.deleteCharAt(sb.length() - 1);
    return sb.append("]").toString();
  }
}
