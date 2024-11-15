package sugarcube.common.graphics.geom.homography;

import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Point3;

public class ProjCross
{
  public final Line3 nsLine;
  public final Line3 weLine;

  public ProjCross()
  {
    this.nsLine = new Line3();
    this.weLine = new Line3();
  }

  public ProjCross(Line3 northSouth, Line3 westEast)
  {
    this.nsLine = northSouth;
    this.weLine = westEast;
  }

  public ProjCross set(Line3 line, boolean northSouth)
  {
    if (northSouth)
      this.nsLine.set(line);
    else
      this.weLine.set(line);
    return this;
  }

  public ProjCross set(Line3 northSouth, Line3 westEast)
  {
    if (northSouth != null)
      this.nsLine.set(northSouth);
    if (westEast != null)
      this.weLine.set(westEast);
    return this;
  }

  public ProjCross set(ProjCross cross)
  {
    return set(cross.nsLine, cross.weLine);
  }

  public ProjCross set(Point3 north, Point3 south, Point3 west, Point3 east)
  {
    return set(north.lineTo(south), west.lineTo(east));
  }

  public ProjCross set(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
  {
    return set(new Line3(x1, y1, x2, y2), new Line3(x3, y3, x4, y4));
  }

  public Point3[] points()
  {
    return new Point3[]
    { nsLine.p1(), nsLine.p2(), weLine.p1(), weLine.p2() };
  }

  public Point3 center()
  {
    return new Point3(weLine.cx(), nsLine.cy());
  }

  public ProjCross resize(double nsSize, double weSize)
  {
    Point3 c = center();
    nsLine.set(nsLine.sub(c).scale(nsSize / nsLine.length()).add(c));
    weLine.set(weLine.sub(c).scale(weSize / weLine.length()).add(c));
    return this;
  }

  @Override
  public String toString()
  {
    return nsLine + " - " + weLine;
  }

  public static ProjCross Unit(Point3 p)
  {
    double delta = 0.5;
    return new ProjCross(new Line3(p.x, p.y - delta, p.x, p.y + delta), new Line3(p.x - delta, p.y, p.x + delta, p.y));
  }

}
