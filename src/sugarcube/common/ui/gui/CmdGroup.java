package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.Set3;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CmdGroup extends ButtonGroup implements ActionListener
{
  public Set3<ActionListener> listeners = new Set3<ActionListener>();
  public boolean mandatory = false;

  public CmdGroup(AbstractButton... buttons)
  {
    this(false, buttons);
  }

  public CmdGroup(boolean selectionMandatory, AbstractButton... buttons)
  {
    super();
    this.mandatory = selectionMandatory;
    for (AbstractButton button : buttons)
    {
      button.addActionListener(this);
      this.add(button);
    }
  }

  @Override
  public void setSelected(ButtonModel model, boolean selected)
  {
    if (mandatory || selected)
      super.setSelected(model, selected);
    else
      clearSelection();
  }

  public CmdGroup addListener(ActionListener... listeners)
  {
    this.listeners.addAll(listeners);
    return this;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    for (ActionListener listener : listeners)
      listener.actionPerformed(e);
  }

  public void select(String actionCmd)
  {
//    Log.debug(this, ".select - "+actionCmd);
    boolean selected = false;
    for (AbstractButton b : this.buttons)
      if (b.getActionCommand().equals(actionCmd))
        b.setSelected(selected = true);
    if (!selected)
      this.clearSelection();
  }
}
