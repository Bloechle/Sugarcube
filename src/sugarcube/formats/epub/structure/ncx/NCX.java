package sugarcube.formats.epub.structure.ncx;

import sugarcube.common.data.xml.XmlNodeProps;
import sugarcube.formats.epub.structure.EPubDocument;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;

//An NCX (Navigation Control XML) file defines a Table of Contents for a book. Two tags can be used to provide
//navigation within the EPUB: <navMap>, which is the actual table of contents and <pageList>, which provides
//page mapping. Both are described in the sections below.
public class NCX extends XmlNodeProps
{
  public class Head extends XmlNodeProps
  {
    public Head()
    {
      super("head");
      this.addMeta("dtb:uid", epub.bookIdentifier());
      this.addMeta("dtb:depth", "-1");//display depth!!!
      this.addMeta("dtb:totalPageCount", "" + epub.nbOfPages());
      this.addMeta("dtb:maxPageNumber", "" + epub.nbOfPages());
    }
  }

  public class DocTitle extends XmlNodeProps
  {
    public DocTitle()
    {
      super("docTitle");
      this.addChild("text", epub.title());
    }
  }

  public class DocAuthor extends XmlNodeProps
  {
    public DocAuthor()
    {
      super("docAuthor");
      this.addChild("text", epub.author());
    }
  }  
  public EPubDocument epub;

  public NCX(EPubDocument epub, OCDNavigation nav)
  {
    super("ncx");
    this.epub = epub;
    this.addAttribute("xml:lang", epub.language());
    this.addAttribute("xmlns", "http://www.daisy.org/z3986/2005/ncx/");
    this.addAttribute("version", "2005-1");
    this.addChildren(new Head(), new DocTitle(), new DocAuthor(), new NCXNav(epub.ocd, nav));
  }
}
