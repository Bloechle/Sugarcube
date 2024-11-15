package sugarcube.common.ui.fx.controls;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxComboBox<T> extends ComboBox<T>
{
  public FxComboBox()
  {

  }

  public FxComboBox<T> style(String style)
  {
    FxCSS.Style(this, style);
    return this;
  }

  public FxComboBox<T> prompt(String text)
  {
    this.setPromptText(text);
    return this;
  }

  public FxComboBox<T> listen(ChangeListener<T> listener)
  {
    this.getSelectionModel().selectedItemProperty().addListener(listener);
    return this;
  }

  public int size()
  {
    return this.getItems().size();
  }

  public boolean isEmpty()
  {
    return this.getItems().isEmpty();
  }

  public T itemAt(int index)
  {
    return this.getItems().get(index);
  }

  public void clearItems()
  {
    this.getItems().clear();
  }

  public void addItem(T item)
  {
    this.getItems().add(item);
  }

  public SingleSelectionModel<T> model()
  {
    return this.getSelectionModel();
  }

  public void setSelectedIndex(int index)
  {
    SingleSelectionModel<T> model = model();
    if (index == model.getSelectedIndex())
      model.clearAndSelect(index);
    else
      model.select(index);
  }

  public FxComboBox<T> tip(String tip)
  {
    Fx.Tooltip(tip, this);
    return this;
  }
}
