package sugarcube.formats.ocd.objects.font;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGFontFaceRef extends OCDNode
{
  public static final String TAG = "font-face";
  private SVGFont font;
  private String fileReference;

  public SVGFontFaceRef(SVGFont parent, String fileReference)
  {
    super(TAG, parent);
    this.font = parent;
    this.fileReference = fileReference;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("font-family", font.fontname());
    xml.startCDataInlineTagging();
    xml.openInlineTag("font-face-src");
    xml.openInlineTag("font-face-uri", "xlink:href", fileReference);
    xml.inlineTag("font-face-format", "string", "svg");
    xml.closeInlineTag();
    xml.closeInlineTag();
    return this.children();
  }
}
