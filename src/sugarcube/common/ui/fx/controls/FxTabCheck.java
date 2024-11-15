package sugarcube.common.fx.controls;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import sugarcube.common.ui.fx.beans.PBool;
import sugarcube.common.ui.fx.controls.FxCheckBox;

public class FxTabCheck extends FxCheckBox
{
  private Tab tab;

  public FxTabCheck(Tab tab)
  {
    this.tab = tab;
  }

  public FxTabCheck bind(ObservableValue<? extends Boolean> obs)
  {
    this.selectedProperty().bind(obs);
    return this;
  }

  public FxTabCheck disable(boolean disable)
  {    
    this.tab.getContent().setDisable(disable);
    return this;
  }
  
  public FxTabCheck synchronize()
  {
    return this.disable(!this.isSelected());
  }
  
  public FxTabCheck select(boolean bool)
  {
    this.setSelected(bool);
    this.disable(!bool);
    return this;
  }

  public static FxTabCheck Inject(Tab tab, final PBool bool)
  {
    try
    {
      final FxTabCheck check = new FxTabCheck(tab);
      check.select(bool.get());      
      
      check.setOnAction(e -> {
        if (!tab.isSelected())
          tab.getTabPane().getSelectionModel().select(tab);
        bool.set(check.isSelected());
        check.synchronize();        
      });
      tab.setGraphic(check);
      return check;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

}
