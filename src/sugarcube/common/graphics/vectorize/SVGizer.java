package sugarcube.common.graphics.vectorize;

import sugarcube.common.data.collections.Stringer;
import sugarcube.common.data.xml.Nb;

public class SVGizer extends Stringer
{
    public int decimals = 2;

    public SVGizer(int decimals)
    {
        this.decimals = decimals;
    }

    public String nb(float f)
    {
        return Nb.String(f, decimals);
    }

    public void openSvg(int width, int height, boolean isViewbox, String desc)
    {
        append(guil("<svg " + (isViewbox ? "viewBox='0 0 " + width + " " + height + "' " : "width='" + width + "' height='" + height + "' ")
                + "version='1.1' xmlns='http://www.w3.org/2000/svg' " + (desc == null ? "" : "desc='" + desc + "' ") + ">\n"));
    }

    public void closeSvg()
    {
        append("</svg>");
    }

    public void circle(float x, float y, float r, String fill, String stroke, float pen)
    {
        append(guil(
                "<circle cx='" + nb(x) + "' cy='" + nb(y) + "' r='" + r + " fill='" + fill + "' stroke-width='" + nb(pen) + "' stroke='" + stroke + "' />\n"));
    }

    public void line(float x1, float y1, float x2, float y2, String stroke, float pen)
    {
        append(guil("<line x1='" + nb(x1) + "' y1='" + nb(y1) + " x2='" + nb(x2) + "' y2='" + nb(y2) + "' stroke-width='" + nb(pen) + "' stroke='"
                + stroke + "' />\n"));
    }

    public void openPath(float x, float y, String desc, String fill, String stroke, float pen, float opacity)
    {
        append("<path ");
        appendIf(desc != null, guil("desc='" + desc + "' "));
        appendIf(fill != null, guil("fill='" + fill + "' "));
        appendIf(stroke != null, guil("stroke='" + stroke + "' "));
        append(guil("stroke-width='" + nb(pen) + "' "));
        append(guil("opacity='" + nb(opacity) + "' "));
        append(guil("d='"));
        moveTo(x, y);
    }

    public void closePath()
    {
        append(guil("Z' />\n"));
    }

    public void moveTo(float x, float y)
    {
        append("M " + nb(x) + " " + nb(y) + " ");
    }

    public void lineTo(float x, float y)
    {
        append("L " + nb(x) + " " + nb(y) + " ");
    }

    public void quadTo(float cpx, float cpy, float x2, float y2)
    {
        append("Q " + nb(cpx) + " " + nb(cpy) + " " + nb(x2) + " " + nb(y2) + " ");
    }

    public String rgb(byte[] c)
    {
        return guil("rgb(" + (c[0] + 128) + "," + (c[1] + 128) + "," + (c[2] + 128) + ")");
    }

    private String guil(String s)
    {
        return s.replace('\'', '"');
    }

}
