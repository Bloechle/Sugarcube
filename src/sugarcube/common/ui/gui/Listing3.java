package sugarcube.common.ui.gui;

import sugarcube.common.data.Zen;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class Listing3<T> extends JList
{
  public Listing3()
  {
  }
  
  public Listing3(Iterator<T> it)
  {
    super(new Zen.Generic<T>().toVector(it));
  }

  public Listing3(T... objects)
  {
    super(objects);
  }    
  
  public T selected()
  {
    return (T)this.getSelectedValue();
  } 
  
  public Listing3 size(int w, int h)
  {
    return size(new Dimension(w, h));
  }

  public Listing3 size(Dimension dim)
  {
    this.setPreferredSize(dim);
    this.setMinimumSize(dim);
    this.setMaximumSize(dim);
    return this;
  }  
}
