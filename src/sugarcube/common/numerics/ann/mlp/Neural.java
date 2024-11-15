package sugarcube.common.numerics.ann.mlp;

import sugarcube.common.graphics.Graphics3;

public abstract class Neural
{
  private final boolean isNeuron;
  protected String name = "";
  protected boolean isEnabled = true;
  protected boolean isSelected = false;

  public Neural(boolean isNeuron)
  {
    this.isNeuron = isNeuron;
  }

  //should be called after each learning cycle, i.e., epoch
  public void cycleElapsed()
  {}

  public String name()
  {
    return name;
  }
  
  public boolean isName(String name)
  {
    return this.name.equals(name);
  }

  public void setName(String name)
  {
    this.name=name;
  }
 
  public abstract void paint(Graphics3 g);

  public boolean isNeuron()
  {
    return isNeuron;
  }

  public boolean isLink()
  {
    return !isNeuron;
  }

  public Neuron toNeuron()
  {
    return this.isNeuron() ? (Neuron) this : null;
  }

  public Link toLink()
  {
    return this.isLink() ? (Link) this : null;
  }

  public boolean isEnabled()
  {
    return this.isEnabled;
  }

  public void setEnabled(boolean isEnabled)
  {
    this.isEnabled=isEnabled;
  }

  public void enable()
  {
    this.isEnabled = true;
  }

  public void disable()
  {
    this.isEnabled = false;
  }

  public void isEnabledSwap()
  {
    this.isEnabled=!this.isEnabled;
  }

  public boolean isSelected()
  {
    return this.isSelected;
  }

  public void select()
  {
    this.isSelected = true;
  }

  public void unselect()
  {
    this.isSelected = false;
  }

  @Override
  public String toString()
  {
    return name; //never change this
  }
}
