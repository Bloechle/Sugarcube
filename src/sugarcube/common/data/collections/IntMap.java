package sugarcube.common.data.collections;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntMap<T> extends LinkedHashMap<Integer, T> implements Iterable<Map.Entry<Integer, T>>
{
    //  private static transient Set<Integer> NOT_FOUND = new HashSet<>();
    private final String name;
    private T def;

    public IntMap()
    {
        this("noname");
    }

    public IntMap(String name)
    {
        this.name = name;
        this.def = null;
    }

    public IntMap(String name, T defaultValue)
    {
        this.name = name;
        this.def = defaultValue;
    }

    public boolean hasDefault()
    {
        return this.def != null;
    }

    public T def()
    {
        return this.def;
    }

    public String name()
    {
        return name;
    }

    public void setDefault(T def)
    {
        this.def = def;
    }

    public int index(Integer key)
    {
        int index = 0;
        for (Integer s : this.keySet())
        {
            if (s.equals(key))
                return index;
            index++;
        }
        return -1;
    }

    public T first()
    {
        Iterator<T> it = this.values().iterator();
        return it.hasNext() ? it.next() : def;
    }

    public T get(Integer key, T def)
    {
        return containsKey(key) ? super.get(key) : def;
    }

    public T get(Integer key)
    {
        if (containsKey(key))
            return super.get(key);
        else
        {
//      if (this.def!=null && !NOT_FOUND.contains(key))
//      {
//        NOT_FOUND.add(key);
//        Log.info(this, ".get - key not found in "+name+": key="+key);
//      }
            return def;
        }
    }

    public boolean containsAllKeys(int... keys)
    {
        for (int key : keys)
            if (!contains(key))
                return false;
        return true;
    }

    public boolean has(Integer key)
    {
        return containsKey(key);
    }

    public boolean contains(Integer key)
    {
        return containsKey(key);
    }

    public IntMap update(Map<? extends Integer, ? extends T> map)
    {
        this.putAll(map);
        return this;
    }

    public IntMap<T> copy()
    {
        IntMap copy = new IntMap<T>(name, def);
        copy.update(this);
        return copy;
    }

    @Override
    public Iterator<Map.Entry<Integer, T>> iterator()
    {
        return this.entrySet().iterator();
    }

    @Override
    public String toString()
    {
        boolean isUnicode = this.name.toLowerCase().endsWith("unicode");
        StringBuilder sb = new StringBuilder();
        sb.append("\nIntMap[").append(name).append("]\n");
        sb.append("def=").append(this.def).append("\n");
        for (Map.Entry<Integer, T> e : IntMap.this.entrySet())
            sb.append(e.getKey()).append(" » ").append(isUnicode ? ((char) (int) (Integer) e.getValue()) + " " + e.getValue().toString() : e.getValue().toString()).append("\n");
        return sb.toString();
    }
}
