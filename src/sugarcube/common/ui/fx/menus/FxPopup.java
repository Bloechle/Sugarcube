package sugarcube.common.ui.fx.menus;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCombination;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.interfaces.Actable;
import sugarcube.common.ui.fx.event.FxAction;

public class FxPopup extends ContextMenu
{
  private static Set3<Node> NODES = new Set3<>();
  private Node node;

  public FxPopup()
  {
//    this.setStyle("-fx-background-color: #333;");      
    this.setStyle("-fx-base: #000;"); 
    this.setOnHidden(e->NODES.remove(this.node));      
  }

  public void clear()
  {
    if (this.isShowing())
      this.hide();
    this.getItems().clear();    
  }

  public boolean isPopulated()
  {
    return !this.getItems().isEmpty();
  }

  public ObservableList<MenuItem> items()
  {
    return this.getItems();
  }

  public FxMenuItem item(String text)
  {
    FxMenuItem item = new FxMenuItem(text);
    this.items().add(item);
    return item;
  }
  
  public FxMenuItem item(String text, Actable act)
  {    
    return item(new FxAction(text).setAction(act));
  }

  public FxMenuItem item(String text, Node node)
  {
    FxMenuItem item = new FxMenuItem(text, node);
    this.items().add(item);
    return item;
  }

  public FxMenuItem item(String text, Node node, KeyCombination accelerator)
  {
    FxMenuItem item = new FxMenuItem(text, node, accelerator);
    this.items().add(item);
    return item;
  }

  public FxMenuItem item(FxAction action)
  {
    FxMenuItem item = new FxMenuItem(action);
    this.items().add(item);
    return item;
  }

  public MenuItem item(MenuItem item)
  {
    if (item == null)
      this.separator();
    else
      this.items().add(item);
    return item;
  }
  
  public FxPopup nop()
  {
    return this;
  }

  public FxPopup sep()
  {
    return separator();
  }

  public FxPopup separator()
  {
    SeparatorMenuItem item = new SeparatorMenuItem();
    this.items().add(item);
    return this;
  }

  public FxPopup items(Object... objects)
  {
    for (Object o : objects)
    {
      if (o == null)
        this.separator();
      else if (o instanceof FxAction)
        this.item(((FxAction) o).menuItem());
      else if (o instanceof MenuItem)
        this.item((MenuItem) o);
      else
        this.separator();
    }
    return this;
  }

  public FxMenu sepMenu(String name, Node icon)
  {
    return this.sep().menu(name, icon);
  }

  public FxMenu menu(String name, Node icon)
  {
    FxMenu menu = new FxMenu(name, icon);
    this.items(menu);
    return menu;
  }

  public boolean isVisible()
  {
    return isShowing();
  }

  public void show(Node node)
  {
    this.node = node;
    Bounds box = node.getBoundsInLocal();
    Point2D p = node.localToScreen(0, box.getMaxY());
    this.show(node, p.getX(), p.getY());
  }
  
  public void show(Node node, ContextMenuEvent e)
  {
    this.show(node, e.getScreenX(), e.getScreenY());
  }

  public void show(Node node, Point3 screenXY)
  {
    this.show(node, screenXY.x, screenXY.y);
  }

  @Override
  public void show(Node node, double x, double y)
  {
    this.node = node;
    if (NODES.notYet(node))
      super.show(node, x, y);
  }
  
  public void close()
  {
    this.hide();
  }

  public void onClose(Actable actable)
  {
    actable.act();
  }

  public static FxPopup get()
  {
    return new FxPopup();
  }

}
