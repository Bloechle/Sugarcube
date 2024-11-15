package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;

public abstract class FontReader extends PDFNode implements PDFGlyph.Interface
{
    protected FontDescriptor descriptor;

    protected FontReader(String type, FontDescriptor desc)
    {
        super(type, desc);
        this.descriptor = desc;
    }
}
