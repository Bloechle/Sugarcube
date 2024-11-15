package sugarcube.insight.ribbon.textedit;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Cmd;
import sugarcube.common.data.collections.Commands;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxClipboard;
import sugarcube.common.ui.fx.controls.FxToggles;
import sugarcube.common.ui.fx.dnd.FileDnD;
import sugarcube.common.ui.fx.event.FxInput;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.insight.Insight;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxFonts;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.core.IS;
import sugarcube.insight.interfaces.FxRibbonLoader;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.interaction.ISSelector;
import sugarcube.insight.ribbon.textedit.render.EditPager;
import sugarcube.insight.ribbon.toolbox.actions.TextAct;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.pdf.resources.icons.Icon;

public class EditRibbon extends FxRibbon
{
    public static FxRibbonLoader LOADER = env -> new EditRibbon(env);

    protected @FXML Button openBt, saveBt, localBt, backBt;
    protected @FXML BorderPane paragraphPane;
    protected @FXML ColorPicker fillBt, strokeBt;
    protected @FXML ComboBox<String> dashBox, fontBox, sizeBox, penBox, interlineBt, charspaceBt;
    protected @FXML ToggleButton boldBt, italicBt, underBt, strikeBt, subBt, supBt, leftBt, centerBt, rightBt, justifyBt;
    protected @FXML Label interlineLabel, charspaceLabel;

    public ISSelector selector;
    public Commands commands = new Commands();

    public EditRibbon(final FxEnvironment env)
    {
        super(env, "Text Content");
        this.rightSideSize = 0;
        this.selector = new ISSelector(this);
    }

    @Override
    public void init()
    {
        super.init(openBt, saveBt, localBt, backBt);
        Fx.DisplayNone(paragraphPane);

        FxToggles.Handle(subBt, supBt, bt -> scriptEvent(bt));
        FxToggles.Handle(leftBt, centerBt, rightBt, justifyBt, bt -> alignEvent(bt));
        // this.insertToggles = FxToggles.Handle(textBt, imageBt, shapeBt, annotBt,
        // bt -> insertEvent());

        Color3 green = IS.GREEN_LIGHT;
        Color3 orange = IS.ORANGE_LIGHT;

        Icon.BOLD.set(boldBt, iconSize, 60, "Text Bold", null, e -> fontEvent());
        Icon.ITALIC.set(italicBt, iconSize, 60, "Text Italic", null, e -> fontEvent());
        Icon.ALIGN_LEFT.set(leftBt, iconSize, 60, "Align Left", orange);
        Icon.ALIGN_CENTER.set(centerBt, iconSize, 60, "Align Center", orange);
        Icon.ALIGN_RIGHT.set(rightBt, iconSize, 60, "Align Right", orange);
        Icon.ALIGN_JUSTIFY.set(justifyBt, iconSize, 60, "Justify", orange);
        Icon.TEXT_HEIGHT.set(interlineLabel, iconSize, 60, "Interline", Color3.LIGHT_GRAY);
        Icon.TEXT_WIDTH.set(charspaceLabel, iconSize, 60, "Charspace", Color3.LIGHT_GRAY);

        Fx.Set(fontBox, FxFonts.NAMES, "Calibri", val -> fontEvent());
        Fx.Set(sizeBox, IS.FONTSIZES, "12", val -> fontEvent());
        Fx.Set(interlineBt, IS.INTERLINES, "1.0", val -> event(CSS.LineHeight, interlineBt));
        Fx.Set(charspaceBt, IS.CHARSPACES, "0.0", val -> event(CSS.LetterSpacing, charspaceBt));
        Fx.Set(dashBox, IS.DASHES, "none", val -> event(CSS.BorderStyle, dashBox));
        Fx.Set(penBox, IS.THICKNESSES, "1.0", val -> event(CSS.BorderWidth, penBox));

        strokeBt.setValue(Color3.DUST_WHITE.fx());
        strokeBt.setOnAction(e -> event(CSS.BorderColor, strokeBt));
        fillBt.setOnAction(e -> event(CSS.Color, fillBt));

        // hide not yet implemented controls
        Fx.SetVisible(false, underBt, strikeBt, subBt, supBt);
    }

    @Override
    public EditPager pager()
    {
        return pager == null ? new EditPager(this) : (EditPager) pager;
    }

    public void event(String key, Object o)
    {
        commands.sendIf(events, key, Fx.Value(o), true);
    }

    public void refreshToolbar(OCDText text)
    {

        if (text == null)
            return;
        // we do not want sync events to be handled
        this.events = false;
        String fontname = text.fontname();
        // Log.debug(this, ".syncToolbar - " + fontname);
        // Fx.setString(fontBox, SVGFont.fontFamily(fontname));
        // Fx.setString(sizeBox, Math.round(text.scaledFontsize()));
        this.boldBt.setSelected(SVGFont.isBold(fontname));
        this.italicBt.setSelected(SVGFont.isItalic(fontname));
        this.events = true;
    }

    @Override
    public void refresh()
    {
        super.refresh();
        this.selector.refresh();
    }

    public void fontEvent()
    {
        // refont
        if (events)
        {
            String fontname = fontname();
            Log.debug(this, ".fontEvent - " + fontname + ", text=" + selector.text());
//      selector.refresh();

            if (fontname != null)
                for (OCDText text : selector.selectedText())
                {
                    env.ocd.needFont(fontname, text.string(), true);
                    text.setFontname(fontname);
                    text.setFontsize(fontsize() / text.scaleY());
                    // OCDOcr.rebox(text);
                    pager.board.refresh(text);
                }

            this.refreshNodes();
            commands.send(CSS.FontName, SVGFont.Postfix(fontBox.getValue().toString(), boldBt.isSelected(), italicBt.isSelected()));
        }
    }

    public void refreshNodes()
    {
        this.pager.refreshNodes();
        this.pager.refresh();
    }

    public void scriptEvent(ToggleButton bt)
    {
        Log.debug(this, ".scriptBt - " + bt.selectedProperty().get());
    }

    public void alignEvent(ToggleButton bt)
    {
        if (events)
            commands.send(CSS.TextAlign,
                    justifyBt.isSelected() ? CSS._justify : (centerBt.isSelected() ? CSS._center : (rightBt.isSelected() ? CSS._right : CSS._left)));
        Log.debug(this, ".alignBt - " + bt.selectedProperty().get());
    }

    public String fontname()
    {
        return SVGFont.Rename(Fx.String(this.fontBox), boldBt.isSelected(), italicBt.isSelected());
    }

    public int fontsize()
    {
        return Fx.Int(this.sizeBox, 12);
    }

    @Override
    public void fileDropped(FileDnD dnd)
    {

        this.update();
    }

    public boolean boardKeyEvent(FxKeyboard kb)
    {
        if (kb.isDown())
            boardKeyDown(kb);
        else if (kb.isTyped())
        {
            if (kb.isControlDown())
                return false;

            char c = kb.getChar();

            Log.debug(this, ".boardKeyType - " + (int) c);
            if (Unicodes.isCharCode(c) || c == Unicodes.ASCII_CR)
            {
                selector.delete();
                writeText(c);
            } else
            {
                OCDTextBlock block = selector.block(false);
                switch (c)
                {
                    case Unicodes.ASCII_BACKSPACE:
                        selector.delete(false);
                        break;
                    case Unicodes.ASCII_DEL:
                        selector.delete(true);
                        break;
                }
                pager.board.refresh(block);
                refresh();
            }

            if (false)
            {
                OCDFlow flow = selector.flow(false);
                if (flow != null)
                {
                    // Log.debug(this, ".boardKeyState - type: code="
                    // +(int)kb.getKeyChar());
                    if (c == 127 || c == 0xffff || (c < 32 && c != '\n' && c != '\r'))// 127=del
                        return false;
                    if (c == '\r')// converts carriage return to line feed
                        c = '\n';
                    selector.delete();

                    OCDText text = newText(null, (char) c);
                    flow.addText(text, selector.end);

                    selector.select(text, text.length());

                    Log.debug(this, ".insertChar - node=" + flow.xmlString());

                    // String textMode = UndoText.MODE_ADD;
                    // UndoPoint last = tab.undoStack().undos.last();
                    // UndoText undo = last instanceof UndoText ? (UndoText) last :
                    // null;
                    // if (undo != null && undo.isMode(textMode) &&
                    // undo.isFlowRef(node.needRef()))
                    // undo.addStep(null, text);
                    // else
                    // {
                    // UndoText p = new UndoText(tab, node, UndoText.MODE_ADD);
                    // p.addStep(null, text);
                    // tab.addUndoPoint(p);
                    // }

                    flow.inflate();
                    kb.consume();
                }
            }

        }
        return false;
    }

    public synchronized OCDText newText(OCDTextLine line, char... c)
    {
        String fontname = commands.string(CSS.FontName, "Calibri");
        float size = commands.real(CSS.FontSize, 11);
        String chars = new String(c);
        fontname = env.ocd.needFont(fontname, chars);

        OCDText text = new OCDText(line);
        text.setUnicodes(chars);
        for (String key : commands.keys())
            text.command(commands.cmd(key));
        text.setFontname(fontname);// fontname needs special care
        text.setFontsize(size);// as well as font size
        if (line != null)
            line.add(text);
        return text;
    }

    public void boardKeyDown(FxKeyboard kb)
    {
        if (selector.isSelecting())
        {
            if (kb.isDown())
                selector.keyDown(kb);
            return;
        }

        KeyCode code = kb.getCode();
        Log.debug(this, ".boardKeyDown - " + kb.getCode() + ", ctrl=" + kb.isControlDown() + ", alt=" + kb.isAltDown());
        boolean ctrl = kb.isControlDown();

        if (ctrl)
        {
            switch (code)
            {
                case SPACE:
                    env.gui.nextPage(code == KeyCode.SPACE);
                    break;
                case C:
                    event(Cmd.COPY, Cmd.SELECTION);
                    break;
                case S:
                    env.saveOCD(this);
                    break;
                case V:
                    String text = FxClipboard.text();
                    if (Str.HasData(text))
                    {
                        selector.delete();
                        writeText(text);
                    }
                    break;
                default:
                    break;
            }
        }

        if (false)
        {
            OCDFlow flow = selector.flow(false);
            if (flow != null)
            {
                boolean select = selector.isPopulated();
                switch (kb.getCode())
                {
                    case BACK_SPACE:
                        selector.delete(true);
                        break;
                    case DELETE:
                        selector.delete(false);
                        break;
                    case X:
                        if (ctrl && select)
                        {
                            FxClipboard.put(selector.text());
                            selector.delete();
                            kb.consume();
                        }
                        break;
                    case V:
                        // if (ctrl)
                        // {
                        // String clip = Zen.Clipboard.text();
                        // if (clip != null && !clip.isEmpty())
                        // {
                        // deleteSelection();
                        // for (char c : clip.toCharArray())
                        // this.insertChar(c);
                        // }
                        // }
                        break;
                    case A:
                        if (ctrl)
                        {

                        }
                        break;
                    default:
                        break;
                }
                // Zen.debug(this, ".boardKeyPressed - code: " + e.getKeyCode() +
                // ", start="
                // + start + ", end=" + end);
                flow.inflate();
            }
        }

    }

    public boolean boardMouseEvent(FxMouse ms)
    {
        // catches all mouse event from board by filtering (not handling)
        if (ms.isMove())
        {
            if (ms.hasShift() && !ms.hasCtrl())
            {
                moveText(selector.end, ms.xy());
            }
        } else if (ms.isDrag())
        {
            if (ms.isPrimaryBt())
            {
                selector.select(ms);
            }

            if (ms.isPrimaryBt())
                needCaret(0);
        } else if (ms.isDown())
        {
            if (ms.isPrimaryBt())
                selector.select(ms);

        } else if (ms.isClick())
        {
            if (ms.isPrimaryBt())
            {
                selector.select(ms);

                removeEmptyParagraphs();
                needCaret(0);

                if (!selector.isSelecting())
                    TextAct.NewText(this, ms.xy());
            }
        }

        env.gui.displayCoords(this, ms);
        return false;
    }

    @Override
    public boolean boardPopup(FxInput in)
    {
        Log.debug(this, ".boardPopup - context");
        this.popup.clear();

        TextEditAct.Populate(popup.sepMenu("Text", null), this);
        popup.sep();

        //
        // StyleTree.Generate(modeler.styles()).populate(this, popup.menu("Style",
        // Icon.TAG.get(iconSize, Color3.COLUMBIA_BLUE)), true);
        // TextAct.Populate(popup.sepMenu("Text", Icon.ALIGN_JUSTIFY.get(iconSize)),
        // this);
        // TableAct.Populate(popup.sepMenu("Table", Icon.TABLE.get(iconSize)),
        // this);
        // GroupAct.Populate(popup.sepMenu("Group",
        // Icon.OBJECT_GROUP.get(iconSize)), this);
        // AnnotAct.Populate(popup.sepMenu("Annotation", Icon.EDIT.get(iconSize)),
        // this);
        // // CopyAct.Populate(popup.sepMenu("Copy", Icon.COPY.get(iconSize)),
        // this);
        // PageAct.Populate(popup.sepMenu("Page", Icon.NEWSPAPER_ALT.get(iconSize)),
        // this);
        // ImageAct.Populate(popup.sepMenu("Image", Icon.IMAGE.get(iconSize)),
        // this);

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

    public void writeText(String str)
    {
        OCDText text = selector.end;
        int index = selector.endIndex;
        if (text == null || index < 0)
            return;

        text.unicodes().insert(index, str.toCharArray());
        needCaret(str.length());
        pager.board.refresh(text);
        refresh();
    }

    public void writeText(char c)
    {

        OCDText text = selector.end;
        int index = selector.endIndex;
        if (text == null || index < 0)
            return;

        if (c == Unicodes.ASCII_CR)
        {
            TextAct.NewLine(this, text);
        } else
        {
            text.unicodes().insert(index, c);
            needCaret(1);
        }
        pager.board.refresh(text);
        refresh();

    }

    public void needCaret(int delta)
    {
        while (delta-- > 0)
            selector.next(true, false);
        refreshToolbar(selector.selectedText().first());
        selector.refresh();
    }

    public void removeEmptyParagraphs()
    {
        for (OCDTextBlock block : pager.page.content().blocks())
        {
            if (block.isTextEmpty())
            {
                FxOCDNode node = pager.board.fxNode(block);
                if (node != null)
                    node.remove();
                block.remove();
            }
        }
    }

    public void moveText(OCDText text, Point3 p)
    {
        if (text == null)
            return;
        float fs = text.scaledFontsize();
        float dx = p.x - text.x();
        float dy = p.y + (fs > 0 ? fs : 12) - text.y();
        text.setXY(text.x() + dx, text.y() + dy);
        pager.board.refresh(text);
        refresh();
    }

    public void command(Cmd cmd)
    {
        if (!cmd.forward)
        {
            linkAndBrush(cmd);
            return;
        }

        // Log.debug(this, ".intercept - " + cmd);
        // switch (cmd.key)
        // {
        // case TextDecoration:
        // return new Cmd(TextDecoration, ((tab.underBt.isSelected() ? _underline :
        // "") + (tab.strikeBt.isSelected() ? " " + _lineThrough : "")).trim());
        // }
        // return cmd;

        Log.debug(this, ".command - " + cmd);
        if (cmd.map == null)
            this.commands.put(cmd);

        OCDPaintable node = pager.interactor.node();
        if (node != null)
            switch (cmd.key)
            {
                case Cmd.COPY:
                    if (node != null)
                        FxClipboard.put(node);
                    break;
                case Cmd.PASTE:
                    if ((node = FxClipboard.ocdNode()) != null)
                    {
                        OCDAnnot annot = node.asAnnot();
                        if (annot == null)
                            page().content().add(node.copy());
                        else
                            page().annots().addAnnotation(annot.copy());
                        this.pager.update();
                    }
                    break;
                case Cmd.DELETE:
                    if (node != null)
                    {
                        this.pager.stopInteract();
                        node.delete();
                        this.pager.update();
                    }
                    break;
                default:
                    node.command(cmd);
                    break;
            }
        update();
    }

    public void linkAndBrush(Cmd cmd)
    {
        // Log.debug(this, ".commandBack - " + cmd);
        events = false;
        switch (cmd.key)
        {
            case CSS.Color:
                fillBt.setValue(cmd.fxColor(null));
                break;
            case CSS.BorderColor:
                strokeBt.setValue(cmd.fxColor(null));
                break;
            case CSS.BorderWidth:
                penBox.setValue(cmd.string("0"));
                break;
            case CSS.FontWeight:
                boldBt.setSelected(!cmd.isValue(CSS._normal));
                break;
            case CSS.FontStyle:
                italicBt.setSelected(!cmd.isValue(CSS._normal));
                break;
            case CSS.TextDecoration:
                underBt.setSelected(cmd.hasValue(CSS._underline));
                strikeBt.setSelected(cmd.hasValue(CSS._lineThrough));
                break;
            case CSS.FontSize:
                sizeBox.setValue("" + cmd.real(8));
                break;
            case CSS.FontName:
                String font = cmd.string("Calibri");
                fontBox.setValue(SVGFont.FontFamily(font));
                boldBt.setSelected(SVGFont.isBold(font));
                italicBt.setSelected(SVGFont.isItalic(font));
                break;
            case CSS.TextAlign:
                switch (cmd.string(CSS._left))
                {
                    case CSS._left:
                        leftBt.setSelected(true);
                        break;
                    case CSS._right:
                        rightBt.setSelected(true);
                        break;
                    case CSS._center:
                        centerBt.setSelected(true);
                        break;
                    case CSS._justify:
                        justifyBt.setSelected(true);
                        break;
                }
                break;
        }
        events = true;
        // else if (cmd.isKey(LineHeight))
        // interlineS3.setValue(false, cmd.real((float) interlineS3.value()));
        // else if (cmd.isKey(LetterSpacing))
        // charspaceS3.setValue(false, cmd.real((float) charspaceS3.value()));
        // else if (cmd.isKey(TextScript))
        // scriptGrp.select(TextScript + cmd.string(_normal));
        // else if (cmd.isKey(FontSize))
        // sizeBox.setValue(false, cmd.real(Math.round(sizeBox.value())));

        //
        // this.refreshUndoRedoBt();
    }

    @Override
    public void dispose()
    {

    }

    public static void main(String... args)
    {
//        Insight.LaunchWithArgs(args, env -> new FileRibbon(env));
        Insight.main();
    }

}
