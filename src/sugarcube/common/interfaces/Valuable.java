package sugarcube.common.interfaces;

public interface Valuable
{
    String value(String key);

    float real(String key);

    int integer(String key);

    boolean bool(String key);
}
