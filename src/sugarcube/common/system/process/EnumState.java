package sugarcube.common.system.process;

import sugarcube.common.data.collections.Str;
import sugarcube.common.data.xml.Nb;

public class EnumState
{
    public String[] values;
    public String value;

    public EnumState(String... values)
    {
        this.values = values;
        reset();
    }

    public void reset()
    {
        this.value = values == null || values.length == 0 ? "" : values[0];
    }

    public int intValue()
    {
        return Nb.Int(value, 0);
    }

    public float floatValue()
    {
        return Nb.Float(value, 0);
    }

    public boolean isDefault()
    {
        return equals(get(0));
    }

    public boolean eq(int index)
    {
        return equals(get(index));
    }

    public boolean eqOne(int... indexes)
    {
        for( int index : indexes)
        if (eq(index))
            return true;
        return false;
    }

    public boolean isIndexInRange(int index)
    {
        return index >= 0 && index < values.length;
    }

    public int index()
    {
       return index(value);
    }

    public int index(String value)
    {
        for (int i = 0; i < values.length; i++)
            if (Str.Eq(values[i], value))
                return i;
        return -1;
    }

    public int index(int index)
    {
        return isIndexInRange(index) ? index : -1;
    }

    public String get(int index)
    {
        return isIndexInRange(index) ? values[index] : value;
    }

    public boolean equals(String name)
    {
        return Str.Eq(this.value, name);
    }

    public EnumState set(String name)
    {
        this.value = name;
        return this;
    }

    public EnumState set(int index)
    {
        if (isIndexInRange(index))
            value = values[index];
        return this;
    }

    public String toString()
    {
        return value;
    }


    public EnumState advance()
    {
        value = Str.CircularNext(value, values);
        return this;
    }

    public String next()
    {
        return advance().value;
    }

    public EnumState selectIndex(int index)
    {
        value  = values[index];
        return this;
    }

    public boolean hasSelection()
    {
        return Str.HasData(value);
    }

    public String select(int skipOutLeftCenterRight)
    {
        return value = isIndexInRange(skipOutLeftCenterRight) ? values[skipOutLeftCenterRight] : "";
    }

    public String label()
    {
        return "- "+value+" -";
    }


}
