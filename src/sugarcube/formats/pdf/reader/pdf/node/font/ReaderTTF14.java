package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Circle3;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.ui.gui.Font3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFDocument;
import sugarcube.formats.pdf.reader.pdf.node.font.ttf.TTF;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;
import sugarcube.formats.pdf.resources.fonts.FONTS;

import java.awt.*;

public class ReaderTTF14 extends FontReader
{

    private ReaderTTF ttf = null;
    private Font3 font3 = null;

    public ReaderTTF14(FontDescriptor desc, PDFStream stream)
    {
        super("Reader14", desc);
        // used as TTF replacement when ReaderTTF fails
        this.font3 = Font3.Load(Font3.TRUETYPE_FONT, stream.inputStream());
    }

    public ReaderTTF14(FontDescriptor desc)
    {
        super("Reader14", desc);
        // non unicode fonts are not that friendly
        // Log.debug(this, " - "+fontname);
        switch (desc.fontname.toLowerCase())
        {
            case "symbol":
                this.ttf = new ReaderTTF(desc, TTF.ParseFont(FONTS.Bytes("Symbol")));
                break;
            case "webdings":
                this.ttf = new ReaderTTF(desc, TTF.ParseFont(FONTS.Bytes("Webdings")));
                break;
            case "zapfdingbats":
                this.ttf = new ReaderTTF(desc, TTF.ParseFont(FONTS.Bytes("ZapfDingbats")));
                break;
            default:
                this.font3 = FONTS.Load(desc.fontname);
                if (font3.replacement() != null)
                {
                    PDFDocument doc = this.descriptor.document();
                    if (doc != null)
                        doc.warnings.add("Non embedded fonts (Reader14_OS): ", desc.fontname);
                }
                break;
        }
    }

    @Override
    public Transform3 transform()
    {
        return new Transform3();
    }

    @Override
    public String sticker()
    {
        return "Reader14";
    }

    @Override
    public PDFGlyph outline(String name, Unicodes unicode, int code)
    {
        // Log.debug(this,
        // ".outline - "+this.fontname+": "+name+", "+unicode.string()+", "+code+(ttf!=null
        // ? ", ReaderTTF=true" : ""));
        if (ttf == null)
        {
            PDFGlyph outline = outline(name, unicode, code, font3, null);

            // if font not embedded in PDF, we scale each replacement font glyph to
            // fit original glyph widths
            if (font3.replacement() != null)
            {
                Double width = this.descriptor.pdfFont.widths.get(code, null);
                if (width != null)
                {
                    // Log.debug(this,
                    // ".outline - name="+name+", width="+width+", adance="+outline.advance.x);
                    double sx = width.doubleValue() / (outline.advance.x * 1000);
                    outline.setWidth(width.doubleValue());
                    outline.setPath(outline.path.scaleX(sx));
                }
            }
            return outline;
        } else
        {
            return ttf.outline(name, unicode, code, true);
        }
    }

    public static PDFGlyph outline(String name, Unicodes uni, int code, Font3 font3, Transform3 transform)
    {
        PDFGlyph outline = new PDFGlyph();
        String text = uni == null ? "" + (char) code : uni.string();

        if (text != null && font3 != null && font3.canDisplayUpTo(text) == -1)
        {
            outline.path = font3.glyph(text);
            outline.advance = font3.advance(text);
            if (transform != null)
            {
                outline.path = transform.transform(outline.path);
                outline.advance = transform.transform(outline.advance);
            }

        } else
            Log.debug(ReaderTTF14.class, ".outline - canDisplayUpTo " + text + ": " + (font3 == null ? "null" : font3.getName()) + ", " + name + ", "
                    + code);
        return outline;
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        if (ttf != null)
            ttf.paint(g, props);
        else
        {
            float scale = props.displayScaling;
            int size = (int) (48 * scale);
            int d = size / 2;
            int x = d;
            int y = size + d;

            g.setColor(Color.BLACK);
            for (int code = 0; code < 256; code++)
            {
                if (x > g.width() - (size + size / 2))
                {
                    x = d;
                    y += (d + size);
                }

                PDFGlyph glyph = this.outline(".notdef", null, code);
                if (glyph == null)
                    continue;
                // System.out.println("font="+this.fontname+" glyph="+code+" path="+glyph.path.stringValue(0));
                // XED.LOG.warn(this,".paint - empty glyph: fontname="+fontname+" code="+code+" path="+glyph.path.stringValue(0.1f));
                g.setColor(Color.BLACK);
                g.fill(new Transform3(size, 0, 0, size, x, y).transform(glyph.path));
                g.setColor(Color.GREEN.darker());
                g.fill(new Circle3(x, y, 2));
                g.draw(new Line3(x, y, x + (int) (glyph.width()), y));
                g.graphics().drawString((char) code + "[" + code + "]", x, y + size / 2);
                x += (d + 3 * size / 2);
            }
        }
    }
}
