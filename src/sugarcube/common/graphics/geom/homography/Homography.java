package sugarcube.common.graphics.geom.homography;

import javafx.geometry.Point3D;
import sugarcube.common.system.log.Log;
import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.numerics.jama.EigenvalueDecomposition;
import sugarcube.common.numerics.jama.Matrix;

public class Homography
{
  private Matrix H = Matrix.identity(3, 3);
  public double camZ = 1;
  public boolean debug = false;

  public Homography()
  {

  }

  public void calibrate(ProjCross from, ProjCross to)
  {
    this.calibrate(from.points(), to.points());
  }

  public void calibrate(Point3[] from, Point3[] to)
  {
    // Creates an array of two times the size of the cam[] array
    double[][] a = new double[2 * from.length][];

    // Creates the estimation matrix
    for (int i = 0; i < from.length; i++)
    {
      double l1[] =
      { from[i].x, from[i].y, camZ, 0, 0, 0, -from[i].x * to[i].x, -from[i].y * to[i].x, -to[i].x };
      double l2[] =
      { 0, 0, 0, from[i].x, from[i].y, camZ, -from[i].x * to[i].y, -from[i].y * to[i].y, -to[i].y };
      a[2 * i] = l1;
      a[2 * i + 1] = l2;
    }
    Matrix A = new Matrix(a);
    Matrix T = A.transpose();
    Matrix X = T.times(A);

    EigenvalueDecomposition E = X.eig();
    // Find the eigenvalues and put that in an array
    double[] eigenvalues = E.getRealEigenvalues();
    // grab the first eigenvalue from the eigenvalues []
    double w = eigenvalues[0];
    int r = 0;
    // Find the minimun eigenvalue
    for (int i = 0; i < eigenvalues.length; i++)
    {
      // if (debug) parent.println(eigenvalues[i]);
      if (eigenvalues[i] <= w)
      {
        w = eigenvalues[i];
        r = i;
      }
    }
    // find the corresponding eigenvector
    Matrix v = E.getV();

    if (debug)
      v.print(9, 9);

    // create the homography matrix from the eigenvector v
    for (int i = 0; i < 3; i++)
    {
      for (int j = 0; j < 3; j++)
      {
        H.set(i, j, v.get(i * 3 + j, r));
      }
    }
  }

  public Point3[] applyAll(Point3... p)
  {
    Point3[] points = new Point3[p.length];
    for (int i = 0; i < points.length; i++)
      points[i] = apply(p[i]);
    return points;
  }

  public Point3 apply(Point3 p)
  {
    return apply(p.x, p.y);
  }

  public Point3 apply(double x, double y)
  {
    double[][] a = new double[3][1];
    a[0][0] = x;
    a[1][0] = y;
    a[2][0] = 1;
    Matrix D = new Matrix(a);
    Matrix U = H.times(D);
    Matrix L = U.times(1 / U.get(2, 0));
    Point3 p = new Point3();
    p.x = (float) L.get(0, 0);
    p.y = (float) L.get(1, 0);
    return p;
  }

  public Point3 apply(Point3D p)
  {
    double[][] a = new double[3][1];
    a[0][0] = p.getX();
    a[1][0] = p.getY();
    a[2][0] = p.getZ();
    Matrix D = new Matrix(a);
    Matrix U = H.times(D);
    Matrix L = U.times(1 / U.get(2, 0));
    Point3 p2 = new Point3();
    p2.x = (float) L.get(0, 0);
    p2.y = (float) L.get(1, 0);
    return p2;
  }

  public Line3 apply(Line3 line)
  {
    return new Line3(apply(line.p1()), apply(line.p2()));
  }

  public ProjCross apply(ProjCross cross)
  {
    return new ProjCross(apply(cross.nsLine), apply(cross.weLine));
  }

  public ProjCross apply(ProjCross cross, ProjCross result)
  {
    if (result == null)
      result = new ProjCross();
    result.set(apply(cross.nsLine), apply(cross.weLine));
    return result;
  }

  public ProjCross unitCross(Point3 center)
  {
    return apply(ProjCross.Unit(center));
  }

  // public double weight(Point3 p)
  // {
  // return weight(p, 100);
  // }
  //
  // public double weight(Point3 p, int length)
  // {
  // return apply(new Line3(p.x, p.y - length / 2, p.x, p.y + length /
  // 2)).length() / (double) length;
  // }

  public void testIceRink()
  {
    Point3[] cam = new Point3[4];
    Point3[] proj = new Point3[4];

    cam[0] = new Point3(1037, 150);
    cam[1] = new Point3(550, 807);
    cam[2] = new Point3(123, 172);
    cam[3] = new Point3(1815, 353);

    proj[0] = new Point3(1000, 200);
    proj[1] = new Point3(1000, 800);
    proj[2] = new Point3(200, 500);
    proj[3] = new Point3(1800, 500);

    this.calibrate(cam, proj);
  }

  @Override
  public String toString()
  {
    return H.toString();
  }

  public static void main(String... args)
  {
    Homography homo = new Homography();

    homo.testIceRink();

    Log.info(Homography.class, ".main - ");
  }

}