package sugarcube.common.system.io.hardware;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.awt.event.*;

public class InputDevice
{
  public static void addMouseHandler(Component c, MouseHandler handler)
  {
    c.addMouseListener(handler);
    c.addMouseMotionListener(handler);
    c.addMouseWheelListener(handler);
  }

  public static void addKeyHandler(Component c, KeyHandler handler)
  {
    c.addKeyListener(handler);
  }

  public static void addMouseKeyHandler(Component c, MouseKeyHandler handler)
  {
    addMouseHandler(c, handler);
    addKeyHandler(c, handler);
  }

  public interface MouseHandler extends MouseListener, MouseWheelListener, MouseMotionListener
  {
  }

  public interface KeyHandler extends KeyListener
  {
  }

  public interface MouseKeyHandler extends MouseHandler, KeyHandler
  {
  }

  public static class MouseAdapter extends java.awt.event.MouseAdapter implements MouseHandler
  {
  }

  public long timestamp;
  public boolean shift = false;
  public boolean ctrl = false;
  public boolean meta = false;
  public boolean alt = false;
  public Object event;

  public InputDevice()
  {
  }

  public InputDevice(Object e)
  {
    this.event = e;
    if (e instanceof InputEvent)
    {
      InputEvent ie = (InputEvent) e;
      this.timestamp = ie.getWhen();
      int modifiers = ie.getModifiers();
      this.shift = (modifiers & InputEvent.SHIFT_MASK) != 0;
      this.ctrl = (modifiers & InputEvent.CTRL_MASK) != 0;
      this.meta = (modifiers & InputEvent.META_MASK) != 0;
      this.alt = (modifiers & InputEvent.ALT_MASK) != 0;
    } else if (e instanceof MouseEvent)
    {
      this.timestamp = System.currentTimeMillis();
      MouseEvent me = (MouseEvent) e;
      this.shift = me.isShiftDown();
      this.ctrl = me.isControlDown();
      this.meta = me.isMetaDown();
      this.alt = me.isAltDown();
    } else if (e instanceof KeyEvent)
    {
      this.timestamp = System.currentTimeMillis();
      KeyEvent ke = (KeyEvent) e;
      this.shift = ke.isShiftDown();
      this.ctrl = ke.isControlDown();
      this.meta = ke.isMetaDown();
      this.alt = ke.isAltDown();
    }
  } 
  
  public boolean isShiftDown()
  {
    return this.shift;
  }  
  
  public boolean isControlDown()
  {
    return this.ctrl;
  }

}
