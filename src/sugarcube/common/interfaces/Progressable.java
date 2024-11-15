package sugarcube.common.interfaces;

public interface Progressable
{
    int STATE_CANCELLED = -1;
    int STATE_READY = 0;
    int STATE_COMPLETED = 1;

    float progress();

    String progressName();

    String progressDescription();

    int progressState();

    default boolean isProgressState(int state)
    {
        return state == progressState();
    }

    default boolean isProgressComplete()
    {
        return isProgressState(STATE_COMPLETED);
    }
}
