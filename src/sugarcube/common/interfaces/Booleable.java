package sugarcube.common.interfaces;

public interface Booleable
{
    Booleable TRUE = () -> true;
    Booleable FALSE = () -> false;

    boolean isTrue();
}
