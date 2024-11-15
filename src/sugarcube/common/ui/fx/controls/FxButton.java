package sugarcube.common.ui.fx.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import sugarcube.common.interfaces.Resetable;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;

public class FxButton extends Button implements Resetable
{

    public FxButton()
    {

    }

    public FxButton(String text)
    {
        super(text);
    }

    public FxButton(Node node)
    {
        super("", node);
    }

    public FxButton action(EventHandler<ActionEvent> handler)
    {
        this.setOnAction(handler);
        return this;
    }

    public FxButton fontSize(int value)
    {
        return style("-fx-font-size: " + value + "px;");
    }

    public FxButton textFill(String value)
    {
        return style("-fx-text-fill: "+value+";");
    }

    public FxButton style(String style)
    {
        return (FxButton) FxCSS.Style(this, style);
    }

    public FxButton tip(String tip)
    {
        Fx.Tooltip(tip, this);
        return this;
    }

    public FxButton graphic(Node node)
    {
        this.setGraphic(node);
        return this;
    }

    public FxButton width(double w)
    {
        Fx.width(w, this);
        return this;
    }

    public FxButton height(double h)
    {
        Fx.height(h, this);
        return this;
    }

    public FxButton size(double w, double h)
    {
        return width(w).height(h);
    }

    @Override
    public void reset()
    {

    }
}
