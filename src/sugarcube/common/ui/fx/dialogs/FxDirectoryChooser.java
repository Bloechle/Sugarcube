package sugarcube.common.ui.fx.dialogs;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import sugarcube.common.data.collections.Str;
import sugarcube.common.system.io.File3;

import java.io.File;

public class FxDirectoryChooser
{
  private DirectoryChooser chooser;

  public FxDirectoryChooser()
  {
    chooser = new DirectoryChooser();
  }

  public FxDirectoryChooser(String title)
  {
    this();
    chooser.setTitle(title);
  }

  public FxDirectoryChooser(String title, String directory)
  {
    this(title);
    this.setDir(directory);
  }

  public String setWorkDir()
  {
    this.setDir(File3.USER_WORK);
    return File3.USER_WORK;
  }

  public void setDir(String path)
  {
    if (Str.HasChar(path))
      this.setDir(new File3(path).directory());
  }

  public void setDir(File file)
  {
    this.chooser.setInitialDirectory(file);
  }

  public File3 show(Window owner)
  {
    File file = chooser.showDialog(owner);
    return file == null ? null : new File3(file);
  }

  public static FxDirectoryChooser Get(String title)
  {
    return new FxDirectoryChooser(title);
  }

  public static FxDirectoryChooser Get(String title, String directory)
  {
    return new FxDirectoryChooser(title, directory);
  }

}
