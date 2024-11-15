package sugarcube.common.ui.fx.menus;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.Class3;
import sugarcube.common.ui.gui.icons.ImageIcon3;

import java.io.InputStream;

public class FxImageIcon implements FxIcon
{
  private Image image;
  
  public FxImageIcon()
  {

  }

  public FxImageIcon(Image image)
  {
    this.image = image;
  }

  public FxImageIcon(Class path, String filename)
  {
    this(loadResourceImage(path, filename));
  }

  public static Image loadResourceImage(Class path, String filename)
  {
    try
    {
      InputStream stream = Class3.Stream(path, filename);
      Image image = new Image(stream);
      stream.close();
      return image;
    } catch (Exception ex)
    {
      Log.warn(ImageIcon3.class, ".loadResourceImage failed at " + path + "/" + filename + ": " + ex);
    }
    return null;
  }
  
  @Override
  public Node node()
  {
    return new ImageView(image);
  }
  
  public static FxImageIcon load(Class path, String filename)
  {
    return new FxImageIcon(path, filename);
  }
}
