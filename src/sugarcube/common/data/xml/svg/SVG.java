package sugarcube.common.data.xml.svg;

import sugarcube.common.data.Zen;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlDecimalFormat;

import java.awt.*;

public class SVG
{
  public static final String EXT = ".svg";
  public static final String EXTZ = ".svgz";
  public static final XmlDecimalFormat DECIMAL_FORMAT;
  public static final String SEPARATOR = " "; // SVG allows "," and " " (or
                                              // both)

  static
  {
    DECIMAL_FORMAT = new XmlDecimalFormat(3);
  }

  public static String toString(double value)
  {
    return Xml.toString(value, DECIMAL_FORMAT);
  }

  public static String toString(float... data)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < data.length; i++)
      sb.append(toString(data[i])).append((i < data.length - 1 ? SEPARATOR : ""));
    return sb.toString();
  }

  public static String toString(Color color)
  {
    StringBuilder sb = new StringBuilder("#");
    for (int value : Zen.Array.Ints(color.getRed(), color.getGreen(), color.getBlue()))
      sb.append((value &= 0xff) < 0x10 ? "0" + Integer.toHexString(value) : Integer.toHexString(value));
    return sb.toString();
  }

  public static String cssImportInstruction(String filePath)
  {
    return "<?xml-stylesheet type=\"text/css\" href=\"" + filePath + "\" ?>";
  }
}
