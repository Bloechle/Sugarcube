package sugarcube.formats.ocd.objects.font;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGFontFaceSrc extends OCDNode
{
  public static final String TAG = "font-face-src";
  protected SVGFontFace fontFace;
  protected SVGFontFaceName fontName;

  public SVGFontFaceSrc(SVGFontFace fontFace)
  {
    super(TAG, fontFace);
    this.fontFace = fontFace;
    this.fontName = new SVGFontFaceName(this);
  }
  
  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {    
    return new List3<OCDNode>(fontName);
  }  
}
