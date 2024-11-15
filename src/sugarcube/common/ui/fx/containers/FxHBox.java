package sugarcube.common.ui.fx.containers;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxHBox extends HBox
{

  public FxHBox()
  {

  }

  public FxHBox(double spacing)
  {
    super(spacing);
  }
  
  public FxHBox(Node... nodes)
  {
    super(nodes);
  }
  
  public FxHBox style(String style)
  {
    FxCSS.Style(this,  style);
    return this;
  }  

  public FxHBox add(Node... nodes)
  {
    getChildren().addAll(Fx.Trim(nodes));
    return this;
  }
  
  public FxHBox set(Node... nodes)
  {
    getChildren().setAll(Fx.Trim(nodes));
    return this;
  }
  
  
  public FxHBox align(Pos align)
  {
    setAlignment(align);
    return this;
  }
  
  public FxHBox width(double w)
  {
    setWidth(w);
    return this;
  }
  
  public FxHBox height(double h)
  {
    setHeight(h);
    return this;
  }
  
  public FxHBox size(double w, double h)
  {
    return width(w).height(h);
  }
  
  public static FxHBox Get(double spacing, Node...nodes)
  {
    return new FxHBox(spacing).add(nodes);
  }
  
  public static FxHBox Get(Node...nodes)
  {
    return new FxHBox(0).add(nodes);
  }

}
