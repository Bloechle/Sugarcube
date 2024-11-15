package sugarcube.formats.pdf.reader.pdf.node.shade;

import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public final class AxialShading extends PDFShading
{
  protected float[] coords;// x0,y0,x1,y1
  protected float[] domain;// t0,t1
  // protected boolean[] extend;
  protected PDFFunction function;
  protected double x1_x0;
  protected double y1_y0;
  protected double norm;
  protected double t1_t0;

  public AxialShading(PDFNode parent, PDFDictionary map, String resourceID)
  {
    super(parent, map, resourceID);
    this.coords = map.get("Coords").floatValues();
    this.domain = map.get("Domain").floatValues(0, 1);
    // Log.debug(this, " - ref: "+this.reference());
    this.function = PDFFunction.instance(this, map.get("Function"));
    // this.extend = map.get("Extend")
    this.add(this.function);
    this.x1_x0 = x1() - x0();
    this.y1_y0 = y1() - y0();
    this.norm = this.x1_x0 * this.x1_x0 + this.y1_y0 * this.y1_y0;
    this.t1_t0 = t1() - t0();
  }

  public float t0()
  {
    return domain[0];
  }

  public float t1()
  {
    return domain[1];
  }

  public float x0()
  {
    return coords[0];
  }

  public float y0()
  {
    return coords[1];
  }

  public float x1()
  {
    return coords[2];
  }

  public float y1()
  {
    return coords[3];
  }

  public double nx(float x, float y)
  {
    return (x1_x0 * (x - x0()) + y1_y0 * (y - y0())) / norm;
  }

  public double t(float x, float y)
  {
    double nx = nx(x, y);
    // because we use[true true] for extends
    // System.out.print((int)Math.round(1000 * nx)+" ");
    if (nx < 0)
      return domain[0];
    else if (nx > 1)
      return domain[1];
    return domain[0] + (t1_t0) * nx(x, y);
  }

  public float[] rgb(float x, float y)
  {
    return this.colorSpace.toRGB(this.function.transform((float) t(x, y)));
  }

  @Override
  public Image3 image(Rectangle3 bounds, PDFDisplayProps props, Transform3 transform, boolean reverseY)
  {
    double pageHeight = props.pageBounds.height;
    float scale = props.displayScaling;
    float ox = bounds.x() + props.pageBounds.x * scale;
    float oy = bounds.y() - props.pageBounds.y * scale;
    int w = bounds.intWidth();
    int h = bounds.intHeight();
    Image3 image = PDF.ImageARGB(w,h);
    Graphics3 g = image.graphics();

    g.clear(this.colorSpace.toColor(this.function.transform(t1())));

    try
    {
      for (int y = 0; y < h; y++)
        for (int x = 0; x < w; x++)
        {
          Point3 p = this.inverseTM.transform((x + ox) / scale, pageHeight - (y + oy) / scale);
          image.setPixel(x, y, rgb(p.x, p.y));
        }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return this.alpha < 1.0 ? new Image3(image, alpha) : image;
  }

  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "[" + this.shadingType + "]" + "\nBBox" + (this.bbox == null ? "none" : bbox.toString())
        + "\nColorSpace[" + this.colorSpace.name() + "]" + "\nCoords" + PDF.toString(coords) + "\nDomain" + PDF.toString(domain)
        // + "\nTM["+this.tm+"]"
        + "\nITM[" + this.inverseTM + "]";
  }
}
