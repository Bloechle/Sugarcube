package sugarcube.common.data.collections;

public class ObjectSet extends Set3<Object>
{

  public ObjectSet(Object... data)
  {
    for (Object d : data)
      this.add(d);
  }


}
