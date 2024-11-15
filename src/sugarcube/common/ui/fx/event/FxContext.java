package sugarcube.common.ui.fx.event;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ContextMenuEvent;
import sugarcube.common.graphics.geom.Point3;

public class FxContext extends FxInput<ContextMenuEvent>
{
  public FxContext(ContextMenuEvent e, String state, Node source)
  {
    super(e, state, source);
  }
  
  public FxContext(ContextMenuEvent e, String state, Scene source)
  {
    super(e, state, source);
  }

  @Override
  public boolean isContextMenuEvent()
  {
    return true;
  }

  public int x()
  {
    return (int) Math.round(event.getX());
  }

  public int y()
  {
    return (int) Math.round(event.getY());
  }

  @Override
  public Point3 xy()
  {
    return new Point3(event.getX(), event.getY());
  }
  
  @Override
  public Point3 screenXY()
  {
    return new Point3(event.getScreenX(), event.getScreenY());
  }   
    

}