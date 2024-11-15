package sugarcube.formats.ocd.objects.nav;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Nb;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.objects.*;

import java.util.Collection;

public class OCDNavigation extends OCDEntry
{
    public static final String TAG = "navigation";
    public OCDDocument ocd;
    public OCDBookmarks bookmarks;
    public OCDToc toc;
    public OCDPageList pageList;

    public OCDNavigation(OCDDocument parent)
    {
        super(TAG, parent, "nav.xml");
        this.ocd = parent;
        this.bookmarks = new OCDBookmarks(this);
        this.toc = new OCDToc(this);
        this.pageList = new OCDPageList(this);
    }

    public OCDNavItem navRoot()
    {
        OCDNavItem rootItem = new OCDNavItem(TAG, this);
        rootItem.items.add(bookmarks);
        rootItem.items.add(toc);
        rootItem.items.add(pageList);
        return rootItem;
    }

    public boolean hasBookmarks()
    {
        return this.bookmarks != null && bookmarks.nbOfChildren() > 0;
    }

    public boolean hasToc()
    {
        return this.toc != null && toc.nbOfChildren() > 0;
    }

    public boolean hasPageList()
    {
        return this.pageList != null && pageList.nbOfChildren() > 0;
    }

    public OCDBookmarks bookmarks()
    {
        return bookmarks;
    }

    public OCDToc toc()
    {
        return toc;
    }

    public OCDPageList pageList()
    {
        return pageList;
    }

    @Override
    public List3<OCDNode> children()
    {
        return new List3<>(bookmarks, toc, pageList);
    }

    @Override
    public Collection<? extends OCDNode> writeAttributes(Xml xml)
    {
        // xml.write("title", title);
        return this.children();
    }

    @Override
    public void readAttributes(DomNode dom)
    {
        // this.title = dom.value("title", title);
    }

    @Override
    public XmlINode newChild(DomNode child)
    {
        if (child.isTag(OCDBookmarks.TAG))
            return bookmarks;
        else if (child.isTag(OCDToc.TAG))
            return toc;
        else if (child.isTag(OCDPageList.TAG))
            return pageList;
        return null;
    }

    @Override
    public void endChild(XmlINode child)
    {
    }

    @Override
    public String sticker()
    {
        return "navigation";
    }

    public OCDNavigation populatePagesListAndTOC()
    {
        this.populatePagesTOC();
        this.populatePageList();
        return this;
    }

    public OCDNavigation populatePagesTOC()
    {
        int counter = 0;
        this.toc.clear();
        for (OCDPage page : ocd)
            this.toc.addChild(new OCDNavItem(toc, "Page-" + (++counter), page.entryFilename()));
        return this;
    }

    public OCDNavigation populatePageList()
    {
        int counter = 0;
        this.pageList.clear();
        for (OCDPage page : ocd)
            this.pageList.addChild(new OCDNavItem(pageList, "Page-" + (++counter), page.entryFilename()));
        return this;
    }

    public OCDNavigation populateStylesTOC()
    {

        this.toc.clear();
        OCDNavItem[] items = new OCDNavItem[5];
        // this.toc.addChild(new OCDNavItem(toc, "Cover",
        // ocd.firstPage().entryFilename()));
        for (OCDPage page : ocd)
        {
            OCDNavItem prevItem = null;
            for (OCDTextBlock block : page.content().blocks())
            {
                String h = block.classname(true).toLowerCase();
                switch (h)
                {
                    case "h1":
                    case "h2":
                    case "h3":
                    case "h4":
                    case "h5":
                        int level = Nb.Int(h.replace("h", ""), 1);
                        if (prevItem != null && prevItem.level() == level)
                        {
                            prevItem.text += " - " + block.uniString(true);
                        } else
                            prevItem = addItem(items, page, block, level);
                        break;
                    default:
                        prevItem = null;
                        break;
                }

            }
        }
        return this;
    }

    public OCDNavItem addItem(OCDNavItem[] items, OCDPage page, OCDTextBlock block, int level)
    {
        level--;
        OCDNavItem item = new OCDNavItem(toc, block.uniString(true), page.entryFilename());
        items[level] = item;
        if (level == 0)
            this.toc.addChild(item);
        else if (items[level - 1] != null)
            items[level - 1].addChild(item);
        return item;
    }

    public OCDNavigation populatePagesTOC(String[] pages, String ext)
    {
        this.toc.populateFlatTOC(pages, ext);
        return this;
    }

    public OCDNavigation completeTOC(String[] pages)
    {
        this.toc.completeTOC(pages);
        return this;
    }

    @Override
    public OCDNavigation copy()
    {
        OCDNavigation node = new OCDNavigation(ocd);
        copyTo(node);
        return node;
    }

    public void copyTo(OCDNavigation node)
    {
        super.copyTo(node);

        node.toc = toc.copy();
        node.bookmarks = bookmarks.copy();
        node.pageList = pageList.copy();
    }
}