package sugarcube.common.ui.fx.event;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class FxKeyboard extends FxInput<KeyEvent>
{
  public static final String UP = "up";
  public static final String DOWN = "down";
  public static final String TYPED = "typed";

  public FxKeyboard(KeyEvent e, String state, Node source)
  {
    super(e, state, source);
  }

  public FxKeyboard(KeyEvent e, String state, Scene source)
  {
    super(e, state, source);
  }

  public boolean isCode(KeyCode code)
  {
    return getCode().equals(code);
  }

  public boolean isUp()
  {
    return UP.equalsIgnoreCase(state);
  }

  public boolean isDown()
  {
    return DOWN.equalsIgnoreCase(state);
  }

  public boolean isTyped()
  {
    return TYPED.equals(state);
  }

  public char getChar()
  {
    String c = event.getCharacter();
    return c.isEmpty() ? 0 : c.charAt(0);
  }

  public int digit()
  {
    switch (getCode())
    {
    case DIGIT0:
      return 0;
    case DIGIT1:
      return 1;
    case DIGIT2:
      return 2;
    case DIGIT3:
      return 3;
    case DIGIT4:
      return 4;
    case DIGIT5:
      return 5;
    case DIGIT6:
      return 6;
    case DIGIT7:
      return 7;
    case DIGIT8:
      return 8;
    case DIGIT9:
      return 9;
    default:
      return -1;
    }
  }

  public int ctrlF()
  {
    if (isControlDown())
      switch (getCode())
      {
      case F1:
        return 1;
      case F2:
        return 2;
      case F3:
        return 3;
      case F4:
        return 4;
      case F5:
        return 5;
      case F6:
        return 6;
      case F7:
        return 7;
      case F8:
        return 8;
      case F9:
        return 9;
      case F10:
        return 10;
      case F11:
        return 11;
      case F12:
        return 12;
      default:
        return 0;
      }
    return -1;
  }

  public KeyCode getCode()
  {
    return event.getCode();
  }

  public boolean isShiftDown()
  {
    return event.isShiftDown();
  }

  public boolean isControlDown()
  {
    return event.isControlDown();
  }

  public boolean isControlDown(boolean withoutAlt)
  {
    return event.isControlDown() && !(withoutAlt && event.isAltDown());
  }

  public boolean isAltDown()
  {
    return event.isAltDown();
  }

  public boolean isMetaDown()
  {
    return event.isMetaDown();
  }

  @Override
  public String toString()
  {
    return event.toString();
  }

  public static KeyCodeCombination ctrl(KeyCode code)
  {
    return new KeyCodeCombination(code, KeyCombination.CONTROL_DOWN);
  }

  public static KeyCodeCombination shift(KeyCode code)
  {
    return new KeyCodeCombination(code, KeyCombination.SHIFT_DOWN);
  }

  public static KeyCodeCombination alt(KeyCode code)
  {
    return new KeyCodeCombination(code, KeyCombination.ALT_DOWN);
  }
}
