package sugarcube.common.ui.gui;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.hardware.Mouse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class Popup3 extends JPopupMenu
{
  public static interface Listener
  {
    public boolean popupRequest(Popup3 popup, Mouse e);
  }
  protected Component source;
  protected Listener listener;

  public Popup3()
  {
    this(null);
  }

  public Popup3(Component source)
  {
    this(source, null);
  }

  public Popup3(Component source, Listener listener)
  {
    super();
    this.source = source;
    this.listener = listener;
    if (source != null)
      source.addMouseListener(new MouseAdapter()
      {
        @Override
        public void mouseReleased(MouseEvent e)
        {
          popup(new Mouse(e, Mouse.POPUP, null));
        }
      });
  }

  public void clear()
  {
    this.removeAll();
  }

  public void remove(Object item)
  {
    if (item instanceof JMenuItem)
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

  public Popup3 removeAll(Collection items)
  {
    return this.removeAll(items.toArray());
  }

  public Popup3 removeAll(Object... items)
  {
    for (Object item : items)
      this.remove(item);
    return this;
  }

  public void add(Object item)
  {
    if (item instanceof Object[])
      for (Object o : (Object[]) item)
        add(o);
    else if (item == null || item == Action3.VOID)
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

  public Popup3 addAll(Collection items)
  {
    return this.addAll(items.toArray());
  }

  public Popup3 addAll(Object... items)
  {
    for (Object item : items)
      this.add(item);
    return this;
  }
  
  public Popup3 popup()
  {
    if (source!=null)
    {
//        Log.debug(this, ".popup - "+ms);
      this.show(source, 0, source.getHeight());
      this.revalidate();
      this.repaint();
    }    
    return this;
  }

  public Popup3 popup(Mouse ms)
  {
    if (source != null && source instanceof AbstractButton && getComponentCount() > 0)
    {
//        Log.debug(this, ".popup - "+ms);
      this.show(ms.getComponent(), 0, source.getHeight());
      this.revalidate();
      this.repaint();
    }
    else if (ms.isPopupTrigger() && (listener == null || listener.popupRequest(this, ms)) && getComponentCount() > 0)
    {
      this.show(ms.getComponent(), ms.getX(), ms.getY());
      this.revalidate();
      this.repaint();
      //Zen.LOG.debug(this, ".popup: isShowing=" + this.isShowing() + ", xy=" + this.getLocationOnScreen());
    }

    return this;
  }

  public static void main(String... args)
  {
    Frame3 frame = new Frame3("PopupMenu3 Frame", 400, 300);
    Popup3 popup = new Popup3(frame.getContentPane(), new Popup3.Listener()
    {
      @Override
      public boolean popupRequest(Popup3 popup, Mouse e)
      {
        popup.clear();
        popup.add(new Action3("Action Item")
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            Log.info(this, ".main - action performed");
          }
        });
        return true;
      }
    });
    frame.display();
  }
}
