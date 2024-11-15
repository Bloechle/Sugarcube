package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.Empty;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.StringSet;

import java.util.Iterator;

public class NodeIt<T extends OCDPaintable> implements Iterator<T>, Iterable<T>
{
  protected List3<Iterator<? extends OCDPaintable>> stack = new List3<Iterator<? extends OCDPaintable>>();
  protected Iterator<? extends OCDPaintable> it = null;
  protected StringSet tygs = new StringSet();
  protected T starting = null;
  protected T ending = null;
  protected T node = null;
  protected boolean hasNext = true;

  public NodeIt(OCDGroup root)
  {
    this.it = root.iterator();
  }

  public NodeIt(OCDGroup root, T start, T end)
  {
    this(root);

    // Log.debug(this, " - start=" + (start == null ? "null" : start.tag) +
    // ", end=" + (end == null ? "null" : end.tag));
    if (start == null && end == null)
      this.it = new Empty.EmptyIterator<>();
    else
    {
      T next = null;
      while (hasNext())
      {
        next = next();
        if (start == null)
          start = next;
        if (next == start || next == end)
        {
          starting = next;
          ending = (next == start ? end : start);
          break;
        }
      }
      node = next;// otherwise node is consumed since next() has already been
                  // called on it
    }
    // Log.debug(this, " - ending: "+(ending==null? "null" :
    // ending.asText().string())+", hasNext="+it.hasNext());
  }

  public NodeIt<T> filter(String... tagOrType)
  {
    this.tygs.setAll(tagOrType);
    return this;
  }

  public T starting()
  {
    return starting;
  }

  public T ending()
  {
    return ending;
  }

  public NodeIt<T> skip()
  {
    if (hasNext())
      this.next();
    return this;
  }

  @Override
  public boolean hasNext()
  {
    if (hasNext)
    {
      if (node == null)
        node = (T) preview();
      if (node == null)
        return hasNext = false;
      if (node == ending)
        return !(hasNext = false);// next time will be false because current
                                  // node has to be read by next
      return hasNext = (node != null && node != ending);
    }
    return false;
  }

  @Override
  public T next()
  {
    // Log.debug(this, ".next - " + node);
    if (node == null)
      hasNext();
    T next = node;
    node = null;
    return next;
  }

  @Override
  public void remove()
  {
    this.it.remove();
  }

  @Override
  public Iterator<T> iterator()
  {
    return this;
  }

  public OCDPaintable preview()
  {
    if (it.hasNext())
    {
      OCDPaintable next = it.next();
      if (next.is(OCDGroup.TAG))
      {
        OCDGroup g = next.asGroup();
        if (tygs.has(g.type))
          return next;
        else
        {
          stack.add(it);
          it = g.nodes.iterator();
          return preview();
        }
      } else if (tygs.isEmpty() || tygs.contains(next.tag))
        return next;
      else {
//        Log.debug(this, ".iterator - node="+next);
        return preview();
      }
    } else if (!stack.isEmpty())
    {
      // Log.debug(this, ".preview - stack pop");
      it = stack.removeLast();
      return preview();
    }
    return null;
  }

  public static String s(OCDNode node)
  {
    return node == null ? "null" : node.is(OCDText.TAG) ? ((OCDText) node).string() : node.is(OCDGroup.TAG) ? ((OCDGroup) node).type : node.tag;
  }   

  public T node(boolean forward, T anchor, T def)
  {
    return forward ? next(anchor, def) : previous(anchor, def);
  }

  public T previous(T anchor, T def)
  {
    T prev = def;
    if (anchor != null)
      for (T curr : this)
        if (curr == anchor)
          return prev;
        else
          prev = curr;
    return def;
  }

  public T next(T anchor, T def)
  {
    if (anchor != null)
      while (hasNext())
        if (next() == anchor)
          return hasNext() ? next() : def; // returns next if exists
    return def;
  }
}
