package sugarcube.insight.core;


import sugarcube.common.ui.fx.event.FxAction;
import sugarcube.common.ui.fx.menus.FxIcon;

public class FxRibbonAction<T extends FxRibbon> extends FxAction
{
    public final FxEnvironment env;
    public final T tab;

    public FxRibbonAction(T tab)
    {
        this(tab, "", null);
    }

    public FxRibbonAction(T tab, String name)
    {
        this(tab, name, null);
    }

    public FxRibbonAction(T tab, String name, FxIcon icon)
    {
        super(name, icon);
        this.tab = tab;
        this.env = tab.env;
    }

    public void resetInteractor()
    {
        tab.pager.interactor.reset();
    }

    public void done()
    {
        done(true);
    }

    public void done(boolean update)
    {
        resetInteractor();
        if (update)
            tab.update();
        else
            tab.refresh();
    }

}
