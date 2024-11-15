package sugarcube.common.ui.fx.event;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import sugarcube.common.system.log.Log;
import sugarcube.common.interfaces.Actable;
import sugarcube.common.ui.fx.menus.FxIcon;
import sugarcube.common.ui.fx.menus.FxImageIcon;
import sugarcube.common.ui.fx.menus.FxMenuItem;

public class FxAction implements EventHandler<ActionEvent>, Actable
{
    protected String cmd;
    protected String text;
    protected String desc;
    protected FxIcon icon;
    protected KeyCombination acc;
    protected Actable actable;

    public FxAction(String text)
    {
        this.text = text;
        this.actable = this;
    }

    public FxAction(String text, FxIcon icon)
    {
        this(text);
        this.icon = icon;
    }

    public FxAction setAction(Actable actable)
    {
        this.actable = actable;
        return this;
    }

    public FxAction icon(FxIcon icon)
    {
        this.icon = icon;
        return this;
    }

    public FxAction icon(Class classPath, String iconName)
    {
        return icon(iconName == null || iconName.isEmpty() ? null : new FxImageIcon(classPath, iconName));
    }

    public FxAction tooltip(String desc)
    {
        this.desc = desc;
        return this;
    }

    public FxAction accelerator(KeyCombination acc)
    {
        this.acc = acc;
        return this;
    }

    public FxAction ctrlAccelerator(KeyCode code)
    {
        return accelerator(new KeyCodeCombination(code, KeyCombination.CONTROL_DOWN));
    }

    public Actable actable()
    {
        return actable;
    }

    public String cmd()
    {
        return cmd;
    }

    public String text()
    {
        return text;
    }

    public FxIcon icon()
    {
        return icon;
    }

    public Node node()
    {
        return icon == null ? null : icon.node();
    }

    @Override
    public void handle(ActionEvent event)
    {
        try
        {
            if (actable != null)
                actable.act();
        } catch (Exception e)
        {
            Log.debug(this, ".handle - exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void act()
    {
        if (actable != null)
            actable.act();
    }

    public void mouse(FxMouse mouse)
    {

    }

    public FxMenuItem menuItem()
    {
        FxMenuItem item = new FxMenuItem(this);
        if (actable == null)
        {
            item.setHideOnClick(false);
            item.label().setOpacity(0.5);
        }
        if (acc != null)
            item.setAccelerator(acc);
        return item;
    }
}
