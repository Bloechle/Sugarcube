package sugarcube.common.system.io;

public class Active
{
  private boolean active = true;

  /**
   * Deadens the object, if the object was already
   * asleep, returned false, else return true.
   * @return true if the object was awake.
   */
  public synchronized boolean sleep()
  {
    if (!active)
      return false;
    else
      return !(active=false);
  }

  public synchronized boolean awake()
  {
    if(active)
      return false;
    else
      return active=true;
  }

  public boolean isActive()
  {
    return active;
  }
  
  public boolean isTrue()
  {
    return active;
  }
  
  public boolean isFalse()
  {
    return !active;
  }
}
