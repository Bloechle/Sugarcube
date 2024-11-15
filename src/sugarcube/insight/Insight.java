package sugarcube.insight;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.Prefs;
import sugarcube.common.system.io.Class3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.process.ProcessThread;
import sugarcube.common.ui.fx.FxRootPane;
import sugarcube.common.ui.fx.base.FxScene;
import sugarcube.formats.ocd.writer.Image2OCDFx;
import sugarcube.formats.pdf.reader.DexterFx;
import sugarcube.insight.core.FxConfiguration;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.IS;
import sugarcube.insight.interfaces.FxFileProcessor;
import sugarcube.insight.interfaces.FxPaneLoader;
import sugarcube.insight.interfaces.FxRibbonLoader;
import sugarcube.insight.ribbon.file.*;
import sugarcube.insight.ribbon.insert.InsertRibbon;
import sugarcube.insight.ribbon.reader.ReaderRibbon;
import sugarcube.insight.ribbon.textedit.EditRibbon;
import sugarcube.insight.ribbon.toolbox.ToolboxRibbon;
import sugarcube.insight.ribbon.video.VideoRibbon;

public class Insight extends FxRootPane implements Unjammable
{
    static
    {
        Prefs.Need();
    }

    public final ProcessThread processThread = new ProcessThread(ProcessThread.DEFAULT_SLEEP_MILLIS);
    public final Set3<FxPaneLoader> insighters = new Set3<>(MetadataPane.LOADER, FontPane.LOADER, PDFPane.LOADER, EpubPane.LOADER, ConfigPane.LOADER,LogPane.LOADER);
    public final Set3<FxRibbonLoader> ribbonLoaders = new Set3<>(FileRibbon.LOADER, ReaderRibbon.LOADER, VideoRibbon.LOADER, InsertRibbon.LOADER, EditRibbon.LOADER,
            ToolboxRibbon.LOADER);
    public final Set3<FxFileProcessor> fileProcessors = new Set3<>(DexterFx.PROCESSOR, Image2OCDFx.PROCESSOR);
    public final FxConfiguration config = new FxConfiguration();
    //public String fileToLoad = null;
    protected FxEnvironment env;

    public Insight()
    {
        this("Sugarcube Insight");
    }

    public Insight(String softName)
    {
        super(softName);
        id("insight-root");
        darky(true);
        config.softName = softName;
        minWidth = 600;
        minHeight = 400;
    }

    public void loadFX(FxScene scene)
    {
        env = new FxEnvironment(this);
        env.loadFX(scene);
        loadFX();
        env.load(startFile());
    }


    public void loadFX()
    {

    }

    public FxEnvironment env()
    {
        return env;
    }

    public File3 welcomeFile()
    {
        String filename = "sugarcube.ocd";
        return File3.TempFile(filename, Class3.Stream(IS.class, filename), false);
    }

    public File3 startFile()
    {
        File3 file = app == null ? null : app.args().firstFile(".ocd");
        if (file != null && file.exists())
            return file;
        return File3.FirstExisting(File3.Get(prefs.lastFile(null)), welcomeFile());
    }

    @Override
    public void resized(int width, int height)
    {
        // Log.debug(this, ".resized - " + width + ", " + height);
        // this.setMaxWidth(width);
        // this.setMaxHeight(height);
        // env.gui.root.setMaxWidth(width);
        // env.gui.root.setMaxHeight(height);
    }

    public void onClose()
    {
        processThread.kill();
        super.onClose();
        env.dispose();
    }

    public Insight setRibbonLoaders(FxRibbonLoader... loaders)
    {
        ribbonLoaders.setAll(loaders);
        return this;
    }

    public static void Open(File3 file)
    {
        Open(file.path());
    }

    public static void Open(String file)
    {
        LaunchWithArgs(new String[]
                {file});
    }

    public synchronized static Insight LaunchWithArgs(String[] args, FxRibbonLoader... loaders)
    {
        Insight insight = new Insight();
        if (loaders != null && loaders.length > 0)
            insight.setRibbonLoaders(loaders);
        insight.launchFX();
        return insight;
    }

    public static void main(String... params)
    {
        LaunchWithArgs(params);
    }


}
