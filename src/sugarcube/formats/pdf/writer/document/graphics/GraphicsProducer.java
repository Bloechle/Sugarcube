package sugarcube.formats.pdf.writer.document.graphics;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.formats.ocd.objects.OCDPath;
import sugarcube.formats.pdf.writer.PDFParams;
import sugarcube.formats.pdf.writer.PDFWriter;
import sugarcube.formats.pdf.writer.core.Lexic;
import sugarcube.formats.pdf.writer.core.Util;
import sugarcube.formats.pdf.writer.core.object.Stream;
import sugarcube.formats.pdf.writer.document.GraphicState;
import sugarcube.formats.pdf.writer.document.Page;
import sugarcube.formats.pdf.writer.exception.PDFException;

import java.awt.geom.PathIterator;

public class GraphicsProducer
{
    // path
    private static String PDF_MOVE = "m";
    private static String PDF_LINE = "l";
    private static String PDF_CUBIC = "c";
    private static String PDF_CLOSE = "h";
    // paint
    private static String PDF_STROKE = "S";
    private static String PDF_FILL_NON_ZERO = "f";
    private static String PDF_FILL_EVEN_ODD = "f*";
    private static String PDF_FILL_AND_STROKE_NON_ZERO = "B";
    private static String PDF_FILL_AND_STROKE_EVEN_ODD = "B*";
    private static String PDF_NO_FILL_AND_STROKE = "n";

    private PDFWriter writer;
    private Boolean enabled = null;

    private StringBuilder content = new StringBuilder(1000);

    public GraphicsProducer(PDFWriter writer)
    {
        this.writer = writer;
    }

    public boolean isEnabled()
    {
        return enabled == null ? enabled = writer.params().bool(PDFParams.GRAPHICS, true) : enabled;
    }

    public void write(Page page, OCDPath path) throws PDFException
    {
        if (!isEnabled())
            return;

        GraphicState gs = page.getGraphicState();
        Stream stream = new Stream(page.pdfWriter());
        page.linkContent(stream.getID());
        content.setLength(0);
        gs.saveState(content);
        gs.beginGraphics();
        gs.setGraphicsParameters(page, path, content);
        createPath(content, path.path3(), path.transform3());
        paintPath(content, path);
        gs.restoreState(content);
        stream.write(content);
    }

    public void writeFont(Path3 path, float width, StringBuilder sb) throws PDFException
    {
        if (writer.params().isTransparent())
            path = new Path3(path.bounds().p1());

        createPath(sb, path, null);
        // if(path.isEmpty())
        // System.out.println(this + " not yet defined path");
        sb.append(PDF_FILL_EVEN_ODD + Lexic.LINE_FEED);
    }

    protected final void paintPath(StringBuilder sb, OCDPath path) throws PDFException
    {
        boolean isStroked = !path.strokeColor().isTransparent();
        boolean isFilled = !path.fillColor().isTransparent();
        if (isStroked && !isFilled)
            sb.append(PDF_STROKE);
        else if (isFilled)
        {
            switch (path.path3().getWindingRule())
            {
                case Path3.WIND_EVEN_ODD:
                    sb.append(isStroked ? PDF_FILL_AND_STROKE_EVEN_ODD : PDF_FILL_EVEN_ODD);
                    break;
                case Path3.WIND_NON_ZERO:
                    sb.append(isStroked ? PDF_FILL_AND_STROKE_NON_ZERO : PDF_FILL_NON_ZERO);
                    break;
                default:
                    throw new PDFException("Invalid painting operation");
            }
        } else
            sb.append(PDF_NO_FILL_AND_STROKE);
        sb.append(Lexic.LINE_FEED);
    }

    public final void createPath(StringBuilder sb, Path3 path3, Transform3 tm)
    {
        PathIterator iterator = path3.getPathIterator(tm);
        double[] coords = new double[6];
        double lastX = 0;
        double lastY = 0;
        int operator;
        while (!iterator.isDone())
        {
            operator = iterator.currentSegment(coords);
            switch (operator)
            {
                case PathIterator.SEG_MOVETO:
                    writeCoordinates(sb, PDF_MOVE, coords, 2);
                    lastX = coords[0];
                    lastY = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    writeCoordinates(sb, PDF_LINE, coords, 2);
                    lastX = coords[0];
                    lastY = coords[1];
                    break;
                case PathIterator.SEG_CUBICTO:
                    writeCoordinates(sb, PDF_CUBIC, coords, 6);
                    lastX = coords[4];
                    lastY = coords[5];
                    break;
                case PathIterator.SEG_QUADTO:
                    coords = quadraticToCubic(lastX, lastY, coords);
                    writeCoordinates(sb, PDF_CUBIC, coords, 6);
                    lastX = coords[4];
                    lastY = coords[5];
                    break;
                case PathIterator.SEG_CLOSE:
                    writeCoordinates(sb, PDF_CLOSE, coords, 0);
                    break;
            }
            iterator.next();
        }
    }

    private void writeCoordinates(StringBuilder sb, String op, double[] coordinates, int nbOfValues)
    {
        for (int c = 0; c < nbOfValues; c++)
        {
            coordinates[c] = Util.format(coordinates[c]);
            sb.append(coordinates[c] + Lexic.SPACE);
        }
        sb.append(op + Lexic.LINE_FEED);
    }

    private double[] quadraticToCubic(double startX, double startY, double[] coord)
    {
        double[] result = new double[6];
        double tcx = coord[0] * 2. / 3.;
        double tcy = coord[1] * 2. / 3.;
        result[0] = startX / 3. + tcx;
        result[1] = startY / 3. + tcy;
        result[2] = coord[2] / 3. + tcx;
        result[3] = coord[3] / 3. + tcy;
        result[4] = coord[2];
        result[5] = coord[3];
        return result;
    }

    public void dispose()
    {
    }
}
