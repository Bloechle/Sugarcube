package sugarcube.common.graphics.geom;

import javafx.scene.transform.Affine;
import sugarcube.common.data.Zen;
import sugarcube.common.ui.fx.base.FxTransform;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Transform3 extends AffineTransform implements Cloneable, Serializable
{
  public static final int ROUND = 1000000;
  public static final Transform3 IDENTITY = new Transform3();

  public Transform3()
  {
    this(1, 0, 0, 1, 0, 0);
  }

  public Transform3(Affine affine)
  {
    this(affine.getMxx(), affine.getMyx(), affine.getMxy(), affine.getMyy(), affine.getTx(), affine.getTy());
  }

  public Transform3(float[] a)
  {
    this(Zen.Array.toDoubles(a));
  }

  // 4 values means no shear, 2 values means only scales, 1 value is
  // proportional scaling
  public Transform3(double... a)
  {
    super(a.length == 6 ? a : a.length == 4 ? new double[]
    { a[0], 0, 0, a[1], a[2], a[3] } : a.length == 2 ? new double[]
    { a[0], 0, 0, a[1], 0, 0 } : a.length == 1 ? new double[]
    { a[0], 0, 0, a[0], 0, 0 } : new double[]
    { 1, 0, 0, 1, 0, 0 });
  }

  public Transform3(AffineTransform at)
  {
    super(at);
  }

  public Transform3(double scale, Point2D pos)
  {
    this(scale, 0, 0, scale, pos.getX(), pos.getY());
  }

  public Transform3 concat(double... d)
  {
    return this.concat(new Transform3(d[0], d[1], d[2], d[3], d[4], d[5]));
  }

  public Transform3 concat(AffineTransform tm)
  {
    Transform3 concat = new Transform3(this);
    concat.concatenate(tm);
    return new Transform3(concat);
  }

  public Transform3 preconcat(double... d)
  {
    return this.preconcat(new Transform3(d[0], d[1], d[2], d[3], d[4], d[5]));
  }

  public Transform3 preconcat(AffineTransform tm)
  {
    Transform3 concat = new Transform3(this);
    concat.preConcatenate(tm);
    return new Transform3(concat);
  }

  public double[] doubleValues()
  {
    return Zen.Array.doubles(getScaleX(), getShearY(), getShearX(), getScaleY(), getTranslateX(), getTranslateY());
  }

  public float[] floatValues()
  {
    return Zen.Array.toFloats(getScaleX(), getShearY(), getShearX(), getScaleY(), getTranslateX(), getTranslateY());
  }

  // public float[] trimmedValues()
  // {
  // if ((int) (ROUND * x()) == 0 && (int) (ROUND * y()) == 0)
  // if ((int) (ROUND * shearX()) == 0 && (int) (ROUND * shearY()) == 0)
  // return (int) (ROUND * scaleX()) == (int) (ROUND * scaleY()) ?
  // Zen.Array.toFloats(scaleX()) : Zen.Array.toFloats(scaleX(), scaleY());
  // else
  // return Zen.Array.toFloats(scaleX(), shearY(), shearX(), scaleY());
  // else
  // return floatValues();
  // }
  public double scaleRatioXY()
  {
    return this.scaleX() / this.scaleY();
  }

  public boolean isNonUniformedScaled(double epsilon)
  {
    return !Zen.Epsilon.is(scaleX(), scaleY(), epsilon);
  }

  public boolean isNonUniformedScaled()
  {
    return scaleX() != scaleY();
  }

  public boolean isSheared(double epsilon)
  {
    return !Zen.Epsilon.isZero(shearX(), epsilon) || !Zen.Epsilon.isZero(shearY(), epsilon);
  }

  public boolean isMirrored()
  {
    return sx() < 0 || sy() < 0;
  }

  // public boolean isIdentity()
  // {
  // return scaleX == 1f && shearY == 0f && shearX == 0f && scaleY == 1f &&
  // deltaX == 0f && deltaY == 0f;
  // }
  public boolean isScaledOrSheared()
  {
    return this.isScaled() || this.isSheared();
  }

  public boolean isScaledOrSheared(double epsilon)
  {
    return this.isScaled(epsilon) || this.isSheared(epsilon);
  }

  public boolean isSheared()
  {
    return this.getShearX() != 0.0 || this.getShearY() != 0.0;
  }

  public boolean isTranslated()
  {
    return this.getTranslateX() != 0.0 || this.getTranslateY() != 0.0;
  }

  public boolean isTranslated(double epsilon)
  {
    return !Zen.Epsilon.is(x(), 0, epsilon) || !Zen.Epsilon.is(y(), 0, epsilon);
  }

  public boolean isScale(double scale)
  {
    return scaleX() == scale && scaleY() == scale && shearX() == 0.0 && shearY() == 0.0 && x() == 0.0 && y() == 0.0;
  }

  public boolean isScaled(double epsilon)
  {
    return !Zen.Epsilon.is(getScaleX(), 1, epsilon) || !Zen.Epsilon.is(getScaleY(), 1, epsilon);
  }

  public boolean isScaled()
  {
    return this.getScaleX() != 1.0 || this.getScaleY() != 0.0;
  }

  public boolean isIdentity(double epsilon)
  {
    return super.isIdentity() || !(isSheared(epsilon) || isScaled(epsilon) || isTranslated(epsilon));
  }

  public Transform3 copy()
  {
    return new Transform3(getScaleX(), getShearY(), getShearX(), getScaleY(), getTranslateX(), getTranslateY());
  }

  public Dimension3 transform(Dimension2D dim)
  {
    return dim == null ? null : new Dimension3(transform(dim.getWidth(), dim.getHeight()));
  }

  public Path3 transform(Shape shape)
  {
    return shape == null ? null : new Path3(shape, this);
  }

  public Shape shapeTransform(Shape shape)
  {
    return shape == null ? null : shape;
  }

  public Point2D transform(Point2D p)
  {
    return (Point2D) super.transform(p, new Point2D.Float());
  }

  public Point3 transform(Point3 p)
  {
    return (Point3) super.transform(p, new Point3());
  }

  public Line3 transform(Line3 l)
  {
    return new Line3(transform(l.getP1()), transform(l.getP2()));
  }

  public Point3 transform(double x, double y)
  {
    Point3 p = new Point3(x, y);
    return (Point3) super.transform(p, p);
  }

  public Coords transform(Coords coords)
  {
    float[] points = new float[coords.floats().length];
    super.transform(coords.floats(), 0, points, 0, points.length / 2);
    return new Coords(points);
  }

  @Override
  public Object clone()
  {
    return super.clone();
  }

  public static Transform3 scaleInstance(double scale)
  {
    return new Transform3(getScaleInstance(scale, scale));
  }

  public static Transform3 scaleInstance(double scaleX, double scaleY)
  {
    return new Transform3(getScaleInstance(scaleX, scaleY));
  }

  public static Transform3 scaleAndRotateInstance(double theta, double scaleX, double scaleY)
  {
    AffineTransform at = getRotateInstance(theta);
    at.scale(scaleX, scaleY);
    return new Transform3(at);
  }

  public static Transform3 centerRotateAndScaleInstance(double theta, double ox, double oy, double scaleX, double scaleY)
  {
    Transform3 at = new Transform3();
    at.translate(ox, oy);
    at.scale(scaleX, scaleY);
    at.rotate(theta);
    at.translate(-ox, -oy);
    return new Transform3(at);
  }

  public static Transform3 rotateInstance(double theta)
  {
    return new Transform3(getRotateInstance(theta));
  }

  public static Transform3 rotateInstance(double theta, double ox, double oy)
  {
    return new Transform3(getRotateInstance(theta, ox, oy));
  }

  public static Transform3 translateInstance(double x, double y)
  {
    return new Transform3(getTranslateInstance(x, y));
  }

  public static Transform3 translateInstance(Point2D p)
  {
    return new Transform3(getTranslateInstance(p.getX(), p.getY()));
  }

  public double scaleWidth()
  {
    return this.backRotate().scaleX();
    // return rotateInstance(-rotation()).transform(transform(new XRectangle(0,
    // 0, 1, 1))).getBounds2D().getWidth();
  }

  public double scaleHeight()
  {
    return rotateInstance(-rotation()).transform(transform(new Rectangle3(0, 0, 1, 1))).getBounds2D().getHeight();
  }

  public double rotation()
  {
    Point2D p = new Transform3(scaleX(), shearY(), shearX(), scaleY(), 0, 0).transform(new Point3(1, 0), null);
    return Math.atan2(p.getY(), p.getX()); // radians
  }

  public double degrees()
  {
    return rotation() * 180 / Math.PI;
  }

  public Transform3 rotationInstance()
  {
    return rotateInstance(rotation());
  }

  public Transform3 backRotationInstance()
  {
    return rotateInstance(-rotation());
  }

  public Transform3 backRotate()
  {
    return this.backRotationInstance().concat(this);
  }

  public double scaleX()
  {
    return getScaleX();
  }

  public double sx()
  {
    return getScaleX();
  }

  public double scaleY()
  {
    return getScaleY();
  }

  public double sy()
  {
    return getScaleY();
  }

  public double shearX()
  {
    return getShearX();
  }

  public double hx()
  {
    return getShearX();
  }

  public double shearY()
  {
    return getShearY();
  }

  public double hy()
  {
    return getShearY();
  }

  public double x()
  {
    return getTranslateX();
  }

  public double y()
  {
    return getTranslateY();
  }

  public float fsx()
  {
    return (float) sx();
  }

  public float fsy()
  {
    return (float) sy();
  }

  public float fhx()
  {
    return (float) hx();
  }

  public float fhy()
  {
    return (float) hy();
  }

  public float floatX()
  {
    return (float) x();
  }

  public float floatY()
  {
    return (float) y();
  }

  public float isx()
  {
    return (int) Math.round(sx());
  }

  public float isy()
  {
    return (int) Math.round(sy());
  }

  public float ihx()
  {
    return (int) Math.round(hx());
  }

  public float ihy()
  {
    return (int) Math.round(hy());
  }

  public float ix()
  {
    return (int) Math.round(x());
  }

  public float iy()
  {
    return (int) Math.round(y());
  }

  public Point2D.Double origin()
  {
    return new Point2D.Double(this.getTranslateX(), this.getTranslateY());
  }

  public Transform3 toOrigin()
  {
    return new Transform3(scaleX(), shearY(), shearX(), scaleY(), 0.0, 0.0);
  }

  public Transform3 rescale(double scale)
  {
    return new Transform3(scaleX() * scale, shearY() * scale, shearX() * scale, scaleY() * scale, x(), y());
  }

  public Transform3 translate(Point2D p)
  {
    return this.moveTo(this.x() + p.getX(), this.y() + p.getY());
  }

  public Transform3 translateBack(Point2D p)
  {
    return this.moveTo(this.x() - p.getX(), this.y() - p.getY());
  }

  public Transform3 moveTo(Point2D p)
  {
    return p == null ? this : this.moveTo(p.getX(), p.getY());
  }

  public Transform3 moveTo(double x, double y)
  {
    return new Transform3(scaleX(), shearY(), shearX(), scaleY(), x, y);
  }

  public Transform3 inv()
  {
    return this.inverse();
  }

  public Transform3 inverse()
  {
    try
    {
      return new Transform3(this.createInverse());
    } catch (NoninvertibleTransformException ex)
    {
      ex.printStackTrace();
      return this;
    }
  }

  public FxTransform fx()
  {
    return new FxTransform(sx(), hy(), hx(), sy(), x(), y());
  }

  public static Transform3 create(AffineTransform affine)
  {
    return affine == null ? null : new Transform3(affine);
  }

  public static Transform3 create(Affine affine)
  {
    return affine == null ? null : new Transform3(affine);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    else if (o == null || o.getClass() != this.getClass())
      return false;
    else
      return Zen.Array.equals(this.doubleValues(), ((Transform3) o).doubleValues());
  }

  @Override
  public int hashCode()
  {
    return super.hashCode();
  }

  public String toString(int decimals)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(Zen.toString(scaleX(), decimals)).append(" ");
    sb.append(Zen.toString(shearY(), decimals)).append(" ");
    sb.append(Zen.toString(shearX(), decimals)).append(" ");
    sb.append(Zen.toString(scaleY(), decimals)).append(" ");
    sb.append(Zen.toString(x(), decimals)).append(" ");
    sb.append(Zen.toString(y(), decimals));
    return sb.toString();
  }

  @Override
  public String toString()
  {
    return toString(4);
  }
}
