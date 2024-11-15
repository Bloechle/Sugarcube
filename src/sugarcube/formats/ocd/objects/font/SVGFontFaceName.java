package sugarcube.formats.ocd.objects.font;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGFontFaceName extends OCDNode
{
  public static final String TAG = "font-face-name";
  protected SVGFontFaceSrc fontFaceSrc;  

  public SVGFontFaceName(SVGFontFaceSrc fontFaceSrc)
  {
    super(TAG, fontFaceSrc);
    this.fontFaceSrc = fontFaceSrc;    
  }
  
  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {    
    xml.write("name", fontFaceSrc.fontFace.font.fontname());
    return this.children();
  }  
}
