package sugarcube.common.ui.fx.containers;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

public class FxFlowPane extends FlowPane
{
  public FxFlowPane()
  {
    super();
  }
  
  public FxFlowPane add(Node... nodes)
  {
    this.getChildren().addAll(nodes);
    return this;
  }
  
  public FxFlowPane set(Node... nodes)
  {
    this.getChildren().setAll(nodes);
    return this;
  }
  
  public FxFlowPane gap(double xy)
  {
    return gap(xy, xy);
  }
  
  public FxFlowPane gap(double x, double y)
  {
    this.setHgap(x);
    this.setVgap(y);  
    return this;
  }
  
}
