package sugarcube.common.system.process;

import sugarcube.common.system.util.Sys;

public class ProcessThreadPool
{
    public final ProcessThread[] threads;

    public ProcessThreadPool(int size)
    {
        threads = new ProcessThread[size];
        for (int i = 0; i < threads.length; i++)
            threads[i] = new ProcessThread(i, ProcessThread.DEFAULT_SLEEP_MILLIS);
    }

    public void execute(int threadIndex, Runnable runnable)
    {
        if (threadIndex >= 0 && threadIndex < threads.length)
            threads[threadIndex].execute(runnable);
        else
            runnable.run();
    }

    public int size()
    {
        return threads.length;
    }

    public boolean isEmpty()
    {
        return threads.length == 0;
    }

    public void waitUntilProcessingDone()
    {
        boolean doLoop;
        do
        {
            doLoop = false;
            for (int i = 0; i < threads.length; i++)
                if (!threads[i].isDone())
                {
                    doLoop = true;
                    break;
                }
            Sys.Sleep(20);
        } while (doLoop);
    }

    public void kill()
    {
        for (int i = 0; i < threads.length; i++)
            threads[i].kill();
    }
}
