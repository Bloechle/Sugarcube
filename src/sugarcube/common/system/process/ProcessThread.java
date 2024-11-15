package sugarcube.common.system.process;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.util.Sys;

public class ProcessThread implements Runnable
{
    public static final int DEFAULT_SLEEP_MILLIS = 5;
    private Thread thread;
    private Runnable runnable;
    private int pause_ms;
    private boolean isProcessing = false;
    private boolean doKill = false;
    private long lastProcessingTime_ms = 0;
    public final int index;


    public ProcessThread(int pauseInMillis)
    {
        this(-1, pauseInMillis);
    }
    public ProcessThread(int index, int pauseInMillis)
    {
        this.index = index;
        pause_ms = pauseInMillis;
        thread = new Thread(this, "ProcessThread");
        thread.setDaemon(false);
        thread.setPriority(8);
        thread.start();
    }

    public void kill()
    {
        doKill = true;
    }

    public void execute(Runnable runnable)
    {
        this.runnable = runnable;
    }

    public void run()
    {
        while (!doKill)
        {
            try
            {
                Runnable currentRunnable = runnable;
                if (currentRunnable != null)
                {
                    isProcessing = true;
                    runnable = null;
                    long time = Sys.Millis();
                    currentRunnable.run();
                    lastProcessingTime_ms = Sys.Millis() - time;
                    isProcessing = false;
                }
                Sys.Sleep(pause_ms);
            } catch (Exception e)
            {
                Log.warn(this, ".run - exception: " + e.getMessage());
                e.printStackTrace();
            }

        }
        isProcessing = false;
    }

    public boolean hasRunnable()
    {
        return runnable != null;
    }

    public boolean isProcessing()
    {
        return isProcessing;
    }

    public boolean isDone()
    {
        return runnable == null && isProcessing == false;
    }

    public long getLastProcessingTime()
    {
        return lastProcessingTime_ms;
    }
}
