package sugarcube.common.data.collections;

import java.util.Iterator;
import java.util.function.Consumer;

public class Iter<T> implements Iterator<T>, Iterable<T>
{
    private final Iterable<T> iterable;
    private Iterator<T> it;
    private T value;
    private int index = -1;

    public Iter(Iterable<T> iterable)
    {
        this.iterable = iterable;
        this.reset();
    }

    public void reset()
    {
        if (it == null || index > -1)
            this.it = iterable.iterator();
        this.index = -1;
        this.value = null;
    }

    public T get()
    {
        return value;
    }

    public T getAndRemove()
    {
        remove();
        return value;
    }

    public T value()
    {
        return value;
    }

    @Override
    public boolean hasNext()
    {
        return it.hasNext();
    }

    @Override
    public T next()
    {
        ++index;
        return value = hasNext() ? it.next() : null;
    }

    public boolean getNext()
    {
        return next() != null;
    }

    public int index()
    {
        return index;
    }

    public void remove()
    {
        it.remove();
        --index;
    }

    public void forEachRemaining(Consumer<? super T> action)
    {
        it.forEachRemaining(action);
    }

    @Override
    public Iterator<T> iterator()
    {
        return it;
    }
}
