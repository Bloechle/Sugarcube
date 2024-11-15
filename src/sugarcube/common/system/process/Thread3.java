package sugarcube.common.system.process;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.util.Sys;
import sugarcube.common.interfaces.Closable;
import sugarcube.common.interfaces.DoneListener;
import sugarcube.common.interfaces.Tester;

public class Thread3 implements Runnable, Closable
{
    private Thread thread = null;
    private Runnable runnable;
    private boolean doDestroy = false;
    private boolean doProcess = false;
    private boolean isProcessing = false;
    private long pause = 500;
    private String name = this.getClass().getSimpleName();
    protected Object data = null;
    protected long startTimestamp = Sys.Millis();
    protected int taskCounter = 0;

    public Thread3()
    {

    }

    public Thread3(Runnable runnable)
    {
        this.runnable = runnable;
    }

    public Thread3(long pause)
    {
        this(pause, null);
    }

    public Thread3(long pause, Runnable runnable)
    {
        this.pause = pause;
        this.runnable = runnable;
    }

    public void init()
    {

    }

    @Override
    public void run()
    {
        try
        {
            this.init();
            do
            {
                if (doProcess())
                {
                    try
                    {
                        isProcessing = true;
                        doTask();
                        isProcessing = false;
                    } catch (Exception e)
                    {
                        Log.error(this, ".run - exception thrown during thread processing loop: " + e.getMessage());
                        e.printStackTrace();
                    }
                    this.taskCounter++;
                }
                pause();
            } while (!doDestroy);
        } catch (Exception e)
        {
            Log.error(this, ".run - exception thrown during thread processing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Thread3 task(Runnable runnable)
    {
        this.runnable = runnable;
        return this;
    }

    public void doTask()
    {
        if (runnable != null)
            runnable.run();
    }

    public Thread3 restart()
    {
        this.doDestroy = false;
        return this.start();
    }

    public void start(Object data)
    {
        this.data = data;
        this.start();
    }

    public synchronized Thread3 start()
    {
        doProcess = !doDestroy;
        if (doProcess && thread == null)
        {
            /**
             * Caution: public void start() causes this thread to begin execution; the
             * Java Virtual Machine calls the run method of this thread. The result is
             * that two threads are running concurrently: the current thread (which
             * returns from the call to the start method) and the other thread (which
             * executes its run method). It is never legal to start a thread more than
             * once. In particular, a thread may not be restarted once it has
             * completed execution.
             */
            this.thread = new Thread(this, name);
            this.thread.setPriority(8); // 9 proved to be too much (blocking)
            this.thread.start(); // can be called only once
        }
        return this;
    }

    public void stop()
    {
        this.doProcess = false;
    }

    public boolean doProcess()
    {
        return doProcess;
    }

    public boolean isProcessing()
    {
        return isProcessing;
    }

    public boolean isDown()
    {
        return thread == null || doDestroy;
    }

    public Thread thread()
    {
        return this.thread;
    }

    public Thread3 pause()
    {
        if (pause > 0)
            Sys.Sleep(pause);
        return this;
    }

    public Thread3 noPause()
    {
        this.pause = 0;
        return this;
    }

    public Thread3 pauseMillis(long millis)
    {
        this.pause = millis;
        return this;
    }

    public Thread3 pauseSeconds(double seconds)
    {
        return pauseMillis(Math.round(seconds * 1000));
    }

    public Thread3 pauseMinutes(double minutes)
    {
        return pauseSeconds(minutes * 60);
    }

    @Override
    public void close()
    {
        this.doProcess = false;
        this.doDestroy = true;
        this.thread = null;
    }

    public static Thread Run(Runnable run)
    {
        return Run(false, run, null);
    }

    public static Thread Daemon(Runnable run)
    {
        return Run(true, run, null);
    }

    public static Thread Daemon(Runnable run, DoneListener doneListener)
    {
        return Run(true, run, doneListener);
    }

    public static Thread Run(boolean daemon, Runnable run)
    {
        return Run(daemon, run, null);
    }

    public static Thread Run(boolean daemon, Runnable run, DoneListener doneListener)
    {
        Runnable runAndDone = run;
        if (doneListener != null)
        {
            runAndDone = () ->
            {
                run.run();
                doneListener.done();
            };
        }

        Thread thread = new Thread(runAndDone);
        thread.setDaemon(daemon);
        thread.start();
        return thread;
    }

    public static Thread LoopingDaemonThread(String name, Tester run)
    {
        return LoopingThread(true, name, run);
    }

    public static Thread LoopingThread(String name, Tester run)
    {
        return LoopingThread(false, name, run);
    }

    public static Thread LoopingThread(boolean daemon, String name, Tester run)
    {
        Thread thread;
        try
        {
            thread = new Thread(() ->
            {
                while (run.condition())
                    if (!daemon && Thread.currentThread().isInterrupted())
                        break;
            }, name);
            thread.setDaemon(daemon);
            thread.start();
        } catch (Exception e)
        {
            e.printStackTrace();
            thread = null;
        }
        return thread;
    }

    public static void Interrupt(Thread... threads)
    {
        for (Thread thread : threads)
            if (thread != null)
                try
                {
                    thread.interrupt();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
    }

    public static void InterruptAndJoin(Thread... threads)
    {
        for (Thread thread : threads)
            if (thread != null)
                try
                {
                    thread.interrupt();
                    thread.join();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
    }

    public static String Current()
    {
        return Thread.currentThread().getName();
    }


    public static Thread Start(String name, Runnable run)
    {
        Thread thread = new Thread(() -> run.run(), name);
        thread.start();
        return thread;
    }

}
