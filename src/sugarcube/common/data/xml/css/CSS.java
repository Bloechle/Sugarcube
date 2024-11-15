package sugarcube.common.data.xml.css;

import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlDecimalFormat;

public interface CSS
{
  XmlDecimalFormat DF = Xml.decimalFormat(3);

  String FILE_EXTENSION = ".css";
  String STYLE = "style";
  // CSS keys
  String BorderColor = "border-color:";
  String BorderWidth = "border-width:";
  String BorderStyle = "border-style:";
  String Clipping = "clipping:";
  String Color = "color:";
  String FontName = "font-name:";
  String FontFamily = "font-family:";
  String FontSize = "font-size:";
  String FontStyle = "font-style:";
  String FontWeight = "font-weight:";
  String TextAlign = "text-align:";
  String LetterSpacing = "letter-spacing:";
  String LineHeight = "line-height:";
  String TextDecoration = "text-decoration:";
  String TextScript = "text-script:";
  String VerticalAlign = "vertical-align:";
  // public static final String Baseline = "baseline:";
  // CSS values
  String _normal = "normal";
  String _bold = "bold";
  String _italic = "italic";
  String _center = "center";
  String _justify = "justify";
  String _left = "left";
  String _right = "right";
  String _overline = "overline";
  String _lineThrough = "line-through";
  String _underline = "underline";
  String _subscript = "subscript";
  String _superscript = "superscript";
  String _top = "top";
  String _middle = "middle";
  String _bottom = "bottom";
  String _none = "none";
  String _dotted = "dotted";
  String _dashed = "dashed";
  String _solid = "solid";
  // CSS key-values
  String FontStyle_normal = FontStyle + _normal;
  String FontStyle_italic = FontStyle + _italic;
  String FontWeight_normal = FontWeight + _normal;
  String FontWeight_bold = FontWeight + _bold;
  String TextAlign_center = TextAlign + _center;
  String TextAlign_justify = TextAlign + _justify;
  String TextAlign_left = TextAlign + _left;
  String TextAlign_right = TextAlign + _right;
  String VerticalAlign_bottom = VerticalAlign + _bottom;
  String VerticalAlign_middle = VerticalAlign + _middle;
  String VerticalAlign_top = VerticalAlign + _top;
  String TextDecoration_overline = TextDecoration + _overline;
  String TextDecoration_lineThrough = TextDecoration + _lineThrough;
  String TextDecoration_underline = TextDecoration + _underline;
  String TextScript_superscript = TextScript + _superscript;
  String TextScript_subscript = TextScript + _subscript;

  static String Get(String... kvi)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < kvi.length; i += 3)
    {
      String init = i + 2 < kvi.length ? kvi[i + 2] : null;
      if (init == null || init.isEmpty() || !kvi[i + 1].equals(init))
        sb.append(kvi[i]).append(":").append(kvi[i + 1]).append(";");
    }
    return sb.toString();
  }

  static String guillemets(String text)
  {
    return "\"" + text + "\"";
  }

  static String toString(double d)
  {
    return Xml.toString(d, DF);
  }

  static String toString(float[] data)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length; i++)
      sb.append(toString(data[i])).append((i < data.length - 1 ? " " : ""));
    return sb.toString();
  }

  static String Style(String oldStyle, String newStyle)
  {
    oldStyle = oldStyle == null ? "" : oldStyle.trim();    
    if(!oldStyle.isEmpty() && !oldStyle.endsWith(";"))
      oldStyle += "; ";

    if (!newStyle.contains(":"))
      return oldStyle + newStyle;

    String style = "";
    
    for (String rule : newStyle.trim().split(";"))
    {
      if(rule.trim().isEmpty())
        continue;
      
      int sep = rule.indexOf(':');
      String key = sep > 0 ? rule.substring(0, sep).trim() : rule.trim();
      rule = sep > 0 ? rule.substring(sep + 1).trim() : "";

      String[] keys = key.contains(".") ? key.split("\\.") : (key.contains(",") ? key.split(",") : new String[]
      { key });
      for (int i = 0; i < keys.length; i++)
      {
        key = keys[i].trim();
        switch (key.toLowerCase())
        {
        case "dis":
        case "disp":
          key = "display";
          rule = Replace(rule, "iblock", "inline-block");
          break;
        case "vis":
          key = "visibility";
          break;
        case "h":
        case "height":
          key = "height";
          rule = Pixelize(rule);
          break;
        case "w":
        case "width":
          key = "width";
          rule = Pixelize(rule);
          break;
        case "m":
        case "margin":
          key = "margin";
          rule = Pixelize(rule);
          break;
        case "p":
        case "padding":
          key = "padding";
          rule = Pixelize(rule);
          break;
        case "mt":
        case "my":
        case "mtop":
        case "m-top":
        case "margin-top":
          key = "margin-top";
          rule = Pixelize(rule);
          break;
        case "mb":
        case "my-":
        case "mbot":
        case "m-bot":
        case "mbottom":
        case "margin-bottom":
          key = "margin-bottom";
          rule = Pixelize(rule);
          break;
        case "ml":
        case "mx":
        case "mleft":
        case "m-left":
        case "margin-left":
          key = "margin-left";
          rule = Pixelize(rule);
          break;
        case "mr":
        case "mx-":
        case "mright":
        case "m-right":
        case "margin-right":
          key = "margin-right";
          rule = Pixelize(rule);
          break;
        case "pos":
        case "position":
          key = "position";
          rule = Replace(rule, "fix", "fixed");
          rule = Replace(rule, "abs", "absolute");
          rule = Replace(rule, "rel", "relative");
          break;
        case "pt":
        case "py":
        case "ptop":
        case "p-top":
        case "padding-top":
          key = "padding-top";
          rule = Pixelize(rule);
          break;
        case "pb":
        case "py-":
        case "pbot":
        case "p-bot":
        case "pbottom":
        case "padding-bottom":
          key = "padding-bottom";
          rule = Pixelize(rule);
          break;
        case "pl":
        case "px":
        case "pleft":
        case "p-left":
        case "padding-left":
          key = "padding-left";
          rule = Pixelize(rule);
          break;
        case "pr":
        case "px-":
        case "pright":
        case "p-right":
        case "padding-right":
          key = "padding-right";
          rule = Pixelize(rule);
          break;
        case "fs":
        case "f-size":
        case "font-size":
          key = "font-size";
          rule = Pixelize(rule);
          break;
        case "fw":
        case "f-weight":
          key = "font-weight";
          break;
        case "va":
        case "valign":
          key = "vertical-align";
          break;
        case "ta":
        case "talign":
          key = "text-align";
          break;
        case "td":
        case "tdeco":
          key = "text-decoration";
          break;
        case "lh":
        case "lheight":
          key = "line-height";
          break;
        case "bg":
          if (rule.contains("url"))
          {
            key = "background";
          } else
          {
            key = "background-color";
            rule = Colorize(rule);
          }
          break;
        case "bgc":
        case "bg-col":
        case "bgcol":
        case "bgcolor":
        case "bg-color":
        case "background-color":
          key = "background-color";
          rule = Colorize(rule);
          break;
        case "bgi":
        case "bgimg":
        case "bg-img":
          key = "background-image";
          break;
        case "bgp":
        case "bgpos":
        case "bg-pos":
          key = "background-position";
          break;
        case "bgr":
        case "bgrep":
        case "bg-rep":
          key = "background-repeat";
          rule = Replace(rule, "no", "no-repeat");
          break;
        case "bgs":
        case "bgsize":
        case "bg-size":
          key = "background-size";
          break;
        case "br":
        case "brad":
        case "b-rad":
        case "bradius":
        case "border-radius":
          key = "border-radius";
          rule = Pixelize(rule);
          break;
        case "bw":
        case "bwidth":
        case "b-width":
        case "border-width":
          key = "border-width";
          rule = Pixelize(rule);
          break;
        case "bc":
        case "bcol":
        case "b-col":
        case "b-color":
        case "bcolor":
        case "border-color":
          key = "border-color";
          rule = Colorize(rule);
          break;
        case "bs":
        case "bstyle":
        case "b-style":
          key = "border-style";
          break;
        case "c":
        case "col":
        case "color":
          key = "color";
          rule = Colorize(rule);
          break;
        case "o":
        case "op":
          key = "opacity";
          break;
        case "l":
        case "x":
        case "left":
          key = "left";
          rule = Pixelize(rule);
          break;
        case "t":
        case "y":
        case "top":
          key = "top";
          rule = Pixelize(rule);
          break;
        case "r":
        case "x-":
        case "right":
          key = "right";
          rule = Pixelize(rule);
          break;
        case "b":
        case "bot":
        case "y-":
        case "bottom":
          key = "bottom";
          rule = Pixelize(rule);
          break;
        case "z":
          key = "z-index";
          break;
        }

        style += " " + key + ":" + rule + ";";

//        Log.debug(CSS.class, ".Style - " + style);
      }
    }

    return (oldStyle + style).trim();
  }

  static String Colorize(String rule)
  {
    rule = Replace(rule, "white(", "rgba(255,255,255,");
    rule = Replace(rule, "black(", "rgba(0,0,0,");
    return rule;
  }

  static String Pixelize(String rule)
  {
    rule = Replace(rule, "'", "px ");
    if (rule.contains("00px"))
      return rule.replaceAll("^\\\\d00px", "0px 0px 0px 0px");
    return rule;
  }

  static String Replace(String text, String from, String to)
  {
    if (text.contains(from))
      return text.replace(from, to);
    return text;
  }
}
