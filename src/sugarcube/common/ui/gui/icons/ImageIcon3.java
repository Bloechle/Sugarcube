package sugarcube.common.ui.gui.icons;

import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.Image3;
import sugarcube.common.graphics.RenderingHints3;
import sugarcube.common.system.io.Class3;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageIcon3 extends ImageIcon
{
  public static final ImageIcon3 SUGARCUBE32 = new ImageIcon3("sugarcube32.png");
//  public static final ImageIcon3 SUGARCUBE24 = new ImageIcon3("sugarcube24.png");
//  public static final ImageIcon3 SUGARCUBE16 = new ImageIcon3("sugarcube16.png");
  public static final int DEFAULT_SIZE = 24;
  
  public ImageIcon3(Image image)
  {
    super(image);
  }
  
  public ImageIcon3(Paint color)
  {
    this(color, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE / 4);
  }
  
  public ImageIcon3(Paint color, int width, int height, int corner)
  {
    super(createPaintImage(color, width, height, corner));
  }
  
  public ImageIcon3(String filename)
  {
    super(loadResourceImage(filename), filename);
  }
  
  public ImageIcon3(String filename, String description)
  {
    super(loadResourceImage(filename), filename);
    this.setDescription(description);
  }  
  
  public ImageIcon3(Class path, String filename)
  {
    super(loadResourceImage(path, filename), filename);
  }
  
  public static BufferedImage loadResourceImage(String filename)
  {
    return loadResourceImage(Icons3.class, filename);
  }
  
  public static BufferedImage loadResourceImage(Class path, String filename)
  {
    try
    {
      InputStream stream = Class3.Stream(path, filename);
      BufferedImage bi = ImageIO.read(stream);
      stream.close();
      return bi;
    }
    catch (Exception ex)
    {
      Log.warn(ImageIcon3.class, ".loadResourceImage failed at "+path+"/"+filename+": " + ex);
    }
    return new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
  }
  
  private static BufferedImage createPaintImage(Paint paint, int width, int height, int corner)
  {
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bi.createGraphics();
    g.setRenderingHints(RenderingHints3.HQ_HINTS);
    g.setPaint(paint);
    g.fillRoundRect(0, 0, width, height, corner, corner);
    g.dispose();
    return bi;
  }
  
  public Image3 image()
  {
    return new Image3(this.getImage());
  }
}
