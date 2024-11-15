package sugarcube.common.ui.fx.beans;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import sugarcube.common.data.xml.Nb;

public class PFloat extends SimpleFloatProperty
{
  public PFloat()
  {    
  }
  
  public PFloat(float value)
  {
    super(value);
  }
  
  public void bindBidi(FloatProperty friend)
  {
    Bindings.bindBidirectional(this,  friend);
  }
  
  public PFloat copy()
  {
    return new PFloat(get());
  }
  
  public static PFloat New()
  {
    return new PFloat();
  } 
  
  public static PFloat New(float value)
  {
    return new PFloat(value);
  }  
  
  public static PFloat New(String value)
  {
    return new PFloat(Nb.Float(value));
  }
}
