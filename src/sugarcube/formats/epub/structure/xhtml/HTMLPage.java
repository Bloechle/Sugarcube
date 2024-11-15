package sugarcube.formats.epub.structure.xhtml;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.system.io.File3;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlDecimalFormat;
import sugarcube.common.data.xml.css.CSSBuilder;
import sugarcube.formats.epub.EPubWriter;
import sugarcube.formats.epub.structure.EPub;
import sugarcube.formats.epub.replica.ReplicaWriter;
import sugarcube.formats.epub.replica.svg.SVGGroup;
import sugarcube.formats.epub.replica.svg.SVGLinkBox;
import sugarcube.formats.ocd.objects.*;

import java.util.Collection;
import java.util.regex.Matcher;

public class HTMLPage extends HTMLDiv
{
  public static final XmlDecimalFormat DF = Xml.decimalFormat(3);
  public static final float EPSILON = 0.01f;
  private OCDPage page;
  private Rectangle3 viewBox;
  private CSSBuilder css = new CSSBuilder();
  public Set3<String> fonts = new Set3<String>();
  public transient EPubWriter writer;
  public transient List3<HTMLNode> boxes = new List3<HTMLNode>();
  public transient SVGGroup lastClipGroup = null;
  public boolean lossless = true;
  public float pageScale = 1f;
  public StringSet addedAnnots = new StringSet();

  public HTMLPage(OCDPage page, ReplicaWriter writer, Rectangle3 viewBox)
  {
    super("class", "root");
    this.page = page;
    this.writer = writer;
    this.viewBox = page.viewBox();
    this.lossless = writer.props.png();
    this.pageScale = writer.pageScale;
    this.addAttribute("style", "width:" + viewBox.width() + "px; height:" + viewBox.height() + "px;");

    for (OCDAnnot annot : page.annots())
      // before writeContent to check URL detection during content writing
      if (annot.isLink())
        this.addLinkAnnot(annot);

    // for (OCDTextBlock block : page.content().blocks())
    // nodes.add(this.writeText(block));
    for (OCDText text : page.content().allTexts())
      // if (!(text.parent() instanceof OCDTextLine))
      nodes.addAll(this.writeText(text));

    for (HTMLNode box : boxes)
      // superposed to content
      this.addChild(box);

    // for (String font : fonts)//after writeContent since used fonts discovered
    // during content writing
    // writer.fonts.add(font); // css.writeSvgFont(font, EPUB.FONT_FOLDER + font
    // + SVG.EXT);
    // for (Map.Entry<String, String> entry : cssClasses.entrySet())
    // css.write("." + entry.getValue(), entry.getKey());

    String boxClass = "." + SVGLinkBox.CLASS;
    css.write(boxClass, "stroke-width:0;fill-opacity:0;fill:black;");
    css.write(boxClass + ":hover", "stroke-width:0;fill-opacity:0.2;fill:white;");
    css.writeComment(EPub.PrintedByReplica());
    // this.style.css = css.toString();

    for (String ref : addedAnnots)
      page.annots().removeAnnotation(ref);
  }

  public Rectangle3 viewBox()
  {
    return viewBox;
  }

  private HTMLParagraph writeText(OCDTextBlock block)
  {
    HTMLParagraph p = new HTMLParagraph();
    for (OCDText text : block.allTexts())
      p.addChildren(writeText(text));
    return p;
  }

  private List3<HTMLTextLayer> writeText(OCDText text)
  {
    String string = text.string();
    if (writer.props.detectUrl())
    {
      Matcher matcher = writer.urlPattern.matcher(string);
      if (matcher.find())
      {
        String link = matcher.group();
        Log.debug(this,  ".writeTextSpan - "+link);
        if (!link.startsWith("http://") && !link.startsWith("https://") && !link.startsWith("ftp://") && !link.startsWith("ftps://"))
          link = "http://" + link;
        if (link.lastIndexOf(".") > 12)
          this.addLinkAnnot(text.bounds(matcher.start(), matcher.end()), link);
      } else if ((matcher = writer.emailPattern.matcher(string)).find())
      {
        String email = matcher.group();
        Log.debug(this, ".writeTextSpan - email link identified [" + matcher.group() + "]: " + email);
        Rectangle3 box = text.bounds(matcher.start(), matcher.end());
        this.addLinkAnnot(box, "mailto:" + email);
        OCDAnnot annot = text.page().annotations().addLinkAnnot(box, "mailto:" + email);
        this.addedAnnots.add(annot.needID());
      }
    }

    List3<HTMLTextLayer> layer = new List3<HTMLTextLayer>();
    Coords coords = text.coords();
    String uni = text.string();
    float fs = text.scaledFontsize();
    int i0 = 0;
    for (int i = 0; i < uni.length(); i++)
    {
      char c = uni.charAt(i);
      if (c == ' ' || i == uni.length() - 1)
      {
        Point3 p = coords.pointAt(i0 < coords.size() ? i0 : coords.size() - 1);

        String sub = uni.substring(i0, i0 = i + 1);
        boolean doAdd = true;
        for (int k = 0; k < sub.length(); k++)
          if (sub.codePointAt(k) > 50000) // because ipad transforms some html
                                          // entities in weird icons !!!
          {
            doAdd = false;
            break;
          }
        if (doAdd)
        {
          HTMLTextLayer xhtmlText = new HTMLTextLayer(this, c == ' ' ? sub + " " : sub, fs * pageScale, (p.x - viewBox.x) * pageScale,
              (p.y - viewBox.y) * pageScale);
          xhtmlText.setParent(this);
          layer.add(xhtmlText);
        }
      }
    }
    return layer;
  }

  public final void addLinkAnnot(OCDAnnot annot)
  {
    HTMLBox rect = new HTMLBox(null, annot.bounds().shiftBack(viewBox.xy()).scale(pageScale));
    String uri = annot.link();
    if (!annot.isWWW() && uri != null)
      uri = File3.Extense(uri, XHTML.FILE_EXTENSION);

    if (uri != null)
    {
      HTMLLinkBox link = new HTMLLinkBox(uri == null ? "" : uri, rect);
      this.boxes.add(link);
    }
  }

  public final void addLinkAnnot(Rectangle3 box, String uri)
  {
    HTMLBox rect = new HTMLBox(null, box.shiftBack(viewBox.xy()).scale(pageScale));
    HTMLLinkBox link = new HTMLLinkBox(uri == null ? "" : uri, rect);

    for (OCDNode l : boxes)
      if (l instanceof HTMLLinkBox && link.overlap((HTMLLinkBox) l) > 0.5)// link
                                                                    // already
                                                                    // defined !
        return;
    this.boxes.add(link);
  }

  public OCDPage ocdPage()
  {
    return page;
  }

  public String filename()
  {
    return "page-" + page.number() + XHTML.FILE_EXTENSION;
  }

  @Override
  public Collection<? extends OCDNode> children()
  {
    return nodes;
  }

  public int width()
  {
    return viewBox.intWidth();
  }

  public int height()
  {
    return viewBox.intHeight();
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    return this.children();
  }

  public String toString(double d)
  {
    return Xml.toString(d, DF);
  }

  public String toString(float[] data)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length; i++)
      sb.append(toString(data[i])).append((i < data.length - 1 ? " " : ""));
    return sb.toString();
  }
}
