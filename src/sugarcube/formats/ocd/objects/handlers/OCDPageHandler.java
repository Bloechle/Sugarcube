package sugarcube.formats.ocd.objects.handlers;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.IntSet;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.common.system.io.ZipItem;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.OCD;
import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.document.OCDItem;

public class OCDPageHandler extends OCDHandler<OCDPage>
{
    public static final String TAG = "pages";

    public OCDPageHandler(OCDDocument ocd)
    {
        super(TAG, ocd);
    }

    public String createPageFilename(int pageNb)
    {
        String filename =  OCD.PageFilename(pageNb);
        while (has(filename))
            filename = OCD.PageFilename(++pageNb);
        return filename;
    }

    public OCDPage[] even()
    {
        return half(true);
    }

    public OCDPage[] odd()
    {
        return half(false);
    }

    public OCDPage[] half(boolean even)
    {
        List3<OCDPage> pages = new List3<>();
        int index = 0;
        for (OCDPage page : map.list())
            if ((++index % 2 == 0) == even)
                pages.add(page);

        return pages.toArray(new OCDPage[0]);
    }

    public OCDPage[] ranges(String ranges)
    {
        Log.debug(this, ".ranges - " + ranges);
        return Str.IsVoid(ranges) ? array() : ranges(ranges.split(","));
    }

    public OCDPage[] ranges(String[] ranges)
    {
        if (ranges == null || ranges.length == 0)
            return new OCDPage[0];
        IntSet set = Fx.ParseRanges(ranges);
        OCDPage[] array = array();
        List3<OCDPage> pages = new List3<>();
        for (Integer i : set)
            if (i > 0 && i <= array.length)
                pages.add(array[i - 1]);
        return pages.toArray(new OCDPage[0]);
    }

    public OCDPage[] array()
    {
        return map.list().toArray(new OCDPage[0]);
    }

    public String[] filenames()
    {
        return map.keyList().array();
    }

    public OCDPage firstPage()
    {
        return map.isEmpty() ? null : map.valueAt(0);
    }

    public OCDPage secondPage()
    {
        return map.size() < 2 ? null : map.valueAt(1);
    }

    public OCDPage penultimatePage()
    {
        return map.size() < 2 ? null : map.valueAt(map.size() - 2);
    }

    public OCDPage lastPage()
    {
        return map.isEmpty() ? null : map.valueAt(map.size() - 1);
    }

    public boolean isEmpty()
    {
        return this.map.isEmpty();
    }

    public OCDPage getPage(String filename, OCDPage def)
    {
        return map.get(File3.Extense(filename, Xml.FILE_EXTENSION), def);
    }

    public boolean hasPage(int nb)
    {
        return nb > 0 && nb <= map.size();
    }

    public OCDPage getPage(int nb)
    {
        return nb < 1 ? this.firstPage() : nb > map.size() ? this.lastPage() : map.valueAt(nb - 1);
    }

    public OCDPage getPage(int nb, OCDPage def)
    {
        return nb < 1 ? def : nb > map.size() ? def : map.valueAt(nb - 1);
    }

    public int nbOfPages()
    {
        return this.map.size();
    }

    // public OCDPage deletePage(int pageNb)
    // {
    // return this.deletePage(map.valueAt(pageNb - 1));
    // }
    //
    // public OCDPage deletePage(OCDPage page)
    // {
    // return this.deletePage(this.pageNumber(page));
    // }

    public OCDPage deletePage(String... filenames)
    {
        OCDPage page = null;
        for (String filename : filenames)
            page = remove(rename(filename));
        return page;
    }

    public int pageNumber(OCDPage page) // first page number is 1 (not zero)...
    {
        int i = 0;
        for (OCDPage p : map.values())
        {
            i++;
            if (p == page)
                return i;
        }
        return -1;
    }

    public int index(String filename)
    {
        filename = rename(filename);
        int i = 0;
        for (String key : map.keys())
        {
            if (filename.equals(key))
                return i;
            i++;
        }
        return -1;
    }

    public static String rename(String filename)
    {
        return File3.Extense(filename, Xml.FILE_EXTENSION);
    }

    @Override
    public OCDPage addEntry(OCDItem item)
    {
        OCDPage page = this.addEntry(this.document().zipEntry(item.filePath()));
        page.setWidth(item.props().floatValue("width", page.width()));
        page.setHeight(item.props().floatValue("height", page.height()));
        page.setType(item.props().get("page-type", page.type()));
        // Log.debug(this,
        // ".addEntry - "+item.filePath()+": "+page.width()+"x"+page.height());
        return page;
    }

    @Override
    public OCDPage addEntry(ZipItem zipEntry)
    {
        return this.add(new OCDPage(ocd, zipEntry));
    }

    public OCDPage addEntry(String filename)
    {
        return this.add(new OCDPage(ocd, OCD.PAGES_DIR + rename(filename)));
    }

    public OCDPage addEntry(String filename, int index)
    {
        return this.add(new OCDPage(ocd, OCD.PAGES_DIR + rename(filename)), index);
    }

    public synchronized OCDPage loadPage(OCDPage page)
    {
        return this.loadPage(page, null);
    }

    public synchronized OCDPage loadPage(OCDPage page, File3 file)
    {
        try
        {
            page.content(false).initState();
            if (file != null && file.exists())
                Xml.Load(page, file);
            else
                IO.Close(Xml.Load(page, ocd.zipStream(page.entryPath())));
        } catch (Exception ex)
        {
            Log.error(this, ".loadPage - failed: file=" + (file == null ? page.entryPath() : file));
            ex.printStackTrace();
        }
        return page;
    }

    public void freeFromMemory()
    {
        freeFromMemory(false);
    }

    public void freeFromMemory(boolean force)
    {
        OCDPage page;
        for (String key : map.keyArray())
            if ((page = map.get(key)) != null)
                page.freeFromMemory(force);
    }

}
