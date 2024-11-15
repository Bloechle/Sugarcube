package sugarcube.formats.pdf.writer.document;

import sugarcube.common.system.util.Sys;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PDFUtil
{
    private static final DecimalFormat FORMAT = new DecimalFormat("#.######", new DecimalFormatSymbols(Locale.US));

    static
    {
        FORMAT.setGroupingUsed(false);
    }

    public static String Print(double value)
    {
        return FORMAT.format(value);
    }

    public static String Print(float value)
    {
        return FORMAT.format(value);
    }

    public static void main(String... args)
    {
        Sys.Println(Print(0));
        Sys.Println(Print(0.0f));
        Sys.Println(Print((float) 0.0));
        Sys.Println(Print(1.23223232332f));
        Sys.Println(Print(211234231.412423241f));
        Sys.Println(Print(-0.234f));
    }
}
