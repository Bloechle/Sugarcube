package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.system.io.File3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFDocument;
import sugarcube.formats.pdf.reader.pdf.node.font.aff.AFF;

import java.awt.*;

public class ReaderAFF extends FontReader
{

    private AFF aff;
    private int firstChar,lastChar;

    public ReaderAFF(FontDescriptor desc, AFF aff)
    {
        super("Type1Font", desc);// from SUN
        this.firstChar = desc.pdfFont.firstChar;
        this.lastChar = desc.pdfFont.lastChar;
        this.aff = aff;
    }

    @Override
    public Transform3 transform()
    {
        return new Transform3(aff.transform);
    }

    public float getWidth(char code, String name)
    {
        if ((firstChar == -1) || (lastChar == -1))
            return aff.getWidth(code, name);
        return (float) (0f + this.descriptor.pdfFont.widths.get((int) code));
    }

    @Override
    public PDFGlyph outline(String name, Unicodes uni, int code)
    {
        return aff.outline(name, uni, code);
    }

    @Override
    public String sticker()
    {
        return "ReaderAFF";
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        int size = (int) (48 * props.displayScaling);
        int x = size;
        int y = size;
        g.setFont(g.getFont().deriveFont(11f));
        g.setColor(Color.BLACK);
        for (String name : aff.name2outline.keySet())
        {
            if (name == null)
            {
                Log.debug(this, ".paint - name==null");
                continue;
            }

            float width = size * aff.name2width.get(name, new Point3(1000, 1000)).x / 1000f;
            // Log.debug(this, ".paint - " + this.fontname + ", name=" + name +
            // ", width=" + width);
            PDFGlyph glyph = aff.getOutline(name, width);
            if (glyph.path == null || glyph.path.isEmpty())
                continue;

            if (x > g.width() - size)
            {
                x = size;
                y += (3 * size / 2);
            }

            g.setColor(Color.BLACK);
            g.fill(new Transform3(1, 0, 0, -1, x, y).transform(glyph.path.scale(size)));
            g.setColor(Color.GREEN.darker());
            g.fill(new Circle3(x, y, 2));
            g.draw(new Line3(x, y, x + (int) width, y));
            g.drawCenter(name, x + width / 2, y + size / 3, g.getFont(), Color3.GREEN_DARK);
            x += 2 * size;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (String name : aff.name2outline.keySet())
        {
            String value;
            Object o = aff.name2outline.get(name);
            if (o == null)
                value = "null";
            else if (o instanceof byte[])
                value = "bytes";
            else
                value = new Rectangle3(((Path3) o).getBounds()).toString();
            sb.append(name).append(" ").append(value).append("\n");
        }
        return "ReaderAFF[" + descriptor.fontname + "]" + "\nMatrix[" + this.transform() + "]" + "\nNames[" + Zen.Array.String(aff.chr2name) + "]"
                + "\nFirstChar[" + this.firstChar + "]" + "\nLastChar[" + this.lastChar + "]" + "\n" + sb + "";
    }

    public static void main(String... args)
    {
        File3 file = File3.Desk("bug/AFF.pdf");
        PDFDocument pdf = new PDFDocument(file, PDFDocument.NOOP_CONSUMER);
    }
}
