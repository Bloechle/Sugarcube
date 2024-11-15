package sugarcube.common.ui.fx.containers;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxCSS;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.insight.core.FxRibbon;
import sugarcube.formats.pdf.resources.icons.Icon;

import java.util.Iterator;

public class FxTabPane<T> extends TabPane
{
    private Tab dragTab, oldTab;

    public FxTabPane(boolean isDraggable)
    {
        if (isDraggable)
            addDragAndDropSupport();
    }

    public FxTab<T> cast(Tab tab)
    {
        return tab instanceof FxTab ? (FxTab<T>) tab : null;
    }

    public FxTab<FxRibbon> getSelectedTab()
    {
        Tab tab = getSelectionModel().getSelectedItem();
        return tab instanceof FxTab ? (FxTab<FxRibbon>) tab : null;
    }

    public FxTab<FxRibbon> selectTab(int index)
    {
        getSelectionModel().select(index);
        return getSelectedTab();
    }

    public void setOldTab(Tab oldTab)
    {
        this.oldTab = oldTab;
    }

    public void setHeight(int value)
    {
        setMinHeight(value);
        setPrefHeight(value);
        setMaxHeight(value);
    }

    public FxTab<T>[] getFxTabs()
    {
        List3<FxTab<T>> tabs = new List3<>();
        for (Tab tab : getTabs())
            if (tab instanceof FxTab)
                tabs.add((FxTab<T>) tab);
        return tabs.toArray(new FxTab[0]);
    }

    public FxTabPane id(String id)
    {
        setId(id);
        return this;
    }

    public FxTabPane style(String style)
    {
        FxCSS.Style(this, style);
        return this;
    }

    public void selectOldTab()
    {
        getSelectionModel().select(oldTab);
    }

    public FxTab<T> addTab(T source, String title, Node content, boolean closeable, int index)
    {
        FxTab<T> tab = new FxTab<>();
        tab.setSource(source);
        tab.setClosable(closeable);
        tab.setId(title + " #" + tab.hashCode());

        if (Str.IsVoid(title))
            tab.setGraphic(Icon.Get(Icon.NAVICON, 20));
        else
            tab.setText(title);

        tab.setContent(content);
        if (index < 0)
            getTabs().add(tab);
        else
            getTabs().add(index, tab);
        return tab;
    }

    public FxTab<T> removeTab(Node content)
    {
        Iterator<Tab> it = getTabs().iterator();
        Tab tab;
        while (it.hasNext() && (tab = it.next()) != null)
            if (tab.getContent() == content)
            {
                it.remove();
                return tab instanceof FxTab ? (FxTab<T>) tab : null;
            }
        return null;
    }

    public void addDragAndDropSupport()
    {
        getTabs().forEach(tab -> addDragHandlers(tab));
        getTabs().addListener((ListChangeListener.Change<? extends Tab> change) ->
        {
            while (change.next())
            {
                if (change.wasAdded())
                    change.getAddedSubList().forEach(tab -> addDragHandlers(tab));

                if (change.wasRemoved())
                    change.getRemoved().forEach(tab -> removeDragHandlers(tab));
            }
        });

        // if we drag onto a tab pane (but not onto the tab graphic), add the tab to the end of the list of tabs:
//        setOnDragOver(e ->
//        {
//            String id = e.getDragboard().getString();
//            if (dragTab != null && id != null && id.equals(dragTab.getId()))
//                e.acceptTransferModes(TransferMode.MOVE);
//        });
//        setOnDragDropped(e ->
//        {
//            String id = e.getDragboard().getString();
//            if (dragTab != null && id != null && id.equals(dragTab.getId()))
//            {
//                dragTab.getTabPane().getTabs().remove(dragTab);
//                getTabs().add(dragTab);
//                dragTab.getTabPane().getSelectionModel().select(dragTab);
//            }
//        });
    }

    private void addDragHandlers(Tab tab)
    {

        if (getTabs().indexOf(tab) <= 0)
            return;

        // move text to label graphic
        String text = tab.getText();
        if (text != null && !text.isEmpty())
        {
            tab.setText(null);
            tab.setGraphic(new FxLabel(text, tab.getGraphic()).height(35));
        }

        Node graphic = tab.getGraphic();
        graphic.setOnDragDetected(e ->
        {
            Dragboard dragboard = graphic.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(tab.getId());
            dragboard.setContent(content);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color3.ANTHRACITE.fx());
            dragboard.setDragView(graphic.snapshot(params, null));
            dragTab = tab;
        });
        graphic.setOnDragOver(e ->
        {
            String id = e.getDragboard().getString();
            if (dragTab != null && id != null && id.equals(dragTab.getId()) && dragTab != tab)
                e.acceptTransferModes(TransferMode.MOVE);
        });
        graphic.setOnDragDropped(e ->
        {
            String id = e.getDragboard().getString();
            if (dragTab != null && id != null && id.equals(dragTab.getId()) && dragTab != tab)
            {
                int index = getTabs().indexOf(tab);
                if (index <= 0)
                    return;
                dragTab.getTabPane().getTabs().remove(dragTab);
                tab.getTabPane().getTabs().add(index, dragTab);
                dragTab.getTabPane().getSelectionModel().select(dragTab);
            }
        });
        graphic.setOnDragDone(e -> dragTab = null);
    }

    private void removeDragHandlers(Tab tab)
    {
        tab.getGraphic().setOnDragDetected(null);
        tab.getGraphic().setOnDragOver(null);
        tab.getGraphic().setOnDragDropped(null);
        tab.getGraphic().setOnDragDone(null);
    }


}
