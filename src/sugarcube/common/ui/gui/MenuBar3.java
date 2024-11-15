package sugarcube.common.ui.gui;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.graphics.geom.Compass;
import sugarcube.common.system.io.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuBar3 extends JMenuBar
{
  private final Frame3 frame;
//  private Image3 titleBarImage = new Image3(ImageIcon3.loadResourceImage("titlebar-sugarcube.png"));
  private Point frameLocation = null;
  private Point mouseLocation = null;
  private Map3<String, JMenu> menus = new Map3<String, JMenu>();
  protected Label3 icon = new Label3(Zen.S3_ICON);
  private GraphicsDevice[] screens = Screen.screens();
  private GraphicsDevice borderScreen = null;
  private int borderCounter = 0;

  public MenuBar3(final Frame3 frame)
  {
    super();
    this.frame = frame;
    this.setOpaque(false);
    this.setMargin(new Insets(0,2,2,0));
    this.setBorder(BorderFactory.createEmptyBorder());    

    MouseAdapter mouseListener = new MouseAdapter()
    {
      @Override
      public void mouseEntered(MouseEvent e)
      {
        screens = Screen.screens();
        Cursor3.setDefault(frame);        
      }

      @Override
      public void mouseDragged(MouseEvent e)
      {
        Point p = e.getLocationOnScreen();
        GraphicsDevice screen = screen(p);
        if (screen != borderScreen)
        {
          borderScreen = screen;
          borderCounter = 0;
        }
        else
          borderCounter++;

//        for (int i = 0; i < screens.length; i++)
//        {
//          Rectangle r = screens[i].getDefaultConfiguration().getBounds();
//          if (r.contains(p))
//            Zen.LOG.debug(this, ".mouseDragged in screen " + i + " (" + r.x + "," + r.y + "," + r.width + "," + r.height + ") at " + p.x + "," + p.y);
//        }
        if (mouseLocation == null)
        {
          mouseLocation = p;
          frameLocation = frame.getLocation();
        }
        int dx = p.x - mouseLocation.x;
        int dy = p.y - mouseLocation.y;
        frame.setLocation(frameLocation.x + dx, frameLocation.y + dy);
      }

      @Override
      public void mousePressed(MouseEvent e)
      {
        if (e.getClickCount() > 1)
          frame.maximize();
      }

      @Override
      public void mouseReleased(MouseEvent e)
      {
        if (borderCounter > 4)
          frame.maximize(compass(borderScreen, e.getLocationOnScreen()), borderScreen);
        mouseLocation = null;
        frameLocation = null;
        borderScreen = null;
        borderCounter = 0;
      }
    };
    this.addMouseListener(mouseListener);
    this.addMouseMotionListener(mouseListener);
  }

  public Compass compass(GraphicsDevice screen, Point p)
  {
    if (screen == null || p == null)
      return Compass.UNDEF;
    Rectangle r = screen.getDefaultConfiguration().getBounds();
    if (p.x == r.x)
      return Compass.WEST;
    else if (p.y == r.y)
      return Compass.NORTH;
    else if (p.x == r.x + r.width - 1)
      return Compass.EAST;
    else if (p.y == r.y + r.height - 1)
      return Compass.SOUTH;
    else
      return Compass.UNDEF;
  }

  public GraphicsDevice screen(Point p)
  {
    for (GraphicsDevice screen : screens)
      if (screen.getDefaultConfiguration().getBounds().contains(p))
        return screen;
    return null;
  }

  @Override
  public JMenu add(JMenu c)
  {
    super.add(c);
    this.menus.put(c.getText().toLowerCase().trim(), c);
    return c;    
  }

  public Label3 icon()
  {
    return this.icon;
  }

  public Label3 setIcon(Icon icon)
  {
    this.icon.setIcon(icon);
    return this.icon;
  }

  public JMenu getMenu(String name)
  {
    return this.menus.get(name.toLowerCase().trim());
  }

  @Override
  public void paintComponent(Graphics g)
  {    
    ((Graphics2D)g).setPaint(frame.bgColor);
    g.fillRect(0,0,this.getWidth(), this.getHeight());
  }
}
