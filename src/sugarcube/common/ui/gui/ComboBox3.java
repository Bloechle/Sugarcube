package sugarcube.common.ui.gui;

import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.Color3;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Iterator;

public class ComboBox3<T> extends JComboBox<T> implements Iterable<T>
{
  private final Mute mute = new Mute();
  private boolean layingOut = false;
  private int wide = -1;

  public ComboBox3(Iterator<T> it)
  {
    super(new Zen.Generic<T>().toVector(it));
  }

  public ComboBox3(T... objects)
  {
    super(objects);
  }

  public void setWide(int wide)
  {
    this.wide = wide;
  }

  public boolean isMuted()
  {
    return mute.isOn();
  }

  public ComboBox3 size(int w, int h)
  {
    return size(new Dimension(w, h));
  }

  public ComboBox3 size(Dimension dim)
  {
    this.setPreferredSize(dim);
    this.setMinimumSize(dim);
    this.setMaximumSize(dim);
    return this;
  }

  @Override
  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);
    this.mute.set(!enabled);
  }

  @Override
  protected void fireItemStateChanged(ItemEvent ie)
  {
    if (mute.isOff())
      super.fireItemStateChanged(ie);
  }

  public synchronized void setStartsWith(boolean triggerAction, String startsWith)
  {
    int index = -1;
    for (int i = 0; i < this.getItemCount(); i++)
      if (this.getItemAt(i).toString().trim().startsWith(startsWith.trim()))
        index = i;
    if (index >= 0)
      this.setSelectedIndex(triggerAction, index);
  }

  public synchronized void setSelectedIndex(boolean triggerAction, int index)
  {
    if (triggerAction)
      super.setSelectedIndex(index);
    else
    {
      boolean wasMuted = this.mute.isOn();
      this.mute.setOn();
      super.setSelectedIndex(index);
      this.mute.set(wasMuted);
    }
  }

  public synchronized void setSelectedItem(boolean triggerAction, Object item)
  {
    if (triggerAction)
      super.setSelectedItem(item);
    else
    {
      boolean wasMuted = this.mute.isOn();
      this.mute.setOn();
      super.setSelectedItem(item);
      this.mute.set(wasMuted);
    }
  }

  public Str selected3()
  {
    T item = selected();
    return item == null ? null : new Str(item.toString());
  }

  public T selected()
  {
    return (T) this.getSelectedItem();
  }

  public List3<T> items()
  {
    List3<T> items = new List3<>();
    for (int i = 0; i < this.getItemCount(); i++)
      items.add(this.getItemAt(i));
    return items;
  }

  @Override
  public void doLayout()
  {
    try
    {
      layingOut = true;
      super.doLayout();
    } finally
    {
      layingOut = false;
    }
  }

  @Override
  public Dimension getSize()
  {
    Dimension dim = super.getSize();
    if (!layingOut && wide > dim.width)
      dim.width = wide;
    return dim;
  }

  public static class ArrowImageUI extends BasicComboBoxUI implements ActionListener
  {
    public JComboBox combo;
    public JButton button;
    public Dimension size;
    public Icon icon;

    public ArrowImageUI(JComboBox combo, Dimension size, Icon icon)
    {
      this.combo = combo;
      this.size = size;
      this.icon = icon;
      this.combo.addActionListener(this);
      this.combo.setOpaque(false);
    }

    @Override
    protected JButton createArrowButton()
    {
      button = new JButton(icon)
      {
        @Override
        public Dimension getSize()
        {
          return size;
        }

        @Override
        public Dimension size()
        {
          return size;
        }

        @Override
        public int getWidth()
        {
          return size.width;
        }
      };
      button.setMinimumSize(size);
      button.setPreferredSize(size);
      button.setMaximumSize(size);
      button.setMargin(new Insets(0, 0, 0, 0));
      button.setBorder(Border3.empty());
      button.setBorderPainted(false);
      button.setFocusPainted(false);
      button.setContentAreaFilled(false);
      button.addActionListener(this);
      return button;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      // if (button != null)
      // button.setText(combo.getSelectedItem().toString());
    }
  }

  public static class ArrowTextUI extends BasicComboBoxUI implements ActionListener
  {
    public JComboBox combo;
    public JButton button;
    public Dimension size;

    public ArrowTextUI(JComboBox combo, Dimension size)
    {
      this.combo = combo;
      this.size = size;
      this.combo.addActionListener(this);
      this.combo.setOpaque(false);
    }

    @Override
    protected JButton createArrowButton()
    {
      button = new JButton(combo.getSelectedItem().toString())
      {
        @Override
        public Dimension getSize()
        {
          return size;
        }

        @Override
        public Dimension size()
        {
          return size;
        }

        @Override
        public int getWidth()
        {
          return 0;
        }

      };
      button.setMinimumSize(size);
      button.setPreferredSize(size);
      button.setMaximumSize(size);
      button.setMargin(new Insets(0, 0, 0, 0));
      button.setBorder(Border3.line(Color3.BLACK, 0));
      return button;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      if (button != null)
        button.setText(combo.getSelectedItem().toString());
    }
  }

  public static class ArrowLessUI extends BasicComboBoxUI
  {
    public ArrowLessUI()
    {
    }

    @Override
    protected JButton createArrowButton()
    {
      final Dimension zero = new Dimension(0, 0);
      JButton bt = new JButton()
      {
        @Override
        public Dimension getSize()
        {
          return zero;
        }

        @Override
        public Dimension size()
        {
          return zero;
        }

        @Override
        public int getWidth()
        {
          return 0;
        }

      };
      bt.setMinimumSize(zero);
      bt.setPreferredSize(zero);
      bt.setMaximumSize(zero);
      return bt;
    }
  }

  @Override
  public Iterator<T> iterator()
  {
    return new Iterator<T>()
    {
      private int index = 0;

      @Override
      public boolean hasNext()
      {
        return index < ComboBox3.this.getItemCount();
      }

      @Override
      public T next()
      {
        return ComboBox3.this.getItemAt(index++);
      }

      @Override
      public void remove()
      {

      }
    };
  }
}
