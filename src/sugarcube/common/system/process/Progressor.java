package sugarcube.common.system.process;

import sugarcube.common.interfaces.Progressable;

import java.io.Serializable;

public class Progressor implements Serializable, Progressable
{
  public String name = "";
  public String desc = "";
  public float progress = 0;
  public int state = STATE_READY;
  public int steps = 100;

  public Progressor()
  {
  }

  public Progressor(String name)
  {
    this.name = name;
  }

  public Progressor(String name, float progress)
  {
    this.name = name;
    this.progress = progress;
  }

  public Progressor(String name, String desc, float progress)
  {
    this.name = name;
    this.desc = desc;
    this.progress = progress;
    this.state = progress < 0 ? STATE_CANCELLED : progress >= 1 ? STATE_COMPLETED : STATE_READY;
  }
  
  public Progressor update(float progress, String desc)
  {
    this.progress = progress;
    this.desc = desc;
    return this;
  }
  
  public void reset()
  {
    this.state = STATE_READY;
    this.progress = 0;
  }

  @Override
  public float progress()
  {
    return progress;
  }

  @Override
  public String progressName()
  {
    return name;
  }

  @Override
  public int progressState()
  {
    return state;
  }

  @Override
  public String progressDescription()
  {
    return desc;
  }

  
  public Progressor complete(String desc)
  {
    update(1,  desc);
    state = STATE_COMPLETED;
    return this;
  }

}
