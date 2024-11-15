package sugarcube.common.ui.gui.table;

import sugarcube.common.system.log.Log;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;

public class BeanTableModel extends AbstractTableModel implements TableModelListener
{
  private static String[] NAMES =
  { "Name", "Value" };

  protected BeanTable table;

  public BeanTableModel(BeanTable table)
  {
    super();
    this.table = table;
    this.addTableModelListener(this);
  }

  @Override
  public String getColumnName(int col)
  {
    return NAMES[col];
  }

  @Override
  public int getRowCount()
  {
    return table.fields.length;
  }

  @Override
  public int getColumnCount()
  {
    return NAMES.length;
  }

  @Override
  public Class getColumnClass(int c)
  {
    return String.class;
  }

  @Override
  public boolean isCellEditable(int row, int col)
  {
    return col != 0;
  }

  public String getStringAt(int row, int col)
  {
    Object o = getValueAt(row, col);
    return o == null ? null : o.toString();
  }

  @Override
  public Object getValueAt(int row, int col)
  {
    if (row > -1 && col > -1 && row < table.fields.length && col < 2)
    {
      Field field = table.fields[row];
      if (col == 0)
        return field.getName();
      else if (col == 1)
        return table.bean.get(field);
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int row, int col)
  {
    if (value == null)
      return;
    Log.debug(this, ".setValueAt - " + row + ", " + col + ": " + value.getClass().getSimpleName() + " " + value);
    try
    {
      if (row > -1 && col > -1 && row < table.fields.length && col < 2)
      {
        Field field = table.fields[row];
        table.bean.set(field, value);
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    fireTableCellUpdated(row, col);
  }

  @Override
  public void tableChanged(TableModelEvent tme)
  {
  }
}
