package sugarcube.common.data.collections;

import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.IO;

import java.io.File;
import java.util.Comparator;

public class Stringer
{
    public static final char DEFAULT_SEP = ';';
    public static final char DEFAULT_QUOT = '\"';
    public final StringBuilder text;
    public String sep = DEFAULT_SEP + "";
    public String quot = DEFAULT_QUOT + "";

    public Stringer()
    {
        this.text = new StringBuilder();
    }

    public Stringer(String text)
    {
        this.text = new StringBuilder(text);
    }

    public Stringer(int capacity)
    {
        this.text = new StringBuilder(capacity);
    }

    public Stringer(CharSequence str)
    {
        this.text = new StringBuilder(str);
    }

    public Stringer clear()
    {
        this.text.delete(0, text.length());
        return this;
    }

    public int length()
    {
        return this.text.length();
    }

    public boolean isEmpty()
    {
        return this.text.length() == 0;
    }

    public boolean isPopulated()
    {
        return this.text.length() > 0;
    }

    public boolean startsWith(char c)
    {
        return text.length() > 0 && text.charAt(0) == c;
    }

    public boolean endsWith(char c)
    {
        return text.length() > 0 && text.charAt(text.length() - 1) == c;
    }

    public boolean startsWith(String s)
    {
        int full = text.length();
        int size = s.length();
        if (full < size)
            return false;
        for (int i = 0; i < size; i++)
            if (text.charAt(i) != s.charAt(i))
                return false;
        return true;
    }

    public boolean endsWith(String s)
    {
        int full = text.length();
        int size = s.length();
        if (full < size)
            return false;
        for (int i = 0; i < size; i++)
            if (text.charAt(full - size + i) != s.charAt(i))
                return false;
        return true;
    }

    public Stringer uppercase(Object... strings)
    {
        for (Object str : strings)
        {
            String s = str.toString();
            for (int i = 0; i < s.length(); i++)
                this.text.append(Character.toUpperCase(s.charAt(i)));
        }
        return this;
    }

    public Stringer lowercase(Object... strings)
    {
        for (Object str : strings)
        {
            String s = str.toString();
            for (int i = 0; i < s.length(); i++)
                this.text.append(Character.toLowerCase(s.charAt(i)));
        }
        return this;
    }

    public Stringer prop(Object key, String... values)
    {
        return prop(key, (Object[]) values);
    }

    public Stringer prop(Object key, Object... values)
    {
        return this.add(key).bracket(values).br();
    }

    public Stringer bracket(Object... strings)
    {
        this.text.append("[");
        for (int i = 0; i < strings.length; i++)
            this.add(strings[i]).append(i == strings.length - 1 ? "" : " ");
        this.text.append("]");
        return this;
    }

    public Stringer row(Object... cells)
    {
        for (int i = 0; i < cells.length; i++)
            quot(cells[i] == null ? "" : cells[i].toString()).sepOrBr(i != cells.length - 1);
        return this;
    }

    public Stringer quote(String quot)
    {
        this.quot = quot;
        return this;
    }

    public Stringer resetSep(String sep)
    {
        this.sep = sep;
        return this;
    }

    public Stringer addSep(Object value)
    {
        return this.append(value).sep();
    }

    public Stringer sep()
    {
        this.text.append(sep);
        return this;
    }

    public Stringer sep(boolean doAdd)
    {
        if (doAdd)
            sep();
        return this;
    }

    public Stringer sepOrBr(boolean sep)
    {
        return sep ? sep() : br();
    }

    public Stringer cm()
    {
        this.text.append(",");
        return this;
    }

    public Stringer sc()
    {
        this.text.append(";");
        return this;
    }

    public Stringer sp()
    {
        this.text.append(' ');
        return this;
    }

    public Stringer sp(int size)
    {
        while (size-- > 0)
            this.text.append(' ');
        return this;
    }

    public Stringer br()
    {
        this.text.append('\n');
        return this;
    }

    public Stringer br2()
    {
        this.text.append("\n\n");
        return this;
    }

    public Stringer quot(String d)
    {
        this.text.append("\"" + d + "\"");
        return this;
    }

    public Stringer rn()
    {
        this.text.append('\r').append('\n');
        return this;
    }

    public Stringer span(Object... strings)
    {
        for (Object str : strings)
            this.text.append(str);
        return this;
    }

    public Stringer add(char c)
    {
        this.text.append(c);
        return this;
    }

    public Stringer add(Object o)
    {
        this.text.append(o == null ? "null" : o.toString());
        return this;
    }

    public Stringer append(CharSequence str)
    {
        this.text.append(str);
        return this;
    }

    public Stringer addAll(String... strings)
    {
        for (String s : strings)
            text.append(s);
        return this;
    }

    public Stringer separateIf(boolean cond, Object... o)
    {
        if(cond)
            separate(o);
        return this;
    }


    public Stringer separate(Object... o)
    {
        for (int i = 0; i < o.length; i++)
        {
            text.append(o[i] == null ? "" : o[i].toString());
            if (i < o.length - 1)
                text.append(sep);
        }
        return this;
    }

    public Stringer separate(boolean... o)
    {
        for (int i = 0; i < o.length; i++)
        {
            text.append("" + o[i]);
            if (i < o.length - 1)
                text.append(sep);
        }
        return this;
    }

    public Stringer separateInts(int... o)
    {
        for (int i = 0; i < o.length; i++)
        {
            text.append("" + o[i]);
            if (i < o.length - 1)
                text.append(sep);
        }
        return this;
    }

    public Stringer separateFloats(float... o)
    {
        for (int i = 0; i < o.length; i++)
        {
            text.append("" + o[i]);
            if (i < o.length - 1)
                text.append(sep);
        }
        return this;
    }

    public Stringer separateDoubles(double... o)
    {
        for (int i = 0; i < o.length; i++)
        {
            text.append("" + o[i]);
            if (i < o.length - 1)
                text.append(sep);
        }
        return this;
    }

    public int lastIndex()
    {
        return text.length() - 1;
    }

    public char lastChar()
    {
        return text.charAt(lastIndex());
    }

    public boolean isLastChar(char c)
    {
        int lastIndex = lastIndex();
        return lastIndex < 0 ? false : text.charAt(lastIndex) == c;
    }

    public Stringer deleteLastChar()
    {
        int lastIndex = lastIndex();
        if (lastIndex >= 0)
            text.deleteCharAt(lastIndex);
        return this;
    }

    public Stringer deleteLastCharIf(char c)
    {
        int lastIndex = lastIndex();
        if (lastIndex >= 0 && text.charAt(lastIndex) == c)
            text.deleteCharAt(lastIndex);
        return this;
    }

    public Stringer normalizeEndOfLine()
    {
        int i = text.length() - 2;
        if (i > 0 && text.charAt(i) != ' ' && text.charAt(i + 1) == '-')
            return deleteLastChar();
        return needLastSpace();
    }

    public Stringer needLastSpace()
    {
        if (lastChar() != ' ')
            append(" ");
        return this;
    }

    public Stringer appendIf(boolean condition, char c)
    {
        if (condition)
            this.text.append(c);
        return this;
    }

    public Stringer appendIf(boolean condition, CharSequence string)
    {
        if (condition)
            this.text.append(string);
        return this;
    }

    public Stringer append(Object o)
    {
        return this.add(o);
    }

    public Stringer removeLast()
    {
        this.text.deleteCharAt(text.length() - 1);
        return this;
    }

    public Stringer removeFirst()
    {
        this.text.deleteCharAt(0);
        return this;
    }

    public Stringer removeLast(int size)
    {
        this.text.delete(text.length() - size, text.length());
        return this;
    }

    public Stringer end(String end)
    {
        int l = text.length();
        int i = l - end.length();
        if (i < 0 || !text.substring(i, l).equals(end))
            text.append(end);
        return this;
    }

    public Stringer start(String start)
    {
        int i = start.length();
        if (i > text.length() || !text.substring(0, i).equals(start))
            text.insert(0, start);
        return this;
    }

    public Stringer trimEnd(String end)
    {
        int l = text.length();
        int i = l - end.length();
        if (i >= 0 && text.substring(i, l).equals(end))
            text.delete(i, l);
        return this;
    }

    public Stringer trimStart(String start)
    {
        int i = start.length();
        if (i <= text.length() && text.substring(0, i).equals(start))
            text.delete(0, i);
        return this;
    }

    public String string()
    {
        return text.toString();
    }

    @Override
    public String toString()
    {
        return text.toString();
    }

    public static Comparator<String> STRING_COMPARATOR = (s1, s2) ->
    {
        if (s1.equals(s2))
            return 0;
        int l = Math.min(s1.length(), s2.length());
        char c1;
        char c2;
        for (int c = 0; c < l; c++)
        {
            c1 = s1.charAt(c);
            c2 = s2.charAt(c);
            if (c1 == c2)
                continue;
            return c1 - c2;
        }
        return s1.length() - s2.length();
    };

    public static Stringer concat(String inbetween, String... strings)
    {
        Stringer concat = new Stringer();
        for (int i = 0; i < strings.length; i++)
            concat.append(strings[i]).append(i == strings.length - 1 ? "" : inbetween);
        return concat;
    }

    // thus we'll be able to add a is(String str) class method to String3 :-)
    public static boolean is(String text, String match, String... matches)
    {
        if (text.equals(match))
            return true;
        for (String s : matches)
            if (text.equals(s))
                return true;
        return false;
    }

    public static boolean startsWith(String base, String candidate, String... candidates)
    {
        if (base.startsWith(candidate))
            return true;
        for (String s : candidates)
            if (base.startsWith(s))
                return true;
        return false;
    }

    public static boolean EndsWith(String base, String candidate, String... candidates)
    {
        if (base.endsWith(candidate))
            return true;
        for (String s : candidates)
            if (base.endsWith(s))
                return true;
        return false;
    }

    public static boolean contains(String base, String candidate, String... candidates)
    {
        if (base.contains(candidate))
            return true;
        for (String s : candidates)
            if (base.contains(s))
                return true;
        return false;
    }

    public static int[] indexSP(String data)
    {
        IntArray a = new IntArray(data.length() / 5);
        for (int i = 0; i < data.length(); i++)
            if (data.charAt(i) == ' ')
                a.add(i);
        return a.array();
    }

    public boolean write(String path)
    {
        return write(File3.Get(path));
    }

    public boolean write(File file)
    {
        return IO.WriteText(file, text.toString());
    }
}
