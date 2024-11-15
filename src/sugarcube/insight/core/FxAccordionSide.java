package sugarcube.insight.core;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TitledPane;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.ui.fx.containers.FxAccordion;
import sugarcube.insight.side.InsightSide;

public class FxAccordionSide extends FxFinalEnvironment
{
    public final FxAccordion accordion = new FxAccordion();
    public final StringMap<FxFinalEnvironment> map = new StringMap<>();
    public boolean resizable = true;

    public FxAccordionSide(FxEnvironment env, String id)
    {
        super(env);
        accordion.preventFullCollapse();
        accordion.setId(id);
    }

    public FxAccordionSide(FxEnvironment env, String id, boolean resizable)
    {
        this(env, id);
        this.resizable = resizable;
    }

    public void clear()
    {
        accordion.clear();
        map.clear();
    }

    @Override
    public void refresh()
    {
        for (FxFinalEnvironment tab : map.values())
            tab.refresh();
    }

    public int nbOfTabs()
    {
        return accordion.getPanes().size();
    }

    public FxAccordion pane()
    {
        return accordion;
    }

    public void addExpandedListener(ChangeListener<TitledPane> listener)
    {
        accordion.expandedPaneProperty().addListener(listener);
    }

    public FxAccordionSide setTabs(int size, FxFinalEnvironment... tabs)
    {
        setTabs(tabs);
        width(size);
        return this;
    }

    public FxAccordionSide setTabs(FxFinalEnvironment... tabs)
    {
        clear();
        for (FxFinalEnvironment tab : tabs)
            addTab(tab, -1);
        return this;
    }

    public FxAccordionSide addTab(FxFinalEnvironment... tabs)
    {
        for (FxFinalEnvironment tab : tabs)
            addTab(tab, -1);
        return this;
    }

    public final TitledPane addTab(FxFinalEnvironment tab, int index)
    {
        if (tab == null)
            return null;
        for (TitledPane t : accordion.getPanes())
            if (t.getContent() == tab.root())
                return t;
        TitledPane t = accordion.add(tab.name, tab.root(), true);
        map.put(tab.name, tab);
        t.getContent().setStyle("-fx-padding:0px; -fx-margin:0px;");
        return t;
    }

    // public final Tab removeTab(T tab)
    // {
    //
    // Tab key = this.pane.removeTab(tab.node());
    // this.tabs.remove(key);
    // return key;
    // }

    public FxAccordionSide toggleWidth()
    {
        Log.debug(this, ".toggleWidth - " + width());
        width(width() > 0 ? 0 : 200);
        return this;
    }

    public void width(double width)
    {
        accordion.setVisible(width != 0);
        minWidth(width > 0 ? 200 : 0);
        maxWidth(width > 0 ? Integer.MAX_VALUE : 0);
        prefWidth(width);
    }

    public void minWidth(double width)
    {
        accordion.setMinWidth(width);
    }

    public void maxWidth(double width)
    {
        accordion.setMaxWidth(width);
    }

    public void prefWidth(double width)
    {
        accordion.setPrefWidth(width);
    }

    public double width()
    {
        return accordion.getWidth();
    }

    public boolean isSidePaneExpanded(InsightSide sidePane)
    {
        if (sidePane == null || accordion.getPanes().isEmpty())
            return false;
        TitledPane selected = accordion.getExpandedPane();
        return selected == null ? false : selected.getContent() == sidePane.root();
    }

}
