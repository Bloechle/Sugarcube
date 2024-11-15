package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.TextMapSet;
import sugarcube.common.interfaces.Progressable;
import sugarcube.common.system.process.Progression;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.nametree.PDFNames;
import sugarcube.formats.pdf.reader.pdf.node.struct.PDFStructTreeRoot;
import sugarcube.formats.pdf.reader.pdf.object.*;

import java.io.File;
import java.util.LinkedList;

public class PDFDocument extends PDFNode implements Progressable
{
    public static PDFDocumentConsumer NOOP_CONSUMER = (page ->
    {
    });

    public final Refs2PageMap refs2PagesMap = new Refs2PageMap();
    public final PDFMemoryPool pool = new PDFMemoryPool(15);
    public final TextMapSet warnings = new TextMapSet();
    private Progression progression = new Progression("Dexter is Extracting PDF File");
    private PDFContent content;// content stream being rendering... access it from anywhere
    private PDFTrailer trailer;
    private String filePath;
    private PDFDocumentConsumer consumer = null;
    private int nbOfPages = -1;
    private transient int currentPageNb;
    private PDFPages pages;
    private PDFOutlines outlines = null;
    private PDFStructTreeRoot structure = null;
    private PDFNames names = null;
    private PDFEnvironment env;
    public PDFDisplayProps displayProps = new PDFDisplayProps();

    public PDFDocument()
    {
        super(Dexter.DOCUMENT, null);
        initialize();
        add(pages = new PDFPages(this));
    }

    public PDFDocument(PDFEnvironment env)
    {
        this();
    	try {
	        parse(env, false);
    	}catch (Exception e) {
		}
    }

    public PDFDocument(PDFEnvironment env, PDFDocumentConsumer consumer)
    {
        this();
    	try {
	        this.consumer = consumer;
	        parse(env, false);
    	}catch (Exception e) {
		}
    }


    public PDFDocument(PDFEnvironment env, PDFDocumentConsumer consumer, boolean propagateException) throws Exception
    {
        this();
    	try {
	        this.consumer = consumer;
	        parse(env, propagateException);
    	}catch (Exception e) {
    		if (propagateException)
    			throw e;
		}
    }

    public PDFDocument(File file, PDFDocumentConsumer consumer)
    {
        this(new PDFEnvironment(file), consumer);
    }

    public PDFEnvironment env()
    {
        return env;
    }

    public void setProgression(Progression progression)
    {
        if (progression != null)
            this.progression = progression;
    }

    public PDFPages pages()
    {
        return pages;
    }

    public PDFOutlines outlines()
    {
        return outlines;
    }

    public PDFNames names()
    {
        return names;
    }

    public PDFStructTreeRoot structure()
    {
        return structure;
    }

    public final void initialize()
    {
        // this.pages.clear();
        this.children.clear();
        this.progression.reset();
        this.currentPageNb = 0;
    }

    @Override
    public float progress()
    {
        return progression.progress();
    }

    @Override
    public String progressName()
    {
        return progression.progressName();
    }

    @Override
    public int progressState()
    {
        return progression.progressState();
    }

    @Override
    public String progressDescription()
    {
        return progression.progressDescription();
    }

    public int nbOfPages()
    {
        return this.nbOfPages;
    }

    public final boolean parse(PDFEnvironment env, boolean propagateException) throws Exception
    {
        progression.reset();
        if (env != null)
            try
            {

                this.env = env;
                this.filePath = env.filePath();
                this.trailer = env.getTrailer();
                PDFDictionary catalog = trailer.getRoot();
                PDFDictionary pagesDictionary = trailer.getPages();
                // Log.debug(this, ".parse - page dico: " + pagesDictionary);
                if (catalog != null && catalog.is("Type", "Catalog") || pagesDictionary != null)
                {
                    if (catalog != null)
                    {
                        Log.debug(this, ".parse - Catalog " + catalog.reference());

                        PDFDictionary dico = catalog.get("Pages").toPDFDictionary();
                        if (dico != null && dico.isValid())
                        {
                            Log.debug(this, ".parse - Pages: " + dico.reference());
                            pagesDictionary = dico;
                        } else
                            Log.debug(this, ".parse - Pages: " + dico);
                    } else
                    {
                        Log.debug(this, ".parse - null Catalog");
                    }
                    // pages count and reference mapping must be done before outlines and
                    // structures reading
                    int pageCount = pagesDictionary.get("Count").intValue(0);
                    this.nbOfPages = countPages(pagesDictionary);
                    Log.debug(this, ".parse - nbOfPages: Found=" + nbOfPages + ", Count=" + pageCount);
                    if (catalog != null)
                    {
                        if (catalog.has("Names"))
                            this.add(this.names = new PDFNames(this, catalog.get("Names").toPDFDictionary()));
                        if (catalog.has("Outlines"))
                            this.add(this.outlines = new PDFOutlines(this, catalog.get("Outlines").toPDFDictionary()));
                        if (catalog.has("StructTreeRoot"))
                            this.add(this.structure = new PDFStructTreeRoot(this, catalog.get("StructTreeRoot").toPDFDictionary()));
                    }

                    progression.setNbOfSteps(nbOfPages);
                    if (consumer != null)
                        consumer.consumeTrailer(this);
                    Log.debug(this, ".parse - Dexter is Reading PDF Document - " + nbOfPages + " pages");
                    parsePages(pagesDictionary, 0, null, null, new PDFResources(this));

                } else
                    Log.error(this, ".parse - Catalog Not Found");
                // these are transient objects used to render the virtual page
                this.content = null;
                progression.complete();
                return true;
            } catch (Exception e)
            {
            	if (propagateException)
            		throw e;
            	else
            		e.printStackTrace();
            }

        progression.complete();
        return false;
    }

    private void parsePages(PDFDictionary pages, int rotation, PDFRectangle mediaBox, PDFRectangle cropBox, PDFResources resources)
    {
        rotation = pages.get("Rotate").toPDFNumber(rotation).intValue();
        mediaBox = pages.get("MediaBox").toPDFRectangle(mediaBox);
        cropBox = pages.get("CropBox").toPDFRectangle(pages.get("MediaBox").toPDFRectangle(cropBox));
        resources = new PDFResources(this);
        resources.populate(pages, resources);

        for (PDFObject kid : pages.get("Kids").toPDFArray())
        {
            PDFDictionary page = kid.toPDFDictionary();
            if (page.is("Type", "Pages"))
            {
                parsePages(page, rotation, mediaBox, cropBox, resources);
            } else if (page.is("Type", "Page"))
            {
                currentPageNb++;
                Log.debug(this, ".parsePages - " + currentPageNb);
                progression.stepAchieved();
                progression.setDescription("Extracting PDF page " + progression.lastStepDone() + " of " + progression.numberOfSteps());
                PDFPage pdfPage = new PDFPage(this, page, rotation, mediaBox, cropBox, resources, currentPageNb);

                if (consumer != null)
                {
                    pdfPage.ensureInMemory();
                    consumer.consumePage(pdfPage);
                    pdfPage.freeFromMemory();
                } else
                    addPage(pdfPage);
            } else
                Log.debug(this, ".parsePages - unexpected page entry: " + kid.unreference());
        }
    }

    private int countPages(PDFDictionary map)
    {
        int counter = 0;
        for (PDFPointer kid : map.get("Kids").toPDFArray().pdfIndirectReferences())
        {
            PDFDictionary page = kid.toPDFDictionary();
            if (page.is("Type", "Pages"))
                counter += countPages(page);
            else if (page.is("Type", "Page"))
            {
                counter++;
                refs2PagesMap.incremement(page.reference());
            }
        }
        return counter;
    }

    public void addPage(PDFPage page)
    {
        this.pages.add(page);
    }

    public PDFPage firstPage()
    {
        return this.pages.firstPage();
    }

    public LinkedList<PDFPage> getPages()
    {
        return this.pages.getPages();
    }

    public String fileName()
    {
        return new File(filePath).getName();
    }

    public String filePath()
    {
        return filePath;
    }

    public PDFContent content()
    {
        return content;
    }

    public PDFContent setContent(PDFContent content)
    {
        PDFContent prevContent = this.content;
        this.content = content;
        return prevContent;
    }

    public PDFTrailer trailer()
    {
        return this.trailer;
    }

    @Override
    public String sticker()
    {
        return "PDF Document";
    }

    public boolean dispose()
    {
        if (env != null)
            return env.dispose();
        env = null;
        return false;
    }

    public static void ConsumePages(File file, Progression progression, PDFPageConsumer consumer)
    {
        if (progression != null)
            progression.reset();
        new PDFDocument(file, new PDFDocumentConsumer()
        {
            @Override
            public void consumePage(PDFPage page)
            {
                consumer.consumePage(page);
                if (progression != null)
                {
                    if (page.number() == page.document.nbOfPages)
                        progression.complete();
                    else
                        progression.setProgress(page.number() / (double) page.document.nbOfPages);
                }
            }
        }).dispose();
    }
}
