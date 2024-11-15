package sugarcube.formats.pdf.reader.pdf.node.annotation;

import sugarcube.common.data.collections.Str;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class PDFWidgetAnnot extends PDFLinkAnnot
{
    public String FT = "";
    public String T = "";
    public String TU = "";


    public PDFWidgetAnnot(PDFNode parent, PDFDictionary map)
    {
        super(parent, map, Dexter.WIDGET);

        this.FT = map.get("FT").stringValue();
        this.T = map.get("T").stringValue();
        this.TU = map.get("TU").stringValue();
    }

    public boolean isWidgetForm()
    {
        return Str.HasChar(FT);
    }

    @Override
    public String sticker()
    {
        return "WidgetAnnot" + reference();
    }

    @Override
    public String toString()
    {
        return "WidgetAnnot" + reference() + "\nBounds" + bounds + "\nGotoRef[" + gotoRef + "]" + "\nGotoURI[" + gotoURI + "]" + "\nGotoAction["
                + gotoAction + "]" + "\nContents[" + contents + "]";
    }
}
