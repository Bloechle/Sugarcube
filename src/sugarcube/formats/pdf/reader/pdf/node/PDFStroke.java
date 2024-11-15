package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.A;
import sugarcube.common.graphics.Stroke3;

public final class PDFStroke extends PDFNode
{
    public enum CAP
    {
        BUTT("butt", 0), ROUND("round", 1), SQUARE("square", 2);
        public final String name;
        public final int id;

        CAP(String name, int id)
        {
            this.name = name;
            this.id = id;
        }
    }

    public enum JOIN
    {
        MITER("miter", 0), ROUND("round", 1), BEVEL("bevel", 2);
        public final String name;
        public final int id;

        JOIN(String name, int id)
        {
            this.name = name;
            this.id = id;
        }
    }

    private double lineWidth = 1.0;
    private double miterLimit = 10.0;
    private double dashOffset = 0.0;
    private double[] dashArray = new double[0];
    private CAP lineCap = CAP.BUTT;
    private JOIN lineJoin = JOIN.BEVEL;
    private String fillRule;

    public PDFStroke(PDFNode node)
    {
        super("LineStyle", node);
    }

    public PDFStroke(PDFNode node, double lineWidth)
    {
        super("LineStyle", node);
        this.lineWidth = lineWidth;
    }

    public PDFStroke scale(double scale)
    {
        PDFStroke style = copy();
        style.lineWidth *= (scale > 0 ? scale : -scale);
        return style;
    }

    public void setLineWidth(double lineWidth)
    {
        this.lineWidth = lineWidth;
    }

    public double getLineWidth()
    {
        return lineWidth;
    }

    public void setLineCap(CAP lineCap)
    {
        this.lineCap = lineCap;
    }

    public void setLineCap(int type)
    {
        this.lineCap = CAP.values()[type];
    }

    public CAP getLineCap()
    {
        return lineCap;
    }

    public void setLineJoin(JOIN lineJoin)
    {
        this.lineJoin = lineJoin;
    }

    public void setLineJoin(int type)
    {
        this.lineJoin = JOIN.values()[type];
    }

    public JOIN getLineJoin()
    {
        return lineJoin;
    }

    public void setMiterLimit(double miterLimit)
    {
        this.miterLimit = miterLimit;
    }

    public double getMiterLimit()
    {
        return miterLimit;
    }

    public void setDashValues(double[] dashArray, double dashOffset)
    {
        this.dashArray = dashArray;
        this.dashOffset = dashOffset;
        if (this.dashOffset < 0)
        {
            this.dashOffset = -this.dashOffset;
            Log.debug(this, ".setDashValues - negative phase value: " + dashOffset);
        }

    }

    public double[] getDashArray()
    {
        return dashArray;
    }

    public double getDashOffset()
    {
        return dashOffset;
    }

    @Override
    public String sticker()
    {
        return type + "[" + lineWidth + "]";
    }

    public Stroke3 stroke3()
    {
        if (dashOffset < 0)
        {
            dashOffset = -dashOffset;
            Log.debug(this, ".stroke3 - negative dash phase" + -dashOffset);
        }
        return new Stroke3(
                (float) lineWidth,
                lineCap.id,
                lineJoin.id,
                (float) miterLimit,
                (float) dashOffset,
                dashArray.length == 0 ? null : Zen.Array.toFloats(dashArray));
    }

    public Stroke3 stroke3(double scale)
    {
        if (scale < 0)
        {
            scale = -scale;
            Log.debug(this, ".stroke3 - negative scale" + scale);
        }
        return new Stroke3(
                (float) (scale * lineWidth),
                lineCap.id,
                lineJoin.id,
                (float) miterLimit,
                (float) dashOffset,
                dashArray.length == 0 ? null : Zen.Array.toFloats(dashArray));
    }

    public PDFStroke copy()
    {
        PDFStroke style = new PDFStroke(this);
        style.lineWidth = lineWidth;
        style.miterLimit = miterLimit;
        style.dashOffset = dashOffset;
        style.dashArray = Zen.Array.copy(dashArray);
        style.lineCap = lineCap;
        style.lineJoin = lineJoin;
        style.fillRule = fillRule;
        return style;
    }

    public boolean equals(PDFStroke stroke)
    {
        return stroke.lineWidth == lineWidth && stroke.miterLimit == miterLimit && stroke.dashOffset == dashOffset && A.equals(stroke.dashArray, dashArray);
    }

    @Override
    public String toString()
    {
        Stroke3 stroke = stroke3();
        return "\nLineWidth[" + lineWidth + "]"
                + "\nMiterLimit[" + miterLimit + "]"
                + "\nDashOffset[" + dashOffset + "]"
                + "\nDashArray[" + Zen.Array.String(dashArray) + "]"
                + "\nFillRule[" + fillRule + "]"
                + "\nLineCap[" + lineCap + "]" + stroke.cap()
                + "\nLineJoin[" + lineJoin + "]" + stroke.join();
    }
}
