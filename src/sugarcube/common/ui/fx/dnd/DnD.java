package sugarcube.common.ui.fx.dnd;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;

import java.util.Arrays;
import java.util.Iterator;

public class DnD<T> implements Iterable<T>
{
  private T[] data;
  private DragEvent event;

  public DnD(DragEvent event, T... data)
  {
    this.event = event;
    this.data = data;
  }
  
  public int size()
  {
    return data==null ? 0 : data.length;
  }

  public T[] data()
  {
    return data;
  }

  public T value()
  {
    return data == null ? null : data.length > 0 ? data[0] : null;
  }

  public DragEvent event()
  {
    return event;
  }

  public Dragboard dragboard()
  {
    return event.getDragboard();
  }

  public boolean isConsumed()
  {
    return event != null && event.isConsumed();
  }

  public DnD<T> consume()
  {
    if (event != null)
      this.event.consume();
    return this;
  }
  
  public boolean consumer()
  {
    boolean ok = !this.isConsumed();
    if(!ok)
      this.consume();
    return ok;
  }

  @Override
  public Iterator<T> iterator()
  {
    return Arrays.asList(data).iterator();
  }
}
