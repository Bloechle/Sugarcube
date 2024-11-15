package sugarcube.insight.ribbon.reader;

import sugarcube.insight.Insight;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxGUI;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.interfaces.FxRibbonLoader;
import sugarcube.insight.ribbon.reader.render.ReaderPager;

public class ReaderRibbon extends FxRibbon
{
    public static FxRibbonLoader LOADER = (env) -> new ReaderRibbon(env);

    public ReaderRibbon(final FxEnvironment env)
    {
        super(env, "OCD Viewer");
    }

    @Override
    public int sideWidth()
    {
        return 0;
    }

    @Override
    public int ribbonHeight()
    {
        return FxGUI.RIBBON_MIN_HEIGHT;
    }

    @Override
    public ReaderPager pager()
    {
        return pager == null ? new ReaderPager(this) : (ReaderPager) pager;
    }

    public static void main(String... args)
    {
        Insight.main();
    }

}
