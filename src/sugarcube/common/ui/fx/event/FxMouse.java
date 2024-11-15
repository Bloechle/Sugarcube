package sugarcube.common.ui.fx.event;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sugarcube.common.graphics.geom.Point3;

public class FxMouse extends FxInput<MouseEvent>
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

    private Point3 pos = null;

    public FxMouse(MouseEvent e, String state, Node source)
    {
        super(e, state, source);
    }

    public FxMouse(MouseEvent e, String state, Scene source)
    {
        super(e, state, source);
    }

    public void set(Point3 pos)
    {
        this.pos = pos;
    }

    public boolean isOver()
    {
        return this.isState(OVER);
    }

    public boolean isOut()
    {
        return this.isState(OUT);
    }

    public boolean isOverOrOut()
    {
        return isOver() || isOut();
    }

    public boolean isDrag()
    {
        return this.isState(DRAG);
    }

    public boolean isClick()
    {
        return this.isState(CLICK);
    }

    public boolean isMove()
    {
        return this.isState(MOVE);
    }

    public boolean isDown()
    {
        return this.isState(DOWN);
    }

    public boolean isUp()
    {
        return this.isState(UP);
    }

    public boolean hasShortcut()
    {
        return hasCtrl() || hasAlt() || hasShift();
    }

    public boolean hasCtrlOrShift()
    {
        return hasCtrl() || hasShift();
    }

    public boolean hasCtrl()
    {
        return event.isControlDown();
    }

    public boolean hasAlt()
    {
        return event.isAltDown();
    }

    public boolean hasShift()
    {
        return event.isShiftDown();
    }

    public boolean isPrimaryBt()
    {
        return event.getButton().equals(MouseButton.PRIMARY);
    }

    public boolean isMiddleBt()
    {
        return event.getButton().equals(MouseButton.MIDDLE);
    }

    public boolean isSecondaryBt()
    {
        return event.getButton().equals(MouseButton.SECONDARY);
    }

    public boolean isPrimaryClick()
    {
        return isClicks(1) && isPrimaryBt();
    }

    public boolean isMiddleClick()
    {
        return isClicks(1) && isMiddleBt();
    }

    public boolean isSecondaryClick()
    {
        return isClicks(1) && isSecondaryBt();
    }

    public boolean isClicks(int nb)
    {
        return this.isState(CLICK) && nb == event.getClickCount();
    }

    public boolean isDoubleClick()
    {
        return isClicks(2);
    }

    public boolean isTripleClick()
    {
        return isClicks(3);
    }

    public int clicks()
    {
        return event.getClickCount();
    }

    public float x()
    {
        return (float) (pos == null ? event.getX() : pos.x);
    }

    public float y()
    {
        return (float) (pos == null ? event.getY() : pos.y);
    }

    public int roundX()
    {
        return Math.round(x());
    }

    public int roundY()
    {
        return Math.round(y());
    }

    public int intX()
    {
        return (int) x();
    }

    public int intY()
    {
        return (int) y();
    }

    public Point3 eventXY()
    {
        return new Point3(event.getX(), event.getY());
    }

    @Override
    public Point3 xy()
    {
        return new Point3(x(), y());
    }

    @Override
    public Point3 screenXY()
    {
        return new Point3(event.getScreenX(), event.getScreenY());
    }

    @Override
    public String toString()
    {
        return "FxMouse[" + state + "," + roundX() + "," + roundY() + "]";
    }

}
