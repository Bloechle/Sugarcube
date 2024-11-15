package sugarcube.common.data.collections;

import java.util.Map;

public class MapInt<V> extends Map3<Integer, V>
{
    public MapInt()
    {
    }

    public MapInt(int initialCapacity)
    {
        super(initialCapacity);
    }

    public MapInt(Map<Integer, V> map)
    {
        this.putAll(map);
    }

    public MapInt(Integer[] keys, V[] values)
    {
        for (int i = 0; i < keys.length && i < values.length; i++)
            this.put(keys[i], values[i]);
    }

    public IntSet keys()
    {
        IntSet set = new IntSet();
        set.addAll(this.keySet());
        return set;
    }

    @Override
    public List3<Integer> keyList()
    {
        List3<Integer> list = new List3<Integer>();
        list.addAll(this.keySet());
        return list;
    }

}
