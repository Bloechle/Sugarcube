package sugarcube.formats.pdf.reader.pdf.node.annotation;

import sugarcube.common.system.log.Log;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFArray;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;

public class PDFAnnotations extends PDFNode<PDFAnnotation>
{
  public PDFAnnotations(PDFNode node, PDFArray annotations)
  {
    super("Annotations", node);
    for (PDFObject value : annotations)
      createAnnotation(node, value);
  }

  private void createAnnotation(PDFNode node, PDFObject po)
  {
    PDFDictionary dictionary = po.toPDFDictionary();
    if (dictionary.is("Subtype", "Link"))
      add(new PDFLinkAnnot(node, dictionary));
    else if (dictionary.is("Subtype", "Widget"))
      add(new PDFWidgetAnnot(node, dictionary));
    else if (dictionary.is("Subtype", "RichMedia"))
      add(new PDFRichMediaAnnot(node, dictionary));
    else
    {
      Log.debug(this,
          ".createAnnotation - not yet implemented annotation: " + dictionary.get("Subtype").toPDFName().stringValue() + ", " + node.reference());
      add(new PDFAnnotation(node, dictionary.get("Subtype").toPDFName().stringValue(), dictionary));
    }
  }

  @Override
  public String sticker()
  {
    return "Annotations[" + this.nbOfChildren() + "]";
  }

  @Override
  public String toString()
  {
    return "Annotations";
  }
}
