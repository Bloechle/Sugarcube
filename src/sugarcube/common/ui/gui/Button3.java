package sugarcube.common.ui.gui;

import sugarcube.common.interfaces.Actable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button3 extends JButton implements ActionListener, Actable
{ 
  public Button3(AbstractAction action, ActionListener... listeners)
  {
    super(action);
    this.addActionListener(this);    
    for (ActionListener listener : listeners)
      this.addActionListener(listener);

  }

  public Button3(Icon icon, ActionListener... listeners)
  {
    super(icon);
    this.addActionListener(this);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);    
  }

  public Button3(String text, ActionListener... listeners)
  {
    super(text);
    this.addActionListener(this);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
  }

  public Button3(String text, Icon icon, ActionListener... listeners)
  {
    super(text, icon);
    this.addActionListener(this);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
  }

  public Button3(Object text, ActionListener... listeners)
  {
    super(text.toString());
    this.addActionListener(this);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
  }
  
  public Button3 setDimension(Dimension d)
  {
    this.setPreferredSize(d);
    return this;
  }

  public Button3 setMargin(int margin)
  {
    return this.setMargin(margin, margin);
  }

  public Button3 setMargin(int topBottom, int leftRight)
  {
    return this.setMargin(topBottom, leftRight, topBottom, leftRight);
  }

  public Button3 setMargin(int top, int left, int bottom, int right)
  {
    this.setMargin(new Insets(top, left, bottom, right));
    return this;
  }  
  
  public static Button3 sugarcube(Action3 a, int marginX)
  {
    return sugarcube(a, marginX, 0);
  }

  public static Button3 sugarcube(Action3 a, int marginX, int marginY)
  {
    final Button3 button = new Button3(a);
    
    button.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseEntered(MouseEvent e)
      {
        if (!button.hasFocus())
        {
          if (!button.isRequestFocusEnabled())
            button.setRequestFocusEnabled(true);
          button.requestFocus();
        }
        button.setContentAreaFilled(true);
      }

      @Override
      public void mouseExited(MouseEvent e)
      {
        button.setContentAreaFilled(false);
      }
    });

    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setFocusable(false);
    button.setBorderPainted(false);
//    button.setBackground(Color3.TRANSPARENT);
    if (button.getIcon() != null)
      button.setText("");
    button.setToolTipText(a.name());
    button.setMargin(marginY, marginX);    
    return button;
  }    

  public Button3 sleep()
  {
    //this.setEnabled(false);
    return this;
  }

  public Button3 awake()
  {
    this.setEnabled(true);
    return this;
  }

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
  }

  public Button3 focus()
  {
    this.requestFocusInWindow();
    return this;
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    this.act();
//    this.actionPerformed(this, e);    
  }
  
  @Override
  public void act()
  {    
  }

//  public void actionPerformed(Button3 button, ActionEvent e)
//  {
//  }  
}
