package sugarcube.common.graphics.geom;

import javafx.collections.ObservableList;
import javafx.scene.shape.*;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.common.ui.gui.Font3;
import sugarcube.common.ui.gui.Frame3;
import sugarcube.common.ui.gui.Panel3;
import sugarcube.common.system.io.File3;

import java.awt.Shape;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Path3 extends Path2D.Float implements Shape3
{
  public static class Seg
  {
    public Op op;
    public float[] p;

    public Seg(Op op, float... p)
    {
      this.op = op;
      this.p = p;
    }

    public int type()
    {
      return op.type;
    }

    public Point3 p()
    {
      return new Point3(p[p.length - 2], p[p.length - 1]);
    }

    public Point3 c0()
    {
      return new Point3(p[0], p[1]);
    }

    public Point3 c1()
    {
      return new Point3(p[2], p[3]);
    }

    public float x()
    {
      return p[p.length - 2];
    }

    public float y()
    {
      return p[p.length - 1];
    }

    public float c0x()
    {
      return p[0];
    }

    public float c0y()
    {
      return p[1];
    }

    public float c1x()
    {
      return p[2];
    }

    public float c1y()
    {
      return p[3];
    }

  }

  public static transient int debugCounter = 0;
  public static final String EVENODD = "evenodd";
  public static final String NONZERO = "nonzero";
  public transient float x = 0;
  public transient float y = 0;
  private transient Rectangle3 bounds;

  public Path3()
  {
    super();
  }

  public Path3(Point2D p)
  {
    this(p.getX(), p.getY());
  }

  public Path3(double x, double y)
  {
    super();
    this.moveTo(x, y);
  }

  public Path3(String rule)
  {
    super(rule == null || !rule.equals(EVENODD) ? Path3.WIND_NON_ZERO : Path3.WIND_EVEN_ODD);
  }

  public Path3(String rule, int initialCapacity)
  {
    super(rule == null || !rule.equals(EVENODD) ? Path3.WIND_NON_ZERO : Path3.WIND_EVEN_ODD, initialCapacity);
  }

  public Path3(int rule)
  {
    super(rule);
  }

  public Path3(Shape shape)
  {
    super(shape);
  }

  public Path3(GeneralPath path)
  {
    super(path);
    this.setWindingRule(path.getWindingRule());
  }

  public Path3(Path2D path)
  {
    super(path);
    this.setWindingRule(path.getWindingRule());
  }

  public Path3(Shape shape, AffineTransform at)
  {
    super(shape, at);
  }

  public Path3(boolean doClosePolygon, double... p)
  {
    super(WIND_NON_ZERO, p.length / 2);
    this.moveTo(p[0], p[1]);
    for (int i = 1; i < p.length / 2; i++)
      this.lineTo(p[2 * i], p[2 * i + 1]);
    if (doClosePolygon)
      this.closePath();
  }

  public void lineMoveTo(double x, double y)
  {
    if (!this.hasMoveTo() || this.isClosed())
      this.moveTo(x, y);
    else
      this.lineTo(x, y);
  }

  public void lineMoveTo(Point2D p)
  {
    if (!this.hasMoveTo())
      this.moveTo(p);
    else
      this.lineTo(p);
  }

  public void moveTo(Point2D p)
  {
    this.moveTo(p.getX(), p.getY());
  }

  public void lineTo(Point2D p)
  {
    this.lineTo(p.getX(), p.getY());
  }

  public void quadTo(Point2D p1, Point2D p2)
  {
    this.quadTo(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  public void curveTo(Point2D p1, Point2D p2, Point2D p3)
  {
    this.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
  }

  public void setWind(String wind)
  {
    this.setWindingRule(wind(wind));
  }

  public void append(int op, float... p)
  {
    switch (op)
    {
    case PathIterator.SEG_MOVETO:
      this.moveTo(p[0], p[1]);
      break;
    case PathIterator.SEG_LINETO:
      this.lineTo(p[0], p[1]);
      break;
    case PathIterator.SEG_QUADTO:
      this.quadTo(p[0], p[1], p[2], p[3]);
      break;
    case PathIterator.SEG_CUBICTO:
      this.curveTo(p[0], p[1], p[2], p[3], p[4], p[5]);
      break;
    case PathIterator.SEG_CLOSE:
      this.closePath();
      break;
    default:
      break;
    }
  }

  public boolean isEmpty()
  {
    // empty means no drawing path primitive
    float[] p = new float[6];
    PathIterator it = getPathIterator(null);
    try
    {
      while (!it.isDone())
      {
        it.next();
        Op op = Op.type(it.currentSegment(p));
        if (op != Op.CLOSE && op != Op.MOVE)
          return false;
      }
      return true;
    } catch (Exception e)
    {
      return true;
    }
  }

  public boolean hasMoveTo()
  {
    return !getPathIterator(null).isDone();
  }

  public boolean isClosed()
  {
    PathIterator pi = this.getPathIterator(null);
    int op = -1;
    float[] c = new float[6];
    try
    {
      while (!pi.isDone())
      {
        op = pi.currentSegment(c);
        pi.next();
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return op == PathIterator.SEG_CLOSE;
  }

  public Path3 reverse()
  {
    return reverse(true, true);
  }

  public Path3 reverseY()
  {
    return reverse(false, true);
  }

  public Path3 reverseX()
  {
    return reverse(true, false);
  }

  public Path3 reverse(boolean bx, boolean by)
  {
    Path3 path = new Path3(this.getWindingRule());
    PathIterator it = getPathIterator(null);
    float[] p = new float[6];
    try
    {
      while(!it.isDone())
      {       
        switch (Op.type(it.currentSegment(p)))
        {
        case MOVE:
          path.moveTo(bx ? -p[0] : p[0], by ? -p[1] : p[1]);
          break;
        case LINE:
          path.lineTo(bx ? -p[0] : p[0], by ? -p[1] : p[1]);
          break;
        case QUAD:
          path.quadTo(bx ? -p[0] : p[0], by ? -p[1] : p[1], bx ? -p[2] : p[2], by ? -p[3] : p[3]);
          break;
        case CUBIC:
          path.curveTo(bx ? -p[0] : p[0], by ? -p[1] : p[1], bx ? -p[2] : p[2], by ? -p[3] : p[3], bx ? -p[4] : p[4], by ? -p[5] : p[5]);
          break;
        case CLOSE:
          path.closePath();
          break;
        }
        it.next();
      } 
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return path;
  }

  public Path3 shift(Point2D p, boolean back)
  {
    return this.shift((float) p.getX(), (float) p.getY(), back);
  }

  public Path3 shift(float dx, float dy, boolean back)
  {
    if (back)
    {
      dx = -dx;
      dy = -dy;
    }
    Path3 path = new Path3(this.getWindingRule());
    PathIterator it = getPathIterator(null);
    float[] p = new float[6];
    try
    {
      do
      {
        switch (Op.type(it.currentSegment(p)))
        {
        case MOVE:
          path.moveTo(p[0] + dx, p[1] + dy);
          break;
        case LINE:
          path.lineTo(p[0] + dx, p[1] + dy);
          break;
        case QUAD:
          path.quadTo(p[0] + dx, p[1] + dy, p[2] + dx, p[3] + dy);
          break;
        case CUBIC:
          path.curveTo(p[0] + dx, p[1] + dy, p[2] + dx, p[3] + dy, p[4] + dx, p[5] + dy);
          break;
        case CLOSE:
          path.closePath();
          break;
        }
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return path;
  }

  public Seg[] segments()
  {
    List3<Seg> segs = new List3<Seg>();
    PathIterator it = getPathIterator(null);
    float[] p = new float[6];
    float ox = 0;
    float oy = 0;
    try
    {
      do
      {
        switch (Op.type(it.currentSegment(p)))
        {
        case MOVE:
          segs.add(new Seg(Op.MOVE, ox = p[0], oy = p[1]));
          break;
        case LINE:
          segs.add(new Seg(Op.LINE, p[0], p[1]));
          break;
        case QUAD:
          segs.add(new Seg(Op.QUAD, p[0], p[1], p[2], p[3]));
          break;
        case CUBIC:
          segs.add(new Seg(Op.CUBIC, p[0], p[1], p[2], p[3], p[4], p[5]));
          break;
        case CLOSE:
          segs.add(new Seg(Op.CLOSE, ox, oy));
          break;
        }
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return segs.toArray(new Seg[0]);
  }

  public Path3 closeSubpaths()
  {
    Path3 norm = new Path3(wind());
    PathIterator it = getPathIterator(null);
    float[] p = new float[6];
    boolean firstMove = true;
    try
    {
      do
      {
        int type = it.currentSegment(p);
        if (type == PathIterator.SEG_MOVETO)
        {
          if (firstMove)
            firstMove = false;
          else
            norm.closePath();
        }
        norm.append(type, p);
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    norm.closePath();
    return norm;
  }

  public enum Op
  {
    MOVE('m', PathIterator.SEG_MOVETO, 2), LINE('l', PathIterator.SEG_LINETO, 2), QUAD('q', PathIterator.SEG_QUADTO, 4), CUBIC('c',
        PathIterator.SEG_CUBICTO, 6), CLOSE('z', PathIterator.SEG_CLOSE, 0);
    private int size;
    private int type;
    private char code;
    private static final Map<Integer, Op> TYPES = new LinkedHashMap<Integer, Op>();
    private static final Map<Integer, Op> CODES = new LinkedHashMap<Integer, Op>();

    static
    {
      TYPES.put(MOVE.type, MOVE);
      TYPES.put(LINE.type, LINE);
      TYPES.put(QUAD.type, QUAD);
      TYPES.put(CUBIC.type, CUBIC);
      TYPES.put(CLOSE.type, CLOSE);

      CODES.put((int) MOVE.code, MOVE);
      CODES.put((int) LINE.code, LINE);
      CODES.put((int) QUAD.code, QUAD);
      CODES.put((int) CUBIC.code, CUBIC);
      CODES.put((int) CLOSE.code, CLOSE);
    }

    private Op(char segName, int segType, int segSize)
    {
      this.size = segSize;
      this.type = segType;
      this.code = segName;
    }

    public static Op type(int type)
    {
      return TYPES.get(type);
    }

    public static Op code(int code)
    {
      return CODES.get(code);
    }

    public boolean isMove()
    {
      return type == MOVE.type;
    }

    public boolean isLine()
    {
      return type == LINE.type;
    }

    public boolean isQuad()
    {
      return type == QUAD.type;
    }

    public boolean isCubic()
    {
      return type == CUBIC.type;
    }

    public boolean isClose()
    {
      return type == CLOSE.type;
    }

    public int type()
    {
      return type;
    }

    public char code()
    {
      return code;
    }

    public int size()
    {
      return size;
    }
  }

  public Rectangle3 refreshBounds()
  {
    return this.bounds = new Rectangle3(this.getBounds2D());
  }

  public Rectangle3 bounds()
  {
    return bounds == null ? refreshBounds() : bounds;
  }

  public Point3 origin()
  {
    PathIterator it = getPathIterator(null);
    double[] p = new double[6];
    Op op = Op.type(it.currentSegment(p));
    return new Point3(p[0], p[1]);
  }

  public Path3 toOrigin()
  {
    Point3 origin = origin();
    return this.transform(new Transform3(1, 0, 0, 1, -origin.x(), -origin.y()));
  }

  @Override
  public Path3 copy()
  {
    return new Path3(this);
  }

  public boolean isNonZero()
  {
    return this.getWindingRule() == Path3.WIND_NON_ZERO;
  }

  public String wind()
  {
    return wind(this.getWindingRule());
  }

  public static String wind(int wind)
  {
    return wind == PathIterator.WIND_NON_ZERO ? NONZERO : EVENODD;
  }

  public static int wind(String wind)
  {
    return wind == null ? PathIterator.WIND_NON_ZERO : wind.equals(EVENODD) ? PathIterator.WIND_EVEN_ODD : PathIterator.WIND_NON_ZERO;
  }

  public Path3 scaleX(double scale)
  {
    return new Path3(this, new Transform3(scale, 0, 0, 1, 0, 0));
  }

  public Path3 scaleY(double scale)
  {
    return new Path3(this, new Transform3(1, 0, 0, scale, 0, 0));
  }

  public Path3 scale(double scaleX, double scaleY)
  {
    return new Path3(this, new Transform3(scaleX, 0, 0, scaleY, 0, 0));
  }

  public Path3 scale(double scale)
  {
    return this.scale(scale, scale);
  }

  public Path3 translate(Point2D p)
  {
    return this.translate(p.getX(), p.getY());
  }

  public Path3 translate(double dx, double dy)
  {
    return new Path3(Transform3.translateInstance(dx, dy).transform(this));
  }

  public Path3 transform(double... transform)
  {
    return transform(new Transform3(transform));
  }

  public Path3 transform(Transform3 transform)
  {
    return new Path3(this, transform);
  }

  @Override
  public void setExtent(Line3 l)
  {
    Rectangle3 box = this.bounds();
    double sx = l.width() / box.width;
    double sy = l.height() / box.height;
    double nx = box.x * sx;
    double ny = box.y * sy;

    // Zen.LOG.debug(this,".setExtent - path.width="+box, sy)
    this.transform(new AffineTransform(sx, 0, 0, sy, l.x() - nx, l.y() - ny));
    this.refreshBounds();
  }

  public Path3 newExtent(Line3 l)
  {
    Rectangle3 box = this.bounds();
    double sx = l.width() / box.width;
    double sy = l.height() / box.height;
    double nx = box.x * sx;
    double ny = box.y * sy;
    return transform(new Transform3(sx, 0, 0, sy, l.x() - nx, l.y() - ny));
  }

  public boolean equalsPath(Path3 path)
  {
    PathIterator it1 = this.getPathIterator(null);
    PathIterator it2 = path.getPathIterator(null);
    float[] p1 = new float[6];
    float[] p2 = new float[6];
    try
    {
      if (!it1.isDone() && !it2.isDone())
        do
        {
          if (it1.currentSegment(p1) != it2.currentSegment(p2))
            return false;
          if (!Arrays.equals(p1, p2))
            return false;
          it1.next();
          it2.next();
        } while (!it1.isDone() && !it2.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return it1.isDone() == it2.isDone();
  }

  public boolean equalsPath(Path3 path, double precision)
  {
    PathIterator it1 = this.getPathIterator(null);
    PathIterator it2 = path.getPathIterator(null);
    float[] p1 = new float[6];
    float[] p2 = new float[6];
    try
    {
      if (!it1.isDone() && !it2.isDone())
        do
        {
          if (it1.currentSegment(p1) != it2.currentSegment(p2))
            return false;
          if (!Zen.Array.equals(p1, p2, precision))
            return false;
          it1.next();
          it2.next();
        } while (!it1.isDone() && !it2.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return it1.isDone() == it2.isDone();
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    return this.equalsPath((Path3) o);
  }

  @Override
  public int hashCode()
  {
    if (true)
      return super.hashCode();
    int result = 1;
    PathIterator it = getPathIterator(null);
    // result = 31 * result + this.getWindingRule();
    try
    {
      do
      {
        double[] p = new double[6];
        result = 31 * result + it.currentSegment(p);
        result = 31 * result + Arrays.hashCode(p);
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return result;
  }

  public String signature()
  {
    Rectangle3 r = this.bounds();
    return (int) r.x + "_" + (int) r.y + "_" + (int) r.width + "_" + (int) r.height + "_" + ((long) hashCode() - (long) Integer.MIN_VALUE);
  }

  private String real(float value)
  {
    return "" + (((int) (value * 1000)) / 1000f);
  }

  public String stringValue()
  {
    return stringValue(0.001);
  }

  public String stringValue(double precision)
  {
    StringBuilder sb = new StringBuilder();
    float ox = 0f;
    float oy = 0f;
    this.x = 0f;
    this.y = 0f;
    PathIterator it = this.getPathIterator(null);
    float[] p = new float[6];
    try
    {
      do
      {
        Op op = Op.type(it.currentSegment(p));
        sb.append(op.code());
        switch (op)
        {
        case MOVE:
          sb.append(" ").append(real(p[0] - this.x));
          sb.append(" ").append(real(p[1] - this.y));
          ox = p[0];
          oy = p[1];
          this.x = p[0];
          this.y = p[1];
          break;
        case LINE:
          sb.append(" ").append(real(p[0] - this.x));
          sb.append(" ").append(real(p[1] - this.y));
          this.x = p[0];
          this.y = p[1];
          break;
        case QUAD:
          sb.append(" ").append(real(p[0] - this.x));
          sb.append(" ").append(real(p[1] - this.y));
          sb.append(" ").append(real(p[2] - this.x));
          sb.append(" ").append(real(p[3] - this.y));
          this.x = p[2];
          this.y = p[3];
          break;
        case CUBIC:
          sb.append(" ").append(real(p[0] - this.x));
          sb.append(" ").append(real(p[1] - this.y));
          sb.append(" ").append(real(p[2] - this.x));
          sb.append(" ").append(real(p[3] - this.y));
          sb.append(" ").append(real(p[4] - this.x));
          sb.append(" ").append(real(p[5] - this.y));
          this.x = p[4];
          this.y = p[5];
          break;
        case CLOSE:
          this.x = ox;
          this.y = oy;
          break;
        }
        it.next();
        if (!it.isDone())
          sb.append(" ");
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public FxPath fx()
  {
    FxPath path = new FxPath();
    path.setFillRule(this.getWindingRule() == Path3.WIND_EVEN_ODD ? FillRule.EVEN_ODD : FillRule.NON_ZERO);
    ObservableList<PathElement> elements = path.getElements();
    PathIterator it = this.getPathIterator(null);
    float[] p = new float[6];
    try
    {
      do
      {
        Op op = Op.type(it.currentSegment(p));
        switch (op)
        {
        case MOVE:
          elements.add(new MoveTo(p[0], p[1]));
          break;
        case LINE:
          elements.add(new LineTo(p[0], p[1]));
          break;
        case QUAD:
          elements.add(new QuadCurveTo(p[0], p[1], p[2], p[3]));
          break;
        case CUBIC:
          elements.add(new CubicCurveTo(p[0], p[1], p[2], p[3], p[4], p[5]));
          break;
        case CLOSE:
          elements.add(new ClosePath());
          break;
        }
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return path;
  }

  public int nbOfSegments(boolean countMove, boolean countClose)
  {
    int counter = 0;
    PathIterator it = this.getPathIterator(null);
    float[] p = new float[6];
    try
    {
      do
      {
        Op op = Op.type(it.currentSegment(p));
        switch (op)
        {
        case MOVE:
          if (countMove)
            counter++;
          break;
        case LINE:
          counter++;
          break;
        case QUAD:
          counter++;
          break;
        case CUBIC:
          counter++;
          break;
        case CLOSE:
          if (countClose)
            counter++;
          break;
        }
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return counter;
  }

  public boolean isBBox(double precision)
  {
    float[] dx = new float[4];
    float[] dy = new float[4];
    int index = 0;
    float lastX = 0f;
    float lastY = 0f;
    PathIterator it = this.getPathIterator(null);
    float[] p = new float[6];
    try
    {
      do
      {
        Op op = Op.type(it.currentSegment(p));
        // OCD.LOG.debug(this,
        // ".isBBox - index="+index+" "+op.toString()+"
        // "+Zen.Array.toString(p));
        switch (op)
        {
        case MOVE:
          if (index != 0)
            return false;
          lastX = p[0];
          lastY = p[1];
          break;
        case LINE:
          if (index > 3)
            return false;
          dx[index] = p[0] - lastX;
          dy[index] = p[1] - lastY;
          lastX = p[0];
          lastY = p[1];
          // OCD.LOG.debug(this, ".isBBox - px="+dx[index]+" py="+dy[index]);
          if (Math.abs(dx[index]) > precision && Math.abs(dy[index]) > precision)
            return false;
          index++;
          break;
        case QUAD:
          return false;
        case CUBIC:
          return false;
        case CLOSE:
          if (index == 3)
          {
            dx[3] = -dx[1];
            dy[3] = -dy[1];
            index++;
          }
        }
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
    return Math.abs(dx[0] + dx[1] + dx[2] + dx[3]) < precision && Math.abs(dy[0] + dy[1] + dy[2] + dy[3]) < precision;
  }

  public boolean isMultiBBox(double precision)
  {
    PathIterator pi = this.getPathIterator(null);
    float[] p = new float[6];
    Path3 sub = null;
    try
    {
      while (!pi.isDone())
      {
        int op = pi.currentSegment(p);
        if (op == PathIterator.SEG_MOVETO)
        {
          if (sub != null && !sub.isBBox(precision))
            return false;
          sub = new Path3(this.getWindingRule());
        }
        sub.append(op, p);
        pi.next();
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return sub == null ? this.isBBox(precision) : sub.isBBox(precision);
  }

  public Path3 first()
  {
    PathIterator pi = this.getPathIterator(null);
    float[] p = new float[6];
    Path3 sub = null;
    try
    {
      while (!pi.isDone())
      {
        int op = pi.currentSegment(p);
        if (op == PathIterator.SEG_MOVETO)
          if (sub == null)
            sub = new Path3(this.getWindingRule());
          else
            return sub;
        sub.append(op, p);
        pi.next();
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return sub == null ? this : sub;
  }

  public List3<Path3> split()
  {
    List3<Path3> list = new List3<Path3>();
    PathIterator pi = this.getPathIterator(null);
    float[] p = new float[6];
    Path3 sub = null;
    try
    {
      while (!pi.isDone())
      {
        int op = pi.currentSegment(p);
        if (op == PathIterator.SEG_MOVETO)
        {
          sub = new Path3(this.getWindingRule());
          list.add(sub);
        }
        sub.append(op, p);
        pi.next();
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return list;
  }

  public int nbOfSubPaths()
  {
    int counter = 0;
    PathIterator pi = this.getPathIterator(null);
    float[] p = new float[6];
    try
    {
      while (!pi.isDone())
      {
        if (pi.currentSegment(p) == PathIterator.SEG_MOVETO)
          counter++;
        pi.next();
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return counter;
  }

  public Path3 closeSubpaths(double epsilon)
  {
    Path3 path = new Path3(this.getWindingRule());
    Point3 move = null;
    Point3 last = null;
    Seg[] segs = this.closeSubpaths().segments();
    for (int i = 0; i < segs.length; i++)
    {
      Seg seg = segs[i];
      Point3 p = seg.p();

      // if next points gets back on last move... means we want to close
      boolean closing = move != null && move.equals(p, epsilon);
      // if next points does not move at all relatively to the last one, we
      // ignore it
      boolean jam = !closing && last != null && last.equals(p, epsilon / 10);

      closing = false;
      jam = false;

      switch (seg.op)
      {
      case MOVE:
        path.moveTo(move = p);
        break;
      case LINE:
        // if jam we ignore, if closing then close op will do it because
        // this.closeSubpaths() ensures it
        if (!closing && !jam)
          path.lineTo(p);
        break;
      case QUAD:
        if (!jam) //
          path.quadTo(seg.c0(), closing ? move : p);
        break;
      case CUBIC:
        if (!jam)
          path.curveTo(seg.c0(), seg.c1(), closing ? move : p);
        break;
      case CLOSE:
        path.closePath();
        break;
      }
      last = p;
    }

    return path;
  }

  public Path3 toCubicPath()
  {
    // algo from http://fontforge.sourceforge.net/bezier.html
    float f23 = 2f / 3f;
    PathIterator it = this.getPathIterator(null);
    Path3 path = new Path3(this.getWindingRule());
    float[] p = new float[6];
    float x0 = 0f;
    float y0 = 0f;
    try
    {
      do
      {
        switch (Op.type(it.currentSegment(p)))
        {
        case MOVE:
          path.moveTo(p[0], p[1]);
          x0 = p[0];
          y0 = p[1];
          break;
        case LINE:
          path.lineTo(p[0], p[1]);
          x0 = p[0];
          y0 = p[1];
          break;
        case QUAD:
          float c1x = x0 + f23 * (p[0] - x0);
          float c1y = y0 + f23 * (p[1] - y0);
          float c2x = p[2] + f23 * (p[0] - p[2]);
          float c2y = p[3] + f23 * (p[1] - p[3]);
          path.curveTo(c1x, c1y, c2x, c2y, p[2], p[3]);
          x0 = p[2];
          y0 = p[3];
          break;
        case CUBIC:
          path.curveTo(p[0], p[1], p[2], p[3], p[4], p[5]);
          x0 = p[4];
          y0 = p[5];
          break;
        case CLOSE:
          path.closePath();
          break;
        }
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return path;
  }

  public Path3 toQuadraticPath()
  {
    // algo from sun repository
    // http://svn.apache.org/repos/asf/xmlgraphics/fop/trunk/src/java/org/apache/fop/afp/util/CubicBezierApproximator.java
    double r34 = 3.0 / 4.0;
    double r38 = 3.0 / 8.0;

    PathIterator it = this.getPathIterator(null);
    Path3 path = new Path3(this.getWindingRule());
    float[] p = new float[6];
    float x0 = 0f;
    float y0 = 0f;
    try
    {
      do
      {
        switch (Op.type(it.currentSegment(p)))
        {
        case MOVE:
          path.moveTo(p[0], p[1]);
          x0 = p[0];
          y0 = p[1];
          break;
        case LINE:
          path.lineTo(p[0], p[1]);
          x0 = p[0];
          y0 = p[1];
          break;
        case QUAD:
          path.quadTo(p[0], p[1], p[2], p[3]);
          x0 = p[2];
          y0 = p[3];
          break;
        case CUBIC:
          double x1 = p[0];
          double y1 = p[1];
          double x2 = p[2];
          double y2 = p[3];
          double x3 = p[4];
          double y3 = p[5];
          // some useful base points
          double ax = rx(x0, x1, r34);
          double ay = ry(y0, y1, r34);
          double bx = rx(x3, x2, r34);
          double by = ry(y3, y2, r34);
          // delta of start-end segment
          double dx = (x3 - x0) / 16.0;
          double dy = (y3 - y0) / 16.0;
          // first control point
          double c1x = rx(x0, x1, r38);
          double c1y = ry(y0, y1, r38);
          // second control point
          double c2x = rx(ax, bx, r38) - dx;
          double c2y = ry(ay, by, r38) - dy;
          // thirs control point
          double c3x = rx(bx, ax, r38) + dx;
          double c3y = ry(by, ay, r38) + dy;
          // fourth control point
          double c4x = rx(x3, x2, r38);
          double c4y = ry(y3, y2, r38);
          // anchor points
          double a1x = rx(c1x, c2x, 0.5);
          double a1y = ry(c1y, c2y, 0.5);
          double a2x = rx(ax, bx, 0.5);
          double a2y = ry(ay, by, 0.5);
          double a3x = rx(c3x, c4x, 0.5);
          double a3y = ry(c3y, c4y, 0.5);

          path.quadTo(c1x, c1y, a1x, a1y);
          path.quadTo(c2x, c2y, a2x, a2y);
          path.quadTo(c3x, c3y, a3x, a3y);
          path.quadTo(c4x, c4y, x3, y3);

          x0 = p[4];
          y0 = p[5];
          break;
        case CLOSE:
          path.closePath();
          break;
        }
        it.next();
      } while (!it.isDone());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return path;
  }

  //
  // public Path3 toPolygon()
  // {
  // Path3 p = this.toZoubiQuadraticPath();
  // double e = 0.0001;
  //
  // int counter = 0;
  // PathIterator it = this.getPathIterator(null);
  // Path3 path = new Path3(this.getWindingRule());
  // float[] p = new float[6];
  // Point3 p0 = new Point3();
  // Point3 p1 = new Point3();
  // Point3 p2 = new Point3();
  // Point3 p3 = new Point3();
  // do
  // {
  // switch (Op.type(it.currentSegment(p)))
  // {
  // case MOVE:
  // path.moveTo(p[0], p[1]);
  // p0.setLocation(p[0], p[1]);
  // break;
  // case LINE:
  // path.lineTo(p[0], p[1]);
  // p0.setLocation(p[0], p[1]);
  // break;
  // case QUAD:
  // path.quadTo(p[0], p[1], p[2], p[3]);
  // p0.setLocation(p[2], p[3]);
  // break;
  // case CUBIC:
  // // using wikipedia variable names
  // // http://en.wikipedia.org/wiki/B%C3%A9zier_curve
  // // cubic control points points
  // // p0 is already defined
  // p1.setLocation(p[0], p[1]);
  // p2.setLocation(p[2], p[3]);
  // p3.setLocation(p[4], p[5]);
  //
  // // if p1==p2, then cubic is equivalent to a quadratic
  // if (p1.distanceSq(p2) < e)
  // path.quadTo(p1.x, p2.x, p[4], p[5]);
  // else
  // {
  // // cubic control points mid points
  // Point3 q0 = midPoint(p0, p1);
  // Point3 q1 = midPoint(p1, p2);
  // Point3 q2 = midPoint(p2, p3);
  //
  // // line segment a to b
  // Point3 r0 = midPoint(q0, q1);
  // Point3 r1 = midPoint(q1, q2);
  // // cubic "mid" point
  // Point3 b = midPoint(r0, r1);
  //
  // Point3 c0 = new Point3();
  // if (Math.abs(p0.x - p1.x) < e)
  // {
  // c0.x = p0.x;
  // float mr = slope(r0, r1);
  // c0.y = mr * (c0.x - r0.x) + r0.y;
  // } else if (Math.abs(r0.x - r1.x) < e)
  // {
  // c0.x = r0.x;
  // float mp = slope(p0, p1);
  // c0.y = mp * (c0.x - p0.x) + p0.y;
  // } else
  // {
  // float mp = slope(p0, p1);
  // float mr = slope(r0, r1);
  // c0.x = (mp * p0.x - p0.y - mr * r0.x + r0.y) / (mp - mr);
  // c0.y = mp * (c0.x - p0.x) + p0.y;
  // }
  //
  // Point3 c3 = new Point3();
  // if (Math.abs(p3.x - p2.x) < e)
  // {
  // c3.x = p3.x;
  // float mr = slope(r0, r1);
  // c3.y = mr * (c3.x - r0.x) + r0.y;
  // } else if (Math.abs(r0.x - r1.x) < e)
  // {
  // c3.x = r0.x;
  // float mp = slope(p3, p2);
  // c3.y = mp * (c3.x - p3.x) + p3.y;
  // } else
  // {
  // float mp = slope(p3, p2);
  // float mr = slope(r0, r1);
  // c3.x = (mp * p3.x - p3.y - mr * r0.x + r0.y) / (mp - mr);
  // c3.y = mp * (c3.x - p3.x) + p3.y;
  // }
  //
  // path.quadTo(c0.x, c0.y, b.x, b.y);
  // path.quadTo(c3.x, c3.y, p[4], p[5]);
  //
  // // visual debugging
  // if (g != null)
  // {
  // int r = 3;
  // g.draw(new Line3(p0, p1), Color3.GRAY);
  // g.fill(new Circle3(p0, r), Color3.GRAY);
  // g.draw(new Line3(p3, p2), Color3.GRAY);
  // g.fill(new Circle3(p[0], p[1], r), Color3.GRAY);
  // g.fill(new Circle3(p[2], p[3], r), Color3.GRAY);
  // g.fill(new Circle3(p[4], p[5], r), Color3.GRAY);
  // g.draw(new Line3(r0, r1), Color3.BLUE);
  // g.fill(new Circle3(b, r), Color3.BLUE);
  // // g.fill(new Circle3(q0, r), Color3.GREEN);
  // // g.fill(new Circle3(q1, r), Color3.GREEN);
  // // g.fill(new Circle3(q2, r), Color3.GREEN);
  //
  // g.fill(new Circle3(c0, r), Color3.CARROT_ORANGE);
  // g.draw(" c " + counter, c0.x, c0.y, null, Color3.CARROT_ORANGE);
  // g.fill(new Circle3(c3, r), Color3.CARROT_ORANGE);
  // g.draw(" c' " + counter, c3.x, c3.y, null, Color3.CARROT_ORANGE);
  // g.fill(new Circle3(r0, r), Color3.BLUE);
  // g.fill(new Circle3(r1, r), Color3.BLUE);
  // g.draw(" b " + counter, b.x, b.y, null, Color3.BLUE);
  // g.circle(p[4], p[5], 2 * counter + 5, Color3.YELLOW_GREEN);
  // counter++;
  // }
  // }
  // p0.setLocation(p[4], p[5]);
  // break;
  // case CLOSE:
  // path.closePath();
  // break;
  // }
  // it.next();
  // } while (!it.isDone());
  // return path;
  //
  // }
  //

  public Path3 toZoubiQuadraticPath()
  {
    return this.toZoubiQuadraticPath(null);
  }

  public Path3 toZoubiQuadraticPath(Graphics3 g)
  {
    double e = 0.0001;
    double me = 0.1;

    int counter = 0;
    PathIterator it = this.getPathIterator(null);
    Path3 path = new Path3(this.getWindingRule());
    float[] p = new float[6];
    Point3 p0 = new Point3();
    Point3 p1 = new Point3();
    Point3 p2 = new Point3();
    Point3 p3 = new Point3();
    float mp;
    float mr;
    Point3 q0;
    Point3 q1;
    Point3 q2;
    // line segment a to b
    Point3 r0;
    Point3 r1;
    // cubic "mid" point
    Point3 b;
    Point3 c0;
    
    try
    {
      do
      {
        switch (Op.type(it.currentSegment(p)))
        {
        case MOVE:
          path.moveTo(p[0], p[1]);
          p0.setLocation(p[0], p[1]);
          break;
        case LINE:
          if (!p0.equals(p[0], p[1], e))
          {
            path.lineTo(p[0], p[1]);
            p0.setLocation(p[0], p[1]);
          }
          break;
        case QUAD:
          if (!p0.equals(p[2], p[3], e))
          {
            path.quadTo(p[0], p[1], p[2], p[3]);
            p0.setLocation(p[2], p[3]);
          }
          break;
        case CUBIC:
          // using wikipedia variable names
          // http://en.wikipedia.org/wiki/B%C3%A9zier_curve
          // cubic control points points
          // p0 is already defined
          p1.setLocation(p[0], p[1]);
          p2.setLocation(p[2], p[3]);
          p3.setLocation(p[4], p[5]);

          // if p1==p2, then cubic is equivalent to a quadratic
          if (p1.distanceSq(p2) < e)
          {
            if (!p0.equals(p[4], p[5], e))
            {
              path.quadTo(p2.x, p2.y, p[4], p[5]);
            }
          } else
          {
            // cubic control points mid points
            q0 = midPoint(p0, p1);
            q1 = midPoint(p1, p2);
            q2 = midPoint(p2, p3);

            // line segment a to b
            r0 = midPoint(q0, q1);
            r1 = midPoint(q1, q2);
            // cubic "mid" point
            b = midPoint(r0, r1);

            c0 = new Point3();
            if (Math.abs(p0.x - p1.x) < e)
            {
              c0.x = p0.x;
              mr = slope(r0, r1);
              c0.y = mr * (c0.x - r0.x) + r0.y;
            } else if (Math.abs(r0.x - r1.x) < e)
            {
              c0.x = r0.x;
              mp = slope(p0, p1);
              c0.y = mp * (c0.x - p0.x) + p0.y;
            } else
            {
              mp = slope(p0, p1);
              mr = slope(r0, r1);

              if (Math.abs(mp - mr) < me)
              {
                c0.x = (p0.x + r0.x) / 2;
                c0.y = (p0.y + r0.y) / 2;
              } else
              {
                c0.x = (mp * p0.x - p0.y - mr * r0.x + r0.y) / (mp - mr);
                c0.y = mp * (c0.x - p0.x) + p0.y;
              }
            }

            Point3 c3 = new Point3();
            if (Math.abs(p3.x - p2.x) < e)
            {
              c3.x = p3.x;
              mr = slope(r0, r1);
              c3.y = mr * (c3.x - r0.x) + r0.y;
            } else if (Math.abs(r0.x - r1.x) < e)
            {
              c3.x = r0.x;
              mp = slope(p3, p2);
              c3.y = mp * (c3.x - p3.x) + p3.y;
            } else
            {

              mp = slope(p3, p2);
              mr = slope(r0, r1);

              if (Math.abs(mp - mr) < me)
              {
                c3.x = (p3.x + r0.x) / 2;
                c3.y = (p3.y + r0.y) / 2;
              } else
              {
                c3.x = (mp * p3.x - p3.y - mr * r0.x + r0.y) / (mp - mr);
                c3.y = mp * (c3.x - p3.x) + p3.y;
              }
            }

            if (!p0.equals(b.x, b.y, e))
            {
              path.quadTo(c0.x, c0.y, b.x, b.y);
            }
            // path.lineTo(b.x, b.y);
            if (!p0.equals(p[4], p[5], e))
            {
              path.quadTo(c3.x, c3.y, p[4], p[5]);
            }
            // path.lineTo(p[4], p[5]);
            // debugImage(path, 600, 600, File3.desktop("tmp/path-" +
            // debugCounter
            // + "-" + counter + ".png"));

            // path.lineTo(p[4], p[5]);
            // visual debugging
            if (g != null)
            {
              int r = 3;
              g.draw(new Line3(p0, p1), Color3.GRAY);
              g.fill(new Circle3(p0, r), Color3.GRAY);
              g.draw(new Line3(p3, p2), Color3.GRAY);
              g.fill(new Circle3(p[0], p[1], r), Color3.GRAY);
              g.fill(new Circle3(p[2], p[3], r), Color3.GRAY);
              g.fill(new Circle3(p[4], p[5], r), Color3.GRAY);
              g.draw(new Line3(r0, r1), Color3.BLUE);
              g.fill(new Circle3(b, r), Color3.BLUE);

              // g.fill(new Circle3(q0, r), Color3.AMBER);
              // g.fill(new Circle3(q1, r), Color3.AMBER);
              // g.fill(new Circle3(q2, r), Color3.AMBER);

              g.fill(new Circle3(c0, r), Color3.CARROT_ORANGE);
              g.draw(" c " + counter, c0.x, c0.y, null, Color3.CARROT_ORANGE);
              g.fill(new Circle3(c3, r), Color3.CARROT_ORANGE);
              g.draw(" c' " + counter, c3.x, c3.y, null, Color3.CARROT_ORANGE);
              g.fill(new Circle3(r0, r), Color3.BLUE);
              g.fill(new Circle3(r1, r), Color3.BLUE);
              g.draw(" b " + counter, b.x, b.y, null, Color3.BLUE);
              g.circle(p[4], p[5], 2 * counter + 5, Color3.YELLOW_GREEN);
              counter++;
            }
          }
          p0.setLocation(p[4], p[5]);
          break;
        case CLOSE:
          path.closePath();
          break;
        }
        it.next();

        // debugImage(path, 600, 600, File3.desktop("tmp/path-" + debugCounter +
        // "-" + counter + ".png"));
        counter++;
      } while (!it.isDone());
    } catch (Exception ee)
    {
      ee.printStackTrace();
    }
    debugCounter++;
    return path;
  }
  
  public Image3 image(double scale)
  {
    Rectangle3 box = this.bounds();     
    Image3 image = new Image3(Math.max(box.width*scale,1), Math.max(box.intHeight()*scale,1));
    Graphics3 g=image.graphics();
    g.clearWhite();
    image.graphics().paint(this.shift(box.origin(),  true).scale(scale),  Color3.BLACK,  null);    
    return image;
  }

  @Override
  public String toString()
  {
    return this.stringValue();
  }

  private static float slope(Point3 p0, Point3 p1)
  {
    return (p1.y - p0.y) / (p1.x - p0.x);
  }

  private static Point3 midPoint(Point3 p0, Point3 p1)
  {
    return new Point3((p0.x + p1.x) / 2f, (p0.y + p1.y) / 2f);
  }

  private static double rx(double x0, double x1, double ratio)
  {
    return x0 + (x1 - x0) * ratio;
  }

  private static double ry(double y0, double y1, double ratio)
  {
    return y0 + (y1 - y0) * ratio;
  }

  public static void main(String... args)
  {
    debugQuadPath();
  }

  public static void debugImage(Path3 path, int w, int h, File3 file)
  {
    Image3 image = new Image3(w, h);
    debugGraphics(path, image.graphics());
    image.write(file);
  }

  public static void debugGraphics(Path3 path, Graphics3 g)
  {
    int gap = 20;
    g.clearWhite();
    g.setStroke(1.0);
    Rectangle3 box = path.refreshBounds();
    double scale = Math.min((g.width() - 2 * gap) / box.width, (g.height() - 2 * gap) / box.height);
    path = new Transform3(scale, 0, 0, scale, 0, 0).transform(path);
    box = path.bounds();
    path = path.translate(-box.x + gap, -box.y + gap);

    Seg[] segs = path.segments();
    Seg prev = null;
    for (int i = 0; i < segs.length; i++)
    {
      Seg seg = segs[i];
      Point3 p = seg.p();
      Point3 o = seg.op.isQuad() ? p.lineTo(seg.c0()).interpolate(0.2) : prev == null ? p : p.lineTo(prev.p()).interpolate(0.2);
      g.circle(p.x(), p.y(), 4, seg.op.isMove() ? Color3.RED.alpha(0.5) : Color3.GREEN_DARK.alpha(0.5));
      if (seg.op.isQuad())
      {
        double cx = seg.c0x();
        double cy = seg.c0y();
        g.line(p.x(), p.y(), cx, cy, Color3.BLUE_PIGMENT.alpha(0.4), 1);
        if (prev != null)
          g.line(prev.x(), prev.y(), cx, cy, Color3.BLUE_PIGMENT.alpha(0.4), 1);
        g.circle(cx, cy, 3, Color3.BLUE_PIGMENT.alpha(0.5));
      }

      if (!seg.op.isMove())
        g.drawTo(i + " " + seg.op.code, o.x(), o.y(), Font3.GUI_FONT.deriveFont(8), Color3.BLACK);
      prev = seg;
    }

    g.setColor(Color3.BLACK);
    g.draw(path, Color3.RED.alpha(1));
  }

  public void debugFrame(final int w, final int h)
  {

    final Panel3 panel = new Panel3()
    {
      @Override
      public void paintComponent3(Graphics3 g)
      {
        debugGraphics(Path3.this, g);
      }
    };

    panel.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e)
      {
        panel.repaint();
      }
    });
    Frame3 frame = new Frame3("Zoubi Quadratic Path", w, h);
    frame.setMinimumSize(new Dimension(20, 20));
    frame.setPanel(panel);
    frame.display();

  }

  public static void debugQuadPath(String... args)
  {
    final Panel3 panel = new Panel3()
    {
      @Override
      public void paintComponent3(Graphics3 g)
      {
        int size = g.intWidth() / 2;
        g.clearWhite();

        Path3 path;
        g.setStroke(3.0);
        g.setColor(Color3.RED.alpha(0.5));

        // path = g.outline("Hello World", Font3.CALIBRI_FONT.size(size / 5f));
        // //System.out.println(path.stringValue());
        // path = path.translate(20, 100);
        // g.paint(path,Color3.RED.alpha(0.5), Color3.RED.alpha(0.5));
        // path = path.translate(0, 100);
        // g.paint(path.toZoubiQuadraticPath(g),Color3.RED.alpha(0.5),
        // Color3.RED.alpha(0.5));
        // path = path.translate(0, 100);
        // g.paint(path.toQuadraticPath(), Color3.RED.alpha(0.5),
        // Color3.RED.alpha(0.5));
        //

        path = new Path3(new Circle3(0, 0, size / 2));
        path = new Transform3(0.5, 1, 0, 1.1, 0, 0).transform(path);
        path = path.translate(size / 2, size);
        g.setColor(Color3.BLACK);
        path = path.translate(size / 2, 0);
        g.draw(path.toZoubiQuadraticPath(g), Color3.RED.alpha(0.5));
      }
    };

    panel.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e)
      {
        panel.repaint();
      }
    });

    Frame3 frame = new Frame3("Zoubi Quadratic Path", 650, 650);
    frame.setMinimumSize(new Dimension(20, 20));
    frame.setPanel(panel);

    frame.display();

  }

  public static void debugIsBBox(String... args)
  {
    Path3 path = new Path3();
    path.moveTo(100, 100);
    path.lineTo(400, 100);
    path.lineTo(400, 200);
    path.lineTo(100, 200);
    // path.lineTo(100,100);
    path.closePath();
    Log.debug(Path3.class, ".main - isBBox: " + path.isBBox(0.001));
  }
}
