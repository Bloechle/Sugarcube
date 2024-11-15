package sugarcube.common.system.io;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.StringSet;

import java.io.File;

public class FileFilter3 extends javax.swing.filechooser.FileFilter implements java.io.FileFilter
{
  private boolean acceptDirectory = false;
  private StringSet extensions = new StringSet();

  public FileFilter3(String... extensions)
  {
    this(true, extensions);
  }

  public FileFilter3(boolean acceptDirectory, String... extensions)
  {
    this.acceptDirectory = acceptDirectory;
    if (extensions != null)
      for (String extension : extensions)
        this.extensions.add(extension.startsWith(".") ? extension : "." + extension);
  }

  @Override
  public boolean accept(File file)
  {
    if (acceptDirectory && file.isDirectory())
      return true;
    if (extensions.isEmpty())
      return true;
    else
      for (String ext : extensions)
        if (file.getName().toLowerCase().endsWith(ext.toLowerCase()))
          return true;
    return false;
  }

  @Override
  public String getDescription()
  {
    return Zen.Array.String(extensions.toArray(new String[0]));
  }
}