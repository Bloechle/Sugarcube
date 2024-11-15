package sugarcube.resources.pdf.encoding.cmap;

import java.io.InputStream;

public class RS_CMAP
{
 
  public static InputStream stream(String filename)
  {
    try
    {
      return RS_CMAP.class.getResourceAsStream(filename);
    } catch (Exception e)
    {
      return null;
    }
  } 
}
