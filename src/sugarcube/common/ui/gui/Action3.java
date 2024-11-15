package sugarcube.common.ui.gui;

import sugarcube.common.system.log.Log;
import sugarcube.common.ui.gui.icons.ImageIcon3;
import sugarcube.common.data.Clipboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Action3<T> extends AbstractAction
{
  @SuppressWarnings("rawtypes")
  public static final Action3 VOID = new Action3("NO_ACTION")
  {
  };

  public Action3(String name)
  {
    super(name);
  }

  public Action3(String name, Icon icon)
  {
    super(name, icon);
  }

  public Action3(String name, String iconName)
  {
    super(name, iconName == null || iconName.isEmpty() ? null : new ImageIcon3(iconName));
  }

  public Action3(String name, Class classPath, String iconName)
  {
    super(name, iconName == null || iconName.isEmpty() ? null : new ImageIcon3(classPath, iconName));
  }

  public Action3(String cmd, String name, Icon icon)
  {
    super(name, icon);
    this.setCommand(cmd);
  }

  public Action3(String cmd, String name, Class classPath, String iconName)
  {
    this(cmd, name, iconName == null || iconName.isEmpty() ? null : new ImageIcon3(classPath, iconName));
  }

  public Action3 enabled(boolean enabled)
  {
    super.setEnabled(enabled);
    return this;
  }

  @Override
  public String toString()
  {
    return this.name();
  }

  public Icon icon()
  {
    return (Icon) this.getValue(SMALL_ICON);
  }

  public Icon iconBig()
  {
    return (Icon) this.getValue(LARGE_ICON_KEY);
  }

  protected void init()
  {
    //used for Action initialization (i.e., kind of constructor)
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    try
    {
      this.act();
    }
    catch (Exception ex)
    {
      Log.warn(this, ".actionPerformed - exception thrown: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  public void act()
  {
  }

  public String string(String key)
  {
    Object value = this.getValue(key);
    return value == null ? null : value.toString();
  }

  public String command()
  {
    String cmd = this.string(AbstractAction.ACTION_COMMAND_KEY);
    return cmd == null ? name() : cmd;
  }

  public String name()
  {
    return this.string(AbstractAction.NAME);
  }

  public String tooltip()
  {
    return this.string(AbstractAction.SHORT_DESCRIPTION);
  }

  public String description()
  {
    return this.string(AbstractAction.LONG_DESCRIPTION);
  }

//  public T object()
//  {
//    return object;
//  }
//
//  public final void setObject(T object)
//  {
//    this.object = object;
//  }
  public Action3 put(String name, Object value)
  {
    this.putValue(name, value);
    return this;
  }

  public final Action3 setName(String name)
  {
    return this.put(NAME, name);
  }

  public final Action3 setIcon(Icon icon)
  {
    return this.put(SMALL_ICON, icon);
  }

  public final Action3 setBigIcon(Icon icon)
  {
    return this.put(LARGE_ICON_KEY, icon);
  }

  public final Action3 setCommand(String cmd)
  {
    return this.put(ACTION_COMMAND_KEY, cmd.isEmpty() ? null : cmd);
  }

  public final Action3 setTooltip(String tooltip)
  {
    return this.put(SHORT_DESCRIPTION, tooltip.isEmpty() ? null : tooltip);
  }

  public final Action3 setDescription(String description)
  {
    return this.put(LONG_DESCRIPTION, description.isEmpty() ? null : description);
  }

  public Action3 setCtrlAccelerator(int keyEvent)
  {
    return this.setAccelerator(keyEvent, ActionEvent.CTRL_MASK);
  }

  public Action3 setAccelerator(int keyCode)
  {
    return this.setAccelerator(keyCode, 0);
  }

  public Action3 setAccelerator(int keyCode, int modifiers)
  {
    return this.setAccelerator(KeyStroke.getKeyStroke(keyCode, modifiers));
  }

  public Action3 setAccelerator(KeyStroke keyStroke)
  {
    return this.put(ACCELERATOR_KEY, keyStroke);
  }

  public Button3 button()
  {
    return new Button3(this);
  }

  public static abstract class Exit extends Action3
  {
    public Exit()
    {
      super("Exit", "exit.png");
    }
  }

  public static class ImageCopy extends Action3
  {
    private BufferedImage image;

    public ImageCopy(BufferedImage image)
    {
      super("Copy", "copy.png");
      this.image = image;
      this.setCtrlAccelerator(KeyEvent.VK_C);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      Clipboard.clip(image);
    }
  }

  public static abstract class About extends Action3
  {
    public About()
    {
      super("About", "about.png");
    }
  }
}
