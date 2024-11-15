package sugarcube.common.data.xml.svg;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class SVGStyle extends OCDNode
{
  public static final String TAG = "style";
  public String css = "";

  public SVGStyle(OCDNode parent)
  {
    super(TAG, parent);
  }
  
  public boolean isEmpty()
  {
    return css == null || css.trim().isEmpty();
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("type", "text/css");
    xml.writeCData("<![CDATA[\n" + css + "]]>", false);
    return this.children();
  }
}
