package sugarcube.common.ui.fx.beans;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import sugarcube.common.fx.controls.FxTabCheck;
import sugarcube.common.data.xml.Nb;

public class PBool extends SimpleBooleanProperty
{
  public PBool(boolean bool)
  {
    super(bool);
  }
  
  public PBool bind(ToggleButton button)
  {
    this.bind(button.selectedProperty());
    return this;
  }
  
  public PBool bind(CheckBox box)
  {
    this.bind(box.selectedProperty());
    return this;
  }
  
  public PBool bidi(CheckBox box)
  {
    this.bindBidirectional(box.selectedProperty());
    return this;
  }
  
  public void bidi(Property<Boolean >property)
  {
    this.bindBidirectional(property);
  }
  
  public void unBidi(Property<Boolean >property)
  {
    this.unbindBidirectional(property);
  }

  public PBool copy()
  {
    return new PBool(get());
  }

  public boolean isTrue()
  {
    return this.get();
  }

  public boolean isFalse()
  {
    return !this.get();
  }

  public boolean is(boolean bool)
  {
    return this.get() == bool;
  }
    
  public  FxTabCheck tab(Tab tab)
  {
    return FxTabCheck.Inject(tab,  this);  
  }

  public static PBool True()
  {
    return new PBool(true);
  }

  public static PBool False()
  {
    return new PBool(false);
  }

  public static PBool New(boolean bool)
  {
    return new PBool(bool);
  }

  public static PBool New(String bool)
  {
    return New(Nb.Bool(bool));
  }

  public static boolean[] Bools(PBool... bools)
  {
    boolean[] bs = new boolean[bools.length];
    for (int i = 0; i < bools.length; i++)
      bs[i] = bools[i].get();
    return bs;
  }
}
