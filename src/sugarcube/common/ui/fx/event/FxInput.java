package sugarcube.common.ui.fx.event;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ContextMenuEvent;
import sugarcube.common.graphics.geom.Point3;

public class FxInput<T extends Event> extends FxEvent<T>
{
  public static final String UNDEF = "undef";
  public static final String CONTEXT = "context";

  protected Scene scene;
  protected Node source;

  protected String state;

  public FxInput(T event)
  {
    this(event, null, (Node) null);
  }

  public FxInput(T event, String state, Node source)
  {
    super(event);
    this.state = state == null ? UNDEF : state;
    this.source = source;
  }

  public FxInput(T event, String state, Scene scene)
  {
    super(event);
    this.state = state == null ? UNDEF : state;
    this.scene = scene;
  }

  public Scene scene()
  {
    return scene;
  }

  public Node source()
  {
    return source;
  }

  public Point3 xy()
  {
    return new Point3(0, 0);
  }

  public Point3 screenXY()
  {
    return xy();
  }

  @Override
  public String state()
  {
    return state;
  }

  public boolean isState(String state)
  {
    return state != null && state.equals(this.state);
  }

  public boolean isContextMenuEvent()
  {
    return this.event instanceof ContextMenuEvent;
  }

}
