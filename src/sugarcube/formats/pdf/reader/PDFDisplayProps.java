package sugarcube.formats.pdf.reader;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.system.reflection.Bean;

import java.awt.*;

public class PDFDisplayProps extends Bean
{
  public boolean displayText = true;
  public boolean displayImages = true;
  public boolean displayGraphics = true;
  public boolean enableColors = true;
  public boolean enableClipping = true;
  public boolean highlightClips = false;
  public boolean highlightTexts = false;
  public boolean highlightImages = false;
  public boolean highlightPaths = false;
  public boolean highlightCropbox = false;
  public int nbOfPrimitives = -1;
  public float displayScaling = 1f;    
  public float contextScaling = 2f;
  public float patternScaling = 3f;
  public float shadingScaling = 3f;
  public Color3 selectionColor = Color3.CERULEAN_BLUE.alpha(0.4);
  public Color3 backgroundColor = new Color3(100, 100, 100);
  public Rectangle3 pageBounds = A4bounds72dpi();
  public int id = -1;   
  
  public PDFDisplayProps()
  {
    
  }
  
  public PDFDisplayProps(double scale)
  {
    this.displayScaling = (float)scale;
  }
  
  public float minX()
  {
    return pageBounds.minX();
  }

  public float maxY()
  {
    return pageBounds.maxY();
  }

  public static Rectangle3 A4bounds72dpi()
  {
    return new Rectangle3(0, 0, 595, 842);
  }

  public Transform3 displayTransform()
  {
    return Transform3.scaleInstance(displayScaling < 0.1 ? 0.1 : displayScaling);
  }

  public Shape shapeTransform(Shape path)
  {
    return new Transform3(1, 0, 0, -1, -pageBounds.minX(), pageBounds.maxY()).transform(path);
  }
  
  public PDFDisplayProps copy(Rectangle3 pageBounds)
  {
    PDFDisplayProps props = this.copy();      
    props.pageBounds = pageBounds;
    return props;
  }

  public PDFDisplayProps copy()
  {
    try
    {
      return (PDFDisplayProps) PDFDisplayProps.this.clone();                
    }
    catch (CloneNotSupportedException ex)
    {
      return null;
    }
  }
}