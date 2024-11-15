package sugarcube.common.ui.fx.containers;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FxScrollPane extends ScrollPane
{

    public FxScrollPane()
    {

    }

    public FxScrollPane(Node content)
    {
        this.setContent(content);
    }

    public FxScrollPane verticalScroll()
    {
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        return this;
    }

    public FxScrollPane fit()
    {
        return fit(true, true);
    }

    public FxScrollPane fit(boolean width, boolean height)
    {
        this.setFitToWidth(width);
        this.setFitToHeight(height);
        return this;
    }

    public void consumeSpace()
    {
        this.addEventFilter(KeyEvent.KEY_PRESSED, ke ->
        {
            if (ke.getCode() == KeyCode.SPACE)
                ke.consume();
        });
    }

    public void ensureVisible(Node node)
    {
        EnsureVisible(this, node);
    }

    public static void EnsureVisible(ScrollPane scroll, Node node)
    {
        double width = scroll.getContent().getBoundsInLocal().getWidth();
        double height = scroll.getContent().getBoundsInLocal().getHeight();

        double x = node.getBoundsInParent().getMaxX();
        double y = node.getBoundsInParent().getMaxY();

        // scrolling values range from 0 to 1
        scroll.setVvalue(y / height);
        scroll.setHvalue(x / width);

        // just for usability
        // node.requestFocus();
    }

}
