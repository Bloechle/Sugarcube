package sugarcube.common.ui.fx.menus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import sugarcube.common.system.log.Log;
import sugarcube.common.ui.fx.containers.FxHBox;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.common.ui.fx.event.FxAction;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.event.FxMouseHandler;

public class FxMenuItem extends CustomMenuItem
{
    private FxLabel label = new FxLabel();
    private FxLabel accel = new FxLabel();
    private FxHBox box;

    public FxMenuItem()
    {
        this.box = new FxHBox(label, accel).align(Pos.CENTER_LEFT);
        box.setStyle("-fx-padding: 8px; -fx-border-width: 0px; -fx-border-color: black;");

        box.setMaxWidth(Double.MAX_VALUE);
        box.setPrefWidth(200);

        this.setContent(box);
        label.setStyle("-fx-text-fill: white; -fx-font-weight:normal;");
        accel.setStyle("-fx-font-style: italic; -fx-font-size:10px; -fx-background-color: rgba(0,0,0,0.1); -fx-background-radius: 8px;");

        box.parentProperty().addListener((obs, old, val) ->
        {
            if (val != null)
                val.setStyle("-fx-padding: 0px; -fx-margin: 0px;");
        });

    }

    public FxMenuItem(String text)
    {
        this();
        int a = text.indexOf(" @");
        if (a > 0)
        {
            label.setText(" " + text.substring(0, a) + "   ");
            accel.setText("  " + text.substring(a + 2) + "  ");
        } else
            label.setText(" " + text + " ");

    }

    public FxMenuItem(String text, Node node)
    {
        this(text);
        label.setGraphic(node);
    }

    public FxMenuItem(String text, Node node, KeyCombination accelerator)
    {
        this(text, node);
        if (accelerator != null)
            setAccelerator(accelerator);
    }

    public FxMenuItem(FxAction action)
    {
        this(action.text(), action.node());
        this.act(action);
        this.mouse(ms -> action.mouse(ms));
    }

    public FxMenuItem ctrl(String key)
    {
        if (key != null)
        {
            KeyCode keyCode = KeyCode.getKeyCode(key.toUpperCase());
            if (keyCode != null)
                setAccelerator(new KeyCodeCombination(keyCode, KeyCombination.CONTROL_DOWN));
            else
                Log.debug(this, ".ctrl - keyCode not found for code: " + key);
        }
        return this;
    }

    public FxLabel label()
    {
        return this.label;
    }

    public FxMenuItem act(EventHandler<ActionEvent> handler)
    {
        this.setOnAction(handler);
        return this;
    }

    public FxMenuItem mouse(FxMouseHandler handler)
    {
        FxHandle.Get(label).mouseOver(handler);
        FxHandle.Get(label).mouseOut(handler);
        return this;
    }

//  public ContextMenuContent.MenuItemContainer wrapper()
//  {
//    ContextMenuContent content = (ContextMenuContent) ((ContextMenuSkin) this.getParentPopup().getSkin()).getNode();
//    // Items container contains MenuItemContainer nodes and Separator nodes.
//    for (Node child : content.getItemsContainer().getChildrenUnmodifiable())
//      if (child instanceof ContextMenuContent.MenuItemContainer)
//      {
//        ContextMenuContent.MenuItemContainer wrapper = (ContextMenuContent.MenuItemContainer) child;
//        if (wrapper.getItem() == this)
//          return wrapper;
//      }
//
//    return null;
//  }

}
