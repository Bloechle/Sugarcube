package sugarcube.common.ui.fx.shapes;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxTrack extends Polyline
{
  
  public FxTrack()
  {
    
  }
  
  public FxTrack add(double x, double y)
  {
    this.getPoints().add(x);
    this.getPoints().add(y);
    return this;
  }

  public FxTrack opacity(double alpha)
  {
    this.setOpacity(alpha);
    return this;
  }

  public FxTrack paint(Color3 fillStroke, double pen)
  {
    return this.paint(fillStroke, fillStroke, pen);
  }

  public FxTrack paint(Color3 fill, Color3 stroke, double pen)
  {
    return this.fill(fill).stroke(stroke).pen(pen);
  }

  public FxTrack paint(Paint fill, Paint stroke, double pen)
  {
    return this.fill(fill).stroke(stroke).pen(pen);
  }

  public FxTrack fill(Color3 c)
  {
    return c == null ? frame() : this.fill(c.fx());
  }

  public FxTrack fill(Paint p)
  {
    this.setFill(p);
    return this;
  }

  public FxTrack frame()
  {
    this.setFill(null);
    return this;
  }

  public FxTrack noFill()
  {
    this.setFill(null);
    return this;
  }

  public FxTrack glass()
  {
    this.setFill(Color3.TRANSPARENT.fx());
    return this;
  }

  public FxTrack dash(double line, double space)
  {
    this.getStrokeDashArray().addAll(line, space);
    return this;
  }

  public FxTrack stroke(Color3 c)
  {
    return this.stroke(c == null ? null : c.fx());
  }

  public FxTrack stroke(Paint p)
  {
    this.setStroke(p);
    return this;
  }

  public FxTrack stroke(Color3 c, double pen)
  {
    this.stroke(c);
    this.pen(pen);
    return this;
  }
  
  public FxTrack stroke(Paint c, double pen)
  {
    this.stroke(c);
    this.pen(pen);
    return this;
  }

  public FxTrack pen(double w)
  {
    this.setStrokeWidth(w);
    return this;
  }
  
  public FxTrack capJoinRound()
  {
    return capRound().joinRound();
  }
  
  public FxTrack joinRound()
  {
    this.setStrokeLineJoin(StrokeLineJoin.ROUND);
    return this;
  }

  public FxTrack capRound()
  {
    this.setStrokeLineCap(StrokeLineCap.ROUND);
    return this;
  }

  public FxTrack style(String style)
  {
    return (FxTrack) FxCSS.Style(this, style, false);
  }

  public FxTrack mouseTransparent()
  {
    this.setMouseTransparent(true);
    return this;
  }

}
