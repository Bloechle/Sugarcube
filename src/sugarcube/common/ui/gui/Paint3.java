package sugarcube.common.ui.gui;

import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.hardware.Mouse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;
import java.util.Set;

public class Paint3 extends Panel3
{
  public static interface Listener
  {
    public void paintGraphics(Graphics3 g);
  }
  protected Set<Listener> listeners = new LinkedHashSet<Listener>();
  protected JScrollPane scrollPane;
  protected PaintPanel paintPanel;

  public Paint3(String title)
  {
    this(title, 1, 1);
  }

  public Paint3(String title, int width, int height)
  {
    super(title);
    this.paintPanel = new PaintPanel(new Dimension(width, height));
    this.scrollPane = new JScrollPane(this.paintPanel);

    this.add(this.scrollPane);
  }

  public void addGraphicsListener(Listener listener)
  {
    this.listeners.add(listener);
  }

  public void removeGraphicsListener(Listener listener)
  {
    this.listeners.remove(listener);
  }

  public Graphics3 graphics()
  {
    return paintPanel.context.graphics();
  }

  public PaintPanel paintPanel()
  {
    return this.paintPanel;
  }

  @Override
  public void refresh()
  {
    this.paintPanel.repaint();
  }

  //this method may be overridden by subclasses :-)
  protected void paintGraphics(Graphics3 g)
  {
  }

  public void mouseWheelMoved(MouseWheelEvent e)
  {
  }

  protected class PaintPanel extends JPanel
  {
    private Image3 context;
    private Popup3 popup;
    private double scale = 1.0;

    public PaintPanel(Dimension dimension)
    {
      this.context = new Image3(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
      this.setPreferredSize(dimension);
      this.addMouseListener(new PopupListener());
      this.addMouseWheelListener(new ZoomListener());
    }

    public int width()
    {
      return context == null ? this.getWidth() : context.width();
    }

    public int height()
    {
      return context == null ? this.getHeight() : context.height();
    }

    private void refreshContext()
    {
      int width = scrollPane.getViewport().getWidth();
      int height = scrollPane.getViewport().getHeight();
      if (context == null || width != (int) (context.getWidth() / scale) || height != (int) (context.getHeight() / scale))
      {
        this.context = new Image3(scale * width, scale * height);
        this.setPreferredSize(context.dimension());
        this.revalidate();
      }
    }

    private class PopupListener extends MouseAdapter
    {
      public void popupize(MouseEvent e)
      {
        if (e.isPopupTrigger())
        {
          Popup3 menu = new Popup3(PaintPanel.this);
          menu.add(new Action3.ImageCopy(context));
          menu.popup(new Mouse(e, Mouse.POPUP, null));
        }
      }

      @Override
      public void mousePressed(MouseEvent e)
      {
        this.popupize(e);
      }

      @Override
      public void mouseReleased(MouseEvent e)
      {
        this.popupize(e);
      }
    }

    private class ZoomListener extends MouseAdapter
    {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
        if (e.isControlDown())
        {
          int rotation = -e.getWheelRotation();
          scale = rotation >= 0 ? scale * (1.0 + rotation * 0.1) : scale / (1.0 - rotation * 0.1);
          scale = scale < 0.1 ? 0.1 : scale > 0.95 && scale < 1.1 ? 1 : scale > 10 ? 10 : Math.round(100 * scale) / 100.0;
          repaint();
        }
        else
          Paint3.this.mouseWheelMoved(e);
      }
    }

    public Popup3 popup()
    {
      return popup;
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
      super.paintComponent(graphics);
      this.refreshContext();
      Graphics3 g = context.graphics();

      g.setScale(scale);
      paintGraphics(g);
      for (Listener listener : listeners)
        listener.paintGraphics(g);

      graphics.drawImage(context, 0, 0, null);
    }
  }
}
