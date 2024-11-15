package sugarcube.common.system.log;

import sugarcube.common.system.log.Logger.Level;
import sugarcube.common.data.collections.Props;
import sugarcube.common.data.Base;
import sugarcube.common.interfaces.Loggable;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.time.Date3;

public class Log implements Unjammable
{
    public static String KEY_LEVEL = "log_level";

    public static final String ID = Base.x32.random12();
    public static Loggable LOG = new Logger("sugarcube");
    public static Logger.Level LEVEL = Level.DEBUG;

    public static void setLevel(String level)
    {
        if (level != null && !level.trim().isEmpty())
            setLevel(Level.instance(level));
    }

    public static void setLevel(Logger.Level level)
    {
        LEVEL = level;
    }

    public static void setLevel(Props props)
    {
        setLevel(props.get(KEY_LEVEL, null));
    }

    public static void setLogger(Loggable loggable)
    {
        LOG = loggable;
    }

    public static Object ungc(Object source)
    {
        return source;
    }

    public static void log(Object source, Object message, Level level)
    {
        if (LOG != null && level.isHigherOrEqual(LEVEL))
        {
            LOG.log(source, message, level);
        }
    }


    public static void Stacktrace(Object source, Object message)
    {
        log(source, message, Level.DEBUG);
        try
        {
            System.out.println("\n");
            throw new Exception("Log.stacktrace - " + message);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msg(Object src, Object msg, Boolean happy)
    {
        if (happy == null)
            Log.debug(src, msg);
        else if (happy)
            Log.info(src, msg);
        else
            Log.warn(src, msg);
    }

    public static void debug(boolean condition, Object source, Object message)
    {
        if (condition)
            log(source, message, Level.DEBUG);
    }

    public static void debug(Object source, Object message)
    {
        log(source, message, Level.DEBUG);
    }

    public static void hello(Object source)
    {
        log(source, " - <HELLO> at " + (new Date3()), Level.DEBUG);
    }

    public static void info(boolean condition, Object source, Object message)
    {
        if (condition)
            log(source, message, Level.INFO);
    }

    public static void info(Object source, Object message)
    {
        log(source, message, Level.INFO);
    }

    public static void warn(boolean condition, Object source, Object message)
    {
        if (condition)
            log(source, message, Level.WARNING);
    }

    public static void warn(Object source, Object message)
    {
        log(source, message, Level.WARNING);
    }

    public static void warn(Object source, Object message, Exception e)
    {
        log(source, message + " - stacktrace=" + Logger.StackTrace(e), Level.WARNING);
    }

    public static void warn(boolean condition, Object source, Object message, Exception e)
    {
        if (condition)
            log(source, message + " - stacktrace=" + Logger.StackTrace(e), Level.WARNING);
    }

    public static void error(boolean condition, Object source, Object message)
    {
        if (condition)
            log(source, message, Level.ERROR);
    }

    public static void error(Object source, Object message)
    {
        log(source, message, Level.ERROR);
    }

    public static void error(Object source, Object message, Exception e)
    {
        log(source, message + "\nSTACK[" + Logger.PrintStackTrace(e) + "]", Level.ERROR);
    }

}
