package sugarcube.common.ui.gui;

import sugarcube.common.graphics.Color3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Toggle3 extends JToggleButton implements ActionListener
{
  public Toggle3(String text, ActionListener... listeners)
  {
    super(text);
    this.addActionListener(this);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
  }

  public Toggle3(String text, boolean selected, ActionListener... listeners)
  {
    super(text, selected);
    this.addActionListener(this);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
  }

  public Toggle3(String text, Icon icon, boolean selected, ActionListener... listeners)
  {
    super(text, icon, selected);
    this.addActionListener(this);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
  }

  public Toggle3(AbstractAction action, ActionListener... listeners)
  {
    super(action);
    this.addActionListener(this);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
  }

  public Toggle3 size(int w, int h)
  {
    return size(new Dimension(w, h));    
  }

  public Toggle3 size(Dimension d)
  {    
    this.setMinimumSize(d);
    this.setPreferredSize(d);
    this.setMaximumSize(d);
    return this;
  }

  public Toggle3 setMargin(int margin)
  {
    return this.setMargin(margin, margin);
  }

  public Toggle3 setMargin(int topBottom, int leftRight)
  {
    return this.setMargin(topBottom, leftRight, topBottom, leftRight);
  }

  public Toggle3 setMargin(int top, int left, int bottom, int right)
  {
    this.setMargin(new Insets(top, left, bottom, right));
    return this;
  }

  public static Toggle3 transparent(Action3 a, int margin)
  {
    final Toggle3 toggle = new Toggle3(a)
    {
      @Override
      public void setSelected(boolean b)
      {
        super.setSelected(b);
        setContentAreaFilled(isSelected() && isEnabled());
      }
    };

    toggle.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseEntered(MouseEvent e)
      {
        if (!toggle.hasFocus())
        {
          if (!toggle.isRequestFocusEnabled())
            toggle.setRequestFocusEnabled(true);
          toggle.requestFocus();
        }
        toggle.setContentAreaFilled(true);
      }

      @Override
      public void mouseExited(MouseEvent e)
      {
        if (!toggle.isSelected())
          toggle.setContentAreaFilled(false);
      }
    });

    toggle.setContentAreaFilled(false);
    toggle.setFocusPainted(false);
    toggle.setFocusable(false);
    toggle.setBorderPainted(false);
    toggle.setBackground(Color3.TRANSPARENT);
    toggle.setText("");
    toggle.setToolTipText(a.name());
    toggle.setMargin(margin);
    return toggle;
  }

  public void select()
  {
    this.setSelected(true);
  }

  public void unselect()
  {
    this.setSelected(false);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    this.actionPerformed(this, e);
  }

  public void actionPerformed(Toggle3 toggle, ActionEvent e)
  {
  }
}
