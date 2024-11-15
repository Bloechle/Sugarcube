package sugarcube.common.data.collections;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.xml.Nb;

import java.lang.reflect.Field;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Objects;
import java.util.Random;

public class Str implements CharSequence
{
    private String data;

    public Str(Object o)
    {
        this.data = o == null ? null : o.toString();
    }

    public Str(String[] data)
    {
        if (data == null || data.length == 0)
            this.data = "";
        else if (data.length == 1)
            this.data = data[0];
        else
        {
            StringBuilder sb = new StringBuilder((data[0].length() + data[data.length - 1].length()) * data.length);
            for (String s : data)
                sb.append(s);
            this.data = sb.toString();
        }
    }

    public Str first()
    {
        return first(Tokens.SPLIT_SPACE);
    }

    public Str first(String splitter)
    {
        return first(splitter, null);
    }

    public Str first(String splitter, String recover)
    {
        return Tokens.Split(data, splitter).get(0, recover);
    }

    public Tokens splitSpace()
    {
        return Tokens.Split(data);
    }

    public Tokens split(String splitter)
    {
        return Tokens.Split(data, splitter);
    }

    public static Str wrap(Object data)
    {
        return new Str(data);
    }

    public Str setData(String data)
    {
        this.data = data;
        return this;
    }

    public String data()
    {
        return data;
    }

    public Str set(String data)
    {
        this.data = data;
        return this;
    }

    public String get()
    {
        return data;
    }

    public String string()
    {
        return data;
    }

    public String lowTrim()
    {
        return data.toLowerCase().trim();
    }

    public boolean contains(String string)
    {
        return data.contains(string);
    }

    public boolean containsOne(String... strings)
    {
        for (String s : strings)
            if (data.contains(s))
                return true;
        return false;
    }

    public boolean containsAll(String... strings)
    {
        for (String s : strings)
            if (!data.contains(s))
                return false;
        return true;
    }

    public Str remove(String... strings)
    {
        this.data = Remove(data, strings);
        return this;
    }

    public Str spacify(String... strings)
    {
        this.data = Spacify(data, strings);
        return this;
    }

    public Str remove(char... chars)
    {
        this.data = Remove(data, chars);
        return this;
    }

    public Str spacify(char... chars)
    {
        this.data = Spacify(data, chars);
        return this;
    }

    public boolean isDigits()
    {
        String d = data.trim();
        for (int i = 0; i < data.length(); i++)
            if (d.charAt(i) < '0' || d.charAt(i) > '9')
                return false;
        return true;
    }

    public boolean hasDigit()
    {
        String d = data.trim();
        for (int i = 0; i < data.length(); i++)
            if (d.charAt(i) >= '0' && d.charAt(i) <= '9')
                return true;
        return false;
    }

    public boolean isInteger()
    {
        return Nb.isInteger(data);
    }

    public boolean isReal()
    {
        return Nb.isReal(data);
    }

    public boolean isPercent()
    {
        return data.endsWith("%");
    }

    public int integer()
    {
        return integer(0);
    }

    public int integer(int def)
    {
        return Nb.Int(data, def);
    }

    public float real()
    {
        return real(0);
    }

    public float real(float def)
    {
        return Nb.Float(data, def);
    }

    public int percent()
    {
        return percent(0);
    }

    public int percent(int def)
    {
        return Nb.Int(data.replace("%", ""), def);
    }

    public boolean has(String text)
    {
        return data.contains(text);
    }

    public boolean has(Object o)
    {
        return data.contains(o.toString());
    }

    public boolean startsWith(String text)
    {
        return data != null && data.startsWith(text);
    }

    public boolean endsWith(String text)
    {
        return data != null && data.endsWith(text);
    }

    public Str trim()
    {
        if (data != null)
            data = data.trim();
        return this;
    }

    public String Trim(int start, int end)
    {
        return data.substring(start, end);
    }

    public int size()
    {
        return data.length();
    }

    @Override
    public int length()
    {
        return data.length();
    }

    @Override
    public char charAt(int i)
    {
        return data.charAt(i);
    }

    @Override
    public CharSequence subSequence(int start, int end)
    {
        return data.subSequence(start, end);
    }

    public boolean equals(String value, boolean ignoreCase)
    {
        return data == null && value == null || (ignoreCase ? data.equalsIgnoreCase(value) : data.equals(value));
    }

    public boolean equals(Str value)
    {
        return value == null ? false : Str.Equals(data, value.data);
    }

    public boolean equals(String value)
    {
        return Str.Equals(data, value);
    }

    public boolean is(String... candidates)
    {
        return Str.Equals(data, candidates);
    }

    @Override
    public String toString()
    {
        return this.data;
    }

    public Str replace(String key, String value)
    {
        this.data = data.replace(key, value);
        return this;
    }

    public Str replace(boolean pairs, String... keyValue)
    {
        this.data = Replace(pairs, data, keyValue);
        return this;
    }

    public Str prefix(char c, int size)
    {
        this.data = Prefix(data, c, size);
        return this;
    }

    public Str postfix(char c, int size)
    {
        this.data = Postfix(data, c, size);
        return this;
    }

    public static long Hash(String data)
    {
        long h = 0;
        if (h == 0 && data.length() > 0)
            for (int i = 0; i < data.length(); i++)
                h = 31 * h + data.charAt(i);
        return h;
    }

    public static boolean Eq(String s, String t)
    {
        return Objects.equals(s, t);
    }

    public static boolean Eq(boolean ignoreCase, String s, String t)
    {
        if (ignoreCase)
        {
            if (s == t)
                return true;
            if (s != null)
                s = s.toLowerCase();
            if (t != null)
                t = t.toLowerCase();
        }
        return Objects.equals(s, t);
    }

    public static boolean Equal(String t1, String t2)
    {
        return Objects.equals(t1, t2);
    }

    public static boolean EqualTrim(String t1, String t2)
    {
        if (Equal(t1, t2))
            return true;
        return t1 != null && t2 != null && t1.replaceAll("\\s+", " ").trim().equals(t2.replaceAll("\\s+", " ").trim());
    }

    public static boolean Equals(String text, String... texts)
    {
        for (String t : texts)
            if (Objects.equals(t, text))
                return true;
        return false;
    }

    public static String Lower(String s)
    {
        return s == null ? null : s.toLowerCase();
    }

    public static String Trim(String data, int max)
    {
        return Trim(data, max, "");
    }

    public static String Trim(String data, int max, String postfix)
    {
        return data == null ? "" : data.length() > max ? data.substring(0, max) + postfix : data;
    }

    public static String ReplaceWith(String text, String newValue, String... oldValues)
    {
        for (int i = 0; i < oldValues.length; i++)
            text = text.replace(oldValues[i], newValue);
        return text;
    }

    public static String ReplaceWhiteSpacesBy(String text, String by)
    {
        return text.replaceAll("\\s+", by);
    }

    public static String ReplacePairs(String text, String... keyValues)
    {
        return Replace(true, text, keyValues);
    }

    public static String Replace(boolean pairs, String text, String... keyValue)
    {
        if (pairs)
            for (int i = 0; i < keyValue.length; i += 2)
                text = text.replace(keyValue[i], keyValue[i + 1]);
        else
            for (int i = 0; i < keyValue.length - 1; i++)
                text = text.replace(keyValue[i], keyValue[keyValue.length - 1]);
        return text;
    }

    public static String ReplaceNonAlphaNumericBy_(String text)
    {
        return text == null ? "" : text.replaceAll("[^A-Za-z0-9]", " ").trim().replaceAll(" +", "_");
    }

    public static String UppercaseStart(String text)
    {
        return text == null ? null : (text.length() < 2 ? text.toUpperCase() : (StartsWithUppercase(text) ? text : text.substring(0, 1).toUpperCase() + text.substring(1)));
    }

    public static String LowercaseStart(String text)
    {
        return text == null ? null : (text.length() < 2 ? text.toLowerCase() : (StartsWithLowercase(text) ? text : text.substring(0, 1).toLowerCase() + text.substring(1)));
    }

    public static boolean StartsWithUppercase(String text)
    {
        return text.length() > 0 && Character.isUpperCase(text.charAt(0));
    }

    public static boolean StartsWithLowercase(String text)
    {
        return text.length() > 0 && Character.isLowerCase(text.charAt(0));
    }

    public static boolean StartsWithDigit(String text)
    {
        return text.length() > 0 && Character.isDigit(text.charAt(0));
    }

    public static boolean StartsWithLetter(String text)
    {
        return text.length() > 0 && Character.isLetter(text.charAt(0));
    }

    public static boolean StartsWithSymbol(String text)
    {
        return !StartsWithLetter(text) && !StartsWithDigit(text);
    }

    public static boolean Starts(String text, String... starts)
    {
        for (String start : starts)
            if (text.startsWith(start))
                return true;
        return false;
    }

    public static boolean Ends(String text, String... ends)
    {
        for (String end : ends)
            if (text.endsWith(end))
                return true;
        return false;
    }

    public static String Unstarts(String text, String... starts)
    {
        for (String start : starts)
            if (text.startsWith(start))
                return text.substring(start.length());
        return text;
    }

    public static String Unends(String text, String... ends)
    {
        for (String end : ends)
            if (text.endsWith(end))
                return text.substring(0, text.length() - end.length());
        return text;
    }

    public static String NeedStart(String text, String start)
    {
        return text.startsWith(start) ? text : start + text;
    }

    public static String NeedEnd(String text, String end)
    {
        return text.endsWith(end) ? text : text + end;
    }

    public static String Remove(String text, String... strings)
    {
        for (String string : strings)
            if (text.contains(string))
                text = text.replace(string, "");
        return text;
    }

    public static String Remove(String text, char... chars)
    {
        for (char c : chars)
            if (text.indexOf(c) > -1)
                text = text.replace("" + c, "");
        return text;
    }

    public static String Spacify(String text, String... strings)
    {
        for (String string : strings)
            if (text.contains(string))
                text = text.replace(string, " ");
        return text;
    }

    public static String Spacify(String text, char... chars)
    {
        for (char c : chars)
            if (text.indexOf(c) > -1)
                text = text.replace(c, ' ');
        return text;
    }

    public static String ToString(String text, int maxSize)
    {
        if (text.length() <= maxSize)
            return text;
        else
            return text.substring(0, maxSize) + "…";
    }

    public static String ToCodePointString(String text)
    {
        String s = "";
        for (char c : text.toCharArray())
            s += ((int) c) + " ";
        return s.trim();
    }

    public static String Unvoid(String text, String def)
    {
        return text == null || text.trim().isEmpty() ? def : text;
    }

    public static String[] Split(String data)
    {
        return data == null || data.isEmpty() ? new String[0] : data.trim().split(Tokens.SPLIT_SPACE);
    }

    public static String[] Split(String data, String sep)
    {
        return data == null || data.isEmpty() ? new String[0] : data.trim().split(sep);
    }

    public static String Concat(String... data)
    {
        if (data.length == 0)
            return "";
        else if (data.length == 1)
            return data[0];
        StringBuilder sb = new StringBuilder(data[0].length() * data.length * 2);
        for (Object o : data)
            sb.append(o);
        return sb.toString();
    }

    public static String ConcatSep(String separator, String... data)
    {
        return ConcatSepEsc(separator, null, data);
    }

    public static String ConcatSepEsc(String separator, String escape, String... data)
    {
        if (data.length == 0)
            return "";
        else if (data.length == 1)
            return escape == null ? data[0] : data[0].replace(separator, escape);
        StringBuilder sb = new StringBuilder(data[0].length() * data.length * 2);
        for (int i = 0; i < data.length; i++)
            sb.append((escape == null ? data[i] : data[i].replace(separator, escape)) + (i == data.length - 1 ? "" : separator));
        return sb.toString();
    }

    public static String Concat(String separator, String escape, Object... data)
    {
        if (data.length == 0)
            return "";

        String first = data[0] == null ? "" : data[0].toString();

        if (data.length == 1)
            return escape == null ? first : first.replace(separator, escape);

        StringBuilder sb = new StringBuilder(first.length() * data.length * 2);
        for (int i = 0; i < data.length; i++)
            sb.append(
                    (escape == null ? (data[i] == null ? "" : data[i].toString()) : (data[i] == null ? "" : data[i].toString()).replace(separator, escape))
                            + (i == data.length - 1 ? "" : separator));

        return sb.toString();
    }

    // public static String Concat(Object... data)
    // {
    // if (data.length == 0)
    // return "";
    // else if (data.length == 1)
    // return data[0].toString();
    // StringBuilder sb = new StringBuilder(data[0].toString().length() *
    // data.length * 2);
    // for (Object o : data)
    // sb.append(o.toString());
    // return sb.toString();
    // }

    public static String ToString(Object o)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getName()).append("[").append(o.getClass().getDeclaredFields().length).append("]");
        for (Field field : o.getClass().getDeclaredFields())
            try
            {
                sb.append("\n").append(field.getName()).append("=").append(field.get(o).toString());
            } catch (Exception e)
            {
                Log.warn(Str.class, ".toString - field reflection exception: " + e);
            }
        return sb.toString();
    }

    public static int NbOfDigits(String text)
    {
        int digits = 0;
        for (char c : text.toCharArray())
            if (Character.isDigit(c))
                digits++;
        return digits;
    }

    public static double DigitsPercentage(String text)
    {
        int size = text == null ? 0 : text.trim().length();
        return size == 0 ? 0 : NbOfDigits(text) / (double) size;
    }

    public static boolean Has(String text, String... candidates)
    {
        for (String candidate : candidates)
            if (text.contains(candidate))
                return true;
        return false;
    }

    public static String UnAccent(String text)
    {
        return Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String Norm(String text)
    {
        return Norm(text, true, true, true);
    }

    public static String Norm(String text, boolean accent, boolean lowercase, boolean whitespace)
    {
        if (lowercase)
            text = text.toLowerCase();
        if (whitespace)
            text = text.replace(" ", "");
        if (accent)
            text = UnAccent(text);
        return text;
    }

    // public static boolean Regex(String text, String... regexes)
    // {
    // return sugarcube.ocd.model.rules.Regex.New(regexes).matches(text);
    // }

    public static String Trim(String text, char c)
    {
        if (text.length() > 0 && text.charAt(0) == c)
            text = text.substring(1);
        if (text.length() > 0 && text.charAt(text.length() - 1) == c)
            text = text.substring(0, text.length() - 1);
        return text;
    }

    public static String TrimStartWS(String text)
    {
        while (text.startsWith(" "))
            text = text.substring(1);
        return text;
    }

    public static String TrimEndWS(String text)
    {
        while (text.endsWith(" "))
            text = text.substring(0, text.length() - 1);
        return text;
    }

    public static String TrimStart(String text, String... starts)
    {
        for (String start : starts)
            if (text.startsWith(start))
                return text.substring(start.length());
        return text;
    }

    public static String TrimEnd(String text, String... ends)
    {
        for (String end : ends)
            if (text.endsWith(end))
                return text.substring(0, text.length() - end.length());
        return text;
    }

    public static String Prefix(int counter, int size)
    {
        return Prefix("" + counter, '0', size);
    }

    public static String Prefix(String data, char c, int size)
    {
        while (data.length() < size)
            data = c + data;
        return data;
    }

    public static String Postfix(String data, char c, int size)
    {
        while (data.length() < size)
            data = data + c;
        return data;
    }

    public static int NbOfLetters(String data)
    {
        int sum = 0;
        for (char c : data.toCharArray())
            if (Character.isLetter(c))
                sum++;
        return sum;
    }

    public static int NbOf(char character, String data)
    {
        int sum = 0;
        for (char c : data.toCharArray())
            if (c == character)
                sum++;
        return sum;
    }

    public static String[] TrimAll(String[] data)
    {
        for (int i = 0; i < data.length; i++)
            data[i] = data[i] == null ? null : data[i].trim();
        return data;
    }

    public static boolean IsVoid(String data)
    {
        return data == null || data.isEmpty();
    }

    public static String IfVoid(String data, String def)
    {
        return IsVoid(data) ? def : data;
    }

    public static String FirstNonVoid(String... array)
    {
        for (String data : array)
            if (!Str.IsVoid(data))
                return data;
        return null;
    }

    public static boolean HasData(String data)
    {
        return !IsVoid(data);
    }

    public static boolean Has(String data)
    {
        return HasChar(data);
    }

    public static boolean HasChar(String data)
    {
        return HasChar(data, true);
    }

    public static boolean HasChar(String data, boolean trim)
    {
        return data != null && !(trim ? data.trim() : data).isEmpty();
    }

    public static boolean HasDigit(String data)
    {
        if (data != null)
        {
            char c;
            for (int i = 0; i < data.length(); i++)
                if ((c = data.charAt(i)) >= '0' && c <= '9')
                    return true;
        }
        return false;
    }

    public static boolean IsDigits(String data)
    {
        if (data != null)
        {
            char c;
            for (int i = 0; i < data.length(); i++)
                if ((c = data.charAt(i)) < '0' || c > '9')
                    return false;

            return true;
        }
        return false;
    }

    public static String[] Unnull(String[] data, String def)
    {
        for (int i = 0; i < data.length; i++)
            if (data[i] == null)
                data[i] = def;
        return data;
    }

    public static String Unnull(String data)
    {
        return data == null ? "" : data;
    }

    public static String Trim(String data)
    {
        return data == null ? "" : data.trim();
    }

    public static String Avoid(String data, String def)
    {
        return IsVoid(data) ? def : data;
    }

    public static int[] indexesOf(String text, char c)
    {
        IntArray indexes = new IntArray(5);
        for (int i = 0; i < text.length(); i++)
            if (text.charAt(i) == c)
                indexes.add(i);
        return indexes.array();
    }

    public static int IndexOf(String text, String... texts)
    {
        int index = -1;
        for (String s : texts)
            if ((index = text.indexOf(s)) > -1)
                return index;
        return -1;
    }

    public static int LastIndexOf(String text, String... texts)
    {
        int index = -1;
        for (String s : texts)
            if ((index = text.lastIndexOf(s)) > -1)
                return index;
        return -1;
    }

    public static boolean Is09AZaz(char c)
    {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
    }

    public static boolean IsAZaz(char c)
    {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }

    public static boolean IsAZ(char c, boolean uppercase)
    {
        return uppercase ? IsAZ(c) : Isaz(c);
    }

    public static boolean IsAZ(char c)
    {
        return c >= 'A' && c <= 'Z';
    }

    public static boolean Isaz(char c)
    {
        return c >= 'a' && c <= 'z';
    }

    public static boolean Is09(char c)
    {
        return c >= '0' && c <= '9';
    }

    public static boolean Is09(String text)
    {
        for (int i = 0; i < text.length(); i++)
            if (!Is09(text.charAt(i)))
                return false;
        return true;
    }

    public static String KeepDigits(String s)
    {
        return s.replaceAll("[^0-9]", "");
    }

    public static int Integer(String s, int def)
    {
        return Nb.Int(KeepDigits(s), def);
    }

    public static String[] Strings(String... strings)
    {
        return strings;
    }

    public static String Digits2(double v)
    {
        return String.format("%.2f", v);
    }

    public static int Count(String text, char c)
    {
        int count = 0;
        for (int i = 0; i < text.length(); i++)
            if (c == text.charAt(i))
                count++;
        return count;
    }

    public static int Count(String text, String find)
    {
        int index = 0, count = 0, length = find.length();
        if (length > 0)
            while ((index = text.indexOf(find, index)) != -1)
            {
                index += length;
                count++;
            }
        return count;
    }

    public static boolean HasChar(String text, char c)
    {
        for (int i = 0; i < text.length(); i++)
            if (text.charAt(i) == c)
                return true;
        return false;
    }

    public static String ReplaceCharsWith(String text, String chars, String with)
    {
        for (int i = 0; i < chars.length(); i++)
            text = text.replace("" + chars.charAt(i), with);
        return text;
    }

    public static String After(String text, String sep)
    {
        int i = text.indexOf(sep);
        return i > 0 ? text.substring(i + 1) : text;
    }

    public static String Before(String text, String sep)
    {
        int i = text.indexOf(sep);
        return i > 0 ? text.substring(0, i) : text;
    }

    public static String Substring(String text, int size)
    {
        int s = text.length();
        return s > size ? text.substring(0, size) : text;
    }

    public static String SubstringEnd(String text, int size)
    {
        int s = text.length();
        return s > size ? text.substring(s - size) : text;
    }

    public static String F(String text, Object... args)
    {
        return String.format(text, args);
    }

    public static String Pad(String text, int size)
    {
        return Pad(text, size, ' ');
    }

    public static String Pad(String text, int size, char c)
    {
        while (text.length() < size)
            text += c;
        return text;
    }

    public static String PrePad(String text, int size)
    {
        return PrePad(text, size, ' ');
    }

    public static String PrePad(String text, int size, char c)
    {
        while (text.length() < size)
            text = c + text;
        return text;
    }

    public static String[] Array(String... data)
    {
        return data;
    }

    public static String[] Array(int size, String value)
    {
        String[] data = new String[size];
        for (int i = 0; i < data.length; i++)
            data[i] = value;
        return data;
    }

    public static String[] Shuffle(String... data)
    {
        Random rand = new Random();
        int j;
        String tmp;
        for (int i = data.length; i > 1; i--)
        {
            j = rand.nextInt(i);
            tmp = data[j];
            data[j] = data[i - 1];
            data[i - 1] = tmp;
        }
        return data;
    }

    public static String[] Array(boolean doShuffle, int repetition, String... data)
    {
        String[] a = new String[data.length * repetition];
        int index = 0;
        for (int i = 0; i < repetition; i++)
            for (int j = 0; j < data.length; j++)
                a[index++] = data[j];
        return doShuffle ? Shuffle(a) : a;
    }

    public static String[] Swap(String[] a, int i, int j)
    {
        String tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        return a;
    }

    public static String InsertStringAt(String value, String insert, int index)
    {
        if (index < 0)
            index = value.length() + index;
        if (index <= 0)
            return insert + value;
        if (index >= value.length())
            return value + insert;
        return value.substring(0, index) + insert + value.substring(index + 1);
    }

    public static int EqIndex(String value, String[] values)
    {
        for (int i = 0; i < values.length; i++)
            if (Str.Eq(value, values[i]))
                return i;
        return -1;
    }

    public static String CircularNext(String value, String[] values)
    {
        int i = Str.EqIndex(value, values);
        if (i > -1)
            return ++i < values.length ? values[i] : values[0];
        return value;
    }

    public static int Size(String s)
    {
        return s == null ? -1 : s.length();
    }

    public static String[] Join(String[] base, String... more)
    {
        String[] joined = new String[base.length + more.length];
        System.arraycopy(base, 0, joined, 0, base.length);
        System.arraycopy(more, 0, joined, base.length, more.length);
        return joined;
    }

    public static String Labelize(String field)
    {
        String label = "";
        for (char c : field.toCharArray())
            label += (c >= 'A' && c <= 'Z') ? " " + c : c;
        return label.trim();
    }


}
