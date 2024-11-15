package sugarcube.common.ui.gui;

import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.Graphics3;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Panel3 extends JPanel implements CmdActable
{
  public Panel3()
  {
    this("");
  }

  public Panel3(int margin)
  {
    this.setLayout(new BorderLayout(margin, margin));
  }

  public Panel3(String name)
  {
    this.setName(name);
    this.setLayout(new BorderLayout());
  }

  public Panel3(Component... components)
  {
    this("");
    this.setLayout(new BorderLayout());
    this.addCenter(components);
  }

  public Panel3(LayoutManager layout, Component... components)
  {
    this("");
    this.setLayout(layout);
    for (Component c : components)
      super.add(c);
  }

//  public void registerKeyboardAction(final Object o, final String methodName, final KeyStroke... keyStrokes)
//  {
//    Action3 action = new Action3(methodName)
//    {
//      @Override
//      public void actionPerformed(ActionEvent e)
//      {
//        Zen.Reflection.invokeMethod(o, methodName);
//      }
//    };
//
//    for (int i = 0; i < keyStrokes.length; i++)
//    {
//      this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStrokes[i], methodName + (keyStrokes.length == 1 ? "" : "_" + i));
//      this.getActionMap().put(methodName + (keyStrokes.length == 1 ? "" : "_" + i), action);
//    }
//  }
  public void addCenter(Component... c)
  {
    if (c.length > 0)
      this.add(c.length > 1 ? new Panel3(new FlowLayout(), c) : c[0], BorderLayout.CENTER);
  }

  public void addNorth(Component... c)
  {
    if (c.length > 0)
      this.add(c.length > 1 ? new Panel3(new FlowLayout(), c) : c[0], BorderLayout.NORTH);
  }

  public void addSouth(Component... c)
  {
    if (c.length > 0)
      this.add(c.length > 1 ? new Panel3(new FlowLayout(), c) : c[0], BorderLayout.SOUTH);
  }

  public void addEast(Component... c)
  {
    if (c.length > 0)
      this.add(c.length > 1 ? Box3.vertical(c) : c[0], BorderLayout.EAST);
  }

  public void addWest(Component... c)
  {
    if (c.length > 0)
      this.add(c.length > 1 ? Box3.vertical(c) : c[0], BorderLayout.WEST);
  }

  public void setPreferredSize(Rectangle2D r)
  {
    this.setPreferredSize(r.getWidth(), r.getHeight());
  }

  public void setPreferredSize(double w, double h)
  {
    this.setPreferredSize(new Dimension((int) (w + 0.5), (int) (h + 0.5)));
  }

  public static Panel3 flow(Component... components)
  {
    Panel3 flow = new Panel3();
    flow.setLayout(new FlowLayout());
    for (Component component : components)
      flow.add(component);
    return flow;
  }

  public void close()
  {
  }

  public void refresh()
  {
  }

  @Override
  public void act(String action, Object data)
  {
    if (action.equals(CmdActable.REFRESH))
      this.refresh();
  }

  public boolean isEmpty()
  {
    return this.getComponentCount() == 0;
  }

  public boolean hasComponent()
  {
    return this.getComponentCount() > 0;
  }

  public Component component()
  {
    if (this.getComponentCount() > 0)
      return this.getComponent(0);
    else
      return null;
  }

  @Override
  public String toString()
  {
    return getName();
  }

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    this.paintComponent3(new Graphics3(g, this.getSize()));
  }

  public void paintComponent3(Graphics3 g)
  {
  }

  public Panel3 setDimension(int w, int h)
  {
    Dimension dim = new Dimension(w, h);
    this.setMinimumSize(dim);
    this.setPreferredSize(dim);
    this.setMaximumSize(dim);
    return this;
  }

  public Point3 screenXY()
  {
    return new Point3(this.getLocationOnScreen());
  }
}
