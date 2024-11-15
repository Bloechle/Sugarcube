package sugarcube.formats.pdf.reader.pdf.node.shade;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFMatrix;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFColorSpace;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFRectangle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class PDFShading extends PDFNode
{
  protected static int COUNTER = 0;
  protected static final Image3 MODEL_IMAGE = PDF.ImageARGB(10, 10);
  protected String resourceID;
  protected int shadingType = -1;
  protected PDFColorSpace colorSpace = null;
  protected PDFRectangle bbox;
//  protected Transform3 tm;
  protected Transform3 inverseTM;
  protected double alpha = 1.0;

  public PDFShading(PDFNode parent, PDFDictionary map, String resourceID)
  {
    super("Shading", parent);
    this.reference = map.reference();
    this.shadingType = map.get("ShadingType").intValue(-1);
    this.colorSpace = PDFColorSpace.instance(this, resourceID, map.get("ColorSpace"));
    this.bbox = map.get("BBox").toPDFRectangle();
    this.add(colorSpace);
    this.resourceID = resourceID;

  }

  public PDFColorSpace colorSpace()
  {
    return this.colorSpace;
  }

  protected Image3 context(int w, int h)
  {
    return PDF.Image(w, h, MODEL_IMAGE.getType());
  }

  public void setAlpha(double alpha)
  {
    this.alpha = alpha;
  }

  public double alpha()
  {
    return alpha;
  }

  public void setTransform(Transform3 tm)
  {
//    this.tm = tm;
    this.inverseTM = tm.inverse();
  }

  public void setTransform(PDFMatrix matrix)
  {
    this.setTransform(matrix.transform());
  }

  public boolean exists()
  {
    return shadingType > 0;
  }

  public static PDFShading instance(PDFNode parent, String resourceID, PDFDictionary map)
  {
    int type = map.get("ShadingType").intValue();
    PDFShading shading = null;
    switch (type)
    {
      case 1:
        break;
      case 2:
        shading = new AxialShading(parent, map, resourceID);
        break;
      case 3:
        shading = new RadialShading(parent, map, resourceID);
        break;
      case 4:
        break;
      case 5:
        break;
      case 6:
        shading = new PatchShading(parent, map, resourceID);
        break;
      case 7:
        shading = new PatchShading(parent, map, resourceID);
        break;
    }

    if (shading == null)
      Log.warn(PDFShading.class, ".instance - shading not yet implemented: type=" + type);
    return shading;
  }

  @Override
  public String sticker()
  {
    return (resourceID == null ? "" : resourceID + " » ") + this.getClass().getSimpleName() + "[" + colorSpace.name() + "] " + this.reference;
  }

  @Override
  public String toString()
  {
    return type + "[" + (colorSpace == null ? "none" : colorSpace.name()) + "] " + this.reference;
  }

  public PDFShading copy()
  {
    return this;
  }

  public Image3 image(Rectangle3 bounds, PDFDisplayProps props, Transform3 tm, boolean reverseY)
  {
    return PDF.ImageARGB(bounds);
  }

  public Paint paint(final PDFDisplayProps props)
  {
//    Log.debug(this, ".paint - props: " + props.pageBounds);    
    return new Paint()
    {
      @Override
      public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform at, RenderingHints rh)
      {
//        System.out.println("DeviceBounds="+deviceBounds);
//        System.out.println("UserBounds=" + userBounds);
//        final float ux = (float) userBounds.getX();
//        final float uy = (float) userBounds.getY();
//        final float uw = (float) userBounds.getWidth();
//        final float uh = (float) userBounds.getHeight();

        return new PaintContext()
        {
          @Override
          public void dispose()
          {
          }

          @Override
          public ColorModel getColorModel()
          {
            return MODEL_IMAGE.getColorModel();
          }

          @Override
          public Raster getRaster(int ox, int oy, int w, int h)
          {
            //return image(new Rectangle3(ox + props.pageBounds.x, oy - props.pageBounds.y, w, h), props).getRaster();
            Image3 image= image(new Rectangle3(ox, oy, w, h), props, null, true);            
//            image.writePng(File3.userDesktop("tmp/shading_"+(COUNTER++)+".png"));
            return image.getRaster();
          }
        };
      }

      @Override
      public int getTransparency()
      {
        return MODEL_IMAGE.type().equals(Image3.Type.ARGB) ? Paint.TRANSLUCENT : Paint.OPAQUE;
      }
    };
  }

  @Override
  public void paint(Graphics3 g, PDFDisplayProps props)
  {
    Image3 image = image(g.bounds(), props, null, true);
    g.draw(image, null);
  }
}
