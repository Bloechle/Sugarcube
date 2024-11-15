package sugarcube.common.ui.fx.fluent;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class FluentLV<T>  extends FxFluent
{
  protected ListView<T> listView;

  public FluentLV(ListView<T> listView)
  {
    this.listView = listView;
  }
  
  public FluentLV<T> cell(Callback<ListView<T>, ListCell<T>> callback)
  {    
    this.listView.setCellFactory(callback);
    return this;
  }
  
  public FluentLV<T> multiple()
  {
    return multiple(true);
  }
  
  public FluentLV<T> single()
  {
    return multiple(false);
  }
  
  public FluentLV<T> items(ObservableList<T> list)
  {
    this.listView.setItems(list);
    return this;
  }

  public FluentLV<T> multiple(boolean multiple)
  {
    this.listView.getSelectionModel().setSelectionMode(multiple ? SelectionMode.MULTIPLE : SelectionMode.SINGLE);
    return this;
  }

  public FluentLV<T> listen(ChangeListener<T> listener)
  {
    listView.getSelectionModel().selectedItemProperty().addListener(listener);
    return this;
  }

  public void onMouseClicked(int nbOfClicks, EventHandler<? super MouseEvent> handler)
  {
    listView.setOnMouseClicked(e -> {
      if (e.getClickCount() == nbOfClicks)
        handler.handle(e);
    });
  }

}
