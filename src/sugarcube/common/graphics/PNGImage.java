package sugarcube.common.graphics;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PNGImage
{
  public static final String PNG = "png";
  private int width = 0;
  private int height = 0;
  private String type = "rgb";
  private byte[] data = null;

  public PNGImage()
  {
  }

  public PNGImage(BufferedImage image)
  {
    this.read(image);
  }    

  private void read(BufferedImage image)
  {
    this.width = image.getWidth();
    this.height = image.getHeight();
    switch (image.getType())
    {
      case Image3.TYPE_BYTE_BINARY:
        this.type = "binary";
        break;
      case Image3.TYPE_BYTE_GRAY:
        this.type = "gray";
        break;
      case Image3.TYPE_BYTE_INDEXED:
        this.type = "indexed";
        break;
      case Image3.TYPE_INT_RGB:
        this.type = "rgb";
        break;
      case Image3.TYPE_INT_ARGB:
        this.type = "argb";
        break;
      default:
        this.type = "" + image.getType();
    }
    ByteArrayOutputStream stream = new ByteArrayOutputStream(width * height / 10);
    try
    {
      ImageIO.write(image, PNG, stream);
      this.data = stream.toByteArray();
    }
    catch (Exception ex)
    {
      Log.warn(this, ".read - image encoding error: " + ex.getMessage());
    }
  }
  
  public int size()
  {
    return data.length;
  }
  
  public static PNGImage create(BufferedImage image)
  {
    return new PNGImage(image);
  }

  public String signature()
  {
    return type + "-" + width + "-" + height + "-" + hashCode() + ".png";
  }

  public byte[] bytes()
  {
    return data;
  }

  public int width()
  {
    return width;
  }

  public void setWidth(int width)
  {
    this.width = width;
  }

  public int height()
  {
    return height;
  }

  public void setHeight(int height)
  {
    this.height = height;
  }

  public String type()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public void write(OutputStream stream)
  {
    BufferedImage image = this.image();
    try
    {
      ImageIO.write(image, PNG, stream);
    }
    catch (IOException ex)
    {
      Log.warn(this, ".write - stream writing exception: " + ex);
    }
  }
  
  public BufferedImage image()
  {
    ByteArrayInputStream stream = new ByteArrayInputStream(data);
    try
    {
      BufferedImage image = ImageIO.read(stream);
      if (image == null)
        Log.warn(this, ".image - stream unknown: length=" + data.length);
      else
        return image;
    }
    catch (Exception e)
    {
      Log.warn(this, ".image - stream reading exception: " + e);
    }
    return new Image3(width, height);
  }  

  public Image3 image3()
  {
    ByteArrayInputStream stream = new ByteArrayInputStream(data);
    try
    {
      BufferedImage image = ImageIO.read(stream);
      if (image == null)
        Log.warn(this, ".image - stream unknown: length=" + data.length);
      else
        return new Image3(image);
    }
    catch (Exception e)
    {
      Log.warn(this, ".image - stream reading exception: " + e);
    }
    return new Image3(width, height);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    PNGImage raster = (PNGImage) o;
    if (this.data.length != raster.data.length)
      return false;
    for (int i = 0; i < this.data.length; i++)
      if (this.data[i] != raster.data[i])
        return false;
    return true;
  }

  @Override
  public int hashCode()
  {
    int size = data.length;
    int result = 1;
    result = 31 * result + size;
    int ds = size / 31 + 1;
    for (int i = 0; i < size; i += ds)
      result = 31 * result + this.data[i];
    return result;
  }

  public PNGImage copy()
  {
    PNGImage copy = new PNGImage();
    copy.width = this.width;
    copy.height = this.height;
    copy.type = this.type;
    copy.data = Zen.Array.copy(data);
    return copy;
  }

  @Override
  public String toString()
  {
    return PNGImage.class.getName()
      + "\nType[" + type + "]"
      + "\nSize[" + width + "," + height + "]"
      + "\nData[" + data.length + "]";
  }
}
