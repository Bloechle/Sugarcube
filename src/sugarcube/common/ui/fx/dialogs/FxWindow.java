package sugarcube.common.ui.fx.dialogs;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.*;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.interfaces.OnClose;
import sugarcube.common.interfaces.OnCloseRequest;
import sugarcube.common.interfaces.Refreshable;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.Class3;
import sugarcube.common.ui.fx.base.FxScene;
import sugarcube.common.ui.fx.containers.FxStackPane;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.dnd.FileDroppable;
import sugarcube.common.ui.fx.skin.RS_Skin;

import java.awt.geom.Point2D;

public class FxWindow extends Stage implements Unjammable, OnCloseRequest, OnClose, Refreshable, FileDroppable
{
    protected Window owner;
    protected FxScene scene;
    protected FxStackPane windowPane;
    protected OnCloseRequest onCloseRequest = this;
    protected OnClose onClose = this;

    protected FxWindow()
    {

    }

    public FxWindow(String title)
    {
        this(title, false);
    }

    public FxWindow(String title, Window owner)
    {
        this(title, false, owner);
    }

    public FxWindow(String title, boolean resizable)
    {
        this(title, resizable, null);
    }

    public FxWindow(String title, boolean resizable, Window owner)
    {
        this(title, resizable, owner, true, true);
    }

    public FxWindow(String title, boolean resizable, Window owner, boolean loadFxml)
    {
        this(title, resizable, owner, loadFxml, loadFxml);
    }

    public FxWindow(String title, boolean resizable, Window owner, boolean loadFxml, boolean loadCSS)
    {
        this(title, resizable, owner, -1, -1, null, loadFxml, loadCSS);
    }

    public FxWindow(String title, boolean resizable, Window owner, double sceneWidth, double sceneHeight)
    {
        this(title, resizable, owner, sceneWidth, sceneHeight, null);
    }

    public FxWindow(String title, boolean resizable, Window owner, double sceneWidth, double sceneHeight, Pane rootPane)
    {
        this(title, resizable, owner, sceneWidth, sceneHeight, rootPane, true, true);
    }

    public FxWindow(String title, boolean resizable, Window owner, double sceneWidth, double sceneHeight, Pane rootPane, boolean loadFxml, boolean loadCSS)
    {
        root(rootPane, title, resizable, sceneWidth, sceneHeight, owner, loadFxml, loadCSS);
    }

    public FxScene root(Pane rootPane, String title, boolean resizable, double sceneWidth, double sceneHeight, Window owner, boolean loadFxml, boolean loadCSS)
    {
        this.owner = owner;
        if (rootPane != null && rootPane instanceof FxStackPane)
        {
            windowPane = (FxStackPane) rootPane;
        } else
        {
            windowPane = new FxStackPane();
            if (rootPane != null)
                windowPane.add(rootPane);
        }

        scene = new FxScene(windowPane, sceneWidth, sceneHeight);
        setResizable(resizable);
        title(title);
        try
        {
            if (loadFxml && !getClass().equals(FxWindow.class))
                windowPane.loadFxml(this, loadCSS);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if (owner != null)
            initOwner(owner);
        setScene(scene);
        setOnCloseRequest(e -> onCloseRequest());
        setOnHidden(e -> onClose());
        icon(RS_Skin.class, "sugarcube.png");
        return scene;
    }

    public boolean checkOversize()
    {
        return CheckOversize(this);
    }

    public Stage stage()
    {
        return this;
    }

    public Window owner()
    {
        return owner;
    }

    public FxScene scene()
    {
        return scene;
    }

    public FxWindow set(Node child)
    {
        windowPane.clear();
        windowPane.add(child);
        return this;
    }

    public FxWindow bindSize(Region node)
    {
        node.minWidthProperty().bind(this.widthProperty());
        node.minHeightProperty().bind(this.heightProperty());
        return this;
    }

    public FxWindow title(String title)
    {
        setTitle(title == null ? "Information Dialog" : title);
        return this;
    }

    public FxWindow modal(boolean isModal)
    {
        return modality(isModal ? Modality.APPLICATION_MODAL : Modality.NONE);
    }

    public FxWindow modality(Modality modality)
    {
        // must call before show()
        this.initModality(modality);
        return this;
    }

    public FxWindow modal()
    {
        this.initModality(Modality.APPLICATION_MODAL);
        return this;
    }

    public FxWindow alwaysOnTop()
    {
        this.setAlwaysOnTop(true);
        return this;
    }

    public FxWindow noModality()
    {
        this.initModality(Modality.NONE);
        return this;
    }

    public FxWindow noMaximize()
    {
        this.initStyle(StageStyle.UTILITY);
        return this;
    }

    public FxWindow minSize(double width, double height)
    {
        if (width > 0)
            this.setMinWidth(width);
        if (height > 0)
            this.setMinHeight(height);
        return this;
    }

    public FxWindow position(Point2D p)
    {
        return p == null ? this : position(p.getX(), p.getY());
    }

    public FxWindow position(double x, double y)
    {
        setX(x);
        setY(y);
        return this;
    }

    public FxWindow size(double w, double h)
    {
        this.setWidth(w);
        this.setHeight(h);
        return this;
    }

    public FxWindow resizable(boolean resizable)
    {
        this.setResizable(resizable);
        return this;
    }

    public Point3 position()
    {
        return new Point3(getX(), getY());
    }

    public Point3 dimension()
    {
        return new Point3(getWidth(), getHeight());
    }

    public Rectangle3 bounds()
    {
        return new Rectangle3(getX(), getY(), getWidth(), getHeight());
    }

    public FxWindow icon(Object cls, String name)
    {
        return this.icon(new Image(Class3.Stream(cls, name)));
    }

    public FxWindow icon(Image icon)
    {
        getIcons().setAll(icon);
        return this;
    }

    public FxWindow show(boolean doShow)
    {
        if (doShow)
            show();
        else
            hide();
        return this;
    }

    @Override
    public void refresh()
    {
    }

    @Override
    public void onClose()
    {
        if (onClose != null && onClose != this)
            onClose.onClose();
    }

    @Override
    public void onCloseRequest()
    {
        if (onCloseRequest != null && onCloseRequest != this)
            onCloseRequest.onCloseRequest();
    }

    public FxWindow setOnClose(OnClose onClose)
    {
        this.onClose = onClose == null ? this : onClose;
        return this;
    }

    public FxWindow setOnCloseRequest(OnCloseRequest onCloseRequest)
    {
        this.onCloseRequest = onCloseRequest == null ? this : onCloseRequest;
        return this;
    }

    public FxWindow dnd()
    {
        FileDnD.Handle(scene, this);
        return this;
    }

    @Override
    public void fileDropped(FileDnD dnd)
    {

    }

    public static boolean CheckOversize(Stage stage)
    {
        Rectangle3 winBox = new Rectangle3(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        for (Screen screen : Screen.getScreens())
        {
            Rectangle3 screenBox = new Rectangle3(screen.getBounds());
            if (screenBox.contains(winBox))
                return false;
            if (screenBox.overlapThis(winBox) > 0.5)
            {
                if (winBox.intX() < screenBox.intX())
                    stage.setX(0);
                if (winBox.intY() < screenBox.intY())
                    stage.setY(0);
                if (winBox.intWidth() > screenBox.intWidth())
                    stage.setWidth(screenBox.intWidth());
                if (winBox.intHeight() > screenBox.intHeight())
                    stage.setHeight(screenBox.intHeight());
                return true;
            }
        }
        return false;
    }

    public static FxWindow Get(boolean modal)
    {
        return new FxWindow().modal(modal);
    }

}
