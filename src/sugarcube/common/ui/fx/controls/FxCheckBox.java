package sugarcube.common.ui.fx.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import sugarcube.common.interfaces.Resetable;
import sugarcube.common.ui.fx.base.Fx;

public class FxCheckBox extends CheckBox implements Resetable
{
    private boolean defaultValue = false;
    public String name;

    public FxCheckBox()
    {
    }

    public FxCheckBox(boolean selected)
    {
        setSelected(defaultValue = selected);
    }

    public FxCheckBox(String text)
    {
        super(text);
    }

    public FxCheckBox(String text, boolean selected)
    {
        super(text);
        setSelected(defaultValue = selected);
    }

    public void reset()
    {
        setSelected(defaultValue);
    }

    public FxCheckBox name(String name)
    {
        this.name = name;
        return this;
    }

    public FxCheckBox on()
    {
        setSelected(true);
        return this;
    }

    public FxCheckBox off()
    {
        this.setSelected(false);
        return this;
    }

    public FxCheckBox action(EventHandler<ActionEvent> handler)
    {
        this.setOnAction(handler);
        return this;
    }


    public FxCheckBox width(double w)
    {
        Fx.width(w, this);
        return this;
    }

    public FxCheckBox height(double h)
    {
        Fx.height(h, this);
        return this;
    }

    public FxCheckBox size(double w, double h)
    {
        return width(w).height(h);
    }

}
