
package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class HTMLSpan extends HTMLNode
{
  public static final String TAG = "span";

  public HTMLSpan(String... props)
  {
    super(TAG, props);
  }

  public boolean equalStyleAndClasses(HTMLSpan span)
  {
    return span!=null && style().equals(span.style()) && classes().equals(span.classes());
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.skipReturn(1);
    props.writeAttributes(xml, false, false);    
    return this.children();
  }
}