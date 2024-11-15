package sugarcube.formats.pdf.reader.pdf.node.shade;

import sugarcube.common.graphics.geom.Circle3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.function.PDFFunction;
import sugarcube.formats.pdf.reader.pdf.object.PDF;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public final class RadialShading extends PDFShading
{
  protected float[] coords;// x0,y0,r0,x1,y1,r1 of starting and ending circles
  protected float[] domain;// t0,t1
  protected boolean[] extend;// extend beyond starting and ending points
  // protected boolean[] extend;from my point of you we should always use [true,
  // true]
  protected PDFFunction function;
  protected float x1_x0;
  protected float y1_y0;
  protected float r1_r0;
  protected float t1_t0;

  public RadialShading(PDFNode parent, PDFDictionary dico, String resourceID)
  {
    super(parent, dico, resourceID);
    this.coords = dico.get("Coords").floatValues();
    this.domain = dico.get("Domain").floatValues(0, 1);
    this.extend = dico.get("Extend").booleanValues(false, false);
    this.function = PDFFunction.instance(this, dico.get("Function"));
    // this.extend = map.get("Extend")
    this.add(this.function);
    this.x1_x0 = x1() - x0();
    this.y1_y0 = y1() - y0();
    this.r1_r0 = r1() - r0();
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

  public float r0()
  {
    return coords[2];
  }

  public float x1()
  {
    return coords[3];
  }

  public float y1()
  {
    return coords[4];
  }

  public float r1()
  {
    return coords[5];
  }

  public float s(float t)
  {
    return (t - t0()) / t1_t0;
  }

  public Transform3 transform(double scale, double ox, double oy, double pageHeight)
  {
    return new Transform3(scale, 0, 0, scale, -ox, -oy).concat(new Transform3(1, 0, 0, -1, 0, (float) pageHeight)).concat(inverseTM.inverse());
  }

  @Override
  public Image3 image(Rectangle3 bounds, PDFDisplayProps props, Transform3 transform, boolean reverseY)
  {
    double pageHeight = props.pageBounds.height;
    float scale = props.displayScaling;
    float ox = bounds.x() + props.pageBounds.x * scale;// bounds used for tiling
                                                       // the shading into
                                                       // sub-images
    float oy = bounds.y() - props.pageBounds.y * scale;
    Image3 image = PDF.ImageARGB(bounds);
    Graphics3 g = image.graphics();

    // TODO extend is used to extend the shading, not the bg which is the
    // default color !
    // if (this.extend[1])
    
    float t= t1();
    float s = s(t1());
    float[] xyz = null;
    float x = x0() + s * x1_x0;
    float y = y0() + s * y1_y0;
    float r = s * r1_r0;
    if (r < 0)// done by experience... not sure it's ok in any case
    {
      r = -r;
      xyz = this.function.transform(1 - t);// transforms 0..1 into colorant
                                           // shades
    } else
      xyz = this.function.transform(t);// transforms 0..1 into colorant shades    
        
    g.clear(this.colorSpace.toColor(xyz));

    Transform3 tm = this.transform(scale, ox, oy, pageHeight);

    int steps = (int) (t1_t0 * (tm.scaleX() > tm.scaleY() ? tm.scaleX() : tm.scaleY()));
    if (steps < 20)
      steps = 50;
    // Log.debug(this, ".image - t1()=" + t1() + ", t0()=" + t0() + ", steps=" +
    // steps);
    for (t = t1(); t >= t0(); t -= t1_t0 / steps)
    {
      s = s(t);
      xyz = null;
      x = x0() + s * x1_x0;
      y = y0() + s * y1_y0;
      r = s * r1_r0;
      if (r < 0)// done by experience... not sure it's ok in any case
      {
        r = -r;
        xyz = this.function.transform(1 - t);// transforms 0..1 into colorant
                                             // shades
      } else
        xyz = this.function.transform(t);// transforms 0..1 into colorant shades

      Color3 rgb = this.colorSpace.toColor(xyz);
      Circle3 circle = new Circle3(x, y, Math.abs(r));
      // Log.debug(this, ".image - ts=" + Zen.Array.toString(xyz) + ", color=" +
      // rgb.cssHexValue() + ", s=" + s + ", x=" + x + ", y=" + y + ", r=" + r);
      g.fill(tm.transform(circle), rgb);
    }

    return alpha < 1.0 ? new Image3(image, alpha) : image;
  }

  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "[" + this.shadingType + "]" + this.reference() + "\nBBox"
        + (this.bbox == null ? "none" : bbox.toString()) + "\nColorSpace[" + this.colorSpace.name() + "]" + "\nCoords" + PDF.toString(coords)
        + "\nDomain" + PDF.toString(domain) + "\nTransform[" + this.inverseTM + "]";
  }
}
