package sugarcube.common.ui.fx.base;

import javafx.scene.layout.Region;

public class FxSpacer extends Region
{
    public FxSpacer(int size)
    {
        this(size, size);
    }

    public FxSpacer(int width, int height)
    {
        setPrefWidth(width);
        setPrefHeight(height);
    }
}
