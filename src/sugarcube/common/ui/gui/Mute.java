package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.List3;

public class Mute
{
  private List3<Boolean> stack = new List3<>();
  private boolean on = false;

  public Mute()
  {
  }

  public Mute(boolean on)
  {
    this.on = on;
  }

  public synchronized boolean set(boolean on)
  {
    boolean old = this.on;
    stack.add(old);
    this.on = on;
    return old;
  }

  public boolean back()
  {
    return this.on = (stack.isEmpty() ? false : stack.removeLast());
  }

  public boolean setOn()
  {
    return set(true);
  }

  public boolean setOff()
  {
    return set(false);
  }

  public boolean isOn()
  {
    return on;
  }

  public boolean isOff()
  {
    return !on;
  }
}
