
package sugarcube.common.ui.gui.icons;

import sugarcube.common.interfaces.Unjammable;


public class Icons3 implements Unjammable
{  
  public static final String OK_24 = "ok-24.png";
  public static final String REFRESH_24= "refresh-24.png";
  public static final String SUGARCUBE_24="sugarcube32.png";

  
  public static ImageIcon3 get(String filename)
  {
    return get(Icons3.class, filename);
  }
  
  public static ImageIcon3 get(Class path, String filename)
  
 {
    return new ImageIcon3(path, filename);
 }
  
}
