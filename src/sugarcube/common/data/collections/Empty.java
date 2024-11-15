
package sugarcube.common.data.collections;

import java.util.Iterator;

public class Empty
{
  public static class EmptyIterator<T> implements Iterator<T>, Iterable<T>
  {
    @Override
    public boolean hasNext()
    {
      return false;
    }

    @Override
    public T next()
    {
      return null;
    }

    @Override
    public void remove()
    {      
    }

    @Override
    public Iterator<T> iterator()
    {
      return this;
    }
  }
}
