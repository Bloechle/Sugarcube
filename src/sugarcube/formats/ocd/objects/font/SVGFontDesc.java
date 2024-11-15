package sugarcube.formats.ocd.objects.font;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGFontDesc extends OCDNode
{
  public static final String TAG = "desc";
  protected SVGFontFace fontFace;

  public SVGFontDesc(SVGFontFace fontFace)
  {
    super(TAG, fontFace);
    this.fontFace = fontFace;
  }
  
  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.writeCData(fontFace.font.fontname());
    return this.children();
  }  
}
