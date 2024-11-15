package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.A;
import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.Color3;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFColorSpace;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

/**
 * PDFColor represents a color with an array of double values, 4 values
 * representing srgb data, from 0.0 to 1.0.
 */
public class PDFColor extends PDFNode
{
    private PDFFunction[] fct = null;
    private PDFColorSpace cs = null;
    private float[] components = null;
    private float[] rgb = null;
    private float alpha = 1;
    private String name = null;

    public PDFColor(PDFNode parent)
    {
        this(parent, false);
    }

    public PDFColor(PDFNode parent, boolean transparent)
    {
        super("Color", parent);
        this.rgb = new float[]
                {0, 0, 0};
        this.alpha = transparent ? 0f : 1f;
    }

    public PDFColor(PDFContent content, boolean isStroke, List3<PDFObject> params)
    {
        super("Color", content);

        if (params.last().isPDFName())
        {
            this.name = params.last().toPDFName().stringValue();
            this.components = new float[params.size() - 1];
        } else
            this.components = new float[params.size()];

        int index = 0;
        for (PDFObject pdfObject : params)
            if (index < this.components.length)
                this.components[index++] = pdfObject.toPDFNumber().floatValue(0f);

        // Log.debug(this, " - components: "+Zen.A.toString(components));

        PDFState state = content.state();
        this.fct = state.TR;

        this.cs = isStroke ? state.strokeColorSpace() : state.fillColorSpace();

        // Log.debug(this, " - color: "+name);
        if (this.cs.isPattern() && name != null)
        {
            // Pattern was set as a empty pattern flag, now it has to be really
            // instanciated !
            this.cs = content.resources().getPattern(name);
            // Log.debug(this, " - pattern: " + params + ", cs=" + cs.defaultColor());
            if (components.length == 0)
                this.rgb = cs.defaultColor().rgb();
        }

        if (components.length != 0)
        {
            rgb = function(cs == null ? components : cs.toRGB(components), fct);

            // if (components.length == 1 && Math.round(components[0] * 10) == 5)
            // Log.debug(this, " - cs=" + cs + ", components=" +
            // Zen.A.toString(components) + ", params=" + params + ", rgb=" +
            // Zen.A.toString(rgb));
            for (int i = 0; i < rgb.length; i++)
                if (rgb[i] < 0)
                    rgb[i] = 0;
                else if (rgb[i] > 1)
                    rgb[i] = 1;
        }
    }

    public static float[] function(float[] inputs, PDFFunction... functions)
    {
        if (functions == null || functions.length == 0)
            return inputs;

        inputs = Zen.Array.copy(inputs);

        if (functions.length == 1 && functions[0].inputSize() == inputs.length)
            return functions[0].transform(inputs);

        if (functions.length == 1)
            for (int i = 0; i < inputs.length; i++)
                inputs[i] = functions[0].transform(inputs[i])[0];
        else if (functions.length == inputs.length)
            for (int i = 0; i < inputs.length; i++)
                inputs[i] = functions[i].transform(inputs[i])[0];
        else
        {
            for (PDFFunction fct : functions)
                if (!fct.isIdentity())
                {
                    Log.warn(PDFColor.class, ".function - wrong number of functions: " + functions.length + ", inputs=" + inputs.length);
                    break;
                }
            // Log.stacktrace(PDFColor.class, ".funcion - wrong number of functions: "
            // + functions.length+", inputs="+inputs.length);
        }
        return inputs;
    }

    public int rgbValue()
    {
        int[] a = rgbValues();
        return ((255 & 0xFF) << 24) | ((a[0] & 0xFF) << 16) | ((a[1] & 0xFF) << 8) | (a[2] & 0xFF);
    }

    public int argbValue()
    {
        int[] a = rgbaValues();
        return ((a[3] & 0xFF) << 24) | ((a[0] & 0xFF) << 16) | ((a[1] & 0xFF) << 8) | (a[2] & 0xFF);
    }

    public Color3 colorValue()
    {
        return new Color3(argbValue());
    }

    public int[] rgbValues()
    {
        return new int[]
                {(int) (rgb[0] * 255 + 0.5), (int) (rgb[1] * 255 + 0.5), (int) (rgb[2] * 255 + 0.5),};
    }

    public int[] rgbaValues()
    {
        return new int[]
                {(int) (rgb[0] * 255 + 0.5), (int) (rgb[1] * 255 + 0.5), (int) (rgb[2] * 255 + 0.5), (int) (alpha * 255 + 0.5)};
    }

    public int[] rgbaValues(int alpha)
    {
        return new int[]
                {(int) (rgb[0] * 255 + 0.5), (int) (rgb[1] * 255 + 0.5), (int) (rgb[2] * 255 + 0.5), (int) (alpha * 255 + 0.5)};
    }

    public PDFColor copy()
    {
        PDFColor vc = new PDFColor(this.parent);
        vc.cs = this.cs;
        vc.fct = this.fct;
        vc.rgb = this.rgb;
        vc.alpha = this.alpha;
        vc.components = this.components;
        return vc;
    }

    public PDFColor setAlpha(double alpha)
    {
        this.alpha = (float) alpha;
        return this;
    }

    public void composeAlpha(double alpha)
    {
        if (alpha < 1)
            this.alpha *= alpha;
    }

    public float alpha()
    {
        return this.alpha;
    }

    public boolean isPattern()
    {
        return this.cs != null && this.cs.isPattern();
    }

    public boolean isTransparent()
    {
        return this.alpha < 0.001;
    }

    public PDFColorSpace colorSpace()
    {
        return this.cs;
    }

    public Color3 color()
    {
        return new Color3(rgb, alpha);
    }

    @Override
    public String sticker()
    {
        return "Color";
    }

    public boolean equals(PDFColor stroke)
    {
        return stroke.alpha == alpha && A.equals(stroke.rgb, rgb);
    }

    @Override
    public String toString()
    {
        return "PDFColor[" + (cs == null ? "null" : cs.name()) + "]" + "\nRGBA[" + Zen.Array.String(rgbaValues()) + "]"
                + (components == null ? "" : "\nXYZ[" + Zen.Array.String(this.components) + "]") + (cs == null ? "" : "\nColorSpace[" + cs.reference + "]")
                + "\nFunction[" + fct + "]";
    }
}
