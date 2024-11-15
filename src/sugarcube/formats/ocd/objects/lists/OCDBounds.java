package sugarcube.formats.ocd.objects.lists;

import sugarcube.common.data.collections.Map3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.formats.ocd.objects.OCDPaintable;

import java.util.Map;

public class OCDBounds extends Map3<OCDPaintable, Rectangle3>
{

  
  public OCDPaintable nodeAt(double x, double y)
  {
    OCDPaintable node = null;
    Rectangle3 bounds = null;
    for(Map.Entry<OCDPaintable, Rectangle3> entry: this.entrySet())
    {      
      Rectangle3 value=entry.getValue();
      if(value.contains(x,y) && (bounds==null || value.area()<bounds.area()))        
      {
        bounds = value;
        node = entry.getKey();
      }
    }    
    return node;
  }
}
