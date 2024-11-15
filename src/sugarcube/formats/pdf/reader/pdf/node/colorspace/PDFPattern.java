package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class PDFPattern extends PDFColorSpace
{
  protected static final Image3 MODEL_IMAGE = PDF.ImageARGB(10, 10);

  public PDFPattern(PDFNode parent)
  {
    super(parent, "Pattern", 1);
  }

  @Override
  public boolean isPattern()
  {
    return true;
  }

  public boolean isTilingPattern()
  {
    return false;
  }

  public boolean isUncoloredTilingPattern()
  {
    return false;
  }

  public Image3 image(Rectangle3 bounds, PDFDisplayProps props, Transform3 tm, boolean reverseY)
  {
    return null;
  }

  public Paint paint(final PDFDisplayProps props)
  {
    return new Paint()
    {
      @Override
      public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, final AffineTransform at, RenderingHints rh)
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
            return image(new Rectangle3(ox, oy, w, h), props, at == null ? null : new Transform3(at), true).getRaster();
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
    g.draw(image(g.bounds(), props, null, true), null);
  }

  public static PDFPattern instance(PDFNode parent, String patternID, PDFObject po)
  {
    po = po.unreference();
    if (po.isPDFDictionary())
    {
//      Log.debug(PDFPattern.class, ".instance - "+po.reference());
      PDFDictionary dico = po.toPDFDictionary();
      int type = dico.get("PatternType").intValue();
      if (type == 1)
        return new PDFTilingPattern(parent, patternID, po.toPDFStream());
      else if (type == 2)
        return new PDFShadingPattern(parent, patternID, po.toPDFDictionary());
    }
    Log.warn(PDFPattern.class, ".instancePattern - unknown pattern: " + po);
    return null;
  }
}