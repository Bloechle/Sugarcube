package sugarcube.insight.render.interaction;

import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import sugarcube.common.ui.fx.shapes.FxCircle;

public class FxInteractorAnchor extends FxCircle
{
    public static final String[] POS =
            {"nw", "n", "ne", "e", "se", "s", "sw", "w"};

    public static final String CLS = "sc-interactor-anchor";
    public final int index;

    public FxInteractorAnchor(int index)
    {
        super(0, 0, 2);
        this.index = index;
        pen(0.5);
        restyle(false, false);
    }

    @Override
    public FxInteractorAnchor fill(Paint p)
    {
        return (FxInteractorAnchor) super.fill(p);
    }

    @Override
    public FxInteractorAnchor stroke(Paint p)
    {
        return (FxInteractorAnchor) super.stroke(p);
    }

    @Override
    public FxInteractorAnchor pen(double size)
    {
        return (FxInteractorAnchor) super.pen(size);
    }

    public FxInteractorAnchor restyle(boolean ix, boolean iy)
    {
        ObservableList<String> classes = this.getStyleClass();
        String posCls = "cursor-resize-" + FxInteractorAnchor.POS[swap(index, ix, iy)];
        return classes.contains(CLS) && classes.contains(posCls) ? this : (FxInteractorAnchor) this.restyle(CLS + " " + posCls);
    }

    public FxInteractorAnchor restyleMove()
    {
        ObservableList<String> classes = this.getStyleClass();
        return classes.contains(CLS) && classes.contains("cursor-move") ? this : (FxInteractorAnchor) this.restyle(CLS + " cursor-move");
    }

    public static int swap(int index, boolean ix, boolean iy)
    {
        if (ix && iy)
        {
            switch (index)
            {
                case 0:
                    return 7;
                case 1:
                    return 5;
                case 2:
                    return 6;
                case 3:
                    return 3;
                case 4:
                    return 0;
                case 5:
                    return 1;
                case 6:
                    return 2;
                case 7:
                    return 3;
            }

        } else if (ix)
        {
            switch (index)
            {
                case 0:
                    return 2;
                case 1:
                    return 1;
                case 2:
                    return 0;
                case 3:
                    return 7;
                case 4:
                    return 6;
                case 5:
                    return 5;
                case 6:
                    return 4;
                case 7:
                    return 3;
            }
        } else if (iy)
        {
            switch (index)
            {
                case 0:
                    return 6;
                case 1:
                    return 5;
                case 2:
                    return 4;
                case 3:
                    return 3;
                case 4:
                    return 2;
                case 5:
                    return 1;
                case 6:
                    return 0;
                case 7:
                    return 7;
            }
        }
        return index;
    }
}