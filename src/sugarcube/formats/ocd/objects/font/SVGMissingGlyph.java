package sugarcube.formats.ocd.objects.font;

import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGMissingGlyph extends OCDNode
{  
  public static final String TAG = "missing-glyph";
  private SVGFont font;

  public SVGMissingGlyph(SVGFont parent)
  {
    super(TAG, parent);
    this.font = parent;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("horiz-adv-x", 500);
    xml.write("d", "m 0 0 z");
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
  }
}