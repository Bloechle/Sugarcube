package sugarcube.common.ui.fx.event;


public interface FxEventHandler extends FxKeyHandler, FxMouseHandler, FxInputHandler, FxScrollHandler, FxContextHandler
{
  @Override
  public void keyEvent(FxKeyboard kb);

  @Override
  public void mouseEvent(FxMouse ms);

  @Override
  public void inputEvent(FxInput in);

  @Override
  public void scrollEvent(FxScroll sc);
  
  @Override
  public void contextEvent(FxContext ctx);
}
