package sugarcube.common.system.process;

import sugarcube.common.system.util.Sys;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPool
{
    private ThreadPoolExecutor pool = null;
    private int nbOfThreads = 3;

    public ThreadPool()
    {

    }

    public ThreadPool(int nbOfThreads)
    {
        this.nbOfThreads = nbOfThreads;
    }

    public ThreadPool pool()
    {
        if (pool == null)
            pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbOfThreads);
        return this;
    }

    public ThreadPool threads(int nbOfThreads)
    {
        pool();
        this.nbOfThreads = nbOfThreads;
        if (pool.getMaximumPoolSize() != nbOfThreads)
            pool.setMaximumPoolSize(nbOfThreads);
        return this;
    }

    public boolean fedUp()
    {
        pool();
        return pool.getActiveCount() == pool.getMaximumPoolSize();
    }

    public boolean isFreeOrWait(int millis)
    {
        if (isFree())
            return true;
        Sys.Sleep(millis);
        return false;
    }

    public boolean isFree()
    {
        return !fedUp();
    }

    public void execute(Runnable runnable)
    {
        pool();
        pool.execute(runnable);
    }

    public void shutdown()
    {
        if (pool != null)
            pool.shutdown();
    }

    public void shutdownNow()
    {
        if (pool != null)
            pool.shutdownNow();
    }


}
