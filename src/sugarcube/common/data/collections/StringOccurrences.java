package sugarcube.common.data.collections;

public class StringOccurrences extends Occurrences<String>
{
  public StringOccurrences(String... objects)
  {
    super(objects);
  }

  public StringOccurrences(Iterable<String> iterable)
  {
    super(iterable);
  }

  public StringOccurrences(String defaultValue, Iterable<String> iterable)
  {
    super(defaultValue, iterable);
  }
}
