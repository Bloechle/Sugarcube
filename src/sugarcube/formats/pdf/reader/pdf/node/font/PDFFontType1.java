package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class PDFFontType1 extends PDFFont
{
    public PDFFontType1(PDFNode parent, PDFDictionary fontMap)
    {
        super(parent, fontMap);
        finalizeConstruction();
    }
}
