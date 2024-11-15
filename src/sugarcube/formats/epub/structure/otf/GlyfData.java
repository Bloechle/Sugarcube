package sugarcube.formats.epub.structure.otf;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Path3.Seg;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;

/** Data for one entry in the glyf table. */
class GlyfData
{
  /** TTF coordinate flags. */
  public static final int NONE = 0;
  public static final int ON_PATH = 1 << 0;
  public static final int X_SHORT_VECTOR = 1 << 1;
  public static final int Y_SHORT_VECTOR = 1 << 2;
  public static final int REPEAT = 1 << 3;

  // same flag or short vector sign flag
  public static final int X_IS_SAME = 1 << 4;
  public static final int Y_IS_SAME = 1 << 5;
  public static final int X_SHORT_VECTOR_POSITIVE = 1 << 4;
  public static final int Y_SHORT_VECTOR_POSITIVE = 1 << 5;

  public List3<Integer> flags = new List3<>();
  public List3<Integer> xCoords = new List3<>();
  public List3<Integer> yCoords = new List3<>();
  public List3<Integer> endPts = new List3<>();

  public int lastX = 0;
  public int lastY = 0;

  public Glyph glyph;

  public int minX = 0;
  public int minY = 0;
  public int maxX = 0;
  public int maxY = 0;

  public GlyfData(Glyph g)
  {
    glyph = g;
    if (glyph.quadPath != null)
      process();
  }

  private void process()
  {
    boolean debug = false;
    
    debug &='à' == glyph.unicode;
    debug &= glyph.fontname.equals("AkzidenzGroteskBQ_Light.svg");
    
//    debug = true;
    
    Log.debug(debug, this, ".process - " + glyph.index + ", " + (char) glyph.unicode + ", " + glyph.fontname);
    Rectangle3 box = glyph.quadPath.bounds();
    minX = OTF.ZUnit(box.minX());
    minY = OTF.ZUnit(box.minY());
    maxX = OTF.ZUnit(box.maxX());
    maxY = OTF.ZUnit(box.maxY());

    if (debug)
      glyph.quadPath.debugFrame(800, 600);

    Seg[] segs = glyph.segs;
    for (int i = 0; i < segs.length; i++)
    {
      Seg seg = segs[i];
      Point3 p = seg.p();

//      Log.debug(debug, this, ".process - " + seg.op.name() + ": " + Math.round(p.x * 1000) + ", " + Math.round(p.y * 1000));

      switch (seg.op)
      {
      case MOVE:
        addPoint(p, true);
        break;
      case LINE:
        addPoint(p, true);
        break;
      case QUAD:
        addPoint(seg.c0(), false);
        addPoint(seg.p(), true);
        break;
      case CUBIC:
        addPoint(p, true);
        Log.debug(this, ".process - cubic curve should not occur in ttf");
        break;
      case CLOSE:
        addPoint(p, true);
        endPts.add(flags.size() - 1);
        break;
      }
    }

  }

  public void addPoint(Point3 p, boolean onPath)
  {
    flags.add(onPath ? ON_PATH : NONE);
    int x = OTF.ZUnit(p.x);
    int y = OTF.ZUnit(p.y + glyph.baseline);
    // Log.debug(this, ".addPoint - " + endIndex + ": " + x + "," + y +
    // ", onPath=" + true);
    xCoords.add(x - lastX);
    yCoords.add(y - lastY);

    if (true)
    {
      lastX = x;
      lastY = y;
    }
  }

  // public static double tie_to_ttf_grid_x(Glyph glyph, double x)
  // {
  // int ttf_x;
  // ttf_x = rint(x * HeadTable.UNITS - glyph.left_limit * HeadTable.UNITS);
  // return (ttf_x / HeadTable.UNITS) + glyph.left_limit;
  // }
  //
  // public static double tie_to_ttf_grid_y(Glyph glyph, double y)
  // {
  // double ttf_y;
  // ttf_y = Math.round(y * HeadTable.UNITS + glyph.base_line *
  // HeadTable.UNITS);
  // return (ttf_y / HeadTable.UNITS) - glyph.base_line;
  // }

  public int nbContours()
  {
    return endPts.size();
  }
}
