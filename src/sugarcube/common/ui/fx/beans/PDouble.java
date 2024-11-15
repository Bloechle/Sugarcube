package sugarcube.common.ui.fx.beans;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Slider;
import sugarcube.common.data.xml.Nb;

public class PDouble extends SimpleDoubleProperty
{
  public PDouble()
  {
  }

  public PDouble(double value)
  {
    super(value);
  }

  public PDouble bind(Slider slider)
  {
    bind(slider.valueProperty());
    return this;
  }

  public void bindBidi(DoubleProperty friend)
  {
    Bindings.bindBidirectional(this, friend);
  }

  public PDouble copy()
  {
    return new PDouble(get());
  }

  public static PDouble New()
  {
    return new PDouble();
  }

  public static PDouble New(double value)
  {
    return new PDouble(value);
  }

  public static PDouble New(String value)
  {
    return new PDouble(Nb.toDouble(value));
  }
}
