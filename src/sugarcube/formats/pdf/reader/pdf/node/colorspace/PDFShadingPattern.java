package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.graphics.Image3;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.PDFExtGState;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.shade.PDFShading;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class PDFShadingPattern extends PDFPattern
{
  private int patternType;
  private PDFShading shading;
  private Transform3 transform;
  private PDFExtGState extGState;

  public PDFShadingPattern(PDFNode parent, String resourceID, PDFDictionary dico)
  {
    super(parent);
    this.reference = dico.reference();
    this.resourceID = resourceID;
    this.patternType = dico.get("PatternType").intValue(2);
    this.shading = PDFShading.instance(this, resourceID, dico.get("Shading").toPDFDictionary());
    this.transform = new Transform3(dico.get("Matrix").toPDFArray().floatValues(1, 0, 0, 1, 0, 0));
    if (dico.contains("ExtGState"))
      this.extGState = dico.contains("ExtGState") ? new PDFExtGState(this, resourceID, dico.get("ExtGState").toPDFDictionary()) : null;

    if (this.shading != null)
      this.shading.setTransform(transform);
    // if (this.resourceID.equals("P0"))
    this.add(shading);
  }

  @Override
  public Color3 defaultColor()
  {
    return shading==null ? Color3.WHITE : shading.colorSpace().defaultColor();
  }

  @Override
  public Image3 image(Rectangle3 bounds, PDFDisplayProps props, Transform3 tm, boolean reverseY)
  {
    shading.setTransform(transform);
    Image3 image = shading.image(bounds, props, tm, reverseY);
    // image.write(File3.userDesktop(""+bounds+".png"));
    return image;
  }

  @Override
  public void paint(Graphics3 g, PDFDisplayProps props)
  {
    g.setClip(null);
    g.setPaint(paint(props));
    g.fill(g.bounds());
    // g.draw(patternCellImage, null);
  }

  @Override
  public String toString()
  {
    return type + "[" + name + "]" + "\nPatternType[" + patternType + "]" + "\nTransform[" + transform + "]";
  }
}
