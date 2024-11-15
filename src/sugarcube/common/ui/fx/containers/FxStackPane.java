package sugarcube.common.ui.fx.containers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;
import sugarcube.common.ui.fx.event.FxEventHandler;
import sugarcube.common.ui.fx.event.FxHandle;

import java.awt.*;

public class FxStackPane extends StackPane
{

  public FxStackPane()
  {

  }

  public Parent loadFxml()
  {
    return loadFxml(this, true);
  }

  public Parent loadFxml(boolean withCSS)
  {
    return loadFxml(this, withCSS);
  }

  public Parent loadFxml(Object controller, boolean withCSS)
  {
    Parent fxml = Fx.Fxml(controller, withCSS);
    add(fxml);
    return fxml;
  }
  
  public FxStackPane id(String id)
  {
    this.setId(id);
    return this;
  }
  
  public FxStackPane style(String style)
  {
    FxCSS.Style(this, style);
    return this;
  }  

  public void add(Node... child)
  {
    if (child != null)
      for (Node add : child)
        if (add != null)
        {
          for (Node node : getChildren())
            if (node == add)
            {
              add = null;
              break;
            }

          if (add != null)
            this.getChildren().add(add);
        }
  }

  public FxStackPane(FxEventHandler handler)
  {
    this.setHandler(handler);
  }

  public void clear()
  {
    this.getChildren().clear();
  }

  public void addChildren(Node... child)
  {
    this.getChildren().addAll(child);
  }

  public FxStackPane addStyleSheet(ObservableList<String> css)
  {
    this.getStylesheets().addAll(css);
    return this;
  }

  public FxStackPane addStyleSheet(String styleSheet)
  {
    this.getStylesheets().add(styleSheet);
    return this;
  }

  public FxStackPane addStyleClass(String styleClass)
  {
    if (styleClass.startsWith("."))
    {
      Log.debug(this, ".addStyleClass - prefix class dot should be removed: " + styleClass);
      styleClass = styleClass.substring(1);
    }

    this.getStyleClass().add(styleClass);
    return this;
  }

  public void setHandler(FxEventHandler handler)
  {
    new FxHandle(this).events(handler);
  }

  public Dimension dimension()
  {
    return new Dimension((int) this.getWidth(), (int) this.getHeight());
  }

  public void setScale(double scale)
  {
    this.setScale(scale, scale);
  }

  public void setScale(double sx, double sy)
  {
    this.setScaleX(sx);
    this.setScaleY(sy);
  }

  public void setSize(double w, double h)
  {
    this.setPrefWidth(w);
    this.setPrefHeight(h);
    this.setMinWidth(w);
    this.setMinHeight(h);
    this.setWidth(w);
    this.setHeight(h);
    this.setMaxWidth(w);
    this.setMaxHeight(h);
  }

  public void setSize(Dimension dim)
  {
    this.setSize(dim.getWidth(), dim.getHeight());
  }

  public Graphics3 graphics()
  {
    // return new Graphics3(this);
    return null;
  }

  public void repaint()
  {
    this.paint(graphics());
  }

  public void paint(Graphics3 g)
  {

  }
}
