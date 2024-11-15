package sugarcube.common.graphics.geom;

import sugarcube.common.data.xml.Nb;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

public class Dimension3 extends Dimension2D
{
  private double width;
  private double height;    

  public Dimension3(Point2D p)
  {
    this(p.getX(), p.getY());
  }  
  
  public Dimension3(double width, double height)
  {
    this.width = width;
    this.height = height;
  }
  
  public Dimension3 round(int decimals)
  {        
    return new Dimension3(Nb.round(width, decimals), Nb.round(height, decimals));
  }
  
  public Dimension dimension()
  {
    return new Dimension(intWidth(), intHeight());
  }

  public int intWidth()
  {
    return (int) (0.5 + width);
  }

  public int intHeight()
  {
    return (int) (0.5 + height);
  }

  public double width()
  {
    return width;
  }

  public double height()
  {
    return height;
  }

  @Override
  public double getWidth()
  {
    return width;
  }

  @Override
  public double getHeight()
  {
    return height;
  }

  @Override
  public void setSize(double width, double height)
  {
    this.width = width;
    this.height = height;
  }
  
  public Dimension3 half()
  {
    return new Dimension3(width/2, height/2);
  }
  
  public Dimension3 scale(double ratio)  
  {
    return new Dimension3(width*ratio, height*ratio);
  }
  
  public Rectangle3 asRectangle()
  {
    return new Rectangle3(0,0,width, height);
  }
  
  public Rectangle3 asRectangle(double x, double y)
  {
    return new Rectangle3(x, y, width, height);
  }
  
  public Dimension3 swap()
  {
    return new Dimension3(height, width);
  }
  
  public double area()
  {
    return height*width;
  }
}
