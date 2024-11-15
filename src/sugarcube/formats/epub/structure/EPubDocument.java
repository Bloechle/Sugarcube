package sugarcube.formats.epub.structure;

import sugarcube.common.data.collections.Couple;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Props;
import sugarcube.common.system.io.File3;
import sugarcube.formats.epub.EPubWriter;
import sugarcube.formats.epub.structure.EPubContentOPF.Manifest;
import sugarcube.formats.epub.structure.EPubContentOPF.Spine;
import sugarcube.formats.epub.structure.meta.Meta;
import sugarcube.formats.epub.structure.ncx.NCX;
import sugarcube.formats.epub.structure.xhtml.XHTML;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.document.OCDMetadata;
import sugarcube.formats.ocd.objects.metadata.dc.DC;
import sugarcube.formats.ocd.objects.metadata.dc.DCElement;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;

import java.util.List;
import java.util.UUID;

public class EPubDocument extends OCDNode
{
  public EPupContainer container;// dummy almost empty file
  public IBookDisplayOptions ibookOptions;// apple stuff
  public EPubContentOPF opf;// epub reader resource file
  public NCX ncx;// old epub2 navigation file used for back compatibility
  public EPubNavigation nav;// epub3 navigation file
  public String bookIdentifier;
  public OCDDocument ocd;
  public OCDMetadata meta;
  public String navTextTitle = "Main Content";
  public String navCoverTitle = "Cover";
  public EPubWriter writer;

  public EPubDocument(EPubWriter writer)
  {
    super("epub", null);
    this.writer = writer;
    this.ocd = writer.ocd;
    this.meta = ocd.metadata().copy();
    String uuid = "http://www.sugarcube.ch/e-books/" + System.currentTimeMillis();
    this.bookIdentifier = "urn:uuid:" + UUID.nameUUIDFromBytes(uuid.getBytes());
    this.container = new EPupContainer();
    this.ibookOptions = new IBookDisplayOptions(writer.props);
    this.opf = new EPubContentOPF(this, null, null, null, null);
  }

  public EPubDocument updateNavigation(OCDNavigation ocdNav)
  {
    if (ocdNav == null)
    {
      ocdNav = ocd.nav();
      if (!writer.props.keepToc())
        ocdNav = ocdNav.copy().populatePagesTOC(writer.pages.array(), XHTML.EXT);
      else if (writer.completeTOC)
        ocdNav = ocdNav.copy().completeTOC(writer.pages.array());
    }

    this.nav = new EPubNavigation(this, ocdNav);
    this.ncx = new NCX(this, ocdNav);
    return this;
  }

  public EPubDocument reopf()
  {
    this.opf = new EPubContentOPF(this, null, opf.manifest, null, null);
    return this;
  }

  public Manifest manifest()
  {
    return opf.manifest;
  }

  public Spine spine()
  {
    return opf.spine;
  }

  public OCDDocument ocd()
  {
    return ocd;
  }

  public String bookIdentifier()
  {
    return meta.value(DC.identifier, bookIdentifier);
  }

  public String title()
  {
    String title = meta.value(DC.title, ocd.fileName());
    return File3.Filename(title.equals("noname") ? ocd.fileName() : title, true);
  }

  //maurizio ext for meta
  public String titleId()
  {
    List<? extends DCElement> elements = this.meta.children();
    for (int e = 0; e < elements.size(); e++){
      DCElement element = elements.get(e);
      if (element.tag.equals("dc:title")){
        return element.props().value("id");
      }
    }
    return "";
  }

  //maurizio ext for meta
  public List3<Meta> meta(){
	  List3<DCElement> elements = this.meta.elements();
	  List3<Meta> meta = new List3<>();
	  for (DCElement element: elements){
		  if (element.tag.equals("meta")){
			  Meta data = new Meta();
			  data.setData(element.cdata());
			  Props props = element.props();
			  Couple<String, String>[] couples = props.couples();
			  for (Couple<String, String> couple: couples){
				  data.add(couple.first(), couple.second());
			  }
			  meta.add(data);
		  }
	  }
	  return meta;
  }

  public String author()
  {
    return meta.value(DC.creator, "-");
  }

  public String publisher()
  {
    return meta.value(DC.publisher, "-");
  }

  public String subject()
  {
    return meta.value(DC.subject, "-");
  }

  public String description()
  {
    return meta.value(DC.description, "-");
  }

  public String language()
  {
    return meta.value(DC.language, "en");
  }

  public int nbOfPages()
  {
    return ocd.pageHandler.nbOfPages();
  }
}
