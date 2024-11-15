package sugarcube.common.system.time;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.Tokens;
import sugarcube.common.data.Base;
import sugarcube.common.ui.gui.Dialog3;
import sugarcube.common.data.xml.Nb;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Date3
{
    private static final int[] MIN =
            {Integer.MIN_VALUE, 1, 1, 0, 0, 0, 0};
    private static final int[] MAX =
            {Integer.MAX_VALUE, 12, 31, 23, 59, 59, 999};
    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;
    public static final int HOUR = 3;
    public static final int MINUTE = 4;
    public static final int SECOND = 5;
    public static final int MILLIS = 6;
    private int gmt = 0;
    private int[] data;// year, month, dayOfMonth, hourOfDay, minute, second,
    // millis

    public Date3()
    {
        this(new GregorianCalendar());
    }

    public Date3(long millis)
    {
        this(gregorian(millis));
    }

    public Date3(Calendar calendar)
    {
        // since month begins with 0 in Calendar
        data = Zen.Array.Ints(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
    }

    public Date3(int year, int month, int dayOfMonth)
    {
        data = Zen.Array.Ints(year, month, dayOfMonth);
    }

    public Date3(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second)
    {
        data = Zen.Array.Ints(year, month, dayOfMonth, hourOfDay, minute, second);
    }

    public Date3(int[] data, boolean copy)
    {
        this.data = copy ? Arrays.copyOf(data, data.length) : data;
    }

    public Date3(String date)
    {
        this.data = ParseAsInts(date);
    }

    public int[] copyData()
    {
        return Arrays.copyOf(data, data.length);
    }

    public int[] copyData(int indexes)
    {
        int[] copy = new int[indexes < 0 ? data.length : indexes > MILLIS ? MILLIS + 1 : indexes + 1];
        for (int i = 0; i < copy.length && i < data.length; i++)
            copy[i] = data[i];
        return copy;
    }

    public Date3 firstDayOfMonth()
    {
        return new Date3(year(), month(), 1);
    }

    public Date3 incDay()
    {
        return inc(DAY);
    }

    public Date3 decDay()
    {
        return dec(DAY);
    }

    public Date3 incYear()
    {
        return inc(YEAR);
    }

    public Date3 decYear()
    {
        return dec(YEAR);
    }

    public Date3 incYear(int value)
    {
        return inc(YEAR, value);
    }

    public Date3 decYear(int value)
    {
        return dec(YEAR, value);
    }

    public Date3 inc(int index)
    {
        return inc(index, 1);
    }

    public Date3 dec(int index)
    {
        return dec(index, 1);
    }

    public Date3 inc(int index, int value)
    {
        int[] copy = copyData(MILLIS);
        while (value-- > 0)
        {
            copy[index]++;
            for (int i = index; i >= 0; i--)
            {
                int max = i == DAY ? DaysInMonth(copy) : MAX[i];
                if (copy[i] > max)
                {
                    copy[i] = MIN[i];
                    copy[i - 1]++;
                } else
                    break;
            }
        }
        return new Date3(copy, false);
    }

    public Date3 dec(int index, int value)
    {
        int[] copy = copyData(MILLIS);
        while (value-- > 0)
        {
            copy[index]--;
            for (int i = index; i >= 0; i--)
            {
                int min = MIN[i];
                if (copy[i] < min)
                {
                    copy[i] = i == DAY ? DaysInMonth(copy) : MAX[i];
                    copy[i - 1]--;
                } else
                    break;
            }
        }
        return new Date3(copy, false);
    }

    public Date3 incMonth(int months)
    {
        int year = months / 12;
        if (year > 0)
            months = months - (year * 12);

        int m = this.month();

        if (m + months > 12)
        {
            year++;
            months = months + (m - 12);
        }
        int[] copy = copyData(MILLIS);
        if (year > 0)
            copy[YEAR] += year;
        if (months > 0)
            copy[MONTH] += months;
        return new Date3(copy, false);
    }

    public Date3 floor(int index)
    {
        int[] copy = copyData(MILLIS);
        if (index >= 0 && index <= MILLIS)
            copy[index] = MIN[index];
        return new Date3(copy, false);
    }

    public Date3 ceil(int index)
    {
        int[] copy = copyData(MILLIS);
        if (index >= 0 && index <= MILLIS)
            copy[index] = index == MONTH ? DaysInMonth(copy[YEAR], copy[MONTH]) : MAX[index];
        return new Date3(copy, false);
    }

    public static int DaysInMonth(int[] date)
    {
        return DaysInMonth(date[YEAR], date[MONTH]);
    }

    public static int DaysInMonth(int year, int month)
    {
        switch (month)
        {
            case 2:
                return IsLeap(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    public static boolean IsLeap(int year)
    {
        if (year % 4 != 0)
            return false;
        else if (year % 100 != 0)
            return true;
        else if (year % 400 == 0)
            return true;
        else
            return false;
    }

    public int year()
    {
        return data.length > YEAR ? data[YEAR] : 0;
    }

    public int month()
    {
        return data.length > MONTH ? data[MONTH] : 0;
    }

    public int day()
    {
        return data.length > DAY ? data[DAY] : 0;
    }

    public int hour()
    {
        return data.length > HOUR ? data[HOUR] : 0;
    }

    public int minute()
    {
        return data.length > MINUTE ? data[MINUTE] : 0;
    }

    public int second()
    {
        return data.length > SECOND ? data[SECOND] : 0;
    }

    public int millis()
    {
        return data.length > MILLIS ? data[MILLIS] : 0;
    }

    public long timeInMillis()
    {
        return new GregorianCalendar(year(), month() - 1, day(), hour(), minute(), second()).getTimeInMillis() + millis();
    }

    public boolean isBefore(int year, int month, int dayOfMonth)
    {
        return this.isBefore(new Date3(year, month, dayOfMonth));
    }

    public boolean isBefore(Date3 date)
    {
        for (int i = 0; i < data.length && i < date.data.length; i++)
        {
            if (data[i] < date.data[i])
                return true;
            if (data[i] > date.data[i])
                return false;
        }
        return false;
    }

    public boolean isAfter(int year, int month, int dayOfMonth)
    {
        return this.isAfter(new Date3(year, month, dayOfMonth));
    }

    public boolean isAfter(Date3 date)
    {
        for (int i = 0; i < data.length && i < date.data.length; i++)
        {
            if (data[i] > date.data[i])
                return true;
            if (data[i] < date.data[i])
                return false;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return date() + "T" + time();
    }

    public String string(boolean utc)
    {
        return utc ? UTC(new Date(this.timeInMillis())) : toString();
    }

    public String timeAndDate()
    {
        return time() + " " + date();
    }

    public static String filename(boolean withTime)
    {
        return withTime ? Format("yyyy-MM-dd_HH'h'mm", false) : Format("yyyy-MM-dd", false);
    }

    public String date()
    {
        return date("-");
    }

    public String date(String sep)
    {
        return year() + sep + BI(month()) + sep + BI(day());
    }

    public String yyyymmdd()
    {
        return date("");
    }

    public String time()
    {
        return BI(hour()) + ":" + BI(minute()) + ":" + BI(second());
    }

    public String timeWithMillis()
    {
        return BI(hour()) + ":" + BI(minute()) + ":" + BI(second()) + "." + MILLIS(millis());
    }

    public LocalDate localDate()
    {
        return LocalDate.of(this.year(), this.month(), this.day());
    }

    public Date3 copy()
    {
        return new Date3(data, true);
    }

    public static String BI(int number)
    {
        return number < 10 ? "0" + number : "" + number;
    }

    public static String MILLIS(int number)
    {
        return (number < 10 ? "00" : (number < 100 ? "0" : "")) + number;
    }

    public static boolean timize()
    {
        Date3 min = new Date3(2013, 9, 1);
        Date3 max = new Date3(2050, 9, 1);
        Date3 now = new Date3();
        // Log.debug(Zen.class,
        // ".isInTimeFrame - now="+now+", min="+min+", max="+max);
        if (now.isBefore(min))
        {
            Dialog3.showMessageDialog(null, Zen.S3_ICON, "Clock out of date", "Your computer clock is out of date, please set it up and try again...");
            System.exit(0);
            return false;
        } else if (now.isAfter(max))
        {
            Dialog3.showMessageDialog(null, Zen.S3_ICON, "License out of date", "Your license is out of date, please contact your reseller");
            System.exit(0);
            return false;
        }
        return true;
    }

    public static GregorianCalendar gregorian()
    {
        return new GregorianCalendar();
    }

    public static GregorianCalendar gregorian(long millis)
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(millis);
        return cal;
    }

    public static Date3 Get(long millis)
    {
        return new Date3(millis);
    }

    public static String Get(java.util.Date date, String sep, boolean reversed)
    {
        return Format(date, reversed ? "yyyy" + sep + "MM" + sep + "dd" : "dd" + sep + "MM" + sep + "yyyy");
    }

    public static String Get(String separator, boolean reversed)
    {
        return Get(Date(), separator, reversed);
    }

    public static String Calendar(java.util.Date date)
    {
        return Format(date, "yyyy-MM-dd");
    }

    public static String Calendar()
    {
        return Calendar(Date());
    }

    public static String UTC(java.util.Date date)
    {
        return Format(date, "yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    public static String UTC()
    {
        return UTC(Date());
    }

    public static String Sql(java.util.Date date, boolean gmt)
    {
        return Format(date, "yyyy-MM-dd HH:mm:ss", gmt);
    }

    public static String Sql()
    {
        return Sql(true);
    }

    public static String Sql(boolean gmt)
    {
        return Sql(Date(), gmt);
    }

    public static String Precise(boolean gmt)
    {
        return Format(Date(), "yyyyMMdd'z'HHmmss", gmt);
    }

    public static String Filename()
    {
        return Sql().replace(" ", "_").replace(":", "");
    }

    public static String Filename(String ext)
    {
        return Filename() + (ext.startsWith(".") ? ext : "." + ext);
    }

    public static String Raw(java.util.Date date)
    {
        return Format(date, "yyyyMMddHHmmss", true);
    }

    public static String Raw()
    {
        return Raw(Date());
    }

    public static String LocalJsonDateTime()
    {
        return Format("yyyyMMdd'-'HHmmss", false);
    }

    public static String FileDate()
    {
        return FileDate(false);
    }

    public static String FileDate(boolean gmt)
    {
        return Format("yyyy-MM-dd", gmt);
    }

    public static String FileMinutes()
    {
        return FileMinutes(false);
    }

    public static String FileMinutes(boolean gmt)
    {
        return Format("yyyy-MM-dd_HH'h'mm", gmt);
    }

    public static String FileSeconds()
    {
        return FileSeconds(false);
    }

    public static String FileSeconds(boolean gmt)
    {
        return Format("yyyy-MM-dd_HH'h'mm'm'ss's'", gmt);
    }

    public static String FileMillis()
    {
        return FileMillis(false);
    }

    public static String FileMillis(boolean gmt)
    {
        return Format("yyyy-MM-dd_HH'h'mm'm'ss's'SSS", gmt);
    }


    public static String RandFile()
    {
        return Format("yyyyMMdd-HHmmss-SSS", false) + "_" + Base.x32.random6();
    }

    public static String Time()
    {
        return Time(true);
    }

    public static String Time(boolean gmt)
    {
        return Format("HH:mm:ss", gmt);
    }

    public static String TimeLocal()
    {
        return Time(false);
    }

    public static String TimeGMT()
    {
        return Time(true);
    }

    public static String Format(String format)
    {
        return Format(format, true);
    }

    public static String Format(String format, boolean gmt)
    {
        return Format(Date(), format, gmt);
    }

    public static String Format(java.util.Date date, String format)
    {
        return Format(date, format, true);
    }

    public static String Format(java.util.Date date, String format, boolean gmt)
    {
        // never change 'Z', its UTC (GMT)
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (gmt)
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static Date3 Get(LocalDate date)
    {
        return new Date3(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static Date3 Now()
    {
        return new Date3();
    }

    public static Date Date()
    {
        return new java.util.Date(System.currentTimeMillis());
    }

    public static int Month(String month)
    {

        if (Str.IsVoid(month))
            return 0;

        if (Str.IsDigits(month))
            return Nb.Int(month);

        month = Str.UnAccent(month.trim()).toLowerCase();
        String tri = month.length() > 3 ? month.substring(0, 3) : month;
        switch (tri)
        {
            case "jan":
            case "gen":
            case "ene":
                return 1;
            case "fev":
            case "feb":
                return 2;
            case "mar":
                return 3;
            case "avr":
            case "apr":
            case "abr":
                return 4;
            case "mai":
            case "may":
            case "mag":
                return 5;
            case "jun":
            case "giu":
                return 6;
            case "jui":
                return month.startsWith("juin") ? 6 : 7;
            case "jul":
            case "lug":
                return 7;
            case "aou":
            case "aug":
            case "ago":
                return 8;
            case "sep":
            case "set":
                return 9;
            case "oct":
            case "okt":
            case "ott":
                return 10;
            case "nov":
                return 11;
            case "dec":
            case "dez":
            case "dic":
                return 12;
        }
        return 0;
    }

    public static String MonthString(String month)
    {
        int m = Month(month);
        return m < 10 ? "0" + m : "" + m;
    }

    public static boolean CheckDotDDMMYYYY(String date)
    {
        return CheckDDMMYYYY(date, Tokens.SPLIT_DOT);
    }

    public static boolean CheckDDMMYYYY(String date, String sepRegex)
    {
        String[] tk = date.split(sepRegex);

        if (tk.length != 3 || tk[0].length() != 2 || tk[1].length() != 2 || tk[2].length() != 4)
            return false;

        if (!Nb.Ranges(tk[0], 1, 31))
            return false;

        if (!Nb.Ranges(tk[1], 1, 12))
            return false;

        if (!Nb.Ranges(tk[2], 1800, 2100))
            return false;

        return true;
    }

    public static boolean CheckDotYYYYMMDD(String date)
    {
        return CheckYYYYMMDD(date, Tokens.SPLIT_DOT);
    }

    public static boolean CheckYYYYMMDD(String date, String sepRegex)
    {
        String[] tk = date.split(sepRegex);

        if (tk.length != 3 || tk[0].length() != 4 || tk[1].length() != 2 || tk[2].length() != 4)
            return false;

        if (!Nb.Ranges(tk[2], 1, 31))
            return false;

        if (!Nb.Ranges(tk[1], 1, 12))
            return false;

        if (!Nb.Ranges(tk[0], 1800, 2100))
            return false;

        return true;
    }

    public static int[] ParseAsInts(String date)
    {
        int[] data;
        date = date.trim();
        int t = date.indexOf('T');
        if (t < 0)
            t = date.indexOf(' ');
        if (t > 0)
        {
            data = new int[6];
            String time = date.substring(t + 1).trim();
            String[] tk = time.split(":");
            if (tk.length > 0)
                data[HOUR] = Nb.Int(tk[0], 12);
            if (tk.length > 1)
                data[MINUTE] = Nb.Int(tk[1], 0);
            if (tk.length > 2)
                data[SECOND] = Nb.Int(true, tk[2], 0);

            date = date.substring(0, t).trim();
        } else
            data = new int[3];

        String[] tk = date.split("-");
        if (tk.length > 0)
            data[YEAR] = Nb.Int(tk[0], 2000);
        if (tk.length > 1)
            data[MONTH] = Nb.Int(tk[1], 1);
        if (tk.length > 2)
            data[DAY] = Nb.Int(tk[2], 1);

        return data;
    }

    public static Date3 Parse(String date)
    {
        return new Date3(ParseAsInts(date), false);
    }

    public static void main(String... args)
    {
        Log.debug(Date3.class, " - time=" + Date3.Precise(true));

    }
}
