package sugarcube.common.ui.fx.containers;

import javafx.scene.control.SplitPane;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxSplitPane extends SplitPane
{
  public FxSplitPane()
  {
    super();    
    this.setStyle("-fx-box-border: transparent;");    
  }
  
  public FxSplitPane id(String id)
  {
    this.setId(id);
    return this;
  }
  
  public FxSplitPane style(String style)
  {
    FxCSS.Style(this, style);
    return this;
  }

}
