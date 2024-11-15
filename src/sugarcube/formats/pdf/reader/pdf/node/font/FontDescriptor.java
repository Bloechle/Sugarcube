package sugarcube.formats.pdf.reader.pdf.node.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.Graphics3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.font.aff.AFF;
import sugarcube.formats.pdf.reader.pdf.node.font.ttf.TTF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;
import sugarcube.formats.pdf.reader.pdf.object.Reference;
import sugarcube.formats.pdf.resources.fonts.FONTS;
import sugarcube.resources.pdf.core14.afm.AFM14;

public class FontDescriptor extends PDFNode<PDFNode>
{

    protected String basefont, fontname;
    protected FontMetrics metrics = new FontMetrics();
    protected PDFStream fontStream;
    protected PDFFont pdfFont;

    public FontDescriptor(PDFFont pdfFont, PDFDictionary map)
    {
        super("FontDescriptor", pdfFont);
        this.pdfFont = pdfFont;
        this.reference = map.reference();

        this.basefont = pdfFont.basefont;
        if (map.contains("FontName"))
            basefont = map.get("FontName").stringValue();
        fontname = PDFFont.TrimPrefix(basefont);

        if (Str.IsVoid(pdfFont.basefont) && fontname!=null)
            pdfFont.basefont = fontname;

        FontFormat format = FontFormat.TTF14;
        if (map.contains("FontFile") && (fontStream = map.get("FontFile").toPDFStream()).isValid())
            format = FontFormat.AFF;
        else if (map.contains("FontFile2") && (fontStream = map.get("FontFile2").toPDFStream()).isValid())
            format = FontFormat.TTF;
        else if (map.contains("FontFile3") && (fontStream = map.get("FontFile3").toPDFStream()).isValid())
            format = FontFormat.CFF;
        pdfFont.format = format;

        // Log.debug(this," - font format="+pdfFont.format+", name="+pdfFont.basefont);

        Reference fontRef = fontStream != null && fontStream.isValid() ? fontStream.reference() : null;
        if (fontRef != null)
        {
            if ((metrics = (FontMetrics) document.pool.get(reference)) == null)
                document.pool.add(reference, metrics = new FontMetrics().populateFromMap(map));

            if (metrics.missingWidth > 0)
            {
                pdfFont.widths.setDefault(metrics.missingWidth);
                pdfFont.defaultWidth = metrics.missingWidth;
            }
        }
        try
        {
            if (fontStream != null && fontStream.is("Subtype", "Type1C"))
            {
                addCFFReader(fontRef);
            } else
                switch (pdfFont.format)
                {
                    case TTF14:
                        addTTF14Reader();
                        break;
                    case AFF:
                        addAFFReader(fontRef);
                        break;
                    case TTF:
                        addTTFReader(fontRef);
                        break;
                    case CFF:
                        addCFFReader(fontRef);
                        break;
                    default:
                        Log.warn(this, " - font type not yet implemented " + type + " stream=" + fontRef);
                }
        } catch (Exception e)
        {
            Log.error(this, " - font reading error: name=" + fontname + " type=" + pdfFont.format + " ref=" + reference
                    + " exception=" + e);
            e.printStackTrace();
        }

    }

    private void addTTF14Reader()
    {
        fontname = FONTS.RenameTo14(fontname);
        addFontReader(new ReaderTTF14(this)); // font 14 or other file or OS font...
        if (FONTS.IsFont14(fontname))
        {
            AFM14 afm = AFM14.Get(fontname).populateMetrics(metrics);
            for (AFM14.AFMWidth width : afm.widths)
            {
                int c = pdfFont.encoding.codeFromName(width.name, -1);
                if (c > 0)
                    pdfFont.widths.put(c, (double) width.width);
            }
        }
    }

    private void addAFFReader(Reference fontRef)
    {
        AFF aff = (AFF) document.pool.get(fontRef);
        if (aff == null)
            document.pool.add(fontRef, aff = AFF.ParseFont(fontname, fontStream));
        addFontReader(new ReaderAFF(this, aff));
    }

    private void addTTFReader(Reference fontRef)
    {
        TTF ttf = (TTF) document.pool.get(fontRef);
        if (ttf == null)
            document.pool.add(fontRef, ttf = TTF.ParseFont(fontStream.byteValues()));
        if (ttf != null)
            addFontReader(new ReaderTTF(this, ttf));
        else
        {
            addFontReader(new ReaderTTF14(this, fontStream));
            Log.debug(this, ".addTTFReader - ReaderTTF unable to read " + fontname + ", using Reader14_OS");
        }
    }

    private void addCFFReader(Reference fontRef)
    {
        CFF cff = (CFF) document.pool.get(fontRef);
        if (cff == null)
            document.pool.add(fontRef, cff = CFF.ParseFont(fontname, pdfFont.encoding.isCID, fontStream.byteValues()));
        if (cff.isCID != pdfFont.encoding.isCID)
            cff = CFF.ParseFont(fontname, pdfFont.encoding.isCID, fontStream.byteValues());
        addFontReader(new ReaderCFF(this, cff));
    }

    private void addFontReader(FontReader reader)
    {
        add(reader);
        pdfFont.outlines = reader;
    }

    @Override
    public String sticker()
    {
        return type + "[" + reference + "]";
    }

    @Override
    public String toString()
    {
        return type + metrics.toString();
    }


    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        for (PDFNode node : this.children)
            node.paint(g, props);
    }


}
