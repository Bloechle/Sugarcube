package sugarcube.common.ui.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckBox3 extends JCheckBox implements ActionListener
{
  public CheckBox3(boolean selected, ActionListener... listeners)
  {
    this(null, null, selected, listeners);
  }

  public CheckBox3(String text, boolean selected, ActionListener... listeners)
  {
    this(text, null, selected, listeners);
  }

  public CheckBox3(String text, Icon icon, boolean selected, ActionListener... listeners)
  {
    super(text, icon, selected);
    this.addActionListener(this);
    this.addListener(listeners);
  }

  public CheckBox3 addListener(ActionListener... listeners)
  {
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
    return this;
  }

  @Override
  public void actionPerformed(ActionEvent ae)
  {
    this.act();
  }

  public void act()
  {
  }
}
