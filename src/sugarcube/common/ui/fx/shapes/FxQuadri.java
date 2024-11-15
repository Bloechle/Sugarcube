package sugarcube.common.ui.fx.shapes;

import javafx.scene.paint.Paint;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxQuadri extends FxPoly
{
  public FxQuadri(Point3... points)
  {
    super(points);
  }

  public FxQuadri noMouse()
  {
    this.setMouseTransparent(true);
    return this;
  }

  public FxQuadri fill(Color3 color)
  {
    return fill(color == null ? null : color.fx());
  }

  public FxQuadri fill(Paint p)
  {
    this.setFill(p);
    return this;
  }

  public FxQuadri noFill()
  {
    this.setFill(null);
    return this;
  }

  public FxQuadri stroke(Color3 color)
  {
    return this.stroke(color.fx());
  }

  public FxQuadri noStroke()
  {
    this.setStroke(null);
    return this;
  }

  public FxQuadri stroke(Paint p)
  {
    this.setStroke(p);
    return this;
  }

  public FxQuadri pen(double size)
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

  public static FxQuadri New(Point3... points)
  {
    return new FxQuadri(points);
  }
}
