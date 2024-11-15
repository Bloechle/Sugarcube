package sugarcube.insight.core;

import javafx.scene.control.Tab;
import sugarcube.common.data.collections.Str;
import sugarcube.common.ui.fx.containers.FxTab;
import sugarcube.common.ui.fx.containers.FxTabPane;
import sugarcube.insight.interfaces.FxRibbonLoader;

public class FxRibbonTabPane extends FxFinalEnvironment
{

    protected FxTabPane<FxRibbon> tabPane = new FxTabPane<>(true).id("insight-ribbon");
    protected boolean events = true;
    protected String style = null;

    public FxRibbonTabPane(FxEnvironment env)
    {
        super(env);
        style("i-tab");
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, old, val) ->
        {
            if (events)
            {
                FxTab<FxRibbon> tab = tabPane.cast(old);
                if (tab != null)
                {
                    tab.getSource().unselect();
                    tabPane.setOldTab(tab);
                }
                tab = tabPane.cast(val);
                if (tab != null)
                    tab.getSource().select();
            }
        });
    }

    public void addRibbonTab(FxRibbonLoader... loaders)
    {
        for (FxRibbonLoader loader : loaders)
            try
            {
                FxRibbon ribbon = loader.load(env);
                addTab(ribbon, -1);
                ribbon.init();
                ribbon.events = true;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    public void addRibbonTabAndSelect(FxRibbonLoader loader)
    {
        try
        {
            FxRibbon ribbon = loader.load(env);
            Tab tab = addTab(ribbon, -1);
            ribbon.init();
            ribbon.events = true;
            tabPane.getSelectionModel().select(tab);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void closeRibbonTab(FxRibbon ribbon)
    {
        try
        {
            ribbon.events = false;
            ribbon.unselect();
            ribbon.dispose();
            removeTab(ribbon);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int nbOfTabs()
    {
        return tabPane.getTabs().size();
    }


    public FxRibbon[] ribbons()
    {
        FxTab<FxRibbon>[] tabs = tabPane.getFxTabs();
        FxRibbon[] ribbons = new FxRibbon[tabs.length];
        for (int i = 0; i < tabs.length; i++)
            ribbons[i] = tabs[i].getSource();
        return ribbons;
    }

    public FxTab<FxRibbon> getTab(FxRibbon ribbon)
    {
        for (FxTab<FxRibbon> tab : tabPane.getFxTabs())
            if (tab.getSource() == ribbon)
                return tab;
        return null;
    }

    @Override
    public void dispose()
    {
        for (FxRibbon ribbon : ribbons())
            ribbon.dispose();
    }

    public FxRibbonTabPane style(String style)
    {
        if (Str.HasChar(style))
            tabPane.getStyleClass().add(style);
        this.style = style;
        return this;
    }

    public void clear()
    {
        for (FxRibbon ribbon : ribbons())
            removeTab(ribbon);
    }

    public void height(int height)
    {
        tabPane.setHeight(height);
    }

    public void selectOldTab()
    {
        tabPane.selectOldTab();
    }

//    public void selectTab(FxOneRibbon ribbon)
//    {
//        FxTab tab = getTab(ribbon);
//        if(tab!=null)
//            tabPane.getSelectionModel().select(tab);
//    }

    public void selectTab(int index)
    {
        tabPane.getSelectionModel().select(index);
        ribbon().select();
    }

    public FxRibbonTabPane setTab(FxRibbon... tabs)
    {
        clear();
        for (FxRibbon tab : tabs)
            addTab(tab, -1);
        return this;
    }

    public final Tab addTab(FxRibbon ribbon, int index)
    {
        for (Tab t : tabPane.getTabs())
            if (t.getContent() == ribbon.root())
                return t;
        events = false;
        FxTab tab = tabPane.addTab(ribbon, ribbon.name, ribbon.root(), false, index);
        if (Str.HasChar(style))
            tab.getStyleClass().add(style);
        events = true;
        return tab;
    }

    public final FxTab<FxRibbon> removeTab(FxRibbon ribbon)
    {
        return tabPane.removeTab(ribbon.root());
    }

    public FxRibbon selectedRibbon()
    {
        FxTab<FxRibbon> tab = tabPane.getSelectedTab();
        if(tab==null)
            tab = tabPane.selectTab(0);
        return tab == null ? null : tab.getSource();
    }

    public boolean isFirstTabSelected()
    {
        return tabPane.getSelectionModel().getSelectedIndex() == 0;
    }
}
