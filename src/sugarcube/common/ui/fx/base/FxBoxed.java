package sugarcube.common.ui.fx.base;

import javafx.scene.paint.Color;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.shapes.FxRect;

public class FxBoxed extends FxGroup
{
  public static final Color GLASS = Color3.GLASS.fx();

  public FxRect box = new FxRect();

  public FxBoxed()
  {
    this.box.fill(GLASS);
  }

  public FxBoxed(boolean boxed)
  {
    this();
    if (boxed)
      this.boxing(null, false);
  }
  
  public FxBoxed mouseTransparent()
  {
    this.mouseTransparent(true);
    return this;
  }

  public FxBoxed mouseTransparent(boolean value)
  {
    this.setMouseTransparent(false);
    this.box.setMouseTransparent(value);
    return this;
  }

  public FxHandle handle()
  {
    return FxHandle.Get(box);
  }

  public void handleMouseEvents()
  {    
    this.setMouseTransparent(false);
    this.box.setMouseTransparent(false);
    this.handle().mouse(ms->mouseEvent(ms));
  }

  public FxRect boxing(Rectangle3 bounds)
  {
    return boxing(bounds, false);
  }

  public FxRect boxing(Rectangle3 bounds, boolean first)
  {
    // Log.debug(this, ".boxing - "+node.tag+": "+bounds);
    if (bounds != null)
      box.set(bounds);
    if (!children().contains(box))
      if (first)
        children().add(0, box);
      else
        children().add(box);
    return box;
  }

  public void mouseEvent(FxMouse ms)
  {

  }

  public FxBoxed update(Rectangle3 box)
  {
    this.box.set(box);
    return this;
  }

  @Override
  public FxBoxed style(String style)
  {
    FxCSS.Style(this, style, false);
    return this;
  }

  public FxBoxed style(String style, boolean restyle)
  {
    FxCSS.Style(this, style, restyle);
    return this;
  }

  public FxBoxed refresh()
  {
    return this;
  }

}
