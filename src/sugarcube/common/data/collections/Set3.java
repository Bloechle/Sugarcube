package sugarcube.common.data.collections;

import sugarcube.common.system.log.Log;

import java.util.*;

public class Set3<T> extends LinkedHashSet<T>
{
    public Set3()
    {

    }

    public Set3(int capacity)
    {
        super(capacity);
    }

    public Set3(T... data)
    {
        if (data != null)
            for (T d : data)
                this.add(d);
    }

    public Set3<T> intersection(Set3<T> set)
    {
        Set3<T> intersection = new Set3<>();
        for (T element : set)
            if (contains(element))
                intersection.add(element);
        return intersection;
    }

    public Set3<T> intersection(T[] array)
    {
        Set3<T> intersection = new Set3<>();
        for (T element : array)
            if (contains(element))
                intersection.add(element);
        return intersection;
    }

    // do not uncomment, since it conflicts with T... data when data.length==1 and
    // data has T iterator on it!!!!
    // public Set3(Iterable<T> data)
    // {
    // if (data != null)
    // for (T d : data)
    // this.add(d);
    // }

    public List3<T> list()
    {
        return new List3<>(this.iterator());
    }

    public boolean isPopulated()
    {
        return !this.isEmpty();
    }

    public boolean isSizeOne()
    {
        return this.size() == 1;
    }

    public boolean has(T data)
    {
        return this.contains(data);
    }

    public boolean yet(T data)
    {
        if (contains(data))
            return true;
        add(data);
        return false;
    }

    public boolean notYet(T data)
    {
        if (!this.contains(data))
        {
            add(data);
            return true;
        }
        return false;
    }

    public boolean hasnt(T data)
    {
        return !this.contains(data);
    }

    public boolean hasOne(T... data)
    {
        return containsOne(data);
    }

    public boolean containsOne(T... data)
    {
        for (T o : data)
            if (this.contains(o))
                return true;
        return false;
    }

    public boolean hasAll(T... data)
    {
        return containsAll(data);
    }

    public boolean containsAll(T... data)
    {
        for (T o : data)
            if (!this.contains(o))
                return false;
        return true;
    }

    public T elementAt(int index)
    {
        Iterator<T> iterator = this.iterator();
        T object = null;
        while (index-- > -1 && iterator.hasNext())
            object = iterator.next();
        return object;
    }

    public T last()
    {
        return size() > 0 ? this.elementAt(this.size() - 1) : null;
    }

    public T first()
    {
        return size() > 0 ? this.elementAt(0) : null;
    }

    public void removeAll(T... data)
    {
        for (T t : data)
            this.remove(t);
    }

    public Set3<T> addLast(T data, int max)
    {
        boolean duplicate = contains(data);
        if (!duplicate)
            while (this.size() > max - 1)
                this.removeFirst();
        if (duplicate)
            remove(data);// to set the added data as last element (LinkedHashSet)
        super.add(data);
        return this;
    }

    public Set3<T> add3(T data)
    {
        super.add(data);
        return this;
    }

    public T removeFirst()
    {
        Iterator<T> it = this.iterator();
        if (it.hasNext())
        {
            T first = it.next();
            it.remove();
            return first;
        }
        return null;
    }

    public Set3<T> remove3(T data)
    {
        super.remove(data);
        return this;
    }

    public Set3<T> removeAll3(Collection<T> data)
    {
        super.removeAll(data);
        return this;
    }

    public Set3<T> unnull()
    {
        if (this.has(null))
            this.remove(null);
        return this;
    }

    public void addNotNull(T data)
    {
        if (data != null)
            this.add(data);
    }

    public void addAllNotNull(T... data)
    {
        for (T t : data)
            this.addNotNull(t);
    }

    public void addAll(T... data)
    {
        if (data != null && data.length > 0)
            this.addAll(Arrays.asList(data));
    }

    public Set3<T> addAll3(T... data)
    {
        if (data != null && data.length > 0)
            this.addAll(Arrays.asList(data));
        return this;
    }

    public Set3<T> addArray(T[] data)
    {
        if (data != null && data.length > 0)
            this.addAll(Arrays.asList(data));
        return this;
    }

    public Set3<T> addIterable(Iterable<T> data)
    {
        for (T t : data)
            this.add(t);
        return this;
    }

    public void addSet(Set<T> data)
    {
        for (T t : data)
            this.add(t);
    }

    public void setAll(T... data)
    {
        this.clear();
        this.addAll(data);
    }

    public void setAll(Iterable<T> data)
    {
        this.clear();
        this.addIterable(data);
    }

    public String[] toStrings()
    {
        return this.toStrings(true);
    }

    public String[] toStrings(boolean ascendingOrder)
    {
        String[] array = new String[this.size()];
        if (ascendingOrder)
        {
            int i = 0;
            for (T o : this)
                array[i++] = o == null ? null : o.toString();
        } else
        {
            int i = array.length - 1;
            for (T o : this)
                array[i--] = o == null ? null : o.toString();
        }
        return array;
    }

    public Set3<T> trim(T start)
    {
        Set3<T> set = new Set3<>();
        boolean add = false;
        for (T t : this)
        {
            if (t.equals(start))
                add = true;
            if (add)
                set.add(t);
        }
        return set;
    }

    public String mergedString()
    {
        StringBuilder sb = new StringBuilder(size() * 10);
        for (T t : this)
            sb.append(t);
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return toString(", ");
    }

    public String toString(String separator)
    {
        // allo mulder, si separator = " " le stringbuilder est dégénéré!!?
        int size = size();
        StringBuilder sb = new StringBuilder(size * 10);
        int counter = 0;
        for (T t : this)
            sb.append(t).append(++counter < size ? separator : "");
        // Log.debug(Set3.class, ".main - "+sb.toString());
        return sb.toString();
    }

    public static void main(String... args)
    {
        IntSet pfff = new IntSet(1, 2, 3, 4);
        Log.debug(Set3.class, ".main - " + pfff.toString());
    }
}
