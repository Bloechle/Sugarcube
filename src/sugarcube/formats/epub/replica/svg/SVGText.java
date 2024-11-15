package sugarcube.formats.epub.replica.svg;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Occurrences;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;

import java.util.Collection;
import java.util.Collections;

public class SVGText extends SVGPaintable
{
  public List3<SVGTextSpan> spans = new List3<>();
  public String textID = null;

  public SVGText(OCDNode parent, SVGPage page, SVGTextSpan... spans)
  {
    this(parent, page, new List3<>(spans));
  }

  public SVGText(OCDNode parent, SVGPage page, List3<SVGTextSpan> spans)
  {
    super("text", parent, page);
    this.spans = spans;
    Collections.sort(spans, SVGTextSpan.zComparator());

    for (SVGTextSpan span : spans)
      span.setParent(this);

    for (int i = 0; i < cssClasses.length; i++)
    {
      Occurrences<String> occ = new Occurrences<>();
      for (SVGTextSpan span : spans)
        if (span.cssClasses[i] == null)
        {
          // occ = null;
          // break;
        } else
          occ.inc(span.cssClasses[i]);
      if (occ != null && occ.isPopulated())
        this.cssClasses[i] = occ.max();
    }

    this.clipID = SVGClip.NONE;
    this.textID = "p" + (page.paragraphID++) + page.viewIndex();
    // Log.debug(this, " - cssClass="+cssClass+", css="+css());
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    xml.write("id", textID);
    this.writeXmlClasses(xml);
    // xml.write("clip-path", "url(#clip-none)");
    return this.children();
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    return spans;
  }
}
