package sugarcube.common.ui.fx.controls;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import sugarcube.common.system.log.Log;
import sugarcube.common.interfaces.UpdateItem;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxListCell<T> extends ListCell<T> implements UpdateItem<T>
{
  public interface Celler<T>
  {
    public void cell(FxListCell<T> cell, T item);
  }
  
  public Celler<T> celler;
  public T item;

  public FxListCell()
  {
    
  }
    
  public FxListCell(Celler<T> celler)
  {
    this.celler = celler;
  }
  
  public FxListCell style(String style)
  {
    FxCSS.Style(this,  style);
    return this;
  }  
  
  public T item()
  {
    return this.getItem();
  }
  
  public ObservableList<T> items()
  {
    return this.getListView().getItems();
  }

  @Override
  public void updateItem(T item, boolean empty)
  {
    super.updateItem(item, empty);
    this.item = item;
    if (empty)
    {
      this.item = null;
      this.setText("");
      this.setGraphic(null);
    } else if (item != null)
      update(item);
    else
      Log.debug(this, ".updateItem - null item");
  }

  @Override
  public void update(T item)
  {
    if (celler != null)
      celler.cell(this, item);
  }

  public void set(String text, Node graphic)
  {
    this.setText(text);
    this.setGraphic(graphic);
  }

}
