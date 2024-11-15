package sugarcube.common.interfaces;

public interface Keepable<T>
{
    boolean doKeep(T o);
}
