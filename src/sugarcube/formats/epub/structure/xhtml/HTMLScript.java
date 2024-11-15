package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.data.xml.Xml;
import sugarcube.formats.epub.structure.js.JS;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;

public class HTMLScript extends HTMLNode
{
  public static final String TAG = "script";
  private JS js;

  public HTMLScript()
  {
    super(TAG, "type", "text/javascript");
  }

  public HTMLScript(JS js)
  {
    this();
    this.js = js;
  }

  public HTMLScript(String js)
  {
    this(new JS().write(js));
  }

  public HTMLScript js(JS js)
  {
    this.js = js;
    return this;
  }

  public HTMLScript js(String js)
  {
    return js(new JS().write(js));
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    this.setCData("\n//<![CDATA[\n" + js + "//]]>\n", false);
    // this.setCData("" + js, false);
    return super.writeAttributes(xml);
  }
}
