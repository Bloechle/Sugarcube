package sugarcube.common.ui.gui;

import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.gui.icons.ImageIcon3;

import javax.swing.*;
import java.awt.*;

public class Menu3 extends JMenu
{
  public Menu3()
  {
  }

  public Menu3(String name, Icon icon)
  {
    super(name == null ? "" : name);
    if (icon != null)
      this.setIcon(icon);
  }

  public Menu3(String name, Class iconClass, String iconName)
  {
    this(name, iconName == null || iconName.isEmpty() ? null : new ImageIcon3(iconClass, iconName));
  }

  public Menu3 sugarcubize()
  {
    this.setFont(this.getFont().deriveFont(Font.BOLD));
    this.setForeground(Color3.DUST_WHITE);
    return this;
  }

  public final Menu3 addItems(Object... items)
  {
    for (Object item : items)
      if (item == null)
        this.addSeparator();
      else if (item instanceof Action)
        this.add((Action) item);
      else if (item instanceof JMenuItem)
        this.add((JMenuItem) item);
      else if (item instanceof Component)
        this.add((Component) item);
      else if (item instanceof String)
      {
        String label = (String) item;
        if (label.equalsIgnoreCase("separator") || label.trim().isEmpty())
          this.addSeparator();
        else
          this.add((String) item);
      }
      else
        this.addSeparator();

    return this;
  }
}
