package sugarcube.insight.ribbon.file;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Loggable;
import sugarcube.common.system.log.Logger;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.event.FxContext;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.menus.FxPopup;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.formats.pdf.resources.icons.Icon;

public class LogPane extends FxEnvironmentPane implements Loggable
{
  public static FxPaneLoader LOADER = env -> new LogPane(env);

  public @FXML HBox topBox;
  public @FXML WebView logView;
  // public @FXML TextArea infoArea;
  public final long time = System.currentTimeMillis();
  private int maxLines = 500;
  private Logger.Recorder recorder = new Logger.Recorder(maxLines);
  private FxPopup popup;
  private Logger.Level level = Logger.Level.DEBUG;

  public LogPane(FxEnvironment env)
  {
    super(env, env.insight.config.logTitle, env.insight.config.logTitle, Icon.Awesome(Icon.EXCLAMATION_TRIANGLE, 36, Color3.ORANGE));
    Logger.AddBigBrother(this);
    logView.setContextMenuEnabled(false);
    FxHandle handle = FxHandle.Get(logView);
    handle.popup(ctx -> popup(ctx));
  }

  public void popup(FxContext ctx)
  {
    if (ctx.isConsumed())
      return;

    if (popup == null)
      this.popup = new FxPopup();

    this.popup.clear();

    for (Logger.Level level : Logger.Level.reverse())
      this.popup.item(" " + level + " ", () -> level(level));

    this.popup.sep().item(" Clear Log", () -> clear());

    this.popup.show(logView, ctx.screenXY());
  }

  public String elapsedSeconds()
  {
    return (System.currentTimeMillis() - time) / 1000 + "";
  }

  public String log()
  {
    return recorder.htmlLog("Log", level, maxLines, false);
  }

  @Override
  public Loggable log(Object source, Object message, Logger.Level level)
  {
    recorder.add(source, message, level);
    this.refresh();
    return this;
  }

  private void setLog(String html)
  {
    Fx.Run(() -> this.logView.getEngine().loadContent(html));
  }

  @Override
  public void refresh()
  {
    if (this.isSelected)
    {
      this.recorder.level(level);
      this.setLog(log());
    }
  }

  public void level(Logger.Level level)
  {
    this.level = level;
    this.refresh();
  }

  public void clear()
  {
    this.recorder.clear();
    this.refresh();
  }

}
