package sugarcube.insight.ribbon.toolbox;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Cmd;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.time.RunTimer;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.dialogs.FxDialog;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.event.FxInput;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.insight.Insight;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.interfaces.FxRibbonLoader;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.ribbon.actions.GroupAct;
import sugarcube.insight.ribbon.actions.annot.AnnotAct;
import sugarcube.insight.ribbon.file.FileRibbon;
import sugarcube.insight.ribbon.toolbox.actions.*;
import sugarcube.insight.ribbon.toolbox.render.ToolPager;
import sugarcube.formats.ocd.analysis.text.Normalizer;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;
import sugarcube.formats.pdf.resources.icons.Icon;

import java.awt.*;

public class ToolboxRibbon extends FxRibbon {
    public static FxRibbonLoader LOADER = env -> new ToolboxRibbon(env);

    public @FXML Button openBt, saveBt, localBt, backBt;
    public @FXML TextField xField, yField, wField, hField;
    public @FXML ToggleButton applyModelTg, tableModeTg, lineModeTg, autoTableTg, playBt;
    public @FXML ListView<String> tablerList;
    public @FXML Slider opacitySlider;

    // link & brush, undo/redo
    // public Commands commands;
    public OCDTextLine validLine = null;
    public RunTimer runTimer = new RunTimer();

    public ToolboxRibbon(final FxEnvironment env) {
        super(env, "Structures");
        this.rightSideSize = 300;
    }

    @Override
    public void init() {
        super.init(openBt, saveBt, localBt, backBt);
        super.init(xField, yField, wField, hField);

        this.lineModeTg.setOnAction(e -> update());
        this.tableModeTg.setOnAction(e -> update());

        Fx.Listen((obs, old, val) -> update(), opacitySlider);
        Fx.InitList((obs, old, val) -> {
        }, tablerList, "Empty Table", "Empty Table", "Path Segments", "Text Projection", "Fedlex Systematic");


        playBt.setGraphic(Icon.PLAY.get(iconSize));

    }

    @Override
    public ToolPager pager() {
        return pager == null ? new ToolPager(this) : (ToolPager) pager;
    }

    public void event(String key, Object o) {
        Log.debug(this, ".event - key=" + key + ", o=" + o);
        // commands.sendIf(events, key, Fx.Value(o), true);
    }



    @Override
    public void refresh() {
        super.refresh();
        this.setBoxFields(fieldBox());
    }


    public boolean process(OCDPage page) {

        return true;

    }

    @Override
    public void startInteraction(FxOCDNode node) {
        // this.objectPane.update(node);
        // this.annotPane.update(node);
    }

    @Override
    public void newPage(OCDPage page) {
        this.events = false;

        this.events = true;
    }

    @Override
    public void fileDropped(FileDnD dnd) {
        this.update();
    }

    public OCDTextLine nextInvalidData(OCDPage page) {
        if (page == null)
            return null;

        Log.debug(this, ".nextInvalidData - " + page.number());

        String lastname = "";
        this.validLine = null;
        page.ensureInMemory();
        for (OCDTextBlock block : page.content().blocks().xSort()) {

            if (block.isClassname("column")) {
                Rectangle3 blockBounds = block.bounds();
                float minX = blockBounds.minX();
                for (OCDTextLine line : block) {
                    String text = line.string();
                    char c = text.charAt(0);
                    if (c == 'e' || c == 'k')
                        continue;
                    Rectangle3 bounds = line.bounds();
                    if (bounds.minX() - minX > 5)
                        continue;

                    boolean issue = false;

                    int i = text.indexOf(",");
                    for (int j = 0; j < i; j++) {
                        c = text.charAt(j);
                        if (c >= '0' && c <= '9' || c == '(' || c == ')')
                            issue = true;
                    }

                    if (issue) // text.compareTo(lastname) < 0)
                    {
                        Rectangle3 r = line.bounds();
                        OCDAnnot annot = page.annotAt(r.center(), OCDAnnot.TYPE_VALIDATION);
                        if (annot == null) {
                            if (page != this.page())
                                page.freeFromMemory();
                            return line;
                        }
                    }
                    lastname = text;
                    minX = bounds.minX();
                }
            }
        }
        if (page != this.page())
            page.freeFromMemory();
        return null;
    }

    public void checkNextPage(OCDPage page) {
        Fx.Run(() -> {

            OCDTextLine line = nextInvalidData(page);
            if (line == null) {
                OCDPage next = page.next();
                if (next != page)
                    checkNextPage(page.next());
            } else
                Fx.Run(() -> env.loadPage(page.number()));
        });
    }

    public void validateData() {
        OCDPage page = this.page();
        if (page == null)
            return;
        String lastname = "";

        this.validLine = nextInvalidData(page);
        if (validLine != null) {
            FxRect box = new FxRect(validLine.bounds());
            box.mouseTransparent();
            box.fill(Color3.ORANGE_RED.alpha(0.5));
            this.pager.board.metaLayer("valid-boxes").add(box);

        }

        if (validLine == null) {
            checkNextPage(page.next());
        } else {
            String input = validLine.string();
            String retext = FxDialog.Input("Edit Textline", "", "", input);
            if (retext != null && !retext.equals(input)) {
                validLine.retext(retext, 6, 16);
            }
            if (retext != null) {
                validLine.modifyPage().annots().addValidationAnnot(validLine.bounds(), "checked");
                update();
            }
        }

        // Fx.Run(() -> this.gui().ensureVisible(box));
    }





    public void detectTables(OCDPage[] pages, int index) {
        Fx.Run(() -> {
            // tab.env().progress(progressor);
            try {
                env().updatePage(pages[index]);
                // tab.env().progress(progressor.update((index + 1) / (float)
                // pages.length, "Processing page " + (index + 1) + "/" +
                // pages.length));

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (index < pages.length - 1)
                detectTables(pages, index + 1);
            else {
                // tab.env().progress(progressor.complete("OCD Pages Processed"));
                // tab.env().progress(null);
            }
        });
    }

    public void groupAsParagraph() {

         if (pager.hasInteractor())
            new TextAct(this, TextAct.GROUP).act();
    }

    public void autoDetect() {
        String pageLink = null;
        for (OCDNavItem item : ocd().nav().toc().list()) {
            if (item.text.startsWith("6.")) {
                pageLink = item.link;
            }
        }
        if (pageLink != null)
            env.updatePage(pageLink);
        autoDetect(page());
    }

    public void autoDetect(OCDPage page) {
//    Log.debug(this, ".autoDectect - page=" + page.number());
        Fx.Run(() -> {
            final OCDPage next = page.hasNext() ? page.next() : null;
            if (next != null && playBt.isSelected())
                runTimer.scheduleFX(5, () -> autoDetect(next));
            else {
                this.update();
                env.saveOCD(this);
            }
        });
    }

    public boolean boardKeyEvent(FxKeyboard kb) {
        super.boardKeyEvent(kb);

        OCDPage page = pager.page;
        KeyCode code = kb.getCode();
        boolean shift = kb.isShiftDown();
        boolean ctrl = kb.isControlDown();
        boolean alt = kb.isAltDown();
        boolean meta = kb.isMetaDown();

        this.pager().displayROrder(alt && !(ctrl || shift));

        if (kb.isDown()) {
            if (ctrl) {
                switch (code) {
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                    case PERIOD:
                    case DELETE:
                        break;
                    case DIGIT0:
                        update();
                        break;
                    case C:
                        event(Cmd.COPY, Cmd.SELECTION);
                        break;
                    case G:
                        this.groupAsParagraph();
                        // GroupAct.GroupAsContent(this).act();
                        break;
                    case LESS:
                        // this.validateData();
                        this.autoDetect();

                        // modelize(page());
                        // refresh();
                        // tabler.extract(this);
                        // this.update();
                        // refresh();

                        break;
                    case K:
                        for (OCDText text : page.modify().texts()) {
                            text.setFillColor(Color.BLACK);
                        }
                        update();
                        break;
                    case M:
                        break;
                    case N:
                        Normalizer.New(null).apply(page);
                        pager.update();
                        // if (nodes.isGroup())
                        // {
                        // OCDGroup g = nodes.group();
                        // String name = Dialog3.showStringDialog(env.board, null,
                        // "Rename Group", "Enter group name: ", g.name());
                        // if (name != null)
                        // g.setName(name);
                        // }
                        break;
                    case O:
                        ImageAct.ViewAct(this);
                        page.modify();
                        // new AnnotAct(tab, AnnotAct.IMAGE).act();
                        break;
                    case P:
                        this.groupAsParagraph();
                        page.modify();
                        break;
                    case Q:
                        // new ParagraphAct(this).act();
                        break;
                    case R:
                        // TableRowAct.Trigger(tab, TableRowAct.ADD_ROW);
                        GroupAct.GroupAsContent(this).act();
                        break;
                    case T:
                        break;
                    default:
                        int digit = kb.ctrlF();
                        break;
                }
            } else {
                switch (code) {
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        pager.interactor.move(code, 1);
                        break;
                    default:
                        break;
                }
            }
        } else if (kb.isTyped()) {
            if (kb.isControlDown())
                return false;

            char c = kb.getChar();

            switch (c) {
                case Unicodes.ASCII_DEL:
                    DeleteAct.Delete(this);
                    return true;
            }

        }

        return false;
    }

    // public boolean boardMouseEvent(FxMouse ms)
    // {
    // // catches all mouse event from board by filtering (not handling)
    // // Log.debug(this, ".boardMouseEvent - "+ms);
    //
    // if (ms.isDown())
    // {
    //
    // // Log.debug(this, ".boardMouseDown - "+this.mode);
    // if (ms.isPrimaryBt())
    // {
    // if (pager.focus.over == null || pager.hasInteractor() &&
    // pager.interactor.isInside(ms.xy()))
    // pager.interactor.mouseDown(ms);
    // else
    // {
    // for (OCDTextBlock block : pager.page.content().blocks())
    // if (block.bounds().contains(ms.xy()))
    // return false;
    //
    // if (pager.focus.over != null)
    // pager.pleaseInteract(pager.focus.over, ms);
    // else
    // pager.interactor.mouseDown(ms);
    // }
    // }
    // } else
    // super.boardMouseEvent(ms);
    //
    // env.gui.displayCoords(ms);
    // return false;
    // }

    public boolean boardMouseEvent(FxMouse ms) {
        // catches all mouse event from board by filtering (not handling)
        // Log.debug(this, ".boardMouseEvent - "+ms);

        if (ms.isDown()) {

            // Log.debug(this, ".boardMouseDown - "+this.mode);
            if (ms.isPrimaryBt()) {
                if (pager.focus.over == null || pager.hasInteractor() && pager.interactor.isInside(ms.xy()))
                    pager.interactor.mouseDown(ms);
                else {
                    OCDPaintable overNode = pager.node();
                    if (overNode != null && overNode.isTextLine()) {
                        this.validLine = (OCDTextLine) overNode;
                        String input = validLine.string();
                        String retext = FxDialog.Input("Edit Textline", "", "", input);
                        if (retext != null && !retext.equals(input)) {
                            validLine.retext(retext, 6, 16);
                        }
                        // validLine.modifyPage().annots().addValidationAnnot(validLine.bounds(),
                        // "checked");
                        update();
                    }

                    for (OCDTextBlock block : pager.page.content().blocks())
                        if (block.bounds().contains(ms.xy()))
                            return false;

                    if (pager.focus.over != null)
                        pager.pleaseInteract(pager.focus.over, ms);
                    else
                        pager.interactor.mouseDown(ms);

                }

            }
        } else
            super.boardMouseEvent(ms);

        env.gui.displayCoords(this, ms);
        return false;
    }

    @Override
    public boolean boardPopup(FxInput in) {
        Log.debug(this, ".boardPopup - context");
        this.popup.clear();

        popup.sep();

        TextAct.Populate(popup.sepMenu("Text", Icon.ALIGN_JUSTIFY.get(iconSize)), this);
        GroupAct.Populate(popup.sepMenu("Group", Icon.OBJECT_GROUP.get(iconSize)), this);
        AnnotAct.Populate(popup.sepMenu("Annotation", Icon.EDIT.get(iconSize)), this);
        CopyAct.Populate(popup.sepMenu("Copy", Icon.COPY.get(iconSize)), this);
        PageAct.Populate(popup.sepMenu("Page", Icon.NEWSPAPER_ALT.get(iconSize)), this);
        ImageAct.Populate(popup.sepMenu("Image", Icon.IMAGE.get(iconSize)), this);

        // OCRAct.Populate(popup.sepMenu("OCR", Icon.FILE_TEXT.get(iconSize)),
        // this);

        // menu.add(new ListAct(this), new ItemAct(this), new
        // SublistAct(this), new SublistEndAct(this));
        // menu.add(new GroupAct(this, false, false, "Current Page",
        // OCDGroup.COLUMN));
        // if (nodes.isAnnot())
        // menu.add("-", new DuplicateAnnotA3(this, nodes.annot()), new
        // RemoveAnnotA3(this));
        this.popup.show(env.gui.board.canvas, in.screenXY());
        return true;
    }

    @Override
    public void dispose() {

    }

    public static void main(String... args) {
        Insight.LaunchWithArgs(args, FileRibbon.LOADER, ToolboxRibbon.LOADER);
    }

}
