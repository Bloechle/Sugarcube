package sugarcube.insight.core.popup;

import javafx.scene.Node;
import javafx.scene.control.Button;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.menus.FxPopup;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxRibbon;
import sugarcube.formats.pdf.resources.icons.Icon;

public class SavePopup extends FxPopup
{
    private FxEnvironment env;
    private FxRibbon tab;
    private boolean alto = false;

    public SavePopup(FxRibbon tab, Button button)
    {
        this.env = tab.env();
        this.tab = tab;
        Icon.SAVE.set(button, tab.iconSize, 100, "Save File (Ctrl+S)", Color3.BLUE_BRIGHT, e -> env.saveOCD(tab));
        FxHandle.Get(button).popup(ctx -> show(button));
    }

    public SavePopup alto()
    {
        this.alto = true;
        return this;
    }

    @Override
    public void show(Node node)
    {
        clear();
        File3 file = env.ocd.file();
        item(" Save ").act(e -> env.saveOCD(tab));
        item(" Save as... ").act(e -> env.saveAsOCD(tab));
        // this.item(" Export eOCD ").setOnAction(e -> new SaveOCDFx(env,
        // file.prefix("e-"), null));
        super.show(node);
    }

    public static SavePopup Attach(FxRibbon tab, Button button)
    {
        return new SavePopup(tab, button);
    }
}
