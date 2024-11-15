package sugarcube.common.graphics.geom;

import sugarcube.common.data.collections.List3;

public class Rects extends List3<Rectangle3>
{
  public Rects()
  {

  }

  public Rects(Rectangle3[] boxes)
  {
    this.addAll3(boxes);
  }
  
  public Rects add(double x, double y, double w, double h)
  {
    this.add(new Rectangle3(x,y,w,h));
    return this;
  }

  public double overlaps(Rectangle3 box)
  {
    double max = 0;
    for (Rectangle3 r : this)
    {
      double overlap = box.overlapThis(r);
      if (overlap > max)
        max = overlap;
    }
    return max;
  }
  
  public Rectangle3[] array()
  {
    return this.toArray(new Rectangle3[0]);
  }

  public static Rects New(Rectangle3[] boxes)
  {
    return new Rects(boxes);
  }
}
