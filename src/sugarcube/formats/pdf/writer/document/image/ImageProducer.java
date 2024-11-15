package sugarcube.formats.pdf.writer.document.image;

import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.object.Stream;
import sugarcube.formats.pdf.writer.document.GraphicState;
import sugarcube.formats.pdf.writer.document.Page;
import sugarcube.formats.pdf.writer.exception.PDFException;

public class ImageProducer
{
    private static String PDF_PAINT_IMAGE = "Do";

    private PDFWriter writer;
    private StringBuilder content = new StringBuilder(1000);

    public ImageProducer(PDFWriter writer)
    {
        this.writer = writer;
    }

    public void write(Page page, OCDImage image) throws PDFException
    {
//        Log.debug(this, ".write - " + image.write(File3.desktop(image.filename())));
        GraphicState gs = page.getGraphicState();
        Stream stream = new Stream(page.pdfWriter());
        page.linkContent(stream.getID());
        content.setLength(0);
        gs.saveState(content);
        int id = gs.setImageParameters(page, image, content);
        if (id > -1)
            content.append(Lexic.SOLIDUS + ImageManager.IMAGE_SUFFIX + id + Lexic.SPACE + PDF_PAINT_IMAGE + Lexic.LINE_FEED);
        gs.restoreState(content);
        if (id > -1)
            stream.write(content);
    }

    public void dispose()
    {
    }
}
