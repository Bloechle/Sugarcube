package sugarcube.formats.ocd.objects;

import javafx.scene.shape.Shape;
import sugarcube.common.system.reflection.Annot._Bean;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Cmd;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.BlendComposite;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.formats.ocd.OCD;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Comparator;

public class OCDPaintableLeaf extends OCDPaintable
{
    protected Stroke3 stroke = null;
    @_Bean(name = "Coord x")
    protected float xCoord = 0;
    @_Bean(name = "Coord y")
    protected float yCoord = 0;
    @_Bean(name = "Shear x")
    protected float xShear = 0;
    @_Bean(name = "Shear y")
    protected float yShear = 0;
    @_Bean(name = "Scale x")
    protected float xScale = 1;
    @_Bean(name = "Scale y")
    protected float yScale = 1;
    protected int fillColor = Color3.BLACK.argb();
    protected int strokeColor = Color3.BLACK.argb();
    protected String clipID = OCDClip.ID_PAGE;
    protected String blendMode = BlendComposite.MODE_NORMAL;

    public OCDPaintableLeaf(String tag)
    {
        super(tag);
    }

    public OCDPaintableLeaf(String tag, OCDNode parent)
    {
        super(tag, parent);
    }

    public void shift(float dx, float dy)
    {
        xCoord += dx;
        yCoord += dy;
    }

    @Override
    public String autoID()
    {
        return this.pageID() + "-t" + this.tag + "-x" + (int) xCoord + "-y" + (int) yCoord + "-z" + (int) zOrder;
    }

    @Override
    public Cmd command(String key)
    {
        switch (key)
        {
            case CSS.Color:
                return new Cmd(key, fillColor());
            case CSS.BorderColor:
                return new Cmd(key, strokeColor());
            case CSS.BorderWidth:
                return new Cmd(key, strokeWidth());
            case CSS.Clipping:
                return new Cmd(key, clipID);
            default:
                return super.command(key);
        }

    }

    @Override
    public void command(Cmd cmd)
    {
        Log.debug(this, ".command - " + cmd);
        switch (cmd.key)
        {
            case CSS.Color:
                fillColor = cmd.color(fillColor()).getRGB();
                break;
            case CSS.BorderColor:
                strokeColor = cmd.color(strokeColor()).getRGB();
                break;
            case CSS.BorderWidth:
                this.setStrokeWidth(cmd.real(this.strokeWidth()));
                break;
            case CSS.BorderStyle:
                this.setStroke(Stroke3.get(this.strokeWidth(), cmd.string("none")));
                break;
            case CSS.Clipping:
                this.setClipID(cmd.string(OCDClip.ID_PAGE));
                break;
            default:
                super.command(cmd);
                break;
        }
    }

    public String clipID()
    {
        return clipID;
    }

    public void setClipID(String clipID)
    {
        this.clipID = clipID;
    }

    public OCDClip clip()
    {
        OCDPage page = page();
        return page == null ? null : page.defs().clip(clipID);
    }

    public Shape fxClip()
    {
        OCDClip clip = clip();
        if (clip == null || clip.path == null)
            return null;
        Path3 path = clip.path;

        Shape fx = null;
        if (path.isBBox(0.1))
            fx = path.bounds().fx();
        else
            fx = path.fx();

        fx.setFill(Color3.WHITE.fx());
        fx.setStroke(null);

        return fx;
    }

    public void unclip()
    {
        setClipID(OCDClip.ID_NONE);
    }

    public void setClip(OCDClip clip)
    {
        clipID = clip == null ? OCDClip.ID_PAGE : clip.id();
    }

    public boolean hasClip()
    {
        return Zen.hasData(clipID) && !clipID.equals(OCDClip.ID_PAGE);
    }

    public boolean isClipped()
    {
        Rectangle3 box = this.bounds();
        Rectangle3 clip = clip().bounds();
        if (clip.contains(box))
            return false;
        if (clip.intersects(box))
            return true;
        return true;
    }

    public String blendMode()
    {
        return blendMode;
    }

    public String svgBlendMode()
    {
        switch (blendMode == null ? "" : blendMode.toLowerCase())
        {
            case "softlight":
                return "soft-light";
        }
        return blendMode;
    }

    public void setBlendMode(String blendMode)
    {
        this.blendMode = blendMode == null ? BlendComposite.MODE_NORMAL : blendMode;
    }

    public boolean hasBlendMode()
    {
        return this.blendMode != null && !this.blendMode.equals(BlendComposite.MODE_NORMAL);
    }

    public Transform3 transform3()
    {
        return transform();
    }

    public Transform3 transform()
    {
        return new Transform3(xScale, yShear, xShear, yScale, xCoord, yCoord);
    }

    public void setTransform(AffineTransform tm)
    {
        this.xScale = (float) tm.getScaleX();
        this.yScale = (float) tm.getScaleY();
        this.xShear = (float) tm.getShearX();
        this.yShear = (float) tm.getShearY();
        this.xCoord = (float) tm.getTranslateX();
        this.yCoord = (float) tm.getTranslateY();
    }

    public void setTransform(double... transform)
    {
        this.setTransform(new Transform3(transform));
    }

    public void setTransform(float[] transform)
    {
        this.setTransform(new Transform3(transform));
    }

    public Point3 position()
    {
        return new Point3(xCoord, yCoord);
    }

    public float x()
    {
        return xCoord;
    }

    public float y()
    {
        return yCoord;
    }

    public void setX(double x)
    {
        this.xCoord = (float) x;
    }

    public void setY(double y)
    {
        this.yCoord = (float) y;
    }

    public void setXY(double x, double y)
    {
        this.xCoord = (float) x;
        this.yCoord = (float) y;
    }

    public void setXY(Point2D p)
    {
        this.setXY(p.getX(), p.getY());
    }

    public void setScale(double scale)
    {
        this.xScale = (float) scale;
        this.yScale = (float) scale;
    }

    public void setScale(double sx, double sy)
    {
        this.xScale = (float) sx;
        this.yScale = (float) sy;
    }

    public void setScaleX(double sx)
    {
        this.xScale = (float) sx;
    }

    public void setScaleY(double sy)
    {
        this.yScale = (float) sy;
    }

    public float sx()
    {
        return xScale;
    }

    public float scaleX()
    {
        return xScale;
    }

    public float sy()
    {
        return yScale;
    }

    public float scaleY()
    {
        return yScale;
    }

    public float shearX()
    {
        return xShear;
    }

    public float shearY()
    {
        return yShear;
    }

    public boolean isTransparent()
    {
        return fillColor().isTransparent() && (strokeWidth() < 0.000001 || strokeColor().isTransparent());
    }

    public boolean isHomogeneous(int distance)
    {
        return fillColor().equals(strokeColor(), distance) || strokeWidth() <= 0;
    }

    public OCDPaintableLeaf fillColor(Color color)
    {
        this.setFillColor(color);
        return this;
    }

    public Color3 fillColor()
    {
        return new Color3(fillColor);
    }

    public void setFillColor(Color color)
    {
        this.fillColor = color.getRGB();
    }

    public OCDPaintableLeaf strokeColor(Color color)
    {
        this.setStrokeColor(color);
        return this;
    }

    public Color3 strokeColor()
    {
        return new Color3(strokeColor);
    }

    public void setStrokeColor(Color color)
    {
        this.strokeColor = color.getRGB();
    }

    public Stroke3 stroke()
    {
        return stroke == null ? new Stroke3(0) : stroke;
    }

    public void setStroke(BasicStroke stroke)
    {
        this.stroke = stroke == null ? null : (stroke instanceof Stroke3 ? (Stroke3) stroke : new Stroke3(stroke));
    }

    public void setStrokeWidth(double width)
    {
        if (stroke == null)
            stroke = new Stroke3(width);
        else
            stroke = stroke.derive(width);
    }

    public OCDPaintableLeaf strokeWidth(double width)
    {
        this.setStrokeWidth(width);
        return this;
    }

    public float strokeWidth()
    {
        return stroke == null ? 0 : stroke.width();
    }

    public boolean isStrokedOrFilled()
    {
        return isStroked() || isFilled();
    }

    public boolean isStroked()
    {
        return (strokeColor >> 24 & 0xff) != 0 && stroke != null && stroke.width() > 0;
    }

    public boolean isFilled()
    {
        return (fillColor >> 24 & 0xff) != 0;
    }

    public boolean isOpaque()
    {
        return (fillColor >> 24 & 0xff) == 0xff;
    }

    protected void writeXmlScale(Xml xml, OCDPageContent.State state)
    {
        float sx = xScale;
        float sy = yScale;
        // Log.debug(this, ".writeXmlScale - sx="+sx+", sy="+sy);
        xml.write("scale", xml.equals(state.SCALE, sx, sy) ? null : xml.equals(sx, sy) ? Zen.Array.Floats(sx) : Zen.Array.Floats(sx, sy));
        state.SCALE = Zen.Array.Floats(sx, sy);
    }

    protected void writeXmlShear(Xml xml, OCDPageContent.State state)
    {
        float hx = xShear;
        float hy = yShear;
        xml.write("shear", xml.equals(state.SHEAR, hx, hy) ? null : xml.equals(hx, hy) ? Zen.Array.Floats(hx) : Zen.Array.Floats(hx, hy));
        state.SHEAR = Zen.Array.Floats(hx, hy);
    }

    protected void writeXmlXY(Xml xml, OCDPageContent.State state)
    {
        float x = xCoord;
        float y = yCoord;
        xml.write("x", xml.equals(x, state.X) ? Float.NaN : (state.X = x));
        xml.write("y", xml.equals(y, state.Y) ? Float.NaN : (state.Y = y));
    }

    protected void writeXmlTransform(Xml xml, OCDPageContent.State state)
    {
        this.writeXmlXY(xml, state);
        this.writeXmlScale(xml, state);
        this.writeXmlShear(xml, state);
    }

    protected void writeXmlFillcolor(Xml xml, OCDPageContent.State state)
    {
        xml.write("fill", xml.equals(fillColor, state.FILL) ? null : new Color3(state.FILL = fillColor).rgba());
    }

    protected void writeXmlStrokecolor(Xml xml, OCDPageContent.State state)
    {
        xml.write("stroke", xml.equals(strokeColor, state.STROKE) ? null : new Color3(state.STROKE = strokeColor).rgba());
    }

    protected void writeXmlStroke(Xml xml, OCDPageContent.State state)
    {
        float pen = stroke == null ? 0 : stroke.width();
        xml.write("pen", xml.equals(pen, state.PEN) ? Float.NaN : (state.PEN = pen));
        if (pen > 0)
        {
            xml.write("cap", xml.equals(stroke.cap(), state.CAP) ? null : (state.CAP = stroke.cap()));
            xml.write("join", xml.equals(stroke.join(), state.JOIN) ? null : (state.JOIN = stroke.join()));
            xml.write("dash", xml.equals(stroke.nanDash(), state.DASH) ? null : (state.DASH = stroke.nanDash()));
            xml.write("phase", xml.equals(stroke.phase(), state.PHASE) ? Float.NaN : (state.PHASE = stroke.phase()));
        }
        // xml.write("wind", xml.equals(stroke.wind, state.WIND) ? null :
        // (state.WIND = stroke.wind));
    }

    protected void writeXmlFillAndStroke(Xml xml, OCDPageContent.State state)
    {
        this.writeXmlFillcolor(xml, state);
        this.writeXmlStrokecolor(xml, state);
        this.writeXmlStroke(xml, state);
    }

    protected void writeXmlZOrder(Xml xml, OCDPageContent.State state)
    {
        xml.write("z", xml.equals(zOrder, state.Z) ? Float.NaN : (state.Z = zOrder));
    }

    protected void writeXmlClip(Xml xml, OCDPageContent.State state)
    {
        xml.write("clip", xml.equals(clipID, state.CLIP) ? null : (state.CLIP = clipID));
    }

    protected void writeXmlBlend(Xml xml, OCDPageContent.State state)
    {
        xml.write("blend", xml.equals(blendMode, state.BLEND) ? null : (state.BLEND = blendMode));
    }

    protected void readXmlTransform(DomNode dom, OCDPageContent.State state)
    {
        float[] tm = new float[6];
        float[] scale = dom.reals("scale", state.SCALE);
        float[] shear = dom.reals("shear", state.SHEAR);
        scale = state.SCALE = scale.length == 1 ? Zen.Array.Floats(scale[0], scale[0]) : scale;
        shear = state.SHEAR = shear.length == 1 ? Zen.Array.Floats(shear[0], shear[0]) : shear;
        tm[0] = scale[0];
        tm[1] = shear[1];
        tm[2] = shear[0];
        tm[3] = scale[1];
        tm[4] = state.X = dom.real("x", state.X);
        tm[5] = state.Y = dom.real("y", state.Y);
        this.setTransform(tm);
    }

    protected void readXmlFillcolor(DomNode dom, OCDPageContent.State state)
    {
        this.fillColor = state.FILL = (dom.has("fill") ? new Color3(dom.reals("fill")).argb() : state.FILL);
    }

    protected void readXmlStrokecolor(DomNode dom, OCDPageContent.State state)
    {
        this.strokeColor = state.STROKE = (dom.has("stroke") ? new Color3(dom.reals("stroke")).argb() : state.STROKE);
    }

    protected void readXmlStroke(DomNode dom, OCDPageContent.State state)
    {
        state.PEN = dom.real("pen", state.PEN);
        state.JOIN = dom.value("join", state.JOIN);
        state.CAP = dom.value("cap", state.CAP);
        state.PHASE = dom.real("phase", state.PHASE);
        state.DASH = dom.reals("dash", state.DASH);
        this.stroke = state.PEN <= 0 ? null : new Stroke3(state.PEN, state.CAP, state.JOIN, state.PHASE, state.DASH);
    }

    protected void readXmlFillAndStroke(DomNode dom, OCDPageContent.State state)
    {
        this.readXmlFillcolor(dom, state);
        this.readXmlStrokecolor(dom, state);
        this.readXmlStroke(dom, state);
    }

    protected void readXmlZOrder(DomNode dom, OCDPageContent.State state)
    {
        this.zOrder = state.Z = dom.real("z", state.Z);
    }

    protected void readXmlClip(DomNode dom, OCDPageContent.State state)
    {
        this.clipID = state.CLIP = dom.value("clip", state.CLIP);
    }

    protected void readXmlBlend(DomNode dom, OCDPageContent.State state)
    {
        this.blendMode = state.BLEND = dom.value("blend", state.BLEND);
    }

    public void paintClip(Graphics3 g, OCD.ViewProps props)
    {
        OCDClip clip = clip();
        if (clip != null)
            clip.paint(g, props);
        else
            g.setClip(null);
    }

    public void paintBlend(Graphics3 g, OCD.ViewProps props)
    {
        g.setComposite(1, blendMode);
    }

    @Override
    public Shape3 shape()
    {
        return transform().transform(path());
    }

    public void copyTo(OCDPaintableLeaf node)
    {
        super.copyTo(node);
        node.xCoord = xCoord;
        node.yCoord = yCoord;
        node.xScale = xScale;
        node.yScale = yScale;
        node.xShear = xShear;
        node.yShear = yShear;
        node.fillColor = fillColor;
        node.strokeColor = strokeColor;
        if (this.stroke != null)
            node.stroke = stroke.copy();
        node.clipID = clipID;
        node.blendMode = blendMode;
    }

    @Override
    public OCDPaintableLeaf canonize()
    {
        return this;
    }

    @Override
    public OCDPaintableLeaf uncanonize()
    {
        return this;
    }

    public static Comparator<OCDPaintableLeaf> xComparator()
    {
        return (o1, o2) -> o1.xCoord < o2.xCoord ? -1 : o1.xCoord > o2.xCoord ? 1 : 0;
    }

    public static Comparator<OCDPaintableLeaf> yComparator()
    {
        return (o1, o2) -> o1.yCoord < o2.yCoord ? -1 : o1.yCoord > o2.yCoord ? 1 : 0;
    }

    public static Comparator<OCDPaintableLeaf> yComparator_()
    {
        return (o1, o2) -> o1.yCoord < o2.yCoord ? 1 : o1.yCoord > o2.yCoord ? -1 : 0;
    }

}
