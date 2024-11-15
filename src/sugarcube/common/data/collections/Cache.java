package sugarcube.common.data.collections;

import sugarcube.common.system.util.Sys;

import java.util.LinkedHashMap;

public class Cache<K, V> extends LinkedHashMap<K, V>
{
    private String name;
    private int maxSize;
    private long timestamp;
    private long memMillis;

    public Cache()
    {
        this("cache");
    }

    public Cache(String name)
    {
        this(name, -1, -1);
    }

    public Cache(int maxSize)
    {
        this("cache", maxSize, -1);
    }

    public Cache(String name, int maxSize)
    {
        this(name, maxSize, -1);
    }

    public Cache(String name, int maxSize, float memSeconds)
    {
        this.name = name;
        this.maxSize = maxSize;
        this.memMillis = memSeconds <= 0 ? -1 : (long) (memSeconds * 1000);
        this.timestamp = Sys.Millis();
    }

    private void checkCleaner()
    {
        if (memMillis > 0 && Sys.Millis() - timestamp > memMillis)
        {
            timestamp = Sys.Millis();
            this.clear();
        }
    }

    public int maxSize()
    {
        return maxSize;
    }

    public void setUnlimited()
    {
        this.maxSize = -1;
    }

    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
    }

    public String name()
    {
        return this.name;
    }
//
//    public V need(K key, V def)
//    {
//        return has(key) ? get(key) : put(key, def);
//    }

    public V get(K key, V def)
    {
        V value = has(key) ? get(key) : def;
        checkCleaner();
        return value;
    }

    public boolean has(Object key)
    {
        return this.containsKey(key);
    }

    @Override
    public V put(K key, V value)
    {
        while (maxSize > 0 && size() > maxSize)
            removeFirst();
        super.put(key, value);
        return value;
    }

    public boolean removeFirst()
    {
        if (size() > 0)
        {
            remove(keySet().iterator().next());
            return true;
        }
        return false;
    }

    public List3<V> list()
    {
        return new List3<V>(values());
    }

}
