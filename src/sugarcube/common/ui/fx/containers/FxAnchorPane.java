package sugarcube.common.ui.fx.containers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.interfaces.Widthable;
import sugarcube.common.ui.fx.base.FxCSS;
import sugarcube.common.ui.fx.event.FxEventHandler;
import sugarcube.common.ui.fx.event.FxHandle;

import java.awt.*;

public class FxAnchorPane extends AnchorPane implements Widthable
{
  public FxAnchorPane()
  {
  }

  public FxAnchorPane(FxEventHandler handler)
  {
    this.setHandler(handler);
  }

  public FxAnchorPane style(String style)
  {
    FxCSS.Style(this, style);
    return this;
  }

  public void setMouseOverEffect(Effect effect)
  {
    this.setOnMouseEntered(e -> setEffect(effect));
    this.setOnMouseExited(e -> setEffect(null));
  }

  public FxAnchorPane setBackgroundColor(Color color)
  {
    this.setBackground(new Background(new BackgroundFill(color, null, null)));
    return this;
  }

  public void clear()
  {
    this.getChildren().clear();
  }

  public void set(Node... nodes)
  {
    this.getChildren().setAll(nodes);
  }

  public void add(Node... nodes)
  {
    for (Node node : nodes)
    {
      boolean add = true;
      for (Node child : getChildren())
      {
        if (child == node)
        {
          add = false;
          break;
        }
      }
      if (add)
        this.getChildren().addAll(nodes);
    }
  }

  public void remove(Node... nodes)
  {
    this.getChildren().removeAll(nodes);
  }

  public FxAnchorPane addStyleSheet(ObservableList<String> css)
  {
    this.getStylesheets().addAll(css);
    return this;
  }

  public FxAnchorPane addStyleSheet(String styleSheet)
  {
    this.getStylesheets().add(styleSheet);
    return this;
  }

  public FxAnchorPane addStyleClass(String styleClass)
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

  public void setSize(Dimension dim)
  {
    this.setSize(dim.getWidth(), dim.getHeight());
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

  public void width(double w)
  {
    this.setPrefWidth(w);
    this.setMinWidth(w);
    this.setWidth(w);
    this.setMaxWidth(w);
  }

  public void height(double h)
  {
    this.setPrefHeight(h);
    this.setMinHeight(h);
    this.setHeight(h);
    this.setMaxHeight(h);
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

  public static void setTopLeft(Node node, double top, double left)
  {
    AnchorPane.setTopAnchor(node, top);
    AnchorPane.setLeftAnchor(node, left);
  }
}
