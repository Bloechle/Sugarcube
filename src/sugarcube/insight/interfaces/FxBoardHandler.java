package sugarcube.insight.interfaces;


import sugarcube.common.ui.fx.event.FxInput;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.event.FxScroll;

public interface FxBoardHandler
{
    boolean boardKeyEvent(FxKeyboard kb);

    boolean boardMouseEvent(FxMouse ms);

    boolean boardInputEvent(FxInput in);

    boolean boardScrollEvent(FxScroll sc);
}
