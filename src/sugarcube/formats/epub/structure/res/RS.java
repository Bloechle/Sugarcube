package sugarcube.formats.epub.structure.res;

import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.Class3;

import java.io.InputStream;

public class RS implements Unjammable
{
  public static Class3 C3 = new Class3(RS.class);

  public static final String REPLICA_CSS = "replica.css";
  public static final String REPLICA_JS = "replica.js"; 
  
  public static final String LIQUID_CSS = "liquid.css"; 
  public static final String LIQUID_JS = "liquid.js";  

  public static final String EBOOK_CSS = "ebook.css"; 
  public static final String FONTS_CSS = "fonts.css";

  
  public static InputStream stream(String filename)
  {
    return C3.stream(filename);
  }
  
  public static byte[] Bytes(String filename)
  {
    return C3.bytes(filename);
  }
  
  public static String String(String filename)
  {
    return C3.string(filename);
  }
}
