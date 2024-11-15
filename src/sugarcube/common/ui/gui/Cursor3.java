package sugarcube.common.ui.gui;

import javafx.scene.Node;

import java.awt.*;

public class Cursor3 extends Cursor
{
  private static final String[] NAMES =
  { "default", "crosshair", "text", "wait", "sw-resize", "se-resize", "nw-resize", "ne-resize", "n-resize", "s-resize", "w-resize", "e-resize",
      "hand", "move" };
  public static final int CUSTOM = CUSTOM_CURSOR;
  public static final int DEFAULT = DEFAULT_CURSOR;
  public static final int CROSS = CROSSHAIR_CURSOR;
  public static final int TEXT = TEXT_CURSOR;
  public static final int WAIT = WAIT_CURSOR;
  public static final int RESIZE_SW = SW_RESIZE_CURSOR;
  public static final int RESIZE_SE = SE_RESIZE_CURSOR;
  public static final int RESIZE_NW = NW_RESIZE_CURSOR;
  public static final int RESIZE_NE = NE_RESIZE_CURSOR;
  public static final int RESIZE_N = N_RESIZE_CURSOR;
  public static final int RESIZE_S = S_RESIZE_CURSOR;
  public static final int RESIZE_W = W_RESIZE_CURSOR;
  public static final int RESIZE_E = E_RESIZE_CURSOR;
  public static final int HAND = HAND_CURSOR;
  public static final int MOVE = MOVE_CURSOR;

  public Cursor3(int type)
  {
    super(type);
  }

  public static int type(String name)
  {
    for (int i = 0; i < NAMES.length; i++)
      if (name.equalsIgnoreCase(NAMES[i]))
        return i;
    return CUSTOM;
  }

  public static String name(int type)
  {
    return type > -1 && type < NAMES.length ? NAMES[type] : "custom";
  }

  public int type()
  {
    return this.getType();
  }

  public static Cursor3 get(int type)
  {
    return new Cursor3(type);
  }

  public static void set(Component c, int type)
  {
    c.setCursor(get(type));
  }

  public static void set(Node c, int type)
  {
    switch (type)
    {
    case CUSTOM:
      c.setCursor(javafx.scene.Cursor.DEFAULT);
      break;
    case DEFAULT:
      c.setCursor(javafx.scene.Cursor.DEFAULT);
      break;
    case CROSS:
      c.setCursor(javafx.scene.Cursor.CROSSHAIR);
      break;
    case TEXT:
      c.setCursor(javafx.scene.Cursor.TEXT);
      break;
    case WAIT:
      c.setCursor(javafx.scene.Cursor.WAIT);
      break;
    case HAND:
      c.setCursor(javafx.scene.Cursor.HAND);
      break;
    case MOVE:
      c.setCursor(javafx.scene.Cursor.MOVE);
      break;
    case RESIZE_SW:
      c.setCursor(javafx.scene.Cursor.SW_RESIZE);
      break;
    case RESIZE_SE:
      c.setCursor(javafx.scene.Cursor.SE_RESIZE);
      break;
    case RESIZE_NW:
      c.setCursor(javafx.scene.Cursor.NW_RESIZE);
      break;
    case RESIZE_NE:
      c.setCursor(javafx.scene.Cursor.NE_RESIZE);
      break;
    case RESIZE_N:
      c.setCursor(javafx.scene.Cursor.N_RESIZE);
      break;
    case RESIZE_S:
      c.setCursor(javafx.scene.Cursor.S_RESIZE);
      break;
    case RESIZE_W:
      c.setCursor(javafx.scene.Cursor.W_RESIZE);
      break;
    case RESIZE_E:
      c.setCursor(javafx.scene.Cursor.E_RESIZE);
      break;
    default:
      c.setCursor(javafx.scene.Cursor.DEFAULT);
      break;
    }
  }

  public static void setDefault(Component c)
  {
    set(c, DEFAULT);
  }

  public static void setCross(Component c)
  {
    set(c, CROSS);
  }

  public static void setText(Component c)
  {
    set(c, TEXT);
  }

  public static void setWait(Component c)
  {
    set(c, WAIT);
  }

  public static void setResizeSW(Component c)
  {
    set(c, RESIZE_SW);
  }

  public static void setResizeSE(Component c)
  {
    set(c, RESIZE_SE);
  }

  public static void setResizeNW(Component c)
  {
    set(c, RESIZE_NW);
  }

  public static void setResizeNE(Component c)
  {
    set(c, RESIZE_NE);
  }

  public static void setResizeN(Component c)
  {
    set(c, RESIZE_N);
  }

  public static void setResizeS(Component c)
  {
    set(c, RESIZE_S);
  }

  public static void setResizeW(Component c)
  {
    set(c, RESIZE_W);
  }

  public static void setResizeE(Component c)
  {
    set(c, RESIZE_E);
  }

  public static void setHand(Component c)
  {
    set(c, HAND);
  }

  public static void setMove(Component c)
  {
    set(c, MOVE);
  }
  
  public static void setDefault(Node c)
  {
    set(c, DEFAULT);
  }

  public static void setCross(Node c)
  {
    set(c, CROSS);
  }

  public static void setText(Node c)
  {
    set(c, TEXT);
  }

  public static void setWait(Node c)
  {
    set(c, WAIT);
  }

  public static void setResizeSW(Node c)
  {
    set(c, RESIZE_SW);
  }

  public static void setResizeSE(Node c)
  {
    set(c, RESIZE_SE);
  }

  public static void setResizeNW(Node c)
  {
    set(c, RESIZE_NW);
  }

  public static void setResizeNE(Node c)
  {
    set(c, RESIZE_NE);
  }

  public static void setResizeN(Node c)
  {
    set(c, RESIZE_N);
  }

  public static void setResizeS(Node c)
  {
    set(c, RESIZE_S);
  }

  public static void setResizeW(Node c)
  {
    set(c, RESIZE_W);
  }

  public static void setResizeE(Node c)
  {
    set(c, RESIZE_E);
  }

  public static void setHand(Node c)
  {
    set(c, HAND);
  }

  public static void setMove(Node c)
  {
    set(c, MOVE);
  }  

  public static Cursor3 standard()
  {
    return new Cursor3(DEFAULT);
  }

  public static Cursor3 cross()
  {
    return new Cursor3(CROSS);
  }

  public static Cursor3 text()
  {
    return new Cursor3(TEXT);
  }

  public static Cursor3 busy()
  {
    return new Cursor3(WAIT);
  }

  public static Cursor3 resizeSW()
  {
    return new Cursor3(RESIZE_SW);
  }

  public static Cursor3 resizeSE()
  {
    return new Cursor3(RESIZE_SE);
  }

  public static Cursor3 resizeNW()
  {
    return new Cursor3(RESIZE_NW);
  }

  public static Cursor3 resizeNE()
  {
    return new Cursor3(RESIZE_NE);
  }

  public static Cursor3 resizeN()
  {
    return new Cursor3(RESIZE_N);
  }

  public static Cursor3 resizeS()
  {
    return new Cursor3(RESIZE_S);
  }

  public static Cursor3 resizeW()
  {
    return new Cursor3(RESIZE_W);
  }

  public static Cursor3 resizeE()
  {
    return new Cursor3(RESIZE_E);
  }

  public static Cursor3 hand()
  {
    return new Cursor3(HAND);
  }

  public static Cursor3 move()
  {
    return new Cursor3(MOVE);
  }

  public static boolean is(int type, Component c)
  {
    return c.getCursor().getType() == type;
  }

  public static boolean is(int type, Node node)
  {
    javafx.scene.Cursor c = node.getCursor();
    if (c.equals(javafx.scene.Cursor.CROSSHAIR))
      return type == CROSS;
    else if (c.equals(javafx.scene.Cursor.TEXT))
      return type == TEXT;
    else if (c.equals(javafx.scene.Cursor.HAND))
      return type == HAND;
    else if (c.equals(javafx.scene.Cursor.MOVE))
      return type == MOVE;
    else if (c.equals(javafx.scene.Cursor.WAIT))
      return type == WAIT;
    else if (c.equals(javafx.scene.Cursor.E_RESIZE))
      return type == RESIZE_E;
    else if (c.equals(javafx.scene.Cursor.S_RESIZE))
      return type == RESIZE_S;
    else if (c.equals(javafx.scene.Cursor.W_RESIZE))
      return type == RESIZE_W;
    else if (c.equals(javafx.scene.Cursor.N_RESIZE))
      return type == RESIZE_N;
    else if (c.equals(javafx.scene.Cursor.NE_RESIZE))
      return type == RESIZE_NE;
    else if (c.equals(javafx.scene.Cursor.NW_RESIZE))
      return type == RESIZE_NW;
    else if (c.equals(javafx.scene.Cursor.SE_RESIZE))
      return type == RESIZE_SE;
    else if (c.equals(javafx.scene.Cursor.SW_RESIZE))
      return type == RESIZE_SW;
    else if (c.equals(javafx.scene.Cursor.DEFAULT))
      return type == DEFAULT;
    return false;
  }

  public boolean is(int type)
  {
    return this.getType() == type;
  }

  @Override
  public boolean equals(Object that)
  {
    if (that == null || !(that instanceof Cursor))
      return false;
    return this.type() == ((Cursor) that).getType();
  }

  @Override
  public int hashCode()
  {
    return type();
  }
}
