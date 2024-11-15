package sugarcube.formats.ocd.objects;

import sugarcube.common.system.time.Timer3;
import sugarcube.insight.core.FxEnvironment;

public class OCDPlayer
{
  private FxEnvironment env;
  private Timer3 timer;
  private int fps = 10;

  public OCDPlayer(FxEnvironment env)
  {
    this.env = env;
    this.timer = new Timer3();
  }

  public boolean playNext()
  {
    OCDPage page = env.page();
    if (page != null && (page = page.next()) != null)
    {
      env.updatePage(page);
      return true;
    }
    return false;
  }

  public void play()
  {
    int millis = 1000 / fps;
    timer.repeat(true);
    timer.delay(millis);
    timer.handle(() -> {
      if (!playNext())
        stop();
    });
    timer.goFX();
  }

  public void stop()
  {
    timer.stop();
  }
}
