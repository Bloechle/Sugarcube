package sugarcube.common.ui.gui.table;

import sugarcube.common.ui.gui.Border3;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class Table3 extends JTable
{
  public Border border = Border3.empty(8, 2);
  public boolean prepare = false;

  public Table3()
  {
    super();
  }

  public Table3(TableModel model)
  {
    super(model);
  }

  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
  {
    JComponent comp = (JComponent) super.prepareRenderer(renderer, row, column);
    comp.setBorder(border);
    if (prepare)
    {
      // ensures a uniform background color (table.setIntercellspacing creates a
      // white border)

      // gives a visual hint about the cell editable state
      comp.setEnabled(isCellEditable(row, column));
      // if (row % 2 == 0)
      // comp.setBackground(Color3.DUST_WHITE);
    }
    return comp;
  }
}
