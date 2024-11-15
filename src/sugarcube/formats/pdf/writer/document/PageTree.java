package sugarcube.formats.pdf.writer.document;

import sugarcube.formats.ocd.objects.OCDDocument;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.object.DictionaryObject;
import sugarcube.formats.pdf.writer.core.writer.StringWriter;
import sugarcube.formats.pdf.writer.core.writer.Writer;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.util.ArrayList;

public class PageTree extends DictionaryObject
{
    private ArrayList<Page> pages = new ArrayList<>();

    public PageTree(PDFWriter writer, int[] pageNumbers) throws PDFException
    {
        super(writer);
        OCDDocument ocd = writer.getDocument();
        for (int p : pageNumbers)
            pages.add(new Page(ocd.getPage(p), getID(), writer));
    }

    @Override
    public void addDictionaryEntries() throws PDFException
    {
        addDictionaryEntry("Type", "Pages", Writer.NAME);
        //kids
        StringWriter writer = new StringWriter();
        writer.openArray();
        for (int p = 0; p < pages.size(); p++)
        {
            if (p > 0)
                writer.write(Lexic.SPACE);
            writer.writeIndirectReference(pages.get(p).getID());
        }
        writer.closeArray();
        addDictionaryEntry("Kids", writer.toString(), Writer.GENERIC_STRING);
        addDictionaryEntry("Count", pages.size(), Writer.INTEGER);
    }

    public ArrayList<Page> getPages()
    {
        return pages;
    }
}
