package sugarcube.common.system.time;

import java.util.TimerTask;

public class TimerTask3 extends TimerTask
{
  private Runnable runnable;
  
  public TimerTask3(Runnable runnable)
  {
    this.runnable = runnable;
  }

  @Override
  public void run()
  {
    if (runnable != null)
      runnable.run();
  }

}
