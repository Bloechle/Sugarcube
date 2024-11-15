package sugarcube.common.data.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Occurrences<T> implements Iterable<Map.Entry<T, Counter>>
{
    public static class Occ<T> implements Comparable<Occ<T>>
    {
        public final T object;
        public final int count;

        public Occ(T object, int count)
        {
            this.object = object;
            this.count = count;
        }

        @Override
        public int compareTo(Occ<T> o)
        {
            return Integer.compare(count, o.count);
        }
    }

    // public static class Result<T> implements Comparable<Result>
    // {
    // public final int nb;
    // public final T obj;
    //
    // public Result(int nb, T obj)
    // {
    // this.nb = nb;
    // this.obj = obj;
    // }
    //
    // @Override
    // public int compareTo(Result o)
    // {
    // return o.nb - this.nb;
    // }
    //
    // }

    private final Map3<T, Counter> map = new Map3<>();
    private T def = null;

    public Occurrences(T... objects)
    {
        for (T o : objects)
            inc(o);
    }

    public Occurrences(Iterable<T> iterable)
    {
        for (T o : iterable)
            inc(o);
    }

    public Occurrences(T def, Iterable<T> iterable)
    {
        this.def = def;
        for (T o : iterable)
            inc(o);
    }

    public Couple<T, Counter>[] couples(boolean asc)
    {
        Couple<T, Counter>[] couples = map.couples();
        Arrays.sort(couples, (c1, c2) -> asc ? c1.value().compareTo(c2.value()) : c2.value().compareTo(c1.value()));
        return couples;
    }

    public Occurrences<T> def(T def)
    {
        this.def = def;
        return this;
    }

    public void clear()
    {
        this.map.clear();
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public boolean isPopulated()
    {
        return !map.isEmpty();
    }

    public Map3<T, Counter> map()
    {
        return map;
    }

    public boolean has(T o)
    {
        return map.containsKey(o);
    }

    public boolean contains(T o)
    {
        return map.containsKey(o);
    }

    public int totalCount()
    {
        int total = 0;
        for (Counter counter : map.values())
            total += counter.value();
        return total;
    }

    public Set<T> keys()
    {
        return map.keySet();
    }

    public double probability(T o)
    {
        return map.isEmpty() ? 0.0 : occurrence(o) / (double) totalCount();
    }

    public int get(T o)
    {
        return occurrence(o);
    }

    public int occurrence(T o)
    {
        return map.containsKey(o) ? map.get(o).value() : 0;
    }

    public int[] occurrences(T[] a)
    {
        int[] occurrences = new int[a.length];
        for (int i = 0; i < occurrences.length; i++)
            occurrences[i] = occurrence(a[i]);
        return occurrences;
    }

    public int inc(T o)
    {
        if (map.containsKey(o))
            return map.get(o).inc();
        else
        {
            map.put(o, new Counter(1));
            return 1;
        }
    }

    public int inc(T o, int value)
    {
        if (map.containsKey(o))
            return map.get(o).inc(value);
        else
        {
            map.put(o, new Counter(value));
            return value;
        }
    }

    public T min()
    {
        Occ<T> min = minimum();
        return min == null ? def : min.object;
    }

    public T max()
    {
        Occ<T> max = maximum();
        return max == null ? def : max.object;
    }

    public T med()
    {
        Occ<T> med = median();
        return med == null ? def : med.object;
    }

    public Occ<T> minimum()
    {
        Map.Entry<T, Counter> entry = null;
        for (Map.Entry<T, Counter> e : map.entrySet())
            if (entry == null || e.getValue().value() < entry.getValue().value())
                entry = e;
        return entry == null ? null : new Occ<T>(entry.getKey(), entry.getValue().value());
    }

    public Occ<T> maximum()
    {
        Map.Entry<T, Counter> entry = null;
        for (Map.Entry<T, Counter> e : map.entrySet())
            if (entry == null || e.getValue().value() > entry.getValue().value())
                entry = e;
        return entry == null ? null : new Occ<T>(entry.getKey(), entry.getValue().value());
    }

    public Occ<T> median()
    {
        Occ<T>[] occs = new Occ[map.size()];
        int index = 0;
        Iterator<Entry<T, Counter>> it = iterator();
        while (it.hasNext())
        {
            Entry<T, Counter> entry = it.next();
            occs[index++] = new Occ<>(entry.getKey(), entry.getValue().value());
        }
        return occs[occs.length / 2];
    }

    public TreeList<Counter, T> treeList()
    {
        TreeList<Counter, T> tree = new TreeList<Counter, T>();
        for (Map.Entry<T, Counter> entry : map.entrySet())
            tree.put(entry.getValue(), entry.getKey());
        return tree;
    }

    public Occurrences<T> trimMin(int minSize)
    {
        Iterator<Entry<T, Counter>> it = iterator();
        while (it.hasNext())
            if(it.next().getValue().value()<minSize)
                it.remove();
        return this;
    }

    public Occurrences<T> trimMax(int maxSize)
    {
        Iterator<Entry<T, Counter>> it = iterator();
        while (it.hasNext())
            if(it.next().getValue().value()>maxSize)
                it.remove();
        return this;
    }

    public Iterator<T> keyIterator()
    {
        return map.keySet().iterator();
    }

    @Override
    public Iterator<Entry<T, Counter>> iterator()
    {
        return map.entrySet().iterator();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<T, Counter> entry : this.map.entrySet())
            sb.append(entry.getKey().toString() + "=" + entry.getValue().stringValue() + ", ");
        return sb.toString();
    }
}
