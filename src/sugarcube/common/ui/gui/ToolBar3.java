package sugarcube.common.ui.gui;

import sugarcube.common.ui.gui.icons.ImageIcon3;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class ToolBar3 extends JToolBar
{
  public ToolBar3()
  {
    this("");
  }

  public ToolBar3(int orientation)
  {
    super(orientation);
  }

  public ToolBar3(String name)
  {
    super(name);
  }

  public ToolBar3(boolean flowLayout)
  {
    this("", true);
  }

  public ToolBar3(String name, boolean flowLayout)
  {
    super(name);
    if (flowLayout)
      this.setLayout(new FlowLayout());
  }

  public void setMargin(int margin)
  {
    this.setMargin(new Insets(margin, margin, margin, margin));
  }

  public void remove(Object item)
  {
    if (item instanceof Action)
    {
      for (Component c : this.getComponents())
        if (c instanceof AbstractButton)
          if (((AbstractButton) c).getAction() == (Action) item)
          {
            this.remove(c);
            return;
          }
    }
    else if (item instanceof Component)
      this.remove((Component) item);
    else if (item instanceof MenuComponent)
      this.remove((MenuComponent) item);
    this.revalidate();
  }

  public ToolBar3 removeAll(Collection items)
  {
    return this.removeAll(items.toArray());
  }

  public ToolBar3 removeAll(Object... items)
  {
    for (Object item : items)
      this.remove(item);
    return this;
  }

  public void add(Object item)
  {
    if (item == null || item == Action3.VOID)
      this.addSeparator();
    else if (item instanceof Action)
      this.add((Action) item);
    else if (item instanceof JMenuItem)
      this.add((JMenuItem) item);
    else if (item instanceof Component)
      this.add((Component) item);
    else
      this.addSeparator();
    this.revalidate();
  }

  public ToolBar3 addAll(Collection items)
  {
    return this.addAll(items.toArray());
  }

  public ToolBar3 addAll(Object... items)
  {
    for (Object item : items)
      this.add(item);
    return this;
  }

  public void add(ToggleGroup group)
  {
    for (Toggle3 toggle : group)
      this.add(toggle);
  }

  @Override
  public JButton add(Action a)
  {
    JButton b = createActionComponent(a);
    b.setMargin(new Insets(0, 0, 0, 0));
    b.setAction(a);
    add(b);
    return b;
  }

  public JButton add(Action a, int index)
  {
    JButton b = createActionComponent(a);
    b.setMargin(new Insets(0, 0, 0, 0));
    b.setAction(a);
    add(b, index);
    return b;
  }

  @Override
  public void addSeparator()
  {
    super.add(new JLabel(new ImageIcon3(this.getOrientation() == HORIZONTAL ? "separator-v.png" : "separator-h.png")));
  }

  public void addSeparator(int index)
  {
    super.add(new JLabel(new ImageIcon3(this.getOrientation() == HORIZONTAL ? "separator-v.png" : "separator-h.png")), index);
  }
}
