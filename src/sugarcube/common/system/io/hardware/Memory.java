package sugarcube.common.system.io.hardware;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.log.Logger;
import sugarcube.common.data.collections.List3;

public class Memory<T>
{
  public class Snapshot
  {
    public String title;
    public long timestamp;
    public long totalMemory;
    public long freeMemory;
    public T object;

    public Snapshot(String title, T object)
    {
      this.title = title;
      this.object = object;
      this.timestamp = System.currentTimeMillis();
      Runtime runtime = Runtime.getRuntime();
//      runtime.gc();
      this.totalMemory = runtime.totalMemory();
      this.freeMemory = runtime.freeMemory();
    }

    public long usedMemory()
    {
      return totalMemory - freeMemory;
    }

    public int elapsedSeconds()
    {
      return (int) (this.timestamp - Memory.this.timeStart) / 1000;
    }

    @Override
    public String toString()
    {
      return title + ", t=" + elapsedSeconds() + "s, m=" + ((totalMemory - freeMemory) / MB) + "MB/" + (totalMemory / MB) + "MB";
    }
  }

  private Snapshot max = null;
  private Snapshot min = null;
  private List3<Snapshot> snaps = new List3<Snapshot>();
  public String source;
  public String title;
  public long timeStart;

  public Memory(Object source)
  {
    this(source, source.getClass().getSimpleName());
  }

  public Memory(Object source, String title)
  {
    this.source = Logger.sourcize(source);
    this.title = title;
    this.timeStart = System.currentTimeMillis();
  }

  public Memory reset()
  {
    this.timeStart = System.currentTimeMillis();
    this.snaps.clear();
    return this;
  }

  public Snapshot reset(String title)
  {
    this.reset();
    return this.snap(title);
  }

  public Snapshot snap(String title)
  {
    return this.snap(title, null);
  }

  public Snapshot snap(String title, T object)
  {
    Snapshot snap = new Snapshot(title, object);
    this.snaps.add(snap);
    if (max == null || snap.usedMemory() > max.usedMemory())
      max = snap;
    if (min == null || snap.usedMemory() < min.usedMemory())
      min = snap;
    // Log.debug(this, ".snap - "+snap);
    return snap;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for (Snapshot snap : snaps)
      sb.append(snap.toString()).append("\n");
    return sb.toString();
  }

  public String prefix()
  {
    return " - " + this.source + (source.equals(title) ? "" : " - " + this.title);
  }

  public Memory log()
  {
    Log.debug(this, prefix() + "\n" + toString());
    return this;
  }

  public Memory logMinMax()
  {

    Log.debug(this, prefix() + ", min=" + min + ", max=" + max);
    return this;
  }

  public Memory logMax()
  {

    Log.debug(this, prefix() + ", max=" + max);
    return this;
  }

  public static final int MB = 1024 * 1024;

  public static Runtime Runtime()
  {
    return Runtime.getRuntime();
  }

  public static long TotalMB()
  {
    return Runtime().totalMemory() / MB;
  }

  public static long FreeMB()
  {
    return Runtime().freeMemory() / MB;
  }

  public static long MaxMB()
  {
    return Runtime().maxMemory() / MB;
  }

  public static long UsedMB()
  {
    return (Runtime().totalMemory() - Runtime().freeMemory()) / MB;
  }

  public static String Info()
  {
    return "Memory[used=" + UsedMB() + "MB, free=" + FreeMB() + "MB, max=" + MaxMB() + "MB]";
  }
  
  public static String FootPrint()
  {
    return "["+ UsedMB() + "MB / " + MaxMB() + "MB]";
  }

}
