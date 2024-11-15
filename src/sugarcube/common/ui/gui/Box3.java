package sugarcube.common.ui.gui;

import javax.swing.*;
import java.awt.*;

public class Box3 extends Box
{
  
  public Box3()
  {
    this(false);
  }

  public Box3(boolean vertical)
  {
    super(vertical ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS);
  }
  
  public static Component hGap(int pixels)
  {
    return Box3.createHorizontalStrut(pixels);
  }
  
  public static Component vGap(int pixels)
  {
    return Box3.createVerticalStrut(pixels);
  }  
  
  public static Box3 v(Component... components)
  {
    return vertical(components);
  }
  
  public static Box3 h(Component... components)
  {
    return horizontal(components);
  }  
  
  public static Box3 v(float alignmentX, Component... components)
  {
    return vertical(alignmentX, components);
  }  
  
  public static Box3 h(float alignmentX, Component... components)
  {
    return horizontal(alignmentX, components);
  }   
  
  public static Box3 vertical(float alignmentX, Component... components)
  {
    Box3 box = new Box3(true);
    box.setAlignmentX(alignmentX);
    for (Component component : components)
      box.add(component);
    return box;
  }  

  public static Box3 vertical(Component... components)
  {
    Box3 box = new Box3(true);
    for (Component component : components)
      box.add(component);
    return box;
  }
  
  public static Box3 horizontal(float alignmentX, Component... components)
  {
    Box3 box = new Box3(false);
    box.setAlignmentX(alignmentX);
    for (Component component : components)
      box.add(component);
    return box;
  }    

  public static Box3 horizontal(Component... components)
  {
    Box3 box = new Box3(false);
    for (Component component : components)
      box.add(component);
    return box;
  }

  public Box3 add(Component... components)
  {
    for (Component component : components)
      super.add(component);
    return this;
  }
  
  public Box3 margin(int margin)
  {
    this.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
    return this;
  }
}
