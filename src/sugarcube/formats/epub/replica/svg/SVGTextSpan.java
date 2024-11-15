package sugarcube.formats.epub.replica.svg;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.Unicodes;
import sugarcube.common.graphics.geom.Coords;
import sugarcube.common.graphics.geom.Point3;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Glyph;
import sugarcube.common.system.util.Unicode;
import sugarcube.common.data.xml.CharRef;
import sugarcube.common.data.xml.Xml;
import sugarcube.formats.ocd.objects.OCDNode;
import sugarcube.formats.ocd.objects.OCDText;
import sugarcube.formats.ocd.objects.font.SVGGlyph;

import java.util.Collection;
import java.util.Comparator;

public class SVGTextSpan extends SVGPaintable
{
  private OCDText ocdText;
  public Unicodes unicodes;
  public String x = "0";
  public String y = "0";
  public double sy = 1;
  private Rectangle3 viewBox;
  public String fontFamily;
  public int i0 = 0;
  public int i1 = -1;
  public int override = 0;
  public int xmlSkipReturn = 0;
  public Coords coords;
  public boolean transparent;
  public float zOrder = -1;

  public SVGTextSpan(SVGText parent, SVGPage page)
  {
    super("tspan", parent, page);
  }

  public SVGTextSpan(SVGText parent, SVGPage page, OCDText ocdText, Rectangle3 viewBox, boolean transparent)
  {
    super("tspan", parent, page);
    this.ocdText = ocdText;
    this.unicodes = ocdText.unicodes();
    this.viewBox = viewBox;
    this.transparent = transparent;
    this.coords = svgCoords(ocdText);
    this.blend = ocdText.svgBlendMode();
    this.i1 = coords.size() - 1;
    this.zOrder = ocdText.zOrder;

    this.setFillColor(transparent ? Color3.WHITE.alpha(0.01) : ocdText.fillColor());
    if (transparent)
      this.setStroke(Color3.BLACK, null, 0);
    else
      this.setStroke(ocdText.strokeColor(), ocdText.isStroked() ? ocdText.stroke() : null, ocdText.transform().scaleWidth());

    this.clipID = transparent ? SVGClip.NONE : ocdText.clipID();

    // setFontFamily is called later in epubwriter since fontfamily may still be
    // modified
    this.fontFamily = ocdText.fontname().replace("#", "-");

    Transform3 tm = ocdText.transform().translateBack(viewBox.origin());
    this.setFontSize(page.toString(ocdText.fontsize() * tm.scaleX()));
    this.x = page.toString(coords.x(i0) - viewBox.x);
    this.y = page.toString(coords.y(i0) - viewBox.y);
    this.sy = tm.scaleY();
    // this.transform = SVG.toString(tm.scaleX, tm.shearY, tm.shearX, tm.scaleY,
    // 0, 0);
  }

  public OCDText text()
  {
    return this.ocdText;
  }

  public Coords svgCoords(OCDText text)
  {
    boolean vMode = text.isVerticalMode();
    Coords c = new Coords(0.0, 0.0);
    double delta = 0;
    float csi = 0;
    float[] cs = text.cs();
    float fs = text.fontsize();
    Transform3 tm = text.transform();
    Glyph[] glyphs = text.glyphs();

    // debugging a specific char
    char fl = 0;// 64258;
    if (fl > 0 && text.unicodes().containsCode(fl))
      Log.debug(this, ".svgCoords - " + text);

    for (int i = 0; i < glyphs.length; i++)
    {
      Glyph glyph = glyphs[i];
      if (fl > 0 && glyph.code().contains(fl + ""))
        Log.debug(this, ".svgCoords - " + glyph.code());

      if (i < cs.length && !Float.isNaN(cs[i]))
        csi = cs[i];
      if (vMode)
        c.add(0, delta += (-glyph.height() + csi) * fs);
      else
        c.add(delta += (glyph.width() + csi) * fs, 0);

      String chars = glyph.code();
      if (chars.length() > 1)
        for (int n = 1; n < chars.length(); n++)
          if (vMode)
            c.add(0, delta);
          else
            c.add(delta, 0);
    }

    c = c.transform(tm);
    // since we render vertical glyphs using LTR we need to offset vertical
    // glyphs coords to match their horizontal origins
    if (vMode)
      for (int i = 0; i < glyphs.length; i++)
      {
        Glyph glyph = glyphs[i];
        Point3 origin = glyph.vertOrigin();
        Point3 p = c.get(i);
        if (i < c.size())
          c.setXY(i, p.x - origin.x * fs * tm.scaleX(), p.y + origin.y * fs * tm.scaleY());
      }

    return c;
  }

  @Override
  public void setFontFamily(String font)
  {
    super.setFontFamily(this.fontFamily = font);
  }

  @Override
  public SVGText parent()
  {
    return (SVGText) parent;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    int start = i0;
    int end = i1;
    String cdata = CharRef.Html(transparent ? Unicode.expand(unicodes.string(start, end)) : unicodes.string(start, end));

    if (cdata.startsWith(" "))
      start++;

    this.x = "" + xml.toString(coords.x(start) - viewBox.x);
    this.y = "" + xml.toString(coords.y(start) - viewBox.y);

    if (ocdText.isVerticalMode())
      for (int i = start + 1; i < end; i++)
        if (transparent)// expands "fi" like ligature to allow text searching
        {
          String lig = Unicode.expand(unicodes.codeAt(i));
          for (int j = 0; j < lig.length(); j++)
          {
            float cy = coords.y(i);
            if (j > 0 && i + 1 < coords.size())
              cy += (coords.y(i + 1) - cy) * j / (float) lig.length();
            this.y += " " + xml.toString(cy - viewBox.y);

          }
        } else
          this.y += " " + xml.toString(coords.y(i) - viewBox.y);
    else
      for (int i = start + 1; i < end; i++)
        if (transparent)// expands "fi" like ligature to allow text searching
        {
          String lig = Unicode.expand(unicodes.codeAt(i));
          for (int j = 0; j < lig.length(); j++)
          {
            float cx = coords.x(i);
            if (j > 0 && i + 1 < coords.size())
              cx += (coords.x(i + 1) - cx) * j / (float) lig.length();
            this.x += " " + xml.toString(cx - viewBox.x);

          }
        } else
          this.x += " " + xml.toString(coords.x(i) - viewBox.x);

    if (ocdText.isVerticalMode())
    {
      String xx = x;
      for (int i = start + 1; i < end; i++)
        x += " " + xx;
    }
    if (!ocdText.unicodes().isRTL())
    {
      String yy = y;
      for (int i = start + 1; i < end; i++)
        y += " " + yy;
    }

    if (page.addSpanID)
      xml.write("id", "w" + (page.spanID++) + page.viewIndex());
    xml.write("y", y);
    xml.write("x", x);
    this.writeXmlBlend(xml);
    this.writeXmlClasses(xml);

    // Log.debug(this, ".writeCData - ["+cdata+"]");

    // strike dash needs a following char to be displayed in webkit
    if (cdata.endsWith("&#822;"))
      cdata = cdata + "\u00A0";

    // Log.debug(this, ".writeAttribute - "+Unicode.expand(cdata));

    xml.writeCData(transparent ? Unicode.expand(cdata) : cdata, false);
    if (this.xmlSkipReturn > 0)
      xml.skipReturn(xmlSkipReturn);
    return this.children();
  }

  @Override
  public void writeXmlClasses(Xml xml)
  {
    StringBuilder sb = new StringBuilder();
    SVGText text = this.parent();
    
//   Log.debug(this,  ".writeXmlClasses - "+unicodes.string()+", this="+Array.toString(cssClasses)+", parent="+Array.toString(text.cssClasses));
    
    for (int i = 0; i < cssClasses.length; i++)
      if (cssClasses[i] != null && !cssClasses[i].equals(text.cssClasses[i]) || cssClasses[i]==null && text.cssClasses[i]!=null)
      {
        // if this or previous span is different, force class writing
        text.cssClasses[i] = null;
        // text spans modifies the state inside a text element
        sb.append(cssClasses[i]).append(" ");
      }
    xml.write("class", sb.toString().trim());
  }

  public String allClasses()
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < cssClasses.length; i++)
      if (cssClasses[i] != null)
        sb.append(cssClasses[i]).append(" ");
    return sb.toString().trim();
  }

  @Override
  public SVGTextSpan copy()
  {
    return new SVGTextSpan((SVGText) parent, page, ocdText, viewBox, transparent);
  }

  public String fragString()
  {
    return unicodes.string(i0, i1);
  }

  public List3<SVGTextSpan> highlightFragment(int start, int end, Color3 color)
  {
    List3<SVGTextSpan> spans = new List3<>();
    SVGTextSpan span;
    if (start > 0)
    {
      span = copy();
      span.i0 = 0;
      span.i1 = start;
      spans.add(span);
    }

    span = copy();
    span.i0 = start;
    span.i1 = end;
    span.setFillColor(color);
    spans.add(span);

    if (end < coords.size())
    {
      span = copy();
      span.i0 = end;
      span.i1 = coords.size() - 1;
      spans.add(span);
    }
    return spans;
  }

  public List3<SVGTextSpan> subfontFragments()
  {
    Map3<Integer, Integer> mapOver = new Map3<>();

    int lastOver = 0;
    mapOver.put(0, lastOver);

    Glyph[] glyphs = this.ocdText.glyphs();
    for (int i = 0; i < glyphs.length; i++)
    {
      Glyph glyph = glyphs[i];
      int over = SVGGlyph.isWhitespace(glyph) ? lastOver : glyph.override();

      if (over != lastOver)
        mapOver.put(i, over);// flags standard/override run start

      lastOver = over;
    }

    mapOver.put(glyphs.length, lastOver == 0 ? 1 : 0);

    lastOver = mapOver.remove(0);

    int start = 0;
    SVGTextSpan span = null;
    List3<SVGTextSpan> spans = new List3<>();
    for (int end : mapOver.keySet())
    {
      span = copy();
      span.xmlSkipReturn = 1;
      span.i0 = start;
      span.i1 = end;

      span.override = lastOver;
      spans.add(span);

      lastOver = mapOver.get(start=end);
    }
    if (span != null)
      span.xmlSkipReturn = 0;

    // if (spans.size() > 1)
    // for (SVGTextSpan frag : spans)
    // Log.debug(this, ".overrideFragment - override[" + frag.override + "] " +
    // frag.fontFamily + ":" + frag.fragString());
    return spans;
  }

  public static Comparator<SVGTextSpan> zComparator()
  {
    return (o1, o2) -> o1.zOrder < o2.zOrder ? -1 : o1.zOrder > o2.zOrder ? 1 : 0;
  }
}
