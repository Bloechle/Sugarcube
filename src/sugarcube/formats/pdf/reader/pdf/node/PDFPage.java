package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDF2OCD;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.annotation.*;
import sugarcube.formats.pdf.reader.pdf.node.font.PDFFont;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFRectangle;
import sugarcube.formats.pdf.reader.pdf.object.Reference;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.ocd.objects.OCDAnnotations;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDPage;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;

public class PDFPage extends PDFNode
{
    public class PDFBoxes extends PDFNode
    {
        public PDFBoxes()
        {
            super("ViewBoxes", PDFPage.this);
        }

        public Rectangle3 mediaBox = null;// user space canvas (PDF primitives are
        // all relative to mediaBox)
        public Rectangle3 cropBox = null;// general clipping path (All primitives
        // are clipped with it)
        public Rectangle3 bleedBox = null;// optional bleed box
        public Rectangle3 trimBox = null;// optional trim box
        public Rectangle3 artBox = null;// optional art box

        @Override
        public String sticker()
        {
            return type;
        }

        @Override
        public String toString()
        {
            return type + "\nMediaBox" + mediaBox + "\nCropBox" + cropBox + "\nBleedBox" + (bleedBox == null ? "[null]" : bleedBox) + "\nTrimBox"
                    + (trimBox == null ? "[null]" : trimBox) + "\nArtBox" + (artBox == null ? "[null]" : artBox);
        }

        @Override
        public void paint(Graphics3 g, PDFDisplayProps props)
        {
            g.setClip(null);
            g.paint(props.shapeTransform(cropBox), null, Color3.RED.alpha(1f), Stroke3.LINE3);
            if (bleedBox != null)
                g.paint(props.shapeTransform(bleedBox), null, Color3.GREEN.alpha(1f), Stroke3.LINE3);
            if (trimBox != null)
                g.paint(props.shapeTransform(trimBox), null, Color3.BLUE.alpha(1f), Stroke3.LINE2);
            if (artBox != null)
                g.paint(props.shapeTransform(artBox), null, Color3.YELLOW.alpha(1f), Stroke3.LINE2);
        }
    }

    // add UserUnit (default 1.0 = 1.0 * 1/72 dpi)
    protected int pageNb;
    protected int rotation = 0;
    protected PDFBoxes boxes;
    protected PDFResources resources;
    protected PDFAnnotations annotations;
    protected PDFContent content;
    protected PDFDictionary page;

    public PDFPage(PDFDocument pdf, PDFDictionary page, int rotation, PDFRectangle mediaBox, PDFRectangle cropBox, PDFResources resources, int pageNb)
    {
        super(Dexter.PAGE, pdf);
        if (Dexter.DEBUG_MODE)
            System.out.print(" p" + pageNb + ", ");
        this.reference = page.reference();
        this.pageNb = pageNb;
        this.rotation = page.get("Rotate").intValue(rotation);
        this.boxes = new PDFBoxes();
        this.boxes.mediaBox = page.get("MediaBox").toPDFRectangle(mediaBox).rectangle();

        this.boxes.cropBox = page.get("CropBox").toPDFRectangle(page.get("MediaBox").toPDFRectangle(cropBox)).rectangle();
        PDFRectangle box = page.get("BleedBox").toPDFRectangle(null);
        if (box != null)
            this.boxes.bleedBox = box.rectangle();
        box = page.get("TrimBox").toPDFRectangle(null);
        if (box != null)
        {
            this.boxes.trimBox = box.rectangle();
        }

        box = page.get("ArtBox").toPDFRectangle(null);
        if (box != null)
            this.boxes.artBox = box.rectangle();
        // page resources must have been updated in the environment before the page
        // contents is read (since page content uses page resources)
        this.resources = new PDFResources(this);
        this.resources.populate(page, resources);

        this.annotations = new PDFAnnotations(this, page.get("Annots").toPDFArray());
        this.rotation = (((this.rotation + 360) % 360) + 45) / 90 * 90;// 0 90 180
        // 270
        this.page = page;
        add(this.boxes);
        add(this.resources);
        // add(this.content); //because is or not in memory
        add(this.annotations);

    }

    public PDFBoxes boundaries()
    {
        return this.boxes;
    }

    public int number()
    {
        return this.pageNb;
    }

    public int nbOfPages()
    {
        return this.document().getPages().size();
    }

    public PDFPage getPage(int pageIndex)
    {
        LinkedList<PDFPage> pages = this.document().getPages();
        return pages.get(pageIndex >= 0 && pageIndex < pages.size() ? pageIndex : 0);
    }

    public PDFPage previous()
    {
        int pageIndex = pageNb - 1 - 1;
        LinkedList<PDFPage> pages = this.document().getPages();
        return pages.get(pageIndex >= 0 && pageIndex < pages.size() ? pageIndex : 0);
    }

    public PDFPage next()
    {
        int pageIndex = pageNb - 1 + 1;
        LinkedList<PDFPage> pages = this.document().getPages();
        return pages.get(pageIndex >= 0 && pageIndex < pages.size() ? pageIndex : pages.size() - 1);
    }

    public PDFPage first()
    {
        LinkedList<PDFPage> pages = this.document().getPages();
        return pages.get(0);
    }

    public PDFPage last()
    {
        LinkedList<PDFPage> pages = this.document().getPages();
        return pages.get(page.size() - 1);
    }

    public Rectangle3 mediaBox()
    {
        return boxes.mediaBox;
    }

    public Rectangle3 cropBox()
    {
        return boxes.cropBox.createIntersection(boxes.mediaBox);
    }

    public Rectangle3 trimBox()
    {
        return boxes.trimBox;
    }

    public int getRotation()
    {
        return rotation;
    }

    public double getRadians()
    {
        return getRotation() * Math.PI / 180.0;
    }

    public PDFContent contents()
    {
        return content;
    }

    public PDFAnnotations annotations()
    {
        return this.annotations;
    }

    public PDFResources resources()
    {
        return resources;
    }

    public Collection<PDFFont> fonts()
    {
        return resources.getFonts();
    }

    @Override
    public String sticker()
    {
        return "Page-" + pageNb + " " + reference;
    }

    @Override
    public String toString()
    {
        return sticker() + "\nRotation[" + rotation + "]";
    }

    public Rectangle3 bounds()
    {
        return boxes.mediaBox;
    }

    public Rectangle3 mediaBounds(double scale)
    {
        return new Rectangle3(mediaTransform(scale).transform(boxes.mediaBox).getBounds2D());
    }

    public Rectangle3 cropBounds(double scale)
    {
        return new Rectangle3(mediaTransform(scale).transform(boxes.cropBox).getBounds2D());
    }

    public Transform3 mediaTransform(double scale)
    {
        Transform3 tm = Transform3.scaleAndRotateInstance(getRadians(), scale, scale);
        Rectangle2D bounds = tm.transform(boxes.mediaBox).getBounds2D();
        return Transform3.translateInstance(-bounds.getMinX(), -bounds.getMinY()).concat(tm);// -bounds.getMinY()?
        // +bounds...
    }

    public Transform3 cropTransform(double scale)
    {
        Transform3 tm = Transform3.scaleAndRotateInstance(getRadians(), scale, scale);
        Rectangle2D bounds = tm.transform(boxes.cropBox).getBounds2D();
        return Transform3.translateInstance(-bounds.getMinX(), -bounds.getMinY()).concat(tm);
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        g.clearWhite();
        if (content != null)
            content.paint(g, props);
    }

    public Image3 createImage(PDFDisplayProps props)
    {
        Transform3 t3 = props.displayTransform();
        Image3 context = PDF.ImageARGB(new Rectangle3(t3.transform(bounds()).getBounds2D()));
        Graphics3 g = context.graphics();
        g.clearWhite();
        g.setTransform(t3);
        paint(g, props);
        g.resetTransform();

        g.setClip(null);

        int counter = 0;
        // if (props.highlightTexts)
        // {
        // for (OCDText.Block block : blocks())
        // {
        // for (OCDText.Line line : block)
        // if (counter++ % 2 == 0)
        // g.paint(t3.transform(line.bounds()), new Color3(0, 0, 255, 64), null);
        // else
        // g.paint(t3.transform(line.bounds()), new Color3(0, 128, 192, 64), null);
        //
        // g.paint(t3.transform(block.bounds()).getBounds2D(), null, Color3.BLACK,
        // Stroke3.LINE);
        // //g.drawText(block.readingOrder + "", box.getMinX() - 4, box.getMinY() +
        // 3, XFont.MONOSPACED_FONT.bold(), XColor.BLUE_PIGMENT, XColor.WHITE);
        // }
        //
        // for (OCDText text : texts())
        // g.draw(t3.transform(text.bounds()), new Color3(0, 0, 255, 64), null);
        // }
        //
        // if (props.highlightPaths)
        // for (OCDGroup group : this.groups(OCDGroup.GRAPHIC))
        // g.draw(t3.transform(group.bounds()), new Color3(0, 255, 0, 255));

        return context;
    }

    public Image3 image(double scale)
    {
        Image3 image = createImage(new PDFDisplayProps(scale).copy(mediaBox()));
        int rotation = getRotation();
        while (rotation > 0)
        {
            image = image.rotate90();
            rotation -= 90;
        }
        return image;
    }

    public boolean isInMemory()
    {
        return this.content != null;
    }

    public synchronized void freeFromMemory()
    {
        if (this.isInMemory())
        {
            // Log.debug(this,
            // ".freeFromMemory - page "+this.number()+" freed from memory");
            this.boxes.parent = null;
            this.boxes = null;
            this.resources = null;// be carefull resources may be shared between
            // pages...
            this.annotations.parent = null;
            this.annotations.children.clear();
            this.annotations = null;
            this.content.freeFromMemory();
            this.children.remove(content);
            this.content = null;
            this.parent = null;
            this.document = null;
        }
    }

    public synchronized void ensureInMemory()
    {
        if (!isInMemory())
            add(content = new PDFContent(this, page.get(true, "Contents")));
    }

    public void addAnnotations(OCDPage ocdPage, Dexter writer)
    {
        OCDAnnotations annots = ocdPage.annots();

        Rectangle3 box = PDF2OCD.Rect(boxes.mediaBox, this);
        double dx = -box.x;
        double dy = -box.y;
        box = box.shift(dx, dy);
        Rectangle3 cropBox = PDF2OCD.Rect(boxes.cropBox, this, dx, dy);
        Rectangle3 viewBox = cropBox.copy();
        annots.addViewboxAnnot(box, "MediaBox");
        annots.addViewboxAnnot(cropBox, "CropBox");
        if (boxes.bleedBox != null)
        {
            Rectangle3 bleedBox = PDF2OCD.Rect(boxes.bleedBox, this, dx, dy);
            annots.addViewboxAnnot(bleedBox, "BleedBox");
            // viewBox = viewBox.intersection(bleedBox);
        }
        Rectangle3 trimBox = null;
        if (boxes.trimBox != null)
        {
            trimBox = PDF2OCD.Rect(boxes.trimBox, this, dx, dy);
            annots.addViewboxAnnot(trimBox, "TrimBox");
            // viewBox = viewBox.intersection(trimBox);
        }
        if (boxes.artBox != null)
        {
            Rectangle3 artBox = PDF2OCD.Rect(boxes.artBox, this, dx, dy);
            annots.addViewboxAnnot(artBox, "ArtBox");
        }
        annots.addViewboxAnnot(viewBox, OCDAnnot.ID_VIEWBOX);

        for (PDFAnnotation annot : this.annotations())
            if (annot.isValid())
            {
                Rectangle3 bounds = PDF2OCD.Rect(annot.bounds(), this, dx, dy);

                if (annot.isWidgetForm())
                {
                    PDFWidgetAnnot widget = annot.toWidget();
                    ocdPage.annots().addFormAnnot(bounds, widget.T).set(OCDAnnot.PROP_NAME, widget.TU).set(OCDAnnot.PROP_WIDGET, widget.FT);
                } else if (annot.isLink() || annot.isWidget())
                {
                    PDFLinkAnnot pdfLink = annot.toLink();
                    String link = pdfLink.gotoURI();

                    if (link == null || link.isEmpty())
                    {
                        Reference ref = pdfLink.gotoRef();
                        if (ref != null)
                        {
                            int i = writer.pdf.refs2PagesMap.pageNb(ref);
                            if (i > 0)
                                link = OCD.PageFilename(i, writer.pdf.nbOfPages());
                        }
                    }

                    if (link == null && pdfLink.nameAction() != null)
                        link = "@" + pdfLink.nameAction();

                    if (link != null || pdfLink.hasContents())
                    {
                        OCDAnnot ocdLink = ocdPage.annots().addLinkAnnot(bounds, link);
                        if (pdfLink.hasContents())
                            ocdLink.setText(pdfLink.contents());
                    }
                }
            }
    }

    public void addContentAnnotations(OCDPage ocdPage, Dexter writer)
    {
        OCDAnnotations annots = ocdPage.annots();

        Rectangle3 box = PDF2OCD.Rect(boxes.mediaBox, this);
        double dx = -box.x;
        double dy = -box.y;

        for (PDFAnnotation annot : this.annotations())
            if (annot.isValid() && annot.isRichMedia())
            {
                Rectangle3 bounds = PDF2OCD.Rect(annot.bounds(), this, dx, dy);
                PDFRichMediaAnnot media = (PDFRichMediaAnnot) annot;
                OCDImage image = ocdPage.content().newImage();
                image.setFromBytes(media.name, media.stream, bounds.extent());
                ocdPage.document().imageHandler.addEntry(image);
            }
    }

}
