package sugarcube.formats.ocd.resources;

import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.io.File3;

public class RS_OCD implements Unjammable
{
  public static String WELCOME_OCD = "Welcome.ocd";

  public static File3 WelcomeFile()
  {
    return WelcomeFile(WELCOME_OCD, RS_OCD.class);
  }
  
  public static File3 WelcomeFile(Class cls)
  {
    return WelcomeFile(WELCOME_OCD, cls);
  }
  
  public static File3 WelcomeFile(String filename, Class cls)
  {
    return File3.TempFile(filename, Class3.Stream(cls, filename), true);
  }
}
