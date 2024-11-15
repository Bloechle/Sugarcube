package sugarcube.common.ui.gui;

import javax.swing.*;
import java.awt.*;

public class ScrollPane3 extends JScrollPane
{
  public ScrollPane3()
  {
    super();
    this.setScroll(true, true);
  }
  
  public ScrollPane3(String name)
  {
    this();
    this.setName(name);
  }

  public ScrollPane3(Component component)
  {
    this(component, true, true);
  }

  public ScrollPane3(Component component, boolean scrollV, boolean scrollH)
  {
    super(component);
    this.setScroll(scrollV, scrollH);
  }

  public ScrollPane3(Component component, boolean scrollV, boolean scrollH, int incrementV)
  {
    super(component);
    this.setScroll(scrollV, scrollH);
    this.setUnitIncrementV(incrementV);
  }

  public final void setUnitIncrementV(int unit)
  {
    this.getVerticalScrollBar().setUnitIncrement(unit);
  }

  public final void setUnitIncrementH(int unit)
  {
    this.getHorizontalScrollBar().setUnitIncrement(unit);
  }

  public final void setScroll(boolean vertical, boolean horizontal)
  {
    this.setVerticalScrollBarPolicy(vertical ? VERTICAL_SCROLLBAR_AS_NEEDED : VERTICAL_SCROLLBAR_NEVER);
    this.setHorizontalScrollBarPolicy(horizontal ? HORIZONTAL_SCROLLBAR_AS_NEEDED : HORIZONTAL_SCROLLBAR_NEVER);
  }
  

  public int barThickV()
  {
    return getVerticalScrollBar().isVisible() ? getVerticalScrollBar().getSize().width : 0;
  }

  public int barThickH()
  {
    return this.getHorizontalScrollBar().isVisible() ? this.getHorizontalScrollBar().getSize().height : 0;
  }  
    
  public void invokeVerticalMax()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
      }
    }); 
  }
}
