package sugarcube.formats.pdf.reader.pdf.node.image;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.pdf.object.PDF;

import java.awt.image.*;

public class RawDecoder
{
  public static BufferedImage decode(PDFImage pdfImage)
  {
    int width = pdfImage.width;
    int height = pdfImage.height;
    int bpc = pdfImage.bpc;

    byte[] stream = null;
    try
    {
      
//      Log.debug(RawDecoder.class, ".decode - width=" + width + ", height=" + height + ", bpc=" + bpc + ", ref=" + pdfImage.stream.reference()
//          + ", cs=" + pdfImage.cs.getClass().getSimpleName());
          
      BufferedImage rgbImage = pdfImage.cs.decodeImage(pdfImage);
      if (rgbImage != null)
      {         
        return rgbImage;
      }
      
      stream = pdfImage.bytes();


      // pick a color model, based on the number of components and bits per
      // component
      ColorModel model = null;
      WritableRaster raster = null;
      if (width * height / 8 == stream.length)
        model = new IndexColorModel(1, 2, Zen.Array.bytes((byte) 0, (byte) 0xff), Zen.Array.bytes((byte) 0, (byte) 0xff), Zen.Array.bytes((byte) 0,
            (byte) 0xff));
      else
        model = pdfImage.cs.createColorModel(pdfImage.bpc);

      raster = Raster.createWritableRaster(model.createCompatibleSampleModel(width, height), new DataBufferByte(stream, stream.length), null);

      BufferedImage xyzImage = new BufferedImage(model, raster, true, null);
      rgbImage = xyzImage;
      if (!pdfImage.cs.isStandardCS())
      {
        rgbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++)
          for (int x = 0; x < width; x++)
            rgbImage.setRGB(x, y, xyzImage.getRGB(x, y));
      }

      if (pdfImage.hasMaskRange())
      {
        int[] p = new int[xyzImage.getRaster().getNumBands()];

        Image3 rgbaImage = PDF.ImageARGB(width, height);
        for (int y = 0; y < height; y++)
          for (int x = 0; x < width; x++)
          {
            for (int i = 0; i < p.length; i++)
              p[i] = xyzImage.getRaster().getSample(x, y, i);

            if (!pdfImage.isInMaskRange(p))
              rgbaImage.setRGB(x, y, rgbImage.getRGB(x, y) | 0xff000000);
          }
        rgbImage = rgbaImage;
      }

      return rgbImage;
    } catch (Exception e)
    {
      Log.warn(RawDecoder.class, ".decode - raw image exception: bpc=" + bpc + " cs=" + pdfImage.cs.name() + " ref=" + pdfImage.stream.reference()
          + " length=" + (stream == null ? "null" : stream.length));
      e.printStackTrace();
    }
    return PDFImage.errorImage(width, height);
  }
}
