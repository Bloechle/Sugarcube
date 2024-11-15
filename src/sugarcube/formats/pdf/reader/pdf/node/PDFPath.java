package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.A;
import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.shade.PDFShading;
import sugarcube.formats.pdf.reader.pdf.node.shade.PatchShading;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.formats.pdf.reader.pdf.object.StreamLocator;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.List;

public class PDFPath extends PDFPaintable
{
    public final static int NO_OP = -1;
    public final static int STROKE = 1;
    public final static int FILL = 2;
    public final static int FILL_STROKE = FILL | STROKE;
    public final static int NON_ZERO_FILL = 4;
    public final static int EVEN_ODD_FILL = 0;
    public final static int NON_ZERO_FILL_STROKE = 7;
    public final static int CLOSE_PATH = 8;
    // used for backtracking (debugging)
    protected StreamLocator streamLocator;
    // used for path construction
    protected transient float x;
    protected transient float y;
    protected Path3 path;
    protected PDFStroke stroke;
    protected PDFColor strokeColor;
    protected PDFColor fillColor;
    protected PDFShading shading;
    protected PDFMatrix shadingTM;
    protected double shadingAlpha = 1.0;
    protected String[] blendModes = null;
    protected int clipRule = -1;
    protected boolean windEvenOddIntersection = false;
    protected boolean windNonZeroIntersection = false;
    protected int flag = -1;

    private PDFPath(PDFPath pdfPath)
    {
        super(Dexter.PATH, pdfPath.parent);
        this.streamLocator = pdfPath.streamLocator.copy();
        this.x = pdfPath.x;
        this.y = pdfPath.y;
        this.tm = pdfPath.tm.copy();
        this.path = new Path3(pdfPath.path);
        this.stroke = pdfPath.stroke.copy();
        this.strokeColor = pdfPath.strokeColor.copy();
        this.fillColor = pdfPath.fillColor.copy();
        this.shading = pdfPath.shading == null ? null : pdfPath.shading.copy();
        this.clipRule = pdfPath.clipRule;
    }

    public PDFPath(PDFContent content)
    {
        super(Dexter.PATH, content);
        this.path = new Path3();
        this.fillColor = new PDFColor(this);
        this.strokeColor = new PDFColor(this);
        this.shading = null;
        this.shadingTM = null;
    }

    public PDFPath(PDFClip clip, PDFShading shading, PDFMatrix shadingTM, double shadingAlpha)
    {
        super(Dexter.PATH, clip.parent);
        this.tm = new PDFMatrix();
        this.path = clip.path;
        this.x = path.x;
        this.y = path.y;
        this.stroke = new PDFStroke(this);
        this.strokeColor = new PDFColor(this, true);
        this.fillColor = new PDFColor(this, true);
        this.clipRule = clip.getClipRule();
        this.shading = shading;
        this.shadingTM = shadingTM;
        this.shadingAlpha = shadingAlpha;
    }

    public boolean hasIdenticalDrawingStateWith(PDFPath path)
    {
        if (flag != path.flag || shading != null || shadingTM != null)
            return false;
        if (!stroke.equals(path.stroke))
            return false;
        if (!strokeColor.equals(path.strokeColor))
            return false;
        if (!fillColor.equals(path.fillColor))
            return false;
        if (shadingAlpha != path.shadingAlpha)
            return false;
        if (!A.equals(blendModes, path.blendModes))
            return false;
        if (clipRule != path.clipRule)
            return false;
        if (windEvenOddIntersection != path.windEvenOddIntersection)
            return false;
        if (windNonZeroIntersection != path.windNonZeroIntersection)
            return false;
        if (!A.equals(tm.values(), path.tm.values()))
            return false;
        return true;
    }

    public boolean isInvisible()
    {
        if (this.path.isEmpty())
            return true;
        if (!hasFillColorPattern() && !hasShading() && !hasStrokeColorPattern() && fillColor.isTransparent() && strokeColor.isTransparent())
            return true;
        return false;
    }

    @Override
    public String blendMode()
    {
        return blendModes == null || blendModes.length == 0 ? null : blendModes[0];
    }

    public boolean hasBlendMode()
    {
        String blendMode = blendMode();
        return blendMode == null || blendMode.isEmpty() || blendMode.equalsIgnoreCase("normal");
    }

    public void setShadingTM(PDFMatrix shadingTM)
    {
        this.shadingTM = shadingTM;
    }

    public void setShadingAlpha(double shadingAlpha)
    {
        this.shadingAlpha = shadingAlpha;
    }

    public void setShading(PDFShading shading)
    {
        this.shading = shading;
    }

    public boolean hasShading()
    {
        return this.shading != null && this.shading.exists();
    }

    public boolean hasPatchShading()
    {
        return hasShading() && this.shading instanceof PatchShading;
    }

    public boolean hasFillColorPattern()
    {
        return this.fillColor != null && this.fillColor.isPattern();
    }

    public boolean hasFCPattern()
    {
        return hasFillColorPattern();
    }

    public boolean hasStrokeColorPattern()
    {
        return this.strokeColor != null && this.strokeColor.isPattern();
    }

    public boolean hasSCPattern()
    {
        return hasStrokeColorPattern();
    }

    public boolean hasFillColor()
    {
        return this.fillColor != null && !this.fillColor.isTransparent();
    }

    public boolean hasStrokeColor()
    {
        return this.strokeColor != null && !this.strokeColor.isTransparent();
    }

    @Override
    public StreamLocator streamLocator()
    {
        return streamLocator;
    }

    protected void toEvenOddClip()
    {
        this.path.setWindingRule(this.clipRule = GeneralPath.WIND_EVEN_ODD);
        this.windEvenOddIntersection = true;
    }

    protected void toNonZeroClip()
    {
        this.path.setWindingRule(this.clipRule = GeneralPath.WIND_NON_ZERO);
        this.windNonZeroIntersection = true;
    }

    public void closePath()
    {
        this.path.closePath();
    }

    public void addRectangle(List<PDFObject> params)
    {
        x = params.get(0).floatValue();
        y = params.get(1).floatValue();
        float oppositeX = params.get(2).floatValue() + x;
        float oppositeY = params.get(3).floatValue() + y;
        path.moveTo(x, y);
        path.lineTo(oppositeX, y);
        path.lineTo(oppositeX, oppositeY);
        path.lineTo(x, oppositeY);
        path.closePath();
    }

    public void moveTo(List<PDFObject> params)
    {
        if (streamLocator == null)
            streamLocator = params.get(0).streamLocator().copy();

        x = params.get(0).floatValue();
        y = params.get(1).floatValue();
        path.moveTo(x, y);
    }

    public void lineTo(List<PDFObject> params)
    {
        x = params.get(0).floatValue();
        y = params.get(1).floatValue();
        path.lineTo(x, y);
    }

    public void cubicCurveTo(List<PDFObject> params)
    {
        float[] p = new float[6];
        for (int i = 5; i >= 0; i--)
            p[i] = params.get(i).floatValue();
        path.curveTo(p[0], p[1], p[2], p[3], p[4], p[5]);
        x = p[4];
        y = p[5];
    }

    public void cubicCurveTo1(List<PDFObject> params)
    {
        float[] p = new float[4];
        for (int i = 3; i >= 0; i--)
            p[i] = params.get(i).floatValue();
        path.curveTo(x, y, p[0], p[1], p[2], p[3]);
        x = p[2];
        y = p[3];
    }

    public void cubicCurveTo2(List<PDFObject> params)
    {
        float[] p = new float[4];
        for (int i = 3; i >= 0; i--)
            p[i] = params.get(i).floatValue();
        path.curveTo(p[0], p[1], p[2], p[3], p[2], p[3]);
        x = p[2];
        y = p[3];
    }

    public PDFStroke getStyle()
    {
        return stroke;
    }

    public PDFNode finalize(PDFContent content, PDFInstr vop, int flag, PDFContext context)
    {
        PDFState state = content.state();
        if (this.streamLocator == null)
            this.streamLocator = vop.streamLocator().copy();
        else
            this.streamLocator.setEndPointer(vop.streamLocator().endPointer());

        this.flag = flag;
        this.strokeColor = (flag & STROKE) == STROKE ? state.strokeColor() : new PDFColor(this, true);
        this.fillColor = (flag & FILL) == FILL ? state.fillColor() : new PDFColor(this, true);

        // this.blendModes = Array3.copy(state.blendModes);
        // if (content.debug)
        // Log.debug(this, ".addClipOrPath - sm:" +sm);

        // if (state.shading != null)
        // {
        // this.setShading(state.shading);
        // this.setShadingTM(state.ctm);
        // }

        // this.shading = state.shading;

        if ((flag & CLOSE_PATH) == CLOSE_PATH)
            closePath();

        int fillRule = flag & NON_ZERO_FILL;

        if (fillRule == NON_ZERO_FILL || fillRule == NON_ZERO_FILL_STROKE)
            this.path.setWindingRule(GeneralPath.WIND_NON_ZERO);
        else
            this.path.setWindingRule(GeneralPath.WIND_EVEN_ODD);

        this.tm = state.ctm();
        this.stroke = state.getLineStyle().copy();

        if (content.baseFillAlpha < 1)
            this.fillColor.composeAlpha(content.baseFillAlpha);
        if (content.baseStrokeAlpha < 1)
            this.strokeColor.composeAlpha(content.baseStrokeAlpha);

        this.blendModes = content.blendModes(state.blendModes);

        this.marks.setAll(content.marks());
        PDFSoftMask sm = state.softClip;// only used with resource content (i.e. Do)
        // ?
        if (sm != null)
        {
            // Log.debug(this,
            // ".finalize - softMask: "+this.reference+", context="+context);
            return sm.clip(this, context);
        } else
            return this;
    }

    @Override
    public PDFPath instance(PDFContent content, PDFInstr instr, PDFContext context)
    {
        PDFState currentState = document().content().state();
        PDFPath copy = copy();
        // vp.strokeColor = currentState.strokeColor();
        // vp.fillColor = currentState.fillColor();
        copy.tm = tm.concat(currentState.ctm());
        // vp.style = currentState.getLineStyle();
        copy.streamLocator = streamLocator;
        return copy;
    }

    public PDFPath copy()
    {
        return new PDFPath(this);
    }

    public PDFColor getStrokeColor()
    {
        return strokeColor;
    }

    @Override
    public PDFColor getFillColor()
    {
        return fillColor;
    }

    public PDFShading shading()
    {
        return shading;
    }

    public PDFMatrix shadingTM()
    {
        return shadingTM;
    }

    @Override
    public String sticker()
    {
        String bip = "";
        if (this.hasPatchShading())
            bip += "PATCH ";
        return bip + id + " » " + type + "[" + path.wind() + "]";
    }

    @Override
    public String toString()
    {
        Rectangle r = path.getBounds();
        return type + (streamLocator() == null ? "" : streamLocator()) + "\nWind[" + path.getWindingRule() + "]" + "\nBounds[x" + r.x + " y" + r.y + " w"
                + r.width + " h" + r.height + "]" + "\nFillColor[" + (fillColor == null ? "null" : Zen.Array.String(fillColor.rgbaValues())) + "]"
                + "\nStrokeColor[" + (strokeColor == null ? "null" : Zen.Array.String(strokeColor.rgbaValues())) + "]" + "\nBlendModes["
                + Zen.Array.String(blendModes) + "]" + "\nShading[" + (shading != null && shading.exists() ? shading.getClass().getSimpleName() : "none")
                + "]" + "\nPattern[" + this.hasFCPattern() + "]" + stroke + "\nTM" + tm + "\nData[" + path.stringValue(0.001f) + "]" + "\nMarks" + this.marks;
    }

    public PDFMatrix matrix()
    {
        return tm;
    }

    public Path3 path()
    {
        return path;
    }

    @Override
    public Shape shape(double minX, double maxY)
    {
        return transform(minX, maxY).transform(path);
    }

    public Shape strokedShape(double minX, double maxY)
    {
        return transform(minX, maxY).transform(this.stroke.stroke3().createStrokedShape(path));
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        try
        {
            g.setComposite(1, this.blendModes);
            // if (blendModes != null)
            // Log.debug(this, ".paint - composite: " + Zen.A.toString(blendModes));
            if (props.displayGraphics)
            {
                Shape shape = shape(props.minX(), props.maxY());
                if (hasShading())
                {
                    // Log.debug(this, ".paint - shading: "+shading);
                    shading.setTransform(shadingTM);
                    shading.setAlpha(shadingAlpha);
                    g.setPaint(shading.paint(props));

                    if (path.isClosed())
                        g.fill(shape);
                    else
                        g.fill(g.bounds());
                } else if (this.hasFillColorPattern())
                {
                    // g.setClip(null);
                    try
                    {
                        g.setPaint(fillColor.colorSpace().toPattern().paint(props));
                        g.fill(shape);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        g.setColor(Color3.GREEN.alpha(0.5));
                        g.draw(shape);
                    }
                } else if (this.hasFillColor())
                {
                    g.setColor(props.enableColors ? fillColor.color() : Color3.BLACK);
                    g.fill(shape);
                }
                if (this.hasStrokeColorPattern())
                {
                    try
                    {
                        g.setStroke(stroke.scale(tm.getScaleX()).stroke3());
                        g.setPaint(strokeColor.colorSpace().toPattern().paint(props));
                        g.draw(shape);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        g.setColor(Color3.GREEN.alpha(0.5));
                        g.draw(shape);
                    }

                } else if (this.hasStrokeColor())
                {
                    g.setStroke(stroke.scale(tm.getScaleX()).stroke3());
                    g.setColor(props.enableColors ? strokeColor.color() : Color3.BLACK);
                    g.draw(shape);
                }

                if (props.highlightPaths)
                {
                    g.setStroke(2.0);
                    g.setColor(Color3.GREEN.alpha(0.5));
                    g.draw(shape);
                }
            }
        } catch (Exception e)
        {
            Log.warn(this, ".paint - OCD path rendering exception: " + e.getMessage());
            e.printStackTrace();
        }
        g.resetComposite(1);
    }
}
