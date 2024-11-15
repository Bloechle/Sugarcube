package sugarcube.formats.ocd.objects.lists;

import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDPaintable;

import java.util.Collections;
import java.util.Comparator;

public class OCDPaintables<T extends OCDPaintable> extends List3<T>
{
  public OCDPaintables()
  {
  }

  public OCDPaintables(Iterable<T> iterable)
  {
    for (T t : iterable)
      this.add(t);
  }

  public Rectangle3 bounds()
  {
    return bounds(new Rectangle3());
  }

  public Rectangle3 bounds(Rectangle3 def)
  {
    if (this.isEmpty())
      return def;
    Rectangle3 box = this.first().bounds().copy();
    for (OCDPaintable node : this)
      box.include(node.bounds());
    return box;
  }

  public OCDPaintables<T> overlap(Rectangle3 box, double min)
  {
    OCDPaintables<T> list = new OCDPaintables<T>();
    for (T node : this)
      if (node.bounds().overlap(box) > min)
        list.add(node);
    return list;
  }

  public OCDPaintables<T>  sortX()
  {
    Collections.sort(this, xComparator());
    return this;    
  }

  public OCDPaintables<T>  sortY()
  {
    Collections.sort(this, yComparator());
    return this;    
  }

  public OCDPaintables<T> sortZOrder()
  {
    Collections.sort(this, zorderComparator());
    return this;
  }

  public OCDPaintables<T>  sortBigger()
  {
    Collections.sort(this, biggerComparator());
    return this;    
  }

  public OCDPaintables<T>  sortSmaller()
  {
    Collections.sort(this, smallerComparator());
    return this;    
  }

  public static Comparator<OCDPaintable> zorderComparator()
  {
    return (o1, o2) -> Float.compare(o1.zOrder(), o2.zOrder());
  }

  public static Comparator<OCDPaintable> smallerComparator()
  {
    return (o1, o2) -> Double.compare(o1.bounds().area(), o2.bounds().area());
  }

  public static Comparator<OCDPaintable> biggerComparator()
  {
    return (o1, o2) -> Double.compare(o2.bounds().area(), o1.bounds().area());
  }

  public static Comparator<OCDPaintable> xComparator()
  {
    return (o1, o2) -> {
      Rectangle3 r1 = o1.bounds();
      Rectangle3 r2 = o2.bounds();
      double x1 = r1.minX();
      double x2 = r2.minX();
      double y1 = r1.minY();
      double y2 = r2.minY();
      return x1 < x2 ? -1 : x1 > x2 ? 1 : y1 < y2 ? -1 : y1 > y2 ? 1 : 0;
    };
  }

  public static Comparator<OCDPaintable> yComparator()
  {
    return (o1, o2) -> {
      Rectangle3 r1 = o1.bounds();
      Rectangle3 r2 = o2.bounds();
      double x1 = r1.minX();
      double x2 = r2.minX();
      double y1 = r1.minY();
      double y2 = r2.minY();
      return y1 < y2 ? -1 : y1 > y2 ? 1 : x1 < x2 ? -1 : x1 > x2 ? 1 : 0;
    };
  }

}
