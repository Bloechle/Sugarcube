package sugarcube.common.ui.gui.table;

import sugarcube.common.system.reflection.Annot._Bean;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.ui.gui.*;
import sugarcube.common.system.reflection.Bean;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.EventObject;

public class BeanTableCell implements TableCellEditor, TableCellRenderer
{
  protected Set3<Bean.Listener> listeners = new Set3<Bean.Listener>();
  protected EventListenerList listenerList = new EventListenerList();
  transient protected ChangeEvent changeEvent = null;
  protected Bean bean;
  protected Field field;
  protected JComponent component;

  public BeanTableCell(Bean bean, Field field, Bean.Listener... listeners)
  {
    this.bean = bean;
    this.field = field;
    this.listeners.addAll(listeners);
    // Log.debug(this, " field=" + field);

    if (bean == null)
    {
      _Bean fieldAnnot = (_Bean) field.getAnnotation(_Bean.class);
      if (fieldAnnot != null && !fieldAnnot.name().isEmpty())
        this.component = new JLabel(" " + fieldAnnot.name());
      else
        this.component = new JLabel(" " + field.getName());
    } else
    {
      Class type = field.getType();
      if (type.equals(Boolean.class) || type.equals(boolean.class))
      {
        this.component = checkBox(field);
      } else
        this.component = textField(field);
    }

    this.component.setOpaque(true);
  }

  public void addListener(Bean.Listener... listeners)
  {
    this.listeners.addAll(listeners);
  }

  public void notifyListeners(Field field, Object src)
  {
    for (Bean.Listener listener : this.listeners)
      listener.beanFieldModified(bean, field, src);
  }

  public CheckBox3 checkBox(final Field field)
  {
    try
    {
      final CheckBox3 checkBox = new CheckBox3(bean.getBoolean(field));
      checkBox.setRequestFocusEnabled(false);
      checkBox.setOpaque(false);
      checkBox.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
          bean.set(field, checkBox.isSelected());
          notifyListeners(field, checkBox);
        }
      });
      return checkBox;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public TextField3 textField(final Field field)
  {
    try
    {
      String data = bean.string(field);
      final TextField3 textField = new TextField3(data);
      // textField.setRequestFocusEnabled(false);
      textField.addListener(new TextField3.Listener()
      {
        @Override
        public void textFieldEvent(TextField3 textField, Object event)
        {
          bean.set(field, textField.getText());
          notifyListeners(field, textField);
        }
      });

      return textField;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public Spinner3 realSpinner(final Field field)
  {
    try
    {
      final Spinner3 spinner = new Spinner3("", bean.getFloat(field), 0.1);
      spinner.addChangeListener(new ChangeListener()
      {
        @Override
        public void stateChanged(ChangeEvent e)
        {
          bean.set(field, (float) spinner.value());
          notifyListeners(field, spinner);
        }
      });
      return spinner;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public Spinner3 intSpinner(final Field field)
  {
    try
    {
      final Spinner3 spinner = new Spinner3("", bean.getInt(field));
      spinner.addChangeListener(new ChangeListener()
      {
        @Override
        public void stateChanged(ChangeEvent e)
        {
          bean.set(field, spinner.intValue());
          notifyListeners(field, spinner);
        }
      });
      return spinner;
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public Button3 button(final Field field)
  {
    try
    {
      Object o = bean.get(field);
      final Button3 button = new Button3(o == null ? "" : o.toString());
      button.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent e)
        {

          String s = "";
          try
          {
            s = Dialog3.showStringDialog(null, null, "Enter your " + field.getName(), null, bean.get(field).toString());
          } catch (IllegalArgumentException e2)
          { //
            e2.printStackTrace();
          }
          if (s != null && !s.trim().isEmpty())
          {
            button.setText(" " + s + " ");
            bean.set(field, s);
            notifyListeners(field, button);
          }
        }
      });
      return button;
    } catch (Exception e1)
    {
      e1.printStackTrace();
    }
    return null;
  }

  // else if (field.isColor())
  // {
  // final int w = 60;
  // final int h = 15;
  // Label3 label = new Label3(" " + field.name());
  // final Button3 button = new Button3(new ImageIcon3(field.colorValue(),
  // w, h, 5));
  // button.setMargin(new Insets(2, 2, 2, 2));
  //
  // button.addActionListener(new ActionListener()
  // {
  // @Override
  // public void actionPerformed(ActionEvent e)
  // {
  // Color color = JColorChooser.showDialog(BeanWrapperPanel.this,
  // "Choose Color", field.colorValue());
  // if (color != null)
  // {
  // button.setIcon(new ImageIcon3(color, w, h, 5));
  // wrapper.updateProperty(field.name(), new Color3(color));
  // }
  // }
  // });
  // components[i] = Box3.horizontal(button, label);
  // }
  // else if (field.isFile())
  // {
  // final Button3 button = button(field.value());
  // button.addActionListener(
  // new ActionListener()
  // {
  // @Override
  // public void actionPerformed(ActionEvent e)
  // {
  // FileChooser3 chooser = new FileChooser3(BeanWrapperPanel.this,
  // field.fileValue().getPath()).enableDirectorySelectionMode();
  // if (chooser.acceptOpenDialog())
  // {
  // button.setText(" " + chooser.getSelectedFile() + " ");
  // wrapper.updateProperty(field.name(), chooser.getSelectedFile());
  // }
  // }
  // });
  // components[i] = Box3.horizontal(label(field.name()), button);
  // }

  //
  // public BeanCell(final JCheckBox checkBox)
  // {
  // editorComponent = checkBox;
  // delegate = new EditorDelegate()
  // {
  // public void setValue(Object value)
  // {
  // boolean selected = false;
  // if (value instanceof Boolean)
  // {
  // selected = ((Boolean) value).booleanValue();
  // } else if (value instanceof String)
  // {
  // selected = value.equals("true");
  // }
  // checkBox.setSelected(selected);
  // }
  //
  // public Object getCellEditorValue()
  // {
  // return Boolean.valueOf(checkBox.isSelected());
  // }
  // };
  // checkBox.addActionListener(delegate);
  // checkBox.setRequestFocusEnabled(false);
  // }
  //
  // /**
  // * Constructs a <code>DefaultCellEditor</code> object that uses a combo box.
  // *
  // * @param comboBox
  // * a <code>JComboBox</code> object
  // */
  // public BeanCell(final JComboBox comboBox)
  // {
  // editorComponent = comboBox;
  // comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
  // delegate = new EditorDelegate()
  // {
  // public void setValue(Object value)
  // {
  // comboBox.setSelectedItem(value);
  // }
  //
  // public Object getCellEditorValue()
  // {
  // return comboBox.getSelectedItem();
  // }
  //
  // public boolean shouldSelectCell(EventObject anEvent)
  // {
  // if (anEvent instanceof MouseEvent)
  // {
  // MouseEvent e = (MouseEvent) anEvent;
  // return e.getID() != MouseEvent.MOUSE_DRAGGED;
  // }
  // return true;
  // }
  //
  // public boolean stopCellEditing()
  // {
  // if (comboBox.isEditable())
  // {
  // // Commit edited value.
  // comboBox.actionPerformed(new ActionEvent(BeanCell.this, 0, ""));
  // }
  // return super.stopCellEditing();
  // }
  // };
  // comboBox.addActionListener(delegate);
  // }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
  {
    return component;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    return component;
  }

  @Override
  public Object getCellEditorValue()
  {
    return bean.get(field);
  }

  @Override
  public boolean isCellEditable(EventObject e)
  {
    return true;
  }

  @Override
  public boolean shouldSelectCell(EventObject anEvent)
  {
    return false;
  }

  @Override
  public boolean stopCellEditing()
  {
    fireEditingStopped();
    return true;
  }

  @Override
  public void cancelCellEditing()
  {
    fireEditingCanceled();
  }

  @Override
  public void addCellEditorListener(CellEditorListener l)
  {
    listenerList.add(CellEditorListener.class, l);
  }

  @Override
  public void removeCellEditorListener(CellEditorListener l)
  {
    listenerList.remove(CellEditorListener.class, l);
  }

  public CellEditorListener[] getCellEditorListeners()
  {
    return listenerList.getListeners(CellEditorListener.class);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is created lazily.
   * 
   * @see EventListenerList
   */
  protected void fireEditingStopped()
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2)
    {
      if (listeners[i] == CellEditorListener.class)
      {
        // Lazily create the event:
        if (changeEvent == null)
          changeEvent = new ChangeEvent(this);
        ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
      }
    }
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is created lazily.
   * 
   * @see EventListenerList
   */
  protected void fireEditingCanceled()
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2)
    {
      if (listeners[i] == CellEditorListener.class)
      {
        // Lazily create the event:
        if (changeEvent == null)
          changeEvent = new ChangeEvent(this);
        ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
      }
    }
  }
}