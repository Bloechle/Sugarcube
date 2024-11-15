package sugarcube.formats.epub.structure;

import sugarcube.common.system.io.File3;
import sugarcube.formats.epub.EPubProps;
import sugarcube.formats.epub.structure.nav.*;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.nav.OCDNavItem;
import sugarcube.formats.ocd.objects.nav.OCDNavigation;
import sugarcube.formats.ocd.objects.nav.OCDToc;

public class EPubNavigation extends NavDocument
{
  public EPubDocument epub;
  public transient String lastLink = "";

  public EPubNavigation(EPubDocument epub, OCDNavigation nav)
  {
    super(epub, epub.title());
    this.epub = epub;
    OCDToc toc = nav.toc();
    NavToc navToc = body.needToc();
    navToc.h1 = new NavH1(navToc, languageTOC(epub.language()));
    this.populateTOC(0, navToc.items, toc);
    this.populateLandmarks();
    this.populatePageList(nav);

    String layout = props.lower(EPubProps.KEY_LAYOUT, "pre-paginated");
    String spreads = props.lower(EPubProps.KEY_SPREAD, "auto");

    String styles = ((layout.equals("reflow") ? "reflow " : "") + (spreads.equals("none") ? "nospread" : "")).trim();
    if (!styles.isEmpty())
      this.body.addAttribute("class", styles);
  }

  public String languageTOC(String language)
  {
    language = language == null ? "" : language.toLowerCase().trim();
    switch (language)
    {
    case "fr":
      return "Sommaire";
    case "de":
      return "Inhaltsverzeichnis";
    }
    return "Table of Contents";
  }

  public void populateTOC(int level, NavList list, OCDNavItem item)
  {
    NavItem li = level == 0 ? null : list.addItem();
    if (level > 0)
    {
      if (item.hasLink())
        li.addLink(lastLink = item.xhtmlLink(), item.text);
      else if (item.nbOfChildren() > 0)
        li.addSpan(item.text);
      else
        li.addLink(lastLink, item.text);
    }

    if (item.nbOfChildren() > 0)
    {
      list = level++ == 0 ? list : li.addList();
      for (OCDNavItem child : item)
        populateTOC(level, list, child);
    }
  }

  public void populateLandmarks()
  {
    NavList list = body.needLandmarks().items();
    String filename = epub.writer.props.get(EPubProps.KEY_MAIN_CONTENT, File3.Extense(epub.ocd.pageHandler.firstPage().entryFilename(), ".xhtml"));
    list.addLinkItem(filename, "Cover", "epub:type", "cover");
    list.addLinkItem(filename, "Main Content", "epub:type", "bodymatter");

  }

  public void populatePageList(OCDNavigation nav)
  {

    NavPageList pages = body.needPageList();
    NavList list = pages.items();

    if (nav.hasPageList())
    {
      int pageNb = 0;
      for (OCDNavItem item : nav.pageList)
      {
        if (item.link != null)
          list.addLinkItem(item.link.replace(".xml", ".xhtml"), "" + (++pageNb));
      }

    } else
      for (OCDPage page : epub.ocd.pages())
        list.addLinkItem(page.entryFilename().replace(".xml", ".xhtml"), "" + page.number());

  }

}
