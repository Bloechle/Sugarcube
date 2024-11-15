package sugarcube.common.system.io;

import sugarcube.common.system.log.Log;

import java.awt.*;
import java.io.File;

public class Desk
{
  public static boolean Open(String path)
  {
    return Open(File3.Get(path));
  }

  public static boolean Open(File file)
  {
    try
    {
      if (file != null && file.exists())
      {
        Desktop desk = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desk != null)
        {
          desk.open(file);
          return true;
        }
      } else
        Log.debug(Desk.class, ".Open - file not found: " + file);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }
}
