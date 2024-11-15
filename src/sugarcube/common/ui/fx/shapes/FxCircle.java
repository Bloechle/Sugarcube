package sugarcube.common.ui.fx.shapes;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxCircle extends Circle
{

  public FxCircle()
  {
    this(0, 0, 1);
  }

  public FxCircle(Point3 p, double r)
  {
    this(p.getX(), p.getY(), r);
  }

  public FxCircle(double x, double y, double r)
  {
    super(x, y, r);
  }

  public Point3 center3()
  {
    return new Point3(getCenterX(), getCenterY());
  }

  public FxCircle noMouse()
  {
    this.setMouseTransparent(true);
    return this;
  }
  
  public FxCircle hide()
  {
    return visible(false);
  }

  public FxCircle show()
  {
    return visible(true);
  }

  public FxCircle visible(boolean isVisible)
  {
    if (this.isVisible() != isVisible)
      this.setVisible(isVisible);
    return this;
  }

  public FxCircle opacity(double opacity)
  {
    this.setOpacity(opacity);
    return this;
  }

  public FxCircle fill(Color3 color)
  {
    return fill(color == null ? null : color.fx());
  }

  public FxCircle fill(Paint p)
  {
    this.setFill(p);
    return this;
  }

  public FxCircle noFill()
  {
    this.setFill(null);
    return this;
  }

  public FxCircle stroke(Color3 color)
  {
    return this.stroke(color.fx());
  }

  public FxCircle noStroke()
  {
    this.setStroke(null);
    return this;
  }

  public FxCircle stroke(Paint p)
  {
    this.setStroke(p);
    return this;
  }

  public FxCircle pen(double size)
  {
    this.setStrokeWidth(size);
    return this;
  }

  public FxCircle style(String style)
  {
    return (FxCircle) FxCSS.Style(this, style);
  }

  public FxCircle restyle(String style)
  {
    return (FxCircle) FxCSS.Style(this, style, true);
  }

  public Point3 c()
  {
    return center3();
  }

  public double x()
  {
    return this.centerXProperty().get();
  }

  public double y()
  {
    return this.centerYProperty().get();
  }

  public double r()
  {
    return this.radiusProperty().get();
  }

  public FxCircle set(Point3 p)
  {
    return this.set(p.x, p.y);
  }

  public FxCircle set(double x, double y)
  {
    this.centerXProperty().set(x);
    this.centerYProperty().set(y);
    return this;
  }

  public FxCircle set(double x, double y, double r)
  {
    this.centerXProperty().set(x);
    this.centerYProperty().set(y);
    this.radiusProperty().set(r);
    return this;
  }

  public boolean contains(Point3 p)
  {
    double x = x();
    double y = y();
    double r = r();
    return p.x >= x - r && p.x <= x + r && p.y >= y - r && p.y <= y + r;
  }

  public static FxCircle New(Point3 p, double r)
  {
    return new FxCircle(p, r);
  }

  public static FxCircle New(double x, double y, double r)
  {
    return new FxCircle(x, y, r);
  }
}
