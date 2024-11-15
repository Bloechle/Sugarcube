package sugarcube.formats.pdf.reader.pdf.node.annotation;

import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.node.actions.PDFAction;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFRectangle;

public class PDFAnnotation extends PDFNode
{
  protected PDFRectangle bounds;
  protected String contents = "";
  protected int structParent = -1;
  protected float borderWidth = 1;

  public PDFAnnotation(PDFNode parent, String type, PDFDictionary map)
  {
    super(type, parent);

    this.contents = map.get("Contents").stringValue(contents);
    this.bounds = map.get("Rect").toPDFRectangle();
    this.structParent = map.get("StructParent").intValue(structParent);
    if (map.has("A"))
      add(PDFAction.Get(this, map.get("A").toPDFDictionary()));
    if (map.has("Border"))
      borderWidth = map.get("Border").toPDFArray().floatValue(2, borderWidth);
    if (map.has("BS"))
    {
      PDFDictionary bs = map.get("BS").toPDFDictionary();
      borderWidth = bs.get("W").floatValue(borderWidth);
    }

  }

  public PDFRectangle bounds()
  {
    return this.bounds;
  }

  public String contents()
  {
    return this.contents;
  }

  public boolean hasContents()
  {
    return this.contents != null && !this.contents.isEmpty();
  }

  @Override
  public String sticker()
  {
    return type;
  }
}
