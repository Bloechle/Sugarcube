package sugarcube.formats.pdf.reader.struct;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.pdf.node.PDFMark;
import sugarcube.formats.pdf.reader.pdf.node.PDFPage;
import sugarcube.formats.pdf.reader.pdf.node.struct.PDFPageStruct;
import sugarcube.formats.pdf.reader.pdf.node.struct.PDFStructElem;
import sugarcube.formats.pdf.reader.pdf.node.struct.PDFStructTreeRoot;
import sugarcube.formats.ocd.analysis.ContentGrouper;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDPaintable;
import sugarcube.formats.ocd.objects.OCDText;

public class GenericStruct
{
    public static final String PRODUCER_ACROBAT = "Acrobat";
    public static final String PRODUCER_WORD = "MsWord";
    public static final String PRODUCER_ABBYY = "ABBYY";
    protected Dexter writer;
    protected PDFStructTreeRoot struct;
    public PDFPageStruct pageStruct;
    public OCDPage page;
    public boolean useStructure = true;

    public GenericStruct(PDFStructTreeRoot struct, PDFPageStruct pageStruct, OCDPage page, Dexter writer)
    {
        this.struct = struct;
        this.page = page;
        this.pageStruct = pageStruct;
        this.writer = writer;
    }

    public PDFStructTreeRoot root()
    {
        return this.struct;
    }

    public boolean isProducer(String producer)
    {
        return this.struct != null && this.struct.isProducer(producer);
    }

    public void dontUseStructure()
    {
        this.useStructure = false;
    }

    public boolean assign(PDFMark mark, OCDPaintable node)
    {
        if (useStructure && node != null && mark != null)
        {
            if(!mark.tag.equalsIgnoreCase("span"))
            {
                node.groupID = mark.mcid;
                if (mark.mcid > -1)
                    node.props().put("mcid", mark.mcid);
                node.setLabel(mark.tag);
                return true;
            }
        }
        return false;
    }

    public boolean assign(PDFMark mark, OCDText text)
    {

        if (assign(mark, (OCDPaintable) text) && pageStruct != null)
        {
            PDFStructElem elem = pageStruct.get(mark.mcid);
            if (elem == null)
            {
                Log.debug(this, ".assign - structElem not found, mcid=" + mark.mcid + ", ids=" + pageStruct.mcids.keySet() + ", text=" + text.string());
            }

            while (elem != null && Str.Equals(elem.name, "Span", "Link"))
            {
                elem = elem.parentElement();

                if (elem != null)
                {
                    text.groupID = 1000000 + elem.reference().id();
                    text.setLabel(elem.name);
                }
            }

            return true;
        }

        return false;
    }

    public boolean process(OCDPage page)
    {
        ContentGrouper grouper = new ContentGrouper();
        for (OCDPaintable p : page.content())
        {
            String mcid = p.props().get("mcid");
            String label = p.label();
//      Log.debug(this, ".process - mcid=" + mcid + ", gid=" + p.groupID + ", label=" + label + ", tag=" + p.tag);

            if (mcid != null && label != null)
            {
                switch (label)
                {
                    case "Figure":
                        grouper.add(p, label, mcid);
                        break;
                    case "P":
                        grouper.add(p, label, mcid);
                        break;
                    default:
                        Log.debug(this, ".process - unknown label: " + label);
                        break;
                }
            }
            p.props().remove("mcid");
        }

        grouper.regroup(page);

        return true;
    }

    public static GenericStruct Instance(PDFStructTreeRoot struct, PDFPage pdfPage, OCDPage page, Dexter writer)
    {
        PDFPageStruct pageStruct = struct == null ? null : struct.getPageStruct(pdfPage.reference());
        if (pageStruct == null)
            return new GenericStruct(struct, pageStruct, page, writer);
        else if (pageStruct.root().isProducedByWord())
            return new MsWordStruct(struct, pageStruct, page, writer);
        else
            return new GenericStruct(struct, pageStruct, page, writer);
    }
}
