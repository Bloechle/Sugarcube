package sugarcube.common.graphics.geom;

import java.awt.*;

public interface Shape3 extends Shape
{  
  public Shape3 copy();

  public void setExtent(Line3 extent);
  
  public default String name()
  {
    return null;
  }
}
