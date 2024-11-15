package sugarcube.common.ui.fx.containers;

import javafx.scene.control.Tab;

public class FxTab<T> extends Tab
{
    private T source;

    boolean isDraggable = false;

    public FxTab()
    {

    }

    public T getSource()
    {
        return source;
    }

    public void setSource(T source)
    {
        this.source = source;
    }
}
