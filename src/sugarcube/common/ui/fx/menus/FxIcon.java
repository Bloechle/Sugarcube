package sugarcube.common.ui.fx.menus;

import javafx.scene.Node;
import sugarcube.common.graphics.Color3;

public interface FxIcon
{
  Node node();

  static FxIcon node(Node node)
  {
    return () -> node;
  }

  static FxImageIcon Image(Class path, String filename)
  {
    return new FxImageIcon(path, filename);
  }

  static FxColorIcon color(Color3 color, int size)
  {
    return new FxColorIcon(color, size);
  }
}
