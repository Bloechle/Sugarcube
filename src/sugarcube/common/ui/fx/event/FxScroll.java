package sugarcube.common.ui.fx.event;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import sugarcube.common.numerics.Math3;
import sugarcube.common.ui.fx.FxInterface;

public class FxScroll extends FxInput<ScrollEvent>
{

  public static final String SCROLL = "scroll";
  public static final String STARTED = "started";
  public static final String FINISHED = "finished";

  public FxScroll(ScrollEvent e, String state, Node source)
  {
    super(e, state, source);
  }
  
  public FxScroll(ScrollEvent e, String state, Scene source)
  {
    super(e, state, source);
  }
  
  public int sign()
  {
    return Math3.Sign(event.getDeltaY());
  }

  public static void Handle(Node node, FxInterface.Scrollable handler)
  {
    node.setOnScroll(Scroll(node, FxScroll.SCROLL, handler));
    node.setOnScrollStarted(Scroll(node, FxScroll.STARTED, handler));
    node.setOnScrollFinished(Scroll(node, FxScroll.FINISHED, handler));
  }
  
  public static void Handle(Scene node, FxInterface.Scrollable handler)
  {
    node.setOnScroll(Scroll(node, FxScroll.SCROLL, handler));
    node.setOnScrollStarted(Scroll(node, FxScroll.STARTED, handler));
    node.setOnScrollFinished(Scroll(node, FxScroll.FINISHED, handler));
  }

  private static EventHandler<ScrollEvent> Scroll(final Node source, final String state, FxInterface.Scrollable handler)
  {
    return e -> handler.scrolled(new FxScroll(e, state, source));
  }   
  
  private static EventHandler<ScrollEvent> Scroll(final Scene source, final String state, FxInterface.Scrollable handler)
  {
    return e -> handler.scrolled(new FxScroll(e, state, source));
  }   

}
