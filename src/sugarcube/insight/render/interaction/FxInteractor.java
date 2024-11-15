package sugarcube.insight.render.interaction;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.fx.base.FxGroup;
import sugarcube.common.ui.fx.event.FxMouse;
import sugarcube.common.ui.fx.shapes.FxLine;
import sugarcube.common.ui.fx.shapes.FxRect;
import sugarcube.common.data.xml.Nb;
import sugarcube.insight.render.FxOCDNode;
import sugarcube.insight.render.FxPager;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.ocd.objects.lists.OCDPaintables;

import java.awt.geom.Rectangle2D;

public class FxInteractor extends FxGroup
{
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String MOVE = "move";
    public static final String RESIZE = "resize";

    protected boolean disabled = false;
    protected boolean isActive = false;
    protected String state = "";

    protected Line3 startExtent = null;
    protected Transform3 startTM = null;
    protected Line3 extent = new Line3();
    protected FxRect box = new FxRect();
    protected FxLine line = new FxLine();
    protected FxInteractorAnchor[] anchors = new FxInteractorAnchor[8];
    // -1 no selection, 0-8 anchor selected, > 8 box selected
    protected int selected = -1;
    protected float aspect = -1;// ratio
    protected Point3 drag = null;
    protected FxInteractable interactable;
    protected FxPager pager;
    protected float anchorRadius = 2;
    protected float lineThick = 0.5f;
    protected boolean isLineMode = false;
    protected FxMouse lastMouse = null;

    public FxInteractor(FxPager pager)
    {
        this.pager = pager;
        isActive = false;
        box.style("sc-interactor");
        line.style("sc-interactor");

        // transparent catches events (whereas no fill do not (i'm not sure... got
        // to check it))
        Color glass = Color3.TRANSPARENT.fx();
        Color black = Color3.BLACK.alpha(0.75).fx();
        Color white = Color3.WHITE.alpha(1).fx();

        for (int i = 0; i < anchors.length; i++)
            anchors[i] = new FxInteractorAnchor(i).stroke(black).fill(white);

        box.stroke(black).fill(glass);
        box.style("cursor-move");
        line.stroke(black);
        line.style("cursor-move");
        reset();
    }

    public boolean isResizable()
    {
        return interactable == null || interactable.isResizable();
    }

    public boolean isMovable()
    {
        return interactable == null || interactable.isMovable();
    }

    public FxOCDNode nodeFx()
    {
        return interactable == null ? null : interactable.node();
    }

    public OCDPaintable node()
    {
        FxOCDNode nodeFx = nodeFx();
        return nodeFx == null ? null : nodeFx.node;
    }

    public FxMouse mouse()
    {
        return lastMouse;
    }

    public boolean hasCtrl()
    {
        return lastMouse != null && lastMouse.hasCtrl();
    }

    public boolean hasShift()
    {
        return lastMouse != null && lastMouse.hasShift();
    }

    public boolean isInside(Point3 p)
    {
        return hoveringAnchor(p, false) > -1;
    }

    public FxInteractor fill(Color3 c)
    {
        box.fill(c);
        return this;
    }

    public FxInteractor setLineMode(boolean lineMode)
    {
        this.isLineMode = lineMode;
        return this;
    }

    public String state()
    {
        return state;
    }

    public void disable()
    {
        disabled = true;
    }

    public boolean isState(String state)
    {
        return Str.Equals(this.state, state);
    }

    public boolean isStart()
    {
        return isState(START);
    }

    public boolean isStop()
    {
        return isState(STOP);
    }

    public boolean isMoving()
    {
        return isState(MOVE);
    }

    public boolean isResizing()
    {
        return isState(RESIZE);
    }

    public boolean hasHandler()
    {
        return interactable != null;
    }

    public FxInteractor start(FxInteractable handler, FxMouse ms)
    {
        if (interactable != null)
            interactable.dispose();
        // start new interaction with handler
        interactable = handler;
        extent.setExtent(handler.extent());
        startExtent = extent.copy();
        startTM = handler.transform();

        if (ms != null)
            updateAnchor(ms);

        Log.debug(this, ".start - node=" + handler + ", extent=" + handler.extent() + ", this=" + this.extent + (ms == null ? "" : ", ms=" + ms.xy())
                + ", tm=" + startTM);
        start(ms);
        return this;
    }

    public void restart(Rectangle3 box)
    {
        // restart new interaction (no handler)
        stop();
        extent.setBounds(box);
        startExtent = extent.copy();
        start(null);
    }

    private void start(FxMouse ms)
    {
        lastMouse = ms;
        state = START;
        selected = ms == null ? 4 : hoveringAnchor(ms.xy(), true);
        update();
        boolean movable = this.isMovable();
        box.style(movable ? "cursor-move" : "cursor-hand");
        line.style(movable ? "cursor-move" : "cursor-hand");
        set(nodes());
        isActive = true;
        setVisible(true);
    }

    public void stop()
    {
        // Log.debug(this, ".stop - " + this.extent);
        state = STOP;
        update();
        if (interactable != null)
            interactable.dispose();
        interactable = null;
        clear();
        reset();
        pager.tab.startInteraction(null);
    }

    public void reset()
    {
        isActive = false;
        selected = -1;
        drag = null;
        setVisible(false);
    }

    public void deselect()
    {
        if (isInactive())
            reset();
        selected = -1;
    }

    public int hoveringAnchor(Point3 p, boolean starts)
    {
        if (disabled)
            return -1;
        if (isResizable())
            for (int i = 0; i < anchors.length; i++)
                if (anchors[i].contains(p))
                    return i;
        if ((starts || isActive()) && bounds().contains(p))
            return anchors.length;
        return -1;
    }

    public int updateAnchor(FxMouse mouse)
    {
        lastMouse = mouse;
        return updateAnchor(mouse.xy());
    }

    public int updateAnchor(Point3 p)
    {
        return selected = hoveringAnchor(p, false);
    }

    public void mouseDown(FxMouse ms)
    {
        Log.debug(this, ".mouseDown");
        if (disabled || !ms.isPrimaryBt())
            return;
        updateAnchor(ms);
        if (!hasHandler() && ms.isPrimaryBt() && selected < 0)
            isActive = false;

        if (!isActive && !ms.hasCtrlOrShift())
        {
            // starts new interaction (no handler)
            Point3 p = ms.xy();
            extent.setBounds(p.x, p.y, 0, 0);
            start(null);
        }
        drag = null;
    }

    public void mouseMove(FxMouse ms)
    {
        // Log.debug(this, ".mouseMove");
        if (disabled)
            return;
        updateAnchor(ms);
    }

    public void mouseDrag(FxMouse ms)
    {
        // Log.debug(this, ".mouseDrag");
        if (disabled || !ms.isPrimaryBt())
            return;
        lastMouse = ms;
        Line3 extent = extent();
        Point3 p = ms.xy();
        if (selected < 0)
            if (!isActive)
            {
                extent.setLine(p, p);
                isActive = true;
                drag = null;
            } else
            {
                // this.state = RESIZING;
                // extent.setP2(p.x, p.y);
            }
        else if (selected < anchors.length)
        {
            if (isResizable())
            {
                state = RESIZE;
                dragAnchor(selected, p);
            }
        } else
        {
            // Log.debug(this, ".mouseEvent - drag="+ms.dxy());
            if (isMovable())
            {
                state = MOVE;
                if (drag != null)
                    extent.move(p.sub(drag));
            }
            drag = p;
        }
        update();
    }

    public FxInteractor move(KeyCode code, float delta)
    {
        if (isInteracting())
            switch (code)
            {
                case UP:
                    return move(0, -delta);
                case DOWN:
                    return move(0, delta);
                case LEFT:
                    return move(-delta, 0);
                case RIGHT:
                    return move(delta, 0);
            }
        return this;
    }

    public FxInteractor move(float dx, float dy)
    {
        extent.move(dx, dy);
        update();
        return this;
    }

    public void mouseUp(FxMouse ms)
    {
        if (ms.isPrimaryBt())
        {
            if (disabled)
                return;
            lastMouse = ms;
            if (selected < 0 && isActive)
                pager.stopInteract();
            else
            {
                deselect();
                if (isVoid())
                {
                    reset();
                    clear();
                }
            }
            drag = null;
        }
    }

    public void mouseClick(FxMouse ms)
    {
        if (disabled || !ms.isPrimaryBt())
            return;
        lastMouse = ms;
    }

    public void dragAnchor(int index, Point3 p)
    {
        if (disabled)
            return;
        selected = index;
        Line3 l = extent();
        switch (index)
        {
            case 0:
                p = checkAspectRatio(p.x, p.y, l.x2, l.y2);
                l.setP1(p.x, p.y);
                break;
            case 1:
                l.setP1(l.x1, p.y);
                break;
            case 2:
                p = checkAspectRatio(p.x, p.y, l.x1, l.y2);
                l.setX2Y1(p.x, p.y);
                break;
            case 3:
                l.setP2(p.x, l.y2);
                break;
            case 4:
                p = checkAspectRatio(p.x, p.y, l.x1, l.y1);
                l.setP2(p.x, p.y);
                break;
            case 5:
                l.setP2(l.x2, p.y);
                break;
            case 6:
                p = checkAspectRatio(p.x, p.y, l.x2, l.y1);
                l.setX1Y2(p.x, p.y);
                break;
            case 7:
                l.setP1(p.x, l.y1);
                break;
        }
    }

    public void refresh()
    {
        float scale = 1 / (pager == null ? 1 : (float) pager.board.tm.sx());
        Line3 extent = extent();
        double thick = lineThick * scale;
        if (thick < 0.5)
            thick = 0.5;
        if (extent != null)
        {
            float r = anchorRadius * scale;
            if (r < 2)
                r = 2;

            float cx = extent.centerX();
            float cy = extent.centerY();
            anchors[0].set(extent.x1, extent.y1, r);
            anchors[1].set(cx, extent.y1, r);
            anchors[2].set(extent.x2, extent.y1, r);
            anchors[3].set(extent.x2, cy, r);
            anchors[4].set(extent.x2, extent.y2, r);
            anchors[5].set(cx, extent.y2, r);
            anchors[6].set(extent.x1, extent.y2, r);
            anchors[7].set(extent.x1, cy, r);
            for (int i = 0; i < anchors.length; i++)
                anchors[i].pen(r/4);
        }
        box.pen(thick);
        line.pen(thick);
        restyleAnchors();
    }

    public void restyleAnchors()
    {
        Line3 line = extent();
        if (line != null)
        {
            boolean ix = line.x1 > line.x2;
            boolean iy = line.y1 > line.y2;
            if (this.isLineMode)
            {
                anchors[0].restyleMove();
                anchors[4].restyleMove();
            } else
                for (int i = 0; i < anchors.length; i++)
                    anchors[i].restyle(ix, iy);
        }
    }

    public Node[] nodes()
    {
        if (this.isLineMode)
            return new Node[]
                    {line, anchors[0], anchors[4]};
        else if (isResizable())
            return new Node[]
                    {box, anchors[1], anchors[3], anchors[5], anchors[7], anchors[0], anchors[6], anchors[2], anchors[4]};
        else
            return new Node[]
                    {box};
    }

    public boolean hasActiveAnchor()
    {
        return isActive && selected > -1 && selected < anchors.length;
    }

    public boolean isSelected()
    {
        return isActive && selected == anchors.length;
    }

    public Line3 activeExtent()
    {
        return isInteracting() ? extent : null;
    }

    public Line3 extent()
    {
        return extent;
    }

    public Line3 startExtent()
    {
        return startExtent;
    }

    public Transform3 startTM()
    {
        return startTM;
    }

    public Rectangle3 bounds()
    {
        return extent.bounds();
    }

    public FxInteractor update(Rectangle3 box)
    {
        extent.setBounds(box);
        return update();
    }

    public FxInteractor update(double x, double y, double w, double h)
    {
        extent.setBounds(x, y, w, h);
        return update();
    }

    public FxInteractor update(String x, String y, String w, String h)
    {
        return update(real(x, extent.minX()), real(y, extent.minY()), real(w, extent.width()), real(h, extent.height()));
    }

    public double real(String v, double def)
    {
        return Nb.Double(v, def);
    }

    public void setActive(boolean active)
    {
        isActive = active;
    }

    public boolean isVoid()
    {
        return box.width() < 8 && box.height() < 8 || disabled;
    }

    public boolean hasSelection()
    {
        return isActive && !isVoid();
    }

    public boolean isInteracting()
    {
        return isActive && bounds().area() > 0;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public boolean isInactive()
    {
        return !isActive();
    }

    public boolean intersects(Rectangle2D r)
    {
        return isInactive() ? false : bounds().intersects(r);
    }

    public boolean contains(Rectangle2D r)
    {
        return isInactive() ? false : bounds().contains(r);
    }

    public boolean includes(Rectangle2D r)
    {
        if (this.isInactive())
            return false;
        Rectangle3 box = bounds();
        if (box.contains(r))
            return true;
        return box.overlapThat(r instanceof Rectangle3 ? (Rectangle3) r : new Rectangle3(r)) > 0.5;
    }

    public OCDPaintables<OCDTextBlock> blocks()
    {
        OCDPaintables<OCDTextBlock> blocks = new OCDPaintables<>();
        for (OCDTextBlock block : pager.page.content().blocks())
            if (includes(block.bounds()))
                blocks.add(block);
        return blocks;
    }

    private Point3 checkAspectRatio(float x, float y, float ox, float oy)
    {
        Point3 p = new Point3(x, y);
        if (aspect > 0)
        {
            if (Math.abs((x - ox) / (y - oy)) > aspect)
                p.x = ox + (x > ox ? 1 : -1) * Math.abs(y - oy) * aspect;
            else
                p.y = oy + (y > oy ? 1 : -1) * Math.abs(x - ox) / aspect;
            return p;
        }
        return p;
    }

    public FxInteractor updateSilently(Line3 extent)
    {
        this.extent.set(extent);
        box.set(extent().bounds());
        line.set(extent());
        refresh();
        return this;
    }

    public FxInteractor update()
    {
        box.set(extent().bounds());
        line.set(extent());
        refresh();
        // Log.debug(this, ".update - extent="+this.extent);
        if (interactable != null && isActive)
            interactable.interacted(this);

        pager.tab.interact(this);
        return this;
    }

}
