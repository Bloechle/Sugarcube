package sugarcube.common.ui.fx.task;

import sugarcube.common.system.time.Timer3;

import java.awt.event.ActionListener;

public class FxTimer extends Timer3
{
  public FxTimer()
  {
    this.isFX = true;
  }
  
  public FxTimer(boolean coalescent)
  {
    this.isFX = true;
    this.setCoalesce(coalescent);
  }


  public FxTimer(int delay, ActionListener listener, boolean repeats)
  {
    super(delay, listener, repeats);
    this.isFX = true;
  }
  
  @Override
  public FxTimer initialDelaySeconds(double seconds)
  {
    super.initialDelaySeconds(seconds);
    return this;
  }
  
  public FxTimer coalescent()
  {
    this.setCoalesce(true);
    return this;
  }
  
  public FxTimer nonCoalescent()
  {
    this.setCoalesce(false);
    return this;
  }
  
  @Override
  public FxTimer repeat(boolean repeat)
  {
    super.repeat(repeat);
    return this;
  }
  
  public FxTimer shot(double seconds, ActionListener listener)
  {    
    this.initialDelaySeconds(seconds);
    this.listen(listener);
    this.repeat(false);
    this.go();
    return this;
  }

  @Override
  public FxTimer go()
  {
    this.isFX = true;
    start();
    return this;
  }

  public static FxTimer Shot(double seconds, ActionListener listener)
  {
    return new FxTimer((int) Math.round(seconds * 1000), listener, false).initialDelaySeconds(seconds).nonCoalescent().go();
  }

  public static FxTimer Repeat(double seconds, ActionListener listener)
  {
    return new FxTimer((int) Math.round(seconds * 1000), listener, true).coalescent().go();
  }

}
