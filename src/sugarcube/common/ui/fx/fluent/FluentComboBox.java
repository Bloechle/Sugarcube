package sugarcube.common.ui.fx.fluent;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;

public class FluentComboBox<T> extends FxFluent
{
  protected ComboBox<T> combo;

  public FluentComboBox(ComboBox<T> combo)
  {
    this.combo = combo;
  }

  public FluentComboBox<T> clear()
  {
    this.combo.getItems().clear();
    return this;
  }

  public FluentComboBox<T> def(T def)
  {
    combo.setValue(def);
    return this;
  }

  public FluentComboBox<T> items(T... items)
  {
    this.def(items[0]);
    combo.getItems().addAll(items);
    return this;
  }

  public FluentComboBox<T> listen(ChangeListener<T> listener)
  {
    combo.getSelectionModel().selectedItemProperty().addListener(listener);
    return this;
  }
  
  public FluentComboBox<T> handle(ChangeListener<T> listener)
  {    
    combo.valueProperty().addListener(listener);
    return this;
  }

}
