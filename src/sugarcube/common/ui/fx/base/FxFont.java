package sugarcube.common.ui.fx.base;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.text.Font;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.Class3;

import java.net.URL;

public class FxFont
{
  private Font font;

  public FxFont()
  {

  }

  public static Font load(String family, double size)
  {
    return new Font(family, size);
  }

  public static Font load(Object path, String filename, double size)
  {
    URL url = Class3.Url(path, filename);
    try
    {
      return Font.loadFont(url.toExternalForm(), size);
    } catch (Exception e)
    {
      e.printStackTrace();
      Log.debug(FxFont.class, ".load - url: " + url + ", filename=" + filename + ", path=" + path);
    }
    return null;
  }

  public static FontLoader loader()
  {
    return Toolkit.getToolkit().getFontLoader();
  }

//  public double width(String text)
//  {
//    return loader().computeStringWidth(text, font);
//  }
//
//  public double height(String text)
//  {
//    return loader().getFontMetrics(font).getLineHeight();
//  }
}
