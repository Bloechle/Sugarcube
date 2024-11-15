package sugarcube.formats.pdf.reader;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.*;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.time.DateUtils;
import sugarcube.formats.pdf.reader.pdf.node.*;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFont;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFRectangle;
import sugarcube.formats.pdf.reader.pdf.object.PDFTrailer;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.ocd.objects.document.OCDMetadata;
import sugarcube.formats.ocd.objects.metadata.dc.DC;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class PDF2OCD
{
    public static Rectangle3 Rect(PDFRectangle box, PDFPage page, double dx, double dy)
    {
        return Rect(box.rectangle(), page).shift(dx, dy);
    }

    public static Rectangle3 Rect(Rectangle3 box, PDFPage page, double dx, double dy)
    {
        return Rect(box, page).shift(dx, dy);
    }

    public static Rectangle3 Rect(Rectangle3 box, PDFPage page)
    {
        return new Rectangle3(Path(new Path3(box), page));
    }

    public static Path3 Path(Path3 path, PDFPage page)
    {
        Rectangle3 box = page.mediaBox();
        path = path.reverseY().transform(Transform3.translateInstance(0, box.height() + box.y()));
        return path;
    }

    public static void PopulateMetadata(PDFDocument pdf, OCDDocument ocd)
    {
        OCDMetadata meta = ocd.metadata();
        PDFTrailer trailer = pdf.trailer();
        if (trailer.hasInfo())
        {
            PDFDictionary dico = trailer.getInfo();
            String title = dico.get("Title").xmlStringValue(File3.Filename(pdf.fileName(), true));
            String author = dico.get("Author", "Creator", "Producer").xmlStringValue("Sugarcube Author");
            String subject = dico.get("Subject").xmlStringValue("");
            String keywords = dico.get("Keywords").xmlStringValue("");
            String timestamp = dico.get("CreationDate", "ModDate").xmlStringValue(DateUtils.universalTime());
            meta.add(DC.title, title);
            meta.add(DC.creator, author);
            meta.add(DC.subject, subject);
            meta.add(DC.description, keywords);
            meta.add(DC.date, timestamp);
        }
        meta.complete();
    }


    public static OCDClip AddClip(OCDContent ocdContent, PDFClip pdfClip, PDFContent content, PDFPage pdfPage, OCDClip parentClip, Set3<OCDClip> clips)
    {
        Rectangle3 box = pdfPage.bounds();

        Path3 path = pdfClip != null && pdfClip.doClip() ? pdfClip.path(box) : new Path3(ocdContent.page().bounds());

        // PDF xObject clipping is done here (not visible in PDFInspector)
        Area area = null;
        PDFContent c = content;
        do
        {
            if (c.baseClip != null)
                (area == null ? (area = new Area(path)) : area).intersect(new Area(c.baseClip.path(box)));
        } while ((c = c.parentContent) != null);

        OCDClip clip = new OCDClip(ocdContent, area != null ? new Path3(area) : path, false);
        return AddClip(ocdContent.page(), clip, clips);
    }

    private static OCDClip AddClip(OCDPage ocdPage, OCDClip clip, Set3<OCDClip> clips)
    {
        for (OCDClip c : clips)
            if (clip.equals(c))
                return c;

        if (clip.path().isBBox(0.001) && clip.path().bounds().equals(ocdPage.bounds(), 0.001))
            return ocdPage.defs().clip(OCDClip.ID_PAGE);

        clip.setID(Dexter.NewClipID(ocdPage));
        ocdPage.defs().addDefinition(clip);
        clips.add(clip);
        return clip;
    }


    public static OCDText NewText(OCDContent ocdContent, PDFText pdfText, PDFPage pdfPage, PDF2SVGFont refont)
    {
        Rectangle3 box = pdfPage.mediaBox();
        OCDText text = new OCDText(ocdContent);
        PDFFont pdfFont = pdfText.getFont();
        if (pdfFont == null)
            text.setFont("Calibri");
        else
        {
            text.setFont(pdfFont.ocdFontname());
            if (pdfFont.isVerticalMode())
                text.setMode(OCDText.MODE_TTB);
        }
        Unicodes pdfCodes = pdfText.codes();
        Unicodes unicodes = new Unicodes();
        String ocdFontname = pdfFont == null ? text.fontname() : pdfFont.ocdFontname();

        for (int i = 0; i < pdfCodes.length(); i++)
        {
            int c = pdfCodes.codeAt(i);
            unicodes.append(refont.remap(ocdFontname + "#c" + c, c));
        }

        text.setUnicodes(unicodes.ints());
        Transform3 tm = pdfText.tm().reverse().transform().toOrigin();
        text.setFontsize(pdfText.fontSize());
        text.setTransform(tm);

        // int isx = (int)Math.round(tm.sx() * 10);
        // int isy = (int)Math.round(tm.sy() * 10);
        // if (isx!=isy)
        // {
        // Log.debug(this,
        // ".newText - " + unicodes.string() + ": sx=" + isx/10f + ", sy=" + isy/10f
        // + ", page=" + pdfPage.number());
        // }

        text.setFillColor(pdfText.fillColor());
        text.setStrokeColor(pdfText.strokeColor());
        // todo: check when sw and sh are different...
        text.setStroke(pdfText.getStroke().stroke3(1 / Math.abs(tm.scaleWidth())));
        text.updateCS(pdfText.deviceCoords(box));
        text.setBlendMode(Str.Lower(pdfText.blendMode()));
        return text;
    }

    public static OCDPath AddPath(OCDContent ocdContent, PDFPath pdfPath, PDFPage pdfPage)
    {
        OCDPath path = new OCDPath(ocdContent);
        Rectangle3 box = pdfPage.mediaBox();
        Transform3 otm = pdfPath.transform();
        Path3 path3 = pdfPath.path().reverseY();
        Transform3 tm = new Transform3(otm.sx(), -otm.hy(), -otm.hx(), otm.sy(), 0, 0);
        path.setTransform(tm.fsx(), tm.fhy(), tm.fhx(), tm.fsy(), otm.floatX() - box.minX(), box.maxY() - otm.floatY());
        path.setPath(path3);
        Color3 fc = pdfPath.getFillColor().color();
        Color3 sc = pdfPath.getStrokeColor().color();
        path.setFillColor(pdfPath.hasFCPattern() || pdfPath.hasShading() ? Color3.TRANSPARENT : fc);
        path.setStrokeColor(pdfPath.hasSCPattern() ? Color3.TRANSPARENT : sc);
        path.setStroke(pdfPath.hasSCPattern() ? Stroke3.NONE : pdfPath.getStyle().stroke3());
        path.setBlendMode(Str.Lower(pdfPath.blendMode()));
        ocdContent.add(path);
        return path;
    }


    public static OCDImage AddImage(OCDContent ocdContent, PDFImage pdfImage, PDFPage pdfPage, double imageQuality)
    {
        // debug(".addImage - width=" + pdfImage.width() + ", height=" +
        // pdfImage.height());
        BufferedImage bi = pdfImage.image();
        if (Image3.isFullyTransparent(bi))
            return null;
        OCDImage image = new OCDImage(ocdContent);

//        if (props.imageGray && bi.getType() != BufferedImage.TYPE_INT_ARGB)
//        {
//            Image3 grayImage = new Image3(bi.getWidth(), bi.getHeight(), Image3.Type.GRAY);
//            Graphics g = grayImage.getGraphics();
//            g.drawImage(bi, 0, 0, null);
//            g.dispose();
//            bi = grayImage;
//        }

        image.setImage(bi, imageQuality);
        Rectangle3 box = pdfPage.mediaBox();
        image.setTransform(pdfImage.concat().reverse(box.minX(), box.maxY()).transform());
        image.setBlendMode(Str.Lower(pdfImage.blendMode()));

        ocdContent.add(image);
        ocdContent.doc().imageHandler.addEntry(image);
        return image;
    }


    public static OCDImage AddStrokePattern(OCDContent ocdContent, PDFPath pdfPath, PDFPage pdfPage, OCDClip clip, PDFDisplayProps displayProps, double imageQuality)
    {
        try
        {
            OCDPage page = ocdContent.page();

            double minX = pdfPage.mediaBox().minX();
            double maxY = pdfPage.mediaBox().maxY();

            Shape shape = pdfPath.strokedShape(minX, maxY);
            Area area = new Area(clip.path());
            area.intersect(new Area(shape));
            clip = new OCDClip(page.defs(), new Path3(area), Dexter.NewClipID(page));
            page.defs().addDefinition(clip);

            Rectangle3 bounds = clip.bounds();
            if (!bounds.isEmpty())
            {
                float scale = displayProps.patternScaling;
                Path3 sPath = clip.path().scale(scale);
                Rectangle3 sBounds = sPath.bounds();
                displayProps.displayScaling = scale;
                Image3 image3 = pdfPath.getStrokeColor().colorSpace().toPattern().image(sBounds, displayProps, null, true);
                // image3 = image3.softClip(sPath.translate(-sBounds.x, -sBounds.y));
                OCDImage image = new OCDImage(ocdContent);
                image.setImage(image3, imageQuality);
                image.setTransform(new Transform3(1 / scale, 0, 0, 1 / scale, bounds.x, bounds.y));
                // Log.debug(this, ".addPattern - page-height=" + pageHeight);
                // image3.write("C:/Users/Zoubi/Desktop/pattern" + debugCounter++ +
                // ".png");
                // page.content().add(clip);
                ocdContent.add(image);
                image.setClip(clip);
                image.setBlendMode(Str.Lower(pdfPath.blendMode()));
                ocdContent.doc().imageHandler.addEntry(image);
                return image;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static OCDImage AddFillPattern(OCDContent ocdContent, PDFPaintable node, PDFPage pdfPage, OCDClip clip, PDFDisplayProps displayProps, double imageQuality)
    {
        try
        {
            OCDPage page = ocdContent.page();

            double minX = pdfPage.mediaBox().minX();
            double maxY = pdfPage.mediaBox().maxY();

            Shape shape = node.shape(minX, maxY);
            Area area = new Area(clip.path());
            area.intersect(new Area(shape));
            clip = new OCDClip(page.defs(), new Path3(area), Dexter.NewClipID(page));
            page.defs().addDefinition(clip);

            Rectangle3 bounds = clip.bounds();
            if (!bounds.isEmpty())
            {
                float scale = displayProps.patternScaling;
                Path3 sPath = clip.path().scale(scale);
                Rectangle3 sBounds = sPath.bounds();
                displayProps.displayScaling = scale;
                Image3 image3 = node.getFillColor().colorSpace().toPattern().image(sBounds, displayProps, null, true);
                // image3 = image3.softClip(sPath.translate(-sBounds.x, -sBounds.y));
                OCDImage image = new OCDImage(ocdContent);
                image.setImage(image3, imageQuality);
                image.setTransform(new Transform3(1 / scale, 0, 0, 1 / scale, bounds.x, bounds.y));
                // Log.debug(this, ".addPattern - page-height=" + pageHeight);
                // image3.write("C:/Users/Zoubi/Desktop/pattern" + debugCounter++ +
                // ".png");
                // page.content().add(clip);
                ocdContent.add(image);
                image.setClip(clip);
                image.setBlendMode(Str.Lower(node.blendMode()));
                ocdContent.doc().imageHandler.addEntry(image);
                return image;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static OCDImage AddShading(OCDContent ocdContent, PDFPath pdfPath, PDFPage pdfPage, OCDClip clip, PDFDisplayProps displayProps, double imageQuality)
    {
        try
        {
            OCDPage page = ocdContent.page();

            double minX = pdfPage.mediaBox().minX();
            double maxY = pdfPage.mediaBox().maxY();

            Shape shape = pdfPath.shape(minX, maxY);
            Area area = new Area(clip.path());
            area.intersect(new Area(shape));
            clip = new OCDClip(page.defs(), new Path3(area), Dexter.NewClipID(page));
            page.defs().addDefinition(clip);

            // clip = pdfPath.path().isClosed() ? new OCDClip(page.defs(),
            // pdfPath.shape(minX, maxY), newClipRef(page)) : clip;
            Rectangle3 bounds = clip.bounds();
            if (!bounds.isEmpty())
            {
                float scale = displayProps.shadingScaling;
                Path3 sPath = clip.path().scale(scale);
                Rectangle3 sBounds = sPath.bounds();
                displayProps.displayScaling = scale;
                pdfPath.shading().setTransform(pdfPath.shadingTM());
                Image3 image3 = pdfPath.shading().image(sBounds, displayProps, null, true);
                image3 = image3.softClip(sPath.translate(-sBounds.x, -sBounds.y));
                OCDImage image = new OCDImage(ocdContent);
                image.setImage(image3, imageQuality);
                image.setTransform(new Transform3(1 / scale, 0, 0, 1 / scale, bounds.x, bounds.y));
                ocdContent.add(image);
                image.setClip(clip);
                image.setBlendMode(Str.Lower(pdfPath.blendMode()));
                ocdContent.doc().imageHandler.addEntry(image);
                return image;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void Normalize(OCDPage page, Rectangle3 box, int deg)
    {
        for (OCDAnnot annot : page.annots())
            NormalizeAnnot(annot, box, deg);
        for (OCDNode node : page.defs())
            NormalizeNode(node, box, deg);
        for (OCDNode node : page.content())
            NormalizeNode(node, box, deg);
    }

    private static void NormalizeNode(OCDNode node, Rectangle3 box, int deg)
    {
        if (OCD.isText(node))
            NormalizeText((OCDText) node, box, deg);
        else if (OCD.isPath(node))
            NormalizePath((OCDPath) node, box, deg);
        else if (OCD.isImage(node))
            NormalizeImage((OCDImage) node, box, deg);
        else if (OCD.isClip(node))
            NormalizeClip((OCDClip) node, box, deg);
        else if (OCD.isGroup(node))
            for (OCDNode child : node.children())
                NormalizeNode(child, box, deg);
    }

    private static void NormalizeText(OCDText text, Rectangle3 box, int deg)
    {
        Transform3 tm = text.transform3();
        float[] coords = text.coords().array();
        if (deg == 90)
        {
            for (int i = 0; i < coords.length; i += 2)
            {
                float tmp = coords[i];
                coords[i] = box.height - coords[i + 1];
                coords[i + 1] = tmp;
            }
            tm.rotate(Math.PI / 2);
        } else if (deg == 180)
        {
            for (int i = 0; i < coords.length; i += 2)
            {
                coords[i] = box.width - coords[i];
                coords[i + 1] = box.height - coords[i + 1];
            }
            tm.rotate(Math.PI);
        } else if (deg == 270)
        {
            for (int i = 0; i < coords.length; i += 2)
            {
                float tmp = coords[i];
                coords[i] = coords[i + 1];
                coords[i + 1] = box.width - tmp;
            }
            tm.rotate(-Math.PI / 2);
        }

        text.setTransform(tm);
        text.updateCS(new Coords(coords, false));
    }

    private static void NormalizePath(OCDPath path, Rectangle3 box, int deg)
    {
        Transform3 tm = path.transform3();
        float x = tm.floatX();
        float y = tm.floatY();
        tm = tm.toOrigin();
        if (deg == 90)
        {
            float tmp = x;
            x = box.height - y;
            y = tmp;
            tm.rotate(Math.PI / 2);
            path.setTransform(tm.fsy(), tm.fhy(), tm.fhx(), tm.fsx(), x, y);
        } else if (deg == 180)
        {
            x = box.width - x;
            y = box.height - y;
            tm.rotate(Math.PI);
            path.setTransform(tm.fsx(), tm.fhy(), tm.fhx(), tm.fsy(), x, y);
        } else if (deg == 270)
        {
            float tmp = x;
            x = y;
            y = box.width - tmp;
            tm.rotate(-Math.PI / 2);
            path.setTransform(tm.fsy(), tm.fhy(), tm.fhx(), tm.fsx(), x, y);
        }
    }

    private static void NormalizeImage(OCDImage image, Rectangle3 box, int deg)
    {
        Transform3 tm = image.transform3();
        float x = tm.floatX();
        float y = tm.floatY();
        tm = tm.toOrigin();
        if (deg == 90)
        {
            float tmp = x;
            x = box.height - y;
            y = tmp;
            tm.rotate(Math.PI / 2);
            image.setTransform(tm.fsy(), tm.fhy(), tm.fhx(), tm.fsx(), x, y);
        } else if (deg == 180)
        {
            x = box.width - x;
            y = box.height - y;
            tm.rotate(Math.PI);
            image.setTransform(tm.fsx(), tm.fhy(), tm.fhx(), tm.fsy(), x, y);
        } else if (deg == 270)
        {
            float tmp = x;
            x = y;
            y = box.width - tmp;
            tm.rotate(-Math.PI / 2);
            image.setTransform(tm.fsy(), tm.fhy(), tm.fhx(), tm.fsx(), x, y);
        }
    }

    private static void NormalizeClip(OCDClip clip, Rectangle3 box, int deg)
    {
        Path3 p = clip.path();
        Point3 o = p.origin();
        float x = o.x;
        float y = o.y;
        float tmp;
        p = p.translate(-x, -y);
        if (deg == 90)
        {
            tmp = x;
            x = box.height - y;
            y = tmp;
            p = p.transform(Transform3.rotateInstance(Math.PI / 2));
        } else if (deg == 180)
        {
            x = box.width - x;
            y = box.height - y;
            p = p.transform(Transform3.rotateInstance(Math.PI));
        } else if (deg == 270)
        {
            tmp = x;
            x = y;
            y = box.width - tmp;
            p = p.transform(Transform3.rotateInstance(-Math.PI / 2));
        }
        p = p.translate(x, y);
        clip.setPath(p);
    }

    private static void NormalizeAnnot(OCDAnnot annot, Rectangle3 box, int deg)
    {
        Rectangle3 r = annot.bounds();
        float x0 = r.x();
        float y0 = r.y();
        float x1 = r.maxX();
        float y1 = r.maxY();
        float tmp;
        if (deg == 90)
        {
            tmp = x0;
            x0 = box.height - y0;
            y0 = tmp;
            tmp = x1;
            x1 = box.height - y1;
            y1 = tmp;
        } else if (deg == 180)
        {
            x0 = box.width - x0;
            y0 = box.height - y0;
            x1 = box.width - x1;
            y1 = box.height - y1;
        } else if (deg == 270)
        {
            tmp = x0;
            x0 = y0;
            y0 = box.width - tmp;
            tmp = x1;
            x1 = y1;
            y1 = box.width - tmp;
        }
        annot.setBounds(new Rectangle3(true, x0, y0, x1, y1));
    }

    public static void main(String... args)
    {
        Dexter.main(args);
    }

}
