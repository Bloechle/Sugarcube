package sugarcube.common.data.collections;

public class Counter implements Comparable<Counter>
{
  private int counter = 0;

  public Counter()
  {
  }

  public Counter(int init)
  {
    this.counter = init;
  }

  public int inc()
  {
    return ++counter;
  }
  
  public int inc(int value)
  {
    return counter+=value;
  }

  public void reset()
  {
    this.counter = 0;
  }

  public int value()
  {
    return counter;
  }

  public String stringValue()
  {
    return "" + counter;
  }

  public String stringValue(int size)
  {
    String c = stringValue();
    while (c.length() < size)
      c = "0" + c;
    return c;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || this.getClass() != o.getClass())
      return false;
    return counter == ((Counter) o).counter;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 59 * hash + this.counter;
    return hash;
  }

  @Override
  public int compareTo(Counter o)
  {
    return counter < o.counter ? -1 : counter > o.counter ? 1 : 0;
  }
  
  @Override
  public String toString()
  {
    return ""+counter;
  }
}
