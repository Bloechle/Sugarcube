package sugarcube.common.interfaces;

public interface Visitor<T>
{
    boolean visit(T o);
}
