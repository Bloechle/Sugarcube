package sugarcube.common.data;

import sugarcube.common.interfaces.Booleable;

public class Bool implements Booleable
{
    private boolean bool;

    public Bool()
    {
        set(false);
    }

    public Bool(boolean bool)
    {
        set(bool);
    }

    public Bool(Booleable booleable)
    {
        set(booleable.isTrue());
    }

    public void set(boolean bool)
    {
        this.bool = bool;
    }

    public void setBool(boolean bool)
    {
        set(bool);
    }

    public Bool on()
    {
        setTrue();
        return this;
    }

    public Bool off()
    {
        setFalse();
        return this;
    }

    public void setTrue()
    {
        set(true);
    }

    public void setFalse()
    {
        set(false);
    }

    @Override
    public boolean isTrue()
    {
        return bool;
    }

    public boolean isFalse()
    {
        return !bool;
    }

    public synchronized boolean doConsume()
    {
        boolean oldBool = bool;
        bool = false;
        return oldBool;
    }

    public static boolean Equals(Boolean a, Boolean b)
    {
        return a == b || a != null && b != null && a.booleanValue() == b.booleanValue();
    }

    public static int Int(boolean b)
    {
        return b ? 1 : 0;
    }

    public static Bool False()
    {
        return new Bool(false);
    }

    public static Bool True()
    {
        return new Bool(true);
    }
}
