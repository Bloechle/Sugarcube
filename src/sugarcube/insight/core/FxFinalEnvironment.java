package sugarcube.insight.core;

import javafx.scene.Node;
import javafx.stage.Stage;
import sugarcube.common.interfaces.Actable;
import sugarcube.common.interfaces.Refreshable;
import sugarcube.common.interfaces.Resetable;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.ui.fx.containers.FxGlassPane;
import sugarcube.insight.interfaces.FxEnvironmentable;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDPage;

public abstract class FxFinalEnvironment implements Unjammable, FxEnvironmentable, Refreshable, Resetable
{
    protected final FxEnvironment env;
    protected boolean isSelected = false;
    protected String name = "";

    public FxFinalEnvironment(FxEnvironment env)
    {
        this.env = env == null && this instanceof FxEnvironment ? (FxEnvironment) this : env;
    }

    public FxFinalEnvironment(FxEnvironment env, String name)
    {
        this.env = env == null && this instanceof FxEnvironment ? (FxEnvironment) this : env;
        this.name = name;
    }

    @Override
    public FxEnvironment env()
    {
        return env;
    }

    public String name()
    {
        return name;
    }

    public Node root()
    {
        return null;
    }

    public void select()
    {
        isSelected = true;
    }

    public void unselect()
    {
        isSelected = false;
    }

    @Override
    public void reset()
    {

    }

    @Override
    public void refresh()
    {

    }

    public void dispose()
    {

    }

    public Stage window()
    {
        return env.window();
    }

    public FxRibbon ribbon()
    {
        return env.gui.ribbonTabPane.selectedRibbon();
    }

    public OCDPage page()
    {
        return env.page;
    }

    public OCDDocument ocd()
    {
        return env.ocd;
    }

    public boolean hasPage()
    {
        return page() != null;
    }

    public boolean isPageNb(int nb)
    {
        return pageNb() == nb;
    }

    public int pageNb()
    {
        OCDPage page = page();
        return page == null ? 0 : page.number();
    }

    public FxBoardEventHandler handler()
    {
        return env.eventHandler;
    }

    public FxGlassPane message(String text, double seconds, Boolean happy)
    {
        return message(text, seconds, happy, null);
    }

    public FxGlassPane message(String text, double seconds, Boolean happy, Actable onHidden)
    {
        return this.env.gui.glassPane.message(text, seconds, happy, onHidden);
    }

    public String toString()
    {
        return name;
    }

}
