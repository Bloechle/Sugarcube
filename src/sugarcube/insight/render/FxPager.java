package sugarcube.insight.render;

import javafx.scene.Node;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.ui.fx.base.FxBoxed;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.insight.core.FxDisplayProps;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxPagerBoard;
import sugarcube.insight.core.FxRibbon;
import sugarcube.insight.render.interaction.FxFocus;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class FxPager<R extends FxRibbon>
{

    public final FxEnvironment env;
    public final FxPagerBoard board;
    public final FxFocus focus;
    public final FxInteractor interactor;
    public R tab;
    public OCDPage page;
    public boolean interactable = true;
    public String[] boardStyles = new String[0];

    public FxPager(R tab, boolean interactable)
    {
        this.tab = tab;
        this.env = tab.env();
        this.board = env.gui.board;
        this.interactable = interactable;
        this.focus = new FxFocus(this);
        this.interactor = new FxInteractor(this);
    }

    public void init()
    {
        if (interactable)
        {
            board.metaLayer("focus", focus);
            board.metaLayer("interactor", interactor);
        }
    }

    public Line3 interactorExtent(boolean withDefault)
    {
        return hasInteractor() ? interactor.extent()
                : withDefault ? new Line3(page.width / 4, page.height / 4, page.width * 3 / 4, page.height * 3 / 4) : null;
    }

    public boolean preventFocusOver()
    {
        return false;
    }

    public FxDisplayProps display()
    {
        return env.gui.display;
    }

    public OCDDocument ocd()
    {
        return page != null ? page.document() : env.ocd();
    }

    public OCDPaintable node()
    {
        OCDPaintable node = interactorNode();
        return node == null ? focusNode() : node;
    }

    public OCDPaintable interactorNode()
    {
        return interactor == null ? null : interactor.node();
    }

    public OCDPaintable focusNode()
    {
        return focus == null ? null : focus.overNode();
    }

    public boolean hasInteractor()
    {
        return interactable && interactor != null && interactor.isInteracting();
    }

    public void stopInteract()
    {
        this.interactor.stop();
    }

    public void pleaseInteract(OCDPaintable node)
    {
        this.pleaseInteract(board.fxNode(node), null);
    }

    public void pleaseInteract(FxOCDNode node, FxMouse ms)
    {
        if (interactor.nodeFx() == node)
            return;

        if (node == null)
            stopInteract();
        else if (node.isInteractable())
        {
            Log.debug(this, ".pleaseInteract - ms=" + ms + ", node=" + node);
            this.focus.updateSelection(node, ms != null && ms.hasCtrl());
            this.interactor.start(node, ms);
            node.commandBack();
            tab.startInteraction(node);
        }
    }

    public void reset()
    {
        focus.clear();
        interactor.stop();
        env.gui.board.reset(boardStyles);
    }

    public OCDPage needPage()
    {
        return page == null ? page = tab.page() : page;
    }

    public void update()
    {
        if (page != null)
        {
            update(page);
            refresh();
        }
    }

    public void update(OCDPage page)
    {
        FxDisplayProps display = display().update(env.insight.prefs);
        display().counter = 0;
        if (page == null)
            return;

        Log.debug(this, ".update - " + page.doc().fileName() + ", page " + page.number());

        reset();
        if (page != this.page)
            interactor.stop();

        this.page = page;

        boolean tooMuchPathToDisplay = false;
        if (page != null)
        {
            int nbOfPaths = 0;
            for (OCDPaintable node : page.content().zOrderedGraphics())
            {
                if (!display.doDisplay(node))
                    continue;
                if (!display.isCounterOK())
                    break;

                if (!node.isPath() || nbOfPaths++ < OCD.MAX_NB_OF_DISPLAY_PATHS)
                    board.addContentNode(fxNode(node, null));
                else
                    tooMuchPathToDisplay = true;
            }
            if (tooMuchPathToDisplay)
                Log.warn(this, ".update - too much path to display: " + nbOfPaths);
            tab.paged(page);
        }
        // never call refresh directly here since it is called by RibbonTab update
    }

    public void refresh()
    {
        display().update(env.insight.prefs);
        //not a good idea to clear meta here... MnistRibbon did not display meta anymore...
//    board.clearMeta(false);p
        refreshScale();
        // if (page != null)
        // this.canvas.setClip(new FxRectangle(page.viewBox()));
    }

    public void refreshNodes()
    {
        for (Node node : board.content.getChildren())
        {
            if (node instanceof FxBoxed)
                ((FxBoxed) node).refresh();
        }

        for (Node node : board.annots.getChildren())
        {
            if (node instanceof FxBoxed)
                ((FxBoxed) node).refresh();
        }
    }

    public void refreshScale()
    {
        interactor.refresh();
        board.resizeAndRescale(needPage());
    }

    public Rectangle3 bounds()
    {
        needPage();
        return (page == null ? new Rectangle3(0, 0, 595, 842) : page.viewBounds());
    }

    public FxRect viewClip()
    {
        return bounds().fx();
    }

    public FxOCDNode fxNode(OCDPaintable node, FxOCDNode parent)
    {
        FxOCDNode fx = createFxNode(node, parent);
        if (fx != null)
            fx.refresh();
        return fx;
    }

    public FxOCDNode createFxNode(OCDPaintable node, FxOCDNode parent)
    {
        switch (node.cast())
        {
            case "OCDPath":
                return new ISPath(this, node.asPath());
            case "OCDText":
                return new ISText(this, node.asText());
            case "OCDTextLine":
                return new ISTextLine(this, node.asTextLine());
            case "OCDTextBlock":
                return new ISTextBlock(this, node.asTextBlock());
            case "OCDImage":
                return new FxOCDImage(this, node.asImage());
            case "OCDContent":
                return new ISContent(this, node.asContent());
            case "OCDAnnot":
                return new ISAnnot(this, node.asAnnot());
            case "OCDFlow":
                return new ISTextFlow(this, node.asFlow());
            case "OCDTable":
                return new ISGroup(this, node.asTable());
        }
        return null;
    }

    public boolean interactor(FxMouse ms, boolean preventIfAlt)
    {
        if (!interactable)
            return false;

        boolean alt = preventIfAlt && ms.hasAlt();
        switch (ms.state())
        {
            case FxMouse.MOVE:
                interactor.mouseMove(ms);
                return true;
            case FxMouse.DRAG:
                interactor.mouseDrag(ms);
                return true;
            case FxMouse.DOWN:
                if ((focus.over == null || alt) || hasInteractor() && interactor.isInside(ms.xy()))
                    interactor.mouseDown(ms);
                else
                {
                    if (focus.over != null && !alt)
                        pleaseInteract(focus.over, ms);
                    else
                        interactor.mouseDown(ms);
                }
                return true;
            case FxMouse.CLICK:
                interactor.mouseClick(ms);
                return true;
            case FxMouse.UP:
                interactor.mouseUp(ms);
                tab.popup.clear();
                return true;
        }
        return false;
    }
}
