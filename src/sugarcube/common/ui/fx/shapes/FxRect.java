package sugarcube.common.ui.fx.shapes;

import javafx.geometry.Bounds;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxCSS;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.event.FxMouse;

public class FxRect extends Rectangle
{
  public FxRect()
  {
    super();
  }

  public FxRect(Rectangle3 r)
  {
    super(r.x, r.y, r.width, r.height);
  }

  public FxRect(double w, double h)
  {
    super(w, h);
  }

  public FxRect(double x, double y, double w, double h)
  {
    super(x, y, w, h);
  }

  public FxHandle handle()
  {
    return FxHandle.Get(this);
  }

  public FxRect handleClick()
  {
    handle().click((ms) -> mouseClicked(ms));
    return this;
  }

  public void mouseClicked(FxMouse mouse)
  {

  }

  public FxRect set(Rectangle3 box)
  {
    return this.set(box.x, box.y, box.width, box.height);
  }

  public FxRect set(Bounds box)
  {
    return this.set(box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
  }

  public FxRect set(double x, double y, double w, double h)
  {
    this.setX(x);
    this.setY(y);
    this.setWidth(w);
    this.setHeight(h);
    return this;
  }

  public FxRect arc(double v)
  {
    return arc(v, v);
  }

  public FxRect arc(double w, double h)
  {
    this.setArcWidth(w);
    this.setArcHeight(h);
    return this;
  }

  public FxRect opacity(double alpha)
  {
    this.setOpacity(alpha);
    return this;
  }

  public FxRect paint(Color3 fillStroke, double pen)
  {
    return this.paint(fillStroke, fillStroke, pen);
  }

  public FxRect paint(Color3 fill, Color3 stroke, double pen)
  {
    return this.fill(fill).stroke(stroke).pen(pen);
  }
  
  public FxRect paint(Paint fill, Paint stroke, double pen)
  {
    return this.fill(fill).stroke(stroke).pen(pen);
  }

  public FxRect fill(Color3 c)
  {
    return c == null ? frame() : this.fill(c.fx());
  }

  public FxRect fill(Paint p)
  {
    this.setFill(p);
    return this;
  }

  public FxRect frame()
  {
    this.setFill(null);
    return this;
  }

  public FxRect noFill()
  {
    this.setFill(null);
    return this;
  }

  public FxRect glass()
  {
    this.setFill(Color3.TRANSPARENT.fx());
    return this;
  }

  public FxRect dash(double line, double space)
  {
    this.getStrokeDashArray().addAll(line, space);
    return this;
  }

  public FxRect stroke(Color3 c)
  {
    return this.stroke(c == null ? null : c.fx());
  }

  public FxRect stroke(Paint p)
  {
    this.setStroke(p);
    return this;
  }

  public FxRect stroke(Color3 c, double pen)
  {
    this.stroke(c);
    this.pen(pen);
    return this;
  }
  
  public FxRect stroke(Paint c, double pen)
  {
    this.stroke(c);
    this.pen(pen);
    return this;
  }

  public FxRect pen(double w)
  {
    this.setStrokeWidth(w);
    return this;
  }

  public FxRect capRound()
  {
    this.setStrokeLineCap(StrokeLineCap.ROUND);
    return this;
  }

  public FxRect style(String style)
  {
    return (FxRect) FxCSS.Style(this, style, false);
  }

  public FxRect mouseTransparent()
  {
    this.setMouseTransparent(true);
    return this;
  }

  public boolean contains(Point3 p)
  {
    return super.contains(p.getX(), p.getY());
  }

  public double x()
  {
    return this.xProperty().get();
  }

  public double y()
  {
    return this.yProperty().get();
  }

  public double width()
  {
    return this.widthProperty().get();
  }

  public double height()
  {
    return this.heightProperty().get();
  }

  public double maxX()
  {
    return x() + width();
  }

  public double maxY()
  {
    return y() + height();
  }

  public double cx()
  {
    return x() + width() / 2.0;
  }

  public double cy()
  {
    return y() + height() / 2.0;
  }

  public Rectangle3 rectangle()
  {
    return new Rectangle3(x(), y(), width(), height());
  }

  public static FxRect New(double x, double y, double w, double h)
  {
    return new FxRect(x, y, w, h);
  }
  
  public static FxRect FromCircle(Point3 center, double radius)  
  {
    return new FxRect(center.x-radius, center.y-radius, 2*radius, 2*radius);
  }

}
