package sugarcube.common.system.log;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.interfaces.Loggable;
import sugarcube.common.system.time.DateUtils;
import sugarcube.common.system.time.Date3;
import sugarcube.common.data.xml.CharRef;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

public class Logger implements Loggable
{
    public enum Level
    {
        ERROR(4),
        WARNING(3),
        INFO(2),
        DEBUG(1);
        public int value;

        Level(int value)
        {
            this.value = value;
        }

        public int intValue()
        {
            return value;
        }

        public boolean is(Level level)
        {
            return this.value == level.value;
        }

        public boolean isHigherOrEqual(Level level)
        {
            return this.value >= level.value;
        }

        public static Level instance(String level)
        {
            if (level == null)
                return null;
            switch (level.trim().toLowerCase())
            {
                case "debug":
                    return DEBUG;
                case "config":
                case "info":
                    return INFO;
                case "warning":
                case "warn":
                    return WARNING;
                case "severe":
                case "error":
                    return ERROR;
                default:
                    return INFO;
            }
        }

        public static Level[] reverse()
        {
            Level[] levels = values();
            for (int i = 0; i < levels.length / 2; i++)
            {
                Level tmp = levels[i];
                levels[i] = levels[levels.length - i - 1];
                levels[levels.length - i - 1] = tmp;
            }
            return levels;
        }
    }

    // BIG_BROTHERS do listen to everything logged everywhere...
    private static Set3<Loggable> BIG_BROTHERS = new Set3<>();
    public static Appendable DEFAULT_OUT = System.out;
    public static int ID = 0;
    private String name;
    private Appendable out = DEFAULT_OUT;
    private Set3<Loggable> loggables = new Set3<>();

    public Logger(String name)
    {
        this.name = name;
    }

    public Logger(Class objectClass)
    {
        this.name = objectClass.getName();
    }

    public Logger(String name, Appendable out)
    {
        this.name = name;
        this.out = out;
    }

    public Logger out(String msg)
    {
        try
        {
            if (out != null)
                out.append(msg);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return this;
    }

    public static String toString(int id, Object source, Object message, Level level)
    {
        return "\n" + levelColor(level) + " " + (source == null ? "" : sourcize(source)) + message;
    }

    public static String levelColor(Level level)
    {

        switch (level)
        {
            case DEBUG:
                return "🟦";
            case INFO:
                return "🟩";
            case WARNING:
                return "🟧";
            case ERROR:
                return "🟥";

        }
        return " ";
    }

    public static String logToString(int id, Object source, Object message, Level level)
    {
        return toString(id, source, message, level);
    }

    public static String sourcize(Object source)
    {
        String src = source == null ? ""
                : source instanceof String ? (String) source : (source instanceof Class ? ((Class) source) : source.getClass()).getSimpleName();
        return src;
    }

    @Override
    public Logger log(Object source, Object message, Level level)
    {
        try
        {
            ID++;
            String msg = logToString(ID, source, message, level);
            if (out != null)
                this.out(msg);
            for (Loggable listener : loggables)
                listener.log(source, message, level);
            if (BIG_BROTHERS != null && !BIG_BROTHERS.contains(this))
                for (Loggable listener : BIG_BROTHERS)
                    listener.log(source, message, level);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public Logger bigBrotherize()
    {
        BIG_BROTHERS.add(this);
        return this;
    }

    public static void AddBigBrother(Loggable... listeners)
    {
        BIG_BROTHERS.addAll(listeners);
    }

    public static void RemoveBigBrother(Loggable... listeners)
    {
        BIG_BROTHERS.removeAll(listeners);
    }

    public void addListener(Loggable... listeners)
    {
        this.loggables.addAll(listeners);
    }

    public void removeListener(Loggable... listeners)
    {
        this.loggables.removeAll(listeners);
    }

    public void dispose()
    {
        if (out != null)
            out = null;
    }

    public Appendable out()
    {
        return this.out;
    }

    public Logger setOut(Appendable out)
    {
        this.out = out;
        return this;
    }

    public String name()
    {
        return name;
    }

    public Logger debug(boolean condition, Object source, Object message)
    {
        return condition ? debug(source, message) : this;
    }

    public Logger debug(Object source, Object message)
    {
        return log(source, message, Level.DEBUG);
    }

    public Logger info(Object source, Object message)
    {
        return log(source, message, Level.INFO);
    }

    public Logger warn(Object source, Object message)
    {
        return log(source, message, Level.WARNING);
    }

    public Logger warn(Object source, Object message, Exception e)
    {
        return log(source, message + " - stacktrace=" + StackTrace(e), Level.WARNING);
    }

    public Logger error(Object source, Object message)
    {
        return log(source, message, Level.ERROR);
    }


    public static String StackTrace(Exception e)
    {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace())
            sb.append("\n").append(element.getClassName()).append(".").append(element.getMethodName());
        return sb.toString();
    }

    public static String PrintMethodTrace(Exception e)
    {
        String[] tks = PrintStackTrace(e).split("\n");
        return tks.length > 1 ? tks[0] + tks[1] : tks[0];
    }

    public static String PrintStackTrace(Exception e)
    {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static String PrintStackTrace(Exception e, int nbOfLines)
    {
        String trace = PrintStackTrace(e);
        int index = 0;
        while (index > -1 && nbOfLines > 0)
        {
            index = trace.indexOf('\n', index);
            nbOfLines--;
        }
        return index > 0 ? trace.substring(0, index) : trace;
    }

    public static class LogRecord
    {
        public static final int AVG_SIZE = 300;
        public int id;
        public Logger.Level level;
        public String time;
        public String source;
        public String message;

        public LogRecord(int logID, Object src, Object msg, Logger.Level level)
        {
            this.id = logID;
            this.level = level;
            this.time = Date3.Sql();
            this.source = src == null ? ""
                    : src instanceof String ? (String) src : (src instanceof Class ? ((Class) src) : src.getClass()).getName();
            this.message = msg.toString();

        }

        public String classname()
        {
            int i = source.lastIndexOf(".");
            if (i < 0)
                i = source.lastIndexOf("$");
            return i < 0 ? source : source.substring(i + 1);
        }

        @Override
        public String toString()
        {
            return toHTML(id);
        }

        public String toString(int logID)
        {
            StringBuilder text = new StringBuilder(AVG_SIZE);
            text.append(logID).append(". ").append(level).append(" ");
            text.append(source == null ? "" : classname());
            text.append(message);
            return text.toString();
        }

        public String toHTML(int logID)
        {
            StringBuilder html = new StringBuilder(AVG_SIZE);
            String levelName = level.name().toLowerCase();
            html.append("<p class=\"p-").append(levelName).append("\">");
            html.append("<span class=\"l l-").append(levelName).append("\">");
            html.append(logID).append(". ").append(level);
            html.append("</span> ");
            html.append(source == null ? "" : classname());
            html.append(CharRef.Escape(message).replaceAll("\n", "<br/>&nbsp;"));
            html.append("</p>\n");
            return html.toString();
        }
    }

    public static class Recorder implements Iterable<LogRecord>
    {
        public transient int recordID = 0;
        public int maxSize = -1;
        public Level minLevel = Level.DEBUG;
        public List3<LogRecord> records = new List3<LogRecord>();

        public Recorder()
        {
        }

        public Recorder(int maxSize)
        {
            this.maxSize = maxSize;
        }

        public Recorder level(Level level)
        {
            this.minLevel = level;
            return this;
        }

        public static String CSS()
        {
            return "" + "body{\n" + "padding: 0px 0px 0px 0px;\n" + "margin: 0px 0px 0px 0px;\n" + "}\n" + "p {\n"
                    + "font-size:10pt;\nfont-family: monospace;\n" + "margin: 0px 0px 0px 0px;\n" + "padding: 1px 8px 1px 8px;\n"
                    + "border-width: 0px 0px 1px 0px;\n" + "border-style: dotted;\n" + "border-color: #DDDDDD;}\n" + ".l {\n" + "color:#000000;\n"
                    + "font-weight:bold;}\n" + ".p-trace {\n" + "background-color:#AAAAAA;}\n" + ".p-debug {\n" + "background-color:#FFFFAA;}\n"
                    + ".p-config {\n" + "background-color:#BBBBFF;}\n" + ".p-info {\n" + "background-color:#BBDDBB;}\n" + ".p-warning {\n"
                    + "background-color:#FFDBAA;}\n" + ".p-error {\n" + "background-color:#FFAAAA;}\n";
        }

        public synchronized String htmlLog(String title, boolean reverse)
        {
            return htmlLog(title, Level.DEBUG, -1, CSS(), reverse);
        }

        public synchronized String htmlLog(String title, Level minLevel, int maxSize, boolean reverse)
        {
            return htmlLog(title, minLevel, maxSize, CSS(), reverse);
        }

        public synchronized String htmlLog(String title, Level minLevel, int maxSize, String css, boolean reverse)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<style type=\"text/css\">\n");
            sb.append(css == null || css.isEmpty() ? CSS() : css);
            sb.append("</style>\n");
            sb.append("<title>").append(title).append("</title>\n");
            sb.append("<script language=\"javascript\" type=\"text/javascript\">\n");
            sb.append("function toBottom(){\n");
            sb.append("  window.scrollTo(0, document.body.scrollHeight);\n");
            sb.append("}\n");
            sb.append("</script>\n");
            sb.append("</head>\n");
            sb.append("<body onload=\"toBottom()\">\n");
            sb.append("<p class=\"p-config\"><br/>Sugarcube's Logger Refreshed at " + DateUtils.timeAndDate() + "<br/></p>\n");
            try
            {
                int counter = 0;
                if (this.records != null && this.records.size() > 0)
                {
                    Iterator<LogRecord> it = reverse ? records.descendingIterator() : records.iterator();
                    if (it != null)
                        while (it.hasNext())
                        {
                            LogRecord record = it.next();
                            if (record.level.isHigherOrEqual(minLevel))
                            {
                                sb.append(record);
                                if (maxSize > 0 && ++counter >= maxSize)
                                    break;
                            }
                        }
                }
            } catch (Exception e)
            {
                sb.append("Logger exception: " + e.getMessage());
                e.printStackTrace();
            }
            sb.append("</body>\n");
            sb.append("</html>\n");
            return sb.toString();
        }

        public synchronized void clear()
        {
            this.records = new List3<LogRecord>();
        }

        public synchronized void reset()
        {
            this.clear();
            this.recordID = 0;
        }

        public synchronized void add(Object source, Object message, Logger.Level level)
        {
            this.add(new LogRecord(recordID++, source, message, level));
        }

        public synchronized void add(int logID, Object source, Object message, Logger.Level level)
        {
            this.add(new LogRecord(logID, source, message, level));
        }

        public synchronized void add(LogRecord record)
        {
            if (record.level.isHigherOrEqual(minLevel))
            {
                this.records.add(record);
                while (maxSize > 0 && this.records.size() > maxSize)
                    this.records.remove(0);
            }
        }

        public synchronized int size()
        {
            return records.size();
        }

        @Override
        public synchronized Iterator<LogRecord> iterator()
        {
            return this.records.iterator();
        }
    }
}
