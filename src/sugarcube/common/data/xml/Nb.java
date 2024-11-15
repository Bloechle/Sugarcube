package sugarcube.common.data.xml;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Nb
{

    public static final double TWO_THIRDS = 2.0 / 3.0;

    public static final DecimalFormatSymbols DFS = new DecimalFormatSymbols(Locale.ENGLISH);

    static
    {
        DFS.setDecimalSeparator('.');
        DFS.setExponentSeparator("E");
    }

    public static int Max(int... values)
    {
        int max = values.length == 0 ? 0 : values[0];
        for (int i = 1; i < values.length; i++)
            if (values[i] > max)
                max = values[i];
        return max;
    }

    public static int Min(int... values)
    {
        int min = values.length == 0 ? 0 : values[0];
        for (int i = 1; i < values.length; i++)
            if (values[i] < min)
                min = values[i];
        return min;
    }

    public static int compare(double v1, double v2, boolean ascendent)
    {
        return v1 < v2 ? (ascendent ? -1 : 1) : v1 > v2 ? (ascendent ? 1 : -1) : 0;
    }

    public static int minIndex(int[] data)
    {
        if (data.length == 0)
            return -1;
        int iMin = 0;
        int min = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[iMin = i];
        return iMin;
    }

    public static int minIndex(float[] data)
    {
        if (data.length == 0)
            return -1;
        int iMin = 0;
        float min = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[iMin = i];
        return iMin;
    }

    public static int minIndex(double... data)
    {
        if (data.length == 0)
            return -1;
        int iMin = 0;
        double min = data[0];
        for (int i = 1; i < data.length; i++)
            if (data[i] < min)
                min = data[iMin = i];
        return iMin;
    }

    public static boolean isInteger(String data)
    {
        for (int i = 0; i < data.length(); i++)
        {
            char c = data.charAt(i);
            if (c != '-' && c < '0' || c > '9')
                return false;
        }
        return true;
    }

    public static boolean isReal(String data)
    {
        if (data.equalsIgnoreCase("NaN"))
            return true;
        char c;
        for (int i = 0; i < data.length(); i++)
        {
            if ((c = data.charAt(i)) < '-' || c > '9')
                return false;
            if (c == '/')
                return false;
        }
        return true;
    }

    public static String[] toStrings(String data)
    {
        return data.split("\\s+");
    }

    public static double[] toDoubles(String data)
    {
        String[] v = toStrings(data);
        double[] d = new double[v.length];
        for (int i = 0; i < d.length; i++)
            d[i] = Double.parseDouble(v[i].contains(",") ? v[i].replaceAll(",", ".") : v[i]);
        return d;
    }

    public static boolean Bool(String value)
    {
        return Bool(value, false);
    }

    public static boolean Bool(String value, boolean def)
    {
        if (value == null || (value = value.trim().toLowerCase()).isEmpty())
            return def;
        else
            try
            {
                switch (value)
                {
                    case "1":
                    case "ok":
                    case "on":
                    case "true":
                    case "enabled":
                        return true;
                    case "0":
                    case "ko":
                    case "off":
                    case "false":
                    case "disabled":
                        return false;
                }
                return Boolean.parseBoolean(value);
            } catch (NumberFormatException e)
            {
                Log.warn(Nb.class, ".toBoolean - string parsing exception: " + e);
                return def;
            }
    }

    public static int Int(String value)
    {
        return Int(value, 0);
    }

    public static int[] Ints(String value, int... splits)
    {
        int[] vals = new int[splits.length];
        int lastSplit = 0;
        for (int i = 0; i < vals.length; i++)
            vals[i] = Int(value.substring(lastSplit, lastSplit = splits[i]));
        return vals;
    }

    public static int[] Ints(String value, String sep)
    {
        String[] s = Str.Split(value, sep);
        int[] a = new int[s.length];
        for (int i = 0; i < s.length; i++)
            a[i] = Int(s[i], 0);
        return a;
    }

    public static int Int(String value, int def)
    {
        return Int(value, (Integer) def);
    }

    public static Integer Int(String value, Integer def)
    {
        return (Integer) (int) Double(value, def);
    }

    public static int Int(boolean seek, String value)
    {
        return Int(seek, value, 0);
    }

    public static int Int(boolean seek, String value, int def)
    {
        if (value == null)
            return def;
        if (!seek)
            return Int(value, def);
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray())
            if (c >= '0' && c <= '9' || c == '-')
                sb.append(c);
        return Int(sb.toString().trim(), def);
    }

    public static float Float(String value)
    {
        return Float(value, 0);
    }

    public static float Float(String value, double def)
    {
        return (float) Double(value, def);
    }

    public static float[] toFloats(String data)
    {
        String[] v = toStrings(data);
        float[] d = new float[v.length];
        for (int i = 0; i < d.length; i++)
            d[i] = Float(v[i]);
        return d;
    }

    public static float[] toFloats(String data, float... def)
    {
        String[] v = toStrings(data);
        float[] d = new float[v.length];
        for (int i = 0; i < d.length; i++)
            d[i] = Float(v[i], i < def.length ? def[i] : 0);
        return d;
    }

    public static double toDouble(boolean seek, String value)
    {
        return toDouble(seek, value, 0);
    }

    public static double toDouble(boolean seek, String value, double def)
    {
        if (!seek)
            return Double(value, def);
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray())
            if (c >= '0' && c <= '9' || c == '-' || c == '.' || c == ',')
                sb.append(c);
        return Double(sb.toString().trim(), def);
    }

    public static double toDouble(String value)
    {
        return Double(value, 0);
    }

    public static double Double(String value, double def)
    {
        if (value == null || value.isEmpty())
            return def;
        else
            try
            {
                return Double.parseDouble(value.contains(",") ? value.replaceAll(",", ".").trim() : value.trim());
            } catch (NumberFormatException e)
            {
                Log.warn(Nb.class, ".toDouble - string parsing exception: " + value);
//         e.printStackTrace();
                return def;
            }
    }

    public static String toString(float[] data, int decimals)
    {
        if (data == null)
            return "null";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
            sb.append(Float.isNaN(data[i]) ? "NaN" : String(data[i], decimals)).append(i < data.length - 1 ? " " : "");
        return sb.toString();
    }

    public static String S0(double d)
    {
        return String(d, 0);
    }

    public static String S1(double d)
    {
        return String(d, 1);
    }

    public static String S2(double d)
    {
        return String(d, 2);
    }

    public static String S3(double d)
    {
        return String(d, 3);
    }

    public static String S4(double d)
    {
        return String(d, 4);
    }

    public static String String(double d, int decimals)
    {
        if (d == 0.0)
            return "0";
        else if (Double.isNaN(d))
            return "-0";
        else if (decimals < 0)
            return Double.toString(d);
        else if (decimals == 0)
            return Long.toString(Math.round(d));
        String s = DF(decimals).format(d);
        return s.equals("0.0") || s.equals("-0.0") ? "0" : s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    public static DecimalFormat DF(int decimals)
    {
        DecimalFormat df = new DecimalFormat("#." + Sharps(decimals), DFS);
        df.setGroupingUsed(false);
        return df;
    }

    public static String Sharps(int size)
    {
        switch (size)
        {
            case 0:
                return "";
            case 1:
                return "#";
            case 2:
                return "##";
            case 3:
                return "###";
            case 4:
                return "####";
            case 5:
                return "#####";
            case 6:
                return "######";
            default:
                String sharps = "";
                for (int i = 0; i < size; i++)
                    sharps += "#";
                return sharps;
        }
    }

//    public static String toPrice(double d, int decimals)
//    {
//        String p = toString(d, decimals);
//        if (p.startsWith("."))
//            p = "0" + p;
//        else if (p.startsWith("-."))
//            p = "-0" + p;
//
//        int i = p.indexOf(".");
//
//        if (i < 0)
//        {
//            p += ".";
//            i = p.length() - 1;
//        }
//
//        if (i > 0)
//            while (p.length() <= i + decimals)
//                p += "0";
//
//        return p;
//    }

    public static String Fix3(int v)
    {
        return Fix(v, 3);
    }

    public static String Fix(int v, int length)
    {
        String s = "" + v;
        while (s.length() < length)
            s = "0" + s;
        return s;
    }

    public static String id(int v, int length)
    {
        return Fix(v, length);
    }

    public static double round(double value, int decimals)
    {
        return Math.round(value * (decimals = (decimals <= 0 ? 1 : (int) Math.round(Math.pow(10, decimals))))) / (double) decimals;
    }

    public static boolean between(double value, double min, double max)
    {
        return value >= min && value <= max;
    }

    public static String HexString(byte data)
    {
        String s = Integer.toHexString(data);
        int size = s.length();
        switch (size)
        {
            case 0:
                return "00";
            case 1:
                return "0" + s;
            case 2:
                return s;
            default:
                return s.substring(size - 2, size);
        }
    }

    public static int Random(int size)
    {
        return (int) (Math.random() * size);
    }

    public static int[] Ints(int... values)
    {
        return values;
    }

    public static long Sign(long v, boolean positif)
    {
        return v >= 0 == positif ? v : -v;
    }

    public static double Clamp(double value, double min, double max)
    {
        return value < min ? min : (value > max ? max : value);
    }

    public static float Clamp(float value, float min, float max)
    {
        return value < min ? min : (value > max ? max : value);
    }

    public static int Clamp(int value, int min, int max)
    {
        return value < min ? min : (value > max ? max : value);
    }

    public static int ClampIndex(int index, int size)
    {
        return index < 0 ? 0 : (index > size - 1 ? size - 1 : index);
    }

    public static boolean Ranges(int nb, int min, int max)
    {
        return nb >= min && nb <= max;
    }

    public static boolean Ranges(String nb, int min, int max)
    {
        return Ranges(Nb.Int(nb, min - 1), min, max);
    }

    public static int ZeroFloor(int value)
    {
        return value < 0 ? 0 : value;
    }

    public static boolean IsPair(int value)
    {
        return value % 2 == 0;
    }

    public static boolean IsOdd(int value)
    {
        return value % 2 == 1;
    }
}
