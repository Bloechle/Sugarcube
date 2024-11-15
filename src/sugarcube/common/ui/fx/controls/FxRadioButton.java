package sugarcube.common.ui.fx.controls;

import javafx.scene.control.RadioButton;
import sugarcube.common.interfaces.Resetable;

public class FxRadioButton extends RadioButton implements Resetable
{
    private boolean defaultValue = false;

    public FxRadioButton(String label, boolean selected)
    {
        super(label);
        defaultValue = selected;
    }

    public void reset()
    {
        setSelected(defaultValue);
    }
}
