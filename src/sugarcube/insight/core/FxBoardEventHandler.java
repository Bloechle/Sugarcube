package sugarcube.insight.core;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.*;
import sugarcube.common.ui.fx.containers.FxAnchorPane;
import sugarcube.common.ui.fx.containers.FxScrollPane;
import sugarcube.common.ui.fx.event.*;
import sugarcube.insight.interfaces.FxBoardHandler;

public class FxBoardEventHandler extends FxFinalEnvironment implements FxBoardHandler
{
    public FxBoardEventHandler(FxEnvironment env)
    {
        super(env);
    }

    public void addEvents(FxAnchorPane pane)
    {
        pane.setOnContextMenuRequested(context(pane, FxInput.CONTEXT));
        pane.setOnScroll(scroll(pane, FxScroll.SCROLL));
        pane.setOnScrollStarted(scroll(pane, FxScroll.STARTED));
        pane.setOnScrollFinished(scroll(pane, FxScroll.FINISHED));

        // non conventional event handling (!= setHandler)
        // event filter goes from parent to children (younger are filtered first:
        // z-ordering)
        // event handling goes back from children to parent (and may be consumed)
        pane.addEventFilter(MouseEvent.MOUSE_MOVED, mouse(pane, FxMouse.MOVE));
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, mouse(pane, FxMouse.DOWN));
        pane.addEventFilter(MouseEvent.MOUSE_RELEASED, mouse(pane, FxMouse.UP));
        pane.addEventFilter(MouseEvent.MOUSE_CLICKED, mouse(pane, FxMouse.CLICK));
        pane.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, mouse(pane, FxMouse.OVER));
        pane.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, mouse(pane, FxMouse.OUT));
        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouse(pane, FxMouse.DRAG));
    }

    public void addKeyEvents(FxScrollPane scroll)
    {
        // scroll naturally gets focus when clicking on its children pane
        scroll.addEventFilter(KeyEvent.KEY_PRESSED, key(scroll, FxKeyboard.DOWN));
        scroll.addEventFilter(KeyEvent.KEY_RELEASED, key(scroll, FxKeyboard.UP));
        scroll.addEventFilter(KeyEvent.KEY_TYPED, key(scroll, FxKeyboard.TYPED));
    }

    public void addKeyEvents(Scene scene, Node node)
    {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, key(node, FxKeyboard.DOWN));
        scene.addEventFilter(KeyEvent.KEY_RELEASED, key(node, FxKeyboard.UP));
        scene.addEventFilter(KeyEvent.KEY_TYPED, key(node, FxKeyboard.TYPED));
    }

    private EventHandler<ScrollEvent> scroll(final Node source, final String state)
    {
        return e -> boardScrollEvent(new FxScroll(e, state, source));
    }

    public EventHandler<ContextMenuEvent> context(final Node source, final String state)
    {
        return e -> boardInputEvent(new FxContext(e, state, source));
    }

    private EventHandler<InputEvent> input(final Node source, final String state)
    {
        return e -> boardInputEvent(new FxInput(e, state, source));
    }

    private EventHandler<MouseEvent> mouse(final Node source, final String state)
    {
        return e -> boardMouseEvent(new FxMouse(e, state, source));
    }

    private EventHandler<KeyEvent> key(final Node source, final String state)
    {
        return e -> boardKeyEvent(new FxKeyboard(e, state, source));
    }

    @Override
    public boolean boardKeyEvent(FxKeyboard kb)
    {
        if (ribbon().boardKeyEvent(kb))
        {
            //avoids key board scroll use for instance
            kb.event().consume();
            return true;
        }
        return false;
    }

    @Override
    public boolean boardMouseEvent(FxMouse ms)
    {
        if (env.page != null)
            ms.set(env.page.fromView(ms.eventXY()));
        return ribbon().boardMouseEvent(env.gui.board.ms = ms);
    }

    @Override
    public boolean boardInputEvent(FxInput in)
    {
        return ribbon().boardInputEvent(in);
    }

    @Override
    public boolean boardScrollEvent(FxScroll sc)
    {
        if (ribbon().boardScrollEvent(sc))
            return true;
        else if (sc.event().isControlDown())
        {
            env.gui.selectZoomFromScroll(-sc.event().getDeltaY() / 50.0);
            return true;
        }
        return false;
    }

    public static FxBoardEventHandler Get(FxEnvironment env)
    {
        return new FxBoardEventHandler(env);
    }
}
