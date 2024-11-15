package sugarcube.common.ui.fx.containers;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;


import java.awt.*;

public class FxBorderPane extends BorderPane
{

    public FxBorderPane()
    {

    }

    public FxBorderPane(Node node)
    {
        this.setCenter(node);
    }

    public void setHeight(int value)
    {
        setMinHeight(value);
        setPrefHeight(value);
        setMaxHeight(value);
    }

    public Parent loadFxml()
    {
        return loadFxml(true);
    }

    public Parent loadFxml(boolean withCSS)
    {
        Parent fxml = Fx.Fxml(this, withCSS);
        if (fxml != null)
            this.setCenter(fxml);
        return fxml;
    }

    public FxBorderPane id(String id)
    {
        this.setId(id);
        return this;
    }

    public FxBorderPane style(String style)
    {
        FxCSS.Style(this, style);
        return this;
    }

    public FxBorderPane idStyle(String idStyle)
    {
        return id(idStyle).style(idStyle);
    }

    public FxBorderPane top(Node node)
    {
        this.setTop(node);
        return this;
    }

    public FxBorderPane bottom(Node node)
    {
        this.setBottom(node);
        return this;
    }

    public FxBorderPane left(Node node)
    {
        this.setLeft(node);
        return this;
    }

    public FxBorderPane right(Node node)
    {
        this.setRight(node);
        return this;
    }

    public FxBorderPane center(Node node)
    {
        this.setCenter(node);
        return this;
    }

    public Node side(boolean right)
    {
        return right ? this.getRight() : this.getLeft();
    }

    public FxBorderPane setSide(boolean right, Node node)
    {
        return right ? right(node) : left(node);
    }

    public Dimension dimension()
    {
        return new Dimension((int) this.getWidth(), (int) this.getHeight());
    }

    public void setScale(double scale)
    {
        this.setScale(scale, scale);
    }

    public void setScale(double sx, double sy)
    {
        this.setScaleX(sx);
        this.setScaleY(sy);
    }

    public void setSize(double w, double h)
    {
        this.setPrefWidth(w);
        this.setPrefHeight(h);
        this.setMinWidth(w);
        this.setMinHeight(h);
        this.setWidth(w);
        this.setHeight(h);
        this.setMaxWidth(w);
        this.setMaxHeight(h);
    }

    public void setSize(Dimension dim)
    {
        this.setSize(dim.getWidth(), dim.getHeight());
    }


    public Scene scene()
    {
        return getScene();
    }

    public Window window()
    {
        return scene().getWindow();
    }


}
