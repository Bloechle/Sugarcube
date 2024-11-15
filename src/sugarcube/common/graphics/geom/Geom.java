

package sugarcube.common.graphics.geom;


public class Geom
{
  public static double precision = 0.000001;
  
  public static double zeroPlus(double v)
  {
    return v<=0 ? precision : v;
  }
  
}
