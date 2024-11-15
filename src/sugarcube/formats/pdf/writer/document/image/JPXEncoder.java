package sugarcube.formats.pdf.writer.document.image;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;
import sugarcube.common.system.Prefs;
import sugarcube.common.system.log.Log;
import sugarcube.common.system.io.File3;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class JPXEncoder
{  
  static
  {
    Prefs.Need();
  }
  
  public boolean lossless = false;
  
  public JPXEncoder lossless(boolean lossless)
  {
    this.lossless = lossless;
    return this;
  }
  
  public byte[] compress(BufferedImage image)
  {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    MemoryCacheImageOutputStream mos = new MemoryCacheImageOutputStream(os);    
    compress(image, mos);
    return os.toByteArray();
  }

  public void compress(BufferedImage image, File3 file)
  {
    try
    {
      compress(image, ImageIO.createImageOutputStream(file));
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void compress(BufferedImage image, ImageOutputStream ios)
  {
    try
    {
      Iterator writers = ImageIO.getImageWritersByFormatName("JPEG2000");
      String name = null;
      ImageWriter writer = null;
      while (writers.hasNext())
      {
        writer = (ImageWriter) writers.next();
        name = writer.getClass().getName();
        if (name.contains("J2KImageWriter"))
          break;        
      }
      if(!(name.contains("J2KImageWriter")))      
        Log.warn(JPXEncoder.class,  ".Compress - writer not found");

      writer.setOutput(ios);
      J2KImageWriteParam param = (J2KImageWriteParam) writer.getDefaultWriteParam();
      IIOImage ioimage = new IIOImage(image, null, null);
      param.setSOP(true);
      param.setWriteCodeStreamOnly(false);      
      param.setProgressionType("layer");
      param.setLossless(lossless);
      param.setCompressionMode(J2KImageWriteParam.MODE_EXPLICIT);
      param.setCompressionType("JPEG2000");
      param.setCompressionQuality(0.1f);
      param.setEncodingRate(1.01);
      param.setFilter(lossless ? J2KImageWriteParam.FILTER_53 : J2KImageWriteParam.FILTER_97);
      writer.write(null, ioimage, param);
      writer.dispose();
      ios.flush();
      ios.close();
      image.flush();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static JPXEncoder New()
  {
    return new JPXEncoder();
  }


}
