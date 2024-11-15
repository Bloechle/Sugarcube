package sugarcube.formats.ocd.objects.font;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.svg.SVG;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGFontFace extends OCDNode
{
  public static final String TAG = "font-face";
  protected SVGFont font;
  protected SVGFontDesc desc;
  protected SVGFontFaceSrc src;

  public SVGFontFace(SVGFont parent)
  {
    super(TAG, parent);
    this.font = parent;
    this.desc = new SVGFontDesc(this);
    this.src = new SVGFontFaceSrc(this);
  }    

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("font-family", font.fontname());
    xml.write("units-per-em", font.unitsPerEm);
    xml.write("ascent", font.toUnits(font.ascent));
    xml.write("descent", font.toUnits(font.descent));
    xml.write("font-style", font.fontStyle);    
    xml.write("font-weight", font.fontWeight);
    return new List3<OCDNode>(desc,src);
  }
  
  @Override
  public void readAttributes(DomNode e)
  {
    font.fontFamily = e.value("font-family",font.fontname())+SVG.EXT;
    font.unitsPerEm = e.real("units-per-em", font.unitsPerEm);
    font.ascent = font.fromUnits(e.value("ascent", font.toUnits(font.ascent)));
    font.descent = font.fromUnits(e.value("descent", font.toUnits(font.descent)));
    font.fontStyle = e.value("font-style", font.fontStyle);
    font.fontWeight = e.value("font-weight", font.fontWeight);
  }  
}
