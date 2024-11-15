package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFContent;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFRectangle;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;

public class PDFTilingPattern extends PDFPattern
{
    private PDFContent content;
    private int patternType;
    private int paintType;
    private int tilingType;
    private PDFRectangle bbox;
    private float xStep;
    private float yStep;
    private Transform3 transform;
    private Transform3 inverseTM;
    private float tileScale = 1;//in order to have usable bitmap resolution
    private Image3 tile = null;

    public PDFTilingPattern(PDFNode parent, String ressourceID, PDFStream stream)
    {
        super(parent);
        this.reference = stream.reference();
        this.resourceID = ressourceID;
        this.patternType = stream.get("PatternType").intValue(1);
        this.paintType = stream.get("PaintType").intValue(1);
        //TilingType no more useful since the advent of high resolution screens
        this.tilingType = stream.get("TilingType").intValue(1);
        this.bbox = stream.get("BBox").toPDFRectangle();
        this.xStep = Math.abs(stream.get("XStep").floatValue((float) bbox.width()));
        this.yStep = Math.abs(stream.get("YStep").floatValue((float) bbox.height()));
        this.transform = new Transform3(stream.get("Matrix").toPDFArray().floatValues(1, 0, 0, 1, 0, 0));
        this.inverseTM = this.transform.inverse();
        this.content = new PDFContent(this, stream, ressourceID);
        this.tileScale = (int) Math.abs(0.5 + 2 * transform.scaleWidth());
        this.tileScale = tileScale < 1 ? 1 : tileScale > 5 ? 5 : tileScale;
//    this.tileScale = 1;
        this.add(content);

        //if (this.resourceID.equals("P0"))
        this.tile = this.tile();
    }

    @Override
    public boolean isTilingPattern()
    {
        return true;
    }

    @Override
    public boolean isUncoloredTilingPattern()
    {
        return paintType == 2;
    }

    public PDFRectangle bbox()
    {
        return this.bbox;
    }

    public int argb(double x, double y)
    {
//    System.out.println("x="+x+", y="+y);    
        x = x - bbox.x();
        y = y - bbox.y();
        if (x >= xStep || x < 0)
            x -= ((int) (x / xStep) + (x > 0 ? 0 : -1)) * xStep;
        if (y >= yStep || y < 0)
            y -= ((int) (y / yStep) + (y > 0 ? 0 : -1)) * yStep;
//    System.out.println("nx="+x+", ny="+y+", w="+cellImage.width()+", h="+cellImage.height());

        int ix = (int) (x * tileScale);
        int iy = (int) (y * tileScale);
        if (ix > 0 && iy > 0 && ix < tile.width() && iy < tile.height())
            return tile.getRGB(ix, iy);//interpolated getRGB
        else
            return 0;
    }

    @Override
    public Image3 image(Rectangle3 bounds, PDFDisplayProps props, Transform3 tm, boolean reverseY)
    {
        double pageHeight = props.pageBounds.height;
        float scale = props.displayScaling;
        float ox = bounds.x() + props.pageBounds.x * scale;
        float oy = reverseY ? bounds.y() - props.pageBounds.y * scale : bounds.y() - props.pageBounds.y * scale;
        int w = bounds.intWidth();
        int h = bounds.intHeight();
        Image3 image = PDF.ImageARGB(w, h);

        tm = tm == null ? inverseTM : inverseTM.concat(tm);

//    if (w < 1 || h < 1)
//      Log.debug(this, ".image - bounds: " + bounds + ", " + this.reference);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
            {
                Point3 p = tm.transform((x + ox) / scale, reverseY ? pageHeight - (y + oy) / scale : (y + oy) / scale);
                image.setPixel(x, y, argb(p.x, p.y));
            }
//    Log.debug(this, ".image - tm.x="+tm.x());
        return image;
    }

    public Image3 tile()
    {
        if (tile != null)
            return this.tile;
        int w = (int) Math.ceil(bbox.width() * tileScale);
        int h = (int) Math.ceil(bbox.height() * tileScale);

//    Log.debug(this, ".cellImage - w=" + w + ", h=" + h);
        if (w < 1)
            w = 1;
        if (h < 1)
            h = 1;

        Image3 image = PDF.ImageARGB(w, h);
        Graphics3 g = image.graphics();
//    g.clearWhite();
        PDFDisplayProps props = Dexter.displayProps.copy(bbox.rectangle());
        g.setTransform(tileScale, 0, 0, tileScale, 0, 0);//0,0 since bbox.rectangle applied in paint, hence not -tileScale * bbox.x(), -tileScale * bbox.y());
        if (this.defaultColor() != null)
            g.setColor(this.defaultColor());
        content.paint(g, props);
//    Log.debug(this, ".tile -"+ " content size=" + content.nbOfChildren()+ ", bbox=" + bbox+ ", tm=" + g.transform());
//    image.write(File3.userDesktop("tmp/" + w + "x" + h + "-tile.png"));
        return image;
    }

    @Override
    public void paint(Graphics3 g, PDFDisplayProps props)
    {
        g.setClip(null);
        g.setPaint(paint(props));
        g.fill(g.bounds());
        //g.draw(patternCellImage, null);
    }

    @Override
    public String toString()
    {
        return type + "[" + name + "]"
                + "\nPatternName[TilingPattern]"
                + "\nPatternType[" + patternType + "]"
                + "\nPaintType[" + paintType + "]"
                + "\nTilingType[" + tilingType + "]"
                + "\nBBox" + bbox
                + "\nXStep[" + xStep + "]"
                + "\nYStep[" + yStep + "]"
                + "\nTransform[" + transform + "]"
                + "\nInverseTM[" + inverseTM + "]"
                + "\nTileScale[" + tileScale + "]";
    }
}
