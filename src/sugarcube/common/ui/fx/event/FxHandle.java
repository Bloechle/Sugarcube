package sugarcube.common.ui.fx.event;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.*;

public class FxHandle implements FxEventHandler
{
  private Scene scene;
  private Node source;
  private Object data;

  // greatly simplifies handling of user events
  public FxHandle(Node source)
  {
    this.source = source;
  }

  public FxHandle(Scene scene)
  {
    this.scene = scene;
  }

  public Node source()
  {
    return source;
  }

  public Object data()
  {
    return data;
  }

  public FxHandle data(Object data)
  {
    this.data = data;
    return this;
  }

  public FxHandle click()
  {
    return click(this);
  }

  public FxHandle click(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMouseClicked(mouse(handler, FxMouse.CLICK));
    else
      source.setOnMouseClicked(mouse(handler, FxMouse.CLICK));
    return this;
  }

  public FxHandle primary()
  {
    return primary(this);
  }

  public FxHandle primary(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMouseClicked(mouse(handler, FxMouse.CLICK, MouseButton.PRIMARY));
    else
      source.setOnMouseClicked(mouse(handler, FxMouse.CLICK, MouseButton.PRIMARY));
    return this;
  }

  public FxHandle secondary()
  {
    return secondary(this);
  }

  public FxHandle secondary(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMouseClicked(mouse(handler, FxMouse.CLICK, MouseButton.SECONDARY));
    else
      source.setOnMouseClicked(mouse(handler, FxMouse.CLICK, MouseButton.SECONDARY));
    return this;
  }

  public FxHandle mouse()
  {
    return mouse(this);
  }

  public FxHandle mouse(FxMouseHandler handler)
  {
    if (source == null)
    {
      scene.setOnMouseMoved(mouse(handler, FxMouse.MOVE));
      scene.setOnMousePressed(mouse(handler, FxMouse.DOWN));
      scene.setOnMouseReleased(mouse(handler, FxMouse.UP));
      scene.setOnMouseClicked(mouse(handler, FxMouse.CLICK));
      scene.setOnMouseEntered(mouse(handler, FxMouse.OVER));
      scene.setOnMouseExited(mouse(handler, FxMouse.OUT));
      scene.setOnMouseDragged(mouse(handler, FxMouse.DRAG));
    } else
    {
      source.setOnMouseMoved(mouse(handler, FxMouse.MOVE));
      source.setOnMousePressed(mouse(handler, FxMouse.DOWN));
      source.setOnMouseReleased(mouse(handler, FxMouse.UP));
      source.setOnMouseClicked(mouse(handler, FxMouse.CLICK));
      source.setOnMouseEntered(mouse(handler, FxMouse.OVER));
      source.setOnMouseExited(mouse(handler, FxMouse.OUT));
      source.setOnMouseDragged(mouse(handler, FxMouse.DRAG));
    }
    return this;
  }

  public FxHandle mouseMoveDrag(FxMouseBoolHandler handler)
  {
    this.mouseMove(ms -> handler.mouseEvent(ms, true));
    this.mouseDrag(ms -> handler.mouseEvent(ms, false));
    return this;
  }

  public FxHandle mouseDownUp(FxMouseBoolHandler handler)
  {
    this.mouseDown(ms -> handler.mouseEvent(ms, true));
    this.mouseUp(ms -> handler.mouseEvent(ms, false));
    return this;
  }

  public FxHandle mouseOverOut(FxMouseBoolHandler handler)
  {
    this.mouseOver(ms -> handler.mouseEvent(ms, true));
    this.mouseOut(ms -> handler.mouseEvent(ms, false));
    return this;
  }

  public FxHandle mouseDown(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMousePressed(mouse(handler, FxMouse.DOWN));
    else
      source.setOnMousePressed(mouse(handler, FxMouse.DOWN));
    return this;
  }

  public FxHandle mouseUp(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMouseReleased(mouse(handler, FxMouse.UP));
    else
      source.setOnMouseReleased(mouse(handler, FxMouse.UP));
    return this;
  }

  public FxHandle mouseOver(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMouseEntered(mouse(handler, FxMouse.OVER));
    else
      source.setOnMouseEntered(mouse(handler, FxMouse.OVER));
    return this;
  }

  public FxHandle mouseOut(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMouseExited(mouse(handler, FxMouse.OUT));
    else
      source.setOnMouseExited(mouse(handler, FxMouse.OUT));
    return this;
  }

  public FxHandle mouseMove(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMouseMoved(mouse(handler, FxMouse.MOVE));
    else
      source.setOnMouseMoved(mouse(handler, FxMouse.MOVE));
    return this;
  }

  public FxHandle mouseDrag(FxMouseHandler handler)
  {
    if (source == null)
      scene.setOnMouseDragged(mouse(handler, FxMouse.DRAG));
    else
      source.setOnMouseDragged(mouse(handler, FxMouse.DRAG));
    return this;
  }

  public FxHandle key()
  {
    return key(this);
  }

  public FxHandle key(FxKeyHandler handler)
  {
    if (source == null)
    {
      scene.setOnKeyPressed(key(handler, FxKeyboard.DOWN));
      scene.setOnKeyReleased(key(handler, FxKeyboard.UP));
      scene.setOnKeyTyped(key(handler, FxKeyboard.TYPED));
    } else
    {
      source.setOnKeyPressed(key(handler, FxKeyboard.DOWN));
      source.setOnKeyReleased(key(handler, FxKeyboard.UP));
      source.setOnKeyTyped(key(handler, FxKeyboard.TYPED));
    }
    return this;
  }

  public FxHandle keyMouse(FxEventHandler handler)
  {
    this.key(handler);
    this.mouse(handler);
    return this;
  }

  public FxHandle scroll()
  {
    return scroll(this);
  }

  public FxHandle scroll(FxScrollHandler handler)
  {
    if (source == null)
    {
      scene.setOnScroll(scroll(handler, FxScroll.SCROLL));
      scene.setOnScrollStarted(scroll(handler, FxScroll.STARTED));
      scene.setOnScrollFinished(scroll(handler, FxScroll.FINISHED));
    } else
    {
      source.setOnScroll(scroll(handler, FxScroll.SCROLL));
      source.setOnScrollStarted(scroll(handler, FxScroll.STARTED));
      source.setOnScrollFinished(scroll(handler, FxScroll.FINISHED));
    }
    return this;
  }

  public FxHandle popup()
  {
    return popup(this);
  }

  public FxHandle popup(FxContextHandler handler)
  {
    if (source == null)
      scene.setOnContextMenuRequested(context(handler, FxInput.CONTEXT));
    else
      source.setOnContextMenuRequested(context(handler, FxInput.CONTEXT));
    return this;
  }

  public FxHandle events()
  {
    return events(this, true);
  }
  
  public FxHandle events(FxEventHandler handler)
  {
    return events(handler, true);
  }

  public FxHandle events(FxEventHandler handler, boolean scrollEvent)
  {
    this.key(handler);
    this.mouse(handler);
    this.popup(handler);
    if (scrollEvent)
      this.scroll(handler);
    return this;
  }

  public EventHandler<ScrollEvent> scroll(final FxScrollHandler handler, final String state)
  {
    return e -> handler.scrollEvent(source == null ? new FxScroll(e, state, scene) : new FxScroll(e, state, source));
  }

  public EventHandler<ContextMenuEvent> context(final FxContextHandler handler, final String state)
  {
    return e -> handler.contextEvent(source == null ? new FxContext(e, state, scene) : new FxContext(e, state, source));
  }

  public EventHandler<InputEvent> input(final FxInputHandler handler, final String state)
  {
    return e -> handler.inputEvent(source == null ? new FxInput<InputEvent>(e, state, scene) : new FxInput<InputEvent>(e, state, source));
  }

  public EventHandler<MouseEvent> mouse(FxMouseHandler handler, String state)
  {
    return e -> handler.mouseEvent(source == null ? new FxMouse(e, state, scene) : new FxMouse(e, state, source));
  }

  public EventHandler<MouseEvent> mouse(FxMouseHandler handler, String state, MouseButton bt)
  {
    return e -> {
      if (e.getButton().equals(bt))
        handler.mouseEvent(source == null ? new FxMouse(e, state, scene) : new FxMouse(e, state, source));
    };
  }

  public EventHandler<KeyEvent> key(FxKeyHandler handler, String state)
  {
    return e -> handler.keyEvent(source == null ? new FxKeyboard(e, state, scene) : new FxKeyboard(e, state, source));
  }

  @Override
  public void keyEvent(FxKeyboard kb)
  {

  }

  @Override
  public void mouseEvent(FxMouse ms)
  {

  }

  @Override
  public void inputEvent(FxInput in)
  {

  }

  @Override
  public void scrollEvent(FxScroll sc)
  {

  }

  @Override
  public void contextEvent(FxContext ctx)
  {

  }

  public static FxHandle Get(Node source)
  {
    return new FxHandle(source);
  }

  public static FxHandle Get(Scene source)
  {
    return new FxHandle(source);
  }
}
