package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.Set3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class ToggleGroup implements Iterable<Toggle3>
{
  private CmdActable.Listeners<Toggle3> listeners = new CmdActable.Listeners<Toggle3>();
  private Set3<Toggle3> group = new Set3<Toggle3>();

  public ToggleGroup(Toggle3... buttons)
  {
    this.addButton(buttons);
  }

  public void addButton(Toggle3... buttons)
  {
    for (final Toggle3 button : buttons)
    {
      this.group.add(button);
      button.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {
          boolean isSelected = button.isSelected();
          if (!isSelected)
            button.setSelected(true);
          else
          {
            for (Toggle3 b : group)
              if (b != button)
                if (b.isSelected() == isSelected)
                  b.setSelected(!isSelected);
            listeners.notifyListeners(this, button);
          }
        }
      });
    }
  }

  public ToggleGroup addActionListener(CmdActable<Toggle3>... listeners)
  {
    this.listeners.add(listeners);
    return this;
  }

  @Override
  public Iterator<Toggle3> iterator()
  {
    return group.iterator();
  }
}
