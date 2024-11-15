package sugarcube.common.graphics;

import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.geom.Dimension3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.data.xml.Nb;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public class Metric
{
  public interface Listener
  {
    public void metricChanged(Metric oldMetric, Metric newMetric);
  }

  private static final StringMap<Double> UNIT_PER_INCH = new StringMap<>();
  public static final double MM_PER_INCH = 25.4f;
  public static final double CM_PER_INCH = 2.54f;
  public static final double PT_PER_INCH = 72;
  public static final double PC_PER_INCH = 6;// since there's 12 points per pica
  public static final String PX = "px";// ocd document pixel for instance
                                       // (default at 72 dpi)
  public static final String MM = "mm";
  public static final String CM = "cm";
  public static final String IN = "in";
  public static final String PT = "pt";
  public static final String PC = "pc";
  public final String unit;
  public final double dpi;

  static
  {
    UNIT_PER_INCH.put(MM, MM_PER_INCH);
    UNIT_PER_INCH.put(CM, CM_PER_INCH);
    UNIT_PER_INCH.put(IN, 1.0);
    UNIT_PER_INCH.put(PT, PT_PER_INCH);
    UNIT_PER_INCH.put(PC, PC_PER_INCH);
  }

  public Metric()
  {
    this(PX);
  }

  public Metric(String unit)
  {
    this(unit, 72);
  }

  public Metric(String unit, double dpi)
  {
    this.unit = unit;
    this.dpi = dpi;
  }

  public double toPx()
  {
    return is(PX) ? 1 : dpi / UNIT_PER_INCH.get(unit, dpi);
  }

  public double fromPx()
  {
    return is(PX) ? 1 : 1 * UNIT_PER_INCH.get(unit, dpi) / dpi;
  }

  public double toPx(double value)
  {
    return value * toPx();
  }

  public double fromPx(double pixels)
  {
    return pixels * fromPx();
  }

  public String fromPx(double pixels, int decimals)
  {
    return Nb.String(fromPx(pixels), decimals);
  }

  public Rectangle3 toPx(Rectangle2D r)
  {
    return new Rectangle3(toPx(r.getX()), toPx(r.getY()), toPx(r.getWidth()), toPx(r.getHeight()));
  }

  public Rectangle3 fromPx(Rectangle2D r)
  {
    return new Rectangle3(fromPx(r.getX()), fromPx(r.getY()), fromPx(r.getWidth()), fromPx(r.getHeight()));
  }

  public Dimension3 toPx(Dimension2D dim)
  {
    return new Dimension3(toPx(dim.getWidth()), toPx(dim.getHeight()));
  }

  public Dimension3 fromPx(Dimension2D dim)
  {
    return new Dimension3(fromPx(dim.getWidth()), fromPx(dim.getHeight()));
  }

  public double from(String unit, double value)
  {
    return this.fromPx(new Metric(unit, dpi).toPx(value));
  }

  public double to(String unit, double value)
  {
    return new Metric(unit, dpi).fromPx(toPx(value));
  }

  public Dimension3 from(String unit, Dimension2D dim)
  {
    return new Dimension3(from(unit, dim.getWidth()), from(unit, dim.getHeight()));
  }

  public Dimension3 to(String unit, Dimension2D dim)
  {
    return new Dimension3(to(unit, dim.getWidth()), to(unit, dim.getHeight()));
  }
  
  public boolean isPX()
  {
    return is(PX);
  }

  public boolean is(String... units)
  {
    if (unit == null)
      return false;
    for (String u : units)
      if (unit.equals(u))
        return true;
    return false;
  }

  public String name()
  {
    if (is(PX))
      return "Pixel";
    else if (is(MM))
      return "Millimeter";
    else if (is(CM))
      return "Centimeter";
    else if (is(IN))
      return "Inch";
    else if (is(PT))
      return "Point";
    else if (is(PC))
      return "Pica";
    else
      return unit;
  }

  @Override
  public String toString()
  {
    return this.unit;
  }

  public static Metric get(String unit, double dpi)
  {
    return new Metric(unit, dpi);
  }

  public static double mm2ocd(double millimeters)
  {
    return ocd().from("mm", millimeters);
  }

  public static double ocd2mm(double ocd)
  {
    return MM_PER_INCH * ocd / PT_PER_INCH;
  }

  public static Metric ocd()
  {
    return new Metric(PX, 72);
  }

  public static final double mm2pt(double mm){
    return inch2pt(mm2inch(mm));
  }
  
  public static final double pt2mm(double pt){
    return pt2inch(inch2mm(pt));
  }
  
  public static final double mm2inch(double mm){
    return mm / MM_PER_INCH;
  }
  
  public static final double inch2mm(double inch){
    return inch * MM_PER_INCH;
  }
  
  public static final double pt2inch(double pt){
    return pt2inch(pt, 72f);
  }
  
  public static final double inch2pt(double inch){
    return inch2pt(inch, 72f);
  }
  
  public static final double pt2inch(double pt, double dpi){
    return pt / dpi;
  }
  
  public static final double inch2pt(double inch, double dpi){
    return inch * dpi;
  }
  
  /**
   * twentieths of a point to points
   * @param dxa 20th of a point
   */
  public static final float dxa2pt(float dxa){
    return Float.isNaN(dxa) ? Float.NaN : dxa / 20f;
  }
  
  public static final float dxa2pt(float dxa, float def)
  {
    return Float.isNaN(dxa) ? def : dxa / 20f;
  }
  
  /**
   * points to twentieths of a point
   * @param pt points
   */
  public static final float pt2dxa(float pt){
    return Float.isNaN(pt) ? Float.NaN : pt * 20f;
  }
}
