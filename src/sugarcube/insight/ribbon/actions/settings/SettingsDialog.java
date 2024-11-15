package sugarcube.insight.ribbon.actions.settings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import sugarcube.common.system.Prefs;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.OnClose;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.dialogs.FxWindow;
import sugarcube.insight.core.FxDisplayProps;
import sugarcube.insight.core.FxGUI;
import sugarcube.insight.core.IS;
import sugarcube.formats.pdf.resources.icons.Icon;

public class SettingsDialog extends FxWindow
{
  private @FXML BorderPane rootPane;

  public @FXML CheckBox showText, showImages, showPaths, applyFonts, applyClips, highRuns, highClips, highPaths;
  public @FXML Spinner<Integer> nbOfLeaves;

  private FxGUI gui;

  public SettingsDialog(FxGUI gui, OnClose onClose)
  {
    super("Settings Dialog", false, gui.window(), true);
    IS.Darky(windowPane, rootPane);

    this.gui = gui;
    this.icon(Icon.Image(Icon.GEAR, 48, Color3.ANTHRACITE));
    this.minSize(300, 220);
    this.noModality();
    this.refresh();
    this.setOnClose(onClose);
    this.show();
    
    FxDisplayProps display = gui.display;
    this.showText.setSelected(display.texts);
    this.showImages.setSelected(display.images);
    this.showPaths.setSelected(display.paths);
    this.applyFonts.setSelected(display.fonts);
    this.applyClips.setSelected(display.clips);
    this.highRuns.setSelected(display.highlightTexts);
    this.highClips.setSelected(display.highlightClips);
    this.highPaths.setSelected(display.highlightPaths);

    nbOfLeaves.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0));
    nbOfLeaves.valueProperty().addListener((obs, old, val) -> refresh());
    Fx.Listen((obs, old, val) -> refresh(), showText, showImages, showPaths, applyFonts, highRuns, applyClips, highClips, highPaths);
    
    refresh();
  }

  public void refresh()
  {    
    Log.debug(this,  ".refresh");
    Prefs prefs = gui.env().insight.prefs;
    
    prefs.store(IS.TEXTS_ON, showText);
    prefs.store(IS.IMAGES_ON, showImages);
    prefs.store(IS.PATHS_ON, showPaths);

    prefs.store(IS.FONTS_ON, applyFonts);
    prefs.store(IS.CLIPS_ON, applyClips);

//    highlightSpaces = prefs.bool(IS.SPACES_HIGH, highlightSpaces);
    prefs.store(IS.TEXTS_HIGH, highRuns);
    prefs.store(IS.PATHS_HIGH, highPaths);
    prefs.store(IS.CLIPS_HIGH, highClips);
    prefs.store(IS.ELEMENTS_NB, nbOfLeaves);
    
    gui.env().ribbon().update();
  }

  public void close()
  {
    Log.debug(this, ".close");
    super.close();
  }

  @Override
  public void onClose()
  {
    Log.debug(this, ".onClose");
    super.onClose();
  }

  public static void Show(FxGUI gui)
  {
    if (gui.displayDialog == null)
      gui.displayDialog = new SettingsDialog(gui, () -> {
        gui.displayDialog.close();
        gui.displayDialog = null;
        gui.refresh();
      });
    gui.displayDialog.toFront();
  }
}
