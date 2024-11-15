package sugarcube.formats.epub.replica.svg;

import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.common.data.xml.svg.SVG;
import sugarcube.formats.ocd.objects.OCDNode;

public class SVGPaintable extends OCDNode
{

  private static int INDEX = 0;
  public static int FILL = INDEX++;
  public static int FILL_OPACITY = INDEX++;
  public static int STROKE = INDEX++;
  public static int STROKE_OPACITY = INDEX++;
  public static int STROKE_WIDTH = INDEX++;
  public static int STROKE_DASH = INDEX++;
  public static int FONTSIZE = INDEX++;
  public static int FONT = INDEX++;
  public static int BLEND = INDEX++;
  public String[] cssClasses = new String[INDEX];
  // public Set8 setClasses = new Set8();
  public String clipID;
  public String blend;
  public SVGPage page;

  public SVGPaintable(String tag, OCDNode parent, SVGPage page)
  {
    super(tag, parent);
    this.page = page;
  }

  public String cssClass(String... keyValDef)
  {
    String prefix = "c";
    switch (keyValDef[0].trim())
    {
    case "fill":
      prefix = "fc";
      break;
    case "fill-opacity":
      prefix = "fo";
      break;
    case "stroke":
      prefix = "sc";
      break;
    case "stroke-width":
      prefix = "sw";
      break;
    case "stroke-dasharray":
      prefix = "sd";
      break;
    case "font-size":
      prefix = "fs";
      break;
    case "font-family":
      prefix = "ff";
      break;
    case "mix-blend-mode":
      prefix = "bm";
      break;
    }
    return page.needCssClass(prefix, CSS.Get(keyValDef));
  }

  public String css(int classIndex, String... keyValDef)
  {
    return cssClasses[classIndex] = cssClass(keyValDef);
  }

  public void setFillColor(Color3 color)
  {
    css(FILL, "fill", color.isTransparent() ? "none" : SVG.toString(color), "");
    css(FILL_OPACITY, "fill-opacity", page.toString(color.alpha()), "");
  }

  public void setStroke(Color3 color, Stroke3 stroke, double scale)
  {
    if (stroke == null || color.isTransparent())
    {
      css(STROKE, "stroke", "none", "");
      return;
    } else
    {
      css(STROKE, "stroke", SVG.toString(color), "");
      css(STROKE_OPACITY, "stroke-opacity", page.toString(color.alpha()), "");
    }

    css(STROKE_WIDTH, "stroke-width", page.toString(stroke.width() * scale), "", "stroke-linecap", stroke.cap(), "butt", "stroke-linejoin",
        stroke.join(), "miter");

    if (!stroke.hasDash())
      return;

    css(STROKE_DASH, "stroke-dasharray", page.toString(stroke.svgDash()), "none", "stroke-dashoffset", SVG.toString(stroke.phase()), "0");
  }

  public void setFontSize(String size)
  {
    css(FONTSIZE, "font-size", size + "px", "");
  }

  public void setFontFamily(String font)
  {
    css(FONT, "font-family", CSS.guillemets(font), "");
  }

  public void setBlendMode(String blend)
  {
    // if (blend != null && !blend.isEmpty())
    // cssClasses[6] = cssClass("mix-blend-mode", blend);
  }

  public boolean hasBlend()
  {
    return blend != null && !blend.equalsIgnoreCase("normal");
  }

  public boolean hasClip()
  {
    return clipID != null && !SVGClip.NONE.equals(clipID);
  }

  public static String unguillemets(String text)
  {
    if (text.startsWith("\""))
      text = text.substring(1);
    if (text.endsWith("\""))
      text = text.substring(0, text.length() - 1);
    return text;
  }

  public void writeXmlBlend(Xml xml)
  {
    if (false && hasBlend())// deactivated, needs to be implemented
      xml.write("style", "mix-blend-mode: " + blend + ";");
  }

  public void writeXmlClip(Xml xml)
  {
    if (hasClip())
      xml.write("clip-path", "url(#" + clipID + page.viewIndex() + ")");
  }

  public void writeXmlClasses(Xml xml)
  {
    StringBuilder sb = new StringBuilder();
    // for (String cssClass : this.cssClasses)
    // if (cssClass != null && setClasses.hasnt(cssClass))
    // sb.append(cssClass).append(" ");
    for (String cssClass : cssClasses)
      if (cssClass != null)
        sb.append(cssClass).append(" ");
    if (sb.length() > 0)
      xml.write("class", sb.toString().trim());
  }
}
