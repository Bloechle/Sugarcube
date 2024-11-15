package sugarcube.common.ui.fx;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import sugarcube.common.ui.fx.containers.FxAnchorPane;
import sugarcube.common.ui.fx.containers.FxBorderPane;
import sugarcube.common.ui.fx.containers.FxScrollPane;
import sugarcube.insight.core.FxRuler;

import java.awt.*;

public class FxBoard
{
    public FxBorderPane wrapper = new FxBorderPane();
    public FxRuler ruler = new FxRuler();
    public FxScrollPane scroll = new FxScrollPane();
    public FxBorderPane pane = new FxBorderPane();
    public FxAnchorPane canvas = new FxAnchorPane();

    public FxBoard()
    {
        wrapper.setId("board-wrapper");
        wrapper.getStyleClass().add("board-wrapper");
        scroll.setId("board-scroll");
        scroll.getStyleClass().add("board-scroll");
        pane.setId("board-pane");
        pane.getStyleClass().add("board-pane");
        canvas.setId("board-canvas");
        canvas.getStyleClass().add("board-canvas");

        pane.setCenter(canvas);
        scroll.setContent(pane);
        wrapper.setCenter(scroll.fit());
        wrapper.setTop(ruler);

        scroll.consumeSpace();
    }

    public Region root()
    {
        return wrapper;
    }

    public FxBoard whiteBackground()
    {
        this.canvas.setBackgroundColor(Color.WHITE);
        return this;
    }

    public void set(Node... nodes)
    {
        if (nodes == null)
            canvas.clear();
        else
            canvas.set(nodes);
    }

    public void requestFocus()
    {
        canvas.requestFocus();
    }

    public void resize(Dimension dimension)
    {
        canvas.setSize(dimension);
    }

    public void resize(double width, double height)
    {
        canvas.setSize(width, height);
    }

}
