package sugarcube.common.ui.fx.beans;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import sugarcube.common.data.xml.Nb;

public class PInteger extends SimpleIntegerProperty
{
  public PInteger()
  {    
  }
  
  public PInteger(int value)
  {
    super(value);
  }
  
  public void bindBidi(IntegerProperty friend)
  {
    Bindings.bindBidirectional(this,  friend);
  }
  
  public PInteger copy()
  {
    return new PInteger(get());
  }
  
  public static PInteger New()
  {
    return new PInteger();
  } 
  
  public static PInteger New(int value)
  {
    return new PInteger(value);
  }  
  
  public static PInteger New(String value)
  {
    return New(Nb.Int(value));
  }
}
