package sugarcube.common.data.collections;

public class StringMapList<V> extends MapList<String, V>
{
  public StringMapList()
  {
  }
  
  public void add(V value)
  {    
    this.add(value.toString(),  value);
  }
  
  public String[] keys()
  {
    return this.keySet().toArray(new String[0]);
  }
  
 
}
