package sugarcube.common.graphics.geom;

import javafx.geometry.BoundingBox;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.Scan;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Boundable;
import sugarcube.common.interfaces.Boxable;
import sugarcube.common.interfaces.Range2D;
import sugarcube.common.interfaces.XYizable;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.common.data.xml.Nb;
import sugarcube.common.data.xml.XmlDecimalFormat;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * The point of origin is supposed to be upper left, the width and height going
 * right and down...
 */
public class Rectangle3 extends Rectangle2D.Float implements Boundable, Shape3, Range2D, Serializable, Cloneable
{
    public Rectangle3()
    {
        this.x = 0f;
        this.y = 0f;
        this.width = 0f;
        this.height = 0f;
    }

    public Rectangle3(int[] p)
    {
        this(true, p[0], p[1], p[2], p[3]);
    }

    public Rectangle3(double[] p)
    {
        this(true, p[0], p[1], p[2], p[3]);
    }

    public Rectangle3(Point p1, Point p2)
    {
        this(true, p1.x, p1.y, p2.x, p2.y);
    }

    public Rectangle3(Point2D p1, Point2D p2)
    {
        this(true, p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public Rectangle3(Line3 extent)
    {
        this(extent.p1(), extent.p2());
    }

    public Rectangle3(double x, double y, double width, double height)
    {
        this(false, x, y, width, height);
    }

    public Rectangle3(String x, String y, String width, String height)
    {
        this(Nb.toDouble(x), Nb.toDouble(y), Nb.toDouble(width), Nb.toDouble(height));
    }

    public Rectangle3(boolean oppositePoints, double... p)
    {
        if (oppositePoints)
            this.setPoints(p);
        else
            this.set(p);
    }

    public Rectangle3(Shape shape)
    {
        this(shape.getBounds2D());
    }

    public Rectangle3(java.awt.geom.Rectangle2D rectangle)
    {
        this(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public Rectangle3(javafx.geometry.Rectangle2D r)
    {
        this(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
    }

    public FxRect fx()
    {
        return new FxRect(this);
    }

    public Rectangle rect()
    {
        return new Rectangle(intX(), intY(), intWidth(), intHeight());
    }

    public BoundingBox box()
    {
        return new BoundingBox(x(), y(), width(), height());
    }

    public Line3 extent()
    {
        return new Line3(x, y, x + width, y + height);
    }

    @Override
    public void setExtent(Line3 line)
    {
        this.setPoints(line.x1, line.y1, line.x2, line.y2);
    }

    public Line3 lineTop()
    {
        return new Line3(p0(), p1());
    }

    public Line3 lineRight()
    {
        return new Line3(p1(), p2());
    }

    public Line3 lineBottom()
    {
        return new Line3(p2(), p3());
    }

    public Line3 lineLeft()
    {
        return new Line3(p3(), p0());
    }

    public Point3 p0()
    {
        return new Point3(x, y);
    }

    public Point3 p01()
    {
        return new Point3(x + width / 2, y);
    }

    public Point3 p1()
    {
        return new Point3(x + width, y);
    }

    public Point3 p12()
    {
        return new Point3(x + width, y + height / 2);
    }

    public Point3 p2()
    {
        return new Point3(x + width, y + height);
    }

    public Point3 p23()
    {
        return new Point3(x + width / 2, y + height);
    }

    public Point3 p3()
    {
        return new Point3(x, y + height);
    }

    public Point3 p30()
    {
        return new Point3(x, y + height / 2);
    }

    public Point3[] cornerPoints()
    {
        return new Point3[]
                {p0(), p1(), p2(), p3()};
    }

    public Point3[] sidePoints()
    {
        return new Point3[]
                {p01(), p12(), p23(), p30()};
    }

    public Rectangle3 setAnchor(Point3 anchor, int index)
    {
        Point3 delta = anchor.sub(anchorPoint(index));
        this.x += delta.x;
        this.y += delta.y;
        return this;
    }

    public Point3[] anchorPoints()
    {
        return new Point3[]
                {p0(), p01(), p1(), p12(), p2(), p23(), p3(), p30()};
    }

    public Point3 anchorPoint(int index)
    {
        switch (index)
        {
            case 0:
                return p0();
            case 1:
                return p01();
            case 2:
                return p1();
            case 3:
                return p12();
            case 4:
                return p2();
            case 5:
                return p23();
            case 6:
                return p3();
            case 7:
                return p30();
            case 8:
                return center();
        }
        return p0();
    }

    public Point3 anchorNorth()
    {
        return p01();
    }

    public Point3 anchorEast()
    {
        return p12();
    }

    public Point3 anchorSouth()
    {
        return p23();
    }

    public Point3 anchorWest()
    {
        return p30();
    }

    public Dimension dimension()
    {
        return new Dimension(this.intWidth(), this.intHeight());
    }

    public static Rectangle3 instance(String reals)
    {
        double[] d = Nb.toDoubles(reals);
        return new Rectangle3(d[0], d[1], d[2], d[3]);
    }

    public Rectangle3 toOrigin()
    {
        return new Rectangle3(0, 0, width, height);
    }

    public Rectangle3 shift(double dx, double dy)
    {
        return new Rectangle3(x + dx, y + dy, width, height);
    }

    public Rectangle3 shift(Point2D p)
    {
        return new Rectangle3(x + p.getX(), y + p.getY(), width, height);
    }

    public Rectangle3 shiftBack(Point2D p)
    {
        return new Rectangle3(x - p.getX(), y - p.getY(), width, height);
    }

    public Rectangle3 scaleDimension(double factorWidth, double factorHeight)
    {
        return new Rectangle3(x, y, width * factorWidth, height * factorHeight);
    }

    public Rectangle3 scaleCenter(double ratio)
    {
        float w = (float) (this.width * ratio);
        float h = (float) (this.height * ratio);
        return new Rectangle3(cx() - w / 2, cy() - h / 2, w, h);
    }

    public Rectangle3 scale(double ratio)
    {
        return this.scale(ratio, ratio);
    }

    public Rectangle3 scale(double sx, double sy)
    {
        return new Rectangle3(x * sx, y * sy, width * sx, height * sy);
    }

    public Rectangle3 include(Rectangle3 r)
    {
        if (r.width <= 0 && r.height <= 0)
            return this;
        if (r.minX() < this.minX())
            this.setMinX(r.minX());
        if (r.minY() < this.minY())
            this.setMinY(r.minY());
        if (r.maxX() > this.maxX())
            this.setMaxX(r.maxX());
        if (r.maxY() > this.maxY())
            this.setMaxY(r.maxY());
        return this;
    }

    public Rectangle3 inflate(double delta)
    {
        return inflate(delta, delta);
    }

    public Rectangle3 inflate(double dw, double dh)
    {
        this.x -= dw / 2.0;
        this.y -= dh / 2.0;
        this.width += dw;
        this.height += dh;
        return this;
    }

    public Rectangle3 inflate(double delta, boolean newInstance)
    {
        return inflate(delta, delta, newInstance);
    }

    public Rectangle3 inflate(double dw, double dh, boolean newInstance)
    {
        return newInstance ? new Rectangle3(x - dw / 2.0, y - dh / 2.0, width + dw, height + dh) : inflate(dw, dh);
    }

    public double overlap(Rectangle3 r)
    {
        return overlap(r, true);
    }

    public double overlapMin(Rectangle3 r)
    {
        return overlap(r, true);
    }

    public double overlapMax(Rectangle3 r)
    {
        return overlap(r, false);
    }

    public double overlap(Rectangle3 r, Boolean min)
    {
        if (!intersects(r))
            return 0.0;
        Rectangle3 i = createIntersection(r);
        if (min == null)
            return i.area();
        else
            return i.area() / (min ? Math.min(area(), r.area()) : Math.max(area(), r.area()));
    }

    public double overlapThis(Rectangle3 r)
    {
        if (!intersects(r))
            return 0.0;
        Rectangle3 i = createIntersection(r);
        return i.area() / area();
    }

    public double overlapThat(Rectangle3 r)
    {
        if (!intersects(r))
            return 0.0;
        Rectangle3 i = createIntersection(r);
        return i.area() / r.area();
    }

    public boolean hasOverlap(Point3 p, boolean x)
    {
        return x ? hasOverlapX(p.x) : hasOverlapY(p.y);
    }

    public boolean hasOverlapX(double x)
    {
        return x >= minX() && x <= maxX();
    }

    public boolean hasOverlapY(double y)
    {
        return y >= minY() && y <= maxY();
    }

    private double distance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public boolean hasOverlapY(Range2D r)
    {
        return maxY() >= r.minY() && minY() <= r.maxY();
    }

    public boolean hasOverlapX(Range2D r)
    {
        return maxX() >= r.minX() && minX() <= r.maxX();
    }

    public float[] overlapXs(Range2D r)
    {
        float minA = minX();
        float maxA = maxX();
        float minB = r.minX();
        float maxB = r.maxX();
        float min = Math.max(minA, minB);
        float max = Math.min(maxA, maxB);
        return max <= min ? null : Zen.Array.Floats(min, max);
    }

    public float overlapX(Range2D r)
    {
        return overlapX(r, true);
    }

    public float overlapX(Range2D r, Boolean minNorm)
    {
        return overlapX(r.minX(), r.maxX(), minNorm);
    }

    public float overlapX(double minB, double maxB)
    {
        return overlapX(minB, maxB, true);
    }

    public float overlapX(double minB, double maxB, Boolean minNorm)
    {
        return overlapX((float) minB, (float) maxB, minNorm);
    }

    public float overlapX(float minB, float maxB, Boolean minNorm)
    {
        float minA = minX();
        float maxA = maxX();
        float min = Math.max(minA, minB);
        float max = Math.min(maxA, maxB);
        if (max <= min)
            return 0;
        else if (minNorm == null)
            return max - min;
        else
            return (max - min) / (minNorm ? Math.min(maxA - minA, maxB - minB) : Math.max(maxA - minA, maxB - minB));
    }

    public float[] overlapYs(Range2D r)
    {
        float minA = minY();
        float maxA = maxY();
        float minB = r.minY();
        float maxB = r.maxY();
        float min = Math.max(minA, minB);
        float max = Math.min(maxA, maxB);
        return max <= min ? null : Zen.Array.Floats(min, max);
    }

    public float overlapY(Range2D r)
    {
        return overlapY(r, true);
    }

    public float overlapY(Range2D r, Boolean minNorm)
    {
        float minA = minY();
        float maxA = maxY();
        float minB = r.minY();
        float maxB = r.maxY();
        float min = Math.max(minA, minB);
        float max = Math.min(maxA, maxB);
        if (max <= min)
            return 0;
        else if (minNorm == null)
            return max - min;
        else
            return (max - min) / (minNorm ? Math.min(maxA - minA, maxB - minB) : Math.max(maxA - minA, maxB - minB));
    }

    public double distance(Range2D r)
    {
        float xMinA = this.minX();
        float yMinA = this.minY();
        float xMaxA = this.maxX();
        float yMaxA = this.maxY();
        float xMinB = r.minX();
        float yMinB = r.minY();
        float xMaxB = r.maxX();
        float yMaxB = r.maxY();
        if (this.intersects(xMinB, yMinB, xMaxB - xMinB, yMaxB - yMinB))
            return 0.0;
        else if (yMaxA >= yMinB && yMinA <= yMaxB) // overlap Y
            return xMaxA <= xMinB ? xMinB - xMaxA : xMinA - xMaxB;
        else if (xMaxA >= xMinB && xMinA <= xMaxB) // overlap X
            return yMaxA <= yMinB ? yMinB - yMaxA : yMinA - yMaxB;
        else if (xMaxA <= xMinB)
            return yMaxA <= yMinB ? distance(xMaxA, yMaxA, xMinB, yMinB) : distance(xMaxA, yMinA, xMinB, yMaxB);
        else if (xMinA >= xMaxB)
            return yMaxA <= yMinB ? distance(xMinA, yMaxA, xMaxB, yMinB) : distance(xMinA, yMinA, xMaxB, yMaxB);
        else
        {
            Log.warn(this, ".distance - this case should never happen...");
            return 0.0;
        }
    }

    public double area()
    {
        return Math.abs(this.width() * this.height());
    }

    public Dimension3 size()
    {
        return new Dimension3(this.width, this.height);
    }

    public double hypLength()
    {
        return Math.sqrt(hypLength2());
    }

    public double hypLength2()
    {
        return this.width * this.width + this.height * this.height;
    }

    public int intX()
    {
        return Math.round(x);
    }

    public int intY()
    {
        return Math.round(y);
    }

    public int intMinX()
    {
        return Math.round(minX());
    }

    public int intMinY()
    {
        return Math.round(minY());
    }

    public int intMaxX()
    {
        return Math.round(maxX());
    }

    public int intMaxY()
    {
        return Math.round(maxY());
    }

    public float x()
    {
        return x;
    }

    @Override
    public float minX()
    {
        return x;
    }

    public float y()
    {
        return y;
    }

    @Override
    public float minY()
    {
        return y;
    }

    public Point3 minXY()
    {
        return new Point3(minX(), minY());
    }

    public Point3 maxXY()
    {
        return new Point3(maxX(), maxY());
    }

    public int intCX()
    {
        return Math.round(x + width / 2f);
    }

    public int intCY()
    {
        return Math.round(y + height / 2f);
    }

    public float cx()
    {
        return x + width / 2f;
    }

    public float centerX()
    {
        return x + width / 2f;
    }

    public float cy()
    {
        return y + height / 2f;
    }

    public float centerY()
    {
        return y + height / 2f;
    }

    public Point3 center()
    {
        return new Point3(cx(), cy());
    }

    public Point3 origin()
    {
        return new Point3(x, y);
    }

    public boolean contains(XYizable p)
    {
        return this.contains(p.xy());
    }

    public Point3 xy()
    {
        return new Point3(x, y);
    }

    public void setXY(double x, double y)
    {
        this.x = (float) x;
        this.y = (float) y;
    }

    public void setXY(Point2D p)
    {
        this.x = (float) p.getX();
        this.y = (float) p.getY();
    }

    public Rectangle3 xy(Point2D p)
    {
        this.setXY(p);
        return this;
    }

    public void setX(double x)
    {
        this.x = (float) x;
    }

    public void setMinX(double x)
    {
        float max = this.maxX();
        this.x = (float) x;
        this.setMaxX(max);
    }

    public void setY(double y)
    {
        this.y = (float) y;
    }

    public void setMinY(double y)
    {
        float max = this.maxY();
        this.y = (float) y;
        this.setMaxY(max);
    }

    @Override
    public float maxX()
    {
        return x + width;
    }

    public void setMaxX(double x)
    {
        this.width = (float) (x - this.x);
    }

    @Override
    public float maxY()
    {
        return y + height;
    }

    public void setMaxY(double y)
    {
        this.height = (float) (y - this.y);
    }

    public int intWidth()
    {
        return Math.round(width);
    }

    public int intHeight()
    {
        return Math.round(height);
    }

    public int rWidth()
    {
        return Math.round(width);
    }

    public int rHeight()
    {
        return Math.round(height);
    }

    public float floatWidth()
    {
        return (float) width;
    }

    public float floatHeight()
    {
        return (float) height;
    }

    public Rectangle3 x(double x)
    {
        this.x = (float) x;
        return this;
    }

    public Rectangle3 y(double y)
    {
        this.y = (float) y;
        return this;
    }

    public Rectangle3 width(double width)
    {
        this.width = (float) width;
        return this;
    }

    public Rectangle3 height(double height)
    {
        this.height = (float) height;
        return this;
    }

    public Rectangle3 maxX(double x)
    {
        this.width = (float) (x - this.x);
        return this;
    }

    public Rectangle3 maxY(double y)
    {
        this.height = (float) (y - this.y);
        return this;
    }

    public float width()
    {
        return width;
    }

    public float height()
    {
        return height;
    }

    public float halfWidth()
    {
        return width / 2f;
    }

    public float halfHeight()
    {
        return height / 2f;
    }

    public void setWidth(double width)
    {
        this.width = (float) width;
    }

    public void setHeight(double height)
    {
        this.height = (float) height;
    }

    public void setDimension(double width, double height)
    {
        this.width = (float) width;
        this.height = (float) height;
    }

    public void setSize(double width, double height)
    {
        this.width = (float) width;
        this.height = (float) height;
    }

    public Rectangle3 resize(double deltaWidth, double deltaHeight)
    {
        return new Rectangle3(x, y, width + deltaWidth, height + deltaHeight);
    }

    // public Rectangle3 augmentWidthNew(double deltaWidth)
    // {
    //
    // return new Rectangle3(this.x, this.y, this.width + deltaWidth,
    // this.height);
    // }

    // public Rectangle3 augmentHeightNew(double deltaHeight)
    // {
    // return new Rectangle3(this.x, this.y, this.width, this.height +
    // deltaHeight);
    // }

    // public Rectangle3 augmentWidth(double deltaWidth)
    // {
    // this.width += deltaWidth;
    // return this;
    // }

    // public Rectangle3 augmentHeight(double deltaHeight)
    // {
    // this.height += deltaHeight;
    // return this;
    // }

    public void setPoints(double... p)
    {
        this.x = (float) (p[0] < p[2] ? p[0] : p[2]);
        this.y = (float) (p[1] < p[3] ? p[1] : p[3]);
        this.width = (float) Math.abs(p[2] - p[0]);
        this.height = (float) Math.abs(p[3] - p[1]);
    }

    public void set(double... p)
    {
        this.x = (float) p[0];
        this.y = (float) p[1];
        this.width = (float) p[2];
        this.height = (float) p[3];
    }

    @Override
    public boolean contains(Rectangle2D r)
    {
        if (r.getWidth() <= 0 || r.getHeight() <= 0)
            r = new Rectangle3(r.getX(), r.getY(), Geom.zeroPlus(r.getWidth()), Geom.zeroPlus(r.getHeight()));
        return super.contains(r);
    }

    public Rectangle3 intersection(Rectangle2D r)
    {
        return this.createIntersection(r);
    }

    @Override
    public Rectangle3 createIntersection(Rectangle2D r)
    {
        Rectangle3 dest = new Rectangle3();
        Rectangle3.intersect(this, r, dest);
        return dest;
    }

    public Rectangle3 union(Rectangle2D r)
    {
        return this.createUnion(r);
    }

    @Override
    public Rectangle3 createUnion(Rectangle2D r)
    {
        Rectangle3 dest = new Rectangle3();
        Rectangle3.union(this, r, dest);
        return dest;
    }

    public int[] intValues()
    {
        return new int[]{intX(), intY(), intWidth(), intHeight()};
    }

    public Rectangle3 round()
    {
        return new Rectangle3(intX(), intY(), intWidth(), intHeight());
    }

    public Rectangle3 round(boolean half)
    {
        return half ? new Rectangle3(Math.round(x - 0.5) + 0.5, Math.round(y - 0.5) + 0.5, intWidth(), intHeight()) : round();
    }

    public Line3 sideLineTo(Rectangle3 r)
    {
        Point3[] sides = sidePoints();
        Point3[] tides = r.sidePoints();

        if (this.hasOverlapX(r))
        {
            if (maxY() < r.minY())
                return sides[2].lineTo(tides[0]);
            else if (minY() > r.maxY())
                return sides[0].lineTo(tides[2]);
        } else if (this.hasOverlapY(r))
            if (maxX() < r.minX())
                return sides[1].lineTo(tides[3]);
            else if (minX() > r.maxX())
                return sides[3].lineTo(tides[1]);

        Line3 line = new Line3(sides[0], tides[0]);
        double min = Math.abs(sides[0].distanceSq(tides[0]));

        for (Point3 p0 : sides)
            for (Point3 p1 : tides)
            {
                double d = Math.abs(p0.distanceSq(p1));
                if (d < min)
                {
                    min = d;
                    line = new Line3(p0, p1);
                }
            }
        return line;
    }

    public Line3 closestLine(Point3 p)
    {
        return closestPoint(p).lineTo(p);
    }

    public Point3 closestPoint(Point3 p)
    {
        double minX = this.minX();
        double maxX = this.maxX();
        double minY = this.minY();
        double maxY = this.maxY();

        double[] d = new double[4];
        d[0] = Math.abs(p.y - minY);
        d[1] = Math.abs(p.x - maxX);
        d[2] = Math.abs(p.y - maxY);
        d[3] = Math.abs(p.x - minX);

        if (p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY)// inside
        {
            int i = Nb.minIndex(d);
            switch (i)
            {
                case 0:
                    return new Point3(p.x, minY);
                case 1:
                    return new Point3(maxX, p.y);
                case 2:
                    return new Point3(p.x, maxY);
                case 3:
                    return new Point3(minX, p.y);
            }
        } else if (p.x >= minX && p.x <= maxX)
            return d[0] < d[2] ? new Point3(p.x, minY) : new Point3(p.x, maxY);
        else if (p.x >= minX && p.x <= maxX || p.y >= minY && p.y <= maxY)
            return d[1] < d[3] ? new Point3(maxX, p.y) : new Point3(minX, p.y);
        else
            return p.x < minX ? new Point3(minX, p.y < minY ? minY : maxY) : new Point3(maxX, p.y < minY ? minY : maxY);
        return new Point3();
    }

    @Override
    public Object clone()
    {
        return copy();
    }

    public Object clone(Object o)
    {
        return copy();
    }

    public Rectangle3 newOrigin()
    {
        return new Rectangle3(0, 0, width, height);
    }

    @Override
    public Rectangle3 copy()
    {
        return new Rectangle3(x, y, width, height);
    }

    public static Rectangle3 fromOCD(String data, Rectangle3 def)
    {
        if (Str.IsVoid(data))
            return def;
        String[] s = Str.Split(data);
        return s.length < 4 ? def : new Rectangle3(s[0], s[1], s[2], s[3]);
    }

    public String toOCD()
    {
        return toOCD(null);
    }

    public String toOCD(XmlDecimalFormat nf)
    {
        nf = XmlDecimalFormat.Need(nf);
        return nf.format(x) + " " + nf.format(y) + " " + nf.format(width) + " " + nf.format(height);
    }

    public boolean equals(Rectangle2D r, double precision)
    {
        return Math.abs(r.getX() - x) < precision && Math.abs(r.getY() - y) < precision && Math.abs(r.getWidth() - width) < precision
                && Math.abs(r.getHeight() - height) < precision;
    }

    public String toString(int decimals, String sep)
    {
        return "[" + Zen.toString(x, decimals) + sep + Zen.toString(y, decimals) + sep + Zen.toString(width, decimals) + sep
                + Zen.toString(height, decimals) + "]";
    }

    @Override
    public String toString()
    {
        return toString(3, " ");
    }

    public static Comparator<Rectangle3> xComparator()
    {
        return (o1, o2) -> o1.x < o2.x ? -1 : o1.x > o2.x ? 1 : 0;
    }

    public static Comparator<Rectangle3> yComparator()
    {
        return (o1, o2) -> o1.y < o2.y ? -1 : o1.y > o2.y ? 1 : 0;
    }

    public static Rectangle3[] array(Rectangle3... r)
    {
        return r;
    }

    public Rectangle3 checkMin(double min)
    {
        return checkMin(min, min);
    }

    public Rectangle3 checkMin(double minW, double minH)
    {
        if (width < minW)
            width = (float) minW;
        if (height < minH)
            height = (float) minH;
        return this;
    }

    public Rectangle3 surround(Rectangle3 box)
    {
        return new Rectangle3(true, box.minX() < minX() ? minX() : box.minX(), box.minY() < minY() ? minY() : box.minY(),
                box.maxX() > maxX() ? maxX() : box.maxX(), box.maxY() > maxY() ? maxY() : box.maxY());
    }

    public Quadri quadri()
    {
        return new Quadri(cornerPoints());
    }

    @Override
    public Rectangle3 bounds()
    {
        return this;
    }

    public FxRect fxClip()
    {
        FxRect fx = this.fx();
        fx.setFill(Color3.WHITE.fx());
        fx.setStroke(null);
        return fx;
    }

    public Rectangle2D.Double rectangle2DDouble()
    {
        return new Rectangle2D.Double(x,y,width,height);
    }

    public static Rectangle3 scan(String data)
    {
        Scan scan = new Scan(data.replace('[', ' ').replace(']', ' ').replace('(', ' ').replace(')', ' ').trim());
        Rectangle3 box = new Rectangle3(scan.real(0), scan.real(0), scan.real(0), scan.real(0));
        scan.close();
        return box;
    }

    public static Rectangle3[] boxes(Boxable[] boxers)
    {
        Rectangle3[] boxes = new Rectangle3[boxers.length];
        for (int i = 0; i < boxes.length; i++)
            boxes[i] = boxers[i].box();
        return boxes;
    }

    public static Rectangle3 Points(double x0, double y0, double x1, double y1)
    {
        return new Rectangle3(true, x0, y0, x1, y1);
    }

    public static Rectangle3[] Array(List<? extends Rectangle3> list)
    {
        return list.toArray(new Rectangle3[0]);
    }

    public static Rectangle3 Bounds(Rectangle3... boxes)
    {
        if (boxes == null || boxes.length == 0)
            return null;
        Rectangle3 grow = boxes[0].copy();
        for (int i = 1; i < boxes.length; i++)
            grow.include(boxes[i]);
        return grow;
    }

    public static Rectangle3 centered(Rectangle3 parent, Dimension3 dim)
    {
        return new Rectangle3((parent.width - dim.width()) / 2, (parent.height - dim.height()) / 2, dim.width(), dim.height());
    }

    public static boolean NullOrEmpty(Rectangle3 r)
    {
        return r == null || r.isEmpty();
    }
    // @Override
    // public Rectangle3 bounds()
    // {
    // return this;
    // }
}
