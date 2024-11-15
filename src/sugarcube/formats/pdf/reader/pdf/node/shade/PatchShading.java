package sugarcube.formats.pdf.reader.pdf.node.shade;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

public class PatchShading extends PDFShading
{
  protected PDFFunction function;
  private final PDFStream dico;
  private Tee[] tees;

  public PatchShading(PDFNode parent, PDFDictionary dico, String resourceID)
  {
    super(parent, dico, resourceID);
    this.dico = dico.toPDFStream();
    int bitsPerCoord = dico.get("BitsPerCoordinate").intValue(1);
    int bitsPerComp = dico.get("BitsPerComponent").intValue(1);
    int bitsPerFlag = dico.get("BitsPerFlag").intValue(1);

    if (dico.contains("Function"))
      this.function = PDFFunction.instance(this, dico.get("Function"));

    float[] decode = dico.get("Decode").floatValues(0, 1, 0, 1);
    float xMin = decode[0];
    float xMax = decode[1];
    float yMin = decode[2];
    float yMax = decode[3];
    int nbOfComponents = this.colorSpace.nbOfComponents();
    if (this.function != null)
      nbOfComponents = 1;
    float[] cMin = new float[nbOfComponents];
    float[] cMax = new float[nbOfComponents];
    for (int k = 0; k < nbOfComponents; k++)
    {
      cMin[k] = decode[4 + k * 2];
      cMax[k] = decode[5 + k * 2];
    }
    try
    {
      BitInputStream stream = new BitInputStream(this.dico.inputStream());
      Patch patch = null;
      ArrayList<Tee> teeList = new ArrayList<Tee>();
      int value;
      while ((value = stream.readbits(bitsPerFlag)) >= 0)
      {
        float[] coords = new float[32];
        float[][] colors = new float[4][nbOfComponents];
        for (int i = value == 0 ? 0 : 4; i < (this.shadingType == 6 ? 12 : 16); i++)
        {
          coords[(i * 2)] = getValue(stream.readLong(bitsPerCoord), xMin, xMax, bitsPerCoord);
          coords[(i * 2 + 1)] = getValue(stream.readLong(bitsPerCoord), yMin, yMax, bitsPerCoord);
        }
        for (int i = value == 0 ? 0 : 2; i < 4; i++)
          for (int c = 0; c < nbOfComponents; c++)
            colors[i][c] = Math.max(0.0F, Math.min(1.0F, getValue(stream.readbits(bitsPerComp), cMin[c], cMax[c], bitsPerComp)));
        patch = new Patch(this.shadingType, value, patch, coords, colors);
        patch.updateList(teeList);
        while (stream.tell() % 8 != 0)
          stream.readbits(1);
      }
      this.tees = teeList.toArray(new Tee[0]);
      stream.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  protected float[] getColorValue(float x, float y, float[] c)
  {
    for (int i = 0; i < this.tees.length; i++)
    {
      if (!this.tees[i].hasColorAt(x, y))
        continue;
      this.tees[i].colorAt(x, y, c);
      return c;
    }
    return null;
  }

  private float getValue(long value, double min, double max, int bpc) throws EOFException
  {
    long delta = (1L << bpc) - 1L;
    if ((value < 0L) || (value > delta))
      Log.debug(this, ".getValue - Invalid value: " + value + "<" + delta);
    return (float) (min + value * (max - min) / delta);
  }

  private static class Tee
  {
    private final float x0;
    private final float y0;
    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;
    private final float y1_y0;
    private final float y2_y0;
    private final float y2_y1;
    private final float x1_x0;
    private final float x2_x0;
    private final float x2_x1;
    private final float[][] colors;
    private final int size;
    private final float[] c1;
    private final float[] c2;
    private float xt1;
    private float xt2;
    private float lastY = (0.0F / 0.0F);

    Tee(float x2, float y2, float x1, float y1, float x0, float y0, float[] colorA, float[] colorB, float[] colorC)
    {
      if (y2 == y1)
      {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.colors = new float[][]
        {
          colorC, colorB, colorA
        };
      }
      else if (y2 == y0)
      {
        this.x0 = x1;
        this.y0 = y1;
        this.x1 = x2;
        this.y1 = y2;
        this.x2 = x0;
        this.y2 = y0;
        this.colors = new float[][]
        {
          colorB, colorA, colorC
        };
      }
      else
      {
        this.x0 = x2;
        this.y0 = y2;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x0;
        this.y2 = y0;
        this.colors = new float[][]
        {
          colorA, colorB, colorC
        };
      }
      this.size = this.colors[0].length;
      this.c1 = new float[this.size];
      this.c2 = new float[this.size];
      this.y1_y0 = (this.y1 - this.y0);
      this.y2_y0 = (this.y2 - this.y0);
      this.y2_y1 = (this.y2 - this.y1);
      this.x1_x0 = (this.x1 - this.x0);
      this.x2_x0 = (this.x2 - this.x0);
      this.x2_x1 = (this.x2 - this.x1);
    }

    boolean hasColorAt(float x, float y)
    {
      double d1 = (y - this.y0) * this.x1_x0 - (x - this.x0) * this.y1_y0;
      double d2 = (y - this.y1) * this.x2_x1 - (x - this.x1) * this.y2_y1;
      double d3 = (this.y2 - y) * this.x2_x0 - (this.x2 - x) * this.y2_y0;
      return (d1 * d2 > 0.0D) && (d2 * d3 > 0.0D);
    }

    void colorAt(float x, float y, float[] colors)
    {
      if (y != this.lastY)
      {
        float t1 = (y - this.y0) / this.y1_y0;
        float t2 = (y - this.y0) / this.y2_y0;
        this.xt1 = (this.x0 + t1 * this.x1_x0);
        this.xt2 = (this.x0 + t2 * this.x2_x0 - this.xt1);
        for (int c = 0; c < this.size; c++)
        {
          this.c1[c] = (this.colors[0][c] + t1 * (this.colors[1][c] - this.colors[0][c]));
          this.c2[c] = (this.colors[0][c] + t2 * (this.colors[2][c] - this.colors[0][c]));
        }
        this.lastY = y;
      }
      float t = this.xt2 == 0.0F ? 0.0F : (x - this.xt1) / this.xt2;
      for (int c = 0; c < this.size; c++)
        colors[c] = (this.c1[c] + t * (this.c2[c] - this.c1[c]));
    }

    @Override
    public String toString()
    {
      return "[T: p0=[" + this.x0 + " " + this.y0 + "] p1=[" + this.x1 + " " + this.y1 + "] p2=[" + this.x2 + " " + this.y2 + "] c0=[" + this.colors[0][0] + " " + this.colors[0][1] + " " + this.colors[0][2] + "]  c1=[" + this.colors[1][0] + " " + this.colors[1][1] + " " + this.colors[1][2] + "]  c0=[" + this.colors[2][0] + " " + this.colors[2][1] + " " + this.colors[2][2] + "]";
    }
  }

  private static final class Patch
  {
    private final float[] coords;
    private final float[][] color;
    private final int[] sides =
    {
      0, 11, 10, 9, 3, 4, 5, 6, 0, 1, 2, 3, 9, 8, 7, 6
    };

    Patch(int shadingType, int value, Patch patch, float[] coords, float[][] color)
    {
      if (value == 1)
      {
        System.arraycopy(patch.coords, 6, coords, 0, 8);
        color[0] = patch.color[1];
        color[1] = patch.color[2];
      }
      else if (value == 2)
      {
        System.arraycopy(patch.coords, 12, coords, 0, 8);
        color[0] = patch.color[2];
        color[1] = patch.color[3];
      }
      else if (value == 3)
      {
        System.arraycopy(patch.coords, 18, coords, 0, 8);
        color[0] = patch.color[3];
        color[1] = patch.color[0];
      }
      else if (value != 0)
        throw new IllegalArgumentException("Invalid edge flag " + value);
      this.coords = coords;
      this.color = color;
      if (shadingType == 6)
      {
        internalPoints(0.3333333F, 0.3333333F, coords, 24);
        internalPoints(0.3333333F, 0.6666667F, coords, 26);
        internalPoints(0.6666667F, 0.6666667F, coords, 28);
        internalPoints(0.6666667F, 0.3333333F, coords, 30);
      }
    }

    private float t(int side, int xOrY, float t)
    {
      float f1 = this.coords[this.sides[side * 4 + 0] + xOrY];
      float f2 = this.coords[this.sides[side * 4 + 1] + xOrY];
      float f3 = this.coords[this.sides[side * 4 + 2] + xOrY];
      float f4 = this.coords[this.sides[side * 4 + 3] + xOrY];
      float f5 = 1.0F - t;
      return f5 * f5 * f5 * f1 + 3.0F * f5 * f5 * t * f2 + 3.0F * f5 * t * t * f3 + t * t * t * f4;
    }

    private void internalPoints(float tx, float ty, float[] coords, int index)
    {
      float tx_ = 1.0F - tx;
      float ty_ = 1.0F - ty;
      float f3 = ty_ * t(0, 0, tx) + ty * t(1, 0, tx);
      float f4 = ty_ * t(0, 1, tx) + ty * t(1, 1, tx);
      float f5 = tx_ * t(2, 0, ty) + tx * t(3, 0, ty);
      float f6 = tx_ * t(2, 1, ty) + tx * t(3, 1, ty);
      float f7 = ty_ * (tx_ * this.coords[0] + tx * this.coords[18]) + ty * (tx_ * this.coords[6] + tx * this.coords[12]);
      float f8 = ty_ * (tx_ * this.coords[1] + tx * this.coords[19]) + ty * (tx_ * this.coords[7] + tx * this.coords[13]);
      coords[index] = f3 + f5 - f7;
      coords[index + 1] = f4 + f6 - f8;
    }

    void updateList(List<Tee> list)
    {
      list.add(new Tee(this.coords[0], this.coords[1], this.coords[6], this.coords[7], this.coords[12], this.coords[13], this.color[0], this.color[1], this.color[2]));
      list.add(new Tee(this.coords[12], this.coords[13], this.coords[18], this.coords[19], this.coords[0], this.coords[1], this.color[2], this.color[3], this.color[0]));
    }
  }
//    public Raster getRaster(int paramInt1, int paramInt2, int w, int h)
//    {
//      ColorSpace localColorSpace = this.a.getColorSpace();
//      int i = this.a.getNumColorComponents();
//      for (int y = 0; y < h; y++)
//        for (int x = 0; x < w; x++)
//        {
//          Point2D.Float localFloat = new Point2D.Float(paramInt1 + x, paramInt2 + y);
//          this.c.transform(localFloat, localFloat);
//          if (dd.this.transform != null)
//            dd.this.transform.transform(localFloat, localFloat);
//          float[] arrayOfFloat = getColorValue(localFloat.x, localFloat.y, this.e);
//          if (arrayOfFloat != null)
//          {
//            int m;
//            int n;
//            if (this.b != null)
//            {
//              m = Math.round(Math.max(0.0F, Math.min(1.0F, arrayOfFloat[0])) * 255.0F);
//              for (n = 0; n < i; n++)
//                this.d.setSample(x, y, n, this.b[m][n]);
//            }
//            else
//            {
//              arrayOfFloat = dd.access$400(arrayOfFloat, dd.this.e, localColorSpace);
//              for (m = 0; m < i; m++)
//              {
//                n = Math.round(Math.max(0.0F, Math.min(1.0F, arrayOfFloat[m])) * 255.0F);
//                this.d.setSample(x, y, m, n);
//              }
//            }
//            this.d.setSample(x, y, i, 255);
//          }
//          else
//            this.d.setSample(x, y, i, 0);
//        }
//      return this.d;
//    }
}
