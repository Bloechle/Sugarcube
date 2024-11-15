package sugarcube.formats.pdf.reader.gui;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.ui.gui.TreeRenderer3;
import sugarcube.formats.pdf.reader.pdf.node.PDFNode;
import sugarcube.formats.pdf.reader.pdf.object.PDFDictionary;
import sugarcube.formats.pdf.reader.pdf.object.PDFObject;
import sugarcube.resources.pdf.tree.ResTree;

public class PDFTreeRenderer extends TreeRenderer3
{
  private static final Set3<String> CS = new Set3<String>(
    "Indexed", "Separation", "ICCBased", "CalRGB", "CalGray", "Lab", "DeviceN", "Pattern");

  public PDFTreeRenderer()
  {
    super(ResTree.class, "Document", "Page", "Pages", "Outlines", "StructTreeRoot", "StructElem",
      "Text", "Image", "XObjects", "Path", "Font", "Fonts",
      "FontDescriptor", "Clip", "Colorspace", "Colorspaces",
      "Shading", "Shadings", "ExtGState", "ExtGStates",
      "Resources", "Content", "Leaf", "BadOp", "Group", "Metadata",
      "Environment", "Stream", "ObjStm", "Encoding", "Function",
      "Trailer");
  }

  
  @Override
  public String iconName(Object value)
  {
    String type = nodeType(value);
    return type == null ? objectType(value) : type; 
  }

  private static String nodeType(Object value)
  {
    PDFNode node = value instanceof PDFNode ? (PDFNode) value : null;
    return node == null ? null : node.getType();
  }

  private static String objectType(Object value)
  {
    PDFObject po = value instanceof PDFObject ? (PDFObject) value : null;
    if (po == null)
      return null;
    String type = null;
    PDFDictionary dico = po != null && po.isPDFDictionary() ? (PDFDictionary) po : null;
    if (dico != null)
      if (dico.is("Subtype", "Image"))
        type = "Image";
      else if (po.isPDFStream())
        type = "Stream";
      else if (dico.contains("FunctionType"))
        type = "Function";
      else if (dico.contains("ColorSpace"))
        type = "ColorSpaceFunction";
      else if (dico.contains("Type"))
        type = dico.get("Type").stringValue();

    String firstArray = po != null && po.isPDFArray() && !po.children().isEmpty() ? po.first().stringValue() : null;
    if (firstArray != null)
      if (CS.contains(firstArray))
        type = "ColorSpace";

    return type == null ? po.type.name() : type;
  }
}
