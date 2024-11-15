package sugarcube.formats.pdf.reader;

import sugarcube.formats.ocd.objects.*;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.StringOccurrences;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.interfaces.Progressable;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.SoftVersion;
import sugarcube.common.system.process.Arguments;
import sugarcube.common.system.process.Progression;
import sugarcube.common.numerics.Math3;
import sugarcube.formats.pdf.reader.pdf.node.*;
import sugarcube.formats.pdf.reader.pdf.object.PDFEnvironment;
import sugarcube.formats.pdf.reader.pdf.object.Reference;
import sugarcube.formats.pdf.reader.struct.GenericStruct;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.analysis.DexterProps;
import sugarcube.formats.ocd.analysis.text.Canonizer;
import sugarcube.formats.ocd.analysis.text.OCDDoctor;
import sugarcube.formats.ocd.writer.OCDWriter;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class Dexter implements Progressable {

    public static final StringSet DEBUG_TYPE_AVOID = null; // new
    // Set8(Dexter.IMAGE,
    // Dexter.PATH);
    public static boolean DISABLE_IMAGES = false;
    public static boolean DISABLE_PATHS = false;
    public static boolean DEBUG_MODE = false;
    public static final SoftVersion VERSION = new SoftVersion("Dexter", "2024-11-09", "v2.4");

    public static final PDFDisplayProps displayProps = new PDFDisplayProps();
    public static final String DOCUMENT = "Document";
    public static final String PAGE = "Page";
    public static final String CLIP = "Clip";
    public static final String TEXT = "Text";
    public static final String PATH = "Path";
    public static final String IMAGE = "Image";
    public static final String CONTENT = "Content";
    public static final String RESOURCES = "Resources";
    public static final String GROUP = "Group";
    public static final String WIDGET = "Widget";
    public static final String RICH_MEDIA = "RichMedia";
    public static final String LINK = "Link";
    public static final String BADOP = "BadOp";
    public static final String GHOST = "Ghost";

    public static final int READING_ORDER_MAX = 1000000;

    public static int TIMES = 0;
    public static boolean DEBUG = false;

    public StringOccurrences fontStats = new StringOccurrences();

    static {
        if (DEBUG_TYPE_AVOID != null && DEBUG_TYPE_AVOID.isPopulated())
            Log.warn(Dexter.class, " - DEBUG_TYPE_AVOID is ON !!!");

        if (DEBUG_MODE)
            Log.info(Dexter.class, " - DEBUG_MODE is on");
    }

    public Progression pdfProgression = new Progression("Dexter is Reading PDF File");
    public Progression ocdProgression = new Progression("Dexter is Generating OCD File");
    public int debugCounter = 0;
    public boolean debugMode = false;
    public PDFEnvironment env;
    public PDFDocument pdf;
    public OCDDocument ocd;
    public DexterProps props = new DexterProps();
    public Set3<OCDClip> clips = new Set3<>();
    public PDF2SVGFont refont = new PDF2SVGFont();
    public boolean doRestructure = true;
//    public transient StringBuilder hashBuilder = new StringBuilder();

    public Dexter() {
        this.pdfProgression.setName("Dexter");
        this.ocdProgression.setName("Dexter");
    }

    public Dexter(DexterProps props) {
        this(props, null);

    }

    public Dexter(DexterProps props, Progression ocdProgression) {
        this();
        if (props != null)
            this.props = props;
        setOCDProgression(ocdProgression);
    }

    public void setPDFProgression(Progression pdf) {
        if (pdf != null)
            pdfProgression = pdf.reset("Dexter", pdfProgression.numberOfSteps());
    }

    public void setOCDProgression(Progression ocd) {
        if (ocd != null)
            ocdProgression = ocd.reset("Dexter", ocdProgression.numberOfSteps());
    }

    public void setProgressions(Progression pdf, Progression ocd) {
        this.setPDFProgression(pdf);
        this.setOCDProgression(ocd);
    }

    public Dexter disableRestructuring() {
        this.doRestructure = false;
        return this;
    }

    public void setCanonProps(DexterProps props) {
        this.props = props;
    }

    public Progression progression() {
        return this.ocdProgression;
    }

    @Override
    public float progress() {
        return ocdProgression.progress();
    }

    @Override
    public String progressName() {
        return ocdProgression.progressName();
    }

    @Override
    public int progressState() {
        return ocdProgression.progressState();
    }

    @Override
    public String progressDescription() {
        return ocdProgression.progressDescription();
    }

    public OCDDocument convert(String pdfPath, String ocdPath) {
        return convert(new File3(pdfPath), new File3(ocdPath));
    }

    public OCDDocument convert(File pdfFile, File ocdFile) {
        return convert(File3.Wrap(pdfFile), File3.Wrap(ocdFile));
    }

    public OCDDocument convert(final File3 pdfFile, final File3 ocdFile) {
        OCDDocument document = null;
        try {
            document = convert(pdfFile, ocdFile, false);
        } catch (Exception e) {

        }
        return document;
    }

    public OCDDocument convert(final File3 pdfFile, final File3 ocdFile, boolean propagateException) throws Exception {
        this.fontStats.clear();

        this.ocd = new OCDDocument();

        this.refont.setOCD(ocd);

        PDFEnvironment env = new PDFEnvironment(pdfProgression);
        if (!env.parse(pdfFile, propagateException))
            return null;

        // TODO needs to be improved to allow direct access to OCDDocument after
        // writing instead of reloading it (e.g., add images to handler)
        File3 file = ocdFile == null ? null : new File3(ocdFile);
        try {
            ocdProgression.reset();

            // TODO - metadata.setDpi and other annotations...
            file = file == null ? new File3(env.filePath()) : file.isDirectory() ? new File3(file, env.fileName()) : file;
            file = file.extense(OCD.FILE_EXTENSION);

            Log.debug(this, ".convert - " + file);

            final OCDWriter writer = new OCDWriter(ocd, file, true);
            writer.writeHeader();

            this.pdf = new PDFDocument(env, new PDFDocumentConsumer() {
                @Override
                public void consumeTrailer(PDFDocument pdf) {
                    Dexter.this.pdf = pdf;
                    // ocd.nbOfPages = pdf.nbOfPages();
                    if (pdf.structure() != null && pdf.structure().isProducedByWord())
                        ocd.properties().put("structure", "word");
                    PDF2OCD.PopulateMetadata(pdf, ocd);
                    Log.info(Dexter.class, " - Dexter is Generating OCD Document with " + pdf.nbOfPages() + " pages: " + ocd.fileName());
                    ocdProgression.setNbOfSteps(pdf.nbOfPages());
                }

                @Override
                public void consumePage(PDFPage page) {
                    if (ocdProgression.canceled())
                        return;

                    int nb = page.number();
                    int nbOfPages = page.document().nbOfPages();
                    System.out.print(nb % 10 == 0 ? "\np" + nb + " " : ".");
                    ocdProgression.stepAchieved(page.number(), "Processing page - " + nb + "/" + nbOfPages);
                    refont.addFonts(page.fonts());

                    OCDPage ocdPage = generateOCDPage(page);
                    writer.writeEntry(ocdPage, true);
                    ocdPage.freeFromMemory(true);
                    ocd.freeFromMemory(true);
                }
            }, propagateException);

            if (!pdf.warnings.isEmpty())
                for (Map.Entry<String, Set3<String>> entry : pdf.warnings.entrySet())
                    Log.warn(this, ".writeOCD - " + entry.getKey() + entry.getValue());

            for (SVGFont font : ocd.fontHandler)
                writer.writeEntry(font);

//            if (model.hasClasses())
//                writer.writeEntry(ocd.addonHandler.addAddon(OCDModel.FILENAME, model));

            for (OCDAddon addon : ocd.addonHandler)
                writer.writeEntry(addon);

            writer.writeEntry(ocd.metadata());
            writer.writeEntry(navigation());
            writer.writeEntry(ocd.styles());

            ocd.statistics().addAll("font", fontStats);

            writer.writeEntry(ocd);
            writer.writeEntry(ocd.manifest());
            writer.complete(true);

            // NetExchange ex = Tracking.Track(VERSION.software,
            // VERSION.versionValue(), hash, name, path, file.lengthKB() + "KB",
            // ocd.pageHandler.nbOfPages(),
            // "", Date3.UTC(), (System.currentTimeMillis() - time) + "ms", "");
            // if (Tracking.Grant(ex))

            Log.info(this, ".write - File written successfully: " + file.path());
            int nbOfPages = ocd.document().nbOfPages();
            ocdProgression.stepAchieved("Document pages processed - " + nbOfPages + "/" + nbOfPages).complete();
            writer.dispose();
            pdf.dispose();
            pdf = null;
        } catch (Exception ex) {
            Log.warn(Dexter.class, ".writeOCD: " + ex);
            if (propagateException) {
                throw ex;
            } else {
                ex.printStackTrace();
                return null;
            }
        }
        return ocd;
    }

    private OCDNavigation navigation() {
        OCDNavigation nav = ocd.nav();
        PDFOutlines outlines = pdf.outlines();
        if (outlines != null)
            addNavItem(outlines, nav.toc());
        else
            nav.populatePagesTOC();
        return ocd.nav();
    }

    private void addNavItem(PDFOutlines out, OCDNavItem node) {
        if (!out.isRoot()) {
            Reference ref = out.gotoPage();
            int i = ref == null ? -1 : this.pdf.refs2PagesMap.pageNb(ref);
            node = node.add(out.title(), i > 0 ? ocd.getPage(i).entryFilename() : null);
        }
        for (PDFOutlines child : out)
            addNavItem(child, node);
    }

    private OCDPage generateOCDPage(PDFPage pdfPage) {
        OCDPage ocdPage = ocd.addPage(OCD.PageFilename(pdfPage.number(), pdfPage.nbOfPages()));
        ocdPage.setProd("Dexter");
        // ensures that OCDPage does not try to dynamically load page from unexisting OCD file
        ocdPage.setInMemory(true);
        Rectangle3 box = pdfPage.mediaBox();
        PDFDisplayProps displayProps = new PDFDisplayProps();
        ocdPage.setSize(box);
        displayProps.pageBounds = box;
        displayProps.displayScaling = 1f;

        pdfPage.addAnnotations(ocdPage, this);
        this.clips.clear();

        GenericStruct structure = GenericStruct.Instance(this.pdf.structure(), pdfPage, ocdPage, this);
        structure.useStructure = props.useTreeStructure;

        if (structure != null && structure.root() != null) {
//            Log.debug(this, ".generateOCDPage - producer=" + structure.root().producer);
            if (structure.root().isProducedByABBYY())
                structure.dontUseStructure();

            if (structure.root().isProducedByAcrobat())
                Log.debug(this, ".generateOCDPage - structure produced by Acrobat");
        }

        OCDClip pageClip = new OCDClip(ocdPage.defs(), ocdPage.bounds(), OCDClip.ID_PAGE);
        ocdPage.defs().addDefinition(pageClip);

        addContent(ocdPage.content(), pageClip, pdfPage.contents(), pdfPage, displayProps, structure, null);

        pdfPage.addContentAnnotations(ocdPage, this);

        ocdPage.content().zOrderize(0);

        // normalize rotated pages
        int deg = pdfPage.getRotation();
        if (deg != 0) {
            PDF2OCD.Normalize(ocdPage, box, deg);
            if (deg == 90 || deg == 270)
                box = new Rectangle3(0, 0, box.height, box.width);
            ocdPage.setSize(box);
            displayProps.pageBounds = box;
            displayProps.displayScaling = 1f;
        }

        if (doRestructure) {
            //ocd page content is still z-ordered
            OCDDoctor.CleanWhiteBg(ocdPage);
            OCDDoctor.CleanOcclusion(ocdPage);
            OCDDoctor.CleanClipping(ocdPage);

            Canonizer.Process(ocdPage, props);

            //ocd page has been r-ordered... z-order is kept with the z attribute
            structure.process(ocdPage);
        }
        OCDDoctor.GenerateIDs(ocdPage);

        // Log.debug(Dexter.this, ".ocdPage - " + pdfPage.number() + " from " + ocd.fileName() + ": " + Canonizer.PrintElapsed());

        int gap = 100;
        ocdPage.annots().addViewboxAnnot(new Rectangle3(-gap, -gap, box.width + 2 * gap, box.height + 2 * gap), OCDAnnot.ID_CANVASBOX);
        ocdPage.annots().addViewboxAnnot(ocdPage.content().blocks().bounds(), OCDAnnot.ID_TEXTBOX);
        return ocdPage;
    }


    private void addContent(OCDContent ocdContent, OCDClip pageClip, PDFContent pdfContent, PDFPage pdfPage, PDFDisplayProps displayProps,
                            GenericStruct structure, OCDClip parentClip) {
        OCDClip contentClip = PDF2OCD.AddClip(ocdContent, null, pdfContent, pdfPage, parentClip, clips);
        OCDClip clip = contentClip;

        int counter = 0;
        for (PDFNode node : pdfContent) {
            if (DEBUG && counter++ % 100 == 0)
                System.out.println("\ncounter=" + counter);

            // Log.debug(this, ".addContent - "+node.getType()+" to "+ocdContent.tag);
            if (node.isValid()) {
                int clipped = -1;
                if (node.isText()) {
                    if (DexterProps.DO_KEEP_TEXTS) {
                        if (DEBUG)
                            System.out.print(".t");
                        PDFText pdfText = node.toText();
                        if (pdfText.hasFillColorPattern()) {
                            OCDImage image = PDF2OCD.AddFillPattern(ocdContent, pdfText, pdfPage, clip, displayProps, props.imageQuality);

                            if ((clipped = clip.doClip(image, 0)) > -1) {
                                if (image != null && image.clip().fits(image.shape(), 1))
                                    image.setClip(pageClip);
                                structure.assign(pdfText.mark(), image);
                            }
                        } else {
                            OCDText text = PDF2OCD.NewText(ocdContent, pdfText, pdfPage, refont);
                            if ((clipped = clip.doClip(text, props.textClipped)) > -1) {
                                structure.assign(pdfText.mark(), text);
                                ocdContent.add(text);
                                fontStats.inc(text.fontname() + "_" + Math3.Round(text.scaledFontsize()), text.length());
                                text.setClipID(clipped == 1 ? OCDClip.ID_PAGE : clip.id());
                            }
                        }
                    }
                } else if (node.isPath()) {
                    if (DexterProps.DO_KEEP_PATHS) {
                        PDFPath pdfPath = node.toPath();
                        if (pdfPath.hasShading()) {
                            if (DEBUG)
                                System.out.print(".sh");

                            if (pdfPath.hasPatchShading()) {
                                Log.debug(this, ".addContent - patch shading");
                            } else {
                                OCDImage image = PDF2OCD.AddShading(ocdContent, pdfPath, pdfPage, clip, displayProps, props.imageQuality);
                                if ((clipped = clip.doClip(image, 0)) > -1) {
                                    if (image != null && image.clip().fits(image.shape(), 1))
                                        image.setClip(pageClip);
                                    structure.assign(pdfPath.mark(), image);
                                }

                            }
                        } else if (pdfPath.hasFillColorPattern()) {
                            if (DEBUG)
                                System.out.print(".fc");
                            OCDImage image = PDF2OCD.AddFillPattern(ocdContent, pdfPath, pdfPage, clip, displayProps, props.imageQuality);
                            if ((clipped = clip.doClip(image, 0)) > -1) {
                                if (image != null && image.clip().fits(image.shape(), 1))
                                    image.setClip(pageClip);
                                structure.assign(pdfPath.mark(), image);
                            }
                        }
                        if (pdfPath.hasStrokeColorPattern()) {
                            if (DEBUG)
                                System.out.print(".sc");
                            OCDImage image = PDF2OCD.AddStrokePattern(ocdContent, pdfPath, pdfPage, clip, displayProps, props.imageQuality);
                            if ((clipped = clip.doClip(image, 0)) > -1) {
                                if (image != null && image.clip().fits(image.shape(), 1))
                                    image.setClip(pageClip);
                                structure.assign(pdfPath.mark(), image);
                            }
                        }

                        if (!pdfPath.isInvisible()) {
                            if (DEBUG)
                                System.out.print(".p");
                            OCDPath ocdPath = PDF2OCD.AddPath(ocdContent, pdfPath, pdfPage);
                            if ((clip.doClip(ocdPath, 0)) > -1) {
                                ocdPath.setClip(clip.fits(ocdPath.shape(), 1) ? pageClip : clip);
                                structure.assign(pdfPath.mark(), ocdPath);
                                if (ocdPath.isOpaque() && !ocdPath.hasBlendMode()) {
                                    Iterator<OCDPaintable> iter = ocdContent.iterator();
                                    while (iter.hasNext()) {
                                        OCDPaintable paint = iter.next();
                                        // removes hidden text
                                        if (paint.isText() && ocdPath.contains(paint.bounds()))
                                            iter.remove();
                                    }
                                }
                            }
                        }
                    }
                } else if (node.isImage()) {
                    if (DexterProps.DO_KEEP_IMAGES) {
                        if (DEBUG)
                            System.out.print(".i");
                        OCDImage image = PDF2OCD.AddImage(ocdContent, node.toImage(), pdfPage, props.imageQuality);
                        if ((clipped = clip.doClip(image, 0)) > -1) {
                            if (image != null)
                                image.setClip(clip.fits(image.shape(), 0.01) ? contentClip : clip);

                            if (structure.isProducer(GenericStruct.PRODUCER_ABBYY))
                                image.setRole("background");

                            structure.assign(node.toImage().mark(), image);
                        }
                    }
                } else if (node.isClip())
                    clip = PDF2OCD.AddClip(ocdContent, node.toClip(), pdfContent, pdfPage, parentClip, clips);
                else if (node.isContent()) {
                    OCDContent subContent = ocdContent.newContent();
                    this.addContent(subContent, pageClip, node.toContent(), pdfPage, displayProps, structure, contentClip);
                }
            } else
                Log.debug(this, ".addContent - invalid: " + node.getType());
        }

        // OCDManifest manifest = ocdPage.manifest();
        // for (SVGFont font : ocdPage.fonts())
        // manifest.addItem(font.fontname(), font.entryPath(), OCDItem.TYPE_FONT);
    }

    public static String NewClipID(OCDPage page) {
        return "c" + page.defs().nbOfChildren();
    }

    public static OCDDocument Convert(String pdfPath, String ocdPath) {
        return Convert(new File3(pdfPath), new File3(ocdPath), new DexterProps());
    }

    public static OCDDocument Convert(final File pdfFile) {
        return Convert(pdfFile, null);
    }

    public static OCDDocument Convert(final File pdfFile, final DexterProps props) {
        return Convert(pdfFile, null, props);
    }

    public static OCDDocument Convert(File pdfFile, File ocdFile, DexterProps props) {
        return Convert(pdfFile, ocdFile, props, null);
    }

    public static OCDDocument Convert(final File pdfFile, final File ocdFile, final DexterProps props, Progression progression) {
        return new Dexter(props, progression).convert(pdfFile, File3.Wrap(ocdFile == null ? pdfFile : ocdFile).extense(OCD.FILE_EXTENSION));
    }

    public static void main(String... arguments) {
//        arguments = new String[]{"C:/Users/jean-/Desktop/mr29.pdf"};

        DexterProps props = new DexterProps();
        Arguments args = new Arguments(arguments);
        DexterProps.DO_KEEP_TEXTS = args.bool("texts", true);
        DexterProps.DO_KEEP_PATHS = args.bool("paths", true);
        DexterProps.DO_KEEP_IMAGES = args.bool("images", true);
        DexterProps.DO_KEEP_INVISIBLE_TEXTS = args.bool("ghost-texts", false);

        switch (args.nbOfParams()) {
            case 0:
                PDF2OCDFrame.main();
                break;
            case 1:
                Convert(args.firstFile(), null, props).close();
                break;
            case 2:
                Convert(args.firstFile(), args.secondFile(), props).close();
                break;
        }
    }
}
