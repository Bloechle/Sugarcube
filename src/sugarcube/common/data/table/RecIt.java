package sugarcube.common.data.table;

import java.util.Iterator;

public class RecIt implements Iterator<Record>
{
    private Iterator<Record> iterator;

    public RecIt(Iterator<Record> iterator)
    {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext()
    {
        return iterator.hasNext();
    }

    @Override
    public Record next()
    {
        return iterator.next();
    }

    @Override
    public void remove()
    {
        iterator.remove();
    }

    public Record nextOne()
    {
        return hasNext() ? next() : null;
    }
}
