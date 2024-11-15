package sugarcube.common.ui.fx;


import sugarcube.common.ui.fx.base.FxScene;
import sugarcube.common.ui.fx.event.FxScroll;

public class FxInterface
{
  public interface FxLoadable
  {
    void loadFX(FxScene scene);
  }
  
  public interface Scrollable
  {
    void scrolled(FxScroll scroll);
  }
  
  public interface Resizable
  {
    void resized(int width, int height);
  }
}
