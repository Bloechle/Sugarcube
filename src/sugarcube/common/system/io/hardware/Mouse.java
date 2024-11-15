package sugarcube.common.system.io.hardware;

import javafx.scene.input.MouseButton;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.interfaces.XYizable;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

public class Mouse extends InputDevice implements XYizable
{
  public static final String UP = "up";
  public static final String DOWN = "down";
  public static final String CLICK = "click";
  public static final String OVER = "over";
  public static final String OUT = "out";
  public static final String WHEEL = "wheel";
  public static final String MOVE = "move";
  public static final String DRAG = "drag";
  public static final String POPUP = "popup";

  public static final int BUTTON_NONE = java.awt.event.MouseEvent.NOBUTTON;
  public static final int BUTTON_LEFT = java.awt.event.MouseEvent.BUTTON1;
  public static final int BUTTON_MIDDLE = java.awt.event.MouseEvent.BUTTON2;
  public static final int BUTTON_RIGHT = java.awt.event.MouseEvent.BUTTON3;
  public static final int BUTTON_LEFT_DOWN = java.awt.event.MouseEvent.BUTTON1_DOWN_MASK;
  public static final int BUTTON_MIDDLE_DOWN = java.awt.event.MouseEvent.BUTTON2_DOWN_MASK;
  public static final int BUTTON_RIGHT_DOWN = java.awt.event.MouseEvent.BUTTON3_DOWN_MASK;
  public float x;
  public float y;
  public float dx;//dragX
  public float dy;//dragY
  public int button;
  public int clicks;
  public boolean isPopup;
  public float wheel;
  public boolean active;
  public Object source;
  public Transform3 otm;
  public String state = "";

  public Mouse()
  {
  }

  public Mouse(float x, float y)
  {
    this.x = x;
    this.y = y;
  }
  
  public Mouse(javafx.scene.input.MouseEvent e, String state, Transform3 otm)
  {
    this.event = e;
    this.state = state;
    this.active = true;
    this.x = (float)e.getX();
    this.y = (float)e.getY();
    
  
    MouseButton mb = e.getButton();
    if(mb==MouseButton.PRIMARY)
    this.button=BUTTON_LEFT;
    else if(mb==MouseButton.SECONDARY)
      this.button = BUTTON_RIGHT;
    else if(mb==MouseButton.MIDDLE)
      this.button = BUTTON_MIDDLE;
    else
      this.button =BUTTON_NONE;
    this.clicks = e.getClickCount();
    
//    this.isPopup = e.isPopupTrigger();
    this.source = e.getSource();
    this.otm = otm;
    
    
    
//    if (e instanceof MouseWheelEvent)
//      this.wheel = (float) ((MouseWheelEvent) e).getWheelRotation(); //e.getPreciseWheelRotation(); //jdk 1.7 !!!
  }

  public Mouse(java.awt.event.MouseEvent e, String state, Transform3 otm)
  {
    super(e);
    this.state = state;
    this.active = true;
    this.x = e.getX();
    this.y = e.getY();
    this.button = e.getButton();
    this.clicks = e.getClickCount();
    this.isPopup = e.isPopupTrigger();
    this.source = e.getComponent();
    this.otm = otm;
    if (e instanceof MouseWheelEvent)
      this.wheel = (float) ((MouseWheelEvent) e).getWheelRotation(); //e.getPreciseWheelRotation(); //jdk 1.7 !!!
  }
  
  public boolean isButtonLeft()
  {
    return this.button == BUTTON_LEFT;
  } 
  
  public boolean isButtonRight()
  {
    return this.button == BUTTON_RIGHT;
  }    
  
  public void offset(Point2D p, boolean add)
  {
    this.x += (add ? p.getX() : -p.getX());
    this.y += (add ? p.getY() : -p.getY());
  }

  public void setDrag(double dragX, double dragY)
  {
    this.dx = (float) dragX;
    this.dy = (float) dragY;
  }

  public Mouse drag(Point2D oldPos)
  {
    this.dx = oldPos == null ? 0 : x - (float) oldPos.getX();
    this.dy = oldPos == null ? 0 : y - (float) oldPos.getY();
    return this;
  }

  public Component getComponent()
  {
    return this.source instanceof Component ? (Component)source : null;
  }
  
  public Object getSource()
  {
    return this.source;
  }  

  public boolean isPopupTrigger()
  {
    return isPopup;
  }

  public boolean isSingleClick()
  {
    return this.clicks == 1;
  }

  public boolean isDoubleClick()
  {
    return this.clicks == 2;
  }

  public boolean isTripleClick()
  {
    return this.clicks == 3;
  }

  public int getClickCount()
  {
    return this.clicks;
  }

  public Point3 getPoint()
  {
    return new Point3(x, y);
  }

  public float x()
  {
    return x;
  }

  public float y()
  {
    return y;
  }

  @Override
  public Point3 xy()
  {
    return new Point3(x, y);
  }

  public void setXY(Point2D p)
  {
    this.x = (float) p.getX();
    this.y = (float) p.getY();
  }

  public float ox()
  {
    return oxy().x;
  }

  public float oy()
  {
    return oxy().y;
  }

  public Point3 oxy()
  {
    return otm == null ? xy() : otm.transform(xy());
  }

  public int getX()
  {
    return Math.round(x);
  }

  public int getY()
  {
    return Math.round(y);
  }

  public float odx()
  {
    return otm == null ? dx : (float) (otm.sx() * dx);
  }

  public float ody()
  {
    return otm == null ? dy : (float) (otm.sy() * dy);
  }

  public int dx()
  {
    return Math.round(dx);
  }

  public int dy()
  {
    return Math.round(dy);
  }

  public int getButton()
  {
    return button;
  }

  public int getWheelRotation()
  {
    return Math.round(this.wheel);
  }
  
  public boolean isUp()
  {
    return UP.equals(state);
  }
  
  public boolean isDown()
  {
    return DOWN.equals(state);
  }
  
  public boolean isClick()
  {
    return CLICK.equals(state);
  }
  
  public boolean isOver()
  {
    return OVER.equals(state);
  }
  
  public boolean isOut()
  {
    return OUT.equals(state);
  }
  
  public boolean isWheel()
  {
    return WHEEL.equals(state);
  }
  
  public boolean isMove()
  {
    return MOVE.equals(state);
  }
  
  public boolean isDrag()
  {
    return DRAG.equals(state);
  }
  
  public boolean isPopup()
  {
    return POPUP.equals(state);
  }
  
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[" + getX() + "," + getY() + "]"
      + "\nOXY[" + ox() + "," + oy() + "]"
      + "\nDrag[" + dx() + "," + dy() + "]"
      + "\nClicks[" + clicks + "]"
      + "\nWheel[" + wheel + "]"
      + "\nCtrl[" + ctrl + "]"
      + "\nPopup[" + isPopup + "]"
      + "\nTransform[" + (otm == null ? "null" : otm) + "]"
      + "\nTimestamp[" + timestamp + "]"
      + "\nState[" + state + "]";
  }
}
