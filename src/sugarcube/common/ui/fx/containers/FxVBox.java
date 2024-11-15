package sugarcube.common.ui.fx.containers;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class FxVBox extends VBox
{

  public FxVBox()
  {
  }

  public FxVBox(double spacing)
  {
    super(spacing);
  }
  
  public FxVBox(Node... nodes)
  {
    super(nodes);
  }

  public FxVBox add(Node... nodes)
  {
    this.getChildren().addAll(nodes);
    return this;
  }

  public FxVBox align(Pos align)
  {
    setAlignment(align);
    return this;
  }
}
