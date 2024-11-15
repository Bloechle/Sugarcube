package sugarcube.common.system.time;

import sugarcube.common.ui.fx.base.Fx;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RunTimer implements Runnable
{
  public ScheduledExecutorService service;
  public Runnable runnable = null;

  public RunTimer()
  {
    this(1);
  }

  public RunTimer(int size)
  {
    service = size > 1 ? Executors.newScheduledThreadPool(size) : Executors.newSingleThreadScheduledExecutor();
  }

  public RunTimer(Runnable run)
  {
    this.runnable = run;
  }

  public void schedule(long millis)
  {
    schedule(millis, null);
  }

  public void schedule(long millis, Runnable runnable)
  {
    Runnable run = runnable == null ? this.runnable : runnable;
    service.schedule(run == null ? this : run, millis, TimeUnit.MILLISECONDS);
  }

  public void scheduleFX(long millis, Runnable runnable)
  {
    schedule(millis, () -> Fx.Run(runnable));
  }
  
  public void dispose()
  {
    if(service!=null)
      service.shutdown();
  }

  @Override
  public void run()
  {

  }
}
