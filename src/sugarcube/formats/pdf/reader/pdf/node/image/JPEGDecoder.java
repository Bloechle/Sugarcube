package sugarcube.formats.pdf.reader.pdf.node.image;

import org.w3c.dom.NodeList;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.graphics.Image3;
import sugarcube.common.graphics.RenderingHints3;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;
import sugarcube.formats.pdf.reader.pdf.node.PDFColor;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.Profiles;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

public class JPEGDecoder
{
  public final static RenderingHints HINTS = RenderingHints3.hqHints();
  public final static ColorConvertOp CMYK2RGB = new ColorConvertOp(Profiles.ADOBE_CMYK_CS(), Profiles.RGB_CS(), HINTS);

  public static BufferedImage Decode(PDFImage image)
  {
    BufferedImage bi = null;

    if (image.cs.nbOfComponents() > 3)
      bi = decodeCMYK(image);
    if (bi == null)
      try
      {
        byte[] data = image.stream.byteValues();

//        System.out.println();
//        for (int i = data.length - 10; i < data.length; i++)
//        {
//          System.out.print(Nb.HexString(data[i]) + " ");
//        }

        // IO.WriteBytes(File3.Desk("out.jpg"), image.stream.byteValues());
        ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(data));
        Iterator<ImageReader> irs = ImageIO.getImageReaders(stream);
        while (irs.hasNext())
        {
          ImageReader ir = irs.next();
          try
          {
            if (ir.canReadRaster())
            {
              ir.setInput(stream, false);
              // Log.debug(JPEGDecoder.class, ".Decode - ir=" +
              // ir.getFormatName() + ", nb=" + ir.isImageTiled(0));
              boolean isRGB = HasRGBAdobeMarker(data);
              bi = CreateJPEG4(image, ir.readRaster(0, null), isRGB);
            }
          } catch (Exception e)
          {
            if ((bi = Image3.Read(data)) == null)
              Log.error(JPEGDecoder.class, ".Decode - image decompression error with " + ir.getFormatName() + ": " + e.getMessage());
          }
        }
        stream.close();
      } catch (Exception e)
      {
        Log.error(JPEGDecoder.class, ".Decode - image decompression fatal error: " + e.getMessage());
        e.printStackTrace();
        bi = PDFImage.errorImage(image.width, image.height);
      }
    return bi;
  }

  private static float interpolate(float x, int bpc, float min, float max)
  {
    return min + (x * ((max - min) / ((1 << bpc) - 1f)));
  }

  private static BufferedImage CreateJPEG4(PDFImage pdfImage, Raster raster, boolean isRGB)
  {
    // Zen.debug(JPEGDecoder.class, ".createJPEG4 - reference=" +
    // pdfImage.stream.reference() + ", isRGB=" + isRGB);
    int w = raster.getWidth(), h = raster.getHeight();
    Image3 image = new Image3(w, h);
    int nbOfComponents = pdfImage.cs.nbOfComponents();
    float[] decode = pdfImage.decode;
    int[][] XYZ = new int[nbOfComponents][];
    for (int i = 0; i < nbOfComponents; i++)
      XYZ[i] = raster.getSamples(0, 0, w, h, i, (int[]) null);
    // Log.debug(JPEGDecoder.class, ".decode - isRGB=" + isRGB);

    PDFFunction[] functions = pdfImage.imageState() == null ? null : pdfImage.imageState().TR();
    if (functions != null && functions.length != 3)
    {
      for (PDFFunction fct : functions)
        if (!fct.isIdentity())
        {
          Log.debug(JPEGDecoder.class, ".createJPEG4 - functions=" + functions.length + " while nbOfComponents=" + nbOfComponents);
          break;
        }
      functions = null;
    }

    for (int i = 0, x = 0, y = 0; i < XYZ[0].length; i++)
    {
      float[] xyz = new float[nbOfComponents];
      // System.out.println("["+XYZ[0][i]+","+XYZ[1][i]+","+XYZ[2][i]+"]");
      for (int j = 0; j < xyz.length; j++)
        xyz[j] = interpolate(XYZ[j][i], pdfImage.bpc, decode[2 * j], decode[2 * j + 1]);
      // if (Adobe_APP14 and transform==2) then YCCK else CMYK or other
      // colorspace

      float[] rgb = (nbOfComponents >= 3 && !isRGB) ? CCY2RGB(xyz) : pdfImage.cs.toRGB(xyz);
      // apply transform function if exists
      if (functions != null)
        rgb = PDFColor.function(rgb, functions);

      for (int j = 0; j < rgb.length; j++)
        if (rgb[j] < 0)
          rgb[j] = 0;
        else if (rgb[j] > 1)
          rgb[j] = 1;

      image.setPixel(x, y, rgb);
      if (++x == w && ++y < h)
        x = 0;
    }
    return image;
  }

  private static boolean HasRGBAdobeMarker(byte[] data)
  {
    // System.out.println();
    // for (int i = 0; i < 14; i++)
    // {
    // System.out.print(Nb.HexString(data[i]) + " ");
    // }

    for (int i = 0; i + 14 < data.length; i++)
      if ((data[i] & 0xff) == 0xee)
        if (data[i + 3] == 'A' && data[i + 4] == 'd' && data[i + 5] == 'o' && data[i + 6] == 'b' && data[i + 7] == 'e')
          return (data[i + 14] & 0xff) == 0;
    return false;
  }

  public static float[] CCY2CMYK(float... c)
  {
    // YCCK
    float k = 0;
    if (c.length == 4)
      k = c[3];
    float[] rgb = new float[3];
    rgb[0] = c[0] - k + 1.402f * (c[2] - 0.5f);
    rgb[1] = c[0] - k - 0.3441363f * (c[1] - 0.5f) - 0.71413636f * (c[2] - 0.5f);
    rgb[2] = c[0] - k + 1.7718f * (c[1] - 0.5f);

    for (int i = 0; i < rgb.length; i++)
      if (rgb[i] < 0)
        rgb[i] = 0;
      else if (rgb[i] > 1)
        rgb[i] = 1;

    float[] cmyk = new float[4];
    cmyk[0] = 1 - rgb[0];
    cmyk[1] = 1 - rgb[1];
    cmyk[2] = 1 - rgb[2];
    cmyk[3] = 1;

    if (cmyk[0] < cmyk[3])
      cmyk[3] = cmyk[0];
    if (cmyk[1] < cmyk[3])
      cmyk[3] = cmyk[1];
    if (cmyk[2] < cmyk[3])
      cmyk[3] = cmyk[2];
    if (cmyk[3] == 1)
    { // Black
      cmyk[0] = 0;
      cmyk[1] = 0;
      cmyk[2] = 0;
    } else
    {
      cmyk[0] = (cmyk[0] - cmyk[3]) / (1 - cmyk[3]);
      cmyk[1] = (cmyk[1] - cmyk[3]) / (1 - cmyk[3]);
      cmyk[2] = (cmyk[2] - cmyk[3]) / (1 - cmyk[3]);
    }
    return cmyk;
  }

  public static float[] CCY2RGB(float... c)
  {
    // YCCK
    float k = 0;
    if (c.length == 4)
      k = c[3];
    float[] rgb = new float[3];
    rgb[0] = c[0] - k + 1.402f * (c[2] - 0.5f);
    rgb[1] = c[0] - k - 0.3441363f * (c[1] - 0.5f) - 0.71413636f * (c[2] - 0.5f);
    rgb[2] = c[0] - k + 1.7718f * (c[1] - 0.5f);

    for (int i = 0; i < rgb.length; i++)
      if (rgb[i] < 0)
        rgb[i] = 0;
      else if (rgb[i] > 1)
        rgb[i] = 1;
    return rgb;
  }

  public static BufferedImage decodeCMYK(PDFImage pdfImage)
  {
    return nonRGBJPEGToRGBImage(pdfImage);
  }

  /**
   * <p>
   * Convert DCT encoded image bytestream to sRGB
   * </p>
   * <p>
   * It uses the internal Java classes and the Adobe icm to convert CMYK and
   * YCbCr-Alpha - the data is still DCT encoded.
   * </p>
   * <p>
   * The Sun class JPEGDecodeParam.java is worth examining because it contains
   * lots of interesting comments
   * </p>
   * <p>
   * I tried just using the new IOImage.read() but on type 3 images, all my
   * clipping code stopped working so I am still using 1.3
   * </p>
   */
  public static BufferedImage nonRGBJPEGToRGBImage(PDFImage pdfImage)
  {
    int width = pdfImage.width;
    int height = pdfImage.height;

    byte[] data = pdfImage.bytes();
    float[] decodeArray = pdfImage.decode;
    boolean isProcessed = false;

    BufferedImage image = null;
    ByteArrayInputStream in = null;

    ImageReader iir = null;
    ImageInputStream iin = null;

    try
    {
      in = new ByteArrayInputStream(data);

      int cmykType = getJPEGTransform(data);

      // Log.debug(JPEGDecoder.class,
      // ".noRGBJPEGToRGBImage - cmykType="+cmykType);

      // suggestion from Carol
      try
      {
        Iterator iterator = ImageIO.getImageReadersByFormatName("JPEG");
        while (iterator.hasNext())
        {
          Object o = iterator.next();
          iir = (ImageReader) o;
          if (iir.canReadRaster())
            break;
        }
      } catch (Exception e)
      {
        e.printStackTrace();

        return null;
      }

      ImageIO.setUseCache(false);
      iin = ImageIO.createImageInputStream((in));
      iir.setInput(iin, true);
      Raster ras = iir.readRaster(0, null);
      // invert
      if (decodeArray != null)
        // decodeArray=Strip.removeArrayDeleminators(decodeArray).trim();
        if ((decodeArray.length == 6 && decodeArray[0] == 1f && decodeArray[1] == 0f && decodeArray[2] == 1f && decodeArray[3] == 0f
            && decodeArray[4] == 1f && decodeArray[5] == 0f) || (decodeArray.length > 2 && decodeArray[0] == 1f && decodeArray[1] == 0))
        {
          DataBuffer buf = ras.getDataBuffer();
          int count = buf.getSize();
          for (int ii = 0; ii < count; ii++)
            buf.setElem(ii, 255 - buf.getElem(ii));
        } else if (decodeArray.length == 6 && decodeArray[0] == 0f && decodeArray[1] == 1f && decodeArray[2] == 0f && decodeArray[3] == 1f
            && decodeArray[4] == 0f && decodeArray[5] == 1f)
        {
          // }else if(decodeArray.indexOf("0 1 0 1 0 1 0 1")!=-1){//identity
          // }else
          // if(decodeArray.indexOf("0.0 1.0 0.0 1.0 0.0 1.0 0.0
          // 1.0")!=-1){//identity
        } else if (decodeArray != null && decodeArray.length > 0)
        {
        }

      if (pdfImage.cs.nbOfComponents() == 4)
      {
        isProcessed = true;
        try
        {
          if (cmykType == 2)
            image = iccConvertCMYKImageToRGB(((DataBufferByte) ras.getDataBuffer()).getData(), width, height);
          else
          {
            // ras = cleanupRaster(ras, pX, pY, 4);
            // Log.debug(JPEGDecoder.class,
            // ".nonRGBJPEGToRGBImage - cmykType==4");
            int w = pdfImage.width();
            int h = pdfImage.height();
            WritableRaster rgbRaster = Profiles.RGB_MODEL().createCompatibleWritableRaster(width, height);
            CMYK2RGB.filter(ras, rgbRaster);
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            image.setData(rgbRaster);

          }
        } catch (Exception e)
        {
          e.printStackTrace();
        }
      } else if (cmykType != 0)
      {
        image = iir.read(0);
        // image = cleanupImage(image, pX, pY, value);
        isProcessed = true;
      }
      // test
      if (!isProcessed)
      {
        WritableRaster rgbRaster;
        if (cmykType == 4)
        { // CMYK
          // ras = cleanupRaster(ras, pX, pY, 4);
          // Log.debug(JPEGDecoder.class, ".nonRGBJPEGToRGBImage -
          // cmykType==4");
          rgbRaster = Profiles.RGB_MODEL().createCompatibleWritableRaster(width, height);
          CMYK2RGB.filter(ras, rgbRaster);
          image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
          image.setData(rgbRaster);

        } else
        {
          boolean isYCC = false;
          try
          {
            IIOMetadata metadata = iir.getImageMetadata(0);
            String metadataFormat = metadata.getNativeMetadataFormatName();
            IIOMetadataNode iioNode = (IIOMetadataNode) metadata.getAsTree(metadataFormat);

            NodeList children = iioNode.getElementsByTagName("app14Adobe");
            if (children.getLength() > 0)
              isYCC = true;
          } catch (Exception e)
          {
            e.printStackTrace();
          }
          if (isYCC)
            image = ImageIO.read(new ByteArrayInputStream(data));
          else
            image = algorithmicConvertYCbCrToRGB(((DataBufferByte) ras.getDataBuffer()).getData(), width, height);

          // image = cleanupImage(image, pX, pY, value);
          image = convertToRGB(image);
        }
      }

    } catch (Exception e)
    {
      image = null;
      e.printStackTrace();
    } catch (Error err)
    {
      if (iir != null)
        iir.dispose();
      if (iin != null)
        try
        {
          iin.flush();
        } catch (IOException e)
        {
          e.printStackTrace();
        }
    }

    try
    {
      in.close();
      iir.dispose();
      iin.close();
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    return image;

  }

  public static BufferedImage convertToRGB(BufferedImage image)
  {
    if ((image.getType() != BufferedImage.TYPE_INT_RGB))
      try
      {
        BufferedImage raw_image = image;
        image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        new ColorConvertOp(HINTS).filter(raw_image, image);
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    return image;
  }

  /**
   * or int colorType = decoder.getJPEGDecodeParam().getEncodedColorID();
   */
  private static int getJPEGTransform(byte[] data)
  {
    int xform = 0;

    for (int i = 0, imax = data.length - 2; i < imax;)
    {

      int type = data[i + 1] & 0xff;
      i += 2; // 0xff and type

      if (type == 0x01 || (0xd0 <= type && type <= 0xda))
      {
      } else if (type == 0xda)
      {
        i = i + ((data[i] & 0xff) << 8) + (data[i + 1] & 0xff);
        while (true)
        {
          for (; i < imax; i++)
            if ((data[i] & 0xff) == 0xff && data[i + 1] != 0)
              break;
          int rst = data[i + 1] & 0xff;
          if (0xd0 <= rst && rst <= 0xd7)
            i += 2;
          else
            break;
        }
      } else
      {
        if (type == 0xee) // Adobe
          if (data[i + 2] == 'A' && data[i + 3] == 'd' && data[i + 4] == 'o' && data[i + 5] == 'b' && data[i + 6] == 'e')
          {
            xform = data[i + 13] & 0xff;
            break;
          }
        i = i + ((data[i] & 0xff) << 8) + (data[i + 1] & 0xff);
      }
    }

    return xform;
  }

  public static BufferedImage iccConvertCMYKImageToRGB(byte[] buffer, int w, int h) throws IOException
  {
    ColorModel rgbModel = Profiles.RGB_MODEL();
    int pixelCount = w * h * 4;
    int Y, Cb, Cr, k, lastY = -1, lastCb = -1, lastCr = -1, lastK = -1;
    int c = 0, m = 0, y = 0;
    double r, g, b;
    for (int i = 0; i < pixelCount; i = i + 4)
    {
      Y = (buffer[i] & 255);
      Cb = (buffer[i + 1] & 255);
      Cr = (buffer[i + 2] & 255);
      k = (buffer[i + 3] & 255);
      if (Y != lastY || Cb != lastCb || Cr != lastCr || k != lastK)
      {
        // System.out.print(" "+k+" ");
        r = Y + 1.402 * Cr - 179.456;
        g = Y - 0.34414 * Cb - 0.71414 * Cr + 135.45984;
        b = Y + 1.772 * Cb - 226.816;

        c = 255 - (int) (r < 0 ? 0 : r > 255 ? 255 : r);
        m = 255 - (int) (g < 0 ? 0 : g > 255 ? 255 : g);
        y = 255 - (int) (b < 0 ? 0 : b > 255 ? 255 : b);
        // k = (int) (k < 0 ? 0 : k > 255 ? 255 : k);

        // trying to reuse values if identical
        lastY = Y;
        lastCb = Cb;
        lastCr = Cr;
        lastK = k;
      }

      buffer[i] = (byte) (c);
      buffer[i + 1] = (byte) (m);
      buffer[i + 2] = (byte) (y);
      // buffer[i + 3] = (byte) (k);
    }

    // Log.debug(JPEGDecoder.class, ".iccConvertCMYKImageToRGB");
    Raster raster = Raster.createInterleavedRaster(new DataBufferByte(buffer, buffer.length), w, h, w * 4, 4, Zen.Array.Ints(0, 1, 2, 3), null);
    WritableRaster rgbRaster = rgbModel.createCompatibleWritableRaster(w, h);
    CMYK2RGB.filter(raster, rgbRaster);
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    image.setData(rgbRaster);
    return image;
  }

  public static BufferedImage algorithmicConvertYCbCrToRGB(byte[] buffer, int w, int h)
  {
    BufferedImage image = null;
    byte[] data = new byte[w * h * 3];
    int pixelCount = data.length > buffer.length ? buffer.length : data.length;

    int r = 0, g = 0, b = 0;
    int lastY = -1, lastCb = -1, lastCr = -1;
    int pixelReached = 0;
    float val1;

    for (int i = 0; i < pixelCount; i = i + 3)
    {
      int Y = ((buffer[i] & 255));
      int Cb = ((buffer[1 + i] & 255));
      int Cr = ((buffer[2 + i] & 255));

      if (lastY != Y || lastCb != Cb || lastCr != Cr)
      {
        val1 = 298.082f * Y;

        r = (int) (((val1 + (408.583f * Cr)) / 256f) - 222.921);
        if (r < 0)
          r = 0;
        if (r > 255)
          r = 255;

        g = (int) (((val1 - (100.291f * Cb) - (208.120f * Cr)) / 256f) + 135.576f);
        if (g < 0)
          g = 0;
        if (g > 255)
          g = 255;

        b = (int) (((val1 + (516.412f * Cb)) / 256f) - 276.836f);
        if (b < 0)
          b = 0;
        if (b > 255)
          b = 255;

        lastY = Y;
        lastCb = Cb;
        lastCr = Cr;
      }

      data[pixelReached++] = (byte) (r);
      data[pixelReached++] = (byte) (g);
      data[pixelReached++] = (byte) (b);
    }

    try
    {
      image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      Raster raster = createInterleavedRaster(data, w, h);
      image.setData(raster);
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    return image;
  }

  public static Raster createInterleavedRaster(byte[] data, int w, int h)
  {
    return Raster.createInterleavedRaster(new DataBufferByte(data, data.length), w, h, w * 3, 3, Zen.Array.Ints(0, 1, 2), null);
  }

  public static void main(String[] args)
  {
    try
    {
      byte[] data = IO.ReadBytes(File3.Desk("magicnumber.jpg"));

      ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(data));
      Iterator<ImageReader> irs = ImageIO.getImageReaders(stream);
      while (irs.hasNext())
      {
        ImageReader ir = irs.next();
        if (ir.canReadRaster())
        {
          ir.setInput(stream, true);
          boolean isRGB = HasRGBAdobeMarker(data);
          Log.debug(JPEGDecoder.class, " - isRGB=" + isRGB);
          // createJPEG4(image, ir.readRaster(0, null), isRGB);
        }
      }
      stream.close();
    } catch (Exception e)
    {
      Log.error(JPEGDecoder.class, ".Decode - image decompression error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
