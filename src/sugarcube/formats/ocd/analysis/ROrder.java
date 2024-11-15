package sugarcube.formats.ocd.analysis;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.numerics.Math3;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.OCDText;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ROrder
{
  public final static String UNDEF = "undef";
  public final static String LEFT_RIGHT = "left-right";
  public final static String RIGHT_LEFT = "right-left";;
  public final static String CUSTOM = "custom";

  public static String ERR_MSG = "";

  private final String rOrder;

  public ROrder()
  {
    this.rOrder = LEFT_RIGHT;
  }

  public ROrder(String rOrder)
  {
    this.rOrder = rOrder;
  }

  public static boolean isUndef(String reading)
  {
    return Str.IsVoid(reading) || reading.equalsIgnoreCase(UNDEF);
  }

  public void sortTable(List3<OCDPaintable> nodes)
  {
    Comparator<OCDPaintable> comparator = (t1, t2) -> {
      Rectangle3 r1 = t1.bounds();
      Rectangle3 r2 = t2.bounds();
      // horizontal overlap
      if (r1.x <= r2.x && r1.x + r1.width >= r2.x || r2.x <= r1.x && r2.x + r2.width >= r1.x)
      {
        if (Math3.r1ContainsR2(r1, r2, 1))
          return 1;
        if (Math3.r1ContainsR2(r2, r1, 1))
          return -1;
        return r1.y < r2.y ? -1 : r1.y > r2.y ? 1 : 0;
      } else
        return r1.x < r2.x ? -1 : r1.x > r2.x ? 1 : 0;

    };
    sort(nodes, comparator);
  }

  public static int Compare(OCDPaintable p0, OCDPaintable p1, boolean topDown, boolean leftRight)
  {
    int x0 = 0;
    int y0 = 0;
    int x1 = 0;
    int y1 = 0;

    if (p0.isTextBlock())
    {
      OCDText t = p0.asTextBlock().firstText();
      if (t != null)
      {
        x0 = Math.round(t.x());
        y0 = Math.round(t.y());
      }
    } else
    {
      Rectangle3 r = p0.bounds();
      x0 = Math.round(r.x);
      y0 = Math.round(r.y);
    }
    if (p1.isTextBlock())
    {
      OCDText t = p1.asTextBlock().firstText();
      if (t != null)
      {
        x1 = Math.round(t.x());
        y1 = Math.round(t.y());
      }
    } else
    {
      Rectangle3 r = p1.bounds();
      x1 = Math.round(r.x);
      y1 = Math.round(r.y);
    }

    return y0 < y1 ? (topDown ? -1 : 1) : y0 > y1 ? (topDown ? 1 : -1) : x0 < x1 ? (leftRight ? -1 : 1) : x0 > x1 ? (leftRight ? 1 : -1) : 0;
  }

  public void sort(List3<OCDPaintable> nodes, final boolean topDown, final boolean leftRight)
  {
    Comparator<OCDPaintable> comparator = (a, b) -> ROrder.Compare(a, b, topDown, leftRight);
    sort(nodes, comparator);
  }

  public void sort(List<OCDPaintable> list, Comparator<OCDPaintable> comparator)
  {
    try
    {
      Collections.sort(list, comparator);
    } catch (Exception e)
    {
      String msg = ".sort - hiccup: resolved";
      if (!ERR_MSG.equals(msg))
      {
        Log.debug(this, ".sort - hiccup: resolved");
        ERR_MSG = msg;
      }
    }
  }

  public void sort(List3<OCDPaintable> nodes)
  {
    switch (rOrder)
    {
    case LEFT_RIGHT:
      sort(nodes, true, true);
      break;
    case RIGHT_LEFT:
      sort(nodes, true, false);
      break;
    default:
      sort(nodes, true, true);
      break;
    }
  }

  public static void Sort(String rOrder, List3<OCDPaintable> nodes, BoundsMap bounds)
  {   
    DexterProps.SortOY(nodes, bounds);
    List3<OCDPaintable> sorted = new List3<>();
    while (nodes.isPopulated())
    {
      OCDPaintable node = nodes.first();      
      Rectangle3 box = node.bounds();           
      for(OCDPaintable n: nodes)
      {
        Rectangle3 b = n.bounds();       
        if(b.x<box.x && b.hasOverlapY(box))
        {
          box = b;
          node = n;
        }               
      }      
      sorted.add(node);
      nodes.remove(node);               
    }
    nodes.setAll(sorted);
  }

  public static void Sort(OCDPage page, BoundsMap bounds)
  {
    Sort(page.rOrder, page.content().graphics(), bounds);
  }

}
