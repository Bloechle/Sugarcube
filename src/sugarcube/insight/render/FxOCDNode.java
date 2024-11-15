package sugarcube.insight.render;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Cmd;
import sugarcube.common.data.collections.Commands;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.interfaces.Visitor;
import sugarcube.common.system.io.hardware.Mouse;
import sugarcube.common.ui.fx.base.FxBoxed;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.event.FxInput;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.event.FxScroll;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.insight.core.FxDisplayProps;
import sugarcube.insight.core.FxEnvironment;
import sugarcube.insight.core.FxGUI;
import sugarcube.insight.core.IS;
import sugarcube.insight.interfaces.FxBoardHandler;
import sugarcube.insight.render.interaction.FxInteractable;
import sugarcube.insight.render.interaction.FxInteractor;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class FxOCDNode<T extends OCDPaintable> extends FxBoxed implements FxInteractable, FxBoardHandler, Unjammable
{
    public static final Color GLASS = Color3.GLASS.fx();
    public static final Color HIGH = IS.INTERACTOR_COLOR.a(0.5).fx();
    public static Color3 HIGH3 = IS.INTERACTOR_COLOR.alpha(0.5);
    public static Color3 GLASS3 = Color3.GLASS;

    // public static RadialGradient HIGH_GRAD = new RadialGradient(0, 0, 0.5, 0.5,
    // 1, true, CycleMethod.NO_CYCLE, new Stop[]
    // { new Stop(0, Color3.BLACK.a(0.2).fx()), new Stop(1,
    // Color3.WHITE.a(0.5).fx()) });
    //
    // public static RadialGradient SEL_GRAD = new RadialGradient(0, 0, 0.5, 0.5,
    // 1, true, CycleMethod.NO_CYCLE, new Stop[]
    // { new Stop(0, Color3.BLACK.a(0.3).fx()), new Stop(1,
    // Color3.WHITE.a(0.6).fx()) });

    public FxPager pager;
    public FxOCDNode parent;
    public T node;
    public String styles = "";
    public Line3 lastExtent = null;
    public Color focusColor = null;
    public boolean isHighlighted = false;
    public boolean isSelected = false;
    protected boolean isResizable = false;
    protected boolean isMovable = false;
    // public String mode = "";

    public FxOCDNode(FxPager pager)
    {
        this(pager, null);
    }

    public FxOCDNode(FxPager pager, T node, String... styles)
    {
        this.pager = pager;
        this.node = node;
        this.styles = "it-node";
        if (node != null)
            this.styles += " " + node.cast();
        for (String style : styles)
            this.styles += " " + style;
        this.styles = this.styles.trim();
        this.reset();
    }

    public FxDisplayProps display()
    {
        return pager.display();
    }

    public String cast()
    {
        return getClass().getSimpleName();
    }

    public void focusOnMouseOver()
    {
        if (pager.interactable)
        {
            focusColor = IS.ORANGE_LIGHT.fx();
            handle().mouseOverOut((ms, isOver) -> mouseEvent(ms));
        }
    }

    public void handleMouseEvents(boolean focusOnMouseOver)
    {
        super.handleMouseEvents();
        if (pager.interactable)
            focusColor = IS.ORANGE_LIGHT.fx();
    }

    public void mouseEvent(FxMouse ms)
    {
        if (focusColor != null && ms.isOverOrOut())
            pager.focus.updateOver(ms.isOver() ? this : null);
    }

    public boolean isResizable()
    {
        return isResizable;
    }

    public boolean isMovable()
    {
        return isMovable;
    }

    public boolean isNode()
    {
        return true;
    }

    public void clip(Shape clip)
    {
        if (clip == null)
        {
            Log.debug(this, ".clip - null: " + this.node);
            return;
        }
        FxDisplayProps display = display();
        clip.setStrokeWidth(0);
        this.setClip(display.clips ? clip : pager.viewClip());
        if (!display.clips && display.highlightClips)
        {
            clip.setStrokeWidth(1);
            clip.setStroke(Color3.GREEN.alpha(0.5).fx());
            clip.setFill(null);
            // clip.setFill(Color3.GREEN.alpha(0.1).fx());
            this.add(clip);
        }

    }

    public FxOCDNode visit(Visitor<FxOCDNode> visitor)
    {
        if (visitor.visit(this))
            return this;
        FxOCDNode fx;
        for (Node node : this.getChildren())
            if ((fx = Cast(node)) != null && (fx = fx.visit(visitor)) != null)
                return fx;
        return null;
    }

    public void reset()
    {
        this.style(styles);
    }

    @Override
    public FxOCDNode refresh()
    {
        if (this.focusColor != null)
            this.boxing();
        return this;
    }

    public Commands commands()
    {
        return pager == null ? null : pager.tab.commands;
    }

    public FxEnvironment env()
    {
        return pager == null ? null : pager.env;
    }

    public FxGUI gui()
    {
        return pager == null ? null : pager.env.gui;
    }

    public FxOCDNode add(FxOCDNode... nodes)
    {
        super.add(nodes);
        return this;
    }

    public FxRect boxing()
    {
        return boxing(bounds());
    }

    public FxRect boxing(boolean first)
    {
        return boxing(bounds(), first);
    }

    public FxRect boxingOCD()
    {
        return boxing(node != null ? node.bounds() : bounds());
    }

    public Rectangle3 bounds()
    {
        if (node != null)
            return node.bounds();

        Bounds b = boundsInLocalProperty().get();
        return new Rectangle3(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    public Transform3 transform()
    {
        return node != null && node.isPaintableLeaf() ? node.asLeaf().transform() : null;
    }

    @Override
    public Line3 extent()
    {
        // Log.debug(this, ".extent - "+node.extent());
        return node == null ? bounds().extent() : node.extent();
    }

    public void delete()
    {
        if (isNull())
            return;
        FxGroup parent = this.parentGroup();
        if (parent != null)
            parent.remove(this);
        OCDNode tmp = node;
        this.node = null;
        if (tmp != null)
            tmp.delete();
    }

    public boolean isNull()
    {
        return node == null;
    }

    public T paintable()
    {
        return node;
    }

    public boolean isAnnotNode()
    {
        return node != null && node.isAnnot();
    }

    public boolean isTextNode()
    {
        return node != null && node.isText();
    }

    public boolean isPathNode()
    {
        return node != null && node.isPath();
    }

    public boolean isImageNode()
    {
        return node != null && node.isImage();
    }

    public boolean isCell()
    {
        return node != null && node.isCell();
    }

    // public String mode()
    // {
    // return mode;
    // }
    //
    // public boolean isMode(String mode)
    // {
    // return Zen.equals(mode, this.mode);
    // }
    //
    // public boolean isModeEdit()
    // {
    // return isMode(Cmd.EDIT);
    // }

    @Override
    public boolean boardKeyEvent(FxKeyboard kb)
    {
        return false;
    }

    @Override
    public boolean boardMouseEvent(FxMouse ms)
    {
        if (!isNull())
        {
            Line3 extent = this.extent();
            switch (ms.state())
            {
                case Mouse.UP:
                    this.commandBack();
                    break;
                case Mouse.DOWN:
                    this.lastExtent = this.extent().copy();
                    break;
                case Mouse.CLICK:
                    // if (ms.clicks() > 1)
                    // this.mode = isModeEdit() ? "" : Cmd.EDIT;
                    break;
                case Mouse.MOVE:
                    break;
                case Mouse.DRAG:
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean boardInputEvent(FxInput in)
    {
        return false;
    }

    @Override
    public boolean boardScrollEvent(FxScroll sc)
    {
        return false;
    }

    public void command(Cmd cmd)
    {
        Log.debug(this, ".command - " + cmd + ", node=" + (node == null ? "null" : node.tag));
        if (node != null)
            node.command(cmd);
    }

    public void commandBack()
    {
    }

    public boolean isInteracting()
    {
        return this == pager.interactor.nodeFx();
    }

    @Override
    public boolean isInteractable()
    {
        return true;
    }

    @Override
    public void interacted(FxInteractor interactor)
    {
    }

    @Override
    public void dispose()
    {

    }

    public String toString()
    {
        return getClass().getSimpleName() + (node == null ? "" : " - " + node.toString());
    }

    public static FxOCDNode Cast(Object o)
    {
        return o instanceof FxOCDNode ? (FxOCDNode) o : null;
    }

}
