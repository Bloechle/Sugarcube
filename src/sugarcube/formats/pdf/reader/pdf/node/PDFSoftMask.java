package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

import java.awt.*;

public class PDFSoftMask extends PDFNode
{
    public static boolean DEBUG = false;
    public CompositeContext composite = null;
    public String subtype = "Alpha";// Alpha, Luminosity
    public float[] backdropColor = new float[0];
    public PDFFunction fct = null;
    public PDFContent content = null;
    public PDFExtGState xState;

    static
    {
        if (DEBUG)
            Log.debug(PDFSoftMask.class, " - debug is on");
    }

    public PDFSoftMask(PDFExtGState parent, PDFDictionary map)
    {
        super("SMask", parent);
        this.xState = parent;
        this.reference = map.reference();
        this.subtype = map.get("S").stringValue(subtype);
        this.backdropColor = map.get("BC").floatValues(backdropColor);
        if (map.has("TR") && !map.get("TR").stringValue().equals("Identity"))
        {
            Log.debug(this, " - SMask has function: " + reference());
            this.fct = PDFFunction.instance(this, map.get("TR"));
            this.add(fct);
        }
        this.content = new PDFContent(this, map.get("G").toPDFStream());
        this.add(content);
    }

    @Override
    public String sticker()
    {
        return "SMask" + reference();
    }

    @Override
    public String toString()
    {
        return sticker() + "\nS[" + subtype + "]" + "\nBC[" + Zen.Array.String(backdropColor) + "]" + "";
    }

    public PDFImage clip(PDFNode paint, PDFContext pdfContext)
    {
        Rectangle3 bbox = this.content.bbox().rectangle();

        PDFContent xObject = paint instanceof PDFContent ? (PDFContent) paint : null;
        if (xObject != null)
        {
            bbox = bbox.intersection(xObject.bbox().rectangle());
        }
        Image3 context = null;
        PDFDisplayProps props = pdfContext == null ? new PDFDisplayProps() : pdfContext.props;
        Rectangle3 pageBounds = props == null ? null : props.pageBounds;

        try
        {
            // props.pageBounds = bbox.copy();
            props = props.copy(bbox.copy());

            double originalScale = props.displayScaling;
            double ratio = 1;
            if (bbox.width > PDF.MAX_IMG_SIZE || bbox.height > PDF.MAX_IMG_SIZE)
            {
                props = props.copy(props.pageBounds);
                props.displayScaling = PDF.MAX_IMG_SIZE / Math.max(bbox.width, bbox.height);
//        ratio = props.displayScaling / originalScale;
            }

            Rectangle3 r = new Rectangle3(bbox.x, pageBounds.maxY() - bbox.y - bbox.height, bbox.width, bbox.height).scale(props.displayScaling);
            // Log.debug(this, ".clip - this" + this.content.reference() + ", paint="
            // + paint.reference() + ", r" + r + ", page=" + pageBounds);

            if (pdfContext != null)
            {
                context = pdfContext.subImage(r, props.displayScaling);
                // Log.debug(this, ".clip - box=" + r + ", page=" + pageBounds +
                // ", ratio=" + ratio);
                if (DEBUG)
                    context.write(File3.desktop("tmp/" + paint.reference() + "-ctx.png"));
            } else
            {
                Log.debug(this, ".clip - void context: " + paint.reference + ", img=" + r + ", ratio=" + ratio + ", bbox=" + bbox);
                context = PDF.ImageARGB(r);
            }

            Graphics3 g = context.graphics();
            g.setTransform(Transform3.scaleInstance(originalScale));
            props.id = 1;
            paint.paint(g, props);
            g.resetTransform();
            g.setClip(null);

        } catch (Exception e)
        {
            // e.printStackTrace();
            Log.warn(this, ".clip - " + this.reference + ", bbox" + this.content.bbox() + ": " + e.getMessage());
            context = PDF.ImageRGB(bbox);
        }

        Image3 maskImage = maskImage(bbox, props);
        PDFImage pdfImage = new PDFImage(paint, compositeImage(paint.reference + "", context, maskImage), bbox.x, bbox.y, props.displayScaling);
        // if(pdfImage.reference==null)
        pdfImage.reference = this.reference;
        return pdfImage;
    }

    public boolean isAlphaMask()
    {
        return this.subtype.equals("Alpha");
    }

    public boolean isLuminosityMask()
    {
        return !isAlphaMask();
    }

    public Image3 compositeImage(String bugId, Image3 image, Image3 mask)
    {
        if (image.width() != mask.width() || image.height() != mask.height())
            Log.debug(this, ".compositeImage - " + reference() + ", " + this.subtype + ": w=" + image.width() + ", h=" + image.height() + ", mw=" + mask.width() + ", mh="
                    + mask.height());
        int w = Math.min(image.width(), mask.width());
        int h = Math.min(image.height(), mask.height());

        String name = "tmp/" + bugId;
        String type = image.type().toString();
        if (DEBUG)
        {
            image.write(File3.desktop(name + "-img_" + type + ".png"));
            mask.write(File3.desktop(name + "-msk_" + subtype + reference() + ".png"));
        }

//    Log.debug(this,  ".compositeImage - "+this.subtype+", "+image.width());
        if (isAlphaMask())
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                    image.multiplyAlpha(x, y, mask.alpha(x, y));
        else
        {
            // experimentally, it seems that we do square the luminosity... don't
            // really know why
            int lum = 0;
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                {
                    image.multiplyAlpha(x, y, (lum = mask.luminosity(x, y)) * lum / 255);
                }
        }

        if (DEBUG)
            image.write(File3.desktop(name + "-res.png"));
        return image;
    }

    public Image3 maskImage(Rectangle3 bounds, PDFDisplayProps props)
    {
        PDFContent maskContent = null;
        PDFNode node = content.instance(null, null, null);

        PDFTransparencyGroup group = this.content.transparencyGroup();
        //TODO - implement  isolated and knockout
        boolean isIsolated = group == null ? false : group.isolated;
        boolean isKnockout = group == null ? false : group.knockout;

        if (node.isContent())
        {
            maskContent = (PDFContent) node;
        } else
        {
            Log.debug(this, ".maskImage - content not found: " + this);
            return null;
        }
        Rectangle3 bbox = maskContent.bbox().rectangle().intersection(bounds);
        Image3 context = null;
        try
        {
            props.pageBounds = bbox;
            Transform3 t3 = props.displayTransform();
            Rectangle3 r = new Rectangle3(t3.transform(bbox).getBounds2D());
            context = PDF.ImageARGB(r);

            if (isLuminosityMask() && backdropColor != null && backdropColor.length > 0)
            {
                float mean = 0;
                for (int i = 0; i < backdropColor.length; i++)
                    mean += backdropColor[i];
//        Log.debug(this, ".maskImage - backDrop: " + Zen.A.toString(backdropColor));
                if (mean < 0.5)
                    context.clearBlack();
            }

            context.setPropTransform(t3);
            context.setPropBounds(r);
            Graphics3 g = context.graphics();
            g.setTransform(t3);
            // maskContent.paint(g, props);
            int counter = 0;
//      Log.debug(this, ".paint - " + this.reference + ": children=" + maskContent.nbOfChildren() + ", tm=" + t3);
            PDFClip clip = null;
            for (PDFNode child : maskContent)
            {
//        Log.debug(this, ".paint - " + this.reference + ": " + child.type + " " + child.reference);
                child.paint(g, props);
                if (DEBUG)
                {
                    String name = "tmp/" + this.reference + "-msk_" + (counter++);
                    if (child.isClip())
                    {
                        clip = child.toClip();
//            Log.debug(this, ".maskImage - clip: " + clip);
                    } else if (child.isImage())
                    {
                        PDFImage childImg = child.toImage();
                        Image3 img3 = new Image3(childImg.image());
                        img3.write(File3.desktop(name + "_alone.png"));
                    }

                    context.write(File3.desktop(name + "_flat.png"));
                }
            }

            g.resetTransform();
            g.setClip(null);
        } catch (Exception e)
        {
            e.printStackTrace();
            context = PDF.ImageARGB(bbox);
        }
        return context;
    }
}
