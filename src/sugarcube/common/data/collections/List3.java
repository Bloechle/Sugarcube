package sugarcube.common.data.collections;

import sugarcube.common.interfaces.Applier;
import sugarcube.common.interfaces.Condition;

import java.util.*;

@SuppressWarnings("serial")
public class List3<T> extends LinkedList<T>
{
    public List3()
    {
    }

    public List3(Iterable<T> iterable)
    {
        this(iterable.iterator());
    }

    public List3(Iterator<T> iterator)
    {
        while (iterator.hasNext())
            this.add(iterator.next());
    }

    public List3(T... data)
    {
        this.addAll(Arrays.asList(data));
    }

    public List3<T> apply(Applier<T> applier)
    {
        for (T element : this)
            applier.apply(element);
        return this;
    }

    public List3<T> applyIf(Condition<T> condition, Applier<T> applier)
    {
        for (T element : this)
            if (condition.isVerified(element))
                applier.apply(element);
        return this;
    }

    public boolean removeIf(Condition<T> condition, Applier<T> applier)
    {
        boolean modified = false;
        Iterator<T> it = iterator();
        while(it.hasNext())
        {
            T element = it.next();
            if(condition.isVerified(element))
            {
                it.remove();
                applier.apply(element);
                modified = true;
            }
        }
        return modified;
    }



    public boolean has(T item)
    {
        for (T candidate : this)
            if (candidate.equals(item))
                return true;
        return false;
    }

    public boolean hasnt(T item)
    {
        return !has(item);
    }

    public T addIf(boolean cond, T data)
    {
        return cond ? add3(data) : null;
    }

    public T add3(T data)
    {
        this.add(data);
        return data;
    }

    public T addAfter(T data, T anchor)
    {
        int index = this.indexOf(anchor);
        if (index >= 0)
            this.add(index + 1, data);
        else
            this.add(data);
        return data;
    }

    public List3<T> addIter3(Iterable<T>... data)
    {
        for (Iterable<T> iter : data)
            for (T t : iter)
                this.add(t);
        return this;
    }

    public List3<T> addAll3(T... data)
    {
        this.add(data);
        return this;
    }

    public List3<T> add(T... data)
    {
        if (data.length == 1)
            this.add(data[0]);
        else
            this.addAll(Arrays.asList(data));
        return this;
    }

    public List3<T> addMany(double times, T... data)
    {
        return this.addMany((int) times, data);
    }

    public List3<T> addMany(int times, T... data)
    {
        for (int i = 0; i < times; i++)
            this.add(data);
        return this;
    }

    public List3<T> setNonNull(T... data)
    {
        this.clear();
        return addNonNull(data);
    }

    public List3<T> addNonNull(T... data)
    {
        for (T t : data)
            if (t != null)
                this.add(t);
        return this;
    }

    public void resetAll(T... data)
    {
        this.clear();
        for (T d : data)
            this.add(d);
    }

    public void setAll(Collection<? extends T> data)
    {
        this.clear();
        this.addAll(data);
    }

    public List3<T> remove(T... data)
    {
        for (T o : data)
            super.remove(o);
        return this;
    }

    public void pollEnds()
    {
        this.pollFirst();
        this.pollLast();
    }

    public T third()
    {
        return this.size() > 2 ? this.get(2) : null;
    }

    public T third(T def)
    {
        return this.size() > 2 ? this.get(2) : def;
    }

    public T second()
    {
        return this.size() > 1 ? this.get(1) : null;
    }

    public T second(T def)
    {
        return this.size() > 1 ? this.get(1) : def;
    }

    public T first()
    {
        return isEmpty() ? null : getFirst();
    }

    public T first(T def)
    {
        return isEmpty() ? def : getFirst();
    }

    public T last()
    {
        return isEmpty() ? null : getLast();
    }

    public T last(T def)
    {
        return isEmpty() ? def : getLast();
    }

    public List3<T> firsts(int size)
    {
        List3<T> list = new List3<>();
        for (T obj : this)
            if (list.size() == size)
                return list;
            else
                list.add(obj);
        return list;
    }

    public List3<T> lasts(int size)
    {
        List3<T> list = new List3<>();
        for (T obj : back())
            if (list.size() == size)
                return list;
            else
                list.add(obj);
        return list;
    }

    public T penultimate()
    {
        return this.size() > 1 ? this.get(this.size() - 2) : null;
    }

    public T penultimate(T def)
    {
        return this.size() > 1 ? this.get(this.size() - 2) : def;
    }

    public T antepenultimate()
    {
        return this.size() > 2 ? this.get(this.size() - 3) : null;
    }

    public T antepenultimate(T def)
    {
        return this.size() > 2 ? this.get(this.size() - 3) : def;
    }

    // as usual, begin included, end not included
    public List3<T> trim(int begin, int end)
    {
        while (this.size() > end)
            this.removeLast();
        for (int i = 0; i < begin; i++)
            this.removeFirst();
        return this;
    }

    public List3<T> trimSize(int size)
    {
        while (size() > size)
            removeLast();
        return this;
    }

    public List3<T> trimSize(int size, boolean removeLast)
    {
        if (removeLast)
            return trimSize(size);
        while (size() > size)
            removeFirst();
        return this;
    }

    public boolean isSizeOne()
    {
        return this.size() == 1;
    }

    public boolean isSize(int... sizes)
    {
        for (int size : sizes)
            if (size() == size)
                return true;
        return false;
    }

    public boolean isPopulated()
    {
        return !this.isEmpty();
    }

    public Iterable<T> back()
    {
        return descending();
    }

    public Iterable<T> descending()
    {
        return () -> descendingIterator();
    }

    public Iterable<T> iterable(boolean forward)
    {
        return forward ? () -> iterator() : () -> descendingIterator();
    }

    public List3<T> reverse()
    {
        Collections.reverse(this);
        return this;
    }

    public List3<T> resize(int maxSize)
    {
        return this.resize(maxSize, false);
    }

    public List3<T> resize(int maxSize, boolean removeFirst)
    {
        while (size() > maxSize && !isEmpty())
            if (removeFirst)
                removeFirst();
            else
                removeLast();
        return this;
    }

    public List3<T> addLast(T last, int maxSize)
    {
        this.addLast(last);
        if (maxSize > 0 && size() > maxSize)
            this.removeFirst();
        return this;
    }

    public List3<T> addFirst(T first, int maxSize)
    {
        this.addFirst(first);
        if (maxSize > 0 && size() > maxSize)
            this.removeLast();
        return this;
    }

    public Iter<T> iter()
    {
        return new Iter(this);
    }

    public String[] toStrings()
    {
        return toStrings(true);
    }

    public String[] toStrings(boolean ascendingOrder)
    {
        String[] array = new String[this.size()];
        if (ascendingOrder)
        {
            int i = 0;
            for (T o : this)
                array[i++] = o.toString();
        } else
        {
            int i = array.length - 1;
            for (T o : this)
                array[i--] = o.toString();
        }
        return array;
    }

    public String toString(String separator)
    {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        int size = this.size();
        for (T t : this)
            sb.append(t.toString()).append(++counter < size ? separator : "");
        return sb.toString();
    }
}
