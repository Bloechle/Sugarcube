package sugarcube.common.graphics;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import sugarcube.common.data.Zen;
import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.common.data.xml.Nb;

import java.awt.*;

public class Stroke3 extends BasicStroke
{
  public static final String BUTT = "butt";
  public static final String ROUND = "round";
  public static final String SQUARE = "square";
  public static final String BEVEL = "bevel";
  public static final String MITER = "miter";
  public static final Stroke3 NONE = new Stroke3(0.0);
  public static final Stroke3 LINE_QUARTER = new Stroke3(0.25, false);
  public static final Stroke3 LINE_HALF = new Stroke3(0.5, false);
  public static final Stroke3 LINE = new Stroke3(1.0, false);
  public static final Stroke3 LINE1_5 = new Stroke3(1.5, false);
  public static final Stroke3 LINE2 = new Stroke3(2.0, false);
  public static final Stroke3 LINE3 = new Stroke3(3.0, false);
  public static final Stroke3 DASHED_LINE = new Stroke3(1.0, false, true);
  public static final Stroke3 DASHED_LINE1_5 = new Stroke3(1.5, false, true);
  public static final Stroke3 DASHED_LINE2 = new Stroke3(2.0, false, true);
  public static final Stroke3 DASHED_LINE3 = new Stroke3(3.0, false, true);

  public Stroke3()
  {
    super(1f, CAP_BUTT, JOIN_MITER);
  }

  public Stroke3(BasicStroke stroke)
  {
    super(stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
  }

  public Stroke3(float line, int cap, int join, float miterlimit, float phase, float[] dash)
  {
    super(line, cap, join, miterlimit, normalizeDash(dash), phase);
  }

  public Stroke3(float line, int cap, int join, float phase, float[] dash)
  {
    super(line, cap, join, 10.0f, normalizeDash(dash), phase);
  }

  public Stroke3(double line)
  {
    super((float) line);
  }

  public Stroke3(double line, boolean round)
  {
    super((float) line, round ? CAP_ROUND : CAP_BUTT, JOIN_ROUND);
  }

  public Stroke3(double line, boolean round, boolean stroked)
  {
    super((float) line, round ? CAP_ROUND : CAP_BUTT, JOIN_MITER, 10.0f, stroked ? new float[]
    { (float) line * 2.0f, (float) line * 2.0f } : null, 0.0f);
  }

  public Stroke3(double line, String cap, String join, double phase, float[] dash)
  {
    super((float) line, cap(cap), join(join), 10f, normalizeDash(dash), (float) phase);
  }

  public static float[] normalizeDash(float[] dash)
  {
    if (dash == null || dash.length == 0)
      return null;
    for (float d : dash)
      if (!Float.isNaN(d) && d != 0)
        return dash;
    return null;
  }

  public Stroke3 derive(double width)
  {
    return new Stroke3((float) width, getEndCap(), getLineJoin(), getMiterLimit(), getDashPhase(), getDashArray());
  }

  public Stroke3 widen(double delta)
  {
    return new Stroke3(getLineWidth() + (float) delta, getEndCap(), getLineJoin(), getMiterLimit(), getDashPhase(), getDashArray());
  }

  private static float phase(String xml)
  {
    double[] a = Nb.toDoubles(xml);
    return a.length > 0 ? (float) a[0] : 0;
  }

  private static float[] dash(String xml)
  {
    double[] a = Nb.toDoubles(xml);
    float[] dash = new float[a.length < 1 ? 0 : a.length - 1];
    for (int i = 1; i < a.length; i++)
      dash[i - 1] = (float) a[i];
    return Zen.Array.isZero(dash) ? null : dash;
  }

  public boolean hasDash()
  {
    float[] dash = this.dash();
    if (dash == null)
      return false;
    if (dash.length == 1)
      if (dash[0] == 0f || Float.isNaN(dash[0]))
        return false;
    return true;
  }

  public float[] dash()
  {
    return this.getDashArray();
  }

  public Double[] fxDash()
  {
    float[] dash = this.getDashArray();
    Double[] fx = new Double[dash.length];
    for (int i = 0; i < dash.length; i++)
      fx[i] = (double) dash[i];
    return fx;
  }

  public float[] svgDash()
  {
    float[] dash = this.getDashArray();
    if (dash == null || dash.length == 0 || dash.length == 1 && dash[0] == 0)
      return new float[0];
    if (dash[0] < 0.001f)// SVG does ensure that dash having 0 length are drawn
                         // (Chrome does not, Explorer does), whereas Java or
                         // PDF do !!!...
      dash[0] = 0.001f;
    return dash;
  }

  public float[] nanDash()
  {
    float[] dash = this.getDashArray();
    return dash == null || dash.length == 0 || dash.length == 1 && dash[0] == 0 ? Zen.Array.Floats(Float.NaN) : dash;
  }

  public float phase()
  {
    return this.getDashPhase();
  }

  public StrokeLineCap fxCap()
  {
    switch (this.getEndCap())
    {
    case BasicStroke.CAP_BUTT:
      return StrokeLineCap.BUTT;
    case BasicStroke.CAP_ROUND:
      return StrokeLineCap.ROUND;
    case BasicStroke.CAP_SQUARE:
      return StrokeLineCap.SQUARE;
    default:
      return StrokeLineCap.BUTT;
    }
  }

  public StrokeLineJoin fxJoin()
  {
    switch (this.getLineJoin())
    {
    case BasicStroke.JOIN_MITER:
      return StrokeLineJoin.MITER;
    case BasicStroke.JOIN_ROUND:
      return StrokeLineJoin.ROUND;
    case BasicStroke.JOIN_BEVEL:
      return StrokeLineJoin.BEVEL;
    default:
      return StrokeLineJoin.MITER;
    }
  }

  public String cap()
  {
    return cap(this.getEndCap());
  }

  public String join()
  {
    return join(this.getLineJoin());
  }

  public static String cap(int cap)
  {
    return cap == CAP_BUTT ? BUTT : cap == CAP_ROUND ? ROUND : SQUARE;
  }

  public static String join(int join)
  {
    return join == JOIN_BEVEL ? BEVEL : join == JOIN_ROUND ? ROUND : MITER;
  }

  public static short cap(String cap)
  {
    return cap == null ? CAP_SQUARE : cap.equals(BUTT) ? (short) CAP_BUTT : cap.equals(ROUND) ? (short) CAP_ROUND : (short) CAP_SQUARE;
  }

  public static short join(String join)
  {
    return join == null ? JOIN_MITER : join.equals(BEVEL) ? (short) JOIN_BEVEL : join.equals(ROUND) ? (short) JOIN_ROUND : (short) JOIN_MITER;
  }

  public float miter()
  {
    return this.getMiterLimit();
  }

  public float pen()
  {
    return this.getLineWidth();
  }

  public float width()
  {
    return this.getLineWidth();
  }

  public Stroke3 copy()
  {
    return new Stroke3(width(), getEndCap(), getLineJoin(), getMiterLimit(), getDashPhase(), getDashArray());
  }

  public void into(FxPath path)
  {
    path.setStrokeWidth(this.width());
    path.setStrokeLineCap(this.fxCap());
    path.setStrokeLineJoin(this.fxJoin());
    if (this.hasDash())
    {
      path.setStrokeDashOffset(this.phase());
      path.getStrokeDashArray().addAll(fxDash());
    }
  }

  public static Stroke3 get(double thick, String css)
  {
    float s = (float)thick;
    switch (css)
    {
    case "none":
      return null;
    case "solid":
      return new Stroke3(thick);
    case "dashed":
      return new Stroke3(s, CAP_BUTT, JOIN_MITER, 0, new float[]{4*s, s});
    case "dotted":
      return new Stroke3(s, CAP_BUTT, JOIN_MITER, 0, new float[]{s, s});
    default:
      return null;
    }
  }
}
