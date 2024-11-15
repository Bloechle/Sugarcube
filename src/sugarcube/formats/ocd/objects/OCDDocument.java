package sugarcube.formats.ocd.objects;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.*;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.SoftVersion;
import sugarcube.common.system.io.Zip;
import sugarcube.common.system.io.ZipItem;
import sugarcube.common.system.process.Progression;
import sugarcube.common.ui.gui.Font3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.writer.OCDWriter;
import sugarcube.formats.ocd.objects.document.*;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.handlers.*;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;
import sugarcube.formats.pdf.writer.PDFWriter;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OCDDocument extends OCDEntry implements Iterable<OCDPage>
{
    public static final String TAG = "document";

    public Font3 defaultFont = Font3.CALIBRI_FONT;
    public Progression progression = new Progression("OCDDocument");
    public StringSet ids = new StringSet();
    public SoftVersion version = OCD.VERSION;
    public OCD.ViewProps viewProps = new OCD.ViewProps();
    public OCDFontHandler fontHandler = new OCDFontHandler(this);
    public OCDImageHandler imageHandler = new OCDImageHandler(this);
    public OCDThumbHandler thumbHandler = new OCDThumbHandler(this);
    public OCDAudioHandler audioHandler = new OCDAudioHandler(this);
    public OCDVideoHandler videoHandler = new OCDVideoHandler(this);
    public OCDAddonHandler addonHandler = new OCDAddonHandler(this);
    public OCDPageHandler pageHandler = new OCDPageHandler(this);
    protected OCDStatistics statistics = new OCDStatistics(this);
    protected OCDManifest manifest = new OCDManifest(this);
    protected OCDMetadata metadata = new OCDMetadata(this);
    protected OCDStyles styles = new OCDStyles(this);
    protected OCDNavigation navigation = new OCDNavigation(this);
    protected Zip zipFile;
    protected String filePath;
    public String viewBox = OCDAnnot.ID_VIEWBOX;
    public float viewScale = 1f;
    public float dpi = OCD.DPI;
    public transient boolean isSaving = false;

    public OCDDocument()
    {
        super(TAG, null);
    }

    public OCDDocument(String filePath)
    {
        this(new File(filePath));
    }

    public OCDDocument(File file)
    {
        this();
        this.load(file);
    }

    public File3 temp(String path)
    {
        return OCD.Temp(needID() + "_" + path);
    }

    public String needID()
    {
        return Str.IsVoid(id) ? id = autoID() : id();
    }

    @Override
    public String autoID()
    {
        return needHash() + "" + Base.x32.random8();
    }

    @Override
    public boolean modified()
    {
        // never change this;
        return true;
    }

    public boolean hasPageModified()
    {
        for (OCDPage page : this)
            if (page.modified())
                return true;
        return false;
    }

    public String hash()
    {
        return props.get("hash", "");
    }

    public String hash(String def)
    {
        return props.get("hash", def);
    }

    public void setHash(String hash)
    {
        this.props.put("hash", hash);
    }

    public String needHash()
    {
        String hash = hash();
        if (Str.IsVoid(hash))
            setHash(hash = Base.x32.random8());
        return hash;
    }

    public float dpi()
    {
        return dpi;
    }

    public void setDpi(float dpi)
    {
        this.dpi = dpi;
    }

    @Override
    public OCDDocument document()
    {
        return this;
    }

    public OCDPageHandler pages()
    {
        return this.pageHandler;
    }

    @Override
    public OCDPage page()
    {
        return this.pageHandler.firstPage();
    }

    public OCDPage page(int pageNb)
    {
        return this.pageHandler.getPage(pageNb);
    }

    public synchronized Image3 needCover(int width, double quality)
    {
        Image3 cover = cover();
        if (cover == null || cover.width() != width)
        {
            OCDPage page = pageHandler.firstPage();
            if (page != null)
            {
                OCD.ViewProps props = new OCD.ViewProps();
                props.box = this.viewBox;
                Rectangle3 box = page.viewBox(props);
                props.scale = width / box.width;

                byte[] data = (cover = page.createImage(props)).write(quality);
                imageHandler.addEntry(data, OCD.COVER_FILENAME);
            }
        }
        return cover;
    }

    public synchronized Image3 cover()
    {
        OCDImageEntry entry = this.imageHandler.get(OCD.COVER_FILENAME);
        return entry == null ? null : entry.image();
    }

    public void setCover(Image3 image)
    {
        imageHandler.addEntry(image.write(0.99), OCD.COVER_FILENAME);
    }

    public OCDPage addLastPageA4()
    {
        return addLastPage(595.32, 841.92);
    }

    public OCDPage addLastPage(double width, double height)
    {
        return addPage(pageHandler.createPageFilename(pageHandler.size() + 1), -1, width, height);
    }

    public OCDPage addPage(String filename, int pageNb, double width, double height)
    {
        OCDPage page = pageNb < 0 ? this.pageHandler.addEntry(filename) : this.pageHandler.addEntry(filename, pageNb - 1);
        // ensures that OCDPage does not try to dynamically load page from
        // unexisting OCD file
        page.setInMemory(true);
        page.setWidth(width);
        page.setHeight(height);
        page.annots().addViewboxAnnot(new Rectangle3(0, 0, width, height), OCDAnnot.ID_VIEWBOX);
        page.defs().addDefinition(new OCDClip(page.defs(), page.bounds(), OCDClip.ID_PAGE));
        return page;
    }

    public OCDPage addPage(String filename)
    {
        return pageHandler.addEntry(filename);
    }

    public boolean hasPage(int nb)
    {
        return this.pageHandler.hasPage(nb);
    }

    public OCDPage firstPage()
    {
        return pageHandler.firstPage();
    }

    public OCDPage lastPage()
    {
        return pageHandler.lastPage();
    }

    public OCDPage getPage(int nb)
    {
        return this.pageHandler.getPage(nb);
    }

    public int nbOfPages()
    {
        return this.pageHandler.nbOfPages();
    }

    public OCDMetadata metadata()
    {
        return this.metadata;
    }

    public OCDStatistics statistics()
    {
        return this.statistics;
    }

    public OCDManifest manifest()
    {
        return this.manifest;
    }

    public OCDStyles styles()
    {
        return this.styles;
    }

    public OCDNavigation nav()
    {
        return navigation;
    }

    public static boolean isOCD(File file)
    {
        return file.getName().endsWith(OCD.FILE_EXTENSION);
    }

    public synchronized Zip zipFile()
    {
        if (zipFile == null && (zipFile = Zip.Get(filePath)) == null)
            Log.Stacktrace(this, ".zipFile - file not found: " + filePath);
        return zipFile;
    }

    public ZipItem zipEntry(String path)
    {
        Zip zip = zipFile();
        return zip == null ? null : zip.entry(path);
    }

    public boolean hasZipEntry(String path)
    {
        Zip zip = zipFile();
        return zip == null ? false : zip.has(path);
    }

    public synchronized Set3<String> zipPaths()
    {
        return zipFile().paths();
    }

    public synchronized void close()
    {
        if (zipFile != null)
            zipFile.dispose();
        zipFile = null;
    }

    public File3 file()
    {
        return new File3(filePath);
    }

    public File3 fileDirectory()
    {
        return filePath == null ? File3.userDesktop() : new File3(filePath).parent();
    }

    public String fileName()
    {
        return filePath == null ? "noname" : new File3(filePath).getName();
    }

    public String filePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public void setViewbox(String ref)
    {
        this.viewBox = ref;
    }

    public String viewBox()
    {
        return this.viewBox;
    }

    @Override
    public String id()
    {
        return id;
    }

    @Override
    public synchronized List<? extends OCDNode> children()
    {
        // GUI viewtree children
        List<OCDNode> nodes = new LinkedList<>();
        nodes.add(properties());
        nodes.add(statistics);
        nodes.add(manifest);
        nodes.add(metadata);
        nodes.add(navigation);
        nodes.add(addonHandler);
        nodes.add(fontHandler);
        nodes.add(imageHandler);
        nodes.add(thumbHandler);
        nodes.add(audioHandler);
        nodes.add(videoHandler);
        nodes.addAll(pageHandler.map().list());
        return nodes;
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
        this.writeXmlID(xml);
        xml.write("ocd", version.versionValue());
        xml.write("id", needID());
        xml.write("dpi", dpi);
        xml.write("view", viewBox == null ? OCDAnnot.ID_PAGEBOX : viewBox);
        xml.write("scale", viewScale);
        // XML tree children
        return new List3<OCDNode>(properties(), statistics);
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        this.readXmlID(dom);
        this.version = new SoftVersion("OCD", dom.value("ocd", "2.0"));
        this.id = dom.value("id", id);
        this.dpi = dom.real("dpi", dpi);
        this.viewBox = dom.value("view", OCDAnnot.ID_PAGEBOX);
        this.viewScale = dom.real("scale", 1f);
        this.viewProps.box = this.viewBox;
        this.viewProps.scale = this.viewScale;
    }

    @Override
    public XmlINode newChild(DomNode child)
    {
        // children are already created in constructor
        if (OCD.isTag(child, OCDProperties.TAG))
            return this.properties().clear();
        else if (OCD.isTag(child, OCDStatistics.TAG))
            return this.statistics.clear();
        else if (OCD.isTag(child, OCDManifest.TAG))
            return this.manifest.clear();
        return null;
    }

    @Override
    public void endChild(XmlINode child)
    {
    }

    @Override
    public Iterator<OCDPage> iterator()
    {
        return pageHandler.iterator();
    }

    @Override
    public void paint(Graphics3 g, OCD.ViewProps props)
    {
        pageHandler.firstPage().paint(g, props);
    }

    @Override
    public String sticker()
    {
        return "OCD Document";
    }

    public OCDDocument writePDF(File file)
    {
        PDFWriter writer = new PDFWriter(this, file);
        writer.write();
        writer.dispose();
        return this;
    }

    public OCDWriter write()
    {
        // we're writing a complete new file
        return write(this.filePath);
    }

    public OCDWriter write(String path)
    {
        // we're writing a complete new file
        return new OCDWriter(this, new File(path)).create().write();
    }

    public OCDWriter rewrite()
    {
        return rewrite(this.filePath);
    }

    public OCDWriter rewrite(String path)
    {
        return new OCDWriter(this, new File(path)).write();
    }

    public static OCDDocument open(String path)
    {
        return open(new File3(path));
    }

    public static OCDDocument open(File file)
    {
        return file.exists() ? new OCDDocument(file) : null;
    }

    public synchronized final boolean load(File file)
    {
        File3 file3 = new File3(file);
        if (!file3.exists())
            Log.warn(this, ".load - file not found: " + file3.path());
        else if (!file3.isDirectory())
            try
            {
                this.filePath = file3.path();
                if (file3.isExtension(OCD.FILE_EXTENSION))
                {
                    // read root document xml containing manifest file
                    progression.start("Loading OCD File");

                    // loads document.xml
                    this.readEntryTry(zipEntry(this.entryPath()));

                    progression.setProgress(0.2);

                    // loads manifest.xml
                    if (this.hasZipEntry(manifest.entryPath))
                        this.manifest.clear().readEntryTry(zipEntry(manifest.entryPath));

                    progression.setProgress(0.6);

                    Stringer surlisted = new Stringer();
                    for (OCDItem item : this.manifest)
                    {
                        if (!this.zipFile.has(item.filePath()))
                        {
                            surlisted.span(item.filePath(), ", ");
                            continue;
                        }

                        if (item.isFontType())
                            this.fontHandler.addEntry(item);
                        else if (item.isImageType())
                            this.imageHandler.addEntry(item);
                        else if (item.isThumbType())
                            this.thumbHandler.addEntry(item);
                        else if (item.isAudioType())
                            this.audioHandler.addEntry(item);
                        else if (item.isVideoType())
                            this.videoHandler.addEntry(item);
                        else if (item.isAddonType())
                            this.addonHandler.addEntry(item);
                        else if (item.isPageType())
                            this.pageHandler.addEntry(item);
                        else
                        {
                            String path = item.filePath();
                            // Log.debug(this, ".load - manifest item: " + path);
                            ZipItem entry = zipEntry(path);
                            this.metadata.readEntryTry(entry);
                            this.navigation.readEntryTry(entry);
                            this.styles.readEntryTry(entry);
                        }
                    }

                    progression.setProgress(0.8);

                    // recovers from poor manifest
                    Stringer unlisted = new Stringer();
                    for (ZipItem entry : zipFile().list())
                    {
                        String path = entry.path();
                        if (this.manifest.hasItem(path))
                            continue;
                        unlisted.span(path, ", ");
                        this.readEntryTry(entry);
                        this.styles.readEntryTry(entry);
                        if (path.contains("font") && !path.contains("font-maps"))
                            this.fontHandler.addEntry(entry);
                        else if (path.contains("image"))
                            this.imageHandler.addEntry(entry);
                        else if (path.contains("addon"))
                            this.addonHandler.addEntry(entry);
                        else if (path.contains("page"))
                            this.pageHandler.addEntry(entry);
                    }
                    if (surlisted.isPopulated())
                        Log.debug(this, ".load - manifest but not found: " + surlisted);
                    if (unlisted.isPopulated())
                        Log.debug(this, ".load - not listed in manifest: " + unlisted.removeLast(2));
                } else
                    Log.warn(this, ".load - file format should be ocd: file=" + file3.getName());
            } catch (Exception ex)
            {
                Log.error(this, ".load - " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        progression.complete("OCD Document Loaded");

        // Log.debug(this, ".load - imageHandler=" + imageHandler.map());
        return true;
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        this.close();
    }

    public boolean existsZipEntry(String path)
    {
        Zip zip = this.zipFile();
        return zip == null ? false : zip.has(path);
    }

    public void loadZipEntry(OCDEntry entry)
    {
        entry.readNode(zipFile().stream(entry.entryPath()));
    }

    public InputStream zipStream(String path)
    {
        Zip zip = zipFile();
        return zip == null ? null : zip.stream(path);
    }

    public byte[] zipBytes(String path)
    {
        Zip zip = zipFile();
        return zip == null ? null : zip.bytes(path);
    }

    public boolean requiresUniqueIDs()
    {
        return id == null || id.trim().length() == 0;
    }

    public String needFont(String fontname, String chars)
    {
        return needFont(fontname, chars, false);
    }

    public String needFont(String fontname, String chars, boolean override)
    {
        SVGFont font = fontHandler.needFont(fontname, chars, override);
        font.glyphs(override ? chars : font.missing(chars), true);
        return fontname;
    }

    public void freeFromMemory(boolean force)
    {
        this.pageHandler.freeFromMemory(force);
        this.imageHandler.freeFromMemory(force);
        this.thumbHandler.freeFromMemory(force);
        this.videoHandler.freeFromMemory(force);
        this.audioHandler.freeFromMemory(force);
    }

    public static OCDDocument Load(File3 file)
    {
        OCDDocument ocd = file != null && file.exists() && file.isExt(OCD.EXT) ? new OCDDocument(file) : null;
        if (file != null && ocd == null)
            Log.warn(OCDDocument.class, ".Load - unable to load file: " + file.path());
        return ocd;
    }

}
