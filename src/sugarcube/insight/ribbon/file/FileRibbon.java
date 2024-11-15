package sugarcube.insight.ribbon.file;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.FxInterface;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;
import sugarcube.common.ui.fx.containers.FxBorderPane;
import sugarcube.common.ui.fx.containers.FxHBox;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.insight.interfaces.FxRibbonLoader;

public class FileRibbon extends FxRibbon implements FxInterface.Resizable
{
    public static FxRibbonLoader LOADER = env -> new FileRibbon(env);

    public FxPaneLoader backToEditor = env -> new FxEnvironmentPane(env, "⯇ Back to Tabs", "", null);
    private ListView<FxEnvironmentPane> listing;
    private FxBorderPane rootPane;
    private FxBorderPane wrapPane;
    private FxHBox topBox = new FxHBox();
    private Text titleNode;
    private FxEnvironmentPane selected = null;

    public FileRibbon(final FxEnvironment env)
    {
        super(env, "", false);

        this.rightSideSize = 0;

        this.titleNode = new Text();
        titleNode.setX(40.0f);
        titleNode.setY(50.0f);
        titleNode.setCache(true);
        titleNode.setText("");
        titleNode.setFont(Font.font(null, FontWeight.NORMAL, 40));

        DropShadow ds = new DropShadow();
        ds.setColor(Color.rgb(0, 0, 0, 0.9));
        ds.setOffsetX(1);
        ds.setOffsetY(1);
        ds.setRadius(1);
        ds.setSpread(1);

        titleNode.setFill(Color3.WHITE.alpha(1).fx());
        titleNode.setEffect(ds);

        topBox.add(titleNode);
        FxCSS.Style(topBox, "-fx-padding: 0px 0px 30px 0px;");

        this.rootPane = new FxBorderPane();
        this.wrapPane = new FxBorderPane();
        this.rootPane.setStyle("-fx-padding: 0px; -fx-background-color: #333;");

        FxCSS.Style(wrapPane, "sc-background");

        Fx.AddCSS(wrapPane, this, getClass().getSimpleName(), ".css");

        wrapPane.setOnDragDropped(e ->
        {
            if (selected != null)
                selected.onDragDropped(e);
            e.setDropCompleted(true);
            e.consume();
        });
    }

    @Override
    public void init()
    {
        env.addResizeListener(this);
        resizeToPane();
        wrapPane.setStyle("-fx-padding: 5px 5px 5px 6px;");
    }

    @Override
    public FilePager pager()
    {
        return pager == null ? new FilePager(this) : (FilePager) pager;
    }

    @Override
    public void resized(int w, int h)
    {
        if (this.isTabSelected())
        {
            env.gui.ribbonTabPane.height((int) env.insight.getHeight());
            this.resizeToPane();
        }
    }

    public void resizeToPane()
    {
        this.toolbar.setMinWidth((int) env.insight.getWidth());
        this.toolbar.setMinHeight((int) env.insight.getHeight());
    }

    @Override
    public int ribbonHeight()
    {
        return (int) env.insight.getHeight();
    }

    private void retab(FxEnvironmentPane pane)
    {
        Log.debug(this, ".retab - pane=" + pane);
        if (pane != null)
        {
            // pane.root.setStyle("-fx-border-width: 1px; -fx-border-color: red;");
            if (selected != null)
                selected.unselect();
            this.selected = pane;
            if (selected != null)
                selected.select();
            this.wrapPane.setCenter(pane.root);
            this.titleNode.setText(pane.title);
            pane.refresh();
        }
    }

    public List3<FxEnvironmentPane> loadPanes()
    {
        List3<FxEnvironmentPane> panes = new List3<>();
        panes.add(backToEditor.load(env));
        for (FxPaneLoader loader : env.insight.insighters)
        {
            try
            {
                panes.add(loader.load(env));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return panes;
    }

    @Override
    public void select()
    {
        Log.debug(this, ".select");
        super.select();
        listing = new ListView<FxEnvironmentPane>();
        listing.getItems().setAll(loadPanes());
        listing.setStyle("-fx-background-insets: 0; -fx-border-color: #999999;-fx-base: rgba(0, 0, 0, 1)");

        listing.setCellFactory(view -> cell());

        listing.getSelectionModel().selectedItemProperty().addListener((obs, old, val) ->
        {
            if (!listing.getItems().isEmpty())
                if (listing.getSelectionModel().getSelectedIndex() == 0)
                    env.gui.ribbonTabPane.selectOldTab();
                else
                    retab(val);
        });

        listing.setMaxWidth(250);
        listing.setPrefWidth(250);
        listing.setMinWidth(250);

        wrapPane.setTop(topBox);
        rootPane.setLeft(listing);
        rootPane.setCenter(wrapPane);

        if (!isPaneActive())
            env.insight.getChildren().add(rootPane);
        listing.getSelectionModel().select(1);

        this.resizeToPane();
    }

    public ListCell<FxEnvironmentPane> cell()
    {
        return new ListCell<FxEnvironmentPane>()
        {
            @Override
            public void updateItem(FxEnvironmentPane item, boolean empty)
            {
                super.updateItem(item, empty);
                boolean isVoid = item == null || empty;
                FxLabel label = new FxLabel(isVoid ? " " : " " + item.label, "-fx-font-size: 20px;-fx-padding:20px;");
                this.setGraphic(isVoid || item.icon == null ? label : label.graphic(item.icon.node()));
            }
        };
    }

    public void handle(ListView<FxEnvironmentPane> view)
    {

    }

    @Override
    public void unselect()
    {
        super.unselect();
        if (isPaneActive())
            env.insight.getChildren().remove(rootPane);
        listing = null;
        wrapPane.setTop(null);
        rootPane.setLeft(null);
        rootPane.setCenter(null);
        if (selected != null)
        {
            selected.unselect();
            selected = null;
        }
    }

    public boolean isPaneActive()
    {
        for (Node node : env.insight.getChildren())
            if (node == this.rootPane)
                return true;
        return false;
    }
}
