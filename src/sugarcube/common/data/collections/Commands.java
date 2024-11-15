package sugarcube.common.data.collections;

import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.gui.Action3;
import sugarcube.common.ui.gui.CmdGroup;
import sugarcube.common.ui.gui.ComboBox3;
import sugarcube.common.ui.gui.Spinner3;
import sugarcube.common.data.xml.css.CSS;
import sugarcube.common.data.xml.css.CSSBuilder;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Map;

public class Commands extends Map3<String, Cmd>
{
  private Set3<Cmd.Handler> handlers = new Set3<Cmd.Handler>();

  public Commands()
  {
  }

  public Commands(Cmd.Handler... handlers)
  {
    this.handle(handlers);
  }

  public void handle(Cmd.Handler... handlers)
  {
    for (Cmd.Handler handler : handlers)
      this.handlers.add(handler);
  }

  public String[] keys()
  {
    return this.keySet().toArray(new String[0]);
  }

  public synchronized void put(Cmd cmd)
  {
    this.put(cmd.key, cmd);
  }

  // public synchronized void send(Cmd cmd)
  // {
  // this.put(cmd);
  // this.notifyReceivers(cmd);
  // }

  public void sendIf(boolean condition, String key, Object value, boolean forward)
  {
    if (condition)
      send(key, value, forward);
  }

  public synchronized void send(String key, Object value)
  {
    send(key, value, true);
  }

  public synchronized void send(String key, Object value, boolean forward)
  {
//    Log.debug(this, ".send - " + (key.endsWith(":") ? key : key + ":") + value);
    Cmd cmd = new Cmd(key, value, this);
    cmd.forward = forward;
    this.put(key, cmd);
    for (Cmd.Handler handler : handlers)
      handler.command(cmd);
  }

  public synchronized void send(String cssKeyValue, boolean isSelected)
  {
    send(CSSBuilder.parseKey(cssKeyValue), isSelected ? CSSBuilder.parseValue(cssKeyValue) : CSS._normal);
  }

  public synchronized void send(String cssKeyValue)
  {
    send(CSSBuilder.parseKey(cssKeyValue), CSSBuilder.parseValue(cssKeyValue));
  }

  public synchronized void back(String key, Object value)
  {
    send(key, value, false);
  }

  public synchronized void back(String cssKeyValue)
  {
    this.back(CSSBuilder.parseKey(cssKeyValue), CSSBuilder.parseValue(cssKeyValue));
  }

  public synchronized Commands listenTo(Object... buttons)
  {
    for (Object o : buttons)
      if (o instanceof ComboBox3)
        listenTo((ComboBox3) o);
      else if (o instanceof JButton)
        listenTo((JButton) o);
      else if (o instanceof JToggleButton)
        listenTo((JToggleButton) o);
      else if (o instanceof Spinner3)
        listenTo((Spinner3) o);
      else if (o instanceof CmdGroup)
        listenTo((CmdGroup) o);
      else if (o instanceof Action3)
        listenTo((Action3) o);
    return this;
  }

  public synchronized Commands listenTo(final Spinner3 widget)
  {
    widget.addChangeListener(e -> {
      if (!widget.isMuted())
        send(widget.getActionCommand(), widget.value());
    });
    return this;
  }

  public synchronized Commands listenTo(final ComboBox3 widget)
  {
    widget.addItemListener(e -> {
      if (!widget.isMuted() && e.getStateChange() == ItemEvent.SELECTED)
        send(widget.getActionCommand(), widget.getSelectedItem());

    });
    return this;
  }

  public synchronized Commands listenTo(final JButton widget)
  {
    widget.addActionListener(e -> send(e.getActionCommand(), e.getActionCommand()));
    return this;
  }

  public synchronized Commands listenTo(final JToggleButton widget)
  {
    widget.addActionListener(e -> send(e.getActionCommand(), widget.isSelected()));
    return this;
  }

  public synchronized Commands listenTo(final CmdGroup widget)
  {
    widget.addListener(e -> send(e.getActionCommand()));
    return this;
  }

  public synchronized Cmd cmd(String key)
  {
    return new Cmd(key, this);
  }

  public synchronized Object value(String key, Object def)
  {
    Object o = this.get(key);
    if (o == null)
      return def;
    else
      return o;
  }

  public synchronized int integer(String key, int def)
  {
    return new Cmd(key, null, this).integer(def);
  }

  public synchronized float real(String key, float def)
  {
    return new Cmd(key, null, this).real(def);
  }

  public synchronized boolean bool(String key, boolean def)
  {
    return new Cmd(key, null, this).bool(def);
  }

  public synchronized String string(String key, String def)
  {
    return new Cmd(key, null, this).string(def);
  }

  public synchronized Color3 color(String key, Color3 def)
  {
    return new Cmd(key, null, this).color(def);
  }

  @Override
  public synchronized String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CmdMap[").append(this.size()).append("]");
    for (Map.Entry<String, Cmd> entry : this.entrySet())
      sb.append("\n" + entry.getValue());
    return sb.toString();
  }
}
