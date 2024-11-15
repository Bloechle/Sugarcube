package sugarcube.common.ui.fx.controls;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import sugarcube.common.interfaces.SourceActable;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxTextField extends TextField
{
  
  public FxTextField()
  {
  }

  public FxTextField(String text)
  {
    super(text);
  }
  
  public FxTextField handle(SourceActable<FxTextField> actable)
  {
    this.textProperty().addListener((old,obs,val)->actable.act(this));
    return this;
  }
  
  public FxTextField style(String style)
  {
    FxCSS.Style(this,  style);
    return this;
  }
  
  public FxTextField width(double w)
  {
    Fx.width(w, this);
    return this;
  }
  
  public FxTextField height(double h)
  {
    Fx.height(h, this);
    return this;
  }
  
  public FxTextField size(double w, double h)
  {
    return width(w).height(h);
  }
  
  public FxTextField centerText()
  {
    this.setAlignment(Pos.CENTER);
    return this;
  }
  
  public FxTextField fontsize(double fs)
  {
    this.setFont(Font.font(this.getFont().getName(), fs));
    return this;
  }
  
  
}
