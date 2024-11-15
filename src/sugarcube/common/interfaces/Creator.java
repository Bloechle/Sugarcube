package sugarcube.common.interfaces;

public interface Creator<I, O>
{
    O create(I input);
}
