package sugarcube.common.ui.fx.shapes;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxLine extends Line
{
  public FxLine()
  {

  }

  public FxLine(Point3 p1, Point3 p2)
  {
    super(p1.x, p1.y, p2.x, p2.y);
  }

  public FxLine(double x1, double y1, double x2, double y2)
  {
    super(x1, y1, x2, y2);
  }

  public FxLine(double x1, double y1, double x2, double y2, Color color)
  {
    super(x1, y1, x2, y2);
    this.fillProperty().set(color);
    this.strokeProperty().set(color);
  }

  public FxLine(double x1, double y1, double x2, double y2, Color color, double stroke)
  {
    this(x1, y1, x2, y2, color);
    this.strokeWidthProperty().set(stroke);
  }

  public FxLine set(double x1, double y1, double x2, double y2)
  {
    setStartX(x1);
    setStartY(y1);
    setEndX(x2);
    setEndY(y2);
    return this;
  }

  public FxLine set(Line3 line)
  {
    this.set(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    return this;
  }
  
  public FxLine opacity(double opacity)
  {
    this.setOpacity(opacity);
    return this;
  }

  public FxLine fill(Paint p)
  {
    this.setFill(p);
    return this;
  }

  public FxLine fill(Color3 c)
  {
    return fill(c.fx());
  }

  public FxLine stroke(Paint p)
  {
    this.setStroke(p);
    return this;
  }

  public FxLine stroke(Color3 c)
  {
    return stroke(c.fx());
  }

  public FxLine dash(double line, double space)
  {
    this.getStrokeDashArray().addAll(line, space);
    return this;
  }

  public FxLine pen(double w)
  {
    this.setStrokeWidth(w);
    return this;
  }

  public FxLine capRound()
  {
    this.setStrokeLineCap(StrokeLineCap.ROUND);
    return this;
  }

  public FxLine capButt()
  {
    this.setStrokeLineCap(StrokeLineCap.BUTT);
    return this;
  }

  public FxLine capSquare()
  {
    this.setStrokeLineCap(StrokeLineCap.SQUARE);
    return this;
  }

  public FxLine style(String style)
  {
    return (FxLine) FxCSS.Style(this, style, false);
  }

  public double x1()
  {
    return this.startXProperty().get();
  }

  public double y1()
  {
    return this.startYProperty().get();
  }

  public double x2()
  {
    return this.endXProperty().get();
  }

  public double y2()
  {
    return this.endYProperty().get();
  }

  public boolean equals(FxLine line, double e)
  {
    return equals(line.x1(), line.y1(), line.x2(), line.y2(), e);
  }

  public boolean equals(double x1, double y1, double x2, double y2, double e)
  {
    return Math.round(x1() - x1) <= e && Math.round(x2() - x2) <= e && Math.round(y1() - y1) <= e && Math.round(y2() - y2) <= e;
  }

  public FxLine mouseTransparent()
  {
    return this.mouseTransparent(true);
  }

  public FxLine mouseTransparent(boolean transparent)
  {
    this.setMouseTransparent(transparent);
    return this;
  }

  public static FxLine New(Point3 p1, Point3 p2)
  {
    return new FxLine(p1, p2);
  }
}
