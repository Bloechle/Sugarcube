package sugarcube.common.ui.fx.controls;

import javafx.scene.control.ToggleButton;
import sugarcube.common.system.log.Log;

public class FxToggles
{
  public interface Toggled
  {
    public void toggled(ToggleButton toggle);
  }

  private boolean mono = true;

  public ToggleButton[] toggles;
  public Toggled toggled;

  public FxToggles(ToggleButton... toggles)
  {
    this.toggles = toggles;

    for (ToggleButton bt : toggles)
    {
      if (bt == null)
        Log.debug(this, " - null toggle button");
      else
        bt.setOnAction((event) -> act(bt));
    }
  }

  public static FxToggles get(ToggleButton... toggles)
  {
    return new FxToggles(toggles);
  }

  public FxToggles handle(Toggled toggled)
  {
    this.toggled = toggled;
    return this;
  }

  public void act(ToggleButton bt)
  {
    if (mono && bt.isSelected())
      for (ToggleButton toggle : toggles)
        if (toggle != bt)
          toggle.selectedProperty().set(false);

    if (mono && !bt.isSelected())
    {
      bt.selectedProperty().set(true);
      return;
    }
    if (toggled != null)
      toggled.toggled(bt);
  }

  public FxToggles deselect()
  {
    for (ToggleButton toggle : toggles)
      toggle.selectedProperty().set(false);
    return this;
  }

  public ToggleButton selected()
  {
    for (ToggleButton bt : toggles)
      if (bt.selectedProperty().get())
        return bt;
    return null;
  }

  public boolean isSelected()
  {
    return selected() != null;
  }
  
  public static FxToggles Handle(ToggleButton bt1, Toggled toggled)
  {
    return new FxToggles(bt1).handle(toggled);
  }

  public static FxToggles Handle(ToggleButton bt1, ToggleButton bt2, Toggled toggled)
  {
    return new FxToggles(bt1, bt2).handle(toggled);
  }

  public static FxToggles Handle(ToggleButton bt1, ToggleButton bt2, ToggleButton bt3, Toggled toggled)
  {
    return new FxToggles(bt1, bt2, bt3).handle(toggled);
  }

  public static FxToggles Handle(ToggleButton bt1, ToggleButton bt2, ToggleButton bt3, ToggleButton bt4, Toggled toggled)
  {
    return new FxToggles(bt1, bt2, bt3, bt4).handle(toggled);
  }

  public static FxToggles Handle(ToggleButton bt1, ToggleButton bt2, ToggleButton bt3, ToggleButton bt4, ToggleButton bt5, Toggled toggled)
  {
    return new FxToggles(bt1, bt2, bt3, bt4, bt5).handle(toggled);
  }

  public static FxToggles Handle(ToggleButton bt1, ToggleButton bt2, ToggleButton bt3, ToggleButton bt4, ToggleButton bt5, ToggleButton bt6,
      Toggled toggled)
  {
    return new FxToggles(bt1, bt2, bt3, bt4, bt5, bt6).handle(toggled);
  }
}
