package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;

import java.awt.*;

public class PDFPaintable extends PDFNode
{
  protected PDFMatrix tm; // transform matrix (device space)
  protected List3<PDFMark> marks = new List3<PDFMark>();
  
  protected PDFPaintable(String type, PDFNode parent)
  {
    super(type, parent);
  }  

  public PDFMatrix tm()
  {
    return tm;
  }
  
  public Transform3 transform()
  {
    return tm.transform();
  }
  
  public Transform3 transform(double minX, double maxY)
  {
    return maxY <= 0 ? tm.transform() : tm.concat(1, 0, 0, -1, (float) -minX, (float) maxY).transform();
  }

  public Point3 toOrigin()
  {
    return tm.toOrigin();
  }
  
  public int mcid()
  {
    PDFMark mark = mark();
    return mark == null ? -1 : mark.mcid;
  }

  public PDFMark mark()
  {
    for (PDFMark mark : this.marks)
      if (mark.mcid > -1)
        return mark;
    return null;
  }

  public Shape shape(double minX, double maxY)
  {
    return null;
  }

  public Color3 fillColor()
  {
    return getFillColor().color();
  }

  public PDFColor getFillColor()
  {
    return null;
  }

  public String blendMode()
  {
    return null;
  }

  @Override
  public String sticker()
  {
    return "";
  }
}
