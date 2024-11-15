package sugarcube.common.ui.fx.fluent;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class FluentButton extends FxFluent
{
  private Button button;

  public FluentButton(Button button)
  {
    this.button = button;
  }


  public FluentButton handle(EventHandler<ActionEvent> handler)
  {    
    button.setOnAction(handler); 
    return this;
  }

}