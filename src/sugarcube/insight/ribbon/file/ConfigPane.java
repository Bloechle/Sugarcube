package sugarcube.insight.ribbon.file;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sugarcube.common.system.Prefs;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.dialogs.FxDirectoryChooser;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.formats.pdf.resources.icons.Icon;

public class ConfigPane extends FxEnvironmentPane
{
  public static final String USER_ID = "insight-user-id";
  public static final String USER_EMAIL = "insight-user-email";
  public static final String USER_DIR = "insight-user-dir";
  
  public static FxPaneLoader LOADER = env -> new ConfigPane(env);

  private @FXML TextField hotfolderField;
  private @FXML Button hotfolderBt;
  private @FXML TextField userID;
  private @FXML TextField userEmail;
  private @FXML TextField userDir;
  
  private Prefs prefs;


  public ConfigPane(FxEnvironment env)
  {
    super(env, "Configuration", "Configuration", Icon.Awesome(Icon.COG, 36, Color3.LIGHT_GRAY));
    hotfolderBt.setOnAction(e -> chooseHotfolder());
    this.prefs = env.insight.prefs;
    this.readStore(true);
  }
  
  public void readStore(boolean read)
  {
    prefs.set(read, USER_ID, userID);
    prefs.set(read, USER_EMAIL, userEmail);
    prefs.set(read, USER_DIR, userDir);
  }
  
  
  @Override
  public void unselect()
  {
    this.readStore(false);
  }
  
  public File3 hotfolder()
  {
    return File3.Get(hotfolderField.getText());
  }

  public void chooseHotfolder()
  {
    FxDirectoryChooser chooser = new FxDirectoryChooser("Set OCR hotfolder");
    chooser.setDir(prefs.get(Prefs.OCR_HOTFOLDER, ""));
    File3 file = chooser.show(env.window());
    if (file != null)
    {
      prefs.put(Prefs.OCR_HOTFOLDER, file.path());
      refresh();
    }
  }

  @Override
  public void refresh()
  {
    String hotfolder = prefs.get(Prefs.OCR_HOTFOLDER, "");
    hotfolderField.setText(hotfolder);
  }

}
