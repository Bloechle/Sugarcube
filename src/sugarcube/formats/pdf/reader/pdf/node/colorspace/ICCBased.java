package sugarcube.formats.pdf.reader.pdf.node.colorspace;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.graphics.Color3;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFStream;
import sugarcube.formats.pdf.reader.pdf.object.Reference;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;

public class ICCBased extends PDFColorSpace
{
  private static Map3<Reference, ColorSpace> map = new Map3<>();
  private PDFColorSpace alternateCS;

  public ICCBased(PDFNode parent, PDFStream stream)
  {
    super(parent, "ICCBased", stream.get("N").intValue(3));

    if (stream.contains("Alternate"))
      this.alternateCS = new DeviceCS(this, stream.get("Alternate").stringValue());
    else
      this.alternateCS = new DeviceCS(this, nbOfComponents());

    PDFArray range = stream.get("Range").toPDFArray();
    PDFStream metadata = stream.get("Metadata").toPDFStream();

    Reference ref = stream.reference();
    if (ref.isIndirectReference())    
      this.colorSpace = map.get(ref);
    
    if (this.colorSpace == null)
      try
      {
        this.colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(stream.byteValues()));
        map.put(ref,  this.colorSpace);          
//        Log.debug(this, " - colorspace: " + colorSpace.getType() + ", ref=" + stream.reference());
      } catch (Exception e)
      {
        Log.warn(this, " - ICCBased color space reading error: " + e.getMessage());
      }

    // Log.debug(this, " - cs: "+this.colorSpace);
    if (this.colorSpace == null)
      this.colorSpace = alternateCS.colorSpace;
    if (alternateCS != null)
      this.add(alternateCS);
    this.nbOfComponents = colorSpace.getNumComponents();
  }

  @Override
  public boolean isStandardCS()
  {
    return this.alternateCS.isStandardCS();
  }

  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "[" + Color3.colorSpace(this.colorSpace.getType()) + "]";
  }
}
