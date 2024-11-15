package sugarcube.common.data.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class StringSet extends Set3<String>
{
    public StringSet()
    {

    }

    public StringSet(String... data)
    {
        this.addAll(data);
    }

    public StringSet(String[]... data)
    {
        for (String[] row : data)
            this.addAll(row);
    }

    public StringSet(Collection<String> set)
    {
        this.addAll(set);
    }

    public StringSet multiCases()
    {
        for (String s : array())
        {
            String lower = s.toLowerCase();
            this.add(lower);
            this.add(s.toUpperCase());
            if (lower.length() > 1)
                this.add(lower.substring(0, 1).toUpperCase() + "" + lower.substring(1));
        }
        return this;
    }

    public StringSet addNonVoid(String data)
    {
        if (!Str.IsVoid(data))
            this.add(data);
        return this;
    }

    public StringSet addAllNonVoid(String... data)
    {
        return addAllNonVoid(false, data);
    }

    public StringSet addAllNonVoid(boolean doTrim, String... data)
    {
        if (data != null)
            for (String d : data)
                if (d != null)
                    addNonVoid(doTrim ? d.trim() : d);
        return this;
    }

    public StringSet addAllChars(String... data)
    {
        if (data != null)
            for (String d : data)
                if (d != null)
                    for (int i = 0; i < d.length(); i++)
                        this.add(d.charAt(i) + "");
        return this;
    }

    @Override
    public StringSet addAll3(String... data)
    {
        super.addAll3(data);
        return this;
    }

    public StringSet addAll3(Collection<? extends String> data)
    {
        super.addAll(data);
        return this;
    }

    public String[] array()
    {
        return this.toArray(new String[0]);
    }

    public String[] sortedArray()
    {
        String[] array = array();
        Arrays.sort(array);
        return array;
    }

    @Override
    public StringList list()
    {
        return new StringList(this.iterator());
    }

    @Override
    public StringSet trim(String start)
    {
        StringSet set = new StringSet();
        boolean add = false;
        for (String t : this)
        {
            if (t.equals(start))
                add = true;
            if (add)
                set.add(t);
        }
        return set;
    }

    public StringSet trimNull()
    {
        Iterator<String> it = this.iterator();
        while (it.hasNext())
            if (it.next() == null)
                it.remove();
        return this;
    }

    public StringSet trimVoid()
    {
        Iterator<String> it = this.iterator();
        while (it.hasNext())
            if (Str.IsVoid(it.next()))
                it.remove();
        return this;
    }

    public StringSet replace(String from, String to)
    {
        StringSet set = new StringSet();
        for (String s : this)
            set.add(s.replace(from, to));
        return set;
    }

    public static String[] EnsureSet(String... data)
    {
        return new StringSet(data).array();
    }
}
