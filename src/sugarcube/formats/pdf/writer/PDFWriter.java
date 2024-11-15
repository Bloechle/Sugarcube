package sugarcube.formats.pdf.writer;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.process.Progression;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.document.*;
import sugarcube.formats.pdf.writer.document.graphics.GraphicsProducer;
import sugarcube.formats.pdf.writer.document.image.ImageManager;
import sugarcube.formats.pdf.writer.document.image.ImageProducer;
import sugarcube.formats.pdf.writer.document.text.TextProducer;
import sugarcube.formats.pdf.writer.document.text.font.FontManager;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PDFWriter
{
    public final static int PDF_VERSION_1_4 = 4;
    public final static int PDF_VERSION_1_5 = 5;
    public final static int PDF_VERSION_1_6 = 6;
    public final static int PDF_VERSION_1_7 = 7;
    public final static String HEADER = "PDF-1.";
    public final static boolean COMPRESS = true;
    public final static boolean LINEARIZE = false;
    public Progression progression;
    private PDFParams params = new PDFParams();
    private OCDDocument ocd;
    private Writer writer;
    private CrossReferenceTable crossReferenceTable;
    public final GraphicsProducer graphicsProducer = new GraphicsProducer(this);
    public final TextProducer textProducer = new TextProducer(this);
    public final ImageProducer imageProducer = new ImageProducer(this);
    public final ImageManager imageManager = new ImageManager(this);
    public final FontManager fontManager = new FontManager(this);
    private int flag = Constants.PAGES_ONLY;
    private int idCounter = 0;
    private Linearizer linearizer;
    private String fileID;
    private transient int nbOfPages;
    private File3 file;
    private String version;
    private String error = null;

    public PDFWriter(OCDDocument ocd)
    {
        this(ocd, (File) null);
    }

    public PDFWriter(OCDDocument ocd, int version)
    {
        this(ocd, (File) null, version);
    }

    public PDFWriter(OCDDocument ocd, String filepath)
    {
        this(ocd, filepath == null ? null : new File3(filepath), PDF_VERSION_1_7);
    }

    public PDFWriter(OCDDocument ocd, String filepath, int version)
    {
        this(ocd, filepath == null ? null : new File3(filepath), version);
    }

    public PDFWriter(OCDDocument ocd, File file)
    {
        this(ocd, file, PDF_VERSION_1_7);
    }

    public PDFWriter(OCDDocument ocd, File file, int version)
    {
        this.version = HEADER + version;
        this.ocd = ocd;
        this.nbOfPages = ocd.pageHandler.nbOfPages();
        // first step metadata, last step writing...
        this.progression = new Progression("Processing " + ocd.fileName(), nbOfPages + 2);
        this.file = (file == null ? ocd.file() : new File3(file)).extense(".pdf_w");
    }

    public PDFWriter binarize(int threshold)
    {
        this.params.set(PDFParams.BINARIZE, threshold);
        return this;
    }

    public PDFWriter jpx()
    {
        this.params.set(PDFParams.JPX, true);
        return this;
    }

    public PDFWriter transparent()
    {
        this.params.setTransparent(true);
        return this;
    }

    public PDFWriter sampling(double sampling)
    {
        this.params.setSampling(sampling);
        return this;
    }

    public PDFWriter setParams(PDFParams params)
    {
        this.params = params;
        return this;
    }

    public PDFParams params()
    {
        return params;
    }

    public void cancel()
    {
        this.progression.cancel();
    }

    public File3 write()
    {
        return write(true, 1, ocd.nbOfPages());
    }

    public File3 write(int firstPage, int lastPage)
    {
        return write(true, firstPage, lastPage);
    }

    public File3 write(boolean isPageRange, int... pageNumbers)
    {
        if (isPageRange)
        {
            int firstPage = pageNumbers[0];
            int lastPage = pageNumbers[pageNumbers.length - 1];

            if (firstPage < 1)
                firstPage = 1;
            if (lastPage < 1)
                lastPage = ocd.nbOfPages();

            pageNumbers = new int[lastPage - firstPage + 1];
            for (int i = 0; i < pageNumbers.length; i++)
                pageNumbers[i] = firstPage + i;
        }

        try
        {
            progression.start("Processing " + ocd.filePath());
            // create filename
            fileID = Util.generateFileID(file.getName().replace(".pdf_w", ".pdf"));
            File tempFile;
            if (LINEARIZE)
            {
                linearizer = new Linearizer(this);
                tempFile = createTempFile();
            } else
            {
                linearizer = null;
                crossReferenceTable = new CrossReferenceTable(this);
                // new file
                createFile();
                // write header
                writer.writeComment(version);
            }
            nbOfPages = pageNumbers.length;
            // catalog
            DocumentCatalog catalog = new DocumentCatalog(this, flag);
            if (LINEARIZE)
                linearizer.setCatalogID(catalog.getID());
            // outlines and thread
            int id = idCounter;
            if ((flag & Constants.OUTLINES) == Constants.OUTLINES)
                ++id;
            if ((flag & Constants.THREADS) == Constants.THREADS)
                ++id;
            // Log.debug(this, ".write - Document catalog: outlines and threads not
            // yet supported");
            // page tree
            PageTree pageTree = new PageTree(this, pageNumbers);
            pageTree.write();
            // pages
            ArrayList<Page> pages = pageTree.getPages();
            Page page;
            int[] hints = new int[HintTable.NUMBER_OF_HINTS];
            int maxObjectsPerPage = 0;
            long maxBytesPerPage = 0;
            long lastObjectOffset = 0;
            long maxContentStreamSize = 0;
            int nbOfObjects;
            int nbOfBytes;
            int currentId;
            long writtenBytes;
            for (int p=0; p<pages.size(); p++)
            {
                // System.out.print(".");
                if (progression.canceled())
                    break;
                writtenBytes = writer.getWrittenBytes();
                currentId = idCounter;
                page = pages.get(p);
                page.createContents();
                hints[HintTable.FIRST_PAGE_LOCATION] = (int) writer.getWrittenBytes();
                page.write();
                nbOfObjects = idCounter - currentId;
                nbOfBytes = (int) (writer.getWrittenBytes() - writtenBytes);
                if (p == 0 && LINEARIZE)
                {
                    linearizer.setNumberOfPages(nbOfPages);
                    linearizer.setFirstPageObject(page.getID());
                    linearizer.setEndFirstPageOffset(writer.getWrittenBytes());
                    linearizer.changeCrossReferenceTable();
                    hints[HintTable.MIN_OBJECTS_AMOUNT] = nbOfObjects;
                    maxObjectsPerPage = nbOfObjects;
                    hints[HintTable.MIN_STREAM_SIZE] = nbOfBytes;
                    maxBytesPerPage = nbOfBytes;
                    hints[HintTable.FIRST_CONTENT_OFFSET] = (int) page.getFirstObjectOffset();
                    lastObjectOffset = page.getLastObjectOffset();
                    hints[HintTable.MIN_CONTENT_SIZE] = (int) page.getMinContentStreamSize();
                    maxContentStreamSize = page.getMaxContentStreamSize();
                    hints[HintTable.SHARED_REFERENCES] = page.getSharedReferences();
                    hints[HintTable.MAX_SHARED_OBJECT] = page.getMaxSharedObjectID();
                } else
                {
                    if (nbOfObjects < hints[HintTable.MIN_OBJECTS_AMOUNT])
                        hints[HintTable.MIN_OBJECTS_AMOUNT] = nbOfObjects;
                    else if (nbOfObjects > maxObjectsPerPage)
                        maxObjectsPerPage = nbOfObjects;
                    if (nbOfBytes < hints[HintTable.MIN_STREAM_SIZE])
                        hints[HintTable.MIN_STREAM_SIZE] = nbOfBytes;
                    else if (nbOfBytes > maxBytesPerPage)
                        maxBytesPerPage = nbOfBytes;
                    if (page.getFirstObjectOffset() < hints[HintTable.MIN_STREAM_SIZE])
                        hints[HintTable.MIN_STREAM_SIZE] = (int) page.getFirstObjectOffset();
                    if (page.getLastObjectOffset() > lastObjectOffset)
                        maxBytesPerPage = page.getLastObjectOffset();
                    if (page.getMinContentStreamSize() < hints[HintTable.MIN_CONTENT_SIZE])
                        hints[HintTable.MIN_CONTENT_SIZE] = (int) page.getMinContentStreamSize();
                    if (page.getMaxContentStreamSize() > maxContentStreamSize)
                        maxContentStreamSize = page.getMaxContentStreamSize();
                    if (hints[HintTable.SHARED_REFERENCES] < page.getSharedReferences())
                        hints[HintTable.SHARED_REFERENCES] = page.getSharedReferences();
                    if (hints[HintTable.MAX_SHARED_OBJECT] < page.getMaxSharedObjectID())
                        hints[HintTable.MAX_SHARED_OBJECT] = page.getMaxSharedObjectID();
                }

                progression.setProgress((p + 1) / (float) nbOfPages);
                progression.setDescription("Processing Page " + (p + 1) + "/" + nbOfPages);
            }
            if (LINEARIZE)
            {
                hints[HintTable.OBJECTS_DIFFERENCE_BITS] = Integer.toBinaryString(maxObjectsPerPage - hints[HintTable.MIN_OBJECTS_AMOUNT]).length();
                hints[HintTable.BYTES_DIFFERENCE_BITS] = Long.toBinaryString(maxBytesPerPage - hints[HintTable.MIN_STREAM_SIZE]).length();
                hints[HintTable.OFFSETS_DIFFERENCE_BITS] = Long.toBinaryString(lastObjectOffset - hints[HintTable.FIRST_CONTENT_OFFSET]).length();
                hints[HintTable.CONTENT_DIFFERENCE_BITS] = Long.toBinaryString(maxContentStreamSize - hints[HintTable.MIN_CONTENT_SIZE]).length();
                hints[HintTable.SHARED_REFERENCES] = Integer.toBinaryString(hints[HintTable.SHARED_REFERENCES]).length();
                hints[HintTable.MAX_SHARED_OBJECT] = Integer.toBinaryString(hints[HintTable.MAX_SHARED_OBJECT]).length();
                linearizer.setHints(hints);

                closeFile();
                linearizer.write(tempFile);
            } else
            {
                FileTrailer fileTrailer = new FileTrailer(this);
                fileTrailer.setCrossReferenceTablePosition(writer.getWrittenBytes());
                fileTrailer.setFileIDs(fileID, fileID);
                // write cross-reference table
                crossReferenceTable.write();
                // write trailer
                fileTrailer.setRootID(catalog.getID());
                fileTrailer.setSize(crossReferenceTable.getNumberOfObjects());
                fileTrailer.write();
                // finalize file
                closeFile();
            }
            progression.stepAchieved("File Written Successfully - " + this.file.getAbsolutePath());
            Log.info(this, ".write - File written successfully: " + this.file.getAbsolutePath());
            progression.complete();
        } catch (PDFException e)
        {
            Log.warn(this, ".write - Some problem occured during PDF file writing operation: " + e);
            error = e.getMessage();
            e.printStackTrace();
        }
        return File3.Wrap(file).extense(".pdf");
    }

    protected final void createFile() throws PDFException
    {
        // create root directory
        try
        {
            Log.debug(this, ".createFile - " + file.path());

            writer = new Writer(new DataOutputStream(new FileOutputStream(file.needDirs(false))));
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new PDFException("Unable to create PDF file '" + file + "'");
        }
    }

    private File createTempFile() throws PDFException
    {
        // create root directory
        try
        {
            File file = File.createTempFile("scPDF2OCD", ".temp");
            file.deleteOnExit();
            FileOutputStream outputStream = new FileOutputStream(file);
            writer = new Writer(new DataOutputStream(outputStream));
            return file;
        } catch (Exception e)
        {
            throw new PDFException("Unable to create PDF file '" + file + "'");
        }
    }

    protected final void closeFile() throws PDFException
    {
        try
        {
            writer.flush();
            writer.close();
            File3 pdfFile = File3.Wrap(file).extense(".pdf");
            if (pdfFile.exists())
                pdfFile.delete();
            File3.Wrap(file).renameTo(pdfFile);
        } catch (Exception e)
        {
            throw new PDFException("Unable to finalize PDF file '" + file + "'");
        }
    }

    public String getVersion()
    {
        return version;
    }

    public OCDDocument getDocument()
    {
        return ocd;
    }

    public int computeID()
    {
        return ++idCounter;
    }

    public void registerEntry(int objectID, long position) throws PDFException
    {
        if (LINEARIZE)
            linearizer.addEntry(position, 0, CrossReferenceTable.IN_USE_ENTRY);
        else
            crossReferenceTable.addEntry(objectID, position, 0, CrossReferenceTable.IN_USE_ENTRY);
        // crossReferenceTable.addEntry(id, type, position);
    }

    /*
     * public void registerEntry(int id, int type, long position) throws
     * PDFException { crossReferenceTable.addEntry(id, type, position); }
     */
    public Writer getWriter()
    {
        return writer;
    }

    public String getFileID()
    {
        return fileID;
    }

    public void dispose()
    {
        graphicsProducer.dispose();
        textProducer.dispose();
        imageProducer.dispose();
        imageManager.dispose();
        fontManager.dispose();
    }

    public static void PrintCCITT(File3 ocd, File3 pdf, double threshold)
    {
        PDFParams params = new PDFParams();
        params.set(PDFParams.BINARIZE, (int) (0.5 + threshold * 255));
        params.set(PDFParams.GRAPHICS, false);
        params.setTransparent(true);
        Convert(ocd, pdf, params, 0, 0);
    }

    public static void Convert(File3 ocd, File3 pdf)
    {
        PDFParams params = new PDFParams();
        Convert(ocd, pdf, params);
    }

    public static void Convert(File3 ocd, File3 pdf, int firstPage, int lastPage)
    {
        PDFParams params = new PDFParams();
        Convert(ocd, pdf, params, firstPage, lastPage);
    }

    public static void Convert(File3 ocd, File3 pdf, PDFParams params)
    {
        Convert(ocd, pdf, params, 0, 0);
    }

    public static void Convert(File3 ocd, File3 pdf, PDFParams params, int firstPage, int lastPage)
    {
        OCDDocument doc = OCD.Load(ocd);
        PDFWriter pdfWriter = new PDFWriter(doc, pdf, PDFWriter.PDF_VERSION_1_7);
        pdfWriter.setParams(params);
        pdfWriter.write(firstPage, lastPage);
        pdfWriter.dispose();
        doc.close();
    }

    public static void main(String... args)
    {
//    PrintCCITT(File3.desktop("/lost/RO_1983_21.ocd"), File3.desktop("/lost/RO_1983_21.pdf"), 0.9);

        Convert(File3.desktop("Bug.ocd"), File3.desktop("Debug.pdf"));
    }

}
