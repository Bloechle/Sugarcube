package sugarcube.common.interfaces;

public interface Condition<T>
{
    boolean isVerified(T o);
}
