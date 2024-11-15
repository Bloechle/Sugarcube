package sugarcube.formats.epub;

import sugarcube.common.system.Prefs;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.util.Regex3;
import sugarcube.common.data.collections.*;
import sugarcube.common.data.Base;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.ZipItem;
import sugarcube.common.system.io.Zipper;
import sugarcube.common.system.io.hardware.Memory;
import sugarcube.common.system.process.Progression;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.common.data.xml.css.CSSBuilder;
import sugarcube.formats.epub.structure.EPub;
import sugarcube.formats.epub.structure.EPubDocument;
import sugarcube.formats.epub.structure.res.RS;
import sugarcube.formats.epub.structure.EPubContentOPF;
import sugarcube.formats.ocd.objects.OCDPageProcessor;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.font.SVGFont;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;

import java.io.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public abstract class EPubWriter
{
  static
  {
    Prefs.Need();
  }

  public int svgViews = 0;
  public Progression progression;
  public StringSet pageIDs = new StringSet();
  public StringSet images = new StringSet();
  public StringSet media = new StringSet();
  public StringSet pages = new StringSet();
  public StringSet svgPages = new StringSet();
  public StringMap<SVGFont> fontsRemapped = new StringMap<>();
  public Zipper zip;
  public OCDDocument ocd;
  public EPubDocument epub;
  public Image3 cover = null;
  public OCDPageProcessor processor;
  public boolean doTrack = false;
  public File3 file;

  public EPubProps props = new EPubProps();

  public boolean debug = false;
  public Color3 linkColor = null;
  public boolean trimClips = true;
  public Pattern urlPattern = Pattern.compile(Regex3.URL);
  public Pattern emailPattern = Pattern.compile(Regex3.EMAIL);

  public String error = null;
  // public boolean keepTOC = true;
  public boolean completeTOC = false;
  public int splitSpread = 1;

  public Rectangle3 mobiViewbox = null;
  public transient Memory<Void> memory = new Memory<Void>(this);

  public StringList cssFiles = new StringList();
  public StringList jsFiles = new StringList();

  public String ebookCss = "";
  public OCDNavigation nav = null;

  public transient int pageWidth;
  public transient int pageHeight;
  public transient Rectangle3 viewBox;
  public transient float pageScale = 1f;

  public EPubWriter(OCDDocument ocd, File file, Progression progression)
  {
    this.ocd = ocd;
    this.file = new File3(file);
    this.file = this.file.isExtension(".zip") ? this.file : this.file.extense(EPub.FILE_EXTENSION);
    // first step metadata, last step
    this.progression = progression == null ? new Progression() : progression;
    this.progression.setName("Replica");
    this.progression.setNbOfSteps(ocd.pageHandler.nbOfPages() + 2);

    OCDPage first = ocd.pageHandler.firstPage();
    if (first != null)
    {
      first.ensureInMemory();
      mobiViewbox = first.viewBox();
    }
  }

  public EPubWriter processor(OCDPageProcessor processor)
  {
    this.processor = processor;
    return this;
  }

  public File3 file()
  {
    return file;
  }

  public Image3 cover()
  {
    return cover;
  }

  public Rectangle3 rebox(Rectangle3 box)
  {
    return box.shiftBack(viewBox.xy()).scale(pageScale);
  }

  protected boolean process(OCDPage page)
  {
    boolean inMemory = page.isInMemory();
    page.ensureInMemory();
    if (processor != null)
      processor.process(page);
    return inMemory;
  }

  public Props props()
  {
    return this.props;
  }

  public void cancel()
  {
    this.progression.cancel();
  }

  public String uniqueID(String prefix)
  {
    int size = 4;
    String id = prefix + Base.x32.random(size);
    while (pageIDs.has(id))
      id = prefix + Base.x32.random(size++);
    pageIDs.add(id);
    return id;
  }

  public abstract void writePages();


  public boolean write()
  {
    // write sequence: header, pages, and resources (since resources are updated
    // during page writing)

    memory.reset("Starts writing " + file.name());
    progression.start("Converting to EPUB - " + ocd.fileName());
    if (this.writeHeader())
      try
      {
        this.epub = new EPubDocument(this);
        manifest("ncx", EPub.NCX_FILE);
        manifest("nav", EPub.NAV_FILE, "properties", "nav");
        // manifest("cover", EPub.COVER_FILE, "properties", "svg");
        manifest("cover-image", EPub.IMAGE_FOLDER + EPub.COVER_IMG_FILE, "properties", "cover-image");

        this.props.normalize();
        for (SVGFont font : this.ocd.fontHandler)
          font.updateGlyphRemapOverrides();

        this.writePages();
        this.epub.reopf();
        this.writeResources();
        this.zip.dispose();

        Log.debug(this, "write - zip closed");

        if (props.doEpubCheck())
          this.props.put("epubcheck_report", EPubChecker.Check(file));

        Log.info(this, ".write - File written successfully: " + this.file.getAbsolutePath());
        progression.stepAchieved("File Written Successfully - " + this.file.getAbsolutePath());
        progression.setDescription("EPUB Conversion - " + ocd.fileName() + ": done");
        progression.complete();

        this.zip = null;
        this.epub = null;

      } catch (Exception e)
      {
        Log.warn(this, ".write - Some problem occured during EPUB file writing operation: " + e);
        error = e.getMessage();
        e.printStackTrace();
      }
    else
    {
      progression.setDescription("EPUB Conversion - " + ocd.fileName() + ": Unable to write file: " + error);
      Log.info(this, ".write - Unable to write file: " + error);
      progression.complete();
      return false;
    }
    memory.snap("EPUB file written");
    memory.logMax();
    return true;
  }

  public boolean writeHeader()
  {
    try
    {
      this.file.needDirs(false);
      this.zip = new Zipper(file);
      if (file.isExtension(EPub.FILE_EXTENSION))
        this.zip.writeMime("application/epub+zip");// setCrc(0x2cab616f);
      return true;
    } catch (IOException e)
    {
      Log.warn(this, ".writeHeader - write error: " + e);
      error = e.getMessage();
    }
    return false;
  }

  public boolean write(String filepath, Image3 image)
  {
    // Log.debug(this, ".writeImage - "+filepath+": quality="+jpgQuality);
    if (image != null && !progression.canceled())
      try
      {
        zip.putNextEntry(new ZipEntry(filepath));
        OutputStream stream = new BufferedOutputStream(zip);
        image.write(stream, Str.Ends(filepath.toLowerCase(), ".jpg", ".jpeg") ? props.jpeg() : -1);
        stream.flush();
        stream = null;
        zip.flush();
        zip.closeEntry();
        return true;
      } catch (Exception e)
      {
        Log.warn(this, ".write - write error: " + e);
        e.printStackTrace();
      }
    return false;
  }

  // public void writeClassData(String filepath, ClassPath classpath)
  // {
  // this.writeData(filepath, classpath.bytes());
  // }

  public void writeCopy(String filepath, ZipItem entry)
  {
    if (!progression.canceled())
      try
      {
        this.zip.putNextEntry(new ZipEntry(filepath));
        BufferedInputStream is = entry.bufferedStream();
        byte[] buffer = new byte[entry.intSize()];
        int bytesRead;
        if (is != null && buffer.length > 0)
          while ((bytesRead = is.read(buffer)) != -1)
            zip.write(buffer, 0, bytesRead);
        this.zip.flush();
        this.zip.closeEntry();
      } catch (Exception e)
      {
        Log.warn(this, ".writeCopy - write error: " + e);
        e.printStackTrace();
      }
  }

  public void write(String filepath, byte[] data)
  {
    if (data == null || data.length == 0)
      Log.debug(this, ".write - no data: " + filepath);
    else if (!progression.canceled())
      try
      {
        Log.debug(this, ".write - " + filepath + ": " + data.length);
        this.zip.putNextEntry(new ZipEntry(filepath));
        this.zip.write(data, 0, data.length);
        this.zip.flush();
        this.zip.closeEntry();
      } catch (Exception e)
      {
        Log.warn(this, ".write - write error: " + e);
        e.printStackTrace();
      }
  }

  public void write(String filepath, XmlINode node, String... headerLines)
  {
    // if (filepath.endsWith(".svg"))
    // {
    // Log.debug(this, ".writeXml - " + filepath);
    // return;
    // }
    Log.debug(this, ".write - " + filepath);

    if (!progression.canceled())
      try
      {
        this.zip.putNextEntry(new ZipEntry(filepath));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.zip, EPub.UTF8));
        Xml xml = new Xml(writer, 1, true);
        xml.setNumberOfDecimals(props.integer(EPubProps.KEY_DECIMALS, 3));
        xml.writeHeader(headerLines);
        xml.write(node);
        xml.close();
        xml = null;
        // never close writer since it will close zip stream too, thus disabling
        // further zip data to be written
        writer.flush();
        writer = null;
        this.zip.flush();
        this.zip.closeEntry();
      } catch (IOException e)
      {
        Log.warn(this, ".write - error: " + e);
        e.printStackTrace();
      }
  }

  public void write(String filepath, String data)
  {
    if (!progression.canceled())
      try
      {
        this.zip.putNextEntry(new ZipEntry(filepath));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.zip, EPub.UTF8)); // "UTF-8"
        writer.write(data);
        writer.flush();
        writer = null;
        this.zip.flush();
        this.zip.closeEntry();
      } catch (IOException e)
      {
        Log.warn(this, ".write - error: " + e);
        e.printStackTrace();
      }
  }

  public String r(double d)
  {
    return "" + (Math.round(d * 100) / 100.0);
  }

  public void updateNav()
  {
    epub.updateNavigation(nav);
  }

  public void writeCover()
  {
    this.cover = ocd.needCover(1600, props.jpeg());
    this.write(EPub.IMAGE_DIR + EPub.COVER_IMG_FILE, cover);
    // this.write(EPub.COVER_PATH, RS.String(RS.COVER_XHTML).replace("$width$",
    // cover.width() + "").replace("$height$", cover.height() + ""));
  }

  public void writeResources()
  {
    boolean fontBase64 = props.font64();

    this.updateNav();
    this.writeCover();
    // SMIL
    // <item id="pg001" href="page001.xhtml" media-type="application/xhtml+xml"
    // media-overlay="moPage001"/>
    // <item id="moPage001" href="smil/page001.smil"
    // media-type="application/smil+xml"/>

    // NEVER list META-INF files in the manifest (Apple directive)
    for (String cssFile : cssFiles)
      manifest(cssFile.replace(".", "-"), EPub.CSS_FOLDER + cssFile);
    for (String jsFile : jsFiles)
      manifest(jsFile.replace(".", "-"), EPub.JS_FOLDER + jsFile);
    // manifest("icon-audio-close", EPub.IMAGE_FOLDER + RS.ICON_CLOSE);
    // write(EPub.IMAGE_DIR + RS.ICON_CLOSE, RS.bytes(RS.ICON_CLOSE));

    if (!fontBase64)
      for (String id : fontsRemapped.keySet())
        manifest(EPub.ID(id + "_otf"), EPub.FONT_FOLDER + id + ".otf");

    for (String id : images)
      manifest(EPub.ID(id), EPub.IMAGE_FOLDER + id);
    for (String id : media)
      manifest(EPub.ID(id).replace("/", "_"), id);

    EPubContentOPF.Spine spine = epub.spine();

//    if (props.bool(EPubProps.KEY_IE9, false))
//      spine.addItemRef(pages.first(), true);

    for (String pageName : pages)
    {
      pageName = EPub.ID(pageName);
      String pageProps = ((svgPages.contains(pageName) ? "svg " : "") + "scripted ").trim();
      if (Str.HasChar(pageProps))
        manifest(pageName, EPub.pageFilepath(pageName), "properties", pageProps);
      else
        manifest(pageName, EPub.pageFilepath(pageName));
      spine.addItemRef(pageName, true);
    }

    write(EPub.CONTAINER_PATH, epub.container);

    if (props.isEpub())
      write(EPub.IBOOK_DISPLAY_PATH, epub.ibookOptions);
    write(EPub.OPF_PATH, epub.opf);
    write(EPub.NCX_PATH, epub.ncx);
    write(EPub.NAV_PATH, epub.nav);

    CSSBuilder fontsCss = new CSSBuilder();
    fontsCss.writeComment("BEGIN FONT PART");
    if (!fontBase64 && !fontsRemapped.isEmpty())
      for (String font : fontsRemapped.keySet())
        fontsCss.writeFont(font, EPub.FONT_FOLDER + font + ".otf");
    fontsCss.writeComment("END FONT PART");

    for (String cssFile : cssFiles)
    {
      String css = RS.String(cssFile);
      switch (cssFile)
      {
      case RS.FONTS_CSS:
        css += "\n" + fontsCss.toString();
        break;
      case RS.EBOOK_CSS:
        css += "\n" + ebookCss;
        break;
      }
      write(EPub.CSS_DIR + cssFile, css);
    }
    
    for (String jsFile : jsFiles)
      write(EPub.JS_DIR + jsFile, RS.Bytes(jsFile));

  }

  public final void manifest(String id, String href, String... props)
  {
    epub.opf.manifest.addItem(id, href, props);
  }


  public void dispose()
  {
    this.cover = null;
  }

  public static void main(String... args)
  {

  }
}
