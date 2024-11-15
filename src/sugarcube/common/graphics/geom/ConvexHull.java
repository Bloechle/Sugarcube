package sugarcube.common.graphics.geom;

import sugarcube.common.data.collections.List3;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class ConvexHull
{
  //Returns the determinant of the point matrix
  //This determinant tells how far p3 is from vector p1p2 and on which side it is
  public static int distance(Point p1, Point p2, Point p3)
  {
    int x1 = p1.x;
    int x2 = p2.x;
    int x3 = p3.x;
    int y1 = p1.y;
    int y2 = p2.y;
    int y3 = p3.y;
    return x1 * y2 + x3 * y1 + x2 * y3 - x3 * y2 - x2 * y1 - x1 * y3;
  }

  //Returns the points of convex hull in the correct order
  public static List3<Point> compute(List3<Point> array)
  {
    int size = array.size();
    if (size < 2)
      return null;
    Point l = array.get(0);
    Point r = array.get(size - 1);
    List3<Point> path = new List3<Point>();
    path.add(l);
    compute(array, l, r, path);
    path.add(r);
    compute(array, r, l, path);
    return path;
  }

  public static void compute(List3<Point> points, Point l, Point r, List3<Point> path)
  {
    if (points.size() < 3)
      return;
    int maxDist = 0;
    int tmp;
    Point p = null;
    for (Point pt : points)
      if (pt != l && pt != r)
      {
        tmp = distance(l, r, pt);
        if (tmp > maxDist)
        {
          maxDist = tmp;
          p = pt;
        }
      }
    List3<Point> left = new List3<Point>();
    List3<Point> right = new List3<Point>();
    left.add(l);
    right.add(p);
    for (Point pt : points)
      if (distance(l, p, pt) > 0)
        left.add(pt);
      else if (distance(p, r, pt) > 0)
        right.add(pt);
    left.add(p);
    right.add(r);
    compute(left, l, p, path);
    path.add(p);
    compute(right, p, r, path);
  }
}

//The panel that will show the CHull class in action
class DrawPanel extends JPanel
{
  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    int size = 80;
    int rad = 4;
    Random r = new Random();
    List3<Point> array = new List3<Point>();
    for (int i = 0; i < size; i++)
    {
      int x = r.nextInt(350) + 15;
      int y = r.nextInt(350) + 15;
      array.add(new Point(x, y));
      g2.draw(new Ellipse2D.Double(x - 2, y - 2, rad, rad));
    }
    Collections.sort(array, new Comparator<Point>()
    {
      @Override
      public int compare(Point pt1, Point pt2)
      {
        int r = pt1.x - pt2.x;
        if (r != 0)
          return r;
        else
          return pt1.y - pt2.y;
      }
    });
    List3<Point> hull = ConvexHull.compute(array);
    Iterator<Point> itr = hull.iterator();
    Point prev = itr.next();
    Point curr = null;
    while (itr.hasNext())
    {
      curr = itr.next();
      g2.drawLine(prev.x, prev.y, curr.x, curr.y);
      prev = curr;
    }
    curr = hull.get(0);
    g2.drawLine(prev.x, prev.y, curr.x, curr.y);
  }
}

class CHullExample extends JFrame
{
  public CHullExample()
  {
    setSize(400, 400);
    setLocation(100, 100);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    DrawPanel dp = new DrawPanel();
    Container cp = this.getContentPane();
    cp.add(dp);
  }

  public static void main(String[] args)
  {
    new CHullExample().setVisible(true);
  }
}