package sugarcube.common.ui.gui;

import javax.swing.*;
import java.awt.*;

public class SplitPane3 extends JSplitPane
{
	public SplitPane3()
	  {
	    this(true, null, null);
	  }

  public SplitPane3(Component first, Component second)
  {
    this(true, first, second);
  }
  
  public SplitPane3(boolean horizontal, Component first, Component second)
  {
    super(horizontal ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT, first, second);
    this.initialize();
  }
  
  public SplitPane3(boolean horizontal, Component first, Component second, double location)
  {
    super(horizontal ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT, first, second);
    this.initialize();
    this.setDividerLocation(location);
  }  
  
  public SplitPane3(boolean horizontal, Component first, Component second, int location)
  {
    super(horizontal ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT, first, second);
    this.initialize();
    if (location > 0)
      this.setDividerLocation(location);
    else
      this.setDividerLocation_(-location);
        
  }
  
  private void initialize()
  {
    this.setOneTouchExpandable(true);
  }
  
  public void setSplit(int location, double weight)
  {
    this.setDividerLocation(location);
    this.setResizeWeight(weight);
  }
  
  public void setSplit_(int location, double weight)
  {
    this.setDividerLocation_(location);
    this.setResizeWeight(1.0 - weight);
  }
  
  public void setDividerLocation_(int location)
  {
    this.setDividerLocation(this.getSize().width - this.getInsets().right - this.getDividerSize() - location);
  }
}
