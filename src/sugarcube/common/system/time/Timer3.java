package sugarcube.common.system.time;

import sugarcube.common.system.log.Log;
import sugarcube.common.interfaces.Actable;
import sugarcube.common.ui.fx.base.Fx;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Timer3 extends Timer implements ActionListener, Actable
{
  public static int DEFAULT_DELAY = 200; // ms
  protected boolean isFX = false;
  protected boolean isSwing = false;
  private Actable handler;
  private ActionListener listener;
  private boolean isActive = true;

  public Timer3()
  {
    this(DEFAULT_DELAY, null);
  }

  public Timer3(int delay)
  {
    this(delay, null);
  }

  public Timer3(int delay, ActionListener listener)
  {
    this(delay, listener, true);
  }

  public Timer3(int delay, ActionListener listener, boolean repeats)
  {
    super(delay, null);
    this.addActionListener(this);
    this.setCoalesce(true);
    this.setRepeats(repeats);
    this.listener = listener;
  }

  public Timer3 repeat(boolean doRepeat)
  {
    this.setRepeats(doRepeat);
    return this;
  }

  public Timer3 delay(int millis)
  {
    this.setDelay(millis);
    return this;
  }

  public Timer3 initialDelaySeconds(double seconds)
  {
    this.setInitialDelay((int) Math.round(seconds * 1000));
    return this;
  }

  public Timer3 delaySeconds(double seconds)
  {
    return delay((int) Math.round(seconds * 1000));
  }

  public Timer3 listen(ActionListener listener)
  {
    this.listener = listener;
    return this;
  }

  public Timer3 handle(Actable handler)
  {
    this.handler = handler;
    return this;
  }

  public Timer3 activate()
  {
    this.isActive = true;
    return this;
  }

  public Timer3 deactivate()
  {
    this.isActive = false;
    return this;
  }

  public Timer3 go()
  {
    this.start();
    return this;
  }

  public Timer3 shotFX()
  {
    this.setRepeats(false);
    return this.goFX();
  }

  public Timer3 goFX()
  {
    this.isFX = true;
    this.isSwing = false;
    return this.go();
  }

  public void close()
  {
    this.stop();
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (isFX)
      Fx.Run(() -> pleaseAct(e));
    else if (isSwing)
      SwingUtilities.invokeLater(() -> pleaseAct(e));
    else
      pleaseAct(e);
  }

  private void pleaseAct(ActionEvent e)
  {
    if (handler != null && listener != null)
      Log.debug(this, ".pleaseAct - both listener and handler are not null");
    if (isActive)
    {
      this.act();
      if (listener != null && listener != this)
        listener.actionPerformed(e);
      if (handler != null && handler != this)
        handler.act();
    }
  }

  @Override
  public void act()
  {
  }

  public static Timer3 FxShot(double seconds, ActionListener listener)
  {
    return new Timer3((int) Math.round(seconds * 1000), listener, false).initialDelaySeconds(seconds).goFX();
  }

  public static Timer3 Repeat(int delay, Actable handler)
  {
    return new Timer3(delay).handle(handler);
  }

  public static java.util.Timer Schedule(long delay, Runnable runnable)
  {
    java.util.Timer timer = null;
    try
    {
      if (delay <= 0)
        runnable.run();
      else
      {
        timer = new java.util.Timer();
        timer.schedule(new TimerTask3(runnable), delay);
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return timer;
  }
}
