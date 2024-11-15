package sugarcube.common.ui.fx.controls;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.beans.PBool;
import sugarcube.common.ui.fx.containers.FxHBox;
import sugarcube.common.ui.fx.containers.FxVBox;
import sugarcube.common.ui.fx.event.FxHandle;

public class FxSwitch extends FxVBox
{
  private PBool on = PBool.True();
  private FxHBox box = new FxHBox();
  private FxLabel label = new FxLabel();
  private FxButton button = new FxButton();
  private String textOn = "ON";
  private String textOff = "OFF";
  private String color = "#3A3";

  public FxSwitch()
  {
    this(true);
  }

  public FxSwitch(boolean isOn)
  {
    this.getStyleClass().add("sc-switch");
    Fx.width(50, label, button);
    Fx.height(25, label, button, box);
    FxHandle.Get(box).click(e -> on.set(!on.get()));
    this.add(box);
    box.set(label, button);
    box.setAlignment(Pos.CENTER);
    button.setMouseTransparent(true);
    label.setAlignment(Pos.CENTER);
    on.addListener((obs, old, val) -> restyle(val));
    on.set(isOn);
    restyle(isOn);
  }
  
  public FxSwitch color(String color)
  {
    this.color = color;
    return this;
  }
  
  public FxSwitch color(Color3 color)
  {
    this.color = color.cssRGBAValue();
    return this;
  }

  public FxSwitch text(String on, String off)
  {
    this.textOn = on;
    this.textOff = off;
    restyle(isOn());
    return this;
  }

  private void restyle(boolean on)
  {
    if (on)
    {
      label.setText(textOn);
      label.style("-fx-text-fill:white;-fx-font-weight:bold;");
      box.setStyle("-fx-background-radius: 4px;-fx-background-color: " + color
          + ";  -fx-border-style: solid;-fx-border-radius: 4px; -fx-border-width: 1px; -fx-border-color: rgba(255, 255, 255, 0.4);");
      box.set(label, button);
    } else
    {
      label.setText(textOff);
      label.style("-fx-text-fill:rgba(255,255,255,0.9);-fx-font-weight:bold;");
      box.setStyle(
          "-fx-background-radius: 4px;-fx-background-color: rgba(0,0,0,0.1);-fx-border-radius: 4px; -fx-border-style: solid; -fx-border-width: 1px; -fx-border-color: rgba(255, 255, 255, 0.4);");
      box.set(button, label);
    }
  }

  public boolean isOn()
  {
    return on.get();
  }

  public SimpleBooleanProperty on()
  {
    return on;
  }

  public SimpleBooleanProperty onProperty()
  {
    return on;
  }
}