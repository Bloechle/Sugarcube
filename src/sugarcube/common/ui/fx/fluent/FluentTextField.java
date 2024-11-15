package sugarcube.common.ui.fx.fluent;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import sugarcube.common.interfaces.Actable;

public class FluentTextField extends FxFluent
{
  private TextField field;

  public FluentTextField(TextField field)
  {
    this.field = field;
  }


  public FluentTextField listen(ChangeListener<String> listener)
  {
    field.textProperty().addListener(listener);    
    return this;
  }
  
  public FluentTextField handle(Actable actor)
  {
    field.textProperty().addListener((old,obs,val)->actor.act());    
    return this;
  }

}