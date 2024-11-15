package sugarcube.common.ui.fx.base;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.WritableImage;
import sugarcube.common.ui.fx.FxInterface;
import sugarcube.common.ui.fx.event.FxEventHandler;
import sugarcube.common.ui.fx.event.FxHandle;

public class FxScene extends Scene
{
  public FxScene(Parent root)
  {
    super(root == null ? new FxGroup() : root);
  }

  public FxScene(Parent root, double width, double height)
  {
    super(root == null ? new FxGroup() : root, width, height);
  }

  public FxScene(Parent root, double width, double height, boolean scene3D)
  {
    super(root == null ? new FxGroup() : root, width, height, scene3D, SceneAntialiasing.BALANCED);
    this.setFill(null);
  }

  public FxGroup rootGroup()
  {
    return getRoot() != null && getRoot() instanceof FxGroup ? (FxGroup) getRoot() : null;
  }

  public WritableImage snapshot()
  {
    return super.snapshot(null);
  }

  public void addResizeListener(FxInterface.Resizable listener)
  {
    widthProperty().addListener(e -> listener.resized(widthProperty().intValue(), heightProperty().intValue()));
    heightProperty().addListener(e -> listener.resized(widthProperty().intValue(), heightProperty().intValue()));
  }
  
  public FxHandle handle()
  {
    return FxHandle.Get(this);
  }
  
  public FxHandle handleEvents(FxEventHandler handler)
  {
    return FxHandle.Get(this).events(handler);
  }
}
