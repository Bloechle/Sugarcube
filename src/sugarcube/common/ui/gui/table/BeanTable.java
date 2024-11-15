package sugarcube.common.ui.gui.table;

import sugarcube.common.graphics.Color3;
import sugarcube.common.ui.gui.Border3;
import sugarcube.common.system.reflection.Bean;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.lang.reflect.Field;

public class BeanTable extends Table3
{
  protected static final Color ODD_ROW_COLOR = Color3.DUST_WHITE;
  protected static final Color EVEN_ROW_COLOR = new Color(230, 230, 240);
  protected Bean bean;
  protected Field[] fields;
  protected BeanTableModel model;
  protected BeanTableCell[][] cells;

  public BeanTable(Object object, Bean.Listener... listeners)
  {
    this.bean = object instanceof Bean ? (Bean) object : new Bean(object);
    this.fields = bean.fields();

    this.model = new BeanTableModel(this);
    this.setModel(model);
    this.cells = new BeanTableCell[fields.length][2];
    for (int i = 0; i < cells.length; i++)
    {
      // name cell => bean must be null
      this.cells[i][0] = new BeanTableCell(null, fields[i]);
      // value cell
      this.cells[i][1] = new BeanTableCell(bean, fields[i], listeners);
    }
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setOpaque(false);
    setIntercellSpacing(new Dimension(0, 0));
    setShowGrid(true);
  }

  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int col)
  {
    Component component = super.prepareRenderer(renderer, row, col);
    boolean selected = getSelectionModel().isSelectedIndex(row);
    if (component instanceof JComponent)
    {
      JComponent c = (JComponent) component;
      c.setOpaque(true);
      c.setBackground(row % 2 == 0 ? EVEN_ROW_COLOR : ODD_ROW_COLOR);
      c.setBorder(Border3.empty(1));
    }

    return component;
  }

  @Override
  public Component prepareEditor(TableCellEditor editor, int row, int col)
  {
    Component component = super.prepareEditor(editor, row, col);
    boolean selected = getSelectionModel().isSelectedIndex(row);
    if (component instanceof JComponent)
    {
      JComponent c = (JComponent) component;
      c.setOpaque(true);
      c.setBackground(row % 2 == 0 ? EVEN_ROW_COLOR : ODD_ROW_COLOR);
      c.setBorder(col == 1 && component instanceof JTextField ? Border3.line(Color3.lightGray, 1) : Border3.empty(1));
    }
    return component;
  }

  public void addListener(Bean.Listener... listeners)
  {
    for (int i = 0; i < cells.length; i++)
    {
      this.cells[i][1].addListener(listeners);
    }
  }

  @Override
  public TableCellRenderer getCellRenderer(int row, int col)
  {
    return cells[row][col];
  }

  @Override
  public TableCellEditor getCellEditor(int row, int col)
  {
    return cells[row][col];
  }

  public static void main(String[] args)
  {
    // Specify the name of the class as a command-line argument
    Class beanClass = null;
    try
    {
      // Use reflection to get the Class from the classname
      beanClass = Class.forName("javax.swing.JLabel");
    } catch (Exception e)
    { // Report errors
      System.out.println("Can't find specified class: " + e.getMessage());
      System.out.println("Usage: java TableDemo <JavaBean class name>");
      System.exit(0);
    }

    JTable table = new BeanTable(beanClass);
    JScrollPane scrollpane = new JScrollPane(table);
    JFrame frame = new JFrame("Properties of JavaBean: ");
    frame.getContentPane().add(scrollpane);
    frame.setSize(500, 400);
    frame.setVisible(true);
  }
}
