package sugarcube.formats.epub;

import sugarcube.common.system.log.Log;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.reflection.Reflect;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class EPubChecker implements Unjammable
{
  public static final String CLASS = "com.adobe.epubcheck.api.EpubCheck";

  public static String Check(File file)
  {
    String path = file.getAbsolutePath();
    StringWriter buffer = new StringWriter();
    boolean valid = false;
    try
    {      
      PrintWriter writer = new PrintWriter(buffer, true);
      // we ensure a non extended File object for the reflection
      Object epubcheck = Reflect.Instance(CLASS, new File(path), writer);
      if (epubcheck != null)
      {
        Object o = Reflect.Method(epubcheck, "validate");
        if (o == null)
          Log.debug(EPubChecker.class, ".check - validate method not found");
        else
          valid = (o instanceof Boolean && (Boolean) o);
      } else
        Log.debug(EPubChecker.class, ".check - not found: " + CLASS);

      writer.flush();
      writer.close();
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    String log = buffer.getBuffer().toString();
    Log.debug(EPubChecker.class, ".check - " + path + " ,valid="+valid+", log=" + log);
    return valid ? "" : log;
  }

  public static void main(String... args)
  {
    EPubChecker.Check(File3.desktop("test.epub"));
  }
}
