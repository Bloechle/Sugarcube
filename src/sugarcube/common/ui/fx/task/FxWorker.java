package sugarcube.common.ui.fx.task;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import sugarcube.common.interfaces.DoneListener;
import sugarcube.common.ui.fx.base.Fx;

public class FxWorker<I, O> extends Service<O> implements Taskable<I, O>, DoneListener
{
  public static final String UNDEF = "void";
  public static final String SUCCEEDED = "succeeded";
  public static final String CANCELLED = "cancelled";
  public static final String FAILED = "failed";

  private DoneListener listener = null;
  private String state = UNDEF;
  private Task<O> task;
  private Taskable<I, O> taskable;
  private I input;
  private O output;

  public FxWorker(I input)
  {
    this(false, null, input);
  }

  public FxWorker(boolean start, final Taskable<I, O> taskable, I input)
  {
    this.taskable = taskable == null ? this : taskable;
    this.input = input;
    this.setOnCancelled(eventHandler(CANCELLED));
    this.setOnSucceeded(eventHandler(SUCCEEDED));
    this.setOnFailed(eventHandler(FAILED));
    if (start)
      this.start();
  }

  public FxWorker<I, O> doneListener(DoneListener listener)
  {
    this.listener = listener;
    return this;
  }

  public FxWorker<I, O> go()
  {
    this.start();
    return this;
  }

  private EventHandler<WorkerStateEvent> eventHandler(final String state)
  {
    return e -> {
      Fx.Run(() -> {
        FxWorker.this.state = state;
        taskable.taskDone(output);
        if (listener != null)
          listener.done();
      });
    };
  }

  public boolean isSuccess()
  {
    return SUCCEEDED.equals(state);
  }

  public boolean isCancelled()
  {
    return CANCELLED.equals(state);
  }

  public boolean hasFailed()
  {
    return FAILED.equals(state);
  }

  public Task<O> task()
  {
    return task;
  }

  @Override
  protected Task<O> createTask()
  {
    this.task = new Task<O>()
    {
      @Override
      protected O call()
      {
        try
        {
          return output = taskable.taskWork(input);
        } catch (Exception e)
        {
          e.printStackTrace();
          return null;
        }
      }
    };
    return task;
  }

  @Override
  public void done()
  {

  }

  @Override
  public O taskWork(I input)
  {

    return null;
  }

  @Override
  public void taskDone(O ouput)
  {

  }

}
