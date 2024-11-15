package sugarcube.formats.pdf.writer.document;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.formats.ocd.objects.*;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.object.Stream;
import sugarcube.formats.pdf.writer.core.writer.StringWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.document.annotation.Link;
import sugarcube.formats.pdf.writer.document.text.TextProducer;
import sugarcube.formats.pdf.writer.document.text.font.Font;
import sugarcube.formats.pdf.writer.document.text.font.FontManager;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;
import java.util.HashMap;

public class Page extends DictionaryObject
{
    // ignored
    // LastModified, BleedBox, TrimBox, ArtBox, BoxColorInfo,
    // Group, Thumb, B, Dur, Trans, Annots, AA, Metadata, PieceInfo,
    // StructParents, ID, PZ, SeparationInfo, Tabs, TemplateInstantiated,
    // PresSteps, UserUnit, VP
    private OCDPage ocdPage;
    private HashMap<Integer, String> linkedFonts = new HashMap<>();
    private HashMap<Integer, String> linkedImages = new HashMap<>();
    private ArrayList<Integer> linkedContents = new ArrayList<>();
    private ArrayList<Integer> annotations = new ArrayList<>();
    private int parentID;
    private GraphicState graphicState;
    private Rectangle3 pageBounds;
    private long firstObjectOffset;
    private long lastObjectOffset;
    private long minContentStreamSize;
    private long maxContentStreamSize;
    private int sharedReferences;
    private int maxSharedObjectID;
    private transient boolean textGroupOpen = false;

    public Page(OCDPage page, int parentID, PDFWriter environment) throws PDFException
    {
        super(environment);
        this.ocdPage = page;
        this.parentID = parentID;
        graphicState = new GraphicState(environment.params());
    }

    public GraphicState getGraphicState()
    {
        return graphicState;
    }

    private void initUserSpace() throws PDFException
    {
        Stream stream = new Stream(pdfWriter());

        linkContent(stream.getID());

        float[] matrix = new float[]
                {1, 0, 0, -1, 0, (float) pageBounds.height};
        StringBuilder stringBuilder = new StringBuilder();
        graphicState.writeMatrix(stringBuilder, matrix);
        stream.write(stringBuilder);
    }

    @Override
    public void addDictionaryEntries() throws PDFException
    {
        addDictionaryEntry("Type", "Page", Writer.NAME);
        addDictionaryEntry("Parent", parentID, Writer.INDIRECT_REFERENCE);
        linkRessources();
        Float[] bounds = Util.rectangleToFloatArray(pageBounds);
        addDictionaryEntry("MediaBox", bounds, Writer.REAL_ARRAY);
        addDictionaryEntry("CropBox", bounds, Writer.REAL_ARRAY);
        addDictionaryEntry("Rotate", 0f, Writer.REAL);
        linkContents();
        linkAnnotations();
    }

    public void createContents() throws PDFException
    {
        PDFWriter pdfWriter = pdfWriter();
        TextProducer textProducer = pdfWriter.textProducer;

        ocdPage.ensureInMemory();
        pageBounds = ocdPage.bounds();
        firstObjectOffset = pdfWriter.getWriter().getWrittenBytes();
        initUserSpace();

        for (OCDPaintable node : ocdPage.content().zOrderedPrimitives(false))
            createChild(node, pdfWriter);
        textProducer.closeGroupIfOpen();

        // add annotations
        createMetadata();
        lastObjectOffset = pdfWriter.getWriter().getWrittenBytes();
        ocdPage.freeFromMemory();
    }

    public void createChild(OCDPaintable node, PDFWriter pdfWriter)
            throws PDFException
    {
        // Log.debug(this, ".createChild - "+node.tag);
        if (node instanceof OCDText)
        {
            // dummy Zoubi solution :-p
            OCDText text = node.asText();
            if (!text.hasPaths())
                return;
            OCDTextBlock block = new OCDTextBlock(ocdPage.content());
            block.newTextLine().add(text, false);
            node = block;
        }

        if (node instanceof OCDGroup)
        {
            if (node instanceof OCDTextBlock)
            {
                OCDTextBlock block = node.asTextBlock();
                if (block.hasGlyphPath())
                    pdfWriter.textProducer.openGroupIfClosed(this).write(this, block);
            } else
            {
                for (OCDPaintable child : ((OCDGroup<OCDPaintable>) node).nodes().sortZOrder())
                    createChild(child, pdfWriter);
            }
        } else
        {
            pdfWriter.textProducer.closeGroupIfOpen();

            if (node instanceof OCDPath)
            {
                //Log.debug(this, ".createChild - OCDPath: "+node.sticker());
                OCDPath path = node.asPath();
                if (path.isFilled() && path.fillColor().alpha() < 0.8)
                {
//                    Rectangle3 box = path.bounds();
//                    Log.info(this, ".createChild - path with alpha: bounds=" + box);
//
//                    Image3 pathImage = new Image3(box.intWidth(), box.intHeight(), true);
//                    Graphics3 g = pathImage.graphics();
//
//                    Path3 shape = path.shape();
//                    g.setColor(path.fillColor());
//                    g.fill(shape);
//                    if (path.isStroked())
//                    {
//                        g.setColor(path.strokeColor());
//                        g.setStroke(path.stroke());
//                        g.stroke(shape);
//                    }
//
//                    OCDImage image = new OCDImage(path.parent());
//                    image.setZOrder(path.zOrder);
//                    image.setTransform(Transform3.IDENTITY);
//                    image.setClipID(path.clipID());
//                    image.setImage(null, pathImage, -1);
//                    image.modify();
//                    createChild(image, pdfWriter);
                } else
                {
                    pdfWriter.graphicsProducer.write(this, path);
                }
            } else if (node instanceof OCDImage)
            {
                OCDImage image = node.asImage();
                //Log.debug(this, ".createChild - OCDImage: " + node.sticker());
                if (!image.isView())
                    pdfWriter.imageProducer.write(this, image);
            } else
                Log.debug(this, ".createChild - " + node.getClass());
        }
    }

    private final void createMetadata() throws PDFException
    {
        OCDAnnotations annots = ocdPage.annotations();
        // create links
        Link link;
        Transform3 transform = new Transform3(new float[]
                {1, 0, 0, -1, 0, (float) pageBounds.height});
        for (OCDAnnot l : annots.type(OCDAnnot.TYPE_LINK))
        {
            link = new Link(pdfWriter(), transform, l);
            annotations.add(link.getID());
        }
    }

    private void linkAnnotations() throws PDFException
    {
        if (annotations.size() == 0)
            return;
        StringWriter writer = new StringWriter();
        writer.openArray();
        for (int a = 0; a < annotations.size(); a++)
        {
            if (a > 0)
                writer.write(Lexic.SPACE);
            writer.writeIndirectReference(annotations.get(a));
        }
        writer.closeArray();
        addDictionaryEntry("Annots", writer.toString(), StringWriter.GENERIC_STRING);
    }

    private final void linkRessources() throws PDFException
    {
        StringWriter writer = new StringWriter();
        writer.openDictionary();
        if (linkedFonts.size() > 0)
        {
            writer.writeName("Font");
            writer.openDictionary();
            for (Integer resource : linkedFonts.keySet())
            {
                writer.writeName(linkedFonts.get(resource));
                writer.write(Lexic.SPACE);
                writer.writeIndirectReference(resource);
                writer.write(Lexic.LINE_FEED);
                sharedReferences++;
                if (resource > maxSharedObjectID)
                    maxSharedObjectID = resource;
            }
            writer.closeDictionary();
        }
        if (linkedImages.size() > 0)
        {
            writer.writeName("XObject");
            writer.openDictionary();
            for (Integer resource : linkedImages.keySet())
            {
                writer.writeName(linkedImages.get(resource));
                writer.write(Lexic.SPACE);
                writer.writeIndirectReference(resource);
                writer.write(Lexic.LINE_FEED);
                sharedReferences++;
                if (resource > maxSharedObjectID)
                    maxSharedObjectID = resource;
            }
            writer.closeDictionary();
        }
        writer.closeDictionary();
        addDictionaryEntry("Resources", writer.toString(), StringWriter.GENERIC_STRING);
    }

    private final void linkContents() throws PDFException
    {
        StringWriter writer = new StringWriter();
        writer.openArray();
        for (Integer id : linkedContents)
        {
            writer.writeIndirectReference(id);
            writer.write(Lexic.LINE_FEED);
        }
        writer.closeArray();
        addDictionaryEntry("Contents", writer.toString(), StringWriter.GENERIC_STRING);
    }

    public final void linkFont(Font font)
    {
        int id = font.getID();
        if (!linkedFonts.containsKey(id))
            linkedFonts.put(id, FontManager.FONT_SUFFIX + id);
    }

    public final void linkImage(int id, String name)
    {
        if (!linkedImages.containsKey(id))
            linkedImages.put(id, name);
    }

    public final void linkContent(int id)
    {
        linkedContents.add(id);
    }

    public Rectangle3 getBounds()
    {
        return pageBounds;
    }

    public long getFirstObjectOffset()
    {
        return firstObjectOffset;
    }

    public long getLastObjectOffset()
    {
        return lastObjectOffset;
    }

    public long getMinContentStreamSize()
    {
        return minContentStreamSize;
    }

    public long getMaxContentStreamSize()
    {
        return maxContentStreamSize;
    }

    public int getSharedReferences()
    {
        return sharedReferences;
    }

    public int getMaxSharedObjectID()
    {
        return maxSharedObjectID;
    }
}
