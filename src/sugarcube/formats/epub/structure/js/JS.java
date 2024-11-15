package sugarcube.formats.epub.structure.js;

import sugarcube.common.graphics.geom.Path3;
import sugarcube.common.graphics.Stroke3;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlDecimalFormat;
import sugarcube.formats.ocd.objects.OCDClip;
import sugarcube.formats.ocd.objects.OCDPath;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.io.IOException;

public class JS
{
  private String context = "ctx";
  private Appendable data;
  private String lineReturn = "\n";
  private XmlDecimalFormat decimalFormat;
  // private int decimalFactor;
  private char lastChar = ' ';

  public JS()
  {
    this(new StringBuilder());
  }

  public JS(Appendable data)
  {
    this(data, 4);
  }

  public JS(Appendable data, int nbOfDecimals)
  {
    this.data = data;
    this.setNumberOfDecimals(nbOfDecimals);
  }

  public final JS setContext(String context)
  {
    this.context = context;
    return this;
  }

  public final JS setNumberOfDecimals(int nbOfDecimals)
  {
    this.decimalFormat = new XmlDecimalFormat(nbOfDecimals);
    return this;
  }

  public JS writeContext(String canvasID)
  {
    return write("var ", context, "=document.getElementById('", canvasID, "').getContext('2d');\n");
  }

  public String quote(String data)
  {
    return "'" + data + "'";
  }

  public String jsColor(Color color)
  {
    return "'rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + toString(color.getAlpha() / 255f) + ")'";
  }

  public JS write(String... strings)
  {
    try
    {
      for (String string : strings)
        this.data.append(string);
      String last = strings.length > 0 ? strings[strings.length - 1] : null;
      this.lastChar = last == null || last.isEmpty() ? ' ' : last.charAt(last.length() - 1);
    } catch (IOException ex)
    {
      ex.printStackTrace();
    }
    return this;
  }

  public JS writeln(String... strings)
  {
    this.write(strings);
    return newline();
  }

  public JS newline()
  {
    return this.write(lastChar == '\n' ? "" : "\n");
  }

  public JS newline(String js)
  {
    return this.write(lastChar == '\n' ? "" : "\n", js);
  }

  public String toString(double value)
  {
    return Xml.toString(value, decimalFormat);
  }

  public JS writeFillColor(Color color)
  {
    return this.writeAss("fillStyle", this.jsColor(color));
  }

  public JS writeStrokeColor(Color color)
  {
    return this.writeAss("strokeStyle", this.jsColor(color));
  }

  public JS writeLineStyle(Stroke3 stroke)
  {
    this.writeAss("lineWidth", stroke.width());
    this.writeAss("lineCap", quote(stroke.cap()));
    this.writeAss("lineJoin", quote(stroke.join()));
    return this;
  }

  public JS writeOCDClip(OCDClip clip)
  {
    this.writeFloatFct("setTransform", 1, 0, 0, 1, 0, 0);
    this.writePath(clip.path());
    this.writeFct("clip");
    return this;
  }

  public JS writeOCDPath(OCDPath path)
  {
    this.writeFloatFct("setTransform", path.transform().floatValues());
    this.writeFillColor(path.fillColor());
    this.writeStrokeColor(path.strokeColor());
    this.writeLineStyle(path.stroke());

    boolean pathWritten = false;
    if (!path.fillColor().isTransparent())
    {
      this.writePath(path.path());
      this.writeFct("fill");
      pathWritten = true;
    }
    if (!path.fillColor().isTransparent())
      if (path.stroke().hasDash())
      {
        this.writePath(path.stroke().createStrokedShape(path.path()));
        this.writeFct("fill");
      } else
      {
        if (!pathWritten)
          this.writePath(path.path());
        this.writeFct("stroke");
      }
    return this;
  }

  public JS writePath(Shape path)
  {
    this.writeFloatFct("beginPath");
    PathIterator it = path.getPathIterator(null);
    float[] p = new float[6];
    do
    {
      Path3.Op op = Path3.Op.type(it.currentSegment(p));
      switch (op)
      {
      case MOVE:
        this.writeFloatFct("moveTo", p[0], p[1]);
        break;
      case LINE:
        this.writeFloatFct("lineTo", p[0], p[1]);
        break;
      case CUBIC:
        this.writeFloatFct("bezierCurveTo", p);
        break;
      case QUAD:
        this.writeFloatFct("quadraticCurveTo", p[0], p[1], p[2], p[3]);
        break;
      case CLOSE:
        this.writeFct("closePath");
        break;
      }
      it.next();
    } while (!it.isDone());
    return this;
  }

  public JS writeAss(String var, String value)
  {
    return write(context, ".", var, "=", value, ";\n");
  }

  public JS writeAss(String var, float value)
  {
    return write(context, ".", var, "=", toString(value), ";\n");
  }

  public JS writeFloatFct(String fct, float... values)
  {
    String[] attributes = new String[values.length];
    for (int i = 0; i < attributes.length; i++)
      attributes[i] = toString(values[i]);
    return writeFct(fct, attributes);
  }

  public JS writeFct(String fct, String... attributes)
  {
    try
    {
      data.append(context).append(".").append(fct).append("(");
      for (int i = 0; i < attributes.length; i++)
        data.append(attributes[i]).append(i < attributes.length - 1 ? "," : "");
      data.append(");\n");
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return this;
  }

  public JS writeOpeningFct(String fct, String... attributes)
  {
    try
    {
      data.append("function").append(" ").append(fct).append("(");
      for (int i = 0; i < attributes.length; i++)
        data.append(attributes[i]).append(i < attributes.length - 1 ? "," : "");
      data.append("){\n");
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return this;
  }

  public JS writeOpeningBracket()
  {
    return this.write("{");
  }

  public JS writeClosingBracket()
  {
    return this.write("}");
  }

  @Override
  public String toString()
  {
    return this.data.toString();
  }

  public static String Escape(String html)
  {
    return html.replace("<", "[[").replace(">", "]]").replace("\n", "").replace("\r", "");
  }

  public static JS Get(String script)
  {
    return new JS().write(script);
  }

  public static String AddClass(String targetID, String classNames)
  {
    return "addClassTimeout('" + targetID + "','animated " + classNames + "', 0)";
  }

  public static String AddClassTimeout(String targetID, String classNames, int animDelay)
  {
    return "addClassTimeout('" + targetID + "','animated " + classNames + "', " + animDelay + ")";
  }
}
