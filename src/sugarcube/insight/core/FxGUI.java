package sugarcube.insight.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import sugarcube.common.system.Prefs;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Metric;
import sugarcube.common.interfaces.Progressable;
import sugarcube.common.system.process.Progression;
import sugarcube.common.numerics.Math3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxCSS;
import sugarcube.common.ui.fx.base.FxSpacer;
import sugarcube.common.ui.fx.containers.*;
import sugarcube.common.ui.fx.controls.FxButton;
import sugarcube.common.ui.fx.controls.FxComboBox;
import sugarcube.common.ui.fx.controls.FxLabel;
import sugarcube.common.ui.fx.controls.FxProgressBar;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.data.xml.Nb;
import sugarcube.insight.Insight;
import sugarcube.insight.ribbon.actions.search.SearchDialog;
import sugarcube.insight.ribbon.actions.settings.SettingsDialog;
import sugarcube.insight.side.dom.DomSide;
import sugarcube.insight.side.pages.ThumbSide;
import sugarcube.insight.side.toc.NavSide;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.pdf.resources.icons.Icon;

import java.util.Scanner;

public class FxGUI extends FxFinalEnvironment
{
    public static final int RIBBON_FULL_HEIGHT = 155;
    public static final int RIBBON_MIN_HEIGHT = 35;

    public final FxBorderPane pane = new FxBorderPane().id("insight-gui");
    public final FxSplitPane splitPane = new FxSplitPane().id("insight-body");
    public final FxPagerBoard board;
    public final FxAccordionSide leftSide, rightSide;
    public final FxBorderPane headerPane, footerPane;
    public final FxHBox footerLeft, footerRight;
    public final FxRibbonTabPane ribbonTabPane;
    public final DomSide sideDom;
    public final NavSide sideNav;
    public final ThumbSide sideThumb;
    public final FxButton leftSideBt, rightSideBt, searchBt, displayBt;
    public final FxLabel coordsLabel, progressLabel;
    public final FxProgressBar progress;
    public final FxGlassPane glassPane;

    public final FxComboBox<String> zoom = new FxComboBox<String>().tip("Page Zoom (Ctrl+Wheel)");
    // public FxTextField number = new FxTextField();
    public final FxButton prevBt = new FxButton(Icon.CHEVRON_LEFT.get(12)).tip("Previous Page (Ctrl+Tab)");
    public final FxButton nextBt = new FxButton(Icon.CHEVRON_RIGHT.get(12)).tip("Next Page (Ctrl+Space)");
    public SearchDialog searchDialog = null;
    public SettingsDialog displayDialog = null;

    public FxDisplayProps display = new FxDisplayProps();

    public FxGUI(FxEnvironment env, Insight insight)
    {
        super(env);
        hide();

        FxConfiguration config = env.insight.config;

        board = new FxPagerBoard(env);
        sideThumb = new ThumbSide(env);
        sideNav = config.showSideNav ? new NavSide(env) : null;
        leftSide = new FxAccordionSide(env, "insight-left-side");
        rightSide = new FxAccordionSide(env, "insight-right-side", false);

        footerPane = new FxBorderPane().id("insight-footer");

        // this.side.node().setId("ribbon-side");
        String style = "-fx-padding: 0px 2px 0px 2px";
        footerLeft = new FxHBox(2).style(style).align(Pos.CENTER_LEFT);
        footerRight = new FxHBox(2).style(style).align(Pos.CENTER_RIGHT);

        ribbonTabPane = new FxRibbonTabPane(env);

        // this.board.setStyle("-fx-border-color: #008C96; -fx-border-width: 1px;");
        sideDom = config.showSideDom ? new DomSide(env) : null;
        leftSide.setTabs(sideDom, sideNav, sideThumb);
        // rightSide.setTab(sideProps);

        headerPane = new FxBorderPane(ribbonTabPane.tabPane).id("insight-header").style("sc-background");

        if (env.insight.config.showRightSide)
            splitPane.getItems().addAll(leftSide.accordion, board.root(), rightSide.accordion);
        else
            splitPane.getItems().addAll(leftSide.accordion, board.root());

        leftSideBt = IS.FooterBt("◐", e -> leftSide.toggleWidth());
        rightSideBt = config.showRightSide ? IS.FooterBt(Icon.BARS, e -> rightSide.toggleWidth()) : null;
        searchBt = config.showSearchBt ? IS.FooterBt(Icon.SEARCH, e -> showSearchDialog()).tip("Search Dialog (F1 or Ctrl+F)") : null;
        displayBt = config.showConfigBt ? IS.FooterBt(Icon.GEAR, e -> showDisplayDialog()).tip("Display Dialog (F3)") : null;
        progressLabel = new FxLabel().fill(Color3.BLACK).size(100, IS.FOOTER_HEIGHT);
        coordsLabel = new FxLabel().alignRight().fill(Color3.BLACK);
        progress = new FxProgressBar().size(200, IS.FOOTER_HEIGHT).show();
        progress.setStyle("-fx-accent: " + IS.GUI_BLUE);
        progress.handleMouse(e -> updatePage(progress.progress()));

        footerPane.setLeft(footerLeft);
        footerPane.setRight(footerRight);

        pane.setTop(headerPane);
        pane.setCenter(splitPane);
        pane.setBottom(footerPane);

        glassPane = new FxGlassPane(env.insight);

        FxCSS.Styles("gui-ft-control", zoom, prevBt, progress, nextBt);
        FxCSS.Styles("gui-ft-zoom", zoom);

        zoom.style("-fx-font-size: 13px; -fx-border-width: 0px;");

        String[] zooms = new String[IS.ZOOMS.length];
        int percent = 0;
        for (int i = 0; i < zooms.length; i++)
        {
            if (IS.ZOOMS[i] == 100)
                percent = i;
            zooms[i] = IS.ZOOMS[i] + "%";
        }

        ObservableList<String> options = FXCollections.observableArrayList(zooms);
        zoom.setItems(options);
        zoom.getSelectionModel().select(percent);
        zoom.setPrefWidth(80);

        Fx.size(80, IS.FOOTER_HEIGHT, zoom);

        style = "-fx-padding: 6px 8px 4px 8px";
        prevBt.style(style);
        nextBt.style(style);

        zoom.setOnAction(e -> updateZoom(zoom.getSelectionModel().getSelectedIndex()));

        nextBt.setOnAction(e -> nextPage());
        prevBt.setOnAction(e -> prevPage());

        insight.getChildren().add(pane);
        FxStackPane.setAlignment(pane, Pos.TOP_LEFT);

        footerLeft.add(leftSideBt, zoom, searchBt, displayBt, new FxSpacer(IS.FOOTER_HEIGHT), prevBt, progress, nextBt);
        footerRight.add(coordsLabel, rightSideBt);
        leftSide.minWidth(0);
        leftSide.prefWidth(100);

        zoom.getSelectionModel().select(env.insight.prefs.get(Prefs.ZOOM, "100%"));
    }

    public void showSearchDialog()
    {
        SearchDialog.Show(this);
    }


    public void showDisplayDialog()
    {
        SettingsDialog.Show(this);
    }

    public void hide()
    {
        pane.setOpacity(0);
    }

    public void show()
    {
        splitPane.setDividerPosition(0, 0.15);
        if (pane.getOpacity() < 1)
            pane.setOpacity(1);
        // Fx.FadeIn(pane, 2);
    }

    public void setRightSide(int size, FxFinalEnvironment... tabs)
    {
        this.rightSide.setTabs(size, tabs);
    }

    @Override
    public void dispose()
    {
        if (searchDialog != null)
            searchDialog.close();
        if (displayDialog != null)
            displayDialog.close();
        env.insight.prefs.put(Prefs.ZOOM, zoom.getSelectionModel().getSelectedItem());
        env.insight.prefs.put(Prefs.LEFT_SIDE_WIDTH, this.splitPane.getDividerPositions()[0]);
        ribbonTabPane.dispose();
    }

    public void selectZoomFromScroll(double ticks)
    {
        int index = zoomIndex() - (int) Math.round(ticks);
        index = index < 0 ? 0 : index > IS.ZOOMS.length - 1 ? IS.ZOOMS.length - 1 : index;
        env.viewProps().scale = Prefs.screenDpi / env.ocdDpi() * IS.ZOOMS[index] / 100f;
        selectZoom(zoomIndex());
    }

    public void selectZoom(int index)
    {
        this.zoom.getSelectionModel().select(index);
    }

    public void updateZoom(int index)
    {
        int zoom = IS.ZOOMS[index];
        env.viewProps().scale = Prefs.screenDpi / env.ocdDpi() * zoom / 100f;
        env.ribbon().pager.refreshScale();
    }

    public float scale()
    {
        return Nb.Int(true, zoom.getValue(), 100) / 100f;
    }

    public void nextPage(boolean forward)
    {
        if (forward)
            nextPage();
        else
            prevPage();
    }

    public void nextPage()
    {
        updatePage(1);
    }

    public void prevPage()
    {
        updatePage(-1);
    }

    public void updatePage(int delta)
    {
        env.updatePage(env.ocd.pageHandler.getPage(env.page.number() + delta));
    }

    public void updatePage(double norm)
    {
        OCDPage page = env.page;
        if (page == null)
            return;
        norm = norm < 0 ? 0 : norm > 1 ? 1 : norm;
        env.updatePage(env.ocd.pageHandler.getPage(Math3.Round(norm * env.ocd.nbOfPages())));
    }

    public void updatePage(String nb)
    {
        Scanner sc = new Scanner(nb.trim().replace('/', ' '));
        if (sc.hasNextInt())
            env.updatePage(env.ocd.pageHandler.getPage(sc.nextInt()));
        sc.close();
    }

    @Override
    public void refresh()
    {
        updatePageField();
        leftSide.refresh();
        // this.rightSide.refresh();
    }

    public void updatePageField()
    {
        OCDPage page = env.page;
        if (page == null)
        {
            progress.setProgress(1, 1);
        } else
        {
            int nb = page.number();
            int total = page.nbOfPages();
            this.progress.setProgress(nb, total);
        }
    }

    public int zoomIndex()
    {
        return IS.ZoomIndex(env.viewProps().scale * env.ocdDpi() / Prefs.screenDpi);
    }

    public void updateDom()
    {
        if (env.ocd != null && sideDom != null)
            this.sideDom.update();
    }

    public void updateNav()
    {
        if (env.ocd != null && sideNav != null)
            this.sideNav.update();
    }

    public void updateThumbs()
    {
        if (env.ocd != null && sideThumb != null)
            this.sideThumb.update();
    }

    public void updateNavDomAndThumbs()
    {
        updateNav();
        updateDom();
        updateThumbs();
    }

    public void requestBoardFocus()
    {
        this.board.requestFocus();
    }

    // public void resizeBoard(Dimension dimension)
    // {
    // this.board.resize(dimension);
    // }

    public void ensureVisible(Node node)
    {
        board.scroll.ensureVisible(node);
    }

    public void task(String text)
    {
        this.progressLabel.setText(text);
    }

    public void displayCoords(String text)
    {
        coordsLabel.setText(text);
    }

    public void displayCoords(FxRibbon tab, FxMouse ms)
    {
        Metric m = env.insight.config.metric;
        Rectangle3 box = tab.hasPage() ? tab.page().bounds() : new Rectangle3();
        String status = tab.statusText(ms);
        displayCoords(status != null ? status + " " : box.intWidth() + " x " + box.intHeight() + "   ( " + m.fromPx(ms.x(), env.insight.config.nbOfDecimalsDisplayed) + ", " + m.fromPx(ms.y(), env.insight.config.nbOfDecimalsDisplayed) + " )" + (m.isPX() ? "" : m.unit) + " ");
    }

    public void progress(double progress, String description)
    {
        boolean doShow = progress < 1;
        glassPane.progress(progress);
        if (Str.HasData(description))
            env.message(description, progress >= 1 ? 3 : 0, progress >= 1 ? true : null);
        progressLabel.setVisible(doShow);
        glassPane.show(doShow);
    }

    public void progress(Progressable prog)
    {
        boolean doShow = prog == null ? false : prog.progressState() != Progression.STATE_COMPLETED;
        glassPane.progress(prog.progress());
        String description = prog.progressDescription();
        if (description != null)
            env.message(description, prog.isProgressComplete() ? 3 : 0, prog.isProgressComplete() ? true : null);
        progressLabel.setVisible(doShow);
        glassPane.show(doShow);
    }

}
