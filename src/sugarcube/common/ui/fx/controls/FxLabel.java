package sugarcube.common.ui.fx.controls;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;
import sugarcube.common.ui.fx.menus.FxIcon;

public class FxLabel extends Label implements FxIcon
{

  public FxLabel()
  {

  }   

  public FxLabel(String text)
  {
    super(text);
  }
  
  public FxLabel(String text, Node node)
  {
    super(text, node);
  }

  public FxLabel(String text, Font font)
  {
    super(text);
    if (font != null)
      this.setFont(font);
  }

  public FxLabel(String text, String style)
  {
    super(text);
    this.setStyle(style);
  }

  public FxLabel alignRight()
  {
    setAlignment(Pos.CENTER_RIGHT);
    return this;
  }
  
  public FxLabel textAlign(TextAlignment align)
  {
    setTextAlignment(align);
    return this;
  }
  
  public FxLabel textAlignRight()
  {
    return textAlign(TextAlignment.RIGHT);
  }
  
  public FxLabel textAlignLeft()
  {
    return textAlign(TextAlignment.LEFT);
  }
  
  public FxLabel alignCenter()
  {
    setAlignment(Pos.CENTER);
    return textAlign(TextAlignment.CENTER);
  }
  
  public FxLabel fill(Color3 c)
  {
    this.setTextFill(c.fx());
    return this;
  }
  
  public FxLabel style(String style)
  {
    FxCSS.Style(this,  style);
    return this;
  }
  
  public FxLabel width(double w)
  {
    Fx.width(w, this);
    return this;
  }
  
  public FxLabel height(double h)
  {
    Fx.height(h, this);
    return this;
  }
  
  public FxLabel size(double w, double h)
  {
    return width(w).height(h);
  }
  
  public FxLabel graphic(Node node)
  {
    this.setGraphic(node);
    return this;
  }

  @Override
  public Node node()
  {
    return this;
  }
  
  public static FxLabel get(String text)
  {
    return new FxLabel(text);
  }
  
  public static FxLabel Space(int size)
  {    
    String space = "";
    while(size-->0)    
      space+="\u00A0";     
    return new FxLabel(space);
  }

}
