package sugarcube.insight.core;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.data.table.CSV;
import sugarcube.common.data.table.DataTable;
import sugarcube.common.data.table.ITable;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.interfaces.DoneListener;
import sugarcube.common.interfaces.Progressable;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.process.Progression;
import sugarcube.common.ui.fx.FxInterface;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxImageCache;
import sugarcube.common.ui.fx.dialogs.FxFileChooser;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.dnd.FileDroppable;
import sugarcube.common.data.xml.Xml;
import sugarcube.insight.Insight;
import sugarcube.insight.interfaces.FxFileProcessor;
import sugarcube.insight.interfaces.FxRibbonLoader;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDPageProcessor;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDPage;

public class FxEnvironment extends FxFinalEnvironment implements Progression.Listener, FileDroppable
{
    public final StringMap<MediaPlayer> mediaPlayers = new StringMap<>();
    public final FxImageCache imageCache = new FxImageCache(100, 1000);
    public final Insight insight;
    public final FxGUI gui;
    public final FxBoardEventHandler eventHandler;
    public OCDDocument ocd;
    public OCDPage page;

    public FxEnvironment(Insight insight)
    {
        super(null);
        this.insight = insight;
        eventHandler = new FxBoardEventHandler(this);
        gui = new FxGUI(this, insight);
    }

    public void clearCaches()
    {
        imageCache.clear();
    }

    public void updateLeftSide()
    {
        gui.updateNavDomAndThumbs();
    }

    public void loadFX(Scene scene)
    {
        gui.ribbonTabPane.addRibbonTab(insight.ribbonLoaders.toArray(new FxRibbonLoader[0]));
        eventHandler.addEvents(gui.board.canvas);
        eventHandler.addKeyEvents(scene, insight);
        gui.leftSide.addExpandedListener((obs, old, val) -> sideExpanded(old, val, true));
        gui.rightSide.addExpandedListener((obs, old, val) -> sideExpanded(old, val, false));
        FileDnD.Handle(scene, this);
        Fx.Runs(() -> gui.ribbonTabPane.selectTab(insight.config.openingTabIndex), () -> gui.show());
    }

    public Stage window()
    {
        return insight.window();
    }

    public void addResizeListener(FxInterface.Resizable listener)
    {
        insight.scene().addResizeListener(listener);
    }

    public void updateTitle(String pageInfo)
    {
        String info = (ocd == null ? "" : ocd.fileName() + " / " + Str.Avoid(pageInfo, "").replace(".xml", ""));
        insight.title("    " + insight.config.softName + " - " + (Str.HasChar(info) ? info : "2up"));
    }

    public boolean hasOCD()
    {
        return ocd != null;
    }

    public void updateDB(ITable db)
    {
        // this.mailingTab.updateDB(db);
    }

    public String ocdName()
    {
        return ocd == null ? "" : ocd.zipFile().name();
    }

    @Override
    public OCDDocument ocd()
    {
        return ocd;
    }

    public String filePath()
    {
        return ocd == null ? null : ocd.filePath();
    }

    public Rectangle3 viewbox()
    {
        OCDPage page = page();
        return page == null ? new Rectangle3(0, 0, 595, 842) : page.viewBox();
    }

    public Rectangle3 viewbounds()
    {
        OCDPage page = page();
        return page == null ? new Rectangle3(0, 0, 595, 842) : page.viewBounds();
    }

    public String pageName()
    {
        OCDPage page = page();
        return page == null ? null : page.entryFilename();
    }

    public float ocdDpi()
    {
        return ocd == null ? 72 : ocd.dpi();
    }

    public OCD.ViewProps viewProps()
    {
        return ocd == null ? new OCD.ViewProps() : ocd.viewProps;
    }

    public float scale()
    {
        OCD.ViewProps props = viewProps();
        return props == null ? 1 : props.scale;
    }

    public FxRibbon[] ribbons()
    {
        return gui.ribbonTabPane.ribbons();
    }

    @Override
    public void refresh()
    {
        FxRibbon ribbon = ribbon();
        if (ribbon != null)
            ribbon.refresh();
    }

    public void updatePage()
    {
        updatePage(page);
    }

    public void updatePage(int pageNb)
    {
        updatePage(ocd.pageHandler.getPage(pageNb));
    }

    public void updatePage(String filename)
    {
        updatePage(ocd.pageHandler.getPage(filename, null));
    }

    private void temporizePage()
    {
        if (insight.config.useTmpFile && page != null && page.modified())
        {
            if (page.modified())
                Xml.write(page, page.needTmp());
            for (OCDImage image : page.images())
                if (image.modified())
                    image.write(ocd.temp(image.filename()));
        }
    }

    public void updatePage(OCDPage ocdPage)
    {
        // Log.stacktrace(this, ".updatePage - "+(pg == null ? "null" :
        // pg.entryPath));
        if (ocdPage == null && ocd != null)
            ocdPage = ocd.pageHandler.firstPage();
        if (ocdPage != null && this.page != null && ocdPage != this.page)
        {
            temporizePage();
            page.freeFromMemory(true);
        }
        if (ocdPage == null)
            return;
        else
            ocdPage.ensureInMemory(insight.config.useTmpFile ? ocdPage.needTmp() : null);

        for (MediaPlayer player : env().mediaPlayers.values())
            player.stop();

        page = ocdPage;
        updateTitle(page.entryFilename());
        FxRibbon ribbon = ribbon();
        if (page != null && ribbon != null)
            ribbon.newPage(page);
        gui.refresh();
        if (ribbon != null)
            ribbon.update();
        if (gui.sideDom != null)
            gui.sideDom.update();
    }

    public void insertPage(int pageNb)
    {
        OCDPage prev = ocd.pageHandler.getPage(pageNb);
        String filename = prev == null ? "page-" + pageNb + ".xml" : prev.entryFilename();
        String post = "";
        while (ocd.pageHandler.has(filename))
        {
            post = post + "i";
            filename = "page-" + pageNb + post + ".xml";
        }
        OCDPage newPage = ocd.addPage(filename, pageNb, prev == null ? 600 : (int) prev.width, prev == null ? 800 : (int) prev.height);
        updatePage();
        updateLeftSide();
    }

    public void importFile()
    {
        File3 file = new FxFileChooser("Import a File").open(insight.window());
        if (file != null && file.isExtension(".pdf", ".ocd"))
        {
            OCDDocument doc = OCD.Load(file);
            for (OCDPage page : doc)
                page.inject(page, true, false);
            doc.close();
            updatePage();
        }
    }

    public void deletePage(String... filenames)
    {
        updatePage(ocd.pageHandler.deletePage(filenames) == page ? null : page);
        updateLeftSide();
    }

    public void deleteAllPagesExcept(String filename)
    {
        StringSet set = new StringSet();
        for (String name : ocd.pageHandler.map().keySet())
            if (!name.contains(filename))
                set.add(name);
        deletePage(set.array());
    }

    public void closeRibbonTab()
    {
        FxRibbon ribbon = ribbon();
        Log.info(this, ".closeTab - " + ribbon.name);
        if (ribbon != null && gui.ribbonTabPane.nbOfTabs() > 1 && !gui.ribbonTabPane.isFirstTabSelected())
        {
            gui.ribbonTabPane.closeRibbonTab(ribbon);
        }
    }

    public void closeOCD()
    {
        closeOCD(true);
    }

    public void closeOCD(boolean loadWelcome)
    {
        if (ocd != null)
            ocd.close();
        if (loadWelcome)
            load(insight.welcomeFile());
    }

    public void saveOCD(OCDPageProcessor processor)
    {
        saveOCD(ocd.file(), processor, null);
    }

    public void saveAsOCD(OCDPageProcessor processor)
    {
        File3 file = Fx.Chooser("Save As...").dir(env.ocd.filePath()).name(env.ocd.fileName()).ocdExtensionFilter().save(insight.window());
        if (file != null)
            saveOCD(file, processor, null);
    }

    public void saveOCD(File3 file, OCDPageProcessor processor, DoneListener listener)
    {
        if (file == null)
            file = ocd.file();
        if (!ocd.isSaving)
        {
            ocd.isSaving = true;
            ribbon().aboutToSaveOCD();
            if (listener == null)
                listener = () ->
                {
                    clearCaches();
                    updateLeftSide();
                    updatePage();
                };
            new SaveOCDFx(this, file, processor, listener);
        } else
        {
            Log.debug(this, ".saveOCD - already saving file: " + file.path());
        }
    }

    public void loadNextOCD(boolean autoSave, boolean forward, OCDPageProcessor processor, DoneListener listener)
    {
        Log.debug(this, "loadNextOCD - autoSave=" + autoSave + ", forward=" + forward + ", listener=" + (listener != null));
        if (autoSave && ocd.hasPageModified())
            saveOCD(null, processor, () -> load(ocd.file().next(forward, true), 1, listener));
        else
        {
            load(ocd.file().next(forward, true), 1, listener);
        }
    }

    public void chooseFile()
    {
        load(Fx.Chooser("Choose a File").dir(env.ocd.filePath()).open(insight.window()));
    }

    @Override
    public void fileDropped(FileDnD dnd)
    {
        Log.debug(this, ".fileDropped - " + ribbon().name);
        ribbon().fileDropped(dnd);
        if (!dnd.isConsumed())
            load(dnd.data());
    }

    public void sideExpanded(TitledPane old, TitledPane val, boolean left)
    {
        Node oldPane = old == null ? null : old.getContent();
        Node newPane = val == null ? null : val.getContent();
        if (newPane != null)
            ribbon().sideExpanded(oldPane, newPane, left);
    }

    public ITable updateDB(File3 file)
    {
        if (file != null && file.isExtension(".csv"))
            try
            {
                DataTable table = CSV.Read(file);
                updateDB(table);
                return table;
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        return null;
    }

//  @Override
//  public boolean process(OCDPage page)
//  {
//    for (RibbonTab tab : this.tabs())
//      tab.process(page);
//    return true;
//  }

    // public void loadWelcome()
    // {
    // this.load(app.welcomeFile());
    // }

    public void loadWelcomeOCD()
    {
        load(insight.welcomeFile());
    }

    public void load(File3... file)
    {
        load(file, 1, null);
    }

    public void load(File3 file, int pageNb, DoneListener listener)
    {
        load(new File3[]{file}, pageNb, listener);
    }

    public void load(File3[] files, int pageNb, DoneListener listener)
    {
        if (files == null || files.length == 0 || files[0] == null)
            return;

        File3 file = files[0];
        Log.debug(this, ".load - " + (files == null ? "null" : file.path()) + ", nb=" + files.length);
        if (!File3.Exists(file))
        {
            Log.debug(this, ".load - file not found: " + (files == null ? "null" : file.path()));
            return;
        }
        insight.config.directory = file.directory();
        if (file.isExt("ocd"))
        {
            closeOCD(false);
            new FxDocumentLoader(this, file, pageNb, listener).go();
        } else if (file.isExt("csv"))
            updateDB(file);
         else
        {
            for (FxFileProcessor updater : insight.fileProcessors)
            {
                if (updater.process(this, file, files))
                    break;
            }
        }
    }


    public void loadPage(int pageNb)
    {
        loadPage(pageNb, null);
    }

    public void loadPage(int pageNb, DoneListener listener)
    {
        loadPage(null, pageNb, listener);
    }

    public void loadPage(File3 ocdFile, int pageNb, DoneListener listener)
    {

        if (ocdFile == null && pageNb > 0 && ocd != null)
            ocdFile = this.env.ocd.file();

        if (!OCD.Exists(ocdFile))
        {
            Log.debug(this, ".loadPage - ocd file not found: " + (ocd == null ? "null" : ocdFile.path()));
        } else if (ocd == null || !ocd.filePath().equals(ocdFile.path()))
        {
            Log.debug(this, ".loadPage - load new file: " + ocdFile.path());
            load(ocdFile, pageNb, listener);
        } else if (pageNb > 0 && pageNb != this.page.number())
        {
            Log.debug(this, ".loadPage - updatePage " + pageNb);
            updatePage(pageNb);
            if (listener != null)
                listener.done();
        } else if (listener != null)
            listener.done();
    }

    public void bookmarkPage()
    {
        ocd.nav().bookmarks().add(page.entryFilename(), page.entryFilename());
        gui.sideNav.update();
    }

    public void reloadPage()
    {
        Log.debug(this, ".reloadPage");
        if (page != null)
        {
            page.freeFromMemory(true);
            page.ensureInMemory(page.tmp);
            if (page.tmp.exists())
                page.tmp.delete();
        }
        ribbon().update();
    }

    @Override
    public void progress(final Progressable progression)
    {
        Fx.Run(() -> gui.progress(progression));
    }

    @Override
    public void dispose()
    {
        Log.debug(this, ".dispose");
        gui.dispose();
        temporizePage();
        if (ocd != null)
            ocd.close();
    }

}
