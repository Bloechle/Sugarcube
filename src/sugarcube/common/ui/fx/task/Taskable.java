package sugarcube.common.ui.fx.task;

public interface Taskable<I, O>
{
    O taskWork(I input);

    void taskDone(O ouput);
}
