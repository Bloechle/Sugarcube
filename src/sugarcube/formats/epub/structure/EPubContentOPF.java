package sugarcube.formats.epub.structure;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringList;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.Mime;
import sugarcube.common.system.time.DateUtils;
import sugarcube.common.data.xml.XmlNodeProps;
import sugarcube.formats.epub.EPubProps;
import sugarcube.formats.ocd.objects.handlers.OCDPageHandler;

public class EPubContentOPF extends XmlNodeProps
{
  public static final String RENDITION = "rendition: http://www.idpf.org/vocab/rendition/#";
  public static final String RENDITION_IBOOKS = RENDITION + " ibooks: http://vocabulary.itunes.apple.com/rdf/ibooks/vocabulary-extensions-1.0/";
  public static final String BOOK_ID = "bookid";

  public class Metadata extends XmlNodeProps
  {
    public Metadata()
    {
      super("metadata");
      EPubProps props = epub.writer.props;
      this.addAttributes("xmlns:dc", "http://purl.org/dc/elements/1.1/");
      this.addAttributes("xmlns:opf", "http://www.idpf.org/2007/opf");
      this.addChild("dc:identifier", "id", BOOK_ID, props.get("dc_identifier", epub.bookIdentifier()));
      String titleId = epub.titleId();
      if (titleId == null || titleId.length() == 0)
        this.addChild("dc:title", props.get("dc_title", epub.title()));
      else
        this.addChild("dc:title", "id", titleId, props.get("dc_title", epub.title()));
      this.addChild("dc:language", props.get("dc_language", epub.language()));
      this.addChild("dc:creator", props.get("dc_creator", epub.author()));
      this.addChild("dc:publisher", props.get("dc_publisher", epub.publisher()));
      this.addChild("dc:subject", props.get("dc_subject", epub.subject()));
      this.addChild("dc:description", props.get("dc_description", epub.description()));
      this.addChild("dc:contributor", "ePUBReplica - sugarcube.ch");      
      this.addChild("meta", "property", "dcterms:modified", DateUtils.universalTime());
      this.addChild("meta", "property", "rendition:layout", props.lower(EPubProps.KEY_LAYOUT, "pre-paginated"));
      this.addChild("meta", "property", "rendition:orientation", props.lower(EPubProps.KEY_ORIENTATION, "auto"));
      this.addChild("meta", "property", "rendition:spread", props.lower(EPubProps.KEY_SPREAD, "auto"));
      this.addChild("meta", "property", "ibooks:specified-fonts", "true");
      // add other meta
      List3<sugarcube.formats.epub.structure.meta.Meta> meta = epub.meta();
      for (sugarcube.formats.epub.structure.meta.Meta entry : meta)
      {
        this.addChild("meta", entry.asStringArray());
      }

      if (props.isMobi())
      {
        // todo remove cover from guide
        int w = Math.round(epub.writer.props.dispWidth());
        Rectangle3 box = epub.writer.mobiViewbox;
        int h = box == null ? Math.round(w * 16f / 10f) : Math.round(box.height * epub.writer.props.dispWidth() / box.width);

        // 1200 corresponds to Kindle Fire screen pixel width, it is inherited
        // as kindle fire logical width
        if (w != 1200)
          Log.debug(this, " - writing kindle epub with non 1200px width: " + w);

        this.addChild("meta", "name", "fixed-layout", "content", "true");
        this.addChild("meta", "name", "original-resolution", "content", w + "x" + h);
      }
      this.addMeta("cover", "cover-image");
    }
  }

  public class Meta extends XmlNodeProps
  {
    public Meta()
    {
      super("meta");
    }
  }

  // All files included in EPUBs must be listed in the EPUB manifest (OPF file).
  // EPUBs containing unmanifested files
  // (that is, files not listed in the manifest) will fail import, as these
  // files are by definition not intentionally included.
  public class Manifest extends XmlNodeProps
  {
    private StringSet items = new StringSet();// duplicate check

    public Manifest()
    {
      super("manifest");
    }

    public final void addItem(String id, String href, String... props)
    {
      String mime = Mime.get(href, null);
      if (mime == null)// svg may be referenced without ".svg" extension
        mime = Mime.get(href = File3.Extense(href, ".svg"), null);
      if (!items.has(href))
      {
        StringList list = new StringList("id", id, "href", href, "media-type", mime);
        this.addChild("item", list.addAll3(props).array());
        items.add(href);
      }

    }
  }

  // The <spine> section of the OPF file indicates the linear reading order of
  // the book’s content (XHTML) files.
  // When the person reading the book uses “next page” navigation, the pages are
  // displayed based on the spine
  // order. Each spine item is identified by idref and that value must match the
  // <item id> listed in the
  // <manifest> for the corresponding spine item

  // All EPUB CFI step sequences begin with the spine, which is the required
  // third element of the package.
  // first level: cdata/1 <metadata>/2 cdata/3 <manifest>/4 cdata/5 <spine>/6
  // cdata/7 <guide>/8
  // second level: cdata/1 <itemref idref="cover">/2 cdata/3 <itemref
  // idref="page-001">/4, etc.
  // ! means follow the reference: itemref -> item -> page-001.xhtml
  // content.opf#epubcfi(/6/4!)
  // content.opf#epubcfi(/6/4[page-001]!) is better
  // /6/4[page-001]!/4/2/2 <body> <div class="root"> <svg>
  public class Spine extends XmlNodeProps
  {
    public Spine()
    {
      super("spine");
      this.addAttributes("toc", "ncx");
      // this.addAttributes("unique-identifier", epub.bookIdentifier());
//      if (epub.writer.props.isEpub())
//        this.addItemRef("cover", false);
    }

    public final void addItemRef(String idref, boolean linear)
    {
      this.addChild("itemref", "idref", idref, "linear", linear ? "yes" : "no");
    }
  }

  // The <guide> block of the OPF file lists the key components of the book,
  // such as the cover page, table of
  // contents, bibliography and so on. The guide elements tell iBooks where the
  // key parts of the book are to
  // make it easier for readers to navigate them.
  // cover, toc, text, foreword, index, title-page, preface, copyright-page,
  // bibliography...
  public class Guide extends XmlNodeProps
  {
    public Guide()
    {
      super("guide");
      OCDPageHandler pages = epub.ocd.pageHandler;
      this.addReference("cover", epub.navCoverTitle, EPub.COVER_FILE);
      // required by ibook: text reference is required for fixed layout books
      // and is used to determine the start of the book's main content for
      // sugarcube.app.sample cutting purposes
      this.addReference("text", epub.navTextTitle,
          epub.writer.props.get(EPubProps.KEY_MAIN_CONTENT, EPub.pageFilepath(pages.firstPage().entryFilename())));
    }

    public final void addReference(String type, String title, String href)
    {
      this.addChild("reference", "type", type, "title", title, "href", href);
    }
  }

  private static final String TAG = "package";
  private EPubDocument epub;
  public Metadata metadata;
  public Manifest manifest;
  public Spine spine;
  public Guide guide;

  public EPubContentOPF(EPubDocument epub, Metadata metadata, Manifest manifest, Spine spine, Guide guide)
  {
    super(TAG);
    this.epub = epub;

//    boolean isEpub = epub.writer.props.isEpub();
    this.addAttributes("xmlns", "http://www.idpf.org/2007/opf", "unique-identifier", BOOK_ID, "version", "3.0", "prefix", RENDITION_IBOOKS);

    this.metadata = metadata == null ? new Metadata() : metadata;
    this.manifest = manifest == null ? new Manifest() : manifest;
    this.spine = spine == null ? new Spine() : spine;
    if (epub.writer.props.showOpfGuide())
      this.guide = guide == null ? new Guide() : guide;
    else
      this.guide = null;

    this.addChildren(this.metadata, this.manifest, this.spine, this.guide);
  }
  
}
