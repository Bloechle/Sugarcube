package sugarcube.common.data.collections;

public class IntOccurrences extends Occurrences<Integer>
{
  public IntOccurrences(Integer... objects)
  {
    super(objects);
  }

  public IntOccurrences(Iterable<Integer> iterable)
  {
    super(iterable);
  }

  public IntOccurrences(Integer def, Iterable<Integer> iterable)
  {
    super(def, iterable);
  }
}
