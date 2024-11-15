package sugarcube.formats.pdf.resources.pdf;


import sugarcube.formats.pdf.resources.RS;

import java.io.InputStream;

public class RS_PDF
{
  public static byte[] bytes(String filename)
  {
    return RS.ReadBytes(stream(filename));
  }  
  
  public static InputStream stream(String filename)
  {
    try
    {
      return RS_PDF.class.getResourceAsStream(filename);
    } catch (Exception e)
    {
      return null;
    }
  }    
}
