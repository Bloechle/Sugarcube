package sugarcube.insight.core.popup;

import javafx.scene.Node;
import javafx.scene.control.Button;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.menus.FxPopup;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxRibbon;
import sugarcube.formats.pdf.resources.icons.Icon;

public class OpenPopup extends FxPopup
{
  private FxEnvironment env;
  private boolean insert = false;

  public OpenPopup(FxRibbon tab, Button button)
  {
    this.env = tab.env();
    Icon.FOLDER_ALTPEN.set(button,  tab.iconSize, 100, "Open File", Color3.YELLOW_FOLDER, e -> tab.env().chooseFile());
    FxHandle.Get(button).popup(ctx -> show(button));
  }

  public OpenPopup insert()
  {
    this.insert = true;
    return this;
  }

  @Override
  public void show(Node node)
  {
    this.clear();
    this.item(" Open ").act(e -> env.chooseFile());

    if (insert)
    {
      this.item(" Import ").act(e -> env.importFile());
      this.separator();
    }

    this.item(" Close ").act(e -> env.closeOCD());

    StringSet recentFiles = env.insight.prefs.recentFiles(5);
    if (recentFiles.isPopulated())
    {
      this.separator();
      for (String path : recentFiles)
        this.item(" " + File3.Filename(path) + " ").act(e -> env.load(File3.Get(path)));
    }
    super.show(node);
  }

  public static OpenPopup Attach(FxRibbon tab, Button button)
  {
    return new OpenPopup(tab, button);
  }

}
