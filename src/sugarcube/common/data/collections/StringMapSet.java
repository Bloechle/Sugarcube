package sugarcube.common.data.collections;

public class StringMapSet<V> extends MapSet<String, V>
{

  public String[] keys()
  {
    return this.keySet().toArray(new String[0]);
  }
}
