package sugarcube.common.graphics.geom;

import java.awt.*;

public class Margins implements Cloneable, java.io.Serializable
{
  public float top = 0f;
  public float left = 0f;
  public float bottom = 0f;
  public float right = 0f;

  public Margins()
  {    
  }
  
  public Margins(double top, double left, double bottom, double right)
  {
    this.top = (float) top;
    this.left = (float) left;
    this.bottom = (float) bottom;
    this.right = (float) right;
  }  

  public Insets insets()
  {
    return new Insets((int)top,(int)left,(int)bottom,(int)right);
  }

  public void set(double top, double left, double bottom, double right)
  {
    this.top = (float) top;
    this.left = (float) left;
    this.bottom = (float) bottom;
    this.right = (float) right;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Margins)
    {
      Margins margins = (Margins) obj;
      return ((top == margins.top) && (left == margins.left) && (bottom == margins.bottom) && (right == margins.right));
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    float sum1 = left + bottom;
    float sum2 = right + top;
    float val1 = sum1 * (sum1 + 1) / 2 + left;
    float val2 = sum2 * (sum2 + 1) / 2 + top;
    float sum3 = val1 + val2;
    return (int)(sum3 * (sum3 + 1) / 2 + val2);
  }

  @Override
  public String toString()
  {
    return getClass().getName() + "[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
  }
  
  public Margins copy()
  {
    return new Margins(top,left,bottom,right);
  }

  @Override
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }
}
