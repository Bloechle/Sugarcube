package sugarcube.common.ui.fx.base;

import javafx.beans.value.ObservableNumberValue;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;

public class FxSubScene extends SubScene
{

  public FxSubScene(Parent root, double width, double height)
  {
    super(root, width, height);
  }
  
  public FxSubScene(Parent root, double width, double height, boolean depthBuffer)
  {
    super(root, width, height, depthBuffer, SceneAntialiasing.BALANCED);
  }

  public FxSubScene(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing)
  {
    super(root, width, height, depthBuffer, antiAliasing);
  }

  public FxGroup rootGroup()
  {
    return getRoot() != null && getRoot() instanceof FxGroup ? (FxGroup) getRoot() : null;
  }
  
  public FxSubScene bindSize(Pane parent, ObservableNumberValue deltaWidth, ObservableNumberValue deltaHeight)
  {        
    this.widthProperty().bind(parent.widthProperty().subtract(deltaWidth));
    this.heightProperty().bind(parent.heightProperty().subtract(deltaHeight));
    return this;
  }
  
}
