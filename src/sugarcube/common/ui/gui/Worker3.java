package sugarcube.common.ui.gui;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Set3;

import javax.swing.*;
import java.util.List;

public class Worker3<T> extends SwingWorker<Worker3.Processable, Void>
{
    public interface Listener<T>
    {
        void done(Worker3 worker, T... data);
    }

    public interface Processable<T> extends Listener<T>
    {
        void process(Worker3 worker, T... data);
    }

    private final Set3<Listener<T>> listeners = new Set3<>();
    private Processable<T> processable;
    private T[] data;

    public Worker3(Processable<T> processable, T... data)
    {
        this(new Listener[0], processable, data);
    }

    public Worker3(Listener<T>[] listeners, Processable<T> processable, T... data)
    {
        this.processable = processable;
        this.listeners.addAll(listeners);
        this.data = data;
        this.execute();
    }

    public Worker3(Processable<T> processable)
    {
        this(processable, (T[]) null);
    }

    public Processable<T> processable()
    {
        return processable;
    }

    public T[] data()
    {
        return data;
    }

    public Set3<Listener<T>> listeners()
    {
        return this.listeners;
    }

    public Listener[] listenersArray()
    {
        Listener[] array = new Listener[listeners.size()];
        int i = 0;
        for (Listener listener : listeners)
            array[i++] = listener;
        return array;
    }

    @Override
    public void process(List<Void> list)
    {
    }

    @Override
    protected Processable<T> doInBackground() throws Exception
    {
        try
        {
            this.processable.process(this, data);
        } catch (Exception ex)
        {
            Log.error(this, ".process - processing exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        return processable;
    }

    @Override
    public void done()
    {
        this.processable.done(this, data);
        for (Listener<T> listener : listeners)
            listener.done(this, data);
        this.data = null;
        this.processable = null;
        //this.cancel(true);
    }
//  public synchronized void dispose()
//  {
//    this.data = null;
//    this.processable = null;
//  }
}
