package sugarcube.formats.pdf;

import sugarcube.common.graphics.Image3;
import sugarcube.common.system.io.File3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFDocument;
import sugarcube.formats.pdf.reader.pdf.node.PDFPage;
import sugarcube.formats.pdf.reader.pdf.object.PDFEnvironment;

public class PDFCapture
{
    private PDFDisplayProps props = new PDFDisplayProps();
    private PDFEnvironment env;
    private PDFDocument doc;
    private File3 pdf;

    public PDFCapture(File3 pdf)
    {
        this.pdf = pdf;
        this.env = new PDFEnvironment();
        this.doc = new PDFDocument();
        env.parse(pdf);
        try {
			doc.parse(env, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public Image3 capture(int pageIndex)
    {
        PDFPage page = doc.pages().getPages().get(pageIndex);
        page.ensureInMemory();
        return page.createImage(props.copy(page.mediaBox()));
    }

    public Image3 save(int pageIndex)
    {
        return capture(pageIndex).toRGB().write(pdf.extense(".jpg"), 0.95);
    }

    public static Image3 Save(File3 file, int pageIndex)
    {
        PDFCapture capture = new PDFCapture(file);
        return capture.save(pageIndex);
    }

    public static void main(String... args)
    {
        for (File3 file : File3.userDesktop().listFiles(".pdf"))
        {
            new PDFCapture(file).save(0);
        }
    }
}
