package sugarcube.insight.core;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Commands;
import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.io.Desk;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.controls.FxToolBar;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.event.FxInput;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.event.FxScroll;
import sugarcube.common.ui.fx.menus.FxPopup;
import sugarcube.insight.core.popup.OpenPopup;
import sugarcube.insight.core.popup.SavePopup;
import sugarcube.insight.interfaces.FxBoardDriver;
import sugarcube.insight.interfaces.FxBoardHandler;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.FxPager;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.insight.side.InsightSide;
import sugarcube.formats.ocd.objects.OCDPageProcessor;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.pdf.resources.icons.Icon;

public class FxRibbon extends FxFinalEnvironment implements FxBoardDriver, FxBoardHandler, OCDPageProcessor
{
    // FXML tab toolbar pane
    protected final FxToolBar toolbar = new FxToolBar();
    public final List3<InsightSide> rightSidePanes = new List3<>();
    public final FxPopup popup = new FxPopup();
    public final Commands commands = new Commands();
    public FxPager<? extends FxRibbon> pager;
    public boolean events = false;
    public int iconSize = 20;
    public int rightSideSize = 200;
    public boolean hideRibbon = false;
    private TextField[] fields = null;

    public FxRibbon(FxEnvironment env, String name)
    {
        this(env, name, true);
    }

    public FxRibbon(FxEnvironment env, String name, boolean loadFxml)
    {
        super(env, name);
        Region fxmlPane = loadFxml ? (Region) Fx.Fxml(this, false) : null;
        if (fxmlPane instanceof ToolBar)
        {
            ToolBar bar = (ToolBar) fxmlPane;
            Node[] items = bar.getItems().toArray(new Node[0]);
            bar.getItems().clear();
            toolbar.getItems().addAll(items);
        } else if (fxmlPane != null)
            toolbar.getItems().add(fxmlPane);
        toolbar.getStyleClass().add("sc-transparent");
        pager = pager();
    }

    public void init()
    {

    }

    public void init(Button openBt, Button saveBt, Button localBt, Button backBt)
    {
        if (openBt != null)
            OpenPopup.Attach(this, openBt);
        if (saveBt != null)
            SavePopup.Attach(this, saveBt).alto();
        if (localBt != null)
            Icon.FOLDER.set(localBt, iconSize, 100, "Local Folder", Color3.YELLOW_FOLDER, e -> Desk.Open(ocd().fileDirectory()));
        if (backBt != null)
            Icon.UNDO.set(backBt, iconSize, 100, "Reload Page", e -> env.reloadPage());
    }

    public void init(TextField xField, TextField yField, TextField wField, TextField hField)
    {
        this.fields = new TextField[]
                {xField, yField, wField, hField};
        Fx.Listen((obs, old, val) ->
        {
            if (events && pager.hasInteractor())
                pager.interactor.update(env.insight.config.metric.toPx(fieldBox()));
        }, xField, yField, wField, hField);
    }

    public Rectangle3 fieldBox()
    {
        return fields == null ? null : Fx.Rect(fields[0], fields[1], fields[2], fields[3]);
    }

    public void setBoxFields(Rectangle3 box)
    {
        events = false;
        if (fields != null)
            set(fields[0], box.x).set(fields[1], box.y).set(fields[2], box.width).set(fields[3], box.height);
        events = true;
    }

    public void addSidePane(InsightSide... panes)
    {
        for (InsightSide pane : panes)
            rightSidePanes.add(pane);
    }

    @Override
    public void dispose()
    {
        for (InsightSide rightPane : rightSidePanes)
            rightPane.dispose();
    }

    public boolean isTabSelected()
    {
        return ribbon() == this;
    }

    public Rectangle3 selection()
    {
        return pager != null && pager.hasInteractor() ? pager.interactor.bounds() : new Rectangle3();
    }

    public int sideWidth()
    {
        return 400;
    }

    public int ribbonHeight()
    {
        return FxGUI.RIBBON_FULL_HEIGHT;
    }

    public void hideRibbon(boolean doHide)
    {
        hideRibbon = doHide;
        env.gui.headerPane.setHeight(hideRibbon ? FxGUI.RIBBON_MIN_HEIGHT : ribbonHeight());
    }

    @Override
    public void select()
    {
        Log.debug(this, ".select - " + (page() == null ? "null page" : page().entryFilename()));
        for (MediaPlayer player : env().mediaPlayers.values())
            player.stop();
        env.gui.board.setPager(this.pager);
        env.gui.headerPane.setHeight(hideRibbon ? FxGUI.RIBBON_MIN_HEIGHT : ribbonHeight());
        // gui().rightSide.maxWidth(sideWidth());
        if (env.page != null)
            newPage(env.page);
        update();
        env.gui.requestBoardFocus();
        env.gui.setRightSide(rightSideSize, rightSidePanes.toArray(new InsightSide[0]));

    }

    @Override
    public void unselect()
    {
        if (pager != null)
            pager.reset();
    }

    public void newPage(OCDPage page)
    {
    }

    public void paged(OCDPage page)
    {

    }

    public void fileDropped(FileDnD dnd)
    {

    }

    public void copyToClipboard()
    {

    }

    public void pasteFromClipboard()
    {

    }

    public void sideExpanded(Node oldPane, Node newPane, boolean left)
    {

    }

    public void ocdLoaded()
    {

    }

    public void aboutToSaveOCD()
    {

    }

    public void saveImageAs()
    {

    }

    @Override
    public boolean process(OCDPage page)
    {
        return true;
    }

    public void update()
    {
        if (pager != null)
        {
            OCDPage page = env.page;
            if (page != null)
                pager.update(env.page());
            refresh();
            env.gui.updateDom();
        }
    }

    public void reset()
    {

    }

    public void reload()
    {
        pager.env.imageCache.clear();
        refresh();
    }

    @Override
    public void refresh()
    {
        if (pager != null)
            pager.refresh();
        if (fields != null)
            setBoxFields(fieldBox());
    }

    public FxPager<? extends FxRibbon> pager()
    {
        return this.pager == null ? new FxPager<>(this, false) : this.pager;
    }

    public void startInteraction(FxOCDNode node)
    {

    }

    public ObservableList<String> stylesheets()
    {
        return toolbar.getStylesheets();
    }

    @Override
    public Node root()
    {
        return toolbar;
    }

    public boolean doHandleClick(FxOCDNode node)
    {
        return true;
    }

    public FxOCDNode pleaseInteract(OCDPaintable node)
    {
        if (node != null)
            env.updatePage(node.page());
        pager.pleaseInteract(node);
        return null;
    }

    @Override
    public boolean boardKeyEvent(FxKeyboard kb)
    {
        for (FxOCDNode node : pager.focus.selected)
            if (node.boardKeyEvent(kb))
                return true;

        KeyCode code = kb.getCode();
        boolean ctrl = kb.isControlDown();
        boolean alt = kb.isAltDown();

//    Log.debug(this, ".boardKeyEvent - " + kb);

        switch (kb.state())
        {
            case FxKeyboard.UP:
                if (ctrl)
                {
                    switch (code)
                    {
                        case A:
                            pager.interactor.restart(page().bounds());
                            return true;
                        case B:
                            env.bookmarkPage();
                            return true;
                        case C:
                            copyToClipboard();
                            return true;
                        case F:
                            env.gui.showSearchDialog();
                            return true;
                        case Q:
                            env.closeOCD();
                            return true;
                        case S:
                            env.saveOCD(this);
                            return true;
                        case T:
                            env.closeRibbonTab();
                            return true;
                        case V:
                            pasteFromClipboard();
                        default:
                            return false;
                    }
                } else
                {
                    switch (code)
                    {
                        case ESCAPE:
                            pager.interactor.stop();
                            return true;
                        case F1:
                            env.gui.showSearchDialog();
                            return true;
                        case F2:
                            return true;
                        case F11:
                            hideRibbon(!hideRibbon);
                            return true;
                        case F12:
                            Fx.ShowScenicViewIfAvailable(env.insight.scene());
                            return true;
                        default:
                            return false;
                    }
                }

            case FxKeyboard.DOWN:
                if (ctrl)
                {
                    switch (code)
                    {
                        case SPACE:
                            env.gui.nextPage(!alt);
                            return true;
                        case BACK_SPACE:
                        case ENTER:
                            env.loadNextOCD(false, code == KeyCode.ENTER, this, () -> refresh());
                            return true;
                        default:
                            return false;
                    }
                } else
                {
                    switch (code)
                    {
                        case PAGE_UP:
                        case PAGE_DOWN:
                            env.gui.nextPage(code == KeyCode.PAGE_DOWN);
                            return true;
                        default:
                            return false;
                    }
                }
            default:
                return false;
        }
    }

    @Override
    public boolean boardMouseEvent(FxMouse ms)
    {
        // catches all mouse event from board by filtering (not handling)
        // Log.debug(this, ".boardMouseEvent - "+ms);
        env.gui.displayCoords(this, ms);
        for (FxOCDNode node : pager.focus.selected)
            if (node.boardMouseEvent(ms))
                return true;

        return pager.interactor(ms, true);
    }

    @Override
    public boolean boardInputEvent(FxInput in)
    {
        for (FxOCDNode node : pager.focus.selected)
            if (node.boardInputEvent(in))
                return true;

        if (in.isContextMenuEvent())
            return boardPopup(in);

        return false;
    }

    @Override
    public boolean boardScrollEvent(FxScroll sc)
    {
        return false;
    }

    public boolean boardPopup(FxInput in)
    {
        return false;
    }

    // public void refreshObjectPane()
    // {
    // }

    public void interact(FxInteractor interactor)
    {
        this.setBoxFields(interactor.bounds());
    }

    public FxRibbon set(TextField field, double v)
    {
        field.setText("" + env.insight.config.metric.fromPx(v, 2) + env.insight.config.metric.unit);
        return this;
    }

//    public String printStatus(Mouse ms, String msg)
//    {
//        String info = " (" + ms.getX() + "," + ms.getY() + ") ";
//        env.gui.progressLabel.setText(info);
//        return info;
//    }

    public String statusText(FxMouse ms)
    {
        return null;
    }

}
