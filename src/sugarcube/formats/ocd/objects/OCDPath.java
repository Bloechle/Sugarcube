package sugarcube.formats.ocd.objects;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.A;
import sugarcube.common.data.json.JsonMap;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.ui.fx.shapes.FxPath;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDPageContent.State;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

public class OCDPath extends OCDPaintableLeaf
{
    public static final String TAG = "path";

    protected Shape3 path = null;
    protected String name = null;

    public OCDPath()
    {
        super(TAG);
    }

    public OCDPath(OCDNode parent)
    {
        super(TAG, parent);
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
        State state = this.page().content(false).state();
        this.writeXmlID(xml);
        this.writeXmlName(xml);
        this.writeXmlClip(xml, state);
        this.writeXmlBlend(xml, state);
        this.writeXmlZOrder(xml, state);
        this.writeXmlTransform(xml, state);
        this.writeXmlFillAndStroke(xml, state);
        xml.write("name", name);
        xml.write("wind", xml.equals(wind(), state.WIND) ? null : (state.WIND = wind()));
        xml.write("d", OCD.path2xml(path(), xml.numberFormat()));
        this.props.writeAttributes(xml);
        return this.children();
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        State state = this.page().content(false).state();
        this.readXmlID(dom);
        this.readXmlName(dom);
        this.readXmlClip(dom, state);
        this.readXmlBlend(dom, state);
        this.readXmlZOrder(dom, state);
        this.readXmlTransform(dom, state);
        this.readXmlFillAndStroke(dom, state);

        if (dom.has("d"))
            this.path = OCD.xml2path(dom.value("d", ""), false);
        else
            this.path = OCD.xml2path(dom.cdata(), false);

        this.name = dom.value("name", null);
        setWind(state.WIND = dom.value("wind", state.WIND));// has to be after path
        // cdata reading
        this.props.readAttributes(dom);
    }

    public String wind()
    {
        return isNonZero() ? Path3.NONZERO : Path3.EVENODD;
    }

    public void setWind(int wind)
    {
        if (path instanceof Path2D)
            ((Path2D) path).setWindingRule(wind);
    }

    public void setWind(String wind)
    {
        setWind(wind.equalsIgnoreCase(Path3.EVENODD) ? Path3.WIND_EVEN_ODD : Path3.WIND_NON_ZERO);
    }

    public boolean isNonZero()
    {
        return !((path instanceof Path2D) && ((Path2D) path).getWindingRule() == Path2D.WIND_EVEN_ODD);
    }

    public void setPath(Shape3 path)
    {
        this.path = path;
    }

    public OCDPath path(Shape3 path)
    {
        this.setPath(path);
        return this;
    }

    /**
     * @return the original shape of the path, without applying the transformation
     * matrix.
     */
    @Override
    public Shape3 path()
    {
        return this.path;
    }

    public Path3 path3()
    {
        return this.path instanceof Path3 ? (Path3) path : new Path3(path);
    }

    @Override
    public Path3 shape()
    {
        return transform().transform(path());
    }

    public Path3 viewShape()
    {
        return this.viewTransform().transform(shape());
    }

    @Override
    public Rectangle3 bounds()
    {
        OCDClip clip = clip();
        Rectangle2D bounds = shape().getBounds2D();
        Rectangle3 box = clip == null ? new Rectangle3(bounds) : clip.bounds().createIntersection(bounds);
        float pen = stroke().pen();
        if (box.width < pen)
            box.width(pen);
        if (box.height < pen)
            box.height(pen);
        return box;
    }

    @Override
    public Rectangle3 bounds(boolean bbox)
    {
        return bounds();
    }

    public boolean contains(Rectangle2D r)
    {
        OCDClip clip = clip();
        if (clip != null && !clip.bounds().contains(r))
            return false;
        Shape3 shape = shape();
        if (!shape.getBounds2D().contains(r))
            return false;
        if (!shape.contains(r))
            return false;
        if (clip != null && !(clip.path()).contains(r))
            return false;
        return true;
    }

    public FxPath fx()
    {
        return fx(false);
    }

    public FxPath fx(boolean doHighlight)
    {
        FxPath fx = path3().fx();
        Transform3 tm = transform();
        if (doHighlight)
        {
            Stroke3.LINE.into(fx);
            fx.setStrokeWidth(1.0 / (Math.min(tm.sx(), tm.sy())));
            fx.setStroke(OCD.HIGHLIGHT_COLOR.alpha(0.5).fx());
        } else if (isStroked())
        {
            stroke().into(fx);
            fx.setStroke(strokeColor().fx());
        } else
            fx.setStroke(null);

        if (isFilled())
            fx.setFill(doHighlight ? OCD.HIGHLIGHT_COLOR.alpha(0.1).fx() : fillColor().fx());
        else
            fx.setFill(null);

        fx.getTransforms().add(tm.fx());
        return fx;
    }

    @Override
    public void setExtent(Line3 extent)
    {
        if (extent.width() < 1 || extent.height() < 1)
            return;
        this.path().setExtent(extent);
    }

    // @Override
    public OCDPath normalize()
    {
        this.clipID = OCDClip.ID_PAGE;
        this.path = transform().transform(path);
        this.setTransform(1, 0, 0, 1, 0, 0);
        return this;
    }

    @Override
    public String sticker()
    {
        String id = id();
        Rectangle3 r = this.bounds();
        return (int) r.x() + "," + (int) r.y() + " - " + (int) r.width() + "x" + (int) r.height() + "" + (id == null ? "" : " #" + id);
    }

    @Override
    public void paint(Graphics3 g, OCD.ViewProps props)
    {
        this.paintClip(g, props);
        this.paintBlend(g, props);
        if (props.paint_graphics)
        {
            g.setStroke(props.use_strokes ? stroke() : Stroke3.LINE);

            Transform3 otm = g.transform();

            g.concatTransform(transform());
            if (isFilled() || !props.use_alphas)
            {
                Color3 c = props.use_colors ? fillColor() : fillColor().grayColor();
                g.setColor(props.use_alphas ? c : c.alpha(1));
                g.fill(path());
            }
            if (isStroked() || !props.use_alphas)
            {
                g.setColor(props.use_colors ? strokeColor() : strokeColor().grayColor());
                g.draw(path());
            }
            g.setTransform(otm);
        }
    }

    public OCDPath copyTo(OCDPath node)
    {
        super.copyTo(node);
        node.path = path.copy();
        node.name = name;
        return node;
    }

    @Override
    public OCDPath copy()
    {
        return copyTo(new OCDPath(parent()));
    }


    public JsonMap toJson() {
        JsonMap json = new JsonMap(); // Start with inherited attributes

        json.put("class", "Path");
        json.put("type", "primitive");
        json.put("name", this.name);
        json.put("wind", this.wind());

        // Add path data
        if (this.path() != null) {
            json.put("d", OCD.path2xml(this.path(), null));
        }

        json.putValueIfNotZero("x", this.xCoord);
        json.putValueIfNotZero("y", this.yCoord);
        json.putValueIfNotZero("shearX", this.xShear);
        json.putValueIfNotZero("shearY", this.yShear);
        json.putValueIfNotOne("scaleX", this.xScale);
        json.putValueIfNotOne("scaleY", this.yScale);


        if (isFilled())
            json.put("fillColor", Color3.toHex(this.fillColor));

        if (isStroked()) {
            json.put("strokeColor", Color3.toHex(this.strokeColor));
            json.put("strokeWidth", this.stroke.width());
            json.put("strokeJoin", this.stroke.join());
            json.put("strokeCap", this.stroke.cap());
            json.put("strokeDash", A.String(this.stroke.dash()));
            json.putValueIfNotZero("strokePhase", this.stroke.phase());
        }

        json.put("clipId", this.clipID);

        return json;
    }

    @Override
    public String toString()
    {
        return "OCDPath[" + tag + "]" + "\nID[" + id() + "]" + "\nZOrder[" + this.zOrder + "]" + "\nClipID[" + clipID + "]" + "\nFill["
                + Zen.Array.String(fillColor().rgba()) + "]" + "\nStroke[" + Zen.Array.String(strokeColor().rgba()) + "]" + "\nDash["
                + Zen.Array.String(stroke().dash()) + "]" + "\nPhase[" + stroke().phase() + "]" + "\nPen[" + stroke().width() + "]" + "\nJoin["
                + stroke().join() + "]" + "\nCap[" + stroke().cap() + "]" + "\nWind[" + this.wind() + "]" + "\nTM[" + this.transform() + "]"
                // + "\nPath[" + this.path.stringValue(0.1f) + "]"
                // + "\nTmPath[" + this.shape().stringValue(0.1f) + "]"
                + "\n" + Xml.toString(this);
    }

}
