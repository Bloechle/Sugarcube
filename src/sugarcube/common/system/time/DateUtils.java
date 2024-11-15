package sugarcube.common.system.time;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils
{
    public static long timeMillis()
    {
        return System.currentTimeMillis();
    }

    public static long timeMillis(int year, int month, int day)
    {
        return new GregorianCalendar(year, month, day).getTimeInMillis();
    }

    public static long timeSeconds(int year, int month, int day)
    {
        return timeMillis(year, month, day) / 1000;
    }

    public static long timeSeconds1904(int year, int month, int day)
    {
        return timeSeconds(year, month, day) - timeSeconds(1904, 01, 01);
    }

    public static GregorianCalendar calendar()
    {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(timeMillis());
        return gc;
    }

    public static int year()
    {
        return calendar().get(GregorianCalendar.YEAR);
    }

    public static int month()
    {
        return calendar().get(GregorianCalendar.MONTH);
    }

    public static int hour()
    {
        return calendar().get(GregorianCalendar.HOUR_OF_DAY);
    }

    public static int hour12()
    {
        return calendar().get(GregorianCalendar.HOUR);
    }

    public static int minute()
    {
        return calendar().get(GregorianCalendar.MINUTE);
    }

    public static int second()
    {
        return calendar().get(GregorianCalendar.SECOND);
    }

    public static String date(long millis, String separator)
    {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(millis);
        return get(GregorianCalendar.YEAR, gc) + separator + get(GregorianCalendar.MONTH, gc) + separator + get(GregorianCalendar.DAY_OF_MONTH, gc);
    }

    public static String date(String separator)
    {
        return date(System.currentTimeMillis(), separator);
    }

    public static String date(long millis)
    {
        return date(millis, ".");
    }

    public static String date()
    {
        return date(".");
    }

    public static String time(long millis, String separator)
    {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(millis);
        return get(GregorianCalendar.HOUR_OF_DAY, gc) + separator + get(GregorianCalendar.MINUTE, gc) + separator + get(GregorianCalendar.SECOND, gc);
    }

    public static String time(String separator)
    {
        return time(System.currentTimeMillis(), separator);
    }

    public static String time(long millis)
    {
        return time(millis, ":");
    }

    public static String time()
    {
        return time(":");
    }

    public static String timeAndDate(long millis)
    {
        return date(millis) + "/" + time(millis);
    }

    public static String timeAndDate()
    {
        return date() + "/" + time();
    }

    public static String universalTime(java.util.Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");// never
        // change
        // 'Z',
        // its
        // UTC
        // (GMT)
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    public static String universalTime()
    {
        return universalTime(new java.util.Date(System.currentTimeMillis()));
    }

    private static String get(int field, GregorianCalendar calendar)
    {
        int value = calendar.get(field);
        return value < 10 ? "0" + value : "" + value;
    }
}
