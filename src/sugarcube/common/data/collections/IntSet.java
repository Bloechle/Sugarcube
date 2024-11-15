package sugarcube.common.data.collections;


import java.util.Collection;

public class IntSet extends Set3<Integer>
{

    public IntSet(int... data)
    {
        if (data != null)
            for (Integer d : data)
                this.add(d);
    }

    public IntSet(Collection<Integer> data)
    {
        if (data != null)
            for (Integer d : data)
                this.add(d);
    }

    public int[] array()
    {
        int[] array = new int[this.size()];
        int index = 0;
        for (Integer i : this)
            array[index++] = i;
        return array;
    }

    public IntSet addInts(int... values)
    {
        for (int value : values)
            this.add(value);
        return this;
    }

    public IntSet addRound(double value)
    {
        this.add((int) Math.round(value));
        return this;
    }

    @Override
    public Ints list()
    {
        return ints();
    }

    public Ints ints()
    {
        return new Ints(this);
    }

}
