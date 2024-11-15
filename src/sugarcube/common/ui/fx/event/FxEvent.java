package sugarcube.common.ui.fx.event;

import javafx.event.Event;
import sugarcube.common.data.collections.Str;

public class FxEvent<T extends Event>
{
  public static final String CONSUMED = "consumed";

  protected String state = "";
  protected T event;

  public FxEvent(T event)
  {
    this.event = event;
  }

  public T event()
  {
    return event;
  }

  public String state()
  {
    return state;
  }

  public FxEvent<T> consume()
  {
    this.state = CONSUMED;
    this.event.consume();
    return this;
  }

  public boolean isConsumed()
  {
    return Str.Equals(state, CONSUMED);
  }
}
