package sugarcube.common.data;

import sugarcube.common.data.collections.A;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.system.util.Sys;
import sugarcube.common.system.log.Log;
import sugarcube.common.ui.gui.icons.ImageIcon3;
import sugarcube.common.interfaces.Closable;
import sugarcube.common.system.time.DateUtils;
import sugarcube.common.data.xml.Nb;

import java.util.Iterator;
import java.util.Vector;

public class Zen
{
  public static final ImageIcon3 S3_ICON = new ImageIcon3("sugarcube24.png");

  public static void LAF()
  {
    Sys.LAF();
  }

  public static class Generic<T>
  {
    public Vector<T> toVector(Iterator<T> it)
    {
      Vector<T> data = new Vector<T>();
      while (it.hasNext())
        data.add(it.next());
      return data;
    }

    public List3<T> toList(Iterator<T> it)
    {
      List3<T> data = new List3<T>();
      while (it.hasNext())
        data.add(it.next());
      return data;
    }
  }

  public static class Epsilon
  {
    public static boolean is(double a, double b, double epsilon)
    {
      return a > b ? a - b < epsilon : b - a < epsilon;
    }

    public static boolean isZero(double a, double epsilon)
    {
      return a > 0 ? a < epsilon : -a < epsilon;
    }
  }


  public static class Array extends A
  {
  }

  public static void exit(long millis)
  {
    sleep(millis);
    System.exit(0);
  }

  public static void sleep(long millis)
  {
    Sys.Sleep(millis);
  }

  public static void close(Closable closable)
  {
    try
    {
      if (closable != null)
        closable.close();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static String toString(double d, int decimals)
  {
    return Nb.String(d, decimals);
  }

  public static String toString(Object o)
  {
    return o == null ? null : o.toString();
  }

  public static String toString(Object o, String def)
  {
    return o == null ? def : o.toString();
  }

  public static void print(Object source, Object msg)
  {
    System.out.print(msg);
  }

  public static void println(Object source, Object msg)
  {
    System.out.println(msg);
  }

  public static void main(String... args)
  {
    Log.debug(Zen.class, " - UTC: " + DateUtils.universalTime());
  }

  public static boolean equiv(String a, String b)
  {
    return equals(a, b, true);
  }

  public static boolean equals(Object a, Object b)
  {
    return a == b ? true : a != null && b != null ? a.equals(b) : false;
  }

  public static boolean equalsToString(Object a, Object b)
  {
    return a == b ? true : a != null && b != null ? a.toString().equals(b.toString()) : false;
  }

  public static boolean equals(String a, String b, boolean emptyEqualsNull)
  {
    return equals(emptyEqualsNull && a == null ? "" : a, emptyEqualsNull && b == null ? "" : b);
  }

  public static boolean isVoid(String data)
  {
    return Str.IsVoid(data);
  }

  public static String unnull(String data)
  {
    return Str.Unnull(data);
  }

  public static String avoid(String data, String def)
  {
    return Str.Avoid(data, def);
  }

  public static boolean hasData(String data)
  {
    return Str.HasData(data);
  }

  public static boolean hasChar(String data)
  {
    return Str.HasChar(data);
  }


  public static String trim(String data)
  {
    return data == null ? "" : data.trim();
  }

  public static String trim(String data, int max)
  {
    data = trim(data);
    return data.length() > max ? data.substring(0, max) : data;
  }

  public static long time()
  {
    return System.currentTimeMillis();
  }

  public static long time(long start)
  {
    return System.currentTimeMillis() - start;
  }

  public static int Sign(long v, boolean invert)
  {
    return v > 0 ? (invert ? -1 : 1) : v < 0 ? (invert ? 1 : -1) : 0;
  }
}
