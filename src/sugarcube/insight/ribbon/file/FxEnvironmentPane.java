package sugarcube.insight.ribbon.file;

import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Pane;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.menus.FxIcon;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxFinalEnvironment;

public class FxEnvironmentPane extends FxFinalEnvironment
{  
  public String label = "";
  public String title = "";
  public boolean scroll = false;  
  public Pane root;
  public FxIcon icon;
  public Object controller;

  public FxEnvironmentPane(FxEnvironment env)
  {
    super(env); 
    this.controller = this;
  }
  
  public FxEnvironmentPane(FxEnvironment env, String label, String title, FxIcon icon)
  {
    this(env);
    this.label = label;
    this.title = title;    
    this.root = (Pane) Fx.Fxml(controller);
    this.icon = icon;
  }
  
  @Override
  public Node root()
  {
    return root;
  }   
  
  @Override
  public String toString()
  {
    return label;
  }
  
  public void onDragDropped(DragEvent e)
  {

  }

}
