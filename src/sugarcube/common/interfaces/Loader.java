package sugarcube.common.interfaces;

public interface Loader<I, O> extends Unjammable
{
    O load(I input);
}
