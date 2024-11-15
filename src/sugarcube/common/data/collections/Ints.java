package sugarcube.common.data.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class Ints extends List3<Integer>
{
    public Ints(Iterable<Integer> iterable)
    {
        this(iterable.iterator());
    }

    public Ints(Iterator<Integer> iterator)
    {
        while (iterator.hasNext())
            this.add(iterator.next());
    }

    public Ints(int... data)
    {
        this.addAll3(data);
    }

    public int last(int def)
    {
        return isEmpty() ? def : get(size() - 1);
    }

    public int beforeLast(int def)
    {
        return isEmpty() ? def : get(size() - 2);
    }

    public Ints addAll3(int... data)
    {
        for (int v : data)
            this.add(v);
        return this;
    }

    public Ints removeDuplicates()
    {
        IntSet set = new IntSet();
        Iterator<Integer> it = iterator();
        while (it.hasNext())
            if (set.yet(it.next()))
                it.remove();
        return this;
    }

    public int removeMin()
    {
        int min = min();
        remove((Integer) min);
        return min;
    }

    public int removeMax()
    {
        int max = max();
        remove((Integer) max);
        return max;
    }

    public int min()
    {
        if (size() == 0)
            return 0;
        int min = firstInt();
        for (Integer v : this)
            if (v != null && v < min)
                min = v;
        return min;
    }

    public int max()
    {
        if (size() == 0)
            return 0;
        int max = firstInt();
        for (Integer v : this)
            if (v != null && v > max)
                max = v;
        return max;
    }

    public int firstInt()
    {
        Integer v = first();
        return v == null ? 0 : v;
    }

    public Ints delta()
    {
        Integer prev = null;
        Ints delta = new Ints();
        for (Integer value : this)
            if (value != null)
            {
                if (prev != null)
                    delta.add(value - prev);
                prev = value;
            }
        return delta;
    }

    public Ints integral()
    {
        int total = 0;
        Ints integral = new Ints();
        for (Integer value : this)
            integral.add(total += (value == null ? 0 : value));
        return integral;
    }

    public Ints addRound(double... data)
    {
        for (double d : data)
            this.add((int) Math.round(d));
        return this;
    }

    public Ints addInt(double... data)
    {
        for (double d : data)
            this.add((int) d);
        return this;
    }

    public Ints addRange(int min, int max)
    {
        for (int i = min; i <= max; i++)
            add(i);
        return this;
    }

    public IntSet set()
    {
        IntSet set = new IntSet();
        set.addAll(this);
        return set;
    }

    public int[] array()
    {
        int[] array = new int[this.size()];
        int i = 0;
        for (int v : this)
            array[i++] = v;
        return array;
    }

    public int[] array(int size, int def)
    {
        int[] array = new int[size];
        int i = 0;
        for (int v : this)
            if (i < array.length)
                array[i++] = v;
            else
                break;
        while (i < array.length)
            array[i++] = def;
        return array;
    }

    public Ints sort()
    {
        return sort(true);
    }

    public Ints sort(boolean asc)
    {
        if (asc)
            Collections.sort(this, (a, b) -> Integer.compare(a, b));
        else
            Collections.sort(this, (a, b) -> -Integer.compare(a, b));
        return this;
    }

    public Ints copy()
    {
        return new Ints(this);
    }

    public Ints addToCopy(int... data)
    {
        Ints copy = copy();
        copy.addAll3(data);
        return copy;
    }

    public boolean has(double value, double distance)
    {
        for (int v : this)
            if (Math.abs(value - v) < distance)
                return true;
        return false;
    }

    public boolean addIfNotHas(double value, double distance)
    {
        boolean doAdd = !has(value, distance);
        if (doAdd)
            this.addRound(value);
        return doAdd;
    }

    public int nearestIndex(double value)
    {
        return nearestIndex((int) Math.round(value));
    }

    public int nearestIndex(int value)
    {
        if (this.isEmpty())
            return -1;
        int index = 0;
        int minIndex = 0;
        int minDelta = Math.abs(value - this.first());
        for (Integer v : this)
        {
            int delta = Math.abs(value - v);
            if (delta < minDelta)
            {
                minIndex = index;
                minDelta = delta;
            }
            index++;
        }
        return minIndex;
    }

    public Ints shuffle()
    {
        Collections.shuffle(this);
        return this;
    }

    public int mean()
    {
        int sum = 0;
        for (Integer v : this)
            if (v != null)
                sum += v;
        return sum == 0 ? 0 : sum / size();
    }

    public int median()
    {
        int[] ints = array();
        Arrays.sort(ints);
        return ints[ints.length / 2];
    }

    public int median(int def)
    {
        return isEmpty() ? def : median();
    }

    public static int[] Array(int size, int value)
    {
        int[] data = new int[size];
        for (int i = 0; i < data.length; i++)
            data[i] = value;
        return data;
    }

    public static int[] IndexArray(int size)
    {
        int[] data = new int[size];
        for (int i = 0; i < data.length; i++)
            data[i] = i;
        return data;
    }
}
