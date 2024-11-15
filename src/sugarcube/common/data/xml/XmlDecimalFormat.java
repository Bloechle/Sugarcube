package sugarcube.common.data.xml;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class XmlDecimalFormat
{
  //default precision 
  public static XmlDecimalFormat DEFAULT = new XmlDecimalFormat(4);

  private DecimalFormatSymbols dfs;
  private DecimalFormat df;
  private int nbOfDecimals;
  private int nbOfSignificantDigits;

  public XmlDecimalFormat(int nbOfDecimals)
  {
    this(nbOfDecimals, nbOfDecimals);
  }

  public XmlDecimalFormat(int nbOfDecimals, int nbOfSignificantDigits)
  {
    this.nbOfDecimals = nbOfDecimals;
    this.nbOfSignificantDigits = nbOfSignificantDigits;
    this.dfs = new DecimalFormatSymbols();
    dfs.setDecimalSeparator('.');
    dfs.setExponentSeparator("E");
    df = decimalFormat(nbOfDecimals);
  }

  public DecimalFormat decimalFormat(int nbOfDecimals)
  {
    DecimalFormat df = null;
    if (nbOfDecimals > -1)
    {
      String sharps = "";
      for (int i = 0; i < nbOfDecimals; i++)
        sharps += "#";
      df = new DecimalFormat("." + sharps, dfs);
      df.setGroupingUsed(false);
    }
    return df;
  }

  public int significant(double v)
  {
    int f = v < 0 ? -1 : 1;
    for (int i = 0; i < nbOfDecimals; i++)
      f *= 10;
    int nb = (int) Math.round(v * f);
    return nb == 0 ? 0 : nb < 10 ? 1 : nb < 100 ? 2 : nb < 1000 ? 3 : nb < 10000 ? 4 : nb < 100000 ? 5 : ("" + nb).length();
  }

  public String format(double v)
  {
    if (nbOfDecimals == 0)
      return Long.toString(Math.round(v));
    else if (v == 0.0)
      return "0";
    else if (Double.isNaN(v))
      return "-";

    int delta = significant(v) - nbOfSignificantDigits;
    String s = delta < 0 ? decimalFormat(nbOfDecimals + delta).format(v) : df.format(v);
    return s.equals(".0") || s.equals("-.0") ? "0" : s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
  }

  public static XmlDecimalFormat Need(XmlDecimalFormat format)
  {
    return format == null ? DEFAULT : format;
  }
}
