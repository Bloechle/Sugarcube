package sugarcube.common.ui.gui;

import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.geom.Compass;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.gui.icons.ImageIcon3;
import sugarcube.common.system.io.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Frame3 extends JFrame implements WindowListener
{
  static
  {
    Sys.LAF();
  }
  private Compass selectionCompass;
  private Rectangle frameBounds;
  private Dimension dimension;
  private Compass frameCompass;
  private Panel3 panel = new Panel3(0);
  private Mute mute = new Mute();
  private int frameBorderSize = 4;
  private MenuBar3 menuBar = null;
  private Menu3 sugarMenu = new Menu3();
  private JLabel sugarTitle = new JLabel();
  protected Color bgColor = Color3.DARK_GRAY;
  private StringMap<Component> items = new StringMap<>();
  
  private Rectangle maxBounds = null;

  public Frame3(String title)
  {
    this(title, null, Compass.CENTER);
  }

  public Frame3(String title, int w, int h)
  {
    this(title, new Dimension(w, h), Compass.CENTER);
  }

  public Frame3(String title, double sw, double sh, Compass compass)
  {
    this(title, Screen.bounds(sw, sh).size().dimension(), compass);
  }

  public Frame3(String title, Dimension dimension, Compass compass)
  {
    super(title);
    this.dimension = dimension;
    this.frameCompass = compass;
    this.mute.setOn();
    this.setIconImage(ImageIcon3.SUGARCUBE32.getImage());
    this.setDisposeOnClose(true);
    this.getContentPane().add(panel, BorderLayout.CENTER);
    this.addWindowListener(this); //careful with this, need to remove it in order to terminate the JVM   
    this.setMinimumSize(new Dimension(150, 32));
    this.setDefaultCloseOperation(Frame3.DISPOSE_ON_CLOSE);
  }

  public MenuBar3 menuBar()
  {
    return this.menuBar;
  }

  public Menu3 sugarMenu()
  {
    return this.sugarMenu;
  }

  public MenuBar3 sugarcubize(Object... items)
  {
    this.setUndecorated(true);
    this.setBackground(this.bgColor);
    this.getRootPane().setBorder(Border3.compound(Border3.line(bgColor.brighter(), 1), Border3.line(bgColor, frameBorderSize - 1)));

    if (this.isResizable())
    {
      MouseAdapter mouseListener = new MouseAdapter()
      {
        @Override
        public void mouseEntered(MouseEvent e)
        {
          if (e.getModifiersEx() != MouseEvent.BUTTON1_DOWN_MASK)
            setCompassCursor(selectionCompass = compass(e.getLocationOnScreen(), getBounds()));
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
          if (e.getModifiersEx() != MouseEvent.BUTTON1_DOWN_MASK)
            setCompassCursor(selectionCompass = Compass.UNDEF);
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
          frameBounds = getBounds();
          Rectangle r = frameBounds;
          Point p = e.getLocationOnScreen();

          if (isResizable())
            switch (selectionCompass)
            {
              case NORTH:
                Frame3.this.setBounds(r.x, p.y, r.width, (int) (r.getMaxY() - p.y));
                break;
              case NORTH_EAST:
                Frame3.this.setBounds(r.x, p.y, p.x - r.x, (int) (r.getMaxY() - p.y));
                break;
              case EAST:
                Frame3.this.setBounds(r.x, r.y, p.x - r.x, r.height);
                break;
              case SOUTH_EAST:
                Frame3.this.setBounds(r.x, r.y, p.x - r.x, p.y - r.y);
                break;
              case SOUTH:
                Frame3.this.setBounds(r.x, r.y, r.width, p.y - r.y);
                break;
              case SOUTH_WEST:
                Frame3.this.setBounds(p.x, r.y, (int) (r.getMaxX() - p.x), p.y - r.y);
                break;
              case WEST:
                Frame3.this.setBounds(p.x, r.y, (int) (r.getMaxX() - p.x), r.height);
                break;
              case NORTH_WEST:
                Frame3.this.setBounds(p.x, p.y, (int) (r.getMaxX() - p.x), (int) (r.getMaxY() - p.y));
                break;
              default:
                break;
            }
          Frame3.this.repaint();

        }

        @Override
        public void mousePressed(MouseEvent e)
        {
          setCompassCursor(selectionCompass = compass(e.getLocationOnScreen(), getBounds()));
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
          setCompassCursor(selectionCompass = Compass.UNDEF);
        }
      };

      this.getRootPane().addMouseListener(mouseListener);
      this.getRootPane().addMouseMotionListener(mouseListener);
    }

    this.sugarMenu.sugarcubize();    
    this.sugarMenu.addItems(items);
    this.setDisposeOnClose(true);    
    return refreshItemBar();
  }

  public void addItem(String name, Component c)
  {
    this.items.put(name, c);
  }

  public Button3 addButtonItem(String name, Action3 a)
  {
    Button3 b = Button3.sugarcube(a, 2, 0);    
    b.setForeground(Color.WHITE);
    b.setBackground(bgColor);
    Font font = b.getFont();
    font = font.deriveFont(Font.PLAIN);
    font = font.deriveFont(16f);
    b.setFont(font);
    this.addItem(name, b);
    return b;
  }

//  public Button3 addDingButtonItem(String name, Action3 a)
//  {
//    Button3 b = Button3.sugarcube(a, 2, 0);
//    b.setForeground(Color.WHITE);    
////    b.setFont(FontRS.font(FontRS.WEBDINGS).deriveFont(20f));
//    this.addItem(name, b);
//    return b;
//  }  
  public MenuBar3 refreshItemBar()
  {
    if (menuBar == null)
      this.setJMenuBar(menuBar = new MenuBar3(this));
    menuBar.removeAll();
    Dimension dim = new Dimension(30, 30);
    sugarMenu.setOpaque(false);
    sugarMenu.setIcon(new ImageIcon3("sugarcube24.png"));
    sugarMenu.setMinimumSize(dim);
    sugarMenu.setMaximumSize(dim);
    sugarMenu.setPreferredSize(dim);
//    sugarMenu.setForeground(Color3.WHITE);

    menuBar.add(sugarMenu);

    for (Component c : items)
      menuBar.add(c);

    if (this.getTitle() != null && !getTitle().isEmpty())
    {
      this.setSugarTitle(this.getTitle());
      sugarTitle.setForeground(Color.WHITE);
      Font font = sugarTitle.getFont();
      font = font.deriveFont(Font.BOLD);
      font = font.deriveFont(font.getSize2D() + 1f);
      sugarTitle.setFont(font);
      menuBar.add(sugarTitle);
    }

    menuBar.add(Box.createHorizontalGlue());
    menuBar.add(frameButton("Minimize", "frame-minimize.png", "frame-minimize-over.png", "frame-minimize-pressed.png"));
    if (this.isResizable())
      menuBar.add(frameButton("Maximize", "frame-maximize.png", "frame-maximize-over.png", "frame-maximize-pressed.png"));
    menuBar.add(frameButton("Close", "frame-close.png", "frame-close-over.png", "frame-close-pressed.png"));
    return menuBar;
  }

  public void setSugarTitle(String title)
  {
    this.sugarTitle.setText("  " + title + "  ");
  }

  private void setCompassCursor(Compass compass)
  {
    if (this.isResizable())
      switch (compass)
      {
        case NORTH:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
          break;
        case NORTH_EAST:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
          break;
        case EAST:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
          break;
        case SOUTH_EAST:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
          break;
        case SOUTH:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
          break;
        case SOUTH_WEST:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
          break;
        case WEST:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
          break;
        case NORTH_WEST:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
          break;
        case UNDEF:
          this.setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          break;
      }
  }

  private Compass compass(Point p, Rectangle r)
  {
    int d = 20;
    if (Math.abs(p.x - r.getMinX()) + Math.abs(p.y - r.getMinY()) < d)
      return Compass.NORTH_WEST;
    else if (Math.abs(p.x - r.getMaxX()) + Math.abs(p.y - r.getMinY()) < d)
      return Compass.NORTH_EAST;
    else if (Math.abs(p.x - r.getMaxX()) + Math.abs(p.y - r.getMaxY()) < d)
      return Compass.SOUTH_EAST;
    else if (Math.abs(p.x - r.getMinX()) + Math.abs(p.y - r.getMaxY()) < d)
      return Compass.SOUTH_WEST;
    else if (Math.abs(p.x - r.getMinX()) < d)
      return Compass.WEST;
    else if (Math.abs(p.y - r.getMinY()) < d)
      return Compass.NORTH;
    else if (Math.abs(p.x - r.getMaxX()) < d)
      return Compass.EAST;
    else if (Math.abs(p.y - r.getMaxY()) < d)
      return Compass.SOUTH;
    else
      return selectionCompass == null ? Compass.UNDEF : selectionCompass;
  }

  public void fullscreen()
  {
//    final JFrame fullscreenFrame = new JFrame();
//    fullscreenFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//    fullscreenFrame.setUndecorated(true);
//    fullscreenFrame.setResizable(false);
//    fullscreenFrame.add(new JLabel("Press ALT+F4 to exit fullscreen.", SwingConstants.CENTER), BorderLayout.CENTER);
//    fullscreenFrame.validate();
    GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
  }

  public boolean isMuted()
  {
    return mute.isOn();
  }

  public Mute mute()
  {
    return mute;
  }

  public String title()
  {
    return this.getTitle();
  }

  public Panel3 panel()
  {
    return panel;
  }

  public void setPanel(JPanel panel)
  {
    this.getContentPane().remove(this.panel);
    this.getContentPane().add(panel, BorderLayout.CENTER);
  }

  @Override
  public void windowOpened(WindowEvent e)
  {
  }

  @Override
  public void windowClosing(WindowEvent e)
  {
  }

  @Override
  public void windowClosed(WindowEvent e)
  {
    if (this.getDefaultCloseOperation() != JFrame.DO_NOTHING_ON_CLOSE)
      close();
  }

  @Override
  public void windowIconified(WindowEvent e)
  {
  }

  @Override
  public void windowDeiconified(WindowEvent e)
  {
  }

  @Override
  public void windowActivated(WindowEvent e)
  {
  }

  @Override
  public void windowDeactivated(WindowEvent e)
  {
  }

  public final void setDisposeOnClose(boolean enabled)
  {
    this.setDefaultCloseOperation(enabled ? JFrame.DISPOSE_ON_CLOSE : JFrame.DO_NOTHING_ON_CLOSE);
  }

  public final void enableExitOnClose_BeCareful()
  {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public void center()
  {
    this.frameCompass = Compass.CENTER;
    this.display();
  }

  public void setDimension(int width, int height)
  {
    this.setDimension(new Dimension(width, height));
  }

  public void setDimension(Dimension dimension)
  {
    this.dimension = dimension;
  }

  public void setInnerDimension(Dimension dimension)
  {
    this.setDimension(new Dimension(dimension.width + 2 * frameBorderSize, dimension.height + 2 * frameBorderSize + 25));
  }

  public void setPackDimension()
  {
    this.pack();
    this.setInnerDimension(panel.getPreferredSize());
  }

  public int width()
  {
    return this.getWidth();
  }

  public int height()
  {
    return this.getHeight();
  }

  public int x()
  {
    return this.getBounds().x;
  }

  public int y()
  {
    return this.getBounds().y;
  }

  public int minX()
  {
    return this.getBounds().x;
  }

  public int minY()
  {
    return this.getBounds().y;
  }

  public int maxX()
  {
    return (int) this.getBounds().getMaxX();
  }

  public int maxY()
  {
    return (int) this.getBounds().getMaxY();
  }
  
  public Point3 screenXY()
  {
    return new Point3(this.getLocationOnScreen());
  }

  public void setLocation()
  {
    this.setLocation(frameCompass);
  }

  public void setLocation(Compass compass)
  {
    this.frameCompass = compass;
    Rectangle3 screen = Screen.bounds();
    Dimension frame = this.getSize();
    int ox = screen.intX();
    int oy = screen.intY();
    int sw = screen.intWidth();
    int sh = screen.intHeight();
    int fw = frame.width;
    int fh = frame.height;
    int sx = sw / 2;
    int sy = sh / 2;
    int fx = fw / 2;
    int fy = fh / 2;
    switch (compass)
    {
      case CENTER:
        this.setLocation(ox + sx - fx, oy + sy - fy);
        break;
      case NORTH_WEST:
        this.setLocation(ox, oy);
        break;
      case NORTH:
        this.setLocation(ox + sx - fx, oy);
        break;
      case NORTH_EAST:
        this.setLocation(ox + sw - fw, oy);
        break;
      case EAST:
        this.setLocation(ox + sw - fw, oy + sy - fy);
        break;
      case SOUTH_EAST:
        this.setLocation(ox + sw - fw, oy + sh - fh);
        break;
      case SOUTH:
        this.setLocation(ox + sx - fx, oy + sh - fh);
        break;
      case SOUTH_WEST:
        this.setLocation(ox, oy + sh - fh);
        break;
      case WEST:
        this.setLocation(ox, oy + sy - fy);
        break;
    }
  }
  
  
  public Frame3 size(int width, int height)
  {    
    this.setPreferredSize(this.dimension = new Dimension(width, height));
    return this;
  }

  public void display(int x, int y, int w, int h)
  {
    if (w > 0 && h > 0)
    {
      this.size(w,h);
    }
    this.pack();
    if (x < 0 && y < 0)
      this.setLocation();
    else
      this.setLocation(x, y);
    this.invokeSetVisible();
  }

  public void display(int x, int y)
  {
    if (dimension != null)
      this.setPreferredSize(dimension);
    this.pack();
    this.setLocation(x, y);
    this.invokeSetVisible();
  }

  public void display()
  {
    if (dimension != null)
      this.setPreferredSize(dimension);
    this.pack();
    this.setLocation();
    this.invokeSetVisible();
  }

  public void invokeSetVisible()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        setVisible(true);
        mute.setOff();
        frameBounds = getBounds();
      }
    });
  }

  public JComponent setComponent(JComponent c)
  {
    this.panel.add(c, BorderLayout.CENTER);
    return c;
  }

  public void normal()
  {
    this.setExtendedState(NORMAL);
    this.setBounds(frameBounds);
  }

  public void maximize(Compass compass, GraphicsDevice screen)
  {
    if (this.isResizable())
    {
      Rectangle r = (screen == null ? Screen.screen() : screen).getDefaultConfiguration().getBounds();
      this.setExtendedState(NORMAL);
      switch (compass)
      {
        case WEST:
          this.setBounds(r.x, r.y, r.width / 2, r.height);
          break;
        case EAST:
          this.setBounds(r.x + r.width / 2, r.y, r.width / 2, r.height);
          break;
        case NORTH:
          this.maximize();
          break;
        case SOUTH:
          this.setBounds(r.x, r.y + r.height / 2, r.width, r.height / 2);
          break;
      }
      this.frameBounds = this.getBounds();
    }
  }
  
  
  
  @Override
public Rectangle getMaximizedBounds() {
	  return(maxBounds);
}

@Override
public void setMaximizedBounds(Rectangle maxBounds) {
	this.maxBounds = maxBounds;
    super.setMaximizedBounds(maxBounds);
}

@Override
  public void setExtendedState(int state) {
	  if (maxBounds == null &&
	          (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH)
	      {
		  		
	          Insets screenInsets = getToolkit().getScreenInsets(getGraphicsConfiguration());
	          Rectangle screenSizeTest = getGraphicsConfiguration().getBounds();
	          DisplayMode mode = getGraphicsConfiguration().getDevice().getDisplayMode();
	          int width = mode.getWidth();
	          int height = mode.getHeight();
	          Rectangle screenSize = new Rectangle(width, height);
	          if(screenSizeTest.width == width && screenSizeTest.height == height){
//	          if(screenSizeTest.x == 0 && screenSizeTest.y == 0){
	        	 Rectangle maxBounds = new Rectangle(screenInsets.left + screenSize.x, 
	                                      screenInsets.top + screenSize.y, 
	                                      screenSize.x + screenSize.width - screenInsets.right - screenInsets.left,
	                                      screenSize.y + screenSize.height - screenInsets.bottom - screenInsets.top);
	        	 super.setMaximizedBounds(maxBounds);
	          }else{
	        	  Rectangle maxBounds = new Rectangle(screenSize.x, 
                          screenSize.y, 
                          screenSize.x + screenSize.width,
                          screenSize.y + screenSize.height);
	        	  super.setMaximizedBounds(maxBounds);
	          }
	          
	      }

	      super.setExtendedState(state);
}

public void maximize()
  {
    if (this.isResizable())
      if (this.getExtendedState() == MAXIMIZED_BOTH)
      {
        this.setExtendedState(NORMAL);
        this.setBounds(frameBounds);
      }
      else
      {
    	  
        this.frameBounds = getBounds();
        this.setExtendedState(MAXIMIZED_BOTH);
      }
  }

  public void minimize()
  {
    if (this.getExtendedState() == ICONIFIED)
    {
      this.setExtendedState(NORMAL);
      this.setBounds(frameBounds);
    }
    else
      this.setExtendedState(ICONIFIED);
  }

  public void close()
  {
    //never use System.exit(0) since it is a brutal call which does not clean no more used resources    
    this.removeWindowListener(this); //in order to allow this window to completely free up its resources    
    this.setVisible(false);
    this.dispose();
    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    //Zen.LOG.debug(this, ".close - frame closed: " + this.getTitle());
  }

  public void refresh()
  {
  }

  public void exitDialog()
  {
    int value = JOptionPane.showConfirmDialog(
      this,
      "Are you sure you want to quit ?",
      "Exit Dialog",
      JOptionPane.YES_NO_OPTION);

    if (value == JOptionPane.YES_OPTION)
      this.close();
  }

  public static Frame3 basicFrame(String title, int width, int height, Component component)
  {
    Frame3 frame = new Frame3(title, width, height);
    frame.add(component);
    frame.display();
    return frame;
  }

  public static Frame3 textFrame(String title, int width, int height, String data)
  {
    Frame3 frame = new Frame3(title, width, height);
    frame.add(new TextArea3(data).scrollWrap());
    frame.display();
    return frame;
  }

  protected Button3 frameButton(final String toolTipText, String iconName, String iconOverName, String iconPressedName, final ActionListener... listeners)
  {
    final ImageIcon3 icon = new ImageIcon3(iconName);
    final ImageIcon3 over = iconOverName == null || iconOverName.isEmpty() ? icon : new ImageIcon3(iconOverName);
    final ImageIcon3 pressed = iconPressedName == null || iconPressedName.isEmpty() ? icon : new ImageIcon3(iconPressedName);
    int margin = 3;
    final Button3 button = new Button3(icon);
    button.addActionListener(
      new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          button.setFocusPainted(false);
          if (toolTipText.toLowerCase().contains("close"))
            close();
          if (toolTipText.toLowerCase().contains("minimize"))
            minimize();
          if (toolTipText.toLowerCase().contains("maximize"))
            maximize();
          for (ActionListener listener : listeners)
            listener.actionPerformed(e);
        }
      });

    button.setOpaque(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    button.setMargin(margin);
    button.setToolTipText(toolTipText);
    button.setText("");

    button.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mousePressed(MouseEvent e)
      {
        button.setIcon(pressed);
      }

      @Override
      public void mouseEntered(MouseEvent e)
      {
        Cursor3.setDefault(Frame3.this);
        if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK)
          button.setIcon(pressed);
        else
          button.setIcon(over);
      }

      @Override
      public void mouseExited(MouseEvent e)
      {
        button.setIcon(icon);
      }
    });
    return button;
  }
}
