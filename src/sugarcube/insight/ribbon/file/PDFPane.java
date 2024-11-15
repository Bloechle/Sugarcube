package sugarcube.insight.ribbon.file;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Progressable;
import sugarcube.common.system.process.Progression;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.formats.pdf.resources.icons.Icon;
import sugarcube.formats.pdf.writer.PDFParams;

public class PDFPane extends FxEnvironmentPane implements Progression.Listener
{
  public static FxPaneLoader LOADER = env -> new PDFPane(env);

  private @FXML Button convertBt;
  private @FXML CheckBox transparentCheck;

  public PDFPane(FxEnvironment env)
  {
    super(env, "PDF", "PDF Printer", Icon.Awesome(Icon.FILE_PDF_ALT, 36, Color3.ANTHRACITE));

    Fx.MaximizeWidth(convertBt);

    convertBt.setOnAction(e -> {
      PDFParams params = new PDFParams();
      params.setTransparent(transparentCheck.selectedProperty().getValue());

      new PDFFx(env, this).setParams(params).start();
    });
  }

  @Override
  public void progress(Progressable progression)
  {
    env.gui.glassPane.progress(progression.progress());
    env.message(progression.progressDescription(), 0, null);
    if (progression.isProgressComplete())
      message("PDF Written", 3, true, null);
  }

}
