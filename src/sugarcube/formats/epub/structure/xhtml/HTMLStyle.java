package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class HTMLStyle extends HTMLNode
{
  public static final String TAG = "style";
  private String css;

  public HTMLStyle(String css)
  {
    super(TAG);
    this.css = css;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    //this.setCData("\n//<![CDATA[\n" + js + "//]]>\n", false);  
    this.setCData(css, false);
    return super.writeAttributes(xml);
  }
}
