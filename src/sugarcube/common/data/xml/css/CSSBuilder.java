package sugarcube.common.data.xml.css;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Property3;
import sugarcube.common.data.collections.Props;

public class CSSBuilder
{
  private Appendable data;

  public CSSBuilder()
  {
    this.data = new StringBuilder();
  }

  public CSSBuilder(String element, String... properties)
  {
    this();
    this.write(element, properties);
  }

  public CSSBuilder write(String data)
  {
    try
    {
      this.data.append(data);
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return this;
  }

  public CSSBuilder writeComment(String comment)
  {
    return this.write("/*" + comment + "*/\n");
  }

  public CSSBuilder write(String element, String... properties)
  {
    try
    {
      this.data.append(element + " {");
      for (int i = 0; i < properties.length; i++)
        if (properties[i].contains(":"))
          this.data.append("\n " + properties[i] + (properties[i].endsWith(";") ? "" : ";"));
        else
          Log.warn(this, ".write - CSS properties declared without colon: " + properties[i]);
      this.data.append("\n}\n");
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return this;
  }

  public CSSBuilder write(String element, String css)
  {
    try
    {
      this.data.append(element + " {\n ");
      this.data.append(css.replace(" ", "").replace("\n", "").replace(";", ";\n "));
      this.data.append("}\n\n");
    } catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return this;
  }

  public CSSBuilder writeFont(String fontname, String... filepaths)
  {
    return this.write(font(fontname, filepaths));
  }

  public static String font(String fontname, String... filepaths)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("@font-face {\n");
    sb.append(" font-family: \"" + fontname + "\";\n");
    sb.append(" font-style: " + "normal" + ";\n");
    sb.append(" font-weight: " + "normal" + ";\n");
    sb.append(" src:");
    int i = 0;
    for (String path : filepaths)
    {
      if (path == null || path.trim().isEmpty())
        continue;
      String type = "svg";
      if (path.endsWith(".otf"))
        type = "opentype";
      else if (path.endsWith(".ttf"))
        type = "truetype";
      else if (path.endsWith(".woff"))
        type = "woff";
      else if (path.endsWith(".eot"))
        type = "embedded-opentype";

      sb.append(i++ == 0 ? "" : ",\n     ");
      sb.append(" url(\"" + path + "\") format(\"" + type + "\")");
    }
    sb.append(";\n }\n\n");
    return sb.toString();
  }

  @Override
  public String toString()
  {
    return this.data.toString().replace("\n }", " }");
  }

  public static String parseKey(String declaration)// color:blue; => color:
  {
    declaration = declaration.trim();
    String key = declaration.substring(0, declaration.indexOf(':') + 1).trim();
    // Log.debug(CSSBuilder.class, ".parseKey - " + key);
    return key;
  }

  public static String parseValue(String declaration)// color:blue; => blue
  {
    declaration = declaration.trim();
    declaration = declaration.endsWith(";") ? declaration.substring(0, declaration.length() - 1) : declaration;
    String value = declaration.substring(declaration.indexOf(':') + 1).trim();
    // Log.debug(CSSBuilder.class, ".parseValue - " + value);
    return value;
  }

  public static Props parseProperties(String declarations)// color:blue;
                                                          // background:none;
                                                          // => as String
                                                          // map
  {
    return parseProperties(declarations, new Props());
  }

  public static Props parseProperties(String declarations, Props props)
  {
    if (declarations != null && !declarations.trim().isEmpty())
      for (String prop : declarations.split(";"))
      {
        prop = prop.trim();
        if (!prop.isEmpty())
          props.put(parseKey(prop), parseValue(prop));// colon added back as CSS
                                                      // property marker :-)
      }
    return props;
  }

  public static String mergeProperties(Props props)
  {
    StringBuilder style = new StringBuilder();
    for (Property3 entry : props.entries())
      if (!entry.isEmptyKey() && entry.isCssKey())
        style.append(entry.key).append(entry.value.endsWith(";") ? entry.value : entry.value + ";");
    return style.toString();
  }
}
