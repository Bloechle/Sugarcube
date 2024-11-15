package sugarcube.common.data.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;

public class StringList extends List3<String>
{
  public StringList(Iterable<String> iterable)
  {
    this(iterable.iterator());
  }

  public StringList(Iterator<String> iterator)
  {
    while (iterator.hasNext())
      this.add(iterator.next());
  }

  public StringList(String... data)
  {
    this.addAll(Arrays.asList(data));
  }

  @Override
  public StringList addAll3(String... data)
  {
    this.add(data);
    return this;
  }

  public String[] array()
  {
    return this.toArray(new String[0]);
  }

  public StringSet set()
  {
    return new StringSet(this);
  }

  @Override
  public StringList reverse()
  {
    super.reverse();
    return this;
  }

  public StringList shuffle()
  {
    Collections.shuffle(this);
    return this;
  }

  public StringList prefix(String prefix)
  {
    StringList prefixed = new StringList();
    for (String data : this)
      prefixed.add(prefix + data);
    return prefixed;
  }

  public StringList postfix(String postfix)
  {
    StringList postfixed = new StringList();
    for (String data : this)
      postfixed.add(data + postfix);
    return postfixed;
  }

  public StringList copy()
  {
    return new StringList(this);
  }

  public StringList regexKeep(String regex)
  {
    Pattern pat = Pattern.compile(regex);
    Iterator<String> it = this.iterator();
    while (it.hasNext())
      if (!pat.matcher(it.next()).find())
        it.remove();
    return this;
  }

  public StringList regexRemove(String regex)
  {
    Pattern pat = Pattern.compile(regex);
    Iterator<String> it = this.iterator();
    while (it.hasNext())
      if (pat.matcher(it.next()).find())
        it.remove();
    return this;
  }

  public String concat(String sep)
  {
    Stringer sg = new Stringer();
    int size = this.size();
    int index = 0;
    for (String s : this)
      sg.append(s).append(++index < size ? sep : "");
    return sg.toString();
  }

  public StringList sort()
  {
    Collections.sort(this);
    return this;
  }

  public StringList sortReverse()
  {
    Collections.sort(this, (a, b) -> b.compareTo(a));
    return this;
  }

  public StringList trimFirsts(boolean trimString)
  {
    while(isPopulated() && Str.IsVoid(trimString ? first().trim() : first()))
        removeFirst();
    return this;
  }
  
  public StringList trimLasts(boolean trimString)
  {
    while(isPopulated() && Str.IsVoid(trimString ? last().trim() : last()))
        removeLast();
    return this;
  }
  
  public String string(String separator)
  {
    StringBuilder sb = new StringBuilder();
    for(String s: this)
    {
      sb.append(s);
      sb.append(separator);
    }
    int size = sb.length();
    if(size>separator.length())
      sb.delete(size-separator.length(),size);
    return sb.toString();
  }
  
}
