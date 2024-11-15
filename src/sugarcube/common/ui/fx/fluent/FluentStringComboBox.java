package sugarcube.common.ui.fx.fluent;

import javafx.scene.control.ComboBox;
import sugarcube.common.interfaces.StringChanged;

public class FluentStringComboBox extends FluentComboBox<String>
{

  public FluentStringComboBox(ComboBox<String> comboBox)
  {
    super(comboBox);
  }

  public FluentStringComboBox changes(StringChanged handler)
  {
    combo.valueProperty().addListener((obs, old, val) -> handler.stringChanged(val));
    combo.getEditor().textProperty().addListener((old, obs, val) -> handler.stringChanged(val));
    return this;
  }

}
