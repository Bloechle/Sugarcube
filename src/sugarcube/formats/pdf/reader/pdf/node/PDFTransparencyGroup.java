package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.pdf.node.colorspace.PDFColorSpace;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;

public class PDFTransparencyGroup extends PDFNode
{
  public String subtype;
  public PDFColorSpace cs;
  public boolean isolated;
  public boolean knockout;

  //TransparencyGroup are linked to 
  public PDFTransparencyGroup(PDFNode parent, PDFDictionary map)
  {
    super(Dexter.GROUP, parent);
    this.reference = map.reference();
    this.subtype = map.get("S").stringValue("null");
    if (!this.subtype.equals("Transparency"))
      Log.warn(this, " unknown group subtype: " + this.subtype);

    if (map.contains("CS"))
      this.cs = PDFColorSpace.instance(this, null, map.get("CS"));

    this.isolated = map.get("I").booleanValue(false);
    this.knockout = map.get("K").booleanValue(false);
  }

  @Override
  public String sticker()
  {
    return "PDFGroup";
  }

  @Override
  public String toString()
  {
    return "PDFGroup " + reference()
      + "\nCS[" + (cs == null ? "null" : cs.name()) + "]"
      + "\nIsolated[" + isolated + "]"
      + "\nKnockout[" + knockout + "]";
  }
}
