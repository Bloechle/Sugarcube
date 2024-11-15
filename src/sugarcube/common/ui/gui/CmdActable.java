package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.Set3;

import java.util.Iterator;

public interface CmdActable<T>
{
  public static final String REFRESH = "Refresh";
  public static final String CLOSE = "Close";
  public static final String ACTION = "Action";
  
  public void act(String action, T data);
  
  public class Listeners<T> implements Iterable<CmdActable<T>>
  {
    protected Set3<CmdActable<T>> listeners = new Set3<>();
    
    public Listeners(CmdActable<T>... listeners)
    {      
      this.listeners.addAll(listeners);
    }
    
    public void notifyListeners(Object classNameAction, T data)
    {
      for (CmdActable<T> listener : listeners)
        listener.act(classNameAction.getClass().getName(), data);      
    }    
    
    public void notifyListeners(String action, T data)
    {
      for (CmdActable<T> listener : listeners)
        listener.act(action, data);      
    }
    
    public void add(CmdActable<T>... listeners)
    {
      this.listeners.addAll(listeners);
    }    
    
    public void remove(CmdActable<T>... listeners)
    {
      this.listeners.removeAll(listeners);
    }    
    
    public void clear()
    {
      this.listeners.clear();
    }
    
    @Override
    public Iterator<CmdActable<T>> iterator()
    {
      return listeners.iterator();
    }
  }
}
